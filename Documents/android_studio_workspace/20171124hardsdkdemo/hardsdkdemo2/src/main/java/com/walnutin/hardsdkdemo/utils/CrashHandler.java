package com.walnutin.hardsdkdemo.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;

//import com.javen.bs.pushsdk.service.PushService;

/**
 * 
 * @particulars:UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.只是处理主线程的异常，要处理其他的异常的话， 
 *                                                                                                自己在起异常的时候调用
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    // 程序的Context对象
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    private static String LOGFILEName = "error_Log.txt";// 本类输出的日志文件名称
    private static String LOG_PATH_SDCARD_DIR = "/error_log/";// 日志文件在sdcard中的路径

    private boolean isSendStatus = false;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     * 
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // 退出程序
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
            
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * 
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
//                Toast.makeText(mContext," I\'m sorry, abnormal program. ", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存错误报告文件
        saveCrashInfo2File(ex);

        // 发送错误报告到服务器
        sendCrashReportsToServer(mContext);

        return true;
    }

    /**
     * 收集设备参数信息
     * 
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
        //    infos.put("phoneModel", PhoneHelper.getModel());
         //   infos.put("SDKVersion", PhoneHelper.getSdkApi());
       //     infos.put("getAppInfo", PhoneHelper.getAppInfo(ctx));

        } catch (Exception e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
    }

    /**
     * 保存错误信息到文件中
     * 
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            String needWriteFiel = formatter.format(new Date());
            File dir = new File(LOG_PATH_SDCARD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = needWriteFiel + LOGFILEName;
            File file = new File(LOG_PATH_SDCARD_DIR, fileName);
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(sb.toString());
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    // /////////////////////////////////////////////////////////////////////
    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     * 
     * @param ctx
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));

            for (String fileName : sortedFiles) {
                // File cr = new File(ctx.getFilesDir(), fileName);
                final File cr = new File(LOG_PATH_SDCARD_DIR, fileName);
                //boolean sendStaus = postReport(cr); // 发送错误报告到服务器
                String text =ReadTxtFile(cr.getAbsolutePath());
//                xxxx.sendLog(mContext, Parameter.URL_SEND_LOG, text, new PushService.pushListener() {
//
//                    @Override
//                    public void sendStatus(boolean isSuccess) {
//                        if (isSuccess) {
//                            cr.delete();// 删除已发送的报告
//                            // cr.renameTo(new File(fileName));
//                        }
//                    }
//                });
//
                
                
            }
        }
    }

    /**
     * 获取错误报告文件名
     * 
     * @param ctx
     * @return
     */
    private String[] getCrashReportFiles(Context ctx) {
        // File filesDir = ctx.getFilesDir();
        File filesDir = new File(LOG_PATH_SDCARD_DIR);
        // 实现FilenameFilter接口的类实例可用于过滤器文件名
        FilenameFilter filter = new FilenameFilter() {
            // accept(File dir, String name)
            // 测试指定文件是否应该包含在某一文件列表中。
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        };
        // list(FilenameFilter filter)
        // 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中满足指定过滤器的文件和目录
        return filesDir.list(filter);
    }

    // TODO 使用HTTP Post 发送错误报告到服务器
    private boolean postReport(File file) {

        //String text =ReadTxtFile(file.getAbsolutePath());
        
        return isSendStatus;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer(mContext);
    }
    
    
    /**
     * 读取文本文件中的内容
     * @param strFilePath
     * @return
     */
    public static String ReadTxtFile(String strFilePath){
        String path = strFilePath;
        String content = ""; //文件内容字符串
            //打开文件
            File file = new File(path);
            //如果path是传递过来的参数，可以做一个非目录的判断
            if (file.isDirectory()){
                Log.d("TestFile", "The File doesn't not exist.");
            }
            else{
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null){
                        InputStreamReader inputreader = new InputStreamReader(instream);
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line;
                        //分行读取
                        while (( line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }                
                        instream.close();
                    }
                }
                catch (java.io.FileNotFoundException e){
                    Log.d("TestFile", "The File doesn't not exist.");
                } 
                catch (IOException e){
                     Log.d("TestFile", e.getMessage());
                }
            }
            return content;
    }

}


package com.walnutin.hardsdkdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 其他工具
 */
public class OtherUtil {


    /**
     * 通过email获得可做数据库名称的字段
     *
     * @param email
     * @return
     */
    public static String getDBNameByEmail(String email) {
        String timeStr = email.replace("@", "_");
        timeStr = timeStr.replace(".", "_");
        return timeStr;
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     */
    public static String encodeGB(String string) {
        // 转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "utf8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
        return split[0];
    }

    /**
     * 判断是否有该组件Component
     * 当Android系统调用Intent时，如果没有找到Intent匹配的Activity组件（Component）
     *
     * @param context
     * @param intent
     * @return
     */
//    public static boolean isIntentAvailable(Context context, Intent intent) {
//        final PackageManager packageManager = context.getPackageManager();
//        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
//        return list.size() > 0;
//    }

    /**
     * get current date string
     *
     * @return
     */
    public static String getTodayDateString() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd",
                Locale.getDefault());
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1;
    }

    /**
     * 时间转成日期
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
         //   XLog.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}

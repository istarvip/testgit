package com.walnutin.service;

import java.util.List;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.walnutin.activity.MapActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MonitorService extends Service {

    public static boolean isCheck = false;

    public static boolean isRunning = false;

    private static final String SERVICE_NAME = "com.baidu.trace.LBSTraceService";

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (isCheck) {
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("thread sleep failed");
                    }

                    if (!isServiceWork(getApplicationContext(), SERVICE_NAME)) {
                        System.out.println("轨迹服务已停止，重启轨迹服务");
                        if (null != MapActivity.client && null != MapActivity.trace) {
                            MapActivity.client.startTrace(MapActivity.trace);
                        } else {
                        	MapActivity.client = null;
                        	MapActivity.client = new LBSTraceClient(getApplicationContext());
                        	MapActivity.entityName = MapActivity.getImei(getApplicationContext());
                        	MapActivity.trace = new Trace(getApplicationContext(), MapActivity.serviceId,
                        			MapActivity.entityName);
                        	MapActivity.client.startTrace(MapActivity.trace);
                        }

                    } else {
                        System.out.println("轨迹服务正在运行");
                    }

                }
            }

        }.start();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 判断某个服务是否正在运行的方法
     * 
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：com.baidu.trace.LBSTraceService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> myList = myAM.getRunningServices(80);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            System.out.println("serviceName : " + mName);
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}

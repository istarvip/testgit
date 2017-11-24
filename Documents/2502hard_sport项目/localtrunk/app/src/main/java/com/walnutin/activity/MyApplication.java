package com.walnutin.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.service.DeviceLinkService;
import com.walnutin.service.NLService;
import com.walnutin.service.StepService;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Administrator on 2016/5/12.
 */
public class MyApplication extends Application {

    private static Context context = null;
    public static String account = "visitor";
    public static String token = null;

    public static MyApplication _instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        _instance = this;
        context = this;
        MobclickAgent.openActivityDurationTrack(false);   //禁止默认的页面统计方式，这样将不会再自动统计Activity。
        MobclickAgent.enableEncrypt(true);//6.0.0版本及以后  加密上传
        ShareSDK.initSDK(this);
        //initImageLoader(this);
        //    LeakCanary.install(this);

    //    startNoticeService();
    }

//    public static void initImageLoader(Context context) {
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .threadPriority(Thread.NORM_PRIORITY - 3)
//                .denyCacheImageMultipleSizesInMemory()
//                .discCacheFileNameGenerator(new Md5FileNameGenerator())
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                //	.writeDebugLogs() // Remove for release app
//                .build();
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config);
//    }

    public static Context getContext() {
        return context;
    }

    public static MyApplication instance() {
        return _instance;
    }

    Intent intent;
    public static boolean isDevConnected = false;

    public static boolean isDevConnected() {
        return isDevConnected;
    }

    public void startDeviceService() {
        if (!isDevConnected()) {
            intent = new Intent(this, DeviceLinkService.class);
            startService(intent);
        }
    }

    public void startStepService() {
        Intent stepServiceIntent = new Intent(this, StepService.class);
        startService(stepServiceIntent);
    }

    public void startNoticeService() {
        Intent stepServiceIntent = new Intent(getApplicationContext(), NLService.class);
        startService(stepServiceIntent);
    }


    public void stopDeviceService() {
        if (isDevConnected) {
            stopService(intent);
            isDevConnected = false;
        }
    }


}

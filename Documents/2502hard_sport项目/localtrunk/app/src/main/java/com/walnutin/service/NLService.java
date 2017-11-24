package com.walnutin.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.walnutin.manager.NoticeInfoManager;
import com.walnutin.activity.MyApplication;
import com.walnutin.entity.NoticeDevice;
import com.walnutin.util.Config;

import java.util.List;

/**
 * 作者：MrJiang on 2016/8/15 11:23
 */
@SuppressLint("NewApi")
public class NLService extends NotificationListenerService {

    NoticeInfoManager noticeInfoManager;

    @Override
    public void onCreate() {
        super.onCreate();
        noticeInfoManager = NoticeInfoManager.getInstance(getApplicationContext());
        noticeInfoManager.getLocalNoticeInfo();
        System.out.println("NLService start");
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        System.out.println("NLService sbn: " + sbn.getPackageName());

        if (MyApplication.isDevConnected == false) {
            return;
        }
        String packageName = sbn.getPackageName();
//        if (packageName.equals("com.android.incallui")) {
//            if (noticeInfoManager.isEnablePhone()) {
//                sendBroadMsg("phone");
//
//            }
//        } else if (packageName.equals("com.android.mms") || packageName.equals("com.android.contacts")) {
//
//            if (noticeInfoManager.isEnableMsg()) {
//                sendBroadMsg("msg");
//
//            }
//        } else
        if (packageName.equals("com.tencent.mm")) {
            if (noticeInfoManager.isEnableWeChat()) {
                sendBroadMsg("WeChat");

            }
        } else if (packageName.equals("com.tencent.mobileqq")) {
            if (noticeInfoManager.isEnableQQ()) {
                sendBroadMsg("qq");
                //    SmsMessage smsManager;smsManager.get
            }
        } else {
            List<NoticeDevice> noticeDeviceList = noticeInfoManager.getSoftRemindList();
            for (NoticeDevice device : noticeDeviceList) {
                if (device.packageInfo.equals(packageName)) {
                    sendBroadMsg("msg");
                }
            }
        }


    }

    public void sendBroadMsg(String key) {
        Intent intent = new Intent(Config.NOTICE_ACTION);
        intent.putExtra("type", key);
        sendBroadcast(intent);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

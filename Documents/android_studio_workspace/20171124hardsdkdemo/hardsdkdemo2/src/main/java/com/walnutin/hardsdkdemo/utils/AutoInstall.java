package com.walnutin.hardsdkdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

//自动安装
public class AutoInstall {

    private static Context mContext;
    private static String mUrl;

    public static void setmUrl(String v) {
        AutoInstall.mUrl = v;
    }

    public static void install(Context v) {
        mContext = v;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(mUrl)), "application/vnd.android.package-archive");
        //	String localeString = "application/vnd.android.package-archive";
        mContext.startActivity(intent);
    }

}

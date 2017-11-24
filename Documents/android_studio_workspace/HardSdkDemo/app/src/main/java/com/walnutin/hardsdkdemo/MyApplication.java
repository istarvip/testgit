package com.walnutin.hardsdkdemo;

import android.app.Application;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by chenliu on 2017/4/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        HardSdk.getInstance().init(this);
    }
}

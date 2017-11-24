package com.walnutin.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.walnutin.Jinterface.NetStateChangeListener;

import java.util.ArrayList;

public class NetStateReceiver extends BroadcastReceiver {

    // 网络变化事件监听
    public static ArrayList<NetStateChangeListener> netList = new ArrayList<NetStateChangeListener>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if(netList.size() > 0) {
                for (int i = 0; i < netList.size(); i++) {
                    netList.get(i).onNetStateChange();
                }
            }
            return;
        }
    }
}
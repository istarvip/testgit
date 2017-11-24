package com.walnutin.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walnutin.Jinterface.NetStateChangeListener;
import com.walnutin.http.NetStateReceiver;

public class BaseFragment extends Fragment implements NetStateChangeListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NetStateReceiver.netList.contains(this)) {
            NetStateReceiver.netList.add(this);  //将当前Activity加入到netList中
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!NetStateReceiver.netList.contains(this)) {
            NetStateReceiver.netList.remove(this);  //将当前Activity加入到netList中
        }
    }

    @Override
    public void onNetStateChange() {
        if (isNetworkAvailable(getActivity())) {
            noticeNet(true);
            //    Toast.makeText(this, "网络可用", Toast.LENGTH_SHORT).show();
        } else {
            //     Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
            noticeNet(false);
        }
    }

    public  void noticeNet(boolean netstate){

    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
}
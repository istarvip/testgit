package com.walnutin.Model;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.walnutin.entity.ServerUser;
import com.walnutin.entity.UserBean;

import java.util.Observable;

/**
 * 作者：MrJiang on 2016/7/15 16:47
 */
public class StoreImpl implements StoreIntf {
    ServerUser serverUser;
    String base64Token;
    Context mContext;

    public StoreImpl(Context context){
        mContext = context;
    }

    @Override
    public ServerUser getServerInfo() {
        return serverUser;
    }

    @Override
    public void setServerInfo(ServerUser serverInfo) {
        serverUser = serverInfo;
    }

    @Override
    public String getbase64TokeInfo() {
        return base64Token;
    }

    @Override
    public void setbase64TokenByObjectWithBackUrl(Object serverUsers, String backUrl) {
        serverUser = (ServerUser) serverUsers;
        ((ServerUser) serverUsers).setBack_url(backUrl);
        Gson gson = new Gson();
        String gsonStr = gson.toJson(serverUser);
        base64Token = Base64.encodeToString(gsonStr.getBytes(), Base64.DEFAULT);
    }


    @Override
    public void setBase64Token() {
        Gson gson = new Gson();
        String gsonStr = gson.toJson(serverUser);
        base64Token = Base64.encodeToString(gsonStr.getBytes(), Base64.DEFAULT);
    }
}

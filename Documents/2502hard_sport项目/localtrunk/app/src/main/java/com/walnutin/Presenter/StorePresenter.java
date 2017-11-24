package com.walnutin.Presenter;

import android.content.Context;

import com.walnutin.Model.StoreImpl;
import com.walnutin.Model.StoreIntf;
import com.walnutin.entity.ServerUser;

/**
 * 作者：MrJiang on 2016/7/15 16:42
 */
public class StorePresenter {

    private StoreIntf storeIntf;

    public StorePresenter(Context context) {
        storeIntf = new StoreImpl(context);

    }

    public  ServerUser getServerInfo() {
        return storeIntf.getServerInfo();
    }

    public  void setServerInfo(ServerUser serverInfo) {
        storeIntf.setServerInfo(serverInfo);
    }

    public String getbase64TokeInfo() {
        return storeIntf.getbase64TokeInfo();
    }

    public void setbase64TokenByObject(Object serverUser,String backUrl) {
        storeIntf.setbase64TokenByObjectWithBackUrl(serverUser,backUrl);
    }

   public void setBase64Token() {
        storeIntf.setBase64Token();
    }


}

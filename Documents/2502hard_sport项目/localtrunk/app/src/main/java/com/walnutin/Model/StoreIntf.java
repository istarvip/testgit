package com.walnutin.Model;

import com.walnutin.entity.ServerUser;

/**
 * 作者：MrJiang on 2016/7/15 16:42
 */
public interface StoreIntf {
    ServerUser getServerInfo();

    void setServerInfo(ServerUser serverInfo);

    String getbase64TokeInfo();

    void setbase64TokenByObjectWithBackUrl(Object serverUser,String backUrl);

    void setBase64Token();
}

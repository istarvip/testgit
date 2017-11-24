package com.walnutin.Model;

import com.walnutin.entity.UserBean;

import cn.sharesdk.framework.Platform;

/**
 * 作者：MrJiang on 2016/7/13 16:49
 */
public interface LoginIntf {
    void Login(String account, String pwd);

    void ThirdLogin(Platform plat,int type);

    void setToken(String token);

    void setOpenId(String openId);

    void saveInfo(UserBean userBean);
    void setPlatType(int type);

    String getLocalAccount();

    String getLocalPwd();

    String getLocalOpenId();

    int getLocalType();

}

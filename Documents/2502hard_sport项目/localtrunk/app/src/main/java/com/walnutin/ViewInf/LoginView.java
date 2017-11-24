package com.walnutin.ViewInf;

import com.walnutin.entity.UserBean;

import cn.sharesdk.framework.Platform;

/**
 * 作者：MrJiang on 2016/7/13 16:49
 */
public interface LoginView {
    int getLoginPlatType();
    String getOpenId();
    String getToken();
    String getAccount();
    String getPassWord();
    String getThirdInfo();
}

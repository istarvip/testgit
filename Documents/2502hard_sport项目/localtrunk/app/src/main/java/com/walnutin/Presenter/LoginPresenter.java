package com.walnutin.Presenter;

import android.util.Log;

import com.walnutin.entity.UserBean;
import com.walnutin.Model.LoginImpl;
import com.walnutin.Model.LoginIntf;
import com.walnutin.ViewInf.LoginView;

import cn.sharesdk.framework.Platform;

/**
 * 作者：MrJiang on 2016/7/13 18:00
 */
public class LoginPresenter {
    LoginIntf loginIntf;
    LoginView loginView;

    public LoginPresenter(LoginView loginV) {
        loginIntf = new LoginImpl();
        loginView = loginV;
    }

    public String getAccount() {
        return loginView.getAccount();
    }

    public String getPassWord() {
        return loginView.getPassWord();
    }

    public void Login(String account, String pwd) {
        loginIntf.Login(account, pwd);
    }

    public void thirdLogin(Platform plat,int type) {
        loginIntf.ThirdLogin(plat,type);
    }

    public String getThirdInfo() {
        return loginView.getThirdInfo();
    }

    public void saveLoginInfo(UserBean userBean) {
        loginIntf.saveInfo(userBean);
    }

    public void setToken() {

        Log.w("presenter  test token", "onLoginResult: "+loginView.getToken());

        Log.w("presenter 测试登", "onLoginResult: this:"+this+" token:"+loginView.getToken());
        loginIntf.setToken(loginView.getToken());
    }

    public void setOpenId() {
        loginIntf.setOpenId(loginView.getOpenId());
    }

    public  String getLocalAccount() {
        return loginIntf.getLocalAccount();
    }

   public String getLocalPwd() {
        return loginIntf.getLocalPwd();
    }

    public  String getLocalOpenId() {
        return loginIntf.getLocalOpenId();
    }

    public  int getLocalType() {
        return loginIntf.getLocalType();
    }

    public void setLoginType(){
        loginIntf.setPlatType(loginView.getLoginPlatType());
    }
}

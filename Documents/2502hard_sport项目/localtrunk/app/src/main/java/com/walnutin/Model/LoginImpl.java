package com.walnutin.Model;

import com.google.gson.Gson;
import com.walnutin.activity.MyApplication;
import com.walnutin.entity.UserBean;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.MD5Util;
import com.walnutin.util.MySharedPf;

import cn.sharesdk.framework.Platform;

/**
 * 作者：MrJiang on 2016/7/13 16:49
 */
public class LoginImpl implements LoginIntf {
    MySharedPf preferenceSettings;
    String openId;
    String token;
    int type;

    public LoginImpl() {
        preferenceSettings = MySharedPf.getInstance(MyApplication.getContext());
    }

    @Override
    public void Login(String account, String pwd) {
        if (pwd != null && pwd.length() <= 25) {
            pwd = MD5Util.MD5(MD5Util.MD5(pwd));
        }
        HttpImpl.getInstance().userLogin(account, pwd);
    }

    @Override
    public void ThirdLogin(Platform plat, int type) {
        //  HttpImpl.getInstance().userThirdLogin(thirdJson);
        openId = plat.getDb().getUserId();
        if (openId== null ){
            return;
        }
        String userIcon = plat.getDb().getUserIcon();
        String userGender ="";
        if(plat.getDb().getUserGender()!=null) {
             userGender = plat.getDb().getUserGender().equals("m") ? "男" : "女";
        }
        String userName = plat.getDb().getUserName();
        UserBean userBean = new UserBean();
        userBean.setToken(plat.getDb().getToken());
        userBean.setOpenid(openId);
        userBean.setHeadimage(userIcon);
        userBean.setSex(userGender);
        userBean.setNickname(userName);
        userBean.setType(type);
        Gson gson = new Gson();
        HttpImpl.getInstance().userThirdLogin(gson.toJson(userBean));
    }

    @Override
    public void setToken(String tokens) {
        token = tokens;
    }

    @Override
    public void setOpenId(String openIds) {
        openId = openIds;
    }

    @Override
    public void saveInfo(UserBean user) {
        preferenceSettings.setString("account", user.getAccount());
        preferenceSettings.setString("sex", user.getSex());
        preferenceSettings.setString("applytime", user.getApplytime());
        preferenceSettings.setString("birth", user.getBirth());
        preferenceSettings.setString("headimage", user.getHeadimage());
        preferenceSettings.setString("height", user.getHeight());
        preferenceSettings.setString("mobile", user.getMobile());
        preferenceSettings.setString("nickname", user.getNickname());
        preferenceSettings.setString("property", user.getProperty());
        preferenceSettings.setString("weight", user.getWeight());
        preferenceSettings.setToken(token);
        preferenceSettings.setInt("type", -1);
        preferenceSettings.setOpenID(null);
        if (type != -1) {
            preferenceSettings.setInt("type", type);
            preferenceSettings.setOpenID(openId);
        }
    }

    @Override
    public void setPlatType(int t) {
        this.type = t;
    }

    @Override
    public String getLocalAccount() {
        return preferenceSettings.getString("account");
    }

    @Override
    public String getLocalPwd() {
        return preferenceSettings.getString("password");
    }

    @Override
    public String getLocalOpenId() {
        return preferenceSettings.getOpenId();
    }

    @Override
    public int getLocalType() {
        return preferenceSettings.getInt("type");
    }
}

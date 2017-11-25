package com.walnutin.hardsdkdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**

 */
public class MySharedPf {
    private String sp_name = "userinfo";
    private SharedPreferences sharedPreferences;
    private static MySharedPf instance;

    public static MySharedPf getInstance(Context context) {

        if (instance == null) {
            instance = new MySharedPf(context);
        }
        return instance;
    }

    private MySharedPf(Context context) {
        sharedPreferences = context.getSharedPreferences(sp_name,
                Context.MODE_PRIVATE);
    }

    public void setUid(String uId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uId", uId);
        editor.commit();
    }

    public String getUid() {
        return sharedPreferences.getString("uId", null);
    }

    public void setSessionID(String sessionID) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sessionids", sessionID);
        editor.commit();
    }

    public void setOpenID(String openID) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("openid", openID);
        editor.commit();
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.commit();

    }

    public String getSessionID() {
        return sharedPreferences.getString("sessionids", "0");
    }

    public String getOpenId() {
        return sharedPreferences.getString("openid", null);
    }

    public String getToken() {
        return sharedPreferences.getString("token", "0");
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public String getString(String key, String defalut) {
        return sharedPreferences.getString(key, defalut);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void deleteCache(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();

    }

}

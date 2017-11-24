package com.walnutin.hardsdkdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;

/**

 */
public class MySharedPf {
    private String sp_name = "userinfo";
    private SharedPreferences sharedPreferences;
    private static MySharedPf instance;
    private SharedPreferences.Editor mEditor;

    public static MySharedPf getInstance(Context context) {
        if (instance == null) {
            instance = new MySharedPf(context);
        }
        return instance;
    }

    private MySharedPf(Context context) {
        sharedPreferences = context.getSharedPreferences(sp_name,
                Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    public void setUid(String uId) {
        mEditor.putString("uId", uId);
        mEditor.commit();
    }

    public String getUid() {
        return sharedPreferences.getString("uId", null);
    }

    public void setSessionID(String sessionID) {
        mEditor.putString("sessionids", sessionID);
        mEditor.commit();
    }

    public void setOpenID(String openID) {

        
        mEditor.putString("openid", openID);
        mEditor.commit();
    }

    public void setToken(String token) {
        mEditor.putString("token", token);
        mEditor.commit();

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
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }

    public void setInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public void deleteCache(){
        mEditor.clear().commit();
    }

    public String getZikuType() {
        return sharedPreferences.getString("ziku", "00");
    }


    public void setZikuType(String type) {
        mEditor.putString("ziku", type);
        mEditor.commit();
    }


    public void setIsSupportBlood(boolean setIsSupportBlood) {
        mEditor.putBoolean("setIsSupportBlood", setIsSupportBlood);
        mEditor.commit();
    }

    public void setIsSupportGPS(boolean setIsSupportBlood) {
        mEditor.putBoolean("setIsSupportGPS", setIsSupportBlood);
        mEditor.commit();
    }


    public void setBraceletType(int type) {
        mEditor.putInt("braceletType", type);
        mEditor.commit();
    }

    public void setBraceletVersion(int version) {
        mEditor.putInt("braceletVersion", version);
        mEditor.commit();
    }

    public int getStepGoal() {
        return sharedPreferences.getInt("target", 7000);
    }


    public int getClimbGoal() {
        return sharedPreferences.getInt("target_climb", 6000);
    }


    public void setSwimGoal(Integer integer) {
        mEditor.putInt("target_swim", integer);
        mEditor.commit();
    }

    public int getSwimGoal() {
        return sharedPreferences.getInt("target_swim", 5);
    }


    public void setWalkGoal(Integer integer) {
        mEditor.putInt("target_walk", integer);
        mEditor.commit();
    }

    public int getWalkGoal() {
        return sharedPreferences.getInt("target_walk", 5000);
    }


    public void setRunGoal(Integer integer) {
        mEditor.putInt("target_run", integer);
        mEditor.commit();
    }

    public int getRunGoal() {
        return sharedPreferences.getInt("target_run", 3);
    }


    public void setCyclingGoal(Integer integer) {
        mEditor.putInt("target_cycling", integer);
        mEditor.commit();
    }

    public int getCyclingGoal() {
        return sharedPreferences.getInt("target_cycling", 8);
    }


    public boolean getIsSupportGPS() {
        return sharedPreferences.getBoolean("setIsSupportGPS", false);
    }

    public void setScreenOnTime(int value) {
        mEditor.putInt("screen_on_time", value);
        mEditor.commit();
    }

    public int getScreenOnTime(){
        return sharedPreferences.getInt("screen_on_time",5);
    }



    public void setHeight(int height){
        mEditor.putInt(HardSdk.getInstance().getAccount()+"_height", height);
        mEditor.commit();
    }



    public void setWeight(int weight){
        mEditor.putInt(HardSdk.getInstance().getAccount()+"_weight", weight);
        mEditor.commit();
    }


    public int getHeight(){
        return sharedPreferences.getInt("height",170);
    }

    public int getWeight(){
        return sharedPreferences.getInt("weight",60);
    }
}

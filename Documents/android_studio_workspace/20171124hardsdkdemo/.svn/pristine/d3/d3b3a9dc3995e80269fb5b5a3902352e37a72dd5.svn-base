package com.walnutin.hardsdkdemo.ProductNeed.entity;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2017/6/3 14:42
 */

public class BloodPressure implements Serializable {
    public String account = HardSdk.getInstance().getAccount();
    public int diastolicPressure; // 收缩压   120~160
    public int systolicPressure; // 舒张压   80~120
    public int durationTime;         // 一次测试持续时长
    public String testMomentTime;     // 一次测试结束的时间点
    public transient int status;                //测试状态

    final static long serialVersionUID = 1;

    public BloodPressure(String account, int diastolicPressure,
                         int systolicPressure, int durationTime, String testMomentTime) {
        this.account = account;
        this.diastolicPressure = diastolicPressure;
        this.systolicPressure = systolicPressure;
        this.durationTime = durationTime;
        this.testMomentTime = testMomentTime;
    }

    public BloodPressure() {
    }

    public int getDiastolicPressure() {
        return this.diastolicPressure;
    }

    public void setDiastolicPressure(int diastolicPressure) {
        this.diastolicPressure = diastolicPressure;
    }

    public int getSystolicPressure() {
        return this.systolicPressure;
    }

    public void setSystolicPressure(int systolicPressure) {
        this.systolicPressure = systolicPressure;
    }

    public int getDurationTime() {
        return this.durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public String getTestMomentTime() {
        return this.testMomentTime;
    }

    public void setTestMomentTime(String testMomentTime) {
        this.testMomentTime = testMomentTime;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

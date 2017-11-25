package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/8/17 10:08
 */
public class Alarm implements Serializable {
   public String formatFirstAlarm; // 闹钟周期
    public String formatSecondAlarm; // 闹钟周期
    public String formatThirdAlarm; // 闹钟周期
    public String firstAlarm =  "00:00";
    public String secondAlarm = "00:00";
    public String thirdAlarm =  "00:00";
    public  boolean isEnableFirstAlarm;
    public  boolean isEnableSecondAlarm;
    public boolean isEnableThirdAlarm;
    public int firstRepeat;
    public int secondtRepeat;
    public int thirdRepeat;

    public String getFormatFirstAlarm() {
        return formatFirstAlarm;
    }

    public void setFormatFirstAlarm(String formatFirstAlarm) {
        this.formatFirstAlarm = formatFirstAlarm;
    }

    public String getFormatSecondAlarm() {
        return formatSecondAlarm;
    }

    public void setFormatSecondAlarm(String formatSecondAlarm) {
        this.formatSecondAlarm = formatSecondAlarm;
    }

    public String getFormatThirdAlarm() {
        return formatThirdAlarm;
    }

    public void setFormatThirdAlarm(String formatThirdAlarm) {
        this.formatThirdAlarm = formatThirdAlarm;
    }

    public String getFirstAlarm() {
        return firstAlarm;
    }

    public void setFirstAlarm(String firstAlarm) {
        this.firstAlarm = firstAlarm;
    }

    public String getSecondAlarm() {
        return secondAlarm;
    }

    public void setSecondAlarm(String secondAlarm) {
        this.secondAlarm = secondAlarm;
    }

    public String getThirdAlarm() {
        return thirdAlarm;
    }

    public void setThirdAlarm(String thirdAlarm) {
        this.thirdAlarm = thirdAlarm;
    }

    public boolean isEnableFirstAlarm() {
        return isEnableFirstAlarm;
    }

    public void setEnableFirstAlarm(boolean enableFirstAlarm) {
        isEnableFirstAlarm = enableFirstAlarm;
    }

    public boolean isEnableSecondAlarm() {
        return isEnableSecondAlarm;
    }

    public void setEnableSecondAlarm(boolean enableSecondAlarm) {
        isEnableSecondAlarm = enableSecondAlarm;
    }

    public boolean isEnableThirdAlarm() {
        return isEnableThirdAlarm;
    }

    public void setEnableThirdAlarm(boolean enableThirdAlarm) {
        isEnableThirdAlarm = enableThirdAlarm;
    }

    public int getFirstRepeat() {
        return firstRepeat;
    }

    public void setFirstRepeat(int firstRepeat) {
        this.firstRepeat = firstRepeat;
    }

    public int getSecondtRepeat() {
        return secondtRepeat;
    }

    public void setSecondtRepeat(int secondtRepeat) {
        this.secondtRepeat = secondtRepeat;
    }

    public int getThirdRepeat() {
        return thirdRepeat;
    }

    public void setThirdRepeat(int thirdRepeat) {
        this.thirdRepeat = thirdRepeat;
    }
}

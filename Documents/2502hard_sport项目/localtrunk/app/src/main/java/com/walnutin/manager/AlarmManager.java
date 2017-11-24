package com.walnutin.manager;

import android.content.Context;

import com.walnutin.activity.MyApplication;
import com.walnutin.entity.Alarm;
import com.walnutin.entity.NoticeInfo;
import com.walnutin.util.Conversion;
import com.walnutin.util.DeviceSharedPf;

/**
 * 作者：MrJiang on 2016/8/17 10:32
 */
public class AlarmManager {
    Alarm alarm;
    private static AlarmManager alarmManager;
    DeviceSharedPf deviceSharedPf;

    private AlarmManager() {
        alarm = new Alarm();
        deviceSharedPf = DeviceSharedPf.getInstance(MyApplication.getContext());

    }

    static public AlarmManager getInstance(Context context) {
        if (alarmManager == null) {
            alarmManager = new AlarmManager();

        }
        return alarmManager;
    }

    public Alarm getLocalAlarmInfo() {
        alarm = (Alarm) Conversion.stringToObject(deviceSharedPf.getString("deviceAlarm", null));
        if (alarm == null) {
            alarm = new Alarm();

        }
        return alarm;
    }

    public void saveAlarmInfo() {
        deviceSharedPf.setString("deviceAlarm", Conversion.objectToString(alarm)); // 保存推送设置
    }

    public String getFormatFirstAlarm() {
        return alarm.getFormatFirstAlarm();
    }

    public void setFormatFirstAlarm(String formatFirstAlarm) {
        alarm.setFormatFirstAlarm(formatFirstAlarm);
    }

    public String getFormatSecondAlarm() {
        return alarm.getFormatSecondAlarm();
    }

    public void setFormatSecondAlarm(String formatSecondAlarm) {
        this.alarm.formatSecondAlarm = formatSecondAlarm;
    }

    public String getFormatThirdAlarm() {
        return alarm.formatThirdAlarm;
    }

    public void setFormatThirdAlarm(String formatThirdAlarm) {
        this.alarm.formatThirdAlarm = formatThirdAlarm;
    }

    public String getFirstAlarm() {
        return alarm.firstAlarm;
    }

    public void setFirstAlarm(String firstAlarm) {
        this.alarm.firstAlarm = firstAlarm;
    }

    public String getSecondAlarm() {
        return alarm.secondAlarm;
    }

    public void setSecondAlarm(String secondAlarm) {
        this.alarm.secondAlarm = secondAlarm;
    }

    public String getThirdAlarm() {
        return alarm.thirdAlarm;
    }

    public void setThirdAlarm(String thirdAlarm) {
        this.alarm.thirdAlarm = thirdAlarm;
    }

    public boolean isEnableFirstAlarm() {
        return alarm.isEnableFirstAlarm;
    }

    public void setEnableFirstAlarm(boolean enableFirstAlarm) {
        alarm.isEnableFirstAlarm = enableFirstAlarm;
    }

    public boolean isEnableSecondAlarm() {
        return alarm.isEnableSecondAlarm;
    }

    public void setEnableSecondAlarm(boolean enableSecondAlarm) {
        alarm.isEnableSecondAlarm = enableSecondAlarm;
    }

    public boolean isEnableThirdAlarm() {
        return alarm.isEnableThirdAlarm;
    }

    public void setEnableThirdAlarm(boolean enableThirdAlarm) {
        alarm.isEnableThirdAlarm = enableThirdAlarm;
    }

    public int getFirstRepeat() {
        return alarm.firstRepeat;
    }

    public void setFirstRepeat(int firstRepeat) {
        this.alarm.firstRepeat = firstRepeat;
    }

    public int getSecondtRepeat() {
        return alarm.secondtRepeat;
    }

    public void setSecondtRepeat(int secondtRepeat) {
        this.alarm.secondtRepeat = secondtRepeat;
    }

    public int getThirdRepeat() {
        return alarm.thirdRepeat;
    }

    public void setThirdRepeat(int thirdRepeat) {
        this.alarm.thirdRepeat = thirdRepeat;
    }

}

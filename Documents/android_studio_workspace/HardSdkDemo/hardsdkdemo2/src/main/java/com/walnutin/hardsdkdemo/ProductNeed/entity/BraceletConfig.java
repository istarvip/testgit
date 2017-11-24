package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2017/3/2 11:28
 */
public class BraceletConfig implements Serializable {

    public DeviceOtherSetting deviceOtherSetting;
    public Alarm alarm;
    public NoticeInfo noticeInfo;

    public BraceletConfig() {
        deviceOtherSetting = new DeviceOtherSetting();
        alarm = new Alarm();
        noticeInfo = new NoticeInfo();
    }

    public DeviceOtherSetting getDeviceOtherSetting() {
        return deviceOtherSetting;
    }

    public void setDeviceOtherSetting(DeviceOtherSetting deviceOtherSetting) {
        this.deviceOtherSetting = deviceOtherSetting;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public NoticeInfo getNoticeInfo() {
        return noticeInfo;
    }

    public void setNoticeInfo(NoticeInfo noticeInfo) {
        this.noticeInfo = noticeInfo;
    }
}

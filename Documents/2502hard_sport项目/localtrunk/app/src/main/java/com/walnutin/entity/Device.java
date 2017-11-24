package com.walnutin.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/8/6 15:19
 */
public class Device implements Serializable {
    public String deviceName;
    public String deviceAddr;

    public Device(String deviceName, String deviceAddr) {
        this.deviceName = deviceName;
        this.deviceAddr = deviceAddr;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }
}

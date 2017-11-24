package com.walnutin.hardsdkdemo.ProductNeed.entity;


import java.io.Serializable;

/**
 * 作者：MrJiang on 2017/5/9 16:51
 */
public class Clock implements Serializable {
    public int serial; // 闹钟序号
    public String time; // 闹钟时间  09:00
    public boolean isEnable;  // 是否开启
    public int repeat;  //重复周期
    public String type = "";
    public String tip = "";

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {

        return "serial:"+serial+" time: "+time+" repeat: "+repeat +" isOpen:"+isEnable;
    }
}

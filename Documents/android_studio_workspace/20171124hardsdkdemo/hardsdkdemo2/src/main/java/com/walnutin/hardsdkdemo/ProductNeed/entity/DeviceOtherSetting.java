package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/8/18 10:26
 */
public class DeviceOtherSetting implements Serializable {
    public boolean isUnlock; // 智能解锁
    public boolean isUnLost; // 智能防丢
    public boolean longSitRemind; // 久坐提醒
    public int longSitTime; // 久坐提醒分钟数
    public int lightScreenTime=5; // 亮屏时间

    public boolean isUnlock() {
        return isUnlock;
    }

    public void setUnlock(boolean unlock) {
        isUnlock = unlock;
    }

    public boolean isUnLost() {
        return isUnLost;
    }

    public void setUnLost(boolean unLost) {
        isUnLost = unLost;
    }

    public boolean isLongSitRemind() {
        return longSitRemind;
    }

    public void setLongSitRemind(boolean longSitRemind) {
        this.longSitRemind = longSitRemind;
    }

    public int getLongSitTime() {
        return longSitTime;
    }

    public void setLongSitTime(int longSitTime) {
        this.longSitTime = longSitTime;
    }

    public int getLightScreenTime() {
        return lightScreenTime;
    }

    public void setLightScreenTime(int lightScreenTime) {
        this.lightScreenTime = lightScreenTime;
    }
}

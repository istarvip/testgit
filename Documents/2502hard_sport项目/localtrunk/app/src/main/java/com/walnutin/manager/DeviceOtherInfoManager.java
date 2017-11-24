package com.walnutin.manager;

import android.content.Context;

import com.walnutin.entity.Alarm;
import com.walnutin.entity.DeviceOtherSetting;
import com.walnutin.entity.UserBean;
import com.walnutin.util.Conversion;
import com.walnutin.util.DeviceSharedPf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caro on 16/6/8.
 * <p/>
 * please instance this singon in application.maybe
 */
public class DeviceOtherInfoManager {

    private static DeviceOtherInfoManager instance;
    private Context context;
    private DeviceOtherSetting deviceOtherSetting;
    DeviceSharedPf deviceSharedPf;

    /**
     * @param context
     * @return
     */
    public static DeviceOtherInfoManager getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceOtherInfoManager(context);
        }
        return instance;
    }

    public DeviceOtherInfoManager(Context mcontext) {
        this.context = mcontext;
        deviceOtherSetting = new DeviceOtherSetting();
        deviceSharedPf = DeviceSharedPf.getInstance(context);

    }

    public DeviceOtherSetting getLocalDeviceOtherSettingInfo() {
        deviceOtherSetting = (DeviceOtherSetting) Conversion.stringToObject(deviceSharedPf.getString("deviceOtherSettingInfo", null));
        if (deviceOtherSetting == null) {
            deviceOtherSetting = new DeviceOtherSetting();

        }
        return deviceOtherSetting;
    }

    public void saveAlarmInfo() {
        deviceSharedPf.setString("deviceOtherSettingInfo", Conversion.objectToString(deviceOtherSetting)); // 保存推送设置
    }

    public boolean isUnlock() {
        return deviceOtherSetting.isUnlock;
    }

    public void setUnlock(boolean unlock) {
        deviceOtherSetting.isUnlock = unlock;
    }

    public boolean isUnLost() {
        return deviceOtherSetting.isUnLost;
    }

    public void setUnLost(boolean unLost) {
        deviceOtherSetting.isUnLost = unLost;
    }

    public boolean isLongSitRemind() {
        return deviceOtherSetting.longSitRemind;
    }

    public void setLongSitRemind(boolean longSitRemind) {
        this.deviceOtherSetting.longSitRemind = longSitRemind;
    }

    public int getLongSitTime() {
        return deviceOtherSetting.longSitTime;
    }

    public void setLongSitTime(int longSitTime) {
        this.deviceOtherSetting.longSitTime = longSitTime;
    }

    public int getLightScreenTime() {
        return deviceOtherSetting.lightScreenTime;
    }

    public void setLightScreenTime(int lightScreenTime) {
        this.deviceOtherSetting.lightScreenTime = lightScreenTime;
    }
}

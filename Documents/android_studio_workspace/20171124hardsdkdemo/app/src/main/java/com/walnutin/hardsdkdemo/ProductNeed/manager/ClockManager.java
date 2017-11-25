package com.walnutin.hardsdkdemo.ProductNeed.manager;

import android.content.Context;

import com.walnutin.hardsdkdemo.ProductNeed.entity.Clock;
import com.walnutin.hardsdkdemo.utils.Conversion;
import com.walnutin.hardsdkdemo.utils.DeviceSharedPf;
import com.walnutin.hardsdkdemo.utils.GlobalValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrJiang on 2016/8/17 10:32
 */
public class ClockManager {
    List<Clock> clockList;
    private static ClockManager alarmManager;
    DeviceSharedPf deviceSharedPf;
    String type;
    Context mContext;

    private ClockManager(Context context) {
        mContext = context;
        clockList = new ArrayList<>();
        deviceSharedPf = DeviceSharedPf.getInstance(context);

    }

    static public ClockManager getInstance(Context context) {
        if (alarmManager == null) {
            alarmManager = new ClockManager(context);

        }
        return alarmManager;
    }

    public List getLocalAlarmInfo(String type) {
        this.type = type;
        // if (type.equals(GlobalValue.FACTORY_HCH)) {
//            clockList = (List<Clock>) Conversion.stringToList(deviceSharedPf.getString("deviceClock_" + GlobalValue.FACTORY_FITCLOUD, null));

        // } else {
        clockList = (List<Clock>) Conversion.stringToList(deviceSharedPf.getString("deviceClock_" + type, null));

        //   }
        if (clockList == null) {
            clockList = new ArrayList<>();
        }
        return clockList;
    }


    public void saveAlarmInfo() {
        if (type.equals(GlobalValue.FACTORY_HCH)) {
            deviceSharedPf.setString("deviceClock_" + GlobalValue.FACTORY_HCH, Conversion.listToString(clockList)); // 保存推送设置
        } else if (type.equals(GlobalValue.FACTORY_YCY)) {
            deviceSharedPf.setString("deviceClock_" + GlobalValue.FACTORY_YCY, Conversion.listToString(clockList));
        } else if (type.equals(GlobalValue.FACTORY_WYP)) {
            deviceSharedPf.setString("deviceClock_" + GlobalValue.FACTORY_WYP, Conversion.listToString(clockList));
        }
    }


    public void addClock(Clock clock) {
        int i = 0;
        for (Clock clock1 : clockList) {
            if (clock1.getSerial() == clock.getSerial()) {
                break;
            }
            i++;
        }
        if (i < clockList.size()) {
            clockList.remove(i);
        }
        clockList.add(clock);
    }

    public void removeClock(Clock clock) {
        int i = 0;
        for (Clock clock1 : clockList) {
            if (clock1.getSerial() == clock.getSerial()) {
                break;
            }
            i++;
        }
        if (i < clockList.size()) {
            clockList.remove(i);
        }
    }


    public void setClockList(List clockList) {
        this.clockList = clockList;
    }

}

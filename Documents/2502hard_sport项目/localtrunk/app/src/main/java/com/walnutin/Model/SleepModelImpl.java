package com.walnutin.Model;

import android.content.Context;

import com.walnutin.entity.SleepModel;
import com.walnutin.util.Conversion;
import com.walnutin.util.PreferenceSettings;
import com.walnutin.util.TimeUtil;
import com.walnutin.view.SleepUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrJiang on 2016/8/3 13:45
 */
public class SleepModelImpl {
    public SleepModel sleepModel;
    private PreferenceSettings mPedometerSettings;
    Context context;

    public SleepModelImpl(Context context) {
        this.context = context;
        mPedometerSettings = PreferenceSettings.getInstance(context);
    }

    public SleepModel loadTodaySleepModel() {
        long laststampTime = mPedometerSettings.getLast_Seen();
        String lastDate = TimeUtil.timeStamp2YMDDate(laststampTime);
        boolean today = lastDate.equals(TimeUtil.nowDate());
        if (today) {
            sleepModel = (SleepModel) Conversion.stringToObject(mPedometerSettings.getString("devSleep", null));
            if (sleepModel == null) {
                sleepModel = new SleepModel();
            }
        }
        return sleepModel;
    }

    public void saveTodaySleepModel() {
        mPedometerSettings.setString("devSleep", Conversion.objectToString(sleepModel));
    }

    public void setDuraionTimeArray(int[] duraionTimeArray) {
        sleepModel.duraionTimeArray = duraionTimeArray;
    }

    public void setSleepStatusArray(int[] sleepStatusArray) {
        sleepModel.sleepStatusArray = sleepStatusArray;
    }

    public void setTimePointArray(int[] timePointArray) {
        sleepModel.timePointArray = timePointArray;
        List<Integer> durationStartPos = new ArrayList<Integer>();
        int initValue = timePointArray[0] - sleepModel.duraionTimeArray[0];
        initValue = initValue < 0 ? initValue + 1440 : initValue;
        durationStartPos.add(initValue);
        for (int i = 1; i < timePointArray.length; i++) {
            initValue += sleepModel.duraionTimeArray[i - 1];
            durationStartPos.add(initValue % 1440);
        }
        sleepModel.setDurationStartPos(durationStartPos);
    }

    private int getDurationLen() {
        int len = 0;
        for (int i = 0; i < sleepModel.duraionTimeArray.length; i++) {
            len += sleepModel.duraionTimeArray[i];
        }
        sleepModel.setAllDurationTime(len);
        return len;
    }

    public int getAllDurationTime() {
        return getDurationLen();
    }

    public int getTotalTime() {
        return sleepModel.totalTime;
    }

    public void setTotalTime(int totalTime) {
        sleepModel.totalTime = totalTime;
    }

    public int getLightTime() {
        return sleepModel.lightTime;
    }

    public void setLightTime(int lightTime) {
        sleepModel.lightTime = lightTime;
    }

    public int getDeepTime() {
        return sleepModel.deepTime;
    }

    public void setDeepTime(int deepTime) {
        sleepModel.deepTime = deepTime;
    }

    public String getStartSleep() {
        return sleepModel.startSleep;
    }

    public void setStartSleep() {
        sleepModel.startSleep = com.walnutin.view.TimeUtil.MinutiToTime(sleepModel.durationStartPos.get(0));

    }

    public String getEndSleep() {
        return sleepModel.endSleep;
    }

    public void setEndSleep() {
        sleepModel.endSleep = com.walnutin.view.TimeUtil.MinutiToTime(sleepModel.timePointArray[sleepModel.timePointArray.length - 1]);

    }

    public int[] getSleepStatusArray() {
        return sleepModel.getSleepStatusArray();
    }

    public List<Integer> getDurationStartPos() {
        return sleepModel.getDurationStartPos();
    }

    public int[] getTimePointArray() {
        return sleepModel.getTimePointArray();
    }

    public int[] getDuraionTimeArray() {
        return sleepModel.getDuraionTimeArray();
    }


}

package com.walnutin.Model;

import android.content.Context;

import com.walnutin.activity.MyApplication;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.SleepModel;
import com.walnutin.entity.StepModel;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.Conversion;
import com.walnutin.util.PreferenceSettings;
import com.walnutin.util.TimeUtil;

/**
 * 作者：MrJiang on 2016/8/3 14:07
 */
public class StepModelImpl {
    public StepModel stepModel;
    Context context;
    private PreferenceSettings mPedometerSettings;

    public StepModelImpl(Context context) {
        this.context = context;
        mPedometerSettings = PreferenceSettings.getInstance(context);
    }

    public StepModel loadTodayStepModel() {
        long laststampTime = mPedometerSettings.getLast_Seen();
        String lastDate = TimeUtil.timeStamp2YMDDate(laststampTime);
        boolean today = lastDate.equals(TimeUtil.nowDate());
        stepModel = new StepModel();
        stepModel.stepGoal = mPedometerSettings.getInt("goals", 10000);
        if (today) {
            stepModel.step = mPedometerSettings.getInt("steps", 0);
            stepModel.distance = mPedometerSettings.getFloat("distance", 0);
            stepModel.calories = mPedometerSettings.getInt("calories", 0);
        } else {
            stepModel.step = 0;
            stepModel.distance = 0;
            stepModel.calories = 0;
        }
        return stepModel;
    }

    public void saveTodayStepModel() {
        mPedometerSettings.setSteps(stepModel.step);
        mPedometerSettings.setDistance(stepModel.distance);
        mPedometerSettings.setCalories(stepModel.calories);
        mPedometerSettings.setStepGoal(stepModel.stepGoal);
        mPedometerSettings.setString("devStep", Conversion.objectToString(stepModel));
    }

    public void setStepGoal(int stepGoal) {
        stepModel.stepGoal = stepGoal;
        mPedometerSettings.setStepGoal(stepGoal);
    }

    public int getStepGoal() {
        return stepModel.stepGoal;
    }

    public void setCalories(int calories) {
        stepModel.calories = calories;
    }

    public void setDistance(float distance) {
        stepModel.distance = distance;
    }

    public int getStep() {
        return stepModel.step;
    }

    public int getCalories() {
        return stepModel.calories;
    }

    public float getDistance() {
        return stepModel.distance;
    }

    public void setStep(int step) {
        stepModel.step = step;
    }


    public void upLoadTodayData() {
        DailyInfo dailyInfo = new DailyInfo();
        dailyInfo.setAccount(MyApplication.account);
        dailyInfo.setStep(stepModel.step);
        dailyInfo.setDistance(stepModel.distance);
        dailyInfo.setCalories(stepModel.calories);
        HttpImpl.getInstance().upLoadStep(dailyInfo);

    }

}

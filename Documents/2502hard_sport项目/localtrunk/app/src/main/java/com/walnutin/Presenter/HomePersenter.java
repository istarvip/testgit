package com.walnutin.Presenter;

import android.content.Context;

import com.walnutin.Model.HeartRateModelImpl;
import com.walnutin.Model.SleepModelImpl;
import com.walnutin.Model.StepModelImpl;
import com.walnutin.entity.HeartRateModel;
import com.walnutin.entity.SleepModel;
import com.walnutin.entity.StepModel;
import com.walnutin.util.Conversion;
import com.walnutin.util.PreferenceSettings;
import com.walnutin.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrJiang on 2016/7/29 16:01
 */
public class HomePersenter {
    private Context context;
    static HomePersenter instance;
    public StepModelImpl stepModelImpl;
    public SleepModelImpl sleepModelImpl;
    public HeartRateModelImpl heartRateModelImpl;
    private PreferenceSettings mPedometerSettings;


    private HomePersenter(Context mcontext) {
        this.context = mcontext;
        stepModelImpl = new StepModelImpl(mcontext);
        sleepModelImpl = new SleepModelImpl(mcontext);
        heartRateModelImpl = new HeartRateModelImpl(mcontext);
        mPedometerSettings = PreferenceSettings.getInstance(context);
    }

    public static HomePersenter getInstance(Context context) {
        if (instance == null) {
            instance = new HomePersenter(context);
        }
        return instance;
    }


    public void loadTodayStep() {   //加载今天的数据
        stepModelImpl.loadTodayStepModel();
    }

    public void loadToayData() {
        stepModelImpl.loadTodayStepModel();
        sleepModelImpl.loadTodaySleepModel();
        heartRateModelImpl.loadTodayHeartModelList();
//        long laststampTime = mPedometerSettings.getLast_Seen();
//        String lastDate = TimeUtil.timeStamp2YMDDate(laststampTime);
//        boolean today = lastDate.equals(TimeUtil.nowDate());


    }

    public void saveTodayData() {      //保存今日数据到本地

        mPedometerSettings.saveTimestamp();
        //     mPedometerSettings.setString("devStep", Conversion.objectToString(stepModel));
        stepModelImpl.saveTodayStepModel();
        sleepModelImpl.saveTodaySleepModel();
        heartRateModelImpl.saveTodayHeartModel();
    }


    /*
    *
    * 记步模块
    *
    * */

    public void setStepGoal(int stepGoal) {
        stepModelImpl.setStepGoal(stepGoal);
    }

    public void setStep(int step) {
        stepModelImpl.setStep(step);
    }


    public int getStepGoal() {
        return stepModelImpl.getStepGoal();
    }

    public void setCalories(int calories) {
        stepModelImpl.setCalories(calories);
    }

    public void setDistance(float distance) {
        stepModelImpl.setDistance(distance);
    }

    public int getStep() {
        return stepModelImpl.getStep();
    }

    public int getCalories() {
        return stepModelImpl.getCalories();
    }

    public float getDistance() {
        return stepModelImpl.getDistance();
    }


    public void upLoadTodayData() {
        stepModelImpl.upLoadTodayData();
    }
    /*
    * 心率模块
    *
    * */

    public void createHeartRateModel() {
        heartRateModelImpl.createHeartRateModel();
    }

    public HeartRateModel getCurrentHeartRateModel() {
        return heartRateModelImpl.getCurrentHeartRateModel();
    }

    public void addHeartRateValue(int value) {     //增加心率值
        heartRateModelImpl.addHeartRateValue(value);
    }

    public void addHeartRateMode(HeartRateModel heartRateModel) {
        heartRateModelImpl.addOrderHeartMode(heartRateModel);     // 按心率从小到大的顺序添加心率mode
    }

    public HeartRateModel getNextHeartModel() {      //得到下一个心率模型
        return heartRateModelImpl.getNextHeartRateModeByCurrentRate();
    }

    public HeartRateModel getBeforeHeartModel() {      //得到前一个心率模型
        return heartRateModelImpl.getBeforHeartRateModeByCurrentRate();
    }

    public List<HeartRateModel> getHeartRateModelList() {
        return heartRateModelImpl.getHeartRateModelList();
    }

    public List<Integer> getLeftBarHeartList() {
        return heartRateModelImpl.getLeftBarHeartList();
    }

    public int getCurrentHeartRatePosition() {
        return heartRateModelImpl.getCurrentHeartIndex();
    }

    public List<Integer> getCurrentRateList() {    //得到当前心率集合
        return heartRateModelImpl.mapToList();
    }

    public void setRecentRateList(List<Integer> recentRateList) {
        heartRateModelImpl.setRecentRateList(recentRateList);
    }

    public List<Integer> getRecentRateList() {
        return heartRateModelImpl.getRecentRateList();
    }

    public List<Integer> getRealRateList() {
        return heartRateModelImpl.getRealRateList();
    }

    public void setRealRateList(List<Integer> realRateList) {
        heartRateModelImpl.setRealRateList(realRateList);
    }

    public void clearRealList() {
        heartRateModelImpl.clearCurrentRateList();
        //     heartRateModelImpl.getRealRateList().clear();
    }

    public void setRightLowRate() {
        heartRateModelImpl.setRightLowRate();
    }

    public void setRightHighRate() {
        heartRateModelImpl.setRightHighRate();
    }

    public void setRightCurrentRate() {
        heartRateModelImpl.setRightCurrentRate();
    }

    public void setHighRate(int highRate) {
        heartRateModelImpl.setHighRate(highRate);
    }

    public void setLowRate(int lowRate) {
        heartRateModelImpl.setLowRate(lowRate);
    }

    public void setCurrentRate(int currentRate) {
        heartRateModelImpl.setCurrentRate(currentRate);
    }

    public int getOnLineCurrentRate() {
        return heartRateModelImpl.getCurrentRate();
    }

    public int getLowRate() {
        return heartRateModelImpl.getLowRate();
    }

    public int getHighRate() {
        return heartRateModelImpl.getHighRate();
    }

    public int getCurrentRate() {
        return heartRateModelImpl.getCurrentRate();
    }

    /*
    * 睡眠模块
    *
    * */

    public void setDuraionTimeArray(int[] duraionTimeArray) {
        sleepModelImpl.setDuraionTimeArray(duraionTimeArray);
    }

    public void setSleepStatusArray(int[] sleepStatusArray) {
        sleepModelImpl.setSleepStatusArray(sleepStatusArray);
    }

    public void setTimePointArray(int[] timePointArray) {
        sleepModelImpl.setTimePointArray(timePointArray);
    }

    public int getDurationLen() {
        return sleepModelImpl.getAllDurationTime();
    }

    public int getLightTime() {
        return sleepModelImpl.getLightTime();
    }

    public void setLightTime(int lightTime) {
        sleepModelImpl.setLightTime(lightTime);
    }

    public int getDeepTime() {
        return sleepModelImpl.getDeepTime();
    }

    public void setDeepTime(int deepTime) {
        sleepModelImpl.setDeepTime(deepTime);
    }

    public String getStartSleep() {
        return sleepModelImpl.getStartSleep();
    }

    public void setStartSleep() {
        sleepModelImpl.setStartSleep();
    }

    public String getEndSleep() {
        return sleepModelImpl.getEndSleep();
    }

    public void setEndSleep() {
        sleepModelImpl.setEndSleep();
    }

    public int getTotalTime() {
        return sleepModelImpl.getTotalTime();
    }

    public void setTotalTime(int totalTime) {
        sleepModelImpl.setTotalTime(totalTime);
    }

    public int[] getSleepStatusArray() {
        return sleepModelImpl.getSleepStatusArray();
    }

    public List<Integer> getDurationStartPos() {
        return sleepModelImpl.getDurationStartPos();
    }

    public int[] getTimePointArray() {
        return sleepModelImpl.getTimePointArray();
    }

    public int[] getDuraionTimeArray() {
        return sleepModelImpl.getDuraionTimeArray();
    }

}

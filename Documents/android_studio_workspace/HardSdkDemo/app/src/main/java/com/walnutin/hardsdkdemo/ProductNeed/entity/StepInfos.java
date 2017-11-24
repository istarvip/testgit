package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.util.Map;

/**
 * 作者：MrJiang on 2016/5/30 21:27
 * caro
 * sid uid calories 字段目前无用
 */
public class StepInfos extends StepBaseInfo {
    private transient Integer sid;  //transient 为排除字段
    private transient Integer uid;
    private transient String weekDateFormat;
    private transient Integer weekOfYear;
    private transient int isUpLoad;
    private boolean finish_status;
    public transient Integer stepGoal;
    public Map<Integer, Integer> stepOneHourInfo;
   /* private int target;*/


    public int isUpLoad() {
        return isUpLoad;
    }

    public void setUpLoad(int upLoad) {
        isUpLoad = upLoad;
    }

    public String getWeekDateFormat() {
        return weekDateFormat;
    }

    public void setWeekDateFormat(String weekDateFormat) {
        this.weekDateFormat = weekDateFormat;
    }

    public int getIsUpLoad() {
        return isUpLoad;
    }

    public void setIsUpLoad(int isUpLoad) {
        this.isUpLoad = isUpLoad;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Map<Integer, Integer> getStepOneHourInfo() {
        return stepOneHourInfo;
    }

    public void setStepOneHourInfo(Map<Integer, Integer> stepOneHourInfo) {
        this.stepOneHourInfo = stepOneHourInfo;
    }

    public boolean isFinish_status() {
        return finish_status;
    }

    public void setFinish_status(boolean finish_status) {
        this.finish_status = finish_status;
    }
}

package com.walnutin.hardsdkdemo.ProductNeed.entity;

/**
 * 作者：MrJiang on 2016/5/30 21:27
 * caro
 * sid uid calories 字段目前无用
 */
public class WeekInfo extends StepBaseInfo {
    private transient int sid;  //transient 为排除字段
    private transient int uid;
    private int weekOfYear;
    private transient String weekDateFormat;
    private transient  int isUpLoad ;
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

    public Integer getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
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

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }


    @Override
    public int hashCode() {
        WeekInfo mDailyInfo = WeekInfo.this;
        String id = mDailyInfo.account;
        return id.hashCode();
    }

}

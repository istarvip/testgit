package com.walnutin.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/6/24 21:18
 */
public  class BaseInfo implements Serializable{
    protected String dates;//日期
    protected int step;//步数
    protected int calories;
    private  Integer weekOfYear;
    protected float distance;//距离
    protected String account;//账户

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(Integer weekOfYear) {
        this.weekOfYear = weekOfYear;
    }
}

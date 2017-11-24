package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2017/8/16 10:16
 */

public class SportData implements Serializable {

    public String account;
    public String date;
    public int step;
    public float distance;
    public int calories;
    public int stepGoal;
    public int maxHeart;
    public int minHeart;
    public int currentHeart;
    public transient int battery;
    public transient String week;
    public transient int progress;
    public transient int sleepTime;
    public int duration;

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String accout) {
        this.account = accout;
    }


    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStep() {
        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getDistance() {
        return this.distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getCalories() {
        return this.calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getStepGoal() {
        return this.stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getMaxHeart() {
        return this.maxHeart;
    }

    public void setMaxHeart(int maxHeart) {
        this.maxHeart = maxHeart;
    }

    public int getMinHeart() {
        return this.minHeart;
    }

    public void setMinHeart(int minHeart) {
        this.minHeart = minHeart;
    }

    public int getCurrentHeart() {
        return this.currentHeart;
    }

    public void setCurrentHeart(int currentHeart) {
        this.currentHeart = currentHeart;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void print() {
        //   return super.toString();
    }
}

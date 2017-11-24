package com.walnutin.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/7/29 16:11
 */
public class StepModel implements Serializable{
    public String todayDate;
    public int step;
    public int calories;
    public float distance;
    public int stepGoal;
    public int healthValue;
    public long credits;   //积分


    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public void setHealthValue(int healthValue) {
        this.healthValue = healthValue;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public int getStep() {
        return step;
    }

    public int getCalories() {
        return calories;
    }

    public float getDistance() {
        return distance;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public int getHealthValue() {
        return healthValue;
    }

    public long getCredits() {
        return credits;
    }
}

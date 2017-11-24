package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2017/8/16 10:48
 */
public class ExerciseData implements Serializable {
    public String account;
    public String date;   // yyyy-MM-dd HH:mm
    public int duration;
    public int calories;
    public int type;
    public int step;
    public float distance;
    public int haiba;
    public int circles; // 游泳圈数
    public String latLngs; // 经纬集合值
    public String screenShortPath;
    public int target;

    public ExerciseData(int type) {
        this.type = type;
    }

    public ExerciseData() {
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getCircles() {
        return circles;
    }

    public void setCircles(int circles) {
        this.circles = circles;
    }

    public String getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(String latLngs) {
        this.latLngs = latLngs;
    }

    public String getScreenShortPath() {
        return screenShortPath;
    }

    public void setScreenShortPath(String screenShortPath) {
        this.screenShortPath = screenShortPath;
    }

    public int getHaiba() {
        return haiba;
    }

    public void setHaiba(int haiba) {
        this.haiba = haiba;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}

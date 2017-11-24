package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/9/13 10:17
 */
public class HealthModel implements Serializable {
    public int stepScore;
    public int heartScore;
    public int sleepScore;
    public String account;
    public String date;
    public transient int isUpLoad;

    public int getStepScore() {
        return stepScore;
    }

    public void setStepScore(int stepScore) {
        this.stepScore = stepScore;
    }

    public int getHeartScore() {
        return heartScore;
    }

    public void setHeartScore(int heartScore) {
        this.heartScore = heartScore;
    }

    public int getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(int sleepScore) {
        this.sleepScore = sleepScore;
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
}

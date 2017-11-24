package com.walnutin.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrJiang on 2016/7/29 16:14
 */
public class SleepModel implements Serializable {
    public int[] duraionTimeArray;
    public int[] timePointArray;
    public int[] sleepStatusArray;
    public List<Integer> durationStartPos = new ArrayList<>(); // ÿһ�ο�ʼλ��
    public String startSleep;
    public String endSleep;
    public int allDurationTime;
    public int lightTime;
    public int deepTime;
    public int totalTime;

    public int[] getDuraionTimeArray() {
        return duraionTimeArray;
    }

    public void setDuraionTimeArray(int[] duraionTimeArray) {
        this.duraionTimeArray = duraionTimeArray;
    }

    public int[] getTimePointArray() {
        return timePointArray;
    }

    public void setTimePointArray(int[] timePointArray) {
        this.timePointArray = timePointArray;
    }

    public int[] getSleepStatusArray() {
        return sleepStatusArray;
    }

    public void setSleepStatusArray(int[] sleepStatusArray) {
        this.sleepStatusArray = sleepStatusArray;
    }

    public List<Integer> getDurationStartPos() {
        return durationStartPos;
    }

    public void setDurationStartPos(List<Integer> durationStartPos) {
        this.durationStartPos = durationStartPos;
    }

    public String getStartSleep() {
        return startSleep;
    }

    public void setStartSleep(String startSleep) {
        this.startSleep = startSleep;
    }

    public String getEndSleep() {
        return endSleep;
    }

    public void setEndSleep(String endSleep) {
        this.endSleep = endSleep;
    }

    public int getAllDurationTime() {
        return allDurationTime;
    }

    public void setAllDurationTime(int allDurationTime) {
        this.allDurationTime = allDurationTime;
    }

    public int getLightTime() {
        return lightTime;
    }

    public void setLightTime(int lightTime) {
        this.lightTime = lightTime;
    }

    public int getDeepTime() {
        return deepTime;
    }

    public void setDeepTime(int deepTime) {
        this.deepTime = deepTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }
}

package com.walnutin.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：MrJiang on 2016/7/29 16:18
 */
public class HeartRateModel implements Serializable{
    public List<Integer> recentRateList = new ArrayList<>();
    public List<Integer> realRateList = new ArrayList<>();
    public int lowRate;
    public int HighRate;
    public int currentRate;
    public int durationTime;         // 一次测试持续时长
    public Map<Long, Integer> heartTrendMap = new LinkedHashMap<Long, Integer>(); // Key 为时间戳、value是实时心率值

    public List<Integer> getRecentRateList() {
        return recentRateList;
    }

    public void setRecentRateList(List<Integer> recentRateList) {
        this.recentRateList = recentRateList;
    }

    public int getLowRate() {
        return lowRate;
    }

    public void setLowRate(int lowRate) {
        this.lowRate = lowRate;
    }

    public int getHighRate() {
        return HighRate;
    }

    public void setHighRate(int highRate) {
        HighRate = highRate;
    }

    public int getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(int currentRate) {
        this.currentRate = currentRate;
    }

    public List<Integer> getRealRateList() {
        return realRateList;
    }

    public void setRealRateList(List<Integer> realRateList) {
        this.realRateList = realRateList;
    }
}

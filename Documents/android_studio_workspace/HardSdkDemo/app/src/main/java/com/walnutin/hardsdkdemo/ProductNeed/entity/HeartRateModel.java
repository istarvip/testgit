package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：MrJiang on 2016/7/29 16:18
 */
public class HeartRateModel implements Serializable {
    public Map<Long, Integer> heartTrendMap = new LinkedHashMap<Long, Integer>(); // Key 为时间戳、value是实时心率值  测量中时间戳对应值map
    public transient List<Integer> recentRateList = new ArrayList<>();  //界面如果需要画出和查询 心率测试曲线则保存本次测量中所有数值
    public transient List<Integer> realRateList = new ArrayList<>();  //
    public transient int lowRate;  //最低心率    根据本次测量中所有数据中的最低值作为最低

    public transient int HighRate; //最高心率    根据本次测量中所有数据中的最高值作为最高
    //public transient int isUpload;
    public String account;
    public String testMomentTime;     // 一次测试结束的时间点
    public int currentRate;           //本次测得的心率值
    public int durationTime;         // 一次测试持续时长
    public boolean isRunning;      // 是否跑步测的
    public int status;

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

    public String getTestMomentTime() {
        return testMomentTime;
    }

    public void setTestMomentTime(String testMomentTime) {
        this.testMomentTime = testMomentTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setStatus(int value){
        status = value;
    }

    public int getStatus(){
        return status;
    }
}

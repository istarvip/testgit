package com.walnutin.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/5/30 21:27
 * caro
 * sid uid calories 字段目前无用
 */
public class DailyInfo extends BaseInfo{
    private transient int sid;  //transient 为排除字段
    private transient int uid;
    private transient  String weekDateFormat;
    private transient int weekOfYear;
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





}

package com.walnutin.entity;

import java.io.Serializable;

public class RankList implements Serializable{
  //  int groupid;
    String headimage;
    String nickname;
    int stepnumber;
    String account;

//    public int getGroupid() {
//        return groupid;
//    }
//
//    public void setGroupid(int groupid) {
//        this.groupid = groupid;
//    }

    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getStepnumber() {
        return stepnumber;
    }

    public void setStepnumber(int stepnumber) {
        this.stepnumber = stepnumber;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

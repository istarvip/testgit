package com.walnutin.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：MrJiang on 2016/6/7 14:55
 */
public class GroupDetailInfo implements Serializable{

    private String groupName;
    private Integer groupid;
    private String verify;
  //  Integer goal;
 //   String description;
    String account;
    private Integer type;
    //String headimage;
    private List<UserBean>user;

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getType() {
        return type;
    }

    public String getVerify() {
        return verify;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<UserBean> getUser() {
        return user;
    }

    public void setUser(List<UserBean> user) {
        this.user = user;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

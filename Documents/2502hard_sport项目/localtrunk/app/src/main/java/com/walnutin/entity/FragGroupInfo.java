package com.walnutin.entity;

import java.util.List;

/**
 * 作者：MrJiang on 2016/6/6 14:53
 */
public class FragGroupInfo {
    String groupName;
    int headcount;
    int type;
    int groupid;
    String description;
    List<String> headimage;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getHeadcount() {
        return headcount;
    }

    public void setHeadcount(int headcount) {
        this.headcount = headcount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getHeadimage() {
        return headimage;
    }

    public void setHeadimage(List<String> headimage) {
        this.headimage = headimage;
    }


}

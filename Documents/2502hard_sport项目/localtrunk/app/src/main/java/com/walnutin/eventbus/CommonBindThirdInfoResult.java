package com.walnutin.eventbus;

import com.walnutin.entity.ThirdInfo;
import com.walnutin.entity.UserBean;

import java.util.List;

public class CommonBindThirdInfoResult {
    private int state;
    private String msg;
    private List<ThirdInfo> userOpen;


    public CommonBindThirdInfoResult(int state, String msg) {
        this.state = state;
        this.msg = msg;

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List getUserOpen() {
        return userOpen;
    }

    public void setUserOpen(List userOpen) {
        this.userOpen = userOpen;
    }
}
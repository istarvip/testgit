package com.walnutin.eventbus;

import com.walnutin.entity.UserBean;

public class CommonloginResult {
    private int state;
    private String msg;
    private String token;
    private String sessionids;
    private UserBean user;


    public CommonloginResult(int state, String msg, UserBean user) {
        this.state = state;
        this.msg = msg;
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getState() {

        return state;
    }

    public String getSessionids() {
        return sessionids;
    }

    public void setSessionids(String sessionids) {
        this.sessionids = sessionids;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
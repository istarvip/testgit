package com.walnutin.eventbus;

//注册返回结果
public class CommonRegisterResult {
    private int state;
    private String msg;

    public CommonRegisterResult(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
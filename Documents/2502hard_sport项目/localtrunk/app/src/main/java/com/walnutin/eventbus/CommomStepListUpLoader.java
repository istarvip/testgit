package com.walnutin.eventbus;

/**
 * 作者：MrJiang on 2016/6/1 17:04
 */
public class CommomStepListUpLoader {
    private int state;
    private String msg;

    public CommomStepListUpLoader(int state, String msg) {
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

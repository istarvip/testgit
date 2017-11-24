package com.walnutin.eventbus;

/**
 * 作者：MrJiang on 2016/6/29 15:41
 */
public class CommonAvater {
    private int state;
    private String msg;
    private String image;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CommonAvater(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }
}

package com.walnutin.eventbus;

import com.walnutin.entity.BaseInfo;
import com.walnutin.entity.DailyInfo;

import java.util.List;

/**
 * 作者：MrJiang on 2016/5/31 11:02
 */
public class DailyStepListResult  {
    int state;
    String msg;
    public String datetype;
    List<BaseInfo> step;

    public DailyStepListResult(int state, String msg) {
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

    public List getStep() {
        return step;
    }

    public void setStep(List step) {
        this.step = step;
    }
}

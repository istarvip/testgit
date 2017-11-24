package com.walnutin.eventbus;

/**
 * 作者：MrJiang on 2016/6/13 18:02
 */
public class CommonBlueMsg {
    public boolean neglect; // state = ture

    public CommonBlueMsg(boolean neglect) {
        this.neglect =neglect;  //忽略此设备
    }
}

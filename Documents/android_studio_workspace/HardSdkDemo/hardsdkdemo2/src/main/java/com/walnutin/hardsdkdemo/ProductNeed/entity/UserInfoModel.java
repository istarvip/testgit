package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

public class UserInfoModel
        implements Serializable {
    private int userid;
    public String avatar = "";
    public String birthdate = "";
    public String phoneNum = "";
    public int checkin_mcount = 0;
    public boolean checkined = false;
    public String email = "";
    public String gender = "";
    public String height = "";
    public boolean is_email = false;
    public boolean is_qq = false;
    public boolean is_sina = false;
    public boolean is_wechat = false;
    public String nickname = "";
    public int target = 0;
    public String weight = "";
}

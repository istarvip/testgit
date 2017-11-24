package com.walnutin.Model;

import com.walnutin.activity.MyApplication;
import com.walnutin.util.MySharedPf;

/**
 * 作者：MrJiang on 2016/7/13 16:55
 */
public class MyModelImpl implements MyModelIntf {
    MySharedPf mySharedPf;


    public MyModelImpl() {
        mySharedPf = MySharedPf.getInstance(MyApplication.getContext());
    }

    @Override
    public String getHeight() {
        return mySharedPf.getString("height", "0");
    }

    @Override
    public String getWeight() {
        //  String weight = mySharedPf.getString("weight","0");
        return mySharedPf.getString("weight", "0");
    }

    @Override
    public Integer getGoal() {
        return mySharedPf.getInt("goal", 10000);
    }

    @Override
    public void setHeadImage() {

    }

    @Override
    public String getBMI() {
        String height = mySharedPf.getString("height", "0");
        String weight = mySharedPf.getString("weight", "0");
        float a = Float.parseFloat(weight);
        double b = Float.parseFloat(height);
        double c = b / 100 * 2;
        if((int)c==0){
            c=1;
        }
        int d = (int) (a / c);
        String e = String.valueOf(d);
        return e;
    }

    @Override
    public String getHeadImage() {
        return mySharedPf.getString("headimage");
    }
}

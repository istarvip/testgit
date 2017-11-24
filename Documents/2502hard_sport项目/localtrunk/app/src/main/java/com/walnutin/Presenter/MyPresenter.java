package com.walnutin.Presenter;

import com.walnutin.Model.MyModelImpl;
import com.walnutin.Model.MyModelIntf;
import com.walnutin.ViewInf.MyView;

/**
 * 作者：MrJiang on 2016/7/13 16:56
 */
public class MyPresenter {
    MyModelIntf myModelIntf;
    MyView myView;

    public MyPresenter(MyView mView) {
        this.myView = mView;
        myModelIntf = new MyModelImpl();
    }

    public String getHeight() {
        return myModelIntf.getHeight();
    }

    public String getWeight(){
        return myModelIntf.getWeight();
    }

    public Integer getGoal(){
        return myModelIntf.getGoal();
    }

    public void setHeadImage(){

    }

    public String getBMI(){
        return myModelIntf.getBMI();
    }

    public String getHeadImage(){
        return myModelIntf.getHeadImage();
    }
}

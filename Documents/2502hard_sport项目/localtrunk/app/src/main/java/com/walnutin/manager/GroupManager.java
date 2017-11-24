package com.walnutin.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.walnutin.activity.MyApplication;
import com.walnutin.db.SqlHelper;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.UserBean;
import com.walnutin.entity.WeekInfo;
import com.walnutin.util.Conversion;
import com.walnutin.util.DateUtils;
import com.walnutin.util.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by caro on 16/6/8.
 * <p/>
 * please instance this singon in application.maybe
 */
public class GroupManager {

    private static GroupManager instance;
    private Context context;
    private List<UserBean>userDelBeanList =new ArrayList<>();

    /**
     * @param context
     * @return
     */
    public static GroupManager getInstance(Context context) {
        if (instance == null) {
            instance = new GroupManager(context);
        }
        return instance;
    }

    public GroupManager(Context mcontext) {
        this.context = mcontext;

    }

    public List<UserBean> getUserDelBeanList() {
        return userDelBeanList;
    }

    public void setUserBeanList(List<UserBean> userBeanList) {
        this.userDelBeanList = userBeanList;
    }

    public void addDeletedUser(UserBean userBean){
        userDelBeanList.add(userBean);
    }
    public void clearDeleteList(){
        userDelBeanList.clear();
    }


}

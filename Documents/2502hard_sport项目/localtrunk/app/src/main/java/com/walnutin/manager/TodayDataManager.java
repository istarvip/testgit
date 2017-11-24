package com.walnutin.manager;

import android.content.Context;

/**
 * 作者：MrJiang on 2016/6/29 11:36
 */
public class TodayDataManager {

    private Context context;
    private static TodayDataManager instance;
    private int todayStep;
    private int todayCalories;
    private float todayDistance;
    String todayDate ="";


    public TodayDataManager(Context mcontext) {
        this.context = mcontext;

    }

    public static TodayDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new TodayDataManager(context);
        }
        return instance;
    }

    public int getTodayStep() {
        return todayStep;
    }

    public void setTodayStep(int todayStep) {
        this.todayStep = todayStep;
    }

    public int getTodayCalories() {
        return todayCalories;
    }

    public void setTodayCalories(int todayCalories) {
        this.todayCalories = todayCalories;
    }

    public float getTodayDistance() {
        return todayDistance;
    }

    public void setTodayDistance(float todayDistance) {
        this.todayDistance = todayDistance;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }
}

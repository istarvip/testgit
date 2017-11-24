package com.walnutin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.hard.R;
import com.walnutin.util.*;

import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */


public class SleepModuleLayout extends RelativeLayout {

    Context mContext;
    private View mRootView;
    SleepStraightLine sleepStraightLine;
    TextView sleepTime;
    TextView deepSleep;
    TextView lightSleep;
    TextView tipSleep;
    TextView totalSleep;
    onItemClick onItemClick;


    private interface onItemClick {
        void transfCredits(int healthValue);
    }

    public SleepModuleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_sleep, this);
        initView();
    }

    public SleepModuleLayout(Context context) {
        super(context);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_sleep, this);
        initView();
    }

    private void initView() {
        sleepStraightLine = (SleepStraightLine) mRootView.findViewById(R.id.sleepStraight);
        sleepTime = (TextView) mRootView.findViewById(R.id.sleepTime);
        deepSleep = (TextView) mRootView.findViewById(R.id.deepSleep);
        lightSleep = (TextView) mRootView.findViewById(R.id.lightSleep);
        tipSleep = (TextView) mRootView.findViewById(R.id.tipSleep);
        totalSleep = (TextView) mRootView.findViewById(R.id.totalSleep);

    }

    public void setOnItemClick(onItemClick onItemClick1) {
        onItemClick = onItemClick1;
    }

    public void setDeepSleep(String deepSleeps) {
        deepSleep.setText(deepSleeps);
    }

    public void setLightSleep(String lightSleep1) {
        lightSleep.setText(lightSleep1);
    }

    public void setTipSleep(String tip) {
        tipSleep.setText(tip);
    }

    public void setTotalSleep(int totalSleeps) {
        totalSleep.setText("时长："+ TimeUtil.MinitueToPrefix(totalSleeps)+"小时 "+TimeUtil.MinitueToSuffix(totalSleeps)+"分钟");
    }


    public void setAllSleepTime(String time) {
        sleepTime.setText(time);
    }


    public void setDuraionTimeArray(int[] duraionTimeArray) {

        sleepStraightLine.setPerDurationTime(duraionTimeArray); // 设置 睡眠持续多少分钟
    }

    public void setAllDurationTime(int duraionTime) {
        sleepStraightLine.setAllDurationTime(duraionTime); // 设置全部睡眠分钟数
    }

    public void setTimePointArray(List<Integer> timePointArray) { // 设置timePointArray前需要设置 duraionTimeArray
        sleepStraightLine.setDurationStartPos(timePointArray);
    }

    public void setSleepStatusArray(int[] sleepStatusArray) {
        sleepStraightLine.setDurationStatus(sleepStatusArray);  //设置状态
    }

    public void setStartSleepTime(String startSleetTime) {
        sleepStraightLine.setStartSleepTime(startSleetTime);
    }

    public void setEndSleepTime(String endSleepTime) {
        sleepStraightLine.setEndSleepTime(endSleepTime);
    }

    public void update() {      //更新进度条对话框

        sleepStraightLine.update();
    }

}

package com.walnutin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.walnutin.hard.R;
import com.walnutin.view.HeartRateModuleLayout;
import com.walnutin.view.HeartRateStraightLine;
import com.walnutin.view.SleepModuleLayout;
import com.walnutin.view.SleepUtils;
import com.walnutin.view.StepModuleLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrJiang on 2016/7/29 10:36
 */
public class TestAcitivty extends Activity {

    StepModuleLayout stepModuleLayout;
    SleepModuleLayout sleepModuleLayout;
    HeartRateModuleLayout heartRateModuleLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        stepModuleLayout = (StepModuleLayout) findViewById(R.id.stepModule);
        sleepModuleLayout = (SleepModuleLayout) findViewById(R.id.sleepModule);
        heartRateModuleLayout = (HeartRateModuleLayout) findViewById(R.id.rateModule);

        initData();
    }

    private void initData() {
        stepModuleLayout.setGoalStepValue(15000);
        stepModuleLayout.setCurrentStep(8000);
        stepModuleLayout.setCurrentDistance("3.6");
        stepModuleLayout.setCurrentCalo(888);

        List<Integer> t = new ArrayList<Integer>();
        t.add(60);
        t.add(88);
        t.add(54);
        t.add(77);
        t.add(99);
        heartRateModuleLayout.setRecentRateList(t);
        heartRateModuleLayout.setHeartRate(50, 60, 100);

        int[] duraionTimeArray = {15, 15, 60, 30, 15};
        int[] timePointArray = {1389, 1404, 24, 54, 69};
        int[] sleepStatusArray = {2, 1, 0, 1, 0};

        SleepUtils sleepUtils = new SleepUtils();
        sleepUtils.setDuraionTimeArray(duraionTimeArray);
        sleepUtils.setSleepStatusArray(sleepStatusArray);
        sleepUtils.setTimePointArray(timePointArray);

        sleepModuleLayout.setAllDurationTime(sleepUtils.getDurationLen());
        sleepModuleLayout.setStartSleepTime(sleepUtils.getStartSleep());
        sleepModuleLayout.setEndSleepTime(sleepUtils.getEndSleep());

        sleepModuleLayout.setDuraionTimeArray(duraionTimeArray);
        sleepModuleLayout.setSleepStatusArray(sleepStatusArray);
        sleepModuleLayout.setTimePointArray(sleepUtils.getDurationStartPos());
        sleepModuleLayout.update();

    }

}

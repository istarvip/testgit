package com.walnutin.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.walnutin.Presenter.HomePersenter;
import com.walnutin.activity.WatchLinkActivity;
import com.walnutin.adapter.WeekPagerAdapter;
import com.walnutin.entity.HeartRateModel;
import com.walnutin.entity.StepModel;
import com.walnutin.eventbus.HomeDataNotice;
import com.walnutin.eventbus.StepChangeNotify;
import com.walnutin.eventbus.WeekChangeNotify;
import com.walnutin.hard.R;
import com.walnutin.manager.RunManager;
import com.walnutin.view.HeartRateModuleLayout;
import com.walnutin.view.LineChart;
import com.walnutin.view.SleepModuleLayout;
import com.walnutin.view.SleepUtils;
import com.walnutin.view.StepModuleLayout;
import com.yc.peddemo.utils.GlobalVariable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    View view;
    StepModuleLayout stepModuleLayout;
    SleepModuleLayout sleepModuleLayout;
    HeartRateModuleLayout heartRateModuleLayout;
    HomePersenter homePersenter;
    BroadcastReceiver deviceReceiver;
    ImageView imgBle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        //     registerStepBroad();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        homePersenter = HomePersenter.getInstance(getContext());
        loadLocalData();
        return view;

    }

    private void initView() {
        stepModuleLayout = (StepModuleLayout) view.findViewById(R.id.stepModule);
        sleepModuleLayout = (SleepModuleLayout) view.findViewById(R.id.sleepModule);
        heartRateModuleLayout = (HeartRateModuleLayout) view.findViewById(R.id.rateModule);

        heartRateModuleLayout.setOnItemClick(new HeartRateModuleLayout.onItemClick() {
            @Override
            public void transfCredits(int healthValue) { // 转换积分

            }

            @Override
            public void nextRate() {        // 切换到下一个 心率值

                if (homePersenter.getCurrentHeartRatePosition() == homePersenter.getHeartRateModelList().size() - 1 || homePersenter.getHeartRateModelList().size() < 1) {
                    Toast.makeText(getContext(), "当前已是最后一个值", Toast.LENGTH_SHORT).show();
                    return;
                }
                heartRateModuleLayout.setCenterHeart(homePersenter.getNextHeartModel().currentRate);
                heartRateModuleLayout.setLeftBarRateList(homePersenter.getLeftBarHeartList());
                heartRateModuleLayout.setRecentRateList(homePersenter.getCurrentRateList());
            }

            @Override
            public void beforeRate() {       // 切换到上一个心率值
                if (homePersenter.getCurrentHeartRatePosition() == 0) {
                    Toast.makeText(getContext(), "当前已是最小值", Toast.LENGTH_SHORT).show();
                    return;
                }
                heartRateModuleLayout.setCenterHeart(homePersenter.getBeforeHeartModel().currentRate);
                heartRateModuleLayout.setLeftBarRateList(homePersenter.getLeftBarHeartList());
                heartRateModuleLayout.setRecentRateList(homePersenter.getCurrentRateList());

            }
        });

        imgBle = (ImageView) view.findViewById(R.id.im_ble);
        imgBle.setOnClickListener(this);

    }

    private void initData() {
        homePersenter.loadTodayStep();
        homePersenter.loadToayData();  // 加载今日数据
        stepModuleLayout.setGoalStepValue(homePersenter.getStepGoal());
        stepModuleLayout.setCurrentStep(homePersenter.getStep());
        DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足
        stepModuleLayout.setCurrentDistance(String.valueOf(decimalFormat.format(homePersenter.getDistance())));
        stepModuleLayout.setCurrentCalo(homePersenter.getCalories());

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

    void loadLocalData() {
        //  homePersenter.loadTodayStep();
        try {

            homePersenter.loadToayData();  // 加载今日数据
            stepModuleLayout.setGoalStepValue(homePersenter.getStepGoal());
            stepModuleLayout.setCurrentStep(homePersenter.getStep());
            DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足
            stepModuleLayout.setCurrentDistance(String.valueOf(decimalFormat.format(homePersenter.getDistance())));
            stepModuleLayout.setCurrentCalo(homePersenter.getCalories());

//            heartRateModuleLayout.setRecentRateList(homePersenter.getRecentRateList());
//            heartRateModuleLayout.setHeartRate(homePersenter.getLowRate(), homePersenter.getCurrentRate(),
//                    homePersenter.getHighRate());

            heartRateModuleLayout.setCenterHeart(homePersenter.getCurrentHeartRateModel().currentRate);
            heartRateModuleLayout.setLeftBarRateList(homePersenter.getLeftBarHeartList());
            heartRateModuleLayout.setRecentRateList(homePersenter.getCurrentRateList());


            sleepModuleLayout.setAllDurationTime(homePersenter.getDurationLen());
            sleepModuleLayout.setTotalSleep(homePersenter.getTotalTime());
            sleepModuleLayout.setStartSleepTime(homePersenter.getStartSleep());
            sleepModuleLayout.setEndSleepTime(homePersenter.getEndSleep());

            sleepModuleLayout.setDuraionTimeArray(homePersenter.getDuraionTimeArray());
            sleepModuleLayout.setSleepStatusArray(homePersenter.getSleepStatusArray());
            sleepModuleLayout.setTimePointArray(homePersenter.getDurationStartPos());
            sleepModuleLayout.update();
        } catch (Exception e) {
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onClick(View v) {
        System.out.println("viewid:" + v.getId() + " : " + R.id.im_ble);
        switch (v.getId()) {
            case R.id.im_ble:
                Intent WatchIntent = new Intent(HomeFragment.this.getActivity(), WatchLinkActivity.class);
                startActivity(WatchIntent);
                break;
        }

    }


    @Subscribe
    public void stepChange(HomeDataNotice.StepChange s) {
        stepModuleLayout.setCurrentCalo(homePersenter.getCalories());
        stepModuleLayout.setCurrentStep(homePersenter.getStep());
        DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足
        stepModuleLayout.setCurrentDistance(String.valueOf(decimalFormat.format(homePersenter.getDistance())));
    }

    @Subscribe
    public void sleepChange(HomeDataNotice.SleepChange s) {
        sleepModuleLayout.setAllDurationTime(homePersenter.getDurationLen());
        sleepModuleLayout.setStartSleepTime(homePersenter.getStartSleep());
        sleepModuleLayout.setEndSleepTime(homePersenter.getEndSleep());
        sleepModuleLayout.setTotalSleep(homePersenter.getTotalTime());
        //    sleepModuleLayout.setDeepSleep("深度睡眠");
        ///      sleepModuleLayout.setLightSleep("浅度睡眠");
        sleepModuleLayout.setDuraionTimeArray(homePersenter.getDuraionTimeArray());
        sleepModuleLayout.setSleepStatusArray(homePersenter.getSleepStatusArray());
        sleepModuleLayout.setTimePointArray(homePersenter.getDurationStartPos());
        sleepModuleLayout.update();
    }

    @Subscribe
    public void heartChange(HomeDataNotice.HeartRateChange s) {
        //  heartRateModuleLayout.setRecentRateList(homePersenter.getRecentRateList());
        //  heartRateModuleLayout.setHeartRate(homePersenter.getLowRate(), homePersenter.getCurrentRate(), homePersenter.getHighRate());

    }

    @Subscribe
    public void heartRealChange(HomeDataNotice.HeartRateRealChange s) {
        if (heartRateModuleLayout.getMeasuringState() == false) {
            heartRateModuleLayout.setMeasuring(true);
        }
        heartRateModuleLayout.setRecentRateList(homePersenter.getCurrentRateList()); //右边实时跳变记录

        if (s.isMeasuring == GlobalVariable.RATE_TEST_FINISH) {  //心率测试完成
            heartRateModuleLayout.setMeasuring(false);
            if (homePersenter.getCurrentRateList().size() < 2) {
                return;
            }
            homePersenter.setRightCurrentRate();
            homePersenter.setRightHighRate();
            homePersenter.setRightLowRate();
            homePersenter.addHeartRateMode(homePersenter.getCurrentHeartRateModel());
            heartRateModuleLayout.setCenterHeart(homePersenter.getCurrentRate());
            heartRateModuleLayout.setLeftBarRateList(homePersenter.getLeftBarHeartList());
//            //     homePersenter.clearRealList();
//            System.out.println("sync test size: " + homePersenter.getHeartRateModelList().size());
//
//            for (HeartRateModel heartRateModel : homePersenter.getHeartRateModelList()){
//                System.out.println("sync test history :"+heartRateModel.heartTrendMap.size());
//            }
        }
        //     heartRateModuleLayout.setHeartRate(homePersenter.getLowRate(), homePersenter.getCurrentRate(), homePersenter.getHighRate());

    }

    @Subscribe
    public void heartMeasure(StepChangeNotify.HeartMeasure sync) {     // true 开始测量
        if (sync.isMeasure) {
            homePersenter.createHeartRateModel();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        homePersenter.saveTodayData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deviceReceiver != null) {
            getActivity().unregisterReceiver(deviceReceiver);
        }
        EventBus.getDefault().unregister(this);
    }


//    private void registerStepBroad() {
//        IntentFilter intentFilter = new IntentFilter("com.hard.stepChangeIntent");
//        intentFilter.addAction("com.hard.rateChangeIntent");
//        intentFilter.addAction("com.hard.sleepChangeIntent");
//        //  intentFilter.addAction();
//        deviceReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals("com.hard.stepChangeIntent")) {
//                    int step = intent.getIntExtra("step", 0);
//                    float dis = intent.getFloatExtra("distance", 0);
//                    int cal = intent.getIntExtra("calories", 0);
//                    homePersenter.setStep(step);
//                    homePersenter.setDistance(dis);
//                    homePersenter.setCalories(cal);
//                    DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足
//                    stepModuleLayout.setCurrentCalo(cal);
//                    stepModuleLayout.setCurrentStep(step);
//                    stepModuleLayout.setCurrentDistance(String.valueOf(decimalFormat.format(dis)));
//                } else if (intent.getAction().equals("com.hard.rateChangeIntent")) {
//                    int low = intent.getIntExtra("lowRate", 0);
//                    int high = intent.getIntExtra("highRate", 0);
//                    int current = intent.getIntExtra("currentRate", 0);
//                    homePersenter.setCurrentRate(current);
//                    ;
//                    homePersenter.setLowRate(low);
//                    homePersenter.setHighRate(high);
//                    homePersenter.getRecentRateList().add(current);
//                    heartRateModuleLayout.setRecentRateList(homePersenter.getRecentRateList());
//                    heartRateModuleLayout.setHeartRate(low, current, high);
//                } else if (intent.getAction().equals("com.hard.sleepChangeIntent")) {
//                    int[] duraionTimeArray = intent.getIntArrayExtra("duraionTimeArray");
//                    int[] timePointArray = intent.getIntArrayExtra("timePointArray");
//                    int[] sleepStatusArray = intent.getIntArrayExtra("sleepStatusArray");
//                    int deepTime = intent.getIntExtra("deepTime", 0);
//                    int lightTime = intent.getIntExtra("lightTime", 0);
//                    int totalTime = intent.getIntExtra("sleepAllTime", 0);
//
//                    homePersenter.setDuraionTimeArray(duraionTimeArray);
//                    homePersenter.setSleepStatusArray(sleepStatusArray);
//                    homePersenter.setTimePointArray(timePointArray);
//                    homePersenter.setDeepTime(deepTime);
//                    homePersenter.setLightTime(lightTime);
//                    homePersenter.setTotalTime(totalTime);
//                    homePersenter.setStartSleep();
//                    homePersenter.setEndSleep();
//
//                    sleepModuleLayout.setAllDurationTime(homePersenter.getDurationLen());
//                    sleepModuleLayout.setStartSleepTime(homePersenter.getStartSleep());
//                    sleepModuleLayout.setEndSleepTime(homePersenter.getEndSleep());
//                    sleepModuleLayout.setTotalSleep(totalTime);
//                    //    sleepModuleLayout.setDeepSleep("深度睡眠");
//                    ///      sleepModuleLayout.setLightSleep("浅度睡眠");
//                    sleepModuleLayout.setDuraionTimeArray(duraionTimeArray);
//                    sleepModuleLayout.setSleepStatusArray(sleepStatusArray);
//                    sleepModuleLayout.setTimePointArray(homePersenter.getDurationStartPos());
//                    sleepModuleLayout.update();
//
//
//                }
//            }
//        };
//        getActivity().registerReceiver(deviceReceiver, intentFilter);
//    }
}

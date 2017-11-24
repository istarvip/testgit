/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.walnutin.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.walnutin.Presenter.HomePersenter;
import com.walnutin.db.SqlHelper;
import com.walnutin.entity.DailyInfo;
import com.walnutin.eventbus.CommomStepUpLoader;
import com.walnutin.eventbus.HomeDataNotice;
import com.walnutin.eventbus.UpdateNotify;
import com.walnutin.activity.MyApplication;
import com.walnutin.http.HttpImpl;
import com.walnutin.step.CaloriesNotifier;
import com.walnutin.step.DistanceNotifier;
import com.walnutin.step.StepDetector;
import com.walnutin.step.StepDisplayer;
import com.walnutin.util.PreferenceSettings;
import com.walnutin.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class StepService extends Service {
    private static final String TAG = "stepService";
    private PreferenceSettings mPedometerSettings;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    private StepDisplayer mStepDisplayer;
    private DistanceNotifier mDistanceNotifier;
    private CaloriesNotifier mCaloriesNotifier;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNM;
    private int randomTime;
    private int mSteps;
    private float mDistance;
    private TimeCount timeCount;
    private int mCalories;
    private int minitues = 0;
    private long startAppTimeMinitue = 0;
    private long ONE_HOUR = 60 * 60;  //一个小时
    BroadcastReceiver stepReceiver;
    HomePersenter homePersenter;

    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();

        mPedometerSettings = PreferenceSettings.getInstance(getApplicationContext());
        acquireWakeLock();
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        registerDetector();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        mStepDisplayer = new StepDisplayer(mPedometerSettings);
        mStepDisplayer.setSteps(mSteps = mPedometerSettings.getInt("steps", 0));
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);


        mDistanceNotifier = new DistanceNotifier(mDistanceListener);
        mDistanceNotifier.setDistance(mDistance = mPedometerSettings.getFloat("distance", 0));
        mStepDetector.addStepListener(mDistanceNotifier);

        mCaloriesNotifier = new CaloriesNotifier(mCaloriesListener);
        mCaloriesNotifier.setCalories(mCalories = mPedometerSettings.getInt("calories", 0));
        mStepDetector.addStepListener(mCaloriesNotifier);

        timeCount = new TimeCount(oneDay, timeGap);
        timeCount.start();

        homePersenter = HomePersenter.getInstance(getApplicationContext());

        reloadSettings();
        readTodayValues();
        initTimerBroad();

        EventBus.getDefault().register(this);
        System.out.println("StepService启动了~~~~~");


        registerStepBroad();
        //     checkLocalUnLoadData();
    }

    private void checkLocalUnLoadData() {
        List<DailyInfo> dailyInfos = SqlHelper.instance().getStepsByIsUpLoad(MyApplication.account, 0);
        //上传 之前没有上传过的数据到服务器中
        if (dailyInfos != null && dailyInfos.size() > 0) {
            HttpImpl.getInstance().upLoadListStep(dailyInfos);
        }
    }

    private void registerStepBroad() {
        IntentFilter intentFilter = new IntentFilter("com.hard.stepChangeIntent");
        intentFilter.addAction("com.hard.rateChangeIntent");
        intentFilter.addAction("com.hard.rateRealChange");
        intentFilter.addAction("com.hard.sleepChangeIntent");
        //  intentFilter.addAction();
        stepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                int step = intent.getIntExtra("step", 0);
//                float dis = intent.getFloatExtra("distance", 0);
//                int cal = intent.getIntExtra("calories", 0);
//                if (dis > mDistance) {
//                    mDistanceNotifier.setDistance(dis);
//                }
//                if (step > mSteps) {
//                    mStepDisplayer.setSteps(step);
//                }
//                if (cal > mCalories) {
//                    mCaloriesNotifier.setCalories(cal);
//                }

                // Toast.makeText(getApplicationContext(),"手环："+step,Toast.LENGTH_SHORT).show();
                //       System.out.println("StepService里的Step=" + step);
                if (intent.getAction().equals("com.hard.stepChangeIntent")) {
                    int step = intent.getIntExtra("step", 0);
                    float dis = intent.getFloatExtra("distance", 0);
                    int cal = intent.getIntExtra("calories", 0);
                    homePersenter.setStep(step);
                    homePersenter.setDistance(dis);
                    homePersenter.setCalories(cal);
                    EventBus.getDefault().post(new HomeDataNotice.StepChange());
                    mHandler.removeMessages(1);
                    mHandler.sendEmptyMessageDelayed(1, 10000);

                } else if (intent.getAction().equals("com.hard.rateChangeIntent")) {
                    int low = intent.getIntExtra("lowRate", 0);
                    int high = intent.getIntExtra("highRate", 0);
                    int current = intent.getIntExtra("currentRate", 0);
                    homePersenter.setCurrentRate(current);
                    homePersenter.setLowRate(low);
                    homePersenter.setHighRate(high);
                    homePersenter.addHeartRateValue(current);
                  //  homePersenter.getRecentRateList().add(0,current);
                    EventBus.getDefault().post(new HomeDataNotice.HeartRateChange());

                }  else if (intent.getAction().equals("com.hard.rateRealChange")) {
                    int current = intent.getIntExtra("currentRate", 0);
                    int state = intent.getIntExtra("status", 0);
                    homePersenter.addHeartRateValue(current);

                    //     homePersenter.getRealRateList().add(0,current);
                    EventBus.getDefault().post(new HomeDataNotice.HeartRateRealChange(state));

                } else if (intent.getAction().equals("com.hard.sleepChangeIntent")) {
                    int[] duraionTimeArray = intent.getIntArrayExtra("duraionTimeArray");
                    int[] timePointArray = intent.getIntArrayExtra("timePointArray");
                    int[] sleepStatusArray = intent.getIntArrayExtra("sleepStatusArray");
                    int deepTime = intent.getIntExtra("deepTime", 0);
                    int lightTime = intent.getIntExtra("lightTime", 0);
                    int totalTime = intent.getIntExtra("sleepAllTime", 0);
                    homePersenter.setDuraionTimeArray(duraionTimeArray);
                    homePersenter.setSleepStatusArray(sleepStatusArray);
                    homePersenter.setTimePointArray(timePointArray);
                    homePersenter.setDeepTime(deepTime);
                    homePersenter.setLightTime(lightTime);
                    homePersenter.setTotalTime(totalTime);
                    homePersenter.setStartSleep();
                    homePersenter.setEndSleep();
                    EventBus.getDefault().post(new HomeDataNotice.SleepChange());

                }

            }
        };
        registerReceiver(stepReceiver, intentFilter);
    }

    int postTime = 2330;     // 23:30提交
    private BroadcastReceiver receiver;

    private void initTimerBroad() {
        startAppTimeMinitue = System.currentTimeMillis() / 1000;
        Random random = new Random();
        randomTime = random.nextInt(20) - 10;  // 晚上 11:30  前后十分钟
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // drawWallpaper();
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minitues = cal.get(Calendar.MINUTE);
                int currentTime = Integer.valueOf(String.valueOf(hour + "" + minitues));
                if (currentTime == 0000) {      //晚上 十二点 重置今天数据
                    readTodayValues(); // 重置今天数据
                    EventBus.getDefault().post(new UpdateNotify()); // 重新加载今日数据
                }
            }
        };
        registerReceiver(receiver, intentFilter);
    }

    @Subscribe
    public void onUpLoaderResult(CommomStepUpLoader su) {
        if (su.getState() == 0) {
            //      Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
        } else {
            //       Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void upLoadTodayData() {
        DailyInfo dailyInfo = new DailyInfo();
        dailyInfo.setAccount(MyApplication.account);
        dailyInfo.setStep(mSteps);
        dailyInfo.setDistance(mDistance);
        dailyInfo.setCalories(mCalories);
        HttpImpl.getInstance().upLoadStep(dailyInfo);
        startAppTimeMinitue = System.currentTimeMillis() / 1000;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");

        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        unregisterReceiver(receiver);
        mHandler.removeCallbacksAndMessages(null);
        mPedometerSettings.setSteps(mSteps);
        mPedometerSettings.setDistance(mDistance);
        mPedometerSettings.setCalories(mCalories);
        mPedometerSettings.setMinitues(minitues);
        wakeLock.release();
        super.onDestroy();

        unregisterDetector();
        // mSensorManager.unregisterListener(mStepDetector);

        if (timeCount != null) {
            timeCount.cancel();
        }

        if (stepReceiver != null) {
            unregisterReceiver(stepReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    private void registerDetector() {
//        mSensor = mSensorManager.getDefaultSensor(
//                Sensor.TYPE_ACCELEROMETER );
//        mSensorManager.registerListener(mStepDetector,
//                mSensor,
//                SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        //   mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value);

        public void distanceChanged(float value);

        public void timeChanged(int value);

        public void caloriesChanged(int value);
    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    public void reloadSettings() {
        if (mStepDetector != null) {
            mStepDetector.setSensitivity(
                    Float.valueOf(mPedometerSettings.getString("sensitivity", "10"))
            );
        }
        if (mStepDisplayer != null) mStepDisplayer.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
        if (mCaloriesNotifier != null) mCaloriesNotifier.reloadSettings();

    }

    public void resetValues() {
        mStepDisplayer.setSteps(0);
        mDistanceNotifier.setDistance(0);
        mCaloriesNotifier.setCalories(0);
        minitues = 0;
        mSteps = 0;
        mDistance = 0;
        mCalories = 0;
        mPedometerSettings.setSteps(mSteps);
        mPedometerSettings.setCalories(mCalories);
        mPedometerSettings.setDistance(mDistance);
        mPedometerSettings.setMinitues(minitues);
    }

    public void readTodayValues() {
        homePersenter.loadTodayStep();
//        long laststampTime = mPedometerSettings.getLast_Seen();
//        String lastDate = TimeUtil.timeStamp2YMDDate(laststampTime);
//        boolean today = lastDate.equals(TimeUtil.nowDate());
//        mSteps = mPedometerSettings.getInt("steps", 0);
//        lastUpLoadStep =mSteps;
//        mDistance = mPedometerSettings.getFloat("distance", 0);
//        mCalories = mPedometerSettings.getInt("calories", 0);
//        minitues = mPedometerSettings.getInt("minitues", 0);
//        if (today) {
//            mStepDisplayer.setSteps(mSteps);
//            mDistanceNotifier.setDistance(mDistance);
//            mCaloriesNotifier.setCalories(mCalories);
//        } else {
//            resetValues();
//        }

    }


    long lastTimeStamp = 0;
    int lastUpLoadStep = 0;  //上次提交时的步数

    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value) {

            passValue();
            mSteps = value;
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 10000);
        }

        public void passValue() {
            if (mCallback != null) {
                //      mCallback.stepsChanged(mSteps);
                //       mCallback.timeChanged(minitues);
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (homePersenter.getStep() - lastUpLoadStep > 10) {
                    lastUpLoadStep = homePersenter.getStep();
                    homePersenter.upLoadTodayData();
                    //   upLoadTodayData();
                }
                mHandler.removeMessages(1);

            }
        }
    };

    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value) {
            mDistance = value;
            passValue();
        }

        public void passValue() {
            if (mCallback != null) {
                //        mCallback.distanceChanged(mDistance);
            }
        }
    };

    private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        public void valueChanged(int value) {
            mCalories = value;
            passValue();
        }

        public void passValue() {
            if (mCallback != null) {
                //      mCallback.caloriesChanged(mCalories);
            }
        }
    };

//    private void showNotification() {
//        CharSequence text = getText(R.string.app_name);
//        Notification notification = new Notification(R.drawable.ic_notification, null,
//                System.currentTimeMillis());
//        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
//        Intent pedometerIntent = new Intent();
//        pedometerIntent.setComponent(new ComponentName(this, Pedometer.class));
//        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                pedometerIntent, 0);
//        notification.setLatestEventInfo(this, text,
//                getText(R.string.notification_subtitle), contentIntent);
//
//        mNM.notify(R.string.app_name, notification);
//    }


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
                if (mPedometerSettings.wakeAggressively()) {
                    wakeLock.release();
                    acquireWakeLock();
                }
            }
        }
    };

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        if (mPedometerSettings.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        } else if (mPedometerSettings.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
        } else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }

    private long oneDay = 86400000;
    private long timeGap = 1000;

    private class TimeCount extends CountDownTimer {
        int second = 0;

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            second++;

            if (second % 60 == 0) {
                if (System.currentTimeMillis() - lastTimeStamp > 55 * 1000)
                    return;
                minitues++;     // 一分钟 通知更新
                //       mCallback.timeChanged(minitues);
                second = 0;
            }
        }


    }

}


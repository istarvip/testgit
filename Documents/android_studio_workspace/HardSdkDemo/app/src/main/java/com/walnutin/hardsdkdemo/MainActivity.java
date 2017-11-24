package com.walnutin.hardsdkdemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardSdkCallback;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Clock;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Version;
import com.walnutin.hardsdkdemo.ProductNeed.manager.HeartRateStatisticManage;
import com.walnutin.hardsdkdemo.ProductNeed.manager.SleepStatisticManage;
import com.walnutin.hardsdkdemo.ProductNeed.manager.StepStatisticManage;
import com.walnutin.hardsdkdemo.utils.DateUtils;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.TimeUtil;
import com.walnutin.hardsdkdemo.utils.WeekUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pub.devrel.easypermissions.EasyPermissions;

import static com.walnutin.hardsdkdemo.R.id.content_info;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IHardSdkCallback, EasyPermissions.PermissionCallbacks, View.OnLongClickListener {

    private Button searchBtn;
    private Button queryHeartBtn;
    private Button querySleepBtn;
    private Button queryStepBtn;
    private Button realTimeStepBtn;
    private Button startSyncBtn;
    private String TAG = MainActivity.class.getSimpleName();
    private EditText contentInfo;
    private final int EDIT_CHANGED = 1;
    private boolean isTestingHeart;
    private Button realHeartBtn;
    private String beforeDate = DateUtils.getBeforeDate(new Date(), -1);
    private String currentDate;
    private Button sedentaryBtn;
    private Button findBand;
    private Button alarmBtn;
    private Button messagePushBtn;
    private Button callPushBtn;
    private Button resetBtn;
    private Button getAlarm2;
    private Button getNoticeFunction;
    private Button changeHV;
    private boolean isCalling;
    private Button updateBtn;
    private Button hourStepBtn;
    private Button getWypAlarmList;
    private StepStatisticManage mStepManager;
    private SleepStatisticManage mSleepManager;
    private HeartRateStatisticManage mHeartRateManager;
    //    private Handler mMyHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(msg.what == EDIT_CHANGED){
//                contentInfo.refreshDrawableState();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        requestPermission();
    }

    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(MainActivity.this, permissions)) {
            EasyPermissions.requestPermissions(MainActivity.this, "搜索蓝牙设备需要定位权限",
                    1, permissions);
        }
    }

    private void initView() {
        searchBtn = (Button) findViewById(R.id.search_device_btn);
        queryHeartBtn = (Button) findViewById(R.id.query_heart_btn);
        querySleepBtn = (Button) findViewById(R.id.query_sleep_btn);
        queryStepBtn = (Button) findViewById(R.id.query_step_btn);
        realTimeStepBtn = (Button) findViewById(R.id.realtime_step_btn);
        realHeartBtn = (Button) findViewById(R.id.realtime_heart_btn);
        startSyncBtn = (Button) findViewById(R.id.start_sync_btn);
        contentInfo = (EditText) findViewById(content_info);

        sedentaryBtn = (Button) findViewById(R.id.set_sedentary_btn);
        findBand = (Button) findViewById(R.id.find_band_btn);
        alarmBtn = (Button) findViewById(R.id.alarm_btn);
        messagePushBtn = (Button) findViewById(R.id.message_push_btn);
        callPushBtn = (Button) findViewById(R.id.call_push_btn);
        updateBtn = (Button) findViewById(R.id.update_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);
        hourStepBtn = (Button) findViewById(R.id.query_hour_step_btn);
        getAlarm2 = (Button) findViewById(R.id.getAlarm2);
        getNoticeFunction = (Button) findViewById(R.id.getNoticeFunction);
        changeHV = (Button) findViewById(R.id.changeHV);
    }


    private void initEvent() {
        HardSdk.getInstance().setHardSdkCallback(this);

        searchBtn.setOnClickListener(this);
        queryHeartBtn.setOnClickListener(this);
        querySleepBtn.setOnClickListener(this);
        queryStepBtn.setOnClickListener(this);
        realTimeStepBtn.setOnClickListener(this);
        startSyncBtn.setOnClickListener(this);
        realHeartBtn.setOnClickListener(this);
        changeHV.setOnClickListener(this);

        sedentaryBtn.setOnClickListener(this);
        findBand.setOnClickListener(this);
        alarmBtn.setOnClickListener(this);
        messagePushBtn.setOnClickListener(this);
        callPushBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        hourStepBtn.setOnClickListener(this);
        getAlarm2.setOnClickListener(this);
        getNoticeFunction.setOnClickListener(this);
        contentInfo.setOnLongClickListener(this);


        mStepManager = StepStatisticManage.getInstance(this);
        mSleepManager = SleepStatisticManage.getInstance(this);
        mHeartRateManager = HeartRateStatisticManage.getInstance(this);

        HardSdk.getInstance().setAccount("user1");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = simpleDateFormat.format(new Date());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.search_device_btn) {
            if (!HardSdk.getInstance().isDevConnected()) {
                contentInfo.append("请先连接手环后再进行此操作。\n");
                return;
            }
        }

        switch (view.getId()) {
            case R.id.search_device_btn:
                //跳转到新页面搜索和连接
                if (!HardSdk.getInstance().isDevConnected()) {
                    Intent intent = new Intent();
                    intent.setClass(this, SearchDeviceActivity.class);
                    startActivity(intent);
                } else {
                    HardSdk.getInstance().disconnect();
                }

                break;
            case R.id.realtime_step_btn:
                //发送查询实时数据命令，优创意会自动返回实时计步
                Log.d(TAG, "onClick: realtime_step_btn");
                contentInfo.append("非优创意手环，调用此方法则会返回实时计步值\n");
                HardSdk.getInstance().noticeRealTimeData();
                break;
            case R.id.start_sync_btn:
                //开始同步
                startSycn();

                break;
            case R.id.realtime_heart_btn:
                //开始测试心率
                Log.d(TAG, "onClick: isTestingHeart:" + isTestingHeart);
                if (!isTestingHeart) {
                    HardSdk.getInstance().startRateTest();
                    isTestingHeart = true;
                    contentInfo.append("爱都手环不支持实时心率测量 开始测量心率：\n");
                    realHeartBtn.setText("停止测试心率");
                } else {
                    HardSdk.getInstance().stopRateTest();
                    isTestingHeart = false;
                    realHeartBtn.setText("开始测试心率");
                }

                break;
            case R.id.query_step_btn:
                StepInfos stepInfos = mStepManager.getDayModeStepByDate(beforeDate);
                stepInfos.getStep();
                contentInfo.append(beforeDate + " 计步：" + stepInfos.getStep() + " 距离:" + stepInfos.getDistance() + " 卡路里：" + stepInfos.getCalories() + "\n");
                break;

            case R.id.query_hour_step_btn:
                Map<Integer, Integer> hourStepMap = mStepManager.getDayModeStepByDate(currentDate).getStepOneHourInfo();

                if (hourStepMap != null) {
                    Set<Integer> hours = hourStepMap.keySet();
                    contentInfo.append(currentDate + "\n");
                    for (Integer i : hours) {
                        contentInfo.append("小时：" + i / 60 + " 步数" + hourStepMap.get(i) + "\n");
                    }
                } else {
                    contentInfo.append("查询每小时步数返回为空" + "\n");
                }
                break;
            case R.id.query_heart_btn:
                contentInfo.append("查询心率:" + beforeDate + "\n");
                List<HeartRateModel> heartRateModels = mHeartRateManager.getDayModeHeartRateListByDate(beforeDate);
                if (heartRateModels != null && heartRateModels.size() > 0) {
                    for (HeartRateModel heartRateModel : heartRateModels) {
                        contentInfo.append(beforeDate + " 心率测试时刻:" + heartRateModel.getTestMomentTime() + " 值： " + heartRateModel.getCurrentRate() + "\n");
                        //其他数据自行从heartRateModel中获取
                    }
                } else {
                    contentInfo.append("没有" + beforeDate + " 心率历史记录" + "\n");
                }
                break;
            case R.id.query_sleep_btn:
                SleepModel sleepModel = mSleepManager.getDayModeSleepByDate(beforeDate);
                contentInfo.append(beforeDate + " 睡眠总时长：" + sleepModel.getTotalTime() + " 深度时长:" + sleepModel.getDeepTime() + " 浅睡时长:" + sleepModel.getLightTime() + "\n");
                break;


            case R.id.set_sedentary_btn:
                //设置久坐
                HardSdk.getInstance().setSedentaryRemindCommand(1, 60, "10:35", "21:00", false);  //开关，时间:分钟
                contentInfo.append("设置久坐提醒间隔为10分钟\n");
                break;
            case R.id.find_band_btn:
                //查找手环
                HardSdk.getInstance().findBand(2);//震动次数
                contentInfo.append("查找手环，部分厂商手环不支持\n");
                break;
            case R.id.alarm_btn:
                //设置闹钟
                int i = TimeUtil.nowHour();
                byte weekper = 127;
                Log.d(TAG, "onClick: weekper" + weekper);
                long timeMillis = (System.currentTimeMillis() + 1000 * 60);
                int hour = TimeUtil.hourFromTimeMillis(timeMillis);
                int minitues = TimeUtil.minituesFromTimeMillis(timeMillis) + 1;
                Log.d(TAG, "onClick: hour:" + hour + " minitues:" + minitues);
                //      HardSdk.getInstance().setAlarmClcok(2, weekper, hour, minitues, true); //闹钟从0开始

                List<Clock> clockList = new ArrayList<>();
                Clock clock = new Clock();
                clock.setEnable(true);
                clock.setTime(String.valueOf(hour + ":" + minitues));
                clock.setRepeat(weekper);
                clock.setEnable(true);
                clock.setSerial(0);
                clockList.add(clock);
                Clock clock1 = new Clock();
                clock1.setEnable(true);
                clock1.setTime(String.valueOf(hour + ":" + (minitues + 1)));
                clock1.setRepeat(weekper);
                clock1.setEnable(true);
                clock1.setSerial(1);
                clockList.add(clock1);

                Clock clock2 = new Clock();
                clock2.setEnable(true);
                clock2.setTime(String.valueOf(hour + ":" + (minitues + 2)));
                clock2.setRepeat(weekper);
                clock2.setEnable(true);
                clock2.setSerial(2);
                clockList.add(clock2);

                for (Clock clock4 : clockList) {
                    System.out.println(clock4.toString());
                }

                HardSdk.getInstance().setAlarmList(clockList);
                contentInfo.append("设置闹钟：" + hour + " 时" + minitues + "分\n");
                break;
            case R.id.message_push_btn:
                //消息推送
                HardSdk.getInstance().sendCallOrSmsInToBLE("18888888888", GlobalValue.TYPE_MESSAGE_SMS, "李三", "");
                //todo  推送内容怎么没有显示出来?
                contentInfo.append("推送短信消息\n");
                break;
            case R.id.call_push_btn:
                //来电提醒
                if (!isCalling) {
                    Log.d(TAG, "onClick: calling:" + 1);
                    HardSdk.getInstance().sendCallOrSmsInToBLE("18888888888", GlobalValue.TYPE_MESSAGE_PHONE, "琅琊王", "");
                    callPushBtn.setText("挂断电话");
                    isCalling = true;
                    contentInfo.append("推送来电\n");
                } else {
                    Log.d(TAG, "onClick: calling:" + 2);
                    //     HardSdk.getInstance().sendOffHookCommand();
                    HardSdk.getInstance().sendOffHookCommand();
                    //          HardSdk.getInstance().stopVibration();
                    callPushBtn.setText("来电通知");
                    isCalling = false;
                    contentInfo.append("停止推送来电\n");
                }
                break;
            case R.id.update_btn:
                //固件升级
                contentInfo.append("执行固件升级\n");
                HardSdk.getInstance().queryDeviceVesion();  //在 callback里面接收回调   todo 把金东方固件升级搬过来。
                break;
            case R.id.reset_btn:
                //恢复出厂设置
                HardSdk.getInstance().resetBracelet();
                contentInfo.append("执行恢复出厂设置\n");

                break;
            case content_info:
            case R.id.getAlarm2:
                HardSdk.getInstance().readClock();
                break;
            case R.id.getNoticeFunction:
                contentInfo.append("获取支持列表：wechat 位数 0 qq 1 facebookmsg 2 whatspp 3 twitter 4 skype 5 Line 6 linkedin 7  Instagram 8 snapchat 9 \n" +
                        HardSdk.getInstance().getSupportNoticeFunction());
                break;
            case R.id.changeHV:
                if (!HardSdk.getInstance().isSupportControlHVScreen()) {
                    contentInfo.append("不支持 横竖屏切换\n");
                } else {
                    if (ishv) {
                        HardSdk.getInstance().setControlHVScreen(GlobalValue.SCREEN_VERTICAL);
                        contentInfo.append("切换横屏\n");
                        ishv = false;
                    } else {
                        HardSdk.getInstance().setControlHVScreen(GlobalValue.SCREEN_HORIZONTAL);
                        contentInfo.append("切换竖屏\n");
                        ishv = true;
                    }
                }
                break;

//            sedentaryBtn = (Button) findViewById(R.id.set_sedentary_btn);
//            findBand = (Button) findViewById(R.id.find_band_btn);
//            alarmBtn = (Button) findViewById(R.id.alarm_btn);
//            messagePushBtn = (Button) findViewById(R.id.message_push_btn);
//            callPushBtn = (Button) findViewById(R.id.call_push_btn);
//            resetBtn = (Button) findViewById(R.id.reset_btn);
        }

    }


    boolean ishv = false;

    private void startSycn() {
        HardSdk.getInstance().syncAllDataFromBracelet(true);
    }

    @Override
    public void onCallbackResult(int flag, boolean state, Object obj) {
        if (flag == GlobalValue.CONNECTED_MSG) {
            Log.d(TAG, "onCallbackResult: 连接成功");
            Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show();
            HardSdk.getInstance().openFuncRemind(GlobalValue.TYPE_MESSAGE_WECHAT, true); //打开对应消息通知开关
            HardSdk.getInstance().openFuncRemind(GlobalValue.TYPE_MESSAGE_PHONE, true); //打开对应来电通知开关
            searchBtn.setText("断开连接");
        } else if (flag == GlobalValue.DISCONNECT_MSG) {
            Log.d(TAG, "onCallbackResult: 连接失败");
            Toast.makeText(this, "连接断开", Toast.LENGTH_LONG).show();
            searchBtn.setText("搜索");
        } else if (flag == GlobalValue.CONNECT_TIME_OUT_MSG) {
            Log.d(TAG, "onCallbackResult: 连接超时");
            Toast.makeText(this, "连接超时", Toast.LENGTH_LONG).show();
            searchBtn.setText("搜索");
        } else if (flag == GlobalValue.STEP_SYNC_START) {
            //     Log.d(TAG, "onCallbackResult: 开始同步");
            //   contentInfo.append("同步步数中...\n");
        } else if (flag == GlobalValue.STEP_FINISH) {
            Log.d(TAG, "onCallbackResult: 同步计步完成");
            contentInfo.append("同步计步完成\n");
        } else if (flag == GlobalValue.HEART_FINISH) {
            Log.d(TAG, "onCallbackResult: 同步心率完成");
            contentInfo.append("同步心率完成\n");
        } else if (flag == GlobalValue.SYNC_FINISH) {
            Log.d(TAG, "onCallbackResult: 同步完成");
            contentInfo.append("同步完成\n");
            Toast.makeText(this, "同步完成", Toast.LENGTH_LONG).show();
        } else if (flag == GlobalValue.Firmware_Version) {
            Version version = (Version) obj;
            Toast.makeText(this, "版本braceletVersion： " + version.braceletVersion + "  firmwareVersion: " + version.firmwareVersion + "  bluetoothVersion:" + version.bluetoothVersion, Toast.LENGTH_LONG).show();
        } else if (flag == GlobalValue.READ_ALARM_OK) {
            List<Clock> clockList = (List<Clock>) obj;

            if (clockList != null && clockList.size() > 0) {
                for (Clock clock : clockList) {
                    contentInfo.append("读取闹钟 \n" + clock.toString() + " 星期： " + WeekUtils.parseRepeat(clock.repeat, 1, GlobalValue.LANGUAGE_CHINESE));
                }
            }

        } else if (flag == GlobalValue.FIND_PHONE) {
            contentInfo.append("手机 找到了\n");
        }
    }


    @Override
    public void onStepChanged(int step, float distance, int calories, boolean finish_status) {
        Log.d(TAG, "onStepChanged: step:" + step);
        contentInfo.append("计步：" + step + " 距离:" + distance + " 卡路里：" + calories + "\n");

    }

    @Override
    public void onSleepChanged(int lightTime, int deepTime, int sleepAllTime, int[] sleepStatusArray, int[] timePointArray, int[] duraionTimeArray) {
        contentInfo.append("睡眠总时间:" + sleepAllTime + "分钟" + "\n");
    }

    @Override
    public void onHeartRateChanged(int rate, int status) {
        Log.d(TAG, "onHeartRateChanged: status:" + status);
        isTestingHeart = true;
        realHeartBtn.setText("停止测试心率");
        contentInfo.append("实时心率值：" + rate + "\n");
        if (status == GlobalValue.RATE_TEST_FINISH) {
            contentInfo.append("测量心率结束" + "\n");
            isTestingHeart = false;
            realHeartBtn.setText("开始测量心率");
        }
    }

    @Override
    public void onBloodPressureChanged(int hightPressure, int lowPressure, int status) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        HardSdk.getInstance().removeHardSdkCallback(this);
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.content_info) {
            contentInfo.clearComposingText();
        }
        return false;
    }
}

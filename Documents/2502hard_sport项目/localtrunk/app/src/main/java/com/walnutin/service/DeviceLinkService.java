package com.walnutin.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.walnutin.entity.SleepModel;
import com.walnutin.entity.StepModel;
import com.walnutin.eventbus.StepChangeNotify;
import com.walnutin.activity.MyApplication;
import com.walnutin.util.Config;
import com.walnutin.util.MySharedPf;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.BluetoothLeService;
import com.yc.peddemo.sdk.DataProcessing;
import com.yc.peddemo.sdk.ICallback;
import com.yc.peddemo.sdk.ICallbackStatus;
import com.yc.peddemo.sdk.RateChangeListener;
import com.yc.peddemo.sdk.SleepChangeListener;
import com.yc.peddemo.sdk.StepChangeListener;
import com.yc.peddemo.sdk.UTESQLOperate;
import com.yc.peddemo.sdk.WriteCommandToBLE;
import com.yc.peddemo.utils.CalendarUtils;
import com.yc.peddemo.utils.GlobalVariable;
import com.yc.pedometer.info.RateOneDayInfo;
import com.yc.pedometer.info.SleepTimeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 作者：MrJiang on 2016/5/30 14:23
 */
public class DeviceLinkService extends Service implements ICallback {
    private BLEServiceOperate mBLEServiceOperate;
    private BluetoothLeService mBluetoothLeService;
    private IBinder binder = new DeviceBinder();
    private WriteCommandToBLE mWriteCommand;
    private Context mContext;
    private int mSteps = 0;
    private float mDistance = 0f;
    private int mCalories = 0;
    private int mlastStepValue;
    private int stepDistance = 0;
    private int lastStepDistance = 0;
    private final int DISCONNECT_MSG = 18;
    private final int CONNECTED_MSG = 19;
    private final int UPDATE_STEP_UI_MSG = 0;

    DataProcessing mDataProcessing;
    MySharedPf mySharedPf;
    private String deviceAddr;
    private String deviceName;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mySharedPf = MySharedPf.getInstance(mContext);
        mBLEServiceOperate = BLEServiceOperate.getInstance(mContext);
        mBluetoothLeService = mBLEServiceOperate.getBleService();
        mWriteCommand = new WriteCommandToBLE(mContext);
        mBluetoothLeService.setICallback(this);
        mDataProcessing = DataProcessing.getInstance(mContext);
        mDataProcessing.setOnStepChangeListener(mOnStepChangeListener);
        mDataProcessing.setOnRateListener(mOnRateListener);
        mDataProcessing.setOnSleepChangeListener(mOnSlepChangeListener);

        //   MyApplication.instance().startStepService();
        mRegisterReceiver();
        //  registerBlueTooth();

        EventBus.getDefault().register(this);
        System.out.println("devservice: onCreate");
    }

    private void registerBlueTooth() {
        IntentFilter intentFilter = new IntentFilter();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "开始连接", Toast.LENGTH_SHORT).show();
        deviceName = mySharedPf.getString("device_name");
        deviceAddr = mySharedPf.getString("device_address");
        mBluetoothLeService.connect(deviceAddr);
        return super.onStartCommand(intent, flags, startId);
    }

    private void mRegisterReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(GlobalVariable.READ_BATTERY_ACTION);
        mFilter.addAction(GlobalVariable.READ_BLE_VERSION_ACTION);
        registerReceiver(mReceiver, mFilter);

        IntentFilter mFilter2 = new IntentFilter();
        mFilter2.addAction(Config.NOTICE_ACTION);
        mFilter2.addAction(Config.NOTICE_MSG_ACTION);
        mFilter2.addAction(Config.NOTICE_PHONE_ACTION);

        registerReceiver(mDeviceNoticeReceiver, mFilter2);

        IntentFilter mFilter3 = new IntentFilter();
        mFilter3.addAction(Config.CLOCK_SETTING);
        registerReceiver(clockReceiver, mFilter3);

        IntentFilter mFilter4 = new IntentFilter();
        mFilter4.addAction(Config.UNLOST);
        mFilter4.addAction(Config.FINDBRACELET);
        registerReceiver(otherDeviceSettingReceiver, mFilter4);


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(GlobalVariable.READ_BLE_VERSION_ACTION)) {
                String version = intent
                        .getStringExtra(GlobalVariable.INTENT_BLE_VERSION_EXTRA);
                //       Toast.makeText(getApplicationContext(), version, Toast.LENGTH_SHORT).show();
                //     show_result.setText("version=" + version);
            } else if (action.equals(GlobalVariable.READ_BATTERY_ACTION)) {
                int battery = intent.getIntExtra(
                        GlobalVariable.INTENT_BLE_BATTERY_EXTRA, -1);
                //      show_result.setText("battery=" + battery);

            }
        }
    };

    private BroadcastReceiver clockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mWriteCommand == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(Config.CLOCK_SETTING)) {
                int flag = intent.getIntExtra("flag", -1);
                byte weekPeroid = intent.getByteExtra("week", (byte) 0);
                int hour = intent.getIntExtra("hour", -1);
                int minitue = intent.getIntExtra("minitue", -1);
                boolean isOpen = intent.getBooleanExtra("isOpen", false);
                mWriteCommand.sendToSetAlarmCommand(flag, weekPeroid, hour, minitue, isOpen);
            }
        }
    };

    private BroadcastReceiver otherDeviceSettingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mWriteCommand == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(Config.FINDBRACELET)) {  // 查找手环
                mWriteCommand.findBand(5);
            } else if (action.equals(Config.UNLOST)) {    // 智能防丢

            }
        }
    };

    private BroadcastReceiver mDeviceNoticeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //     System.out.println("");
            if (mWriteCommand == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(Config.NOTICE_ACTION)) {
                String type = intent.getStringExtra("type");
                if (type.equals("msg")) {
                    mWriteCommand.sendSmsCommand(3);
                } else if (type.equals("WeChat")) {
                    mWriteCommand.sendQQWeChatTypeCommand(GlobalVariable.WeChatType);

                } else if (type.equals("qq")) {
                    mWriteCommand.sendQQWeChatTypeCommand(GlobalVariable.QQType);
                }
            } else if (action.equals(Config.NOTICE_MSG_ACTION)) {
                String contact = intent.getStringExtra("contacts");
                if (contact == null) {
                    return;
                }
                mWriteCommand.sendNumberToBLE(contact, GlobalVariable.SmsType);
            } else if (action.equals(Config.NOTICE_PHONE_ACTION)) {
                String state = intent.getStringExtra("state");
                if (state.equals("CALL_STATE_RINGING")) {
                    final String contact = intent.getStringExtra("contacts");
                    if (contact == null) {
                        return;
                    }
                    mWriteCommand.sendNumberToBLE(contact, GlobalVariable.PhoneType);
                } else if (state.equals("CALL_STATE_OFFHOOK")) {
                    mWriteCommand.sendOffHookCommand();
                }
            }
        }
    };


    private StepChangeListener mOnStepChangeListener = new StepChangeListener() {

        @Override
        public void onStepChange(int steps, float distance, int calories) {
            mSteps = steps;
            mDistance = distance;
            mCalories = calories;
            System.out.println("sync: 步数变化中");

            Intent StepsIntent = new Intent("com.hard.stepChangeIntent");
            StepsIntent.putExtra("step", steps);
            StepsIntent.putExtra("distance", distance);
            StepsIntent.putExtra("calories", calories);
            MyApplication._instance.sendBroadcast(StepsIntent);


        }

    };

    private SleepChangeListener mOnSlepChangeListener = new SleepChangeListener() {

        @Override
        public void onSleepChange() {
            System.out.println("sync: 睡眠变化中");

            UTESQLOperate mySQLOperate = new UTESQLOperate(mContext);
            SleepTimeInfo sleepTimeInfo = mySQLOperate.querySleepInfo(CalendarUtils.getCalendar(-1), CalendarUtils.getCalendar(0)); //CalendarUtils.getCalendar(0)

            if (sleepTimeInfo != null) {
                Intent StepsIntent = new Intent("com.hard.sleepChangeIntent");
                StepsIntent.putExtra("lightTime", sleepTimeInfo.getLightTime());
                StepsIntent.putExtra("deepTime", sleepTimeInfo.getDeepTime());
                StepsIntent.putExtra("sleepAllTime", sleepTimeInfo.getSleepTotalTime());
                StepsIntent.putExtra("sleepStatusArray", sleepTimeInfo.getSleepStatueArray());
                StepsIntent.putExtra("timePointArray", sleepTimeInfo.getTimePointArray());
                StepsIntent.putExtra("duraionTimeArray", sleepTimeInfo.getDurationTimeArray());

                //  sleepTimeInfo.get

                MyApplication._instance.sendBroadcast(StepsIntent);
            }
        }

    };

    private RateChangeListener mOnRateListener = new RateChangeListener() {

        @Override
        public void onRateChange(int rate, int status) {
            System.out.println("sync: 心率变化中:status: " + status + " --" + GlobalVariable.RATE_TEST_FINISH);
            Intent StepsIntent = new Intent("com.hard.rateRealChange");
            StepsIntent.putExtra("currentRate", rate);
            StepsIntent.putExtra("status", status);
            MyApplication._instance.sendBroadcast(StepsIntent);
        }

    };

    void sendRateToMainUI() {

        UTESQLOperate mySQLOperate = new UTESQLOperate(mContext);
        RateOneDayInfo mRateOneDayInfo = mySQLOperate.queryRateOneDayMainInfo(CalendarUtils.getCalendar(0));
//            List<RateOneDayInfo> mRateOneDayInfoList = mySQLOperate
//                    .queryRateOneDayDetailInfo(CalendarUtils.getCalendar(0));
        if (mRateOneDayInfo != null) {
            Intent StepsIntent = new Intent("com.hard.rateChangeIntent");
            StepsIntent.putExtra("lowRate", mRateOneDayInfo.getLowestRate());
            StepsIntent.putExtra("highRate", mRateOneDayInfo.getHighestRate());
            StepsIntent.putExtra("currentRate", mRateOneDayInfo.getCurrentRate());
            MyApplication._instance.sendBroadcast(StepsIntent);
        }
    }

    private void updateSteps(int steps) {
        stepDistance = steps - mlastStepValue;
        Log.d("upDateSteps", "stepDistance =" + stepDistance
                + ",lastStepDistance=" + lastStepDistance + ",steps =" + steps);
        if (stepDistance > 3 || stepDistance < 0) {
            if (steps <= 0) {
                steps = 0;
            } else {
                //    tv_steps.setText("" + steps);
            }
        }
        mlastStepValue = steps;
        lastStepDistance = stepDistance;

    }


    @Subscribe
    public void heartMeasure(StepChangeNotify.HeartMeasure sync) {
        if (sync.isMeasure) {
//mWriteCommand.sendst
            mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_START);
        } else {
            mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_STOP);
            sendRateToMainUI();
        }
        // mWriteCommand.syncAllSleepData();
        System.out.println("sync: 心率启动：" + sync.isMeasure);

        //   Toast.makeText(this, "同步更新 ", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void syncSleepDev(StepChangeNotify.SyncSleep sync) {
        mWriteCommand.syncAllSleepData();
        System.out.println("sync: 开始同步睡眠");

        //   Toast.makeText(this, "同步更新 ", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void syncStepDev(StepChangeNotify.SyncStep sync) {
        mWriteCommand.syncAllStepData();
        System.out.println("sync: 开始同步步数");

        //   Toast.makeText(this, "同步更新 ", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void syncHeartDev(StepChangeNotify.SyncHeart sync) {
        mWriteCommand.syncAllRateData();
        System.out.println("sync: 开始同步心率");

        //   Toast.makeText(this, "同步更新 ", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void OnResult(boolean result, int status) {
        Log.i("devstatus", "result=" + result + ",status=" + status);
        System.out.println("----status:" + status + "---result=" + result);
        if (status == ICallbackStatus.DISCONNECT_STATUS) {
            MyApplication.isDevConnected = false;
            mHandler.sendEmptyMessage(DISCONNECT_MSG);
            System.out.println("连接断开");
        } else if (status == ICallbackStatus.GET_BLE_VERSION_OK) {
            //        mWriteCommand.syncAllStepData();
        } else if (status == ICallbackStatus.CONNECTED_STATUS) {
            //      System.out.println("连接成功");

            mHandler.sendEmptyMessage(CONNECTED_MSG);
            MyApplication.isDevConnected = true;
            //        System.out.println("sync: 开始同步心率");
            mWriteCommand.syncAllStepData();
            //      Toast.makeText(getApplicationContext(), "开始同步步数", Toast.LENGTH_SHORT).show();
            //    mWriteCommand.syncAllStepData();
        } else if (status == ICallbackStatus.SYNC_TIME_OK) {// after set time
            mWriteCommand.sendToReadBLEVersion();
        } else if (status == ICallbackStatus.OFFLINE_SLEEP_SYNC_OK) {
            //      Toast.makeText(getApplicationContext(), "同步睡眠完成", Toast.LENGTH_SHORT).show();
            System.out.println("sync: 同步睡眠完成");

        } else if (status == ICallbackStatus.OFFLINE_STEP_SYNC_OK) {
            System.out.println("sync: 同步步数 ok");
            mWriteCommand.syncAllSleepData();

        } else if (status == ICallbackStatus.OFFLINE_RATE_SYNC_OK) {
            sendRateToMainUI();
            System.out.println("sync: 同步心率 ok");

            //   Toast.makeText(getApplicationContext(), "开始同步睡眠", Toast.LENGTH_SHORT).show();
            //     mWriteCommand.syncAllSleepData();
        } else if (status == ICallbackStatus.SENG_INCALL_NUMBER_OK) { //发送来电号码操作完成
            mWriteCommand.sendIncallCommand(10);
        } else if (status == ICallbackStatus.SEND_SMS_NUMBER_OK) { //发送短信号码操作完成
            mWriteCommand.sendSmsCommand(5);
        } else if (status == ICallbackStatus.SENG_OFFHOOK_OK) { ///挂断/接听电话操作完成
            mWriteCommand.sendStopVibrationCommand();
        } else if (status == ICallbackStatus.SENG_QQ_COMMAND_OK) { //////发送QQ指令操作完成
            mWriteCommand.sendSmsCommand(3);
        } else if (status == ICallbackStatus.SENG_WECHAT_COMMAND_OK) { /////发送微信指令操作完成
            mWriteCommand.sendSmsCommand(3);
        } else if (status == ICallbackStatus.OPERATION_FAILE) { /////操作失败
            Toast.makeText(getApplicationContext(), "操作失败", Toast.LENGTH_SHORT).show();
        }
    }


    public class DeviceBinder extends Binder {
        public DeviceLinkService getDeviceService() {
            return DeviceLinkService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_STOP);
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mReceiver);
        unregisterReceiver(mDeviceNoticeReceiver);
        unregisterReceiver(clockReceiver);
        unregisterReceiver(otherDeviceSettingReceiver);
        mBluetoothLeService.disconnect();
        mBLEServiceOperate.disConnect();
        EventBus.getDefault().unregister(this);
    }
    //   LinkDetailActivity mLinkDetailActivity= null;

    int rssi_value;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GlobalVariable.GET_RSSI_MSG:    //得到线损值
                    Bundle bundle = msg.getData();
                    rssi_value = bundle.getInt(GlobalVariable.EXTRA_RSSI);
                    System.out.println("rssi_value :"+rssi_value);
                    break;

                case DISCONNECT_MSG:
                    Toast.makeText(getApplicationContext(), "连接断开", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("ConnectedDevice");
                    intent.putExtra("deviceName", deviceName);
                    intent.putExtra("deviceAddr", deviceAddr);
                    intent.putExtra("connection_status", false);
                    MyApplication._instance.sendBroadcast(intent);
                    MyApplication.instance().stopDeviceService();
                    MyApplication.isDevConnected = false;

                    if (rssi_value < -95) {
                        Toast.makeText(getApplicationContext(), "手环丢失拉 ", Toast.LENGTH_SHORT).show();
                    }
                    //       mBluetoothLeService.readRssi();

                    mHandler.removeCallbacksAndMessages(null); // 取消Handle所有消息
                    break;
                case CONNECTED_MSG:
                    MyApplication.isDevConnected = true;
                    //      Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
                    //mWriteCommand.syncAllStepData();
                    intent = new Intent("ConnectedDevice");
                    intent.putExtra("deviceName", deviceName);
                    intent.putExtra("deviceAddr", deviceAddr);
                    intent.putExtra("connection_status", true);
                    MyApplication._instance.sendBroadcast(intent);


                    mBluetoothLeService.setRssiHandler(mHandler);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService.readRssi();
                            mHandler.postDelayed(this, 1000);
                        }
                    }, 1000);
                    break;
            }
        }
    };
}

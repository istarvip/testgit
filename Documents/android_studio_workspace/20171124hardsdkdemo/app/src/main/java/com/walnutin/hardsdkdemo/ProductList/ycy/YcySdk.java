package com.walnutin.hardsdkdemo.ProductList.ycy;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductList.ThirdBaseSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.BloodPressure;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Clock;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfo;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.R;
import com.walnutin.hardsdkdemo.utils.Config;
import com.walnutin.hardsdkdemo.utils.DateUtils;
import com.walnutin.hardsdkdemo.utils.DeviceSharedPf;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;
import com.walnutin.hardsdkdemo.utils.Utils;
import com.walnutin.hardsdkdemo.utils.WeekUtils;
import com.yc.pedometer.info.BPVOneDayInfo;
import com.yc.pedometer.info.RateOneDayInfo;
import com.yc.pedometer.info.SleepTimeInfo;
import com.yc.pedometer.info.StepOneDayAllInfo;
import com.yc.pedometer.info.StepOneHourInfo;
import com.yc.pedometer.sdk.BLEServiceOperate;
import com.yc.pedometer.sdk.BloodPressureChangeListener;
import com.yc.pedometer.sdk.BluetoothLeService;
import com.yc.pedometer.sdk.DataProcessing;
import com.yc.pedometer.sdk.ICallback;
import com.yc.pedometer.sdk.ICallbackStatus;
import com.yc.pedometer.sdk.OnServerCallbackListener;
import com.yc.pedometer.sdk.RateChangeListener;
import com.yc.pedometer.sdk.SleepChangeListener;
import com.yc.pedometer.sdk.StepChangeListener;
import com.yc.pedometer.sdk.UTESQLOperate;
import com.yc.pedometer.sdk.WriteCommandToBLE;
import com.yc.pedometer.update.Updates;
import com.yc.pedometer.utils.CalendarUtils;
import com.yc.pedometer.utils.GetFunctionList;
import com.yc.pedometer.utils.GlobalVariable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：MrJiang on 2017/1/12 11:18
 */
public class YcySdk extends ThirdBaseSdk implements ICallback, OnServerCallbackListener {
    private String TAG = "YcySdk";
    private String deviceAddr;
    private BLEServiceOperate mBLEServiceOperate;
    private BluetoothLeService mBluetoothLeService;
    private WriteCommandToBLE mWriteCommand;
    private Context mContext;
    private String contact;
    private boolean isPhone = false;
    private final int isSupportSendText = 1190;
    UTESQLOperate mySQLOperate;
    DataProcessing mDataProcessing;
    Updates mUpdates;
    //  Handler mDeviceLinkServiceHandler = new Handler();     //rssi  和  固件升级


    Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case isSupportSendText:
                    Log.i(TAG, "sendKeyReadIsHasContentPush isSupportSend");
                    mWriteCommand.sendKeyReadIsHasContentPush(); // 是否支持消息推送
                    break;
                case GlobalValue.IN_CALL:
                    sendNameToBLE(contact);
                    break;
                case GlobalValue.INCALL_OR_SMS_NAME:
                    if (isPhone) {
                        sendIncallCommand(20);
                    } else {
                        sendSmsCommand(5, "");
                    }
                    break;
                case GlobalValue.MSG_CALL:
                    sendNameToBLE(contact);
                    break;
                case GlobalValue.COMMON_MSG:
                    sendSmsCommand(3, "");
                    break;
                case GlobalVariable.GET_RSSI_MSG:
                    Bundle bundle = msg.getData();
                    mIDataCallBack.onResult(bundle.getInt(GlobalVariable.EXTRA_RSSI), true, GlobalValue.READ_RSSI_VALUE);
                    break;
                case 16:
                    myHandler.sendEmptyMessageDelayed(isSupportSendText, 3000);
                    mIDataCallBack.onResult(null, true, GlobalValue.SYNC_FINISH);
                    break;
                case GlobalValue.START_PROGRESS_MSG:

                    mIDataCallBack.onResult(msg.obj, true, GlobalValue.START_PROGRESS_MSG);

                case GlobalValue.UPDATE_BLE_PROGRESS_MSG:
                    int schedule = msg.arg1;
                    Log.i(TAG, "UPDATE_BLE_PROGRESS_MSG: " + msg.arg1 + " -- " + msg.obj);
                    mIDataCallBack.onResult(schedule, true, GlobalValue.UPDATE_BLE_PROGRESS_MSG);

                    break;
                case GlobalValue.DOWNLOAD_IMG_FAIL_MSG:
                    mIDataCallBack.onResult(null, true, GlobalValue.DOWNLOAD_IMG_FAIL_MSG);

                    break;
                case GlobalValue.DISMISS_UPDATE_BLE_DIALOG_MSG:
                    mIDataCallBack.onResult(msg.obj, true, GlobalValue.DISMISS_UPDATE_BLE_DIALOG_MSG);

                    break;
                case GlobalValue.SERVER_IS_BUSY_MSG:
                    mIDataCallBack.onResult(null, true, GlobalValue.SERVER_IS_BUSY_MSG);

                    break;
                case GlobalValue.SERVER_CALL_BACK_OK_MSG:
                    Log.i(TAG, "version-----------: " + localVersion);
                    int status = mUpdates.getBLEVersionStatus(localVersion);
                    Log.i(TAG, "status-----: " + status);
                    if (status == GlobalVariable.OLD_VERSION_STATUS) {
                        mIDataCallBack.onResult(null, true, GlobalValue.SERVER_HAVE_UPDATE);
                        return true;
                    } else if (status == GlobalVariable.NEWEST_VERSION_STATUS) {
                        Utils.showToast(mContext, mContext.getResources().getString(R.string.ble_is_newest));
                    } else if (status == GlobalVariable.FREQUENT_ACCESS_STATUS) {
                        Utils.showToast(mContext, mContext.getResources().getString(R.string.frequent_access_server));
                    }
                    //  mIDataCallBack.onResult(null, true, GlobalValue.SERVER_IS_BUSY_MSG);

                    break;

            }
            return false;
        }
    });

    private static YcySdk mInstance;

    private YcySdk() {
    }

    ;

    public static ICommonSDKIntf getInstance() {
        if (mInstance == null) {
            mInstance = new YcySdk();
        }
        return mInstance;
    }


    @Override
    public boolean initialize(Context context) {
        mContext = context;
        mBLEServiceOperate = BLEServiceOperate.getInstance(mContext);
        try {
            if (!mBLEServiceOperate.isSupportBle4_0()) {
                return false;
            }
            initService();
            mySQLOperate = UTESQLOperate.getInstance(mContext);
            mWriteCommand = new WriteCommandToBLE(mContext);
            mBluetoothLeService.setICallback(this);
            mDataProcessing = DataProcessing.getInstance(mContext);
            mDataProcessing.setOnStepChangeListener(mOnStepChangeListener);
            mDataProcessing.setOnRateListener(mOnRateListener);
            mDataProcessing.setOnSleepChangeListener(mOnSlepChangeListener);
            mDataProcessing.setOnBloodPressureListener(mOnBloodPressureListener);

            mBluetoothLeService.setRssiHandler(myHandler);

            mUpdates = Updates.getInstance(mContext);
            mUpdates.registerBroadcastReceiver();
            mUpdates.setHandler(myHandler);
            mUpdates.setOnServerCallbackListener(this);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void connect(String addr) {
        deviceAddr = addr;
        mBluetoothLeService.connect(deviceAddr);
    }

    @Override
    public void initService() {
        Log.i(TAG, "Ycy initService");

        mBluetoothLeService = mBLEServiceOperate.getBleService();
        Log.i(TAG, "Ycy initService + " + mBluetoothLeService);

    }

    @Override
    public void disconnect() {
        mBluetoothLeService.disconnect();
    }

    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen, String typ, String tip) {
        mWriteCommand.sendToSetAlarmCommand(flag + 1, weekPeroid, hour, minitue, isOpen, 5);
    }

    @Override
    public void setAlarmList(List clockList) {

        if (clockList != null && clockList.size() > 0) {
            this.clockList = clockList;
            clockSize = clockList.size();
            configHandler.post(runnable);
        }
    }

    private Handler configHandler = new Handler();

    List<Clock> clockList;
    int clockSize = 0;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {

                if (clockSize > 0) {
                    Clock clock = clockList.get(clockSize - 1);
                    packageSendInfo(clock.getTime(), "haha", clock.serial, clock.repeat, clock.isEnable, clock.type, clock.tip);
                    clockSize--;
                } else if (clockSize == 0) {
                    configHandler.removeCallbacks(null);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            configHandler.postDelayed(this, 99);
        }
    };

    void packageSendInfo(String time, String repeatValue, int flag, int cycle, boolean isOpen, String type, String tip) {
        if (time != null && time.length() > 0 && repeatValue != null && repeatValue.length() >= 2) {
            String[] times = time.split(":");
            int hour = Integer.parseInt(times[0]);
            int minitue = Integer.parseInt(times[1]);
            byte weekPeroid = 0;
            if (cycle == 0) {  // 每天的闹钟
                weekPeroid = Config.EVERYDAY;
            } else {
                weekPeroid = WeekUtils.getWeekByteByReapeat(cycle); // 得到星期的信息
            }
            if (flag == 1) {
                //       alarmManager.setEnableFirstAlarm(isOpen);
            } else if (flag == 2) {
                //        alarmManager.setEnableSecondAlarm(isOpen);
            } else if (flag == 3) {
                //        alarmManager.setEnableThirdAlarm(isOpen);
            }

            setAlarmClcok(flag, weekPeroid, hour, minitue, isOpen, type, tip);  //发送设置闹钟广播
        }
    }


    @Override
    public void readRssi() {
        mBluetoothLeService.readRssi();
    }

    @Override
    public void findBand(int num) {
        mWriteCommand.findBand(num);
    }

    @Override
    public void setHeightAndWeight(int height, int weight, int age, String sexfrom, String birthday) {
        MySharedPf.getInstance(mContext).setHeight(height);
        MySharedPf.getInstance(mContext).setWeight(weight);
        int screenOnTime = MySharedPf.getInstance(HardSdk.getInstance().getContext()).getScreenOnTime();
        mWriteCommand.sendStepLenAndWeightToBLE(height, weight, screenOnTime, MySharedPf.getInstance(mContext).getInt("target", 10000), false, true, 130);
    }


    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time, String startTime, String endTime, boolean isDisturb) {
        mWriteCommand.sendSedentaryRemindCommand(isOpen, time);
    }

    @Override
    public void resetBracelet() {
        mWriteCommand.deleteDevicesAllData();
    }


    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
//        public static int QQType = 1;
//        public static int WeChatType = 2;
//        public static int PhoneType = 2;
//        public static int SmsType = 3;

        Log.d(TAG, "sendQQWeChatTypeCommand:1 ");
        switch (type) {
            case GlobalValue.TYPE_MESSAGE_QQ:
                type = GlobalVariable.QQType;
                if (isSupportContentPush == true) {
                    mWriteCommand.sendTextToBle(body, GlobalVariable.TYPE_QQ);
                    return;
                }
                mWriteCommand.sendQQWeChatTypeCommand(type);
                break;
            case GlobalValue.TYPE_MESSAGE_WECHAT:
                type = GlobalVariable.WeChatType;
                if (isSupportContentPush == true) {
                    mWriteCommand.sendTextToBle(body, GlobalVariable.TYPE_WECHAT);
                    return;
                }
                mWriteCommand.sendQQWeChatTypeCommand(type);

                break;
            case GlobalValue.TYPE_MESSAGE_SMS:
                break;

            case GlobalValue.TYPE_MESSAGE_FACEBOOK:
                // mWriteCommand.sendQ
            case GlobalValue.TYPE_MESSAGE_WHATSAPP:

            case GlobalValue.TYPE_MESSAGE_OTHERSMS:
                if (isSupportContentPush == true) {
                    mWriteCommand.sendTextToBle(body, GlobalVariable.TYPE_SMS);
                    return;
                }
                mWriteCommand.sendSmsCommand(3);
                break;
        }
    }

//    @Override
//    public void sendCallOrSmsInToBLE(String number, int type) {
//        mWriteCommand.sendCallOrSmsInToBLE(number, type);
//    }

    @Override
    public void sendOffHookCommand() {
        mWriteCommand.sendOffHookCommand();
    }


    @Override
    public void openTakePhotoFunc(boolean isOpen) {
        if (isOpen == true) {
            mWriteCommand.openShakeMode();
        } else {
            mWriteCommand.closeShakeMode();
        }
    }


    @Override
    public void sendPalmingStatus(boolean isOpen) {

    }

    @Override
    public boolean isSupportOffPower() {
        return false;
    }

    @Override
    public boolean isSupportResetBand() {
        return false;
    }

    @Override
    public void getSportTenData() {

    }

    @Override
    public void setScreenOnTime(int screenOnTime) {

    }

    @Override
    public void changeMetric(boolean isMetric) {

    }

    @Override
    public void changeAutoHeartRate(boolean isAuto) {

    }

    @Override
    public void changePalming(boolean palming) {

    }

    @Override
    public void setTarget(int target, int type) {

    }

    @Override
    public void readClock() {

    }

    @Override
    public boolean isSupportTakePhoto() {
        return true;
    }

    @Override
    public boolean isSupportFindBand() {
        return true;
    }

    @Override
    public boolean isSupportLostRemind() {
        return false;
    }

    @Override
    public boolean isNeedAlarmListSetting() {
        return false;
    }

    @Override
    public boolean isSupportWristScreen() {
        return GetFunctionList.isSupportFunction(mContext, GlobalVariable.IS_SUPPORT_RAISE_HAND_BRIGHT);
    }

    @Override
    public String getSupportNoticeFunction() {
        return "1100000000";
    }

    @Override
    public boolean isSupportSetSedentarinessTime() {
        return false;
    }

    @Override
    public boolean isFindPhone() {
        return GetFunctionList.isSupportFunction(mContext, GlobalVariable.IS_SUPPORT_BAND_FIND_PHONE_FUNCTION);
    }

    @Override
    public boolean isSupportControlHVScreen() {
        return GetFunctionList.isSupportFunction(mContext, GlobalVariable.IS_SUPPORT_HOR_VER);

    }

    @Override
    public void setControlHVScreen(int type) {
        mWriteCommand.sendToControlHVScreen(type);
    }

    @Override
    public void startRateTest() {
        mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_START);

    }

    @Override
    public void stopRateTest() {
        mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_STOP);

    }

    @Override
    public void startBloodTest() {
        mWriteCommand.sendBloodPressureTestCommand(GlobalVariable.BLOOD_PRESSURE_TEST_START);

    }

    @Override
    public void stopBloodTest() {
        mWriteCommand.sendBloodPressureTestCommand(GlobalVariable.BLOOD_PRESSURE_TEST_STOP);

    }

    @Override
    public void syncAllStepData() {
        System.out.println("syncAllStepData");
        mWriteCommand.syncAllStepData();
        // mWriteCommand.syncAllSleepData();
    }

    @Override
    public void syncAllHeartRateData() {
        mWriteCommand.syncAllRateData();
        isSupportHeartValue = false;
        isSupportDownTimer.cancel();
        isSupportDownTimer.start();

    }

    @Override
    public void syncAllSleepData() {
        mWriteCommand.syncAllSleepData();
    }

    @Override
    public void stopVibration() {
        mWriteCommand.sendStopVibrationCommand();
    }


    private void sendNameToBLE(String name) {
        mWriteCommand.sendNameToBLE(name);
    }

    private void sendIncallCommand(int num) {
        mWriteCommand.sendIncallCommand(num);
    }

    @Override
    public List queryRateOneDayDetailInfo(String date) {
        return mySQLOperate.queryRateOneDayDetailInfo(date);
    }

    @Override
    public Map<Integer, Integer> queryOneHourStep(String date) {
        Map<Integer, Integer> maps = new LinkedHashMap();
        if (!TextUtils.isEmpty(date)) {
            if (date.contains("-")) {
                String year = date.split("-")[0];
                String month = date.split("-")[1];
                String day = date.split("-")[2];
                date = year + month + day;
            }
            StepOneDayAllInfo stepOneDayAllInfo = mySQLOperate.queryRunWalkInfo(date);
            List<StepOneHourInfo> oneHourInfos = stepOneDayAllInfo.getStepOneHourArrayInfo();
            for (StepOneHourInfo oneHourInfo : oneHourInfos) {
                if (oneHourInfo.getStep() > 0) {
                    maps.put(oneHourInfo.getTime(), oneHourInfo.getStep());
                }
            }
        }
        return maps;
    }

    @Override
    public StepInfo queryOneDayStepInfo(String date) {
        com.yc.pedometer.info.StepInfo ycyStepInfo = mySQLOperate.queryStepInfo(date);
        StepInfo stepInfo = new StepInfo();
        if (ycyStepInfo != null) {
            stepInfo.setCalories(ycyStepInfo.getCalories());
            stepInfo.setDate(ycyStepInfo.getDate());
            stepInfo.setDistance(ycyStepInfo.getDistance());
            stepInfo.setSportTime(ycyStepInfo.getSportTime());
            stepInfo.setStep(ycyStepInfo.getStep());
        }
        return stepInfo;
    }

    @Override
    public SleepModel querySleepInfo(String startDate, String endDate) {

        SleepTimeInfo sleepTimeInfo = mySQLOperate.querySleepInfo(endDate); //新版去掉第一个参数
        SleepModel sleepModel = null;
        if (sleepTimeInfo != null) {
            sleepModel = new SleepModel();
            sleepModel.account = HardSdk.getInstance().getAccount();
            sleepModel.duraionTimeArray = sleepTimeInfo.getDurationTimeArray();
            sleepModel.sleepStatusArray = sleepTimeInfo.getSleepStatueArray();
            sleepModel.timePointArray = sleepTimeInfo.getTimePointArray();
            sleepModel.deepTime = sleepTimeInfo.getDeepTime();
            sleepModel.lightTime = sleepTimeInfo.getLightTime();
            sleepModel.totalTime = getTotalTime(sleepModel.duraionTimeArray);
        }
        return sleepModel;
    }

    @Override
    public boolean isSupportHeartRate(String deviceName) {

        return isSupportHeartValue;
    }


    @Override
    public int getSupportAlarmNum() {
        return 3;
    }

    @Override
    public void noticeRealTimeData() {

    }

    @Override
    public void queryDeviceVesion() {


//        try {
//            Thread.sleep(30);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        mWriteCommand.sendToReadBLEVersion();

        Log.i(TAG, "queryDeviceVesion");
    }

    @Override
    public boolean isVersionAvailable(String version) {

        localVersion = version;
        mWriteCommand.queryDeviceFearture();

        Log.i(TAG, "version: " + version);
        int status = mUpdates.accessServerersionStatus(version);
        Log.i(TAG, "status: " + status);
        if (status == GlobalVariable.FREQUENT_ACCESS_STATUS) {
            Utils.showToast(mContext, mContext.getResources().getString(R.string.frequent_access_server));
        }

        return false;
    }

    @Override
    public void startUpdateBLE() {
        mUpdates.startUpdateBLE();
    }

    @Override
    public void cancelUpdateBle() {
        mUpdates.clearUpdateSetting();
    }

    @Override
    public void readBraceletConfig() {

    }

    @Override
    public void unBindUser() {

    }

    @Override
    public void setDeviceSwitch(String type, boolean isOpen) {

    }

    @Override
    public void writeCommand(String hexValue) {

    }

    @Override
    public void findBattery() {
        mWriteCommand.sendToReadBLEBattery();
    }

    @Override
    public void startAutoHeartTest(boolean isAuto) {
        mWriteCommand.sendKeySetTimingTestRate(isAuto, 2);
    }


    @Override
    public void syncNoticeConfig() {

    }

    public int getSupportAlarmNumber() {
        return 3;
    }

    @Override
    public void setUnLostRemind(boolean isOpen) {

    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int type, String contact, String content) {
        Log.d(TAG, "sendCallOrSmsInToBLE: ");
        this.contact = contact;
        // isSupportContentPush = false;
        Log.i(TAG, number + " : " + contact + " isSupportCon:" + isSupportContentPush);
        if (GlobalVariable.PhoneType == type) {
            isPhone = true;
            if (isSupportContentPush == true) {
                if (!contact.equals("")) {
                    mWriteCommand.sendTextToBle(contact, GlobalVariable.TYPE_PHONE);
                } else {
                    mWriteCommand.sendTextToBle(number, GlobalVariable.TYPE_PHONE);
                }
                return;
            }
            mWriteCommand.sendNumberToBLE(number, type);
        } else if (GlobalVariable.SmsType == type) {
            isPhone = false;
            if (isSupportContentPush == true) {
                if (!contact.equals("")) {
                    mContext.getSharedPreferences(GlobalVariable.SettingSP, 0).edit().putString(GlobalVariable.SMS_RECEIVED_NUMBER,
                            contact);//保存推送短信的号码,短信推送时，必须
                    mContext.getSharedPreferences(GlobalVariable.SettingSP, 0).edit().commit();
                } else {
                    mContext.getSharedPreferences(GlobalVariable.SettingSP, 0).edit().putString(GlobalVariable.SMS_RECEIVED_NUMBER,
                            number);//保存推送短信的号码,短信推送时，必须
                    mContext.getSharedPreferences(GlobalVariable.SettingSP, 0).edit().commit();
                }
                mWriteCommand.sendTextToBle(content, GlobalVariable.TYPE_SMS);

                return;
            }
            mWriteCommand.sendNumberToBLE(number, type);
        }
    }

    private void sendSmsCommand(int num, String content) {
        mWriteCommand.sendSmsCommand(num);
    }

    @Override
    public void openFuncRemind(int type, boolean isOpen) {

    }

    boolean isSupportBloodPressure = false;

    @Override
    public boolean isSupportBloodPressure(String deviceName) {
        return isSupportBloodPressure;
    }

    @Override
    public boolean isSupportUnLostRemind(String deviceName) {
        return false;
    }

    boolean isSupportContentPush = false;

    long lastSyncMoment;

    @Override
    public void OnResult(boolean result, int status) {
        //   System.out.println("--status：----"+ status+"   ");
        Log.i(TAG, "onResult: status: " + status + "  result: " + result);

        if (status == ICallbackStatus.DISCONNECT_STATUS) {
            Log.d(TAG, "onResult: 进入了DISCONNECT_STATUS");
            if (!HardSdk.getInstance().isDevConnected()) {
                return;
            }
            mIDataCallBack.onResult(null, true, GlobalValue.DISCONNECT_MSG);
        } else if (status == ICallbackStatus.GET_BLE_VERSION_OK) {
        } else if (status == ICallbackStatus.OFFLINE_SLEEP_SYNCING) {
            isSleepFinish = false;
        } else if (status == ICallbackStatus.OFFLINE_STEP_SYNCING) {
            mIDataCallBack.onResult(null, true, GlobalValue.STEP_SYNC_START);
        } else if (status == ICallbackStatus.CONNECTED_STATUS) {
            mIDataCallBack.onResult(null, true, GlobalValue.CONNECTED_MSG);
            isSupportContentPush = false;
        } else if (status == ICallbackStatus.SYNC_TIME_OK) {// after set time
        } else if (status == ICallbackStatus.OFFLINE_SLEEP_SYNC_OK) {
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSleepFinish == true) {
                        myHandler.sendEmptyMessageDelayed(isSupportSendText, 3000);
                        mIDataCallBack.onResult(null, true, GlobalValue.SYNC_FINISH);
                    }
                }
            }, 1000);

        } else if (status == ICallbackStatus.OFFLINE_STEP_SYNC_OK) {
            mIDataCallBack.onResult(null, true, GlobalValue.STEP_FINISH);

        } else if (status == ICallbackStatus.OFFLINE_RATE_SYNCING) {
            isSupportHeartValue = true;
        } else if (status == ICallbackStatus.OFFLINE_RATE_SYNC_OK) { // 心率同步完成
            Log.i(TAG, "支持心率");
            isSupportBloodPressure = false;
            mWriteCommand.syncAllBloodPressureData();  // 监测 同步血压
            isSupportBloodPressureDownTimer.cancel();
            isSupportBloodPressureDownTimer.start();

        } else if (status == ICallbackStatus.OFFLINE_BLOOD_PRESSURE_SYNCING) {
            isSupportBloodPressure = true;
            //       Log.i(TAG,"OFFLINE_BLOOD_PRESSURE_SYNCING");

        } else if (status == ICallbackStatus.OFFLINE_BLOOD_PRESSURE_SYNC_OK) {
            isSupportBloodPressure = true;
            Log.i(TAG, "支持血压");
            if (DeviceSharedPf.getInstance(mContext).getString("lastsyncSleepTime" +
                    HardSdk.getInstance().getDeviceAddr(), "2010").equals(TimeUtil.getCurrentDate())) { //是同一天
                Log.d(TAG, "onResult: OFFLINE_RATE_SYNC_OK 发送支持消息推送命令");
                myHandler.sendEmptyMessageDelayed(isSupportSendText, 3000);
            } else {
                mIDataCallBack.onResult(null, true, GlobalValue.HEART_FINISH);
            }

        } else if (status == ICallbackStatus.IS_SUPPOERT_PUSH_CONTENT) {      // 消息推送
            Log.d(TAG, "onResult: 支持消息推送");
            isSupportContentPush = true;
        } else if (status == ICallbackStatus.GET_SPORT_STATUS_OK && result == false) {      // 是睡眠状态
            if (HardSdk.getInstance().isSyncing() == false) {
                DeviceSharedPf.getInstance(mContext).setString("lastsyncSleepTime" + HardSdk.getInstance().getDeviceAddr(), "2010"); // 设置同步日期
            }
        } else if (status == ICallbackStatus.GET_BLE_VERSION_OK) {      // 得到蓝牙版本
            Log.i(TAG, "GET_BLE_VERSION_OK");

        } else if (status == ICallbackStatus.SEDENTARY_REMIND_CLOSE) { //久坐提醒关闭
            if (DeviceSharedPf.getInstance(mContext).getInt("isFirstSync", 0) == 0) {   // 关闭闹钟
                DeviceSharedPf.getInstance(mContext).setInt("isFirstSync", 1);
            }
        } else if (status == ICallbackStatus.SENG_INCALL_NUMBER_OK) { //发送来电号码操作完成
            myHandler.sendEmptyMessage(GlobalValue.IN_CALL);
        } else if (status == ICallbackStatus.SEND_INCALL_OR_SMS_NAME_OK) { //发送来电或短信用户名字操作完成
            myHandler.sendEmptyMessage(GlobalValue.INCALL_OR_SMS_NAME);
        } else if (status == ICallbackStatus.SEND_SMS_NUMBER_OK) { //发送短信号码操作完成
            myHandler.sendEmptyMessage(GlobalValue.MSG_CALL);
        } else if (status == ICallbackStatus.SENG_OFFHOOK_OK) { ///挂断/接听电话操作完成
            mIDataCallBack.onResult(null, true, GlobalValue.HANDLE_CALL);
        } else if (status == ICallbackStatus.SENG_QQ_COMMAND_OK) { //////发送QQ指令操作完成
            myHandler.sendEmptyMessage(GlobalValue.COMMON_MSG);
        } else if (status == ICallbackStatus.SENG_WECHAT_COMMAND_OK) { /////发送微信指令操作完成
            myHandler.sendEmptyMessage(GlobalValue.COMMON_MSG);
        } else if (status == ICallbackStatus.SEND_QQ_WHAT_SMS_CONTENT_OK) { /////发送QQ、微信、短信内容OK
            Log.d(TAG, "onResult: ICallbackStatus.SEND_QQ_WHAT_SMS_CONTENT_OK");
            if (isSupportContentPush == true && isPhone == true) {
                mWriteCommand.sendQQWeChatVibrationCommand(20);
            } else if (isSupportContentPush == true) {
                mWriteCommand.sendQQWeChatVibrationCommand(3);
            } else {
                myHandler.sendEmptyMessage(GlobalValue.COMMON_MSG);
            }
        } else if (status == ICallbackStatus.SEND_PHONE_NAME_NUMBER_OK) { /////
            myHandler.sendEmptyMessage(GlobalValue.COMMON_MSG);
        } else if (status == ICallbackStatus.OPERATION_FAILE) { /////操作失败
            mIDataCallBack.onResult(null, true, GlobalValue.OPERATION_FAILD);
        } else if (status == ICallbackStatus.DISCOVERY_DEVICE_SHAKE) {
            mIDataCallBack.onResult(null, true, GlobalValue.DISCOVERY_DEVICE_SHAKE);
        } else if (status == ICallbackStatus.PRESS_FIND_PHONE_BUTTON) {
            mIDataCallBack.onResult(null, true, GlobalValue.FIND_PHONE);

        } else if (status == ICallbackStatus.BLOOD_PRESSURE_TEST_STAT) {
            mIDataCallBack.onResult(null, true, GlobalValue.BLOOD_PRESSURE_TEST_STAT);
        } else if (status == ICallbackStatus.BLOOD_PRESSURE_TEST_TIME_OUT) {
            mIDataCallBack.onResult(null, true, GlobalValue.BLOOD_PRESSURE_TEST_TIME_OUT);
        } else if (status == ICallbackStatus.BLOOD_PRESSURE_TEST_ERROR) {
            mIDataCallBack.onResult(null, true, GlobalValue.BLOOD_PRESSURE_TEST_ERROR);
        }
    }

    boolean isSupportHeartValue = false;
    boolean isSupportBloodPressureValue = false;

    CountDownTimer isSupportDownTimer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (isSupportHeartValue == false) {
                mWriteCommand.syncAllBloodPressureData(); // 如果不支持心率 尝试同步血压
                isSupportBloodPressureDownTimer.cancel();
                isSupportBloodPressureDownTimer.start();
            }
        }
    };


    CountDownTimer isSupportBloodPressureDownTimer = new CountDownTimer(3000, 3000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            Log.i(TAG, "isSupportBloodPressure: " + isSupportBloodPressure);
            if (isSupportBloodPressure == false) {
                if (DeviceSharedPf.getInstance(mContext).getString("lastsyncSleepTime" + HardSdk.getInstance().getDeviceAddr(), "2010").equals(TimeUtil.getCurrentDate())) { //是同一天
                    myHandler.sendEmptyMessageDelayed(isSupportSendText, 3000);
                    Log.i(TAG, "onCallbackResult: 发送支持消息推送命令");

                } else {
                    mIDataCallBack.onResult(null, true, GlobalValue.HEART_FINISH);
                }
            }
        }
    };

    @Override
    public void OnDataResult(boolean b, int i, byte[] bytes) {

    }

    @Override
    public void onCharacteristicWriteCallback(int i) {

    }

    @Override
    public void onIbeaconWriteCallback(boolean b, int i, int i1, String s) {

    }

    @Override
    public void onQueryDialModeCallback(boolean b, int i, int i1, int i2) {

    }

    @Override
    public void onControlDialCallback(boolean b, int i, int i1) {

    }

    @Override
    public void onSportsTimeCallback(boolean b, String s, int i, int i1) {

    }

    private StepChangeListener mOnStepChangeListener = new StepChangeListener() {

        @Override
        public void onStepChange(StepOneDayAllInfo stepOneDayAllInfo) {
            Log.i(TAG, "onStepChange: " + stepOneDayAllInfo.getStep());
            mIRealDataSubject.stepChanged(stepOneDayAllInfo.getStep(),
                    stepOneDayAllInfo.getDistance(), stepOneDayAllInfo.getCalories(), false);

        }


    };
    boolean isSleepFinish = false;

    private SleepChangeListener mOnSlepChangeListener = new SleepChangeListener() {

        @Override
        public void onSleepChange() {
            Log.i(TAG, "v 睡眠变化中");
            isSleepFinish = true;

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SleepTimeInfo sleepTimeInfo = mySQLOperate.querySleepInfo(CalendarUtils.getCalendar(0)); //CalendarUtils.getCalendar(0)
                    Log.i(TAG, " sleepTImeInfo: " + sleepTimeInfo);
                    if (sleepTimeInfo != null && mIRealDataSubject != null) {
                        mIRealDataSubject.sleepChanged(sleepTimeInfo.getLightTime(), sleepTimeInfo.getDeepTime(), sleepTimeInfo.getSleepTotalTime(),
                                sleepTimeInfo.getSleepStatueArray(), sleepTimeInfo.getTimePointArray(), sleepTimeInfo.getDurationTimeArray());
                    }
                }
            }, 3000);

        }

    };

    private RateChangeListener mOnRateListener = new RateChangeListener() {

        @Override
        public void onRateChange(int rate, int status) {
            if (mIRealDataSubject != null)
                mIRealDataSubject.heartRateChanged(rate, status);
        }

    };


    private BloodPressureChangeListener mOnBloodPressureListener = new BloodPressureChangeListener() {
        @Override
        public void onBloodPressureChange(int hightPressure, int lowPressure,
                                          int status) {
            Log.i(TAG, " high:" + hightPressure + " low: " + lowPressure + " status: " + status);
            mIRealDataSubject.bloodPressureChanged(hightPressure, lowPressure, status);


        }
    };

    @Override
    public void syncBraceletDataToDb() {
        syncTodayHeartRate(); // 拉取心率数据

        syncBraceletData(); // 拉取
    }

    void syncBraceletData() {
        //  System.out.println("AsyncHistoryData: " + params[0]);
        //   System.out.println("AsyncHistoryData:start " + System.currentTimeMillis());
        String lastSyncToDbTime = SqlHelper.instance().getLastSyncStepDate(HardSdk.getInstance().getAccount());
        try {
            Log.i(TAG, "syncBraceletData");

            if (lastSyncToDbTime != null) { //lastSyncToDbTime != null
                int gapDay = DateUtils.daysBetween(lastSyncToDbTime, TimeUtil.getCurrentDate());
                String lastSyncDate = MySharedPf.getInstance(mContext).getString(HardSdk.getInstance().getAccount() + "_lastData_" + HardSdk.getInstance().getDeviceAddr(), "1997-01-01");
                int lastGap = DateUtils.daysBetween(lastSyncDate, TimeUtil.getCurrentDate()); // 上次同步的具体日期与今天的差值
                System.out.println("AsyncHistoryData gapDay: " + gapDay + "  time:" + lastSyncToDbTime);
                if (gapDay < 2 && lastGap < 1) {
                    syncBloodPressureData(0);
                    syncStepData(0);
                    syncSleepData(7);
                    return;
                }
                if (gapDay > 7) {
                    gapDay = 7;
                }
                syncStepData(gapDay);
                syncSleepData(gapDay);
                syncHeartData(gapDay);
                syncBloodPressureData(gapDay);

            } else {       // 同步最近7天的数据
                syncStepData(7);
                syncSleepData(7);
                syncHeartData(7);
                syncBloodPressureData(7);

            }
            MySharedPf.getInstance(mContext).setString(HardSdk.getInstance().getAccount() + "_lastData_" + HardSdk.getInstance().getDeviceAddr(), TimeUtil.getCurrentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //    System.out.println("AsyncHistoryData:end " + System.currentTimeMillis());
    }


    private void syncStepData(int gapDay) {
        List<StepInfos> stepInfosList = new ArrayList<>();

        for (int i = 0; i <= gapDay; i++) {
            StepInfo stepInfo = (StepInfo) this.queryOneDayStepInfo(CalendarUtils.getCalendar(-i));
            if (stepInfo != null) {
                StepInfos stepInfos = new StepInfos();
                stepInfos.setStep(stepInfo.getStep());
                stepInfos.setAccount(HardSdk.getInstance().getAccount());
                stepInfos.setDates(DateUtils.getBeforeDate(new Date(), -i));
                stepInfos.setUpLoad(0);
                stepInfos.setDistance(stepInfo.getDistance());
                stepInfos.setCalories(stepInfo.getCalories());
                Map<Integer, Integer> maps = this.queryOneHourStep(stepInfo.date);
                stepInfos.setStepOneHourInfo(maps);
                stepInfosList.add(0, stepInfos);
            }
        }
        SqlHelper.instance().syncBraceletStepData(stepInfosList);
    }

    public void setTimePointArray(SleepModel sleepModel, int[] timePointArray) {
        sleepModel.timePointArray = timePointArray;
        int initValue = timePointArray[0] - sleepModel.duraionTimeArray[0]; // 开始时刻
        for (int i = 1; i < timePointArray.length; i++) {
            int gapValue = timePointArray[i] - timePointArray[i - 1];
            gapValue = gapValue > 0 ? gapValue : (gapValue + 1440) % 1440;
            sleepModel.duraionTimeArray[i] = gapValue;  //重置 状态数组
        }

    }

    public int getTotalTime(int[] duraionTimeArray) {
        if (duraionTimeArray == null) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < duraionTimeArray.length; i++) {
            sum += duraionTimeArray[i];
        }
        return sum;
    }

    private void syncSleepData(int gapDay) {

        List<SleepModel> sleepModelList = new ArrayList<>();
        for (int i = 0; i <= gapDay; i++) {
            SleepModel sleepModel = this.querySleepInfo(CalendarUtils.getCalendar(-i - 1), CalendarUtils.getCalendar(-i));
            if (sleepModel != null) {
                setTimePointArray(sleepModel, sleepModel.timePointArray);
                sleepModel.date = DateUtils.getBeforeDate(new Date(), -i);    //往前推 i 天
                sleepModelList.add(0, sleepModel);
            }

        }
        SqlHelper.instance().syncBraceletSleepData(sleepModelList);
    }


    private void syncTodayHeartRate() {
        System.out.println("AsyncHistoryData syncTodayHeartRate  start:" + System.currentTimeMillis());
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        List<RateOneDayInfo> rateOneDayInfoList = this.queryRateOneDayDetailInfo(CalendarUtils.getCalendar(0));
        for (RateOneDayInfo rateOneDayInfo : rateOneDayInfoList) {
            HeartRateModel heartRateModel = new HeartRateModel();
            heartRateModel.account = HardSdk.getInstance().getAccount();
            heartRateModel.testMomentTime = TimeUtil.MinitueToDetailTime(0, rateOneDayInfo.getTime()); // 测试时间
            heartRateModel.currentRate = rateOneDayInfo.getRate();
            heartRateModelList.add(0, heartRateModel);
        }
        SqlHelper.instance().syncBraceletHeartData(heartRateModelList);
        //    System.out.println("AsyncHistoryData syncTodayHeartRate  end:" + System.currentTimeMillis());
    }

    public void syncHeartData(int gapDay) {
        //    System.out.println("AsyncHistoryData syncHeartData  start:" + System.currentTimeMillis());

        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        for (int i = 1; i <= gapDay; i++) {
            List<RateOneDayInfo> rateOneDayInfoList = this.queryRateOneDayDetailInfo(CalendarUtils.getCalendar(-i));
            for (RateOneDayInfo rateOneDayInfo : rateOneDayInfoList) {
                HeartRateModel heartRateModel = new HeartRateModel();
                heartRateModel.account = HardSdk.getInstance().getAccount();
                heartRateModel.testMomentTime = TimeUtil.MinitueToDetailTime(-i, rateOneDayInfo.getTime()); // 测试时间
                heartRateModel.currentRate = rateOneDayInfo.getRate();
                heartRateModelList.add(0, heartRateModel);
            }
        }
        SqlHelper.instance().syncBraceletHeartData(heartRateModelList);

        //   System.out.println("AsyncHistoryData syncHeartData  end:" + System.currentTimeMillis());
    }

    public void syncBloodPressureData(int gapDay) {
        //    System.out.println("AsyncHistoryData syncHeartData  start:" + System.currentTimeMillis());

        List<BloodPressure> heartRateModelList = new ArrayList<>();
        for (int i = 0; i <= gapDay; i++) {
            List<BPVOneDayInfo> bloodPressureOneDayInfo = mySQLOperate.queryBloodPressureOneDayInfo(CalendarUtils.getCalendar(-i));
            for (BPVOneDayInfo b : bloodPressureOneDayInfo) {
                BloodPressure bloodPressure = new BloodPressure();
                bloodPressure.account = HardSdk.getInstance().getAccount();
                bloodPressure.testMomentTime = TimeUtil.MinitueToDetailTime(-i, b.getBloodPressureTime()); // 测试时间
                bloodPressure.systolicPressure = b.getLowBloodPressure();
                bloodPressure.diastolicPressure = b.getHightBloodPressure();
                heartRateModelList.add(0, bloodPressure);
            }
        }
        SqlHelper.instance().syncBraceletBloodPressureData(heartRateModelList);

        //   System.out.println("AsyncHistoryData syncHeartData  end:" + System.currentTimeMillis());
    }

    String localVersion;

    @Override
    public void OnServerCallback(int i) {

        myHandler.sendEmptyMessage(GlobalValue.SERVER_CALL_BACK_OK_MSG);
    }
}


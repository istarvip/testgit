package com.walnutin.hardsdkdemo.ProductList.fitcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.htsmart.wristband.WristbandApplication;
import com.htsmart.wristband.bean.SyncRawData;
import com.htsmart.wristband.bean.TodayTotalData;
import com.htsmart.wristband.bean.WristbandAlarm;
import com.htsmart.wristband.bean.WristbandConfig;
import com.htsmart.wristband.bean.WristbandNotification;
import com.htsmart.wristband.bean.WristbandVersion;
import com.htsmart.wristband.bean.config.NotificationConfig;
import com.htsmart.wristband.bean.config.SedentaryConfig;
import com.htsmart.wristband.connector.ConnectorListener;
import com.htsmart.wristband.connector.IDeviceConnector;
import com.htsmart.wristband.performer.IDevicePerformer;
import com.htsmart.wristband.performer.PerformerListener;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductList.ThirdBaseSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.FitCloudUser;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by chenliu on 2017/2/20.
 */

public class FitCloudSdk extends ThirdBaseSdk implements ConnectorListener, PerformerListener {
    public static final String TAG = "FitCloudSdk";
    private IDeviceConnector mIDeviceConnector = WristbandApplication.getDeviceConnector();
    private IDevicePerformer mIDevicePerformer = WristbandApplication.getDevicePerformer();
    private FitCloudUser mUser;
    // Handler handler;
    private byte mAlarmWeekPeroid;

    private Runnable mRealHeartTimeoutTimer;
    private Handler mMyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private int mQQMessageType = -1;
    private int mWechatMessageType = -1;
    private int mSmsMessageType = -1;
    private int mPhoneMessageType = -1;
    private boolean mQQMessageIsOpen;
    private boolean mWechatMessageIsOpen;
    private boolean mSmsMessageIsOpen;
    private boolean mPhoneMessageIsOpen;
    private int mSedentaryIsOpen;
    private int mSedentaryTime;
    private Context mContext;

    private static FitCloudSdk mInstance;

    private FitCloudSdk() {
    }


    public static ICommonSDKIntf getInstance() {
        if (mInstance == null) {
            mInstance = new FitCloudSdk();
        }
        return mInstance;
    }


    @Override
    public boolean initialize(Context context) {
        mContext = context;
        mUser = new FitCloudUser();
        mUser.setId(10001);
        mUser.setWearLeft(true);
        try {
            mUser.setSex(MySharedPf.getInstance(context).getString("sex","男").equals("男"));
            mUser.setHeight(Integer.valueOf(MySharedPf.getInstance(context).getString("height", "172")));
            mUser.setWeight(Integer.valueOf(MySharedPf.getInstance(context).getString("weight", "60.0").split("\\.")[0]));
            mUser.setBirthday(new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault()).parse(MySharedPf.getInstance(context).getString("birth", "1990-01-01")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mIDeviceConnector.addConnectorListener(this);
        mIDevicePerformer.addPerformerListener(this);
        mRealHeartTimeoutTimer = new Runnable() {
            @Override
            public void run() {
                mIDevicePerformer.closeHealthyRealTimeData(IDevicePerformer.HEALTHY_TYPE_HEART_RATE);
            }
        };

        return true;
    }


    private boolean isUserBound() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean("user_bind" + mUser.getId(), false);
    }

    private void setUserBound(boolean bound) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.edit().putBoolean("user_bind" + mUser.getId(), bound).apply();
    }

    @Override
    public void connect(String addr) {
        if (isUserBound()) {
            mIDeviceConnector.connectWithLogin(addr, mUser);
        } else {
            mIDeviceConnector.connectWithBind(addr, mUser);

        }
    }

    @Override
    public void disconnect() {
        mIDeviceConnector.close();
    }

    @Override
    public void readRssi() {

    }

    @Override
    public void stopVibration() {
    }

    @Override
    public void findBand(int num) {
        mIDevicePerformer.cmd_findWristband();
    }

    @Override
    public void resetBracelet() {
    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {
        WristbandNotification notification = new WristbandNotification();
        if (!TextUtils.isEmpty(contact)) {
            notification.setName(contact);
        } else {
            notification.setName(number);
        }
        notification.setType(WristbandNotification.TYPE_TELEPHONE_COMING);
        notification.setContent(content);
        mIDevicePerformer.sendWristbandNotification(notification);
    }

    @Override
    public void sendOffHookCommand() {
        WristbandNotification notification = new WristbandNotification();
        notification.setType(WristbandNotification.TYPE_TELEPHONE_REJECT);
        mIDevicePerformer.sendWristbandNotification(notification);
    }

    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
        //     Log.d(TAG, "onResponseNotificationConfig: mMessageType:" + mMessageType);
        WristbandNotification notification = new WristbandNotification();
        notification.setContent(body);
        switch (type) {
            case GlobalValue.TYPE_MESSAGE_QQ:
                notification.setType(WristbandNotification.TYPE_QQ);
                break;
            case GlobalValue.TYPE_MESSAGE_WECHAT:
                notification.setType(WristbandNotification.TYPE_WECHAT);
                break;
            case GlobalValue.TYPE_MESSAGE_SMS:
                notification.setType(WristbandNotification.TYPE_SMS);
                break;
            case GlobalValue.TYPE_MESSAGE_FACEBOOK:
                notification.setType(WristbandNotification.TYPE_FACEBOOK);
                break;
            case GlobalValue.TYPE_MESSAGE_TWITTER:
                notification.setType(WristbandNotification.TYPE_TWITTER);
                break;
            case GlobalValue.TYPE_MESSAGE_INSTAGRAM:
                notification.setType(WristbandNotification.TYPE_INSTAGRAM);
                break;
            case GlobalValue.TYPE_MESSAGE_LINKEDIN:
                notification.setType(WristbandNotification.TYPE_LINKEDIN);
                break;
            case GlobalValue.TYPE_MESSAGE_WHATSAPP:
                notification.setType(WristbandNotification.TYPE_WHATSAPP);
                break;
            default:
                notification.setType(WristbandNotification.TYPE_OTHERS_APP);
                break;
        }
        mIDevicePerformer.sendWristbandNotification(notification);
    }

    @Override
    public void setUnLostRemind(boolean isOpen) {

    }

    List<WristbandAlarm> wristbandAlarmList = new ArrayList<>();


    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {
        Log.i(TAG, "setAlarmClcok: " + flag + " wek: " + weekPeroid);
        WristbandAlarm alarm = new WristbandAlarm();
        alarm.setEnable(isOpen);
        GregorianCalendar calendar = new GregorianCalendar();
        alarm.setAlarmId(flag - 1);
        alarm.setYear(calendar.get(Calendar.YEAR));
        alarm.setMonth(calendar.get(Calendar.MONTH));
        alarm.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        alarm.setHour(hour);
        alarm.setMinute(minitue);
        alarm.setRepeat(calcRepeatValueByWeekPeriod(weekPeroid));

        int i = -1;
        boolean isExist = false;
        for (WristbandAlarm wristbandAlarm : wristbandAlarmList) {
            i++;
            if (wristbandAlarm.getAlarmId() == flag - 1) {
                isExist = true;
                break;
            }
        }
        if (isExist == true) {
            wristbandAlarmList.remove(i);
        }
        wristbandAlarmList.add(alarm);
        mIDevicePerformer.cmd_requestAlarmList();
    }

    private int getTmpRepeatValue(boolean[] week) {
        int remind = (week[0] == false ? 0 : 1) * 1 // 周一
                + (week[1] == false ? 0 : 1) * 2 // 周二
                + (week[2] == false ? 0 : 1) * 4 // 周三
                + (week[3] == false ? 0 : 1) * 8 // 周四
                + (week[4] == false ? 0 : 1) * 16 // 周五
                + (week[5] == false ? 0 : 1) * 32 // 周六
                + (week[6] == false ? 0 : 1) * 64; // 周日
        return remind;
    }

    private int calcRepeatValueByWeekPeriod(byte weekPeroid) {
        boolean weeks[] = new boolean[7];
        for (int i = 0; i < 7; i++) {
            int isSet = weekPeroid & 1;
            if (isSet == 1) {
                if (i == 0) {
                    weeks[6] = true;
                } else {
                    weeks[i - 1] = true;
                }
            }
            weekPeroid = (byte) (weekPeroid >> 1);
        }
        int value = getTmpRepeatValue(weeks);
        Log.i(TAG, "calcRepeatValueByWeekPeriod:" + value);

        return value;
    }

    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time) {
        Log.d(TAG, "sendSedentaryRemindCommand: isOpen:" + isOpen);

        SedentaryConfig sedentaryConfig = new SedentaryConfig();
        sedentaryConfig.setEnable(isOpen == 1 ? true : false);
        sedentaryConfig.setStart(540);
        sedentaryConfig.setEnd(1080);
        this.mSedentaryIsOpen = isOpen;
        this.mSedentaryTime = time;
        mIDevicePerformer.cmd_setSedentaryConfig(sedentaryConfig);
        //  mIDevicePerformer.cmd_requestWristbandConfig();
    }

    @Override
    public void setHeightAndWeight(int height, int weight, int screenOnTime) {
//        this.mHeight = height;
//        this.mWeight = weight;
//        this.mScreenOnTime = screenOnTime;
//        mIDevicePerformer.cmd_requestWristbandConfig();
    }

    @Override
    public void startRateTest() {
        mIDevicePerformer.openHealthyRealTimeData(IDevicePerformer.HEALTHY_TYPE_HEART_RATE);
    }

    @Override
    public void stopRateTest() {
        mIDevicePerformer.closeHealthyRealTimeData(IDevicePerformer.HEALTHY_TYPE_HEART_RATE);
    }


    @Override
    public void syncAllStepData() {
        mIDevicePerformer.syncData(); // 同步数据
    }

    @Override
    public void syncAllHeartRateData() {

    }

    @Override
    public void syncAllSleepData() {

    }


    @Override
    public Map<Integer, Integer> queryOneHourStep(String date) {
        return null;
    }

    @Override
    public void syncBraceletDataToDb() {

    }

    // NotificationConfig notificationConfig = null;

    @Override
    public void openFuncRemind(int type, boolean isOpen) {
        Log.d(TAG, "openFuncRemind: type:" + type + " isOpen:" + isOpen + "  --");

        switch (type) {
            case GlobalValue.TYPE_MESSAGE_QQ:
                mQQMessageType = type;
                mQQMessageIsOpen = isOpen;
                break;
            case GlobalValue.TYPE_MESSAGE_WECHAT:
                mWechatMessageType = type;
                mWechatMessageIsOpen = isOpen;
                break;
            case GlobalValue.TYPE_MESSAGE_PHONE:
                mPhoneMessageType = type;
                mPhoneMessageIsOpen = isOpen;
                break;
            case GlobalValue.TYPE_MESSAGE_SMS:
                mSmsMessageType = type;
                mSmsMessageIsOpen = isOpen;
                break;
        }


        mIDevicePerformer.cmd_requestNotificationConfig();
    }

    @Override
    public boolean isSupportHeartRate(String deviceName) {
        return false;
    }

    @Override
    public boolean isSupportBloodPressure(String deviceName) {
        return false;
    }

    @Override
    public boolean isSupportUnLostRemind(String deviceName) {
        return false;
    }

    @Override
    public int getAlarmNum() {
        return 0;
    }

    @Override
    public void noticeRealTimeData() {

        mIDevicePerformer.syncData();

    }

    @Override
    public void queryDeviceVesion() {

    }

    @Override
    public boolean isVersionAvailable(String version) {
        return false;
    }

    @Override
    public void startUpdateBLE() {

    }

    @Override
    public void cancelUpdateBle() {

    }

    @Override
    public void readBraceletConfig() {

    }

    @Override
    public void onConnect(WristbandConfig wristbandConfig) {
        setUserBound(true);
        mIDataCallBack.onResult(null, true, GlobalValue.CONNECTED_MSG);

        //   handler.sendEmptyMessage(GlobalValue.CONNECTED_MSG);
        //    handler.sendEmptyMessage(GlobalValue.SYNC_FINISH);
    }

    @Override
    public void onDisconnect(boolean b, boolean b1) {
        Log.d(TAG, "onDisconnect: ");
        mIDataCallBack.onResult(null, true, GlobalValue.DISCONNECT_MSG);
    }

    @Override
    public void onConnectFailed(int i) {
        Log.d(TAG, "onDisconnect: ");
        mIDataCallBack.onResult(null, true, GlobalValue.DISCONNECT_MSG);
        //  handler.sendEmptyMessage(GlobalValue.DISCONNECT_MSG);
    }

    @Override
    public void onCommandSend(boolean b, int i) {

    }

    @Override
    public void onResponseEnterOTA(boolean b, int i) {

    }

    @Override
    public void onResponseAlarmList(List<WristbandAlarm> list) {
        Log.i(TAG, "onResponseAlarmList");
        if (wristbandAlarmList.size() == 3) {
            list.clear();
            list.addAll(wristbandAlarmList);
        } else {
            if (list.size() < 3) { //默认为三个闹钟
                list.clear();
                for (int i = 0; i < 3; i++) {
                    WristbandAlarm alarm = new WristbandAlarm();
                    GregorianCalendar calendar = new GregorianCalendar();
                    alarm.setYear(calendar.get(Calendar.YEAR));
                    alarm.setMonth(calendar.get(Calendar.MONTH));
                    alarm.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                    alarm.setHour(0);
                    alarm.setMinute(0);
                    alarm.setEnable(false);
                    list.add(alarm);
                }
            }
            for (WristbandAlarm alarm : wristbandAlarmList) {
                for (int i = 0; i < 3; i++) { // 3为 list size
                    if (alarm.getAlarmId() == list.get(i).getAlarmId()) {
                        list.set(i, alarm);
                    }
                }
            }

        }
        mIDevicePerformer.cmd_setAlarmList(list);

//        for (WristbandAlarm wristbandAlarm : list) {
//            Log.i("onResponIndex", wristbandAlarm.getAlarmId() + "");
//            Log.i("onResponEnable", wristbandAlarm.isEnable() + "");
//            Log.i("onResponMiniute", wristbandAlarm.getMinute() + "");
//            Log.i("onResponRepeat", wristbandAlarm.getRepeat() + "");
//        }

//
//        for (WristbandAlarm alarm : list) {
//            Log.i(TAG, "onResponseAlarmList: " + alarm.getAlarmId());
//            if (alarm.getAlarmId() == (mAlarmFlag - 1)) {
//                alarm.setEnable(mAlarmIsOpen);
//                GregorianCalendar calendar = new GregorianCalendar();
//                alarm.setYear(calendar.get(Calendar.YEAR));
//                alarm.setMonth(calendar.get(Calendar.MONTH));
//                alarm.setDay(calendar.get(Calendar.DAY_OF_MONTH));
//                alarm.setHour(mAlarmHour);
//                alarm.setMinute(mAlarmMinitue);
//
//            }
//        }


        //
    }

    /**
     * 根据设定的星期，返回下个日期
     *
     * @return 日期
     */
    private Calendar getNextDayFromWeek() {
        int todayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int diffValue = -1;
        for (int i = 1; i < 8; i++) {
            int isWeekDayOpen = mAlarmWeekPeroid & 1;
            if (isWeekDayOpen == 1) {
                if (todayOfWeek <= i) {
                    diffValue = i - todayOfWeek;
                } else {
                    diffValue = i + 7 - todayOfWeek;
                }
            }
            mAlarmWeekPeroid = (byte) (mAlarmWeekPeroid >> 1);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, diffValue);
        return calendar;
    }

    @Override
    public void onResponseNotificationConfig(NotificationConfig notificationConfig) {
        //    Log.d(TAG, "onResponseNotificationConfig: mMessageType:" + mMessageType);
        //   this.notificationConfig = notificationConfig;
        if (mQQMessageType != -1) {
            notificationConfig.setFlagEnable(NotificationConfig.FLAG_QQ, mQQMessageIsOpen);
            mQQMessageType = -1;
        }
        if (mWechatMessageType != -1) {
            notificationConfig.setFlagEnable(NotificationConfig.FLAG_WECHAT, mWechatMessageIsOpen);
            mWechatMessageType = -1;
        }
        if (mSmsMessageType != -1) {
            notificationConfig.setFlagEnable(NotificationConfig.FLAG_SMS, mSmsMessageIsOpen);
            mSmsMessageType = -1;
        }
        if (mPhoneMessageType != -1) {
            notificationConfig.setFlagEnable(NotificationConfig.FLAG_TELEPHONE, mPhoneMessageIsOpen);
            mPhoneMessageType = -1;
        }
        mIDevicePerformer.cmd_setNotificationConfig(notificationConfig);


    }

    @Override
    public void onResponseWristbandVersion(WristbandVersion wristbandVersion) {

    }

    @Override
    public void onResponseWristbandConfig(WristbandConfig wristbandConfig) {

//        SedentaryConfig sedentaryConfig = wristbandConfig.getSedentaryConfig();
//        Log.i(TAG, "start:" + sedentaryConfig.getStart());
//        Log.i(TAG, "end:" + sedentaryConfig.getEnd());
//        for (byte b : sedentaryConfig.getValues()) {
//            Log.i(TAG, "values:" + b);
//
//        }
    }

    @Override
    public void onResponseBattery(int i, int i1) {

    }

    @Override
    public void onFindPhone() {

    }

    @Override
    public void onUserUnBind(boolean b) {
    }

    @Override
    public void onOpenHealthyRealTimeData(int i, boolean b) {
    }

    @Override
    public void onCloseHealthyRealTimeData(int i) {
        Log.d(TAG, "onCloseHealthyRealTimeData: ");
        mIRealDataSubject.heartRateChanged(i, GlobalValue.RATE_TEST_FINISH);
    }

    @Override
    public void onResultHealthyRealTimeData(int i, int i1, int i2, int i3, int i4) {
        mMyHandler.removeCallbacks(mRealHeartTimeoutTimer);
        Log.d(TAG, "onResultHealthyRealTimeData: heartRate" + i + " oxygen:" + i1 + " diastolicPressure:" + i2 + " systolicPressure:" + i3 + " respiratoryRate:" + i4);
        mIRealDataSubject.heartRateChanged(i, GlobalValue.RATE_TEST_TESTING);
        mMyHandler.postDelayed(mRealHeartTimeoutTimer, 10000);
    }

    @Override
    public void onCameraTakePhoto() {

    }

    @Override
    public void onSyncDataStart(boolean b) {
        mIDataCallBack.onResult(null, true, GlobalValue.STEP_SYNC_START); // 同步开始
    }

    @Override
    public void onSyncDataEnd(boolean b) {

        mIDataCallBack.onResult(null, true, GlobalValue.SYNC_FINISH);

     //   dealSleepList(null);
    }

    @Override
    public void onSyncDataTodayTotalData(TodayTotalData todayTotalData) {

        Log.i(TAG, "TodayTotalData: getLightSleep:" + todayTotalData.getLightSleep() + " getDeepSleep:" + todayTotalData.getDeepSleep());
        Log.i(TAG, "TodayTotalData: getCalories:" + todayTotalData.getCalories() + " getDeepSleep:" + todayTotalData.getSteps());


        mIRealDataSubject.stepChanged(todayTotalData.getSteps(), todayTotalData.getDistance() / 1000f, todayTotalData.getCalories() / 1000, true);
        //      mIRealDataSubject.heartRateChanged(todayTotalData.getHeartRate(), GlobalValue.RATE_TEST_FINISH);
    }

    @Override
    public void onSyncDataResult(List<SyncRawData> list) {
        if (list.size() == 0)
            return;
        int dataType = list.get(0).getType();

        Log.e(TAG, "dataType:" + dataType + "  size:" + list.size());
        for (SyncRawData syncRawData : list) {
            Log.i(TAG, "getTimeStamp: " + syncRawData.getTimeStamp() + "");
            Log.i(TAG, "getValue: " + syncRawData.getValue() + "");
        }

        switch (dataType) {
            case SyncRawData.TYPE_STEPS:
                dealStepList(list);
                break;    //step
            case SyncRawData.TYPE_SLEEP:
                dealSleepList(list);
                break;       //sleep
            case SyncRawData.TYPE_HEART_RATE:
                dealHeartRateList(list);
                break;       //	TYPE_HEART_RATE
        }
    }


    void dealHeartRateList(List<SyncRawData> list) {
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        for (SyncRawData syncRawData : list) {
            if (syncRawData.getValue() > 0) {
                HeartRateModel heartRateModel = new HeartRateModel();
                heartRateModel.account = HardSdk.getInstance().getAccount();
                heartRateModel.testMomentTime = sdf.format(syncRawData.getTimeStamp() * 1000L);
                heartRateModel.currentRate = syncRawData.getValue();
                heartRateModelList.add(0, heartRateModel);
            }
        }
        SqlHelper.instance().syncBraceletHeartData(heartRateModelList);

        mIRealDataSubject.heartRateChanged(0, GlobalValue.RATE_TEST_FINISH);
    }


    void dealStepList(List<SyncRawData> list) {

        StepInfos stepInfos = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(), TimeUtil.getCurrentDate());

        Map<Integer, Integer> stepTodayOneHourInfoMap = FitCloudDb.getInstance().getDailyStepDataByDate(TimeUtil.getCurrentDate());
        if (stepTodayOneHourInfoMap == null) {
            stepTodayOneHourInfoMap = new LinkedHashMap<>();
        }
        String format = "yyyy-MM-dd HH";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time = "";
        String day = "";
        for (SyncRawData syncRawData : list) {
            if (syncRawData.getValue() > 0) {
                time = sdf.format(syncRawData.getTimeStamp() * 1000L);
                day = time.split(" ")[0];
                if (!day.equals(TimeUtil.getCurrentDate())) {
                    continue;
                }
                int hour = Integer.valueOf(time.split(" ")[1]);
                int minitue = 60 * hour;
                int rawValue = syncRawData.getValue();
                if (!stepTodayOneHourInfoMap.containsKey(minitue)) {
                    stepTodayOneHourInfoMap.put(minitue, rawValue);
                } else {
                    int mapStep = stepTodayOneHourInfoMap.get(minitue);
                    stepTodayOneHourInfoMap.put(minitue, rawValue + mapStep);
                }
            }
        }
        Gson gson = new Gson();

        FitCloudDb.getInstance().insertOrUpdateStep(TimeUtil.getCurrentDate(), gson.toJson(stepTodayOneHourInfoMap));

        stepInfos.setStepOneHourInfo(stepTodayOneHourInfoMap);
        SqlHelper.instance().insertOrUpdateTodayStep(stepInfos);
    }


    void dealSleepList(List<SyncRawData> list) {
//        int start = 1489584600+3600*24*4;
//        List<SyncRawData> syncRawDatas = new ArrayList<SyncRawData>();
//        Random rand = new Random();
//
//        for (int i = 0; i < 148; i++) {
//            SyncRawData syncRawData = new SyncRawData();
//            syncRawData.setTimeStamp(start);
//            syncRawData.setValue(rand.nextInt(3) + 1);
//            start += 300;
//            syncRawDatas.add(syncRawData);
//        }
//        list = syncRawDatas;    // 前部分为假数据

        int len = list.size();
        if (len == 0) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH) - 1, 18, 0, 0);
        long yesterDaySixTimeStamps = calendar.getTimeInMillis() / 1000;
        long todaySixTimeStamps = yesterDaySixTimeStamps + 3600 * 24;

        List<SyncRawData> validList = getValidList(list, yesterDaySixTimeStamps, todaySixTimeStamps); //经过掐头去尾的有效集合
        if (validList == null) {
            return;
        }

        List<DbSyncRawDataEntity> dbSyncRawDataList = FitCloudDb.getDailySleepDataByDate(TimeUtil.getCurrentDate());
        if (dbSyncRawDataList != null) {       //本地数据库有今天的值
            long dbtailTimeStamp = dbSyncRawDataList.get(dbSyncRawDataList.size() - 1).getTimeStamp(); // 取本地数据库最后一个数值时间戳
            long startTimeStamp = validList.get(0).getTimeStamp(); // 取有效集合中第一个时间戳
            int gap = (int) ((startTimeStamp - dbtailTimeStamp) / 300);
            for (int i = 1; i < gap; i++) {     // 补齐掐头去尾部分数据
                DbSyncRawDataEntity syncRawData = new DbSyncRawDataEntity();
                syncRawData.setValue(SyncRawData.SLEEP_STATUS_SOBER);   // 加入清醒时长
                syncRawData.setTimeStamp(dbtailTimeStamp + (i) * 300);   // 加入清醒时间戳
                dbSyncRawDataList.add(syncRawData);
            }

        } else {
            dbSyncRawDataList = new ArrayList<>();
        }

        for (SyncRawData syncRawData : validList) { // 将有效部分追加到本地数据库
            DbSyncRawDataEntity dbSyncRawDataEntity = new DbSyncRawDataEntity();
            dbSyncRawDataEntity.setTimeStamp(syncRawData.getTimeStamp());
            dbSyncRawDataEntity.setValue(syncRawData.getValue());
            dbSyncRawDataList.add(dbSyncRawDataEntity);
        }

        ArrayList<Integer> timePointArray = new ArrayList<Integer>();
        ArrayList<Integer> sleepStatusArray = new ArrayList<Integer>();
        ArrayList<Integer> duraionTimeArray = new ArrayList<Integer>();

        String format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        int deepSleep = 0;
        int lightSleep = 0;
        long wakeTime = 0;

        long startTimeStamp = dbSyncRawDataList.get(0).getTimeStamp();

        String date = TimeUtil.getCurrentDate(); // 今天日期
        String time = sdf.format(startTimeStamp * 1000L).split(" ")[1];

        int startMinitue = 0;
        startMinitue = Integer.valueOf(time.split(":")[0]) * 60 + Integer.valueOf(time.split(":")[1]);
        len = dbSyncRawDataList.size();

        for (int i = 0; i < len; i++) {
            DbSyncRawDataEntity syncRawData = dbSyncRawDataList.get(i);
            timePointArray.add((startMinitue + 5 * i + 5) % 1440); // 每一段结束时刻
            int status = syncRawData.getValue();
            switch (status) {
                case SyncRawData.SLEEP_STATUS_DEEP:
                    sleepStatusArray.add(0);
                    deepSleep += 5;
                    break;
                case SyncRawData.SLEEP_STATUS_SHALLOW:
                    sleepStatusArray.add(1);
                    lightSleep += 5;
                    break;
                case SyncRawData.SLEEP_STATUS_SOBER:
                    sleepStatusArray.add(2);
                    wakeTime += 5;
                    break;
            }
            duraionTimeArray.add(5);
        }
        Gson gson = new Gson();
        FitCloudDb.insertOrUpdateSleep(date, gson.toJson(dbSyncRawDataList)); //更新到本地数据库中

        int totalTime = deepSleep + lightSleep;

        correctSleepArray(date, sleepStatusArray, duraionTimeArray, timePointArray, 5, totalTime, lightSleep, deepSleep);
    }

    synchronized List<SyncRawData> getValidList(List<SyncRawData> syncRawDataList, long yesterDaySixTimeStamps, long todaySixTimeStamps) {
        int len = syncRawDataList.size();
        int startIndex = 0;
        int endIndex = len;
        for (int i = 0; i < len; i++) {
            if (syncRawDataList.get(i).getValue() == SyncRawData.SLEEP_STATUS_SOBER) {
                startIndex++;
            } else {
                break;
            }
        }

        for (int i = len - 1; i >= 0; i--) {
            if (syncRawDataList.get(i).getValue() == SyncRawData.SLEEP_STATUS_SOBER) {
                endIndex--;
            } else {
                break;
            }
        }

        if (startIndex >= endIndex) {     //一段无效数据
            return null;
        }

        List<SyncRawData> dealSyncDataList = syncRawDataList.subList(startIndex, endIndex); //掐头去尾处理后的有效数据
        len = dealSyncDataList.size();
        long startTimeStamp = dealSyncDataList.get(0).getTimeStamp();
        long endTimeStamp = dealSyncDataList.get(len - 1).getTimeStamp();

        if (endTimeStamp < yesterDaySixTimeStamps) {
            return null;
        } else if (startTimeStamp > todaySixTimeStamps) {
            return null;
        }

        if (startTimeStamp > yesterDaySixTimeStamps) {
            if (endTimeStamp > todaySixTimeStamps) { // 处理尾部 就好
                int tailGap = (int) ((endTimeStamp - todaySixTimeStamps) / 300);
                dealSyncDataList = dealSyncDataList.subList(0, dealSyncDataList.size() - tailGap);
            } else {
                return dealSyncDataList;
            }

        } else {
            int tailGap = dealSyncDataList.size();
            if (endTimeStamp > todaySixTimeStamps) {  // 处理尾部
                tailGap = (int) ((endTimeStamp - todaySixTimeStamps) / 300);
            }
            int gapHead = (int) ((yesterDaySixTimeStamps - startTimeStamp) / 300);
            dealSyncDataList = dealSyncDataList.subList(gapHead, dealSyncDataList.size() - tailGap);

        }

        return dealSyncDataList;
    }


    void correctSleepArray(String date, List<Integer> sleepStatusArray,
                           List<Integer> duraionTimeArray, List<Integer> timePointArray, int duraionTime, int total, int light, int deepTime) {
        ArrayList<Integer> newtimePointArray = new ArrayList<Integer>();
        ArrayList<Integer> newsleepStatusArray = new ArrayList<Integer>();
        ArrayList<Integer> newduraionTimeArray = new ArrayList<Integer>();
        int len = sleepStatusArray.size();
        for (int i = 0; i < len; ) {
            int status = sleepStatusArray.get(i);
            int index = 0;
            for (int j = i + 1; j < sleepStatusArray.size(); j++) {
                if (status != sleepStatusArray.get(j)) {
                    break;
                }
                index++;
            }
            newsleepStatusArray.add(status);
            newduraionTimeArray.add(duraionTime * (index + 1));
            newtimePointArray.add((timePointArray.get(i) + duraionTime * index) % 1440);
            i = i + index + 1;
        }
        len = newsleepStatusArray.size();
        int[] dtArray = new int[len];
        int[] statusArray = new int[len];
        int[] pointArray = new int[len];
        for (int i = 0; i < len; i++) {
            statusArray[i] = newsleepStatusArray.get(i);
            dtArray[i] = newduraionTimeArray.get(i);
            pointArray[i] = newtimePointArray.get(i);
        }
        if (total > 0) {
            List<SleepModel> sleepModelList = new ArrayList<>();
            SleepModel sleepTimeInfo = new SleepModel();
            sleepTimeInfo.account = HardSdk.getInstance().getAccount();
            sleepTimeInfo.duraionTimeArray = dtArray;
            sleepTimeInfo.sleepStatusArray = statusArray;
            sleepTimeInfo.timePointArray = pointArray;
            sleepTimeInfo.deepTime = deepTime;
            sleepTimeInfo.lightTime = light;
            sleepTimeInfo.totalTime = total;
            sleepTimeInfo.date = date;
            sleepModelList.add(sleepTimeInfo);
            SqlHelper.instance().syncBraceletSleepData(sleepModelList);
            mIRealDataSubject.sleepChanged(sleepTimeInfo.getLightTime(), sleepTimeInfo.getDeepTime(), sleepTimeInfo.getTotalTime(),
                    sleepTimeInfo.getSleepStatusArray(), sleepTimeInfo.getTimePointArray(), sleepTimeInfo.getDuraionTimeArray());

        }

    }

}

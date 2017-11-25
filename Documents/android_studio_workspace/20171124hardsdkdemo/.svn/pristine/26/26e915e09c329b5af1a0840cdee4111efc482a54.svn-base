package com.walnutin.hardsdkdemo.ProductList.ad;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.veryfit.multi.ble.AppBleListener;
import com.veryfit.multi.ble.ProtocalCallBack;
import com.veryfit.multi.config.Constants;
import com.veryfit.multi.entity.Alarm;
import com.veryfit.multi.entity.LongSit;
import com.veryfit.multi.entity.SportData;
import com.veryfit.multi.entity.SwitchDataAppBleEnd;
import com.veryfit.multi.entity.SwitchDataAppBlePause;
import com.veryfit.multi.entity.SwitchDataAppBleRestore;
import com.veryfit.multi.entity.SwitchDataAppEndReply;
import com.veryfit.multi.entity.SwitchDataAppIngReply;
import com.veryfit.multi.entity.SwitchDataAppPauseReply;
import com.veryfit.multi.entity.SwitchDataAppRestoreReply;
import com.veryfit.multi.entity.SwitchDataAppStartReply;
import com.veryfit.multi.entity.SwitchDataBleEnd;
import com.veryfit.multi.entity.SwitchDataBleIng;
import com.veryfit.multi.entity.SwitchDataBlePause;
import com.veryfit.multi.entity.SwitchDataBleRestore;
import com.veryfit.multi.entity.SwitchDataBleStart;
import com.veryfit.multi.nativedatabase.AlarmNotify;
import com.veryfit.multi.nativedatabase.AntilostInfos;
import com.veryfit.multi.nativedatabase.BasicInfos;
import com.veryfit.multi.nativedatabase.FunctionInfos;
import com.veryfit.multi.nativedatabase.GsensorParam;
import com.veryfit.multi.nativedatabase.HealthHeartRate;
import com.veryfit.multi.nativedatabase.HealthHeartRateAndItems;
import com.veryfit.multi.nativedatabase.HealthHeartRateItem;
import com.veryfit.multi.nativedatabase.HealthSport;
import com.veryfit.multi.nativedatabase.HealthSportAndItems;
import com.veryfit.multi.nativedatabase.HealthSportItem;
import com.veryfit.multi.nativedatabase.HrSensorParam;
import com.veryfit.multi.nativedatabase.NoticeOnOff;
import com.veryfit.multi.nativedatabase.RealTimeHealthData;
import com.veryfit.multi.nativedatabase.healthSleep;
import com.veryfit.multi.nativedatabase.healthSleepAndItems;
import com.veryfit.multi.nativedatabase.healthSleepItem;
import com.veryfit.multi.nativeprotocol.ProtocolEvt;
import com.veryfit.multi.nativeprotocol.ProtocolUtils;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductList.ThirdBaseSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.BraceletConfig;
import com.walnutin.hardsdkdemo.ProductNeed.entity.DeviceOtherSetting;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.NoticeInfo;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.utils.DateUtils;
import com.walnutin.hardsdkdemo.utils.DeviceSharedPf;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;
import com.walnutin.hardsdkdemo.utils.WeekUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：MrJiang on 2017/1/12 11:18
 */
public class AdSdk extends ThirdBaseSdk implements AppBleListener, ProtocalCallBack {
    //    private String name;
    private Context mContext;
    private static AdSdk mInstance;
    private final String TAG = "AdSdk";
    private final int MESSAGE_ERROR = 1;
    ProtocolUtils protocolUtils;
    BraceletConfig braceletConfig;

    private AdSdk(){
    };

    public static ICommonSDKIntf getInstance() {
        if(mInstance == null){
            mInstance = new AdSdk();
        }
        return mInstance;
    }

    @Override
    public boolean initialize(Context context) {
        mContext = context;


//        mIDeviceConnector.

        try {
            if (!isSupportBle4_0()) {
                return false;
            }
            ProtocolUtils.getInstance().setProtocalCallBack(this);
            ProtocolUtils.getInstance().setBleListener(this);
            isRateTesting = false;
            protocolUtils = ProtocolUtils.getInstance();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "initialize: ");

        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isSupportBle4_0() {

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager.getAdapter() == null) {
            return false;
        }
        return true;
    }

    String tmpAddr;

    @Override
    public void connect(String addr) {
        Log.d(TAG, "connect: ");
        tmpAddr = addr;
        String ar = DeviceSharedPf.getInstance(mContext).getString("Ad_" + addr, null);
        if (ar != null) {
            ProtocolUtils.getInstance().reConnect();
        } else {
            ProtocolUtils.getInstance().connect(addr);
        }
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "disconnect: ");
        ProtocolUtils.getInstance().setUnConnect();
        isRateTesting = false;
    }

    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {

        //    Log.d(TAG, "setAlarmClcok: weekPeroid:" + Integer.toBinaryString(weekPeroid));
        try {
            List<Alarm> alarms = ProtocolUtils.getInstance().getAllAlarms();

            if (alarms.size() < 7) {
                for (int i = 0; i < 7; i++) {
                    alarms.add(new Alarm());
                }
            }
            boolean weeks[] = new boolean[7];
            Alarm alarm = alarms.get(flag - 1);
            alarm.setAlarmHour(hour);
            alarm.setAlarmMinute(minitue);
            alarm.setOff_on(isOpen);
            alarm.setAlarmType(AlarmNotify.TYPE_ALARM_WAKEUP);
            for (int i = 0; i < 8; i++) {
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

            //     System.out.println("adSdk weekPeroid:" + weekPeroid);

            alarm.setWeek(weeks);
            //  Log.d(TAG, "setAlarmClcok: weeks" + weeks);
            ProtocolUtils.getInstance().setAlarm(alarms);
        } catch (Exception e) {
            e.printStackTrace();
            onSysEvt(0, ProtocolEvt.SET_CMD_ALARM.toIndex(), MESSAGE_ERROR, 0);
        } finally {

        }


    }


    @Override
    public void setHeightAndWeight(int height, int weight, int var3) {
        String birth = MySharedPf.getInstance(mContext).getString("birth");
        int year = 1995;
        int month = 1;
        int day = 1;
        String nickName = MySharedPf.getInstance(mContext).getString("nickname", "");
        String sex = MySharedPf.getInstance(mContext).getString("sex");
        int sexType = 0;
        if (sex != null) {
            if (sex.equals("女")) {
                sexType = 1;
            } else {
                sexType = 0;
            }
        }
        if (birth != null) {
            year = Integer.valueOf(birth.split("-")[0]);
            month = Integer.valueOf(birth.split("-")[1]);
            day = Integer.valueOf(birth.split("-")[2]);
        }
        ProtocolUtils.getInstance().setUserinfo(nickName, year, month, day, weight * 100, height, sexType);

        //  ProtocolUtils.getInstance().setSportgoal(var3);
    }


    @Override
    public void stopVibration() {
    }


    @Override
    public void readRssi() {
    }

    @Override
    public void findBand(int num) {

    }


    /**
     * 开启消息通知
     *
     * @param type
     * @param isOpen
     */
    @Override
    public void openFuncRemind(int type, boolean isOpen) {
        NoticeOnOff onOff = protocolUtils.getNotice();
        switch (type) {
            case GlobalValue.TYPE_MESSAGE_QQ:
                onOff.setQQonOff(isOpen);
                break;
            case GlobalValue.TYPE_MESSAGE_WECHAT:
                onOff.setWxonOff(isOpen);
                break;
            case GlobalValue.TYPE_MESSAGE_SMS:
                onOff.setMsgonOff(isOpen);
                break;
            case GlobalValue.TYPE_MESSAGE_PHONE:
                onOff.setCallonOff(isOpen);
                break;
        }
        protocolUtils.addNotice(onOff);

    }


    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time) {
        Log.d(TAG, "sendSedentaryRemindCommand: isOpen" + isOpen);
        Log.d(TAG, "sendSedentaryRemindCommand: time" + time);
        boolean mySwitch = false;
        if (isOpen == 1) {
            mySwitch = true;
        }
        ProtocolUtils.getInstance().setLongsit(9, 0, 18, 0, time, mySwitch, new boolean[]{true, true, true, true, true, true, true});
    }

    @Override
    public void resetBracelet() {
        ProtocolUtils.getInstance().reStartDevice();
    }

    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
        Log.d(TAG, "sendQQWeChatTypeCommand: type" + type + "  body:" + body);
        switch (type) {
            case GlobalValue.TYPE_MESSAGE_QQ:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_QQ, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_WECHAT:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_WX, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_WEIBO:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_WEIBO, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_FACEBOOK:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_FACEBOOK, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_TWITTER:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_TWITTER, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_WHATSAPP:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_WHATSAPP, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_INSTAGRAM:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_INSTAGRAM, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_LINKEDIN:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_LINKEDIN, null, null, body);
                break;
            case GlobalValue.TYPE_MESSAGE_OTHERSMS:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_MSG, null, null, body);
                break;
            default:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_MSG, null, null, body);
        }
    }


    @Override
    public void sendOffHookCommand() {
        Log.d(TAG, "sendOffHookCommand: ");
        ProtocolUtils.getInstance().stopCall();
    }

    boolean isRateTesting = false;

    Handler heartHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ProtocolUtils.getInstance().getLiveData();

            heartHandler.postDelayed(this, 1500);
        }
    };

    @Override
    public void startRateTest() {
        Log.d(TAG, "startRateTest: ");
        //爱都未发现开始监测和停止监测心率功能
//        ProtocolUtils.getInstance().setHeartRateMode(Constants.HEARTRATE_MODE_AUTOMATIC);
        isRateTesting = true;
        heartHandler.post(runnable);
    }

    @Override
    public void stopRateTest() {
        isRateTesting = false;
        heartHandler.removeCallbacks(null);
    }

    @Override
    public void syncAllStepData() {
        //      Log.d(TAG, "syncAllStepData: ");
        String ar = DeviceSharedPf.getInstance(mContext).getString("Ad_" + tmpAddr, null);
        if (ar != null) {
            mIDataCallBack.onResult(null,true,GlobalValue.STEP_FINISH); //代表同步配置完成
        } else {
            ProtocolUtils.getInstance().StartSyncConfigInfo();  // 同步配置
        }
        //   ProtocolUtils.getInstance().StartSyncConfigInfo();  // 同步配置
        //  ProtocolUtils.getInstance().StartSyncHealthData();  // 同步 健康数据
    }

    @Override
    public void syncAllHeartRateData() {
        ProtocolUtils.getInstance().StartSyncHealthData();  // 同步 健康数据
    }

    @Override
    public void syncAllSleepData() {

    }





    @Override
    public Map<Integer, Integer> queryOneHourStep(String date) {
        int year = Integer.valueOf(date.split("-")[0]);
        int month = Integer.valueOf(date.split("-")[1]) - 1;
        int day = Integer.valueOf(date.split("-")[2]);
        List<HealthSportItem> healthSportItemList = ProtocolUtils.getInstance().getHealthSportItem(new Date(year, month, day));//(月份从0开始例如8月传7)
        Map<Integer, Integer> map = new LinkedHashMap<>();
        int index = 0;
        int step = 0;
        for (HealthSportItem healthSportItem : healthSportItemList) {
            index++;
            step += healthSportItem.getStepCount();
            if (index % 4 == 0) {
                map.put(60 * ((index / 4) - 1), step);
                step = 0;
            }
        }

        return map;
    }

    public SleepModel querySleepInfo(String startDate, String endDate) {
        int year = Integer.valueOf(startDate.split("-")[0]);
        int month = Integer.valueOf(startDate.split("-")[1]) - 1;
        int day = Integer.valueOf(startDate.split("-")[2]);
        healthSleep healthSleep = ProtocolUtils.getInstance().getHealthSleep(new Date(year, month, day));//(月份从0开始例如8月传7)
        List<healthSleepItem> healthSleepItemList = ProtocolUtils.getInstance().getHealthSleepItem(new Date(year, month, day));
        if (healthSleepItemList != null && healthSleepItemList.size() > 0) {
            SleepModel sleepModel = new SleepModel();
            sleepModel.totalTime = healthSleep.getTotalSleepMinutes();
            sleepModel.account = HardSdk.getInstance().getAccount();
            sleepModel.deepTime = healthSleep.getDeepSleepMinutes();
            sleepModel.lightTime = healthSleep.getLightSleepMinutes();
            int wakeHour = healthSleep.getSleepEndedTimeH();
            int wakeMinitue = healthSleep.getSleepEndedTimeM();
            int wakeTime = wakeHour * 60 + wakeMinitue;
            int len = healthSleepItemList.size();
            int[] duraionTimeArray = new int[len];
            int[] sleepStatusArray = new int[len];
            int[] timePointArray = new int[len];
            for (int i = len - 1; i >= 0; i--) {
                healthSleepItem sleepItem = healthSleepItemList.get(i);
                duraionTimeArray[i] = sleepItem.getOffsetMinute();
                timePointArray[i] = wakeTime;
                if (timePointArray[i] < 0) {
                    timePointArray[i] += 1440;
                }
                wakeTime -= sleepItem.getOffsetMinute();
                if (sleepItem.getSleepStatus() == 3) {
                    sleepStatusArray[i] = 0;
                } else {
                    sleepStatusArray[i] = sleepItem.getSleepStatus() == 2 ? 1 : 2;
                }
            }
            sleepModel.duraionTimeArray = duraionTimeArray;
            sleepModel.sleepStatusArray = sleepStatusArray;
            sleepModel.timePointArray = timePointArray;
            return sleepModel;
        }
        return null;
    }


    @Override
    public boolean isSupportHeartRate(String deviceName) {
        return true;
    }


    @Override
    public int getAlarmNum() {
        return 3;
    }

    @Override
    public void noticeRealTimeData() {
        ProtocolUtils.getInstance().getLiveData();
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

    NoticeInfo noticeInfo;
    DeviceOtherSetting deviceOtherSetting;
    com.walnutin.hardsdkdemo.ProductNeed.entity.Alarm alarm;

    @Override
    public void readBraceletConfig() { // 首先读取 防丢配置
        braceletConfig = new BraceletConfig();
        noticeInfo = braceletConfig.getNoticeInfo();
        deviceOtherSetting = braceletConfig.getDeviceOtherSetting();
        alarm = braceletConfig.getAlarm();

        readUnLostConfig();
        readLongSitConfig();
        readNoticeConfig();
        readAlarmInfoConfig();

//        Message msg = mHandler.obtainMessage();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("braceletConfig", braceletConfig);
//        msg.setData(bundle);
//        msg.what = GlobalValue.READ_CONFIG_OK;
//        mHandler.sendMessage(msg);

        mIDataCallBack.onResult(braceletConfig,true,GlobalValue.READ_CONFIG_OK);

    }

    private void readNoticeConfig() {
        NoticeOnOff onOff = ProtocolUtils.getInstance().getNotice();
        boolean CallonOff = onOff.getCallonOff();
        boolean MsgonOff = onOff.getMsgonOff();
        boolean WxonOff = onOff.getWxonOff();
        boolean QQonOff = onOff.getQQonOff();
        noticeInfo.isEnablePhone = CallonOff;
        noticeInfo.isEnableMsg = MsgonOff;
        noticeInfo.isEnableWeChat = WxonOff;
        noticeInfo.isEnableQQ = QQonOff;
    }

    private void readAlarmInfoConfig() {
        List<Alarm> alarmList = protocolUtils.getAllAlarms();
        if (alarmList.size() > 0) {
            for (int i = 0; i < 3; i++) { // 暂只支持3个闹钟
                Alarm am = alarmList.get(i);
                int hour = am.alarmHour;
                int minitue = am.alarmMinute;
                if (i == 0) {
                    if (am.isOff_on() == true) {
                        alarm.setEnableFirstAlarm(true);
                        alarm.setFirstAlarm(TimeUtil.formatTwoString(hour) + ":" + TimeUtil.formatTwoString(minitue));
                        alarm.setFirstRepeat(getAlarmRepeatValue(am.getWeek()));
                        alarm.setFormatFirstAlarm(WeekUtils.parseRepeat(getTmpRepeatValue(am.week), 0,GlobalValue.LANGUAGE_CHINESE));
                    }
                } else if (i == 1) {
                    if (am.isOff_on() == true) {
                        alarm.setEnableSecondAlarm(am.isOff_on());
                        alarm.setSecondAlarm(TimeUtil.formatTwoString(hour) + ":" + TimeUtil.formatTwoString(minitue));
                        alarm.setSecondtRepeat(getAlarmRepeatValue(am.getWeek()));
                        alarm.setFormatSecondAlarm(WeekUtils.parseRepeat(getTmpRepeatValue(am.week), 0,GlobalValue.LANGUAGE_CHINESE));
                    }
                } else if (i == 2) {
                    if (am.isOff_on() == true) {
                        alarm.setEnableThirdAlarm(am.isOff_on());
                        alarm.setThirdAlarm(TimeUtil.formatTwoString(hour) + ":" + TimeUtil.formatTwoString(minitue));
                        alarm.setThirdRepeat(getAlarmRepeatValue(am.getWeek()));
                        alarm.setFormatThirdAlarm(WeekUtils.parseRepeat(getTmpRepeatValue(am.week), 0,GlobalValue.LANGUAGE_CHINESE));
                    }
                }
            }

        } else {

        }

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

    private int getAlarmRepeatValue(boolean[] week) {
        int remind = (week[0] == false ? 0 : 1) * 1 // 周一
                + (week[1] == false ? 0 : 1) * 2 // 周二
                + (week[2] == false ? 0 : 1) * 4 // 周三
                + (week[3] == false ? 0 : 1) * 8 // 周四
                + (week[4] == false ? 0 : 1) * 16 // 周五
                + (week[5] == false ? 0 : 1) * 32 // 周六
                + (week[6] == false ? 0 : 1) * 64; // 周日
        return remind;
    }

    private void readUnLostConfig() {
        AntilostInfos antilostInfos = protocolUtils.getAntilostInfos();
        int mode = antilostInfos.getMode();
        //   Message msg = mHandler.obtainMessage();
        //  Bundle bundle = new Bundle();
        if (mode == Constants.LOSE_MODE_NO_ANTI) {
            deviceOtherSetting.setUnLost(false);
        } else {
            deviceOtherSetting.setUnLost(true);
        }
        //msg.setData(bundle);
        //  mHandler.sendEmptyMessage(GlobalValue.READ_UNLOST_OK);
    }

    //
    private void readLongSitConfig() {
        //  ProtocolUtils.getInstance().setLongsit(9, 0, 18, 0, time, mySwitch, new boolean[]{true, true, true, true, true, true, true});

        LongSit longSit = ProtocolUtils.getInstance().getLongSit();
        int startHour = longSit.startHour;
        int startMinute = longSit.startMinute;
        int endHour = longSit.endHour;
        int endMinute = longSit.endMinute;
        int interval = longSit.interval; // 多长 提醒一次
        boolean isopen = longSit.isOnOff(); // 多长 提醒一次
        deviceOtherSetting.setLongSitRemind(isopen);
        deviceOtherSetting.setLongSitTime(interval);
//        Message msg = mHandler.obtainMessage();
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isOpen", isopen);
//        bundle.putInt("time", interval);
//        msg.setData(bundle);
//        mHandler.sendEmptyMessage(GlobalValue.READ_LONGSIT_OK);
    }

    @Override
    public void setUnLostRemind(boolean isOpen) {
        if (isOpen) {
            ProtocolUtils.getInstance().setFindPhone(true);
            ProtocolUtils.getInstance().setAntilost(Constants.LOSE_MODE_FAR_ANTI);
        } else {
            ProtocolUtils.getInstance().setFindPhone(false);
            ProtocolUtils.getInstance().setAntilost(Constants.LOSE_MODE_NO_ANTI);
        }
    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {
        Log.d(TAG, "sendCallOrSmsInToBLE:smsType " + smsType);
        Log.d(TAG, "sendCallOrSmsInToBLE:contact " + contact);
        Log.d(TAG, "sendCallOrSmsInToBLE:contactName " + number);
        switch (smsType) {
            case GlobalValue.TYPE_MESSAGE_PHONE:
                ProtocolUtils.getInstance().setCallEvt(contact, number);
                break;
            case GlobalValue.TYPE_MESSAGE_SMS:
                ProtocolUtils.getInstance().setSmsEvt(Constants.MSG_TYPE_MSG, contact, number, content);
                break;
        }
    }


    @Override
    public void syncBraceletDataToDb() {
        String lastSyncToDbTime = SqlHelper.instance().getLastSyncStepDate(HardSdk.getInstance().getAccount());
        try {
            if (lastSyncToDbTime != null) { //lastSyncToDbTime != null
                int gapDay = DateUtils.daysBetween(lastSyncToDbTime, TimeUtil.getCurrentDate());
                String lastSyncDate = MySharedPf.getInstance(mContext).getString(HardSdk.getInstance().getAccount() + "_lastData_" + HardSdk.getInstance().getDeviceAddr(), "1997-01-01");
                int lastGap = DateUtils.daysBetween(lastSyncDate, TimeUtil.getCurrentDate()); // 上次同步的具体日期与今天的差值
                System.out.println("AsyncHistoryData gapDay: " + gapDay + "  time:" + lastSyncToDbTime);
                if (gapDay < 2 && lastGap < 1) { //
                    return;
                }
                if (gapDay > 7) {
                    gapDay = 7;
                }
                syncStepData(gapDay);
                syncSleepData(gapDay);
                syncHeartData(gapDay);
            } else {       // 同步最近7天的数据
                syncStepData(7);
                syncSleepData(7);
                syncHeartData(7);
            }
            MySharedPf.getInstance(mContext).setString(HardSdk.getInstance().getAccount() + "_lastData_" + HardSdk.getInstance().getDeviceAddr(), TimeUtil.getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onBlueToothError(int i) {

    }

    @Override
    public void onBLEConnecting(String s) {

    }

    @Override
    public void onBLEConnected(BluetoothGatt bluetoothGatt) {
        Log.i(TAG, "onBLEConnected");
        if (HardSdk.getInstance().isDevConnected() == true) {
            return;
        }
        mIDataCallBack.onResult(null,true,GlobalValue.CONNECTED_MSG);
    }

    @Override
    public void onServiceDiscover(BluetoothGatt bluetoothGatt, int i) {
        String ar = DeviceSharedPf.getInstance(mContext).getString("Ad_" + tmpAddr, null);
        if (ar == null) {
            ProtocolUtils.getInstance().setBind();
        }
    }

    @Override
    public void onBLEDisConnected(String s) {
        Log.e(TAG, "onBLEDisConnected: ");
        heartHandler.removeCallbacks(null);
        mIDataCallBack.onResult(null,true,GlobalValue.DISCONNECT_MSG);
    }

    @Override
    public void onBLEConnectTimeOut() {
        Log.d(TAG, "onBLEConnectTimeOut: ");
        mIDataCallBack.onResult(null,true,GlobalValue.CONNECT_TIME_OUT_MSG);
    }


    @Override
    public void healthData(byte[] bytes) {

    }


    @Override
    public void onFuncTable(FunctionInfos functionInfos) {

    }

    @Override
    public void onSleepData(healthSleep healthSleep, healthSleepAndItems healthSleepAndItems) {

        if (mIRealDataSubject != null) {
            SleepModel sleepModel = querySleepInfo(TimeUtil.getCurrentDate(), null);
            if (sleepModel != null) {
                mIRealDataSubject.sleepChanged(sleepModel.getLightTime(), sleepModel.getDeepTime(), sleepModel.getTotalTime(),
                        sleepModel.getSleepStatusArray(), sleepModel.getTimePointArray(), sleepModel.getDuraionTimeArray());
            }
        }

    }

    @Override
    public void onHealthSport(HealthSport healthSport, HealthSportAndItems healthSportAndItems) {
        if (mIRealDataSubject != null && healthSport != null) {
            mIRealDataSubject.stepChanged(healthSport.getTotalStepCount(),
                    healthSport.getTotalDistance() / 1000f, healthSport.getTotalCalory(), true);

            List<HealthSportItem> healthSportItemList = healthSportAndItems.items;
            if (healthSportItemList != null && healthSportItemList.size() > 0) {
                StepInfos stepInfos = new StepInfos();
                stepInfos.setAccount(HardSdk.getInstance().getAccount());
                stepInfos.setDates(TimeUtil.getCurrentDate());
                stepInfos.setUpLoad(0);
                stepInfos.setStep(healthSport.getTotalStepCount());
                stepInfos.setDistance(healthSport.getTotalDistance() / 1000f);
                stepInfos.setCalories(healthSport.getTotalCalory());
                Map<Integer, Integer> map = new LinkedHashMap<>();
                int index = 0;
                int step = 0;
                for (HealthSportItem healthSportItem : healthSportItemList) {
                    index++;
                    step += healthSportItem.getStepCount();
                    if (index % 4 == 0) {
                        map.put(60 * ((index / 4) - 1), step);
                        step = 0;
                    }
                }
                stepInfos.setStepOneHourInfo(map);

                SqlHelper.instance().insertOrUpdateTodayStep(stepInfos);
            }
        }

    }


    @Override
    public void onHealthHeartRate(HealthHeartRate healthHeartRate, HealthHeartRateAndItems healthHeartRateAndItems) {
        //   Log.i(TAG,"onHealthHeartRate ,");
        if (healthHeartRateAndItems != null) {
            List<HealthHeartRateItem> healthHeartRateItemList = healthHeartRateAndItems.items;
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            //     Log.i(TAG,"onHealthHeartRate day: "+day+" healthHeartRate.day:"+healthHeartRate.day);
            if (healthHeartRateItemList != null && healthHeartRateItemList.size() > 0 && day == healthHeartRate.day) {
                List<HeartRateModel> heartRateModelList = new ArrayList<>();
                int minitue = 0;
                for (HealthHeartRateItem healthHeartRateItem : healthHeartRateItemList) {
                    minitue += healthHeartRateItem.getOffsetMinute();
                    HeartRateModel heartRateModel = new HeartRateModel();
                    heartRateModel.account = HardSdk.getInstance().getAccount();
                    heartRateModel.testMomentTime = TimeUtil.MinitueToDetailTime(0, minitue); // 测试时间
                    heartRateModel.currentRate = healthHeartRateItem.getHeartRaveValue();
                    heartRateModelList.add(0, heartRateModel);
                }
                //       Log.i(TAG,"heartRateModelList:"+heartRateModelList.size());
                SqlHelper.instance().syncBraceletHeartData(heartRateModelList);
            }
            mIRealDataSubject.heartRateChanged(0, GlobalValue.RATE_TEST_FINISH);
        }
    }

    @Override
    public void onLiveData(RealTimeHealthData realTimeHealthData) {
        Log.d(TAG, "onLiveData: realTimeHealthData"+realTimeHealthData);

        if (mIRealDataSubject != null && realTimeHealthData != null) {
            mIRealDataSubject.stepChanged(realTimeHealthData.totalStep, realTimeHealthData.getTotalDistances() / 1000f, realTimeHealthData.getTotalCalories(), true);
        }
//        if (isRateTesting == true) {
//            mIRealDataSubject.heartRateChanged(realTimeHealthData.heartRate, 0);
//        } else {
//            mIRealDataSubject.heartRateChanged(realTimeHealthData.heartRate, GlobalValue.RATE_TEST_FINISH);
//        }

    }


    /**
     * 1.自动同步时间
     * 2.时间成功后，同步配置
     * 3.配置成功后，通知上层时间同步完。上层开始调用同步计步。
     *
     * @param eventBase
     * @param eventType
     * @param error
     * @param value
     */
    @Override
    public void onSysEvt(int eventBase, int eventType, int error, int value) {

        Log.d(TAG, "onSysEvt: error:" + error + "   eventType:" + eventType);
        if (value > 0) {
            mIDataCallBack.onResult(null,true,GlobalValue.STEP_SYNC_START);
            Log.d(TAG, "onSysEvt: 正在同步数据:" + value + "%");
        }

        if (eventType == ProtocolEvt.BIND_CMD_REQUEST.toIndex() && error == ProtocolEvt.SUCCESS) {
            System.out.println("connect success");
            DeviceSharedPf.getInstance(mContext).setString("Ad_" + tmpAddr, tmpAddr); //该手环已经绑定过

            //绑定成功
        } else if (eventType == ProtocolEvt.SYNC_EVT_CONFIG_PROCESSING.toIndex() && error == ProtocolEvt.SUCCESS) {
            //同步配置中
            mIDataCallBack.onResult(null,true,GlobalValue.STEP_SYNC_START);
        } else if ((eventType == ProtocolEvt.SYNC_EVT_CONFIG_SYNC_COMPLETE.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 同步配置成功");
            mIDataCallBack.onResult(null,true,GlobalValue.STEP_FINISH);  //代表同步配置完成
        } else if ((eventType == ProtocolEvt.SET_CMD_TIME.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 设置时间成功");
        } else if ((eventType == ProtocolEvt.SYNC_EVT_HEALTH_PROCESSING.toIndex()) && error == ProtocolEvt.SUCCESS) {
            //      Log.d(TAG, "onSysEvt: 同步当天数据进行中:" + value + "%");
        } else if ((eventType == ProtocolEvt.SYNC_EVT_HEALTH_SYNC_COMPLETE.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 同步当天数据完成");
            mIDataCallBack.onResult(null,true,GlobalValue.SYNC_FINISH);
            ProtocolUtils.getInstance().getLiveData();

        } else if ((eventType == ProtocolEvt.SET_CMD_LONG_SIT.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 设置久坐成功");
            //此处上传成功回调
        } else if ((eventType == ProtocolEvt.SET_CMD_ALARM.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 设置闹钟成功");
            //此处上传成功回调
        } else if ((eventType == ProtocolEvt.SET_CMD_SPORT_GOAL.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 设置目标步数成功");
            //此处上传成功回调
        } else if ((eventType == ProtocolEvt.SET_NOTICE_CALL.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 通知来电成功");
            //此处上传成功回调
        } else if ((eventType == ProtocolEvt.SET_NOTICE_MSG.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 通知短信或其他信息成功");
            //此处上传成功回调
        } else if ((eventType == ProtocolEvt.SET_NOTICE_STOP_CALL.toIndex()) && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 停止来电成功");
            //此处上传成功回调
        } else if (eventType == ProtocolEvt.SET_CMD_LOST_FIND.toIndex() && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 防丢提醒 设置成功");
        } else if (eventType == ProtocolEvt.GET_FUNC_TABLE.toIndex() && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 得到FUNC成功");
            //     mHandler.sendEmptyMessage(GlobalValue.STEP_SYNC_START);
        } else if (eventType == ProtocolEvt.SET_CMD_USER_INFO.toIndex() && error == ProtocolEvt.SUCCESS) {
            Log.d(TAG, "onSysEvt: 设置用户信息");
            //     mHandler.sendEmptyMessage(GlobalValue.STEP_SYNC_START);
            mIDataCallBack.onResult(null,true,GlobalValue.SYNC_TIME_OK);
        }

    }


    private void syncStepData(int gapDay) {
        List<StepInfos> stepInfosList = new ArrayList<>();

        for (int i = 1; i <= gapDay; i++) {
            HealthSport sport = ProtocolUtils.getInstance().getHealthSport(DateUtils.getOffsetDate(-i));//(月份从0开始例如8月传7)
            if (sport != null) {
                StepInfos stepInfos = new StepInfos();
                stepInfos.setStep(sport.getTotalStepCount());
                stepInfos.setAccount(HardSdk.getInstance().getAccount());
                stepInfos.setDates(DateUtils.getBeforeDate(new Date(), -i));
                stepInfos.setUpLoad(0);
                stepInfos.setDistance(sport.getTotalDistance() / 1000f);
                stepInfos.setCalories(sport.getTotalCalory());
                Map<Integer, Integer> maps = this.queryOneHourStep(stepInfos.getDates());
                stepInfos.setStepOneHourInfo(maps);
                stepInfosList.add(0, stepInfos);
            }
        }
        SqlHelper.instance().syncBraceletStepData(stepInfosList);
    }

    private void syncSleepData(int gapDay) {

        List<SleepModel> sleepModelList = new ArrayList<>();
        for (int i = 1; i <= gapDay; i++) {
            SleepModel sleepModel = this.querySleepInfo(DateUtils.getBeforeDate(new Date(), -i), null);
            if (sleepModel != null) {
                sleepModel.account = HardSdk.getInstance().getAccount();
                sleepModel.date = DateUtils.getBeforeDate(new Date(), -i);    //往前推 i 天
                sleepModelList.add(0, sleepModel);
            }

        }
        SqlHelper.instance().syncBraceletSleepData(sleepModelList);
    }


    public void syncHeartData(int gapDay) {
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        for (int i = 1; i <= gapDay; i++) {
            List<HealthHeartRateItem> healthHeartRateItemList = ProtocolUtils.getInstance().getHeartRateItems(DateUtils.getOffsetDate(-i));
            if (healthHeartRateItemList != null && healthHeartRateItemList.size() > 0) {
                int minitue = 0;
                for (HealthHeartRateItem healthHeartRateItem : healthHeartRateItemList) {
                    minitue += healthHeartRateItem.getOffsetMinute();
                    HeartRateModel heartRateModel = new HeartRateModel();
                    heartRateModel.account = HardSdk.getInstance().getAccount();
                    heartRateModel.testMomentTime = TimeUtil.MinitueToDetailTime(-i, minitue); // 测试时间
                    heartRateModel.currentRate = healthHeartRateItem.getHeartRaveValue();
                    heartRateModelList.add(0, heartRateModel);
                }
            }

        }
        SqlHelper.instance().syncBraceletHeartData(heartRateModelList);
        //   System.out.println("AsyncHistoryData syncHeartData  end:" + System.currentTimeMillis());
    }


    @Override
    public void onSwitchDataAppStart(SwitchDataAppStartReply switchDataAppStartReply, int i) {

    }

    @Override
    public void onSwitchDataAppIng(SwitchDataAppIngReply switchDataAppIngReply, int i) {

    }

    @Override
    public void onSwitchDataAppEnd(SwitchDataAppEndReply switchDataAppEndReply, int i) {

    }

    @Override
    public void onSwitchDataBleStart(SwitchDataBleStart switchDataBleStart, int i) {

    }

    @Override
    public void onSwitchDataBleIng(SwitchDataBleIng switchDataBleIng, int i) {

    }

    @Override
    public void onSwitchDataBleEnd(SwitchDataBleEnd switchDataBleEnd, int i) {

    }

    @Override
    public void onSwitchDataAppPause(SwitchDataAppPauseReply switchDataAppPauseReply, int i) {

    }

    @Override
    public void onSwitchDataAppRestore(SwitchDataAppRestoreReply switchDataAppRestoreReply, int i) {

    }

    @Override
    public void onSwitchDataBlePause(SwitchDataBlePause switchDataBlePause, int i) {

    }

    @Override
    public void onSwitchDataBleRestore(SwitchDataBleRestore switchDataBleRestore, int i) {

    }

    @Override
    public void onSwitchDataAppBlePause(SwitchDataAppBlePause switchDataAppBlePause, int i) {

    }

    @Override
    public void onSwitchDataAppBleRestore(SwitchDataAppBleRestore switchDataAppBleRestore, int i) {

    }

    @Override
    public void onSwitchDataAppBleEnd(SwitchDataAppBleEnd switchDataAppBleEnd, int i) {

    }


    @Override
    public void onMacAddr(byte[] bytes) {

    }

    @Override
    public void onActivityData(SportData sportData, int i) {

    }


    @Override
    public void onDataSendTimeOut(byte[] bytes) {

    }

    @Override
    public void onWriteDataToBle(byte[] bytes) {

    }

    @Override
    public void onDeviceInfo(BasicInfos basicInfos) {

    }

    @Override
    public void onLogData(byte[] bytes, boolean b) {

    }

    @Override
    public void onGsensorParam(GsensorParam gsensorParam) {

    }

    @Override
    public void onHrSensorParam(HrSensorParam hrSensorParam) {

    }


    @Override
    public void onSensorData(byte[] bytes) {

    }

}

package com.walnutin.hardsdkdemo.ProductList.hch;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.BleBaseSdk;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Clock;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfo;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Version;
import com.walnutin.hardsdkdemo.utils.Config;
import com.walnutin.hardsdkdemo.utils.DateUtils;
import com.walnutin.hardsdkdemo.utils.DeviceSharedPf;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;
import com.walnutin.hardsdkdemo.utils.WeekUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 作者：MrJiang on 2017/1/12 17:24
 */
public class HchSdk extends BleBaseSdk {
    final String TAG = "HchSdk";
    private WriteCommand mWriteCommand;
    private DataProcessing mDataProcessing;
    private static HchSdk mInstance;

    private HchSdk() {
    }

    public static ICommonSDKIntf getInstance() {
        if (mInstance == null) {
            mInstance = new HchSdk();
        }
        return mInstance;
    }

    @Override
    public boolean initialize(Context context) {
        if (!super.initialize(context)) {
            return false;
        }
        mDataProcessing = DataProcessing.getInstance();
        mDataProcessing.setHeartRateListener(this);
        mDataProcessing.setSleepListener(this);
        mDataProcessing.setStepListener(this);
        mDataProcessing.setDataCallback(this);
        return true;
    }

    @Override
    public void initService() {
        mBLEServiceOperate.startBindService(GlobalValue.FACTORY_HCH);
    }


    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen, String type, String tip) {

        if (TextUtils.isEmpty(type)) {
            type = GlobalValue.SLEEP_TYPE;
        }
        mWriteCommand.setAlarmClock(flag, weekPeroid, hour, minitue, isOpen, type, tip);
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
    public void findBand(int num) {
        mWriteCommand.findBand(num);
    }


    @Override
    public void setHeightAndWeight(int height, int weight, int age, String sexfrom, String birthday) {
        String value = "";
        String h = DigitalTrans.algorismToHEXString(height);
        String w = DigitalTrans.algorismToHEXString(weight);

//        String sex = MySharedPf.getInstance(mContext).getString("sex", "女"); // 0 为女 1 为男
        String sex = null;
        if (sexfrom != null) {
            if (sexfrom.equals("女")) {
                sex = "01";
            } else {
                sex = "00";
            }
        } else {
            sex = "00";
        }

        String birthYear = null;
        if (birthday != null) {
            String[] split = birthday.split("-");
            if (split != null && split.length == 3) {
                birthYear = split[0];
            }
        }

//        String year = MySharedPf.getInstance(mContext).getString("birth", "1995-01-01");
        String year = null;
        if (birthYear != null) {
            year = DigitalTrans.algorismToHEXString(Integer.parseInt(getYearOld(birthYear)));
        }
        if (sex != null && year != null) {
            value = DigitalTrans.getResultCommand("68040400" + h + w + sex + year);
            mWriteCommand.sendHexString(value);
        } else {
            Log.e(TAG, "setHeightAndWeight: " + "设置身高体总出错，最终传值为null");
        }
    }


    String getYearOld(String date) {
        String birth = date;
        int year = Integer.valueOf(String.valueOf(birth.split("-")[0]));
        int yearOld = Calendar.getInstance().get(Calendar.YEAR) - year;
        return yearOld + "";
    }

    @Override
    public void sendSedentaryRemindCommand(int isOpen, int interval, String startTime, String endTime, boolean isDisturb) {
        mWriteCommand.setLongSitRemind(isOpen, interval, startTime, endTime);
    }

    @Override
    public void resetBracelet() {
        mWriteCommand.deleteDevicesAllData();
    }


    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
        if (type == GlobalValue.TYPE_MESSAGE_QQ) {
            type = 02;
        } else if (type == GlobalValue.TYPE_MESSAGE_WECHAT) {
            type = 01;

        } else if (type == GlobalValue.TYPE_MESSAGE_FACEBOOK) { // 添加facebook 消息提醒
            type = 03;
        } else if (type == GlobalValue.TYPE_MESSAGE_WHATSAPP) {
            type = 06;
        }
        mWriteCommand.notifyMessage(type, body);
    }


    @Override
    public void openTakePhotoFunc(boolean isOpen) {
        if (isOpen == true) {
            mWriteCommand.setTakePhoto("00");
        } else {
            mWriteCommand.setTakePhoto("01");
        }
    }


    @Override
    public void startRateTest() {
        mWriteCommand.sendRateTestCommand(GlobalValue.RATE_START);
    }

    @Override
    public void stopRateTest() {
        mWriteCommand.sendRateTestCommand(GlobalValue.RATE_STOP);
    }


    int timeIndex = 0;

    @Override
    public void syncAllStepData() {

//        Log.d(TAG, "syncAllStepData: 开始同步");
//        if (DeviceSharedPf.getInstance(mContext).getString("lastsyncSleepTime" + HardSdk.getInstance().getDeviceAddr(), "2010")
//                .equals(TimeUtil.getCurrentDate())) {
        mWriteCommand.correctTime();   //todo hardsdk 里面需要做多次开始冗余的判断
//        } else {
//            if (timeIndex++ > 3) {
//                cdtSync.cancel();
//                cdtSync.start();
//                timeIndex = 0;
//            }
//        }
    }

    @Override
    public void syncAllHeartRateData() {
        //        mWriteCommand.asyncHeartData(1);
        //    mWriteCommand.asyncAllData("00"); // 計步同步
        //   mWriteCommand.asyncAllData("02");
    }


    @Override
    public void syncAllSleepData() {
        //   mWriteCommand.asyncAllData("02");
    }

    @Override
    public StepInfo queryOneDayStepInfo(String date) {
        return null;
    }

    @Override
    public Map<Integer, Integer> queryOneHourStep(String date) {
        Map<Integer, Integer> map = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(), date).getStepOneHourInfo();
        return map;
    }

    @Override
    public SleepModel querySleepInfo(String startDate, String endDate) {
        return null;
    }

    @Override
    public void stopVibration() {
    }


    @Override
    public List queryRateOneDayDetailInfo(String date) {
        return null;
    }

    @Override
    public boolean isSupportHeartRate(String deviceName) {
        return true;
    }

    @Override
    public boolean isSupportBloodPressure(String deviceName) {
        return false;
    }

    @Override
    public boolean isSupportUnLostRemind(String deviceName) {
        return false;
    }


    public int getSupportAlarmNum() {
        return 8;
    }

    @Override
    public void noticeRealTimeData() {

        if (HardSdk.getInstance().isSyncing() == false) {
            mWriteCommand.getRealtimeData();
//            mWriteCommand.asyncAllData("00");
        }
    }

    @Override
    public void queryDeviceVesion() {
        mWriteCommand.sendHexString("680700006F16");
    }

    @Override
    public boolean isVersionAvailable(String version) {
        return false;
    }


    @Override
    public void setDeviceSwitch(String type, boolean isOpen) {

    }

    @Override
    public void writeCommand(String hexValue) {

        mWriteCommand.sendHexString(hexValue);
    }

    @Override
    public void findBattery() {
        mWriteCommand.sendHexString("680300006B16");
    }


    @Override
    public void setUnLostRemind(boolean isOpen) {

        mWriteCommand.setUnLostRemind(isOpen);
    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {
        if (smsType == GlobalValue.TYPE_MESSAGE_PHONE) {
            //     mWriteCommand.notifyMessage(0,content);
            mWriteCommand.sendCallInfo(number, contact);
        } else if (smsType == GlobalValue.TYPE_MESSAGE_SMS) {
            mWriteCommand.notifyMessage(0, content);
        }
    }


    @Override
    public void openFuncRemind(int type, boolean isOpen) {
        mWriteCommand.openFuncRemind(type, isOpen);
    }

    @Override
    public void syncBraceletDataToDb() {
        //   HchDb.getInstance().printAllHeartRateData();

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
                syncHeartData(gapDay);
            } else {       // 同步最近7天的数据
                syncHeartData(7);
            }
            MySharedPf.getInstance(mContext).setString(HardSdk.getInstance().getAccount() + "_lastData_" + HardSdk.getInstance().getDeviceAddr(), TimeUtil.getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void refreshBleServiceUUID() {
        mBluetoothLeService.getCurrentUUID(GlobalValue.FACTORY_HCH);
    }


    @Override
    public void OnConnetionStateResult(boolean result, int status) {
        System.out.println("----------data: " + status);
        if (status == GlobalValue.DISCONNECT_MSG) {
            mIDataCallback.onResult(null, true, GlobalValue.DISCONNECT_MSG);
        } else if (status == GlobalValue.CONNECTED_MSG) {
            mIDataCallback.onResult(null, true, GlobalValue.CONNECTED_MSG);
        } else if (status == GlobalValue.CONNECT_TIME_OUT_MSG) {
            mIDataCallback.onResult(null, true, GlobalValue.CONNECT_TIME_OUT_MSG);
        }
    }

    @Override
    public void onSleepChange() {
        //收到睡眠数据
        SleepModel sleepTimeInfo = SqlHelper.instance().getOneDaySleepListTime(HardSdk.getInstance().getAccount(), TimeUtil.getCurrentDate()); //CalendarUtils.getCalendar(0)
        //   Log.i(TAG, "onSleepChange " + sleepTimeInfo);

        if (sleepTimeInfo.totalTime > 0 && mIRealDataSubject != null) {
            mIRealDataSubject.sleepChanged(sleepTimeInfo.getLightTime(), sleepTimeInfo.getDeepTime(), sleepTimeInfo.getTotalTime(),
                    sleepTimeInfo.getSleepStatusArray(), sleepTimeInfo.getTimePointArray(), sleepTimeInfo.getDuraionTimeArray());
        }
    }


    @Override
    public void onHeartRateChange(int heartRate, int state) {
        if (mIRealDataSubject != null) {
            mIRealDataSubject.heartRateChanged(heartRate, state);
        }
    }

    //收到计步数据
    @Override
    public void onStepChange(int steps, float distance, int calories) {
        if (mIRealDataSubject != null) {
            mIRealDataSubject.stepChanged(steps, distance, calories, true);
        }
    }

    private CountDownTimer cdt = new CountDownTimer(2000, 2000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mWriteCommand.asyncHeartData(3); // 計步同步
        }

        @Override
        public void onFinish() {
            mWriteCommand.asyncHeartData(2); // 計步同步
        }
    };


    private CountDownTimer cdtSync = new CountDownTimer(2000, 2000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            System.out.println("CountDownTimer cdtSync");
            mWriteCommand.correctTime(); // 开始同步
            timeIndex = 0;
        }
    };

    private void updateTodayHeartRate() {
        List<String> todayData = HchDb.getInstance().getHeartRateDataByDate(TimeUtil.getCurrentDate());

        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        String heartValue = "";

        int len = todayData.size();
        for (int j = 0; j < len; j++) {
            String data = todayData.get(j);
            int serial = Integer.parseInt(data.substring(2, 4)); // 包号
            data = data.substring(4, data.length());

            for (int i = 0; i < 180; i++) {
                heartValue = data.substring(i * 2, i * 2 + 2);
                if (!heartValue.equals("FF")) {
                    int value = DigitalTrans.hexStringToAlgorism(heartValue);
                    if (value > 0) {
                        HeartRateModel heartRateModel = new HeartRateModel();
                        int miniute = 180 * (serial - 1) + i;
                        heartRateModel.currentRate = value;
                        heartRateModel.testMomentTime = TimeUtil.MinitueToDetailTimeByDate(TimeUtil.getCurrentDate(), miniute);
                        heartRateModel.account = HardSdk.getInstance().getAccount();
                        heartRateModelList.add(heartRateModel);
                    }
                }
            }
        }

        SqlHelper.instance().syncBraceletHeartData(heartRateModelList);

        onHeartRateChange(0, GlobalValue.RATE_TEST_FINISH);
    }

    @Override
    public void onResult(Object data, boolean state, int flag) {
        super.onResult(data, state, flag);
        //    System.out.println("-------------status:--------------- " + status);
        if (flag == GlobalValue.SYNC_TIME_OK) {  // 今天
            timeIndex = 0;
            mIDataCallback.onResult(null, true, GlobalValue.SYNC_TIME_OK);
            mWriteCommand.asyncAllData("00"); //今天全天记步
        } else if (flag == GlobalValue.STEP_SYNCING) {
            mIDataCallback.onResult(null, true, GlobalValue.SYNC_TIME_OK);
            mWriteCommand.asyncAllData("01"); // 同步睡眠
            Log.i(TAG, "记步同步z。。。。");
        } else if (flag == GlobalValue.STEP_FINISH) {
            Log.i(TAG, "记步完成。。。。");
            mIDataCallback.onResult(null, true, GlobalValue.STEP_FINISH);
            mWriteCommand.asyncAllData("02"); // 同步睡眠

        } else if (flag == GlobalValue.HEART_FINISH) {
            Log.i(TAG, "心率完成。。。。");
            updateTodayHeartRate();
            mIDataCallback.onResult(null, true, GlobalValue.SYNC_FINISH);

        } else if (flag == GlobalValue.SLEEP_SYNC_OK) { // 睡眠完成 同步心率
            Log.i(TAG, "睡眠完成。。。。");
            mWriteCommand.asyncHeartData(1);
        } else if (flag == GlobalValue.OFFLINE_SYNC_OK) { //   离线同步完成
            DeviceSharedPf.getInstance(mContext).setString("lastsyncSleepTime" + HardSdk.getInstance().getDeviceAddr(), TimeUtil.getCurrentDate()); // 设置同步日期
            syncAllStepData();
        } else if (flag == GlobalValue.DISCOVERY_DEVICE_SHAKE) {
            mWriteCommand.sendHexString("680e0000017716");  //  拍照功能应答
            mIDataCallback.onResult(null, true, GlobalValue.DISCOVERY_DEVICE_SHAKE);
        } else if (flag == GlobalValue.Firmware_Version) {
            String v = (String) data;

            Version version = new Version();
            version.firmwareVersion = String.valueOf(DigitalTrans.hexStringToAlgorism(v.substring(8, 10)));
            String bV = v.substring(10, 12);
            String fV = v.substring(12, 14);

            version.bluetoothVersion = DigitalTrans.hexStringToAlgorism(bV.substring(0, 1)) + "." + DigitalTrans.formatData(DigitalTrans.hexStringToAlgorism(bV.substring(1, 2)) + "");
            version.braceletVersion = DigitalTrans.hexStringToAlgorism(fV.substring(0, 1)) + "." + DigitalTrans.formatData(DigitalTrans.hexStringToAlgorism(fV.substring(1, 2)) + "");

            System.out.println("firm:" + version.firmwareVersion + " blueVersion:" + version.bluetoothVersion + " braVer:" + version.braceletVersion);
            mIDataCallback.onResult(version, true, GlobalValue.Firmware_Version);

        } else if (flag == GlobalValue.CHECK_SUPPORT_PAGE_SUCCESS) {
            String v = (String) data;
            mIDataCallback.onResult(v.substring(10, 18), true, GlobalValue.CHECK_SUPPORT_PAGE_SUCCESS);

        } else if (flag == GlobalValue.READ_PAGE_CONFIG) {
            String v = (String) data;
            mIDataCallback.onResult(v.substring(10, 18), true, GlobalValue.READ_PAGE_CONFIG);

        }
    }

    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {

        if (status == GlobalValue.OFFLINE_STEP_SYNCING) {
            mWriteCommand.answerData("00", data.substring(8, 14)); // 记步接收中回调。。。
            mIDataCallback.onResult(null, true, GlobalValue.STEP_SYNC_START);
            cdtSync.cancel();
        } else if (status == GlobalValue.OFFLINE_STEP_SYNC_OK) {
            mWriteCommand.answerData("01", data.substring(8, 14)); // 记步接收完回调。。。
        } else if (status == GlobalValue.OFFLINE_HEART_SYNCING) {
            Log.i(TAG, "心率同步中---。。。。");
            if (data.substring(8, 14).equals(DigitalTrans.getHexTodayDate())) {
                mWriteCommand.answerHeartData(data.substring(8, 14), Integer.valueOf(data.substring(18, 20)) + 1);
            } else {
                mWriteCommand.answerHeartData(data.substring(8, 14), Integer.valueOf(data.substring(18, 20)));
            }
        } else if (status == GlobalValue.OFFLINE_SLEEP_SYNC_OK) {
            mWriteCommand.answerData("02", data.substring(8, 14)); // 睡眠接收中回调。。。
        } else if (status == GlobalValue.FATIGE) {
            mWriteCommand.answerFatigeData(data.substring(8, 14)); // 疲劳度接收中回调。。。

        }
    }

    @Override
    public void onChildBleServiceInitOK() {
        mWriteCommand = new WriteCommand(mBluetoothLeService);
    }


    public void syncHeartData(int gapDay) {
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        for (int g = 1; g <= gapDay; g++) {
            List<String> todayData = HchDb.getInstance().getHeartRateDataByDate(DateUtils.getBeforeDate(new Date(), -g));
            String heartValue = "";
            int len = todayData.size();
            for (int j = 0; j < len; j++) {
                String data = todayData.get(j);
                int serial = Integer.parseInt(data.substring(2, 4)); // 包号
                data = data.substring(4, data.length());
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if ((serial - 1) * 3 > hour) {
                    return;
                }

                for (int i = 0; i < 180; i++) {
                    heartValue = data.substring(i * 2, i * 2 + 2);
                    if (!heartValue.equals("FF")) {
                        int value = DigitalTrans.hexStringToAlgorism(heartValue);
                        if (value > 0) {
                            HeartRateModel heartRateModel = new HeartRateModel();
                            int miniute = 180 * (serial - 1) + i;
                            heartRateModel.currentRate = value;
                            heartRateModel.testMomentTime = TimeUtil.MinitueToDetailTime(-i, miniute);
                            heartRateModel.account = HardSdk.getInstance().getAccount();
                            heartRateModelList.add(heartRateModel);
                        }
                    }
                }
            }
        }
        SqlHelper.instance().syncBraceletHeartData(heartRateModelList);
    }


    @Override
    public void sendOffHookCommand() {
        mWriteCommand.sendOffHookCommand();
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
        return true;
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
        return true;
    }

    @Override
    public boolean isNeedAlarmListSetting() {
        return false;
    }

    @Override
    public boolean isSupportWristScreen() {
        return true;
    }

    // //wechat 位数 0 qq 1 facebookmsg 2 whatspp 3 twitter 4 skype 5 Line 6 linkedin7 Instagram 8
    @Override
    public String getSupportNoticeFunction() {
        return "1111110000";
    }

    @Override
    public boolean isSupportSetSedentarinessTime() {
        return true;
    }

    @Override
    public boolean isFindPhone() {
        return false;
    }

    @Override
    public boolean isSupportControlHVScreen() {
        return false;
    }

    @Override
    public void setControlHVScreen(int type) {
    }

    @Override
    public void startBloodTest() {

    }

    @Override
    public void stopBloodTest() {

    }


    @Override
    public void startAutoHeartTest(boolean isAuto) {

        String data = "";
        if (isAuto) {
            data = "6802050001021e04019516";
        } else {
            data = "6802050001021e04009416";

        }
        mWriteCommand.sendHexString(data);
    }


    @Override
    public void syncNoticeConfig() {

    }

    @Override
    public void readNoticeConfigFromBand() {

    }

    @Override
    public void readAlarmListFromBand() {

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
    public void unBindUser() {

    }


}

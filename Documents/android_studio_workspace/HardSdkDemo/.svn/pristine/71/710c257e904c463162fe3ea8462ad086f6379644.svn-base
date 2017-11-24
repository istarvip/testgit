package com.walnutin.hardsdkdemo.ProductList.hch;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.BleBaseSdk;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.utils.DateUtils;
import com.walnutin.hardsdkdemo.utils.DeviceSharedPf;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

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
    private com.walnutin.hardsdkdemo.ProductList.hch.DataProcessing mDataProcessing;

    private static HchSdk mInstance;
    private HchSdk(){
    }

    public static ICommonSDKIntf getInstance() {
        if(mInstance == null){
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
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {
        mWriteCommand.setAlarmClock(flag, weekPeroid, hour, minitue, isOpen);
    }


    @Override
    public void findBand(int num) {
        mWriteCommand.findBand(num);
    }

    @Override
    public void setHeightAndWeight(int height, int weight, int var3) {

    }

    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time) {
        mWriteCommand.setLongSitRemind(isOpen, time);
    }

    @Override
    public void resetBracelet() {
        mWriteCommand.deleteDevicesAllData();
    }


    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
//        0x00：短信
//        0x01：微信
//        0x02：QQ
//        0x03：facebook
//        0x04：skype
//        0x05：twitter
//        0x06：whatsAPP


        Log.d(TAG, "sendQQWeChatTypeCommand: type:"+type +" body:"+body);
        if (type == GlobalValue.TYPE_MESSAGE_QQ) {
            type = 02;
        } else if (type == GlobalValue.TYPE_MESSAGE_WECHAT) {
            type = 01;
        } else if (type == GlobalValue.TYPE_MESSAGE_FACEBOOK) {
            type = 03;
        }else if (type == GlobalValue.TYPE_MESSAGE_SKYPE) {
            type = 04;
        }else if (type == GlobalValue.TYPE_MESSAGE_TWITTER) {
            type = 05;
        }else if (type == GlobalValue.TYPE_MESSAGE_WHATSAPP) {
            type = 06;
        }else if (type == GlobalValue.TYPE_MESSAGE_OTHERSMS) {
            type = 00;
        }
        mWriteCommand.notifyMessage(type, body);
    }


    @Override
    public void sendOffHookCommand() {
        mWriteCommand.sendOffHookCommand();
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

        if (DeviceSharedPf.getInstance(mContext).getString("lastsyncSleepTime" + HardSdk.getInstance().getDeviceAddr(), "2010")
                .equals(TimeUtil.getCurrentDate())) {
            mWriteCommand.correctTime();
        } else {
            if (timeIndex++ > 3) {
                cdtSync.cancel();
                cdtSync.start();
                timeIndex =0;
            }
        }
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
    public Map<Integer, Integer> queryOneHourStep(String date) {
        Map<Integer, Integer> map = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(),date).getStepOneHourInfo();
        return map;
    }


    @Override
    public void stopVibration() {

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


    public int getAlarmNum() {
        return 0;
    }

    @Override
    public void noticeRealTimeData() {

        if (HardSdk.getInstance().isSyncing() == false) {
            mWriteCommand.getRealtimeData();
        }
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
            HardSdk.getInstance().setDevConnected(false);
            mIDataCallback.onResult(null,true,GlobalValue.DISCONNECT_MSG);
        } else if (status == GlobalValue.CONNECTED_MSG) {
            mIDataCallback.onResult(null,true,GlobalValue.CONNECTED_MSG);
        } else if (status == GlobalValue.CONNECT_TIME_OUT_MSG) {
            mIDataCallback.onResult(null,true,GlobalValue.CONNECT_TIME_OUT_MSG);
        }
    }

    @Override
    public void onSleepChange() {
        //收到睡眠数据
        SleepModel sleepTimeInfo = SqlHelper.instance().getOneDaySleepListTime(HardSdk.getInstance().getAccount(), TimeUtil.getCurrentDate()); //CalendarUtils.getCalendar(0)
        Log.i(TAG, "onSleepChange " + sleepTimeInfo);

        if (sleepTimeInfo.totalTime > 0 && iDataSubject != null) {
            iDataSubject.sleepChanged(sleepTimeInfo.getLightTime(), sleepTimeInfo.getDeepTime(), sleepTimeInfo.getTotalTime(),
                    sleepTimeInfo.getSleepStatusArray(), sleepTimeInfo.getTimePointArray(), sleepTimeInfo.getDuraionTimeArray());
        }
    }


    @Override
    public void onHeartRateChange(int heartRate, int state) {
        if (iDataSubject != null) {
            iDataSubject.heartRateChanged(heartRate, state);
        }
    }

    //收到计步数据
    @Override
    public void onStepChange(int steps, float distance, int calories) {
        if (iDataSubject != null) {
            iDataSubject.stepChanged(steps, distance, calories, true);
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
        super.onResult(data,state,flag);
        //    System.out.println("-------------status:--------------- " + status);
        if (flag == GlobalValue.SYNC_TIME_OK) {  // 今天
            timeIndex = 0;
            mIDataCallback.onResult(null,true,GlobalValue.SYNC_TIME_OK);
            mWriteCommand.asyncAllData("00"); //今天全天记步
        } else if (flag == GlobalValue.STEP_SYNCING) {
            mIDataCallback.onResult(null,true,GlobalValue.SYNC_TIME_OK);
            mWriteCommand.asyncAllData("01"); // 同步睡眠
            Log.i(TAG, "记步同步z。。。。");
        } else if (flag == GlobalValue.STEP_FINISH) {
            Log.i(TAG, "记步完成。。。。");
            mIDataCallback.onResult(null,true,GlobalValue.STEP_FINISH);
            mWriteCommand.asyncAllData("02"); // 同步睡眠

        } else if (flag == GlobalValue.HEART_FINISH) {
            Log.i(TAG, "心率完成。。。。");
            updateTodayHeartRate();
            mIDataCallback.onResult(null,true,GlobalValue.SYNC_FINISH);

        } else if (flag == GlobalValue.SLEEP_SYNC_OK) { // 睡眠完成 同步心率
            Log.i(TAG, "睡眠完成。。。。");
            mWriteCommand.asyncHeartData(1);
        } else if (flag == GlobalValue.OFFLINE_SYNC_OK) { //   离线同步完成
            DeviceSharedPf.getInstance(mContext).setString("lastsyncSleepTime" + HardSdk.getInstance().getDeviceAddr(), TimeUtil.getCurrentDate()); // 设置同步日期
            syncAllStepData();
        }
    }

    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {

        if (status == GlobalValue.OFFLINE_STEP_SYNCING) {
            mWriteCommand.answerData("00", data.substring(8, 14)); // 记步接收中回调。。。
            mIDataCallback.onResult(null,true,GlobalValue.STEP_SYNC_START);
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
    public void onBleServiceInitOK() {
        super.onBleServiceInitOK();
        mWriteCommand = new WriteCommand(mBluetoothLeService);
    }
}

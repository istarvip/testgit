package com.walnutin.hardsdkdemo.ProductList.rtk;

import android.content.Context;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.BleBaseSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IRealDataListener;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfo;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by chenliu on 2017/2/23.
 */

public class RtkSdk extends BleBaseSdk implements IRealDataListener {

    final String TAG = "RtkSdk";
    private WriteCommand mWriteCommand;
    private DataProcessing mDataProcessing;

    private static RtkSdk mInstance;

    private RtkSdk() {
    }


    public static ICommonSDKIntf getInstance() {
        if (mInstance == null) {
            mInstance = new RtkSdk();
        }
        return mInstance;
    }


    @Override
    public boolean initialize(Context context) {
        Log.d(TAG, "rtk  initialize2: run");
        if (!super.initialize(context)) {
            return false;
        }
        mDataProcessing = DataProcessing.getInstance();
        //    mDataProcessing.setHeartRateListener(this);
        mDataProcessing.setSleepListener(this);
        mDataProcessing.setmIRealDataListener(this);
        //    mDataProcessing.setStepListener(this);
        mDataProcessing.setDataCallback(this);
        Log.d(TAG, "rtk  initialize3: run");
        return true;
    }

    @Override
    public void initService() {
        Log.d(TAG, "RtkSdk  initService: run");
        mBLEServiceOperate.startBindService(GlobalValue.FACTORY_RTK);
    }

    @Override
    public void refreshBleServiceUUID() {
        mBluetoothLeService.getCurrentUUID(GlobalValue.FACTORY_RTK);
    }


    @Override
    public void resetBracelet() {
        mWriteCommand.resetBracelet();
    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {

        if (GlobalValue.TYPE_MESSAGE_SMS == smsType) {
            mWriteCommand.sendSmsInfo(number, contact, content);
        } else if (GlobalValue.TYPE_MESSAGE_PHONE == smsType) {
            mWriteCommand.sendCallInfo(number, contact);
        }
    }

    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
        mWriteCommand.sendQQWeChatTypeCommand(type, body);
    }


    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time, String startTime, String endTime, boolean isDisturb) {
        try {
            if (startTime != null) {
                String[] splitStart = startTime.split(":");
                String[] splitEnd = startTime.split(":");

                if (splitStart != null && splitStart.length == 2 && splitEnd != null && splitEnd.length == 2) {
                    mWriteCommand.sendSedentaryRemindCommand(isOpen, time, Integer.parseInt(splitStart[0]), Integer.parseInt(splitStart[1]), Integer.parseInt(splitEnd[0]), Integer.parseInt(splitEnd[1]));
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void setHeightAndWeight(int height, int weight, int age, String sexfrom, String birthday) {
        mWriteCommand.setHeight(height);
        mWriteCommand.setWeight(weight);
        mWriteCommand.setAge(age);
        mWriteCommand.setSex(sexfrom);
    }

    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen, String typ, String tip) {
        mWriteCommand.setAlarmClock(flag, weekPeroid, hour, minitue, isOpen);
    }


    @Override
    public void sendOffHookCommand() {
        mWriteCommand.sendOffHookCommand();
    }

    @Override
    public void sendPalmingStatus(boolean isOpen) {

    }

    @Override
    public void startRateTest() {
        mWriteCommand.sendRateTestCommand(GlobalValue.RATE_START);
    }

    @Override
    public void stopRateTest() {
        mWriteCommand.sendRateTestCommand(GlobalValue.RATE_STOP);

    }

    @Override
    public void syncAllStepData() {
//        Log.i(TAG,"syncAllStepData");
//        startRateTest();
        mWriteCommand.syncTime();

        mIDataCallback.onResult(null, true, GlobalValue.STEP_SYNC_START);

        mWriteCommand.sendHexString("17");  //获取 设备版本号
        //   getSportTenData();
    }

    @Override
    public void syncAllHeartRateData() {    //

        mIDataCallback.onResult(null, true, GlobalValue.HEART_FINISH);
    }

    @Override
    public void syncAllSleepData() {
        if (MySharedPf.getInstance(mContext).getIsSupportGPS()) {
            mWriteCommand.sendHexString("54");
        } else {
            getSleepData();
        }
        //     mWriteCommand.sendHexString("54"); // 得到每天详细睡眠 记录
    }


    private void getSleepData() {      // 得到 每天大概数据
        String data = DigitalTrans.algorismToHEXString(25);
        mWriteCommand.sendHexString(data);
    }

    @Override
    public Map<Integer, Integer> queryOneHourStep(String date) {
        return null;
    }


    @Override
    public void syncBraceletDataToDb() {

    }


    @Override
    public void openFuncRemind(int type, boolean isOpen) {
        switch (type) {
            case GlobalValue.TYPE_MESSAGE_QQ:
                break;
            case GlobalValue.TYPE_MESSAGE_WECHAT:
                break;

            case GlobalValue.TYPE_MESSAGE_SMS:
                break;
            case GlobalValue.TYPE_MESSAGE_FACEBOOK:
                break;
        }
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

    @Override
    public int getSupportAlarmNum() {
        return 1;
    }

    @Override
    public void noticeRealTimeData() {

    }

    @Override
    public void queryDeviceVesion() {
        mWriteCommand.queryDeviceVersion();
    }

    @Override
    public boolean isVersionAvailable(String version) {
        return false;
    }


    @Override
    public StepInfo queryOneDayStepInfo(String date) {
        return null;
    }

    @Override
    public SleepModel querySleepInfo(String startDate, String endDate) {
        return null;
    }

    @Override
    public List<HeartRateModel> queryRateOneDayDetailInfo(String date) {
        return null;
    }

    @Override
    public void setDeviceSwitch(String type, boolean isOpen) {

    }

    @Override
    public boolean isSupportOffPower() {
        return true;
    }

    @Override
    public boolean isSupportResetBand() {
        return true;
    }

    @Override
    public void getSportTenData() {

        String data = DigitalTrans.algorismToHEXString(39) + "00" + "00" +
                DigitalTrans.algorismToHEXString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                + DigitalTrans.algorismToHEXString(Calendar.getInstance().get(Calendar.MINUTE));
        mWriteCommand.sendHexString(data);
    }


    @Override
    public void setScreenOnTime(int screenOnTime) {
        mWriteCommand.setScreenOnTime(screenOnTime);
    }

    @Override
    public void changeMetric(boolean isMetric) {
        mWriteCommand.setMetric(isMetric);
    }

    @Override
    public void changeAutoHeartRate(boolean isAuto) {
        mWriteCommand.setAutoHeartRate(isAuto);
    }

    @Override
    public void changePalming(boolean palming) {
        mWriteCommand.changePalming(palming);
    }

    @Override
    public void setTarget(int target, int type) {

        String hexT = DigitalTrans.algorismToHEXString(target);

        if (hexT.length() == 2) {
            hexT = hexT + "00";
        }
        hexT = DigitalTrans.formatData(hexT);
        String cmd = "26" + hexT;
        mWriteCommand.sendHexString(cmd);
    }

    @Override
    public void readClock() {

    }

    @Override
    public boolean isSupportTakePhoto() {
        return false;
    }

    @Override
    public boolean isSupportFindBand() {
        return false;
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
        return false;
    }

    @Override
    public String getSupportNoticeFunction() {
        return null;
    }

    @Override
    public boolean isSupportSetSedentarinessTime() {
        return false;
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
    public void openTakePhotoFunc(boolean isOpen) {

    }

    private void writeSportData() {         // 运动数据
        String data = DigitalTrans.algorismToHEXString(21);
        mWriteCommand.sendHexString(data);
    }

    private void writeExciseData() {
        mWriteCommand.sendHexString("4d");
    }

    @Override
    public void OnConnetionStateResult(boolean result, int status) {
        Log.d(TAG, "OnConnetionStateResult: status:" + status);
        if (status == GlobalValue.DISCONNECT_MSG) {
            Log.d(TAG, "OnConnetionStateResult: ");
            mIDataCallback.onResult(null, true, GlobalValue.DISCONNECT_MSG);
        } else if (status == GlobalValue.CONNECTED_MSG) {
            mIDataCallback.onResult(null, true, GlobalValue.CONNECTED_MSG);
        } else if (status == GlobalValue.CONNECT_TIME_OUT_MSG) {
            mIDataCallback.onResult(null, true, GlobalValue.CONNECT_TIME_OUT_MSG);
        }
    }

    @Override
    public void onResult(Object data, boolean state, int flag) {
        super.onResult(data, state, flag);
        switch (flag) {
            case GlobalValue.Firmware_Version:    //  读取版本 信息成功 ，
                Log.i(TAG, "writeSportData");
                //  mIDataCallback.onResult(null, true, GlobalValue.SYNC_FINISH);
                getSportTenData();
                break;
            case GlobalValue.STEP_SYNCING:    // 获取运动数据
                Log.i(TAG, "writeSportData");
                //  mIDataCallback.onResult(null, true, GlobalValue.SYNC_FINISH);
                writeSportData();
                break;
            case GlobalValue.DAY_SLEEP_OK:    // 获取运动数据
                Log.i(TAG, "writeSportData");
                //  mIDataCallback.onResult(null, true, GlobalValue.SYNC_FINISH);
                getSleepData();
                break;
            case GlobalValue.STEP_FINISH:      // 获取睡眠数据
                mIDataCallback.onResult(null, true, GlobalValue.STEP_FINISH);
                break;
            case GlobalValue.SLEEP_SYNC_OK:       // 获取锻炼数据
                Log.i(TAG, "writeExciseData");
                writeExciseData();
                break;
            case GlobalValue.SYNC_FINISH:             // 同步完成
                mIDataCallback.onResult(null, true, GlobalValue.SYNC_FINISH);
                break;
        }
    }


    @Override
    public void onChildBleServiceInitOK() {
        mWriteCommand = new WriteCommand(mBluetoothLeService);
    }


    //以下内容要合并接口

    //没有此功能
    @Override
    public void findBand(int num) {

    }

    //没有此功能
    @Override
    public void stopVibration() {

    }

    //没有此功能
    @Override
    public void setUnLostRemind(boolean isOpen) {

    }

    @Override
    public void startUpdateBLE() {

    }

    @Override
    public void cancelUpdateBle() {

    }

    //没有此功能
    @Override
    public void readBraceletConfig() {

    }

    //没有此功能
    @Override
    public void unBindUser() {

    }

    @Override
    public void findBattery() {

    }

    @Override
    public void startAutoHeartTest(boolean isAuto) {

    }


    @Override
    public void setAlarmList(List clockList) {

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
    public void writeCommand(String hexCmd) {

    }

    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {
    }


    //没有用到
    @Override
    public void onStepChange(int steps, float distance, int calories) {

    }

    //没有用到
    @Override
    public void onHeartRateChange(int heartRate, int state) {

    }

    //没有用到
    @Override
    public void onSleepChange() {

    }

    @Override
    public void onRealData(int step, float distance, int calories, int heart, int sbp, int dbp, int battery) {
        if (mIRealDataSubject != null) {
            mIRealDataSubject.stepChanged(step, distance, calories, false);
            mIRealDataSubject.heartRateChanged(heart, 0);
            mIRealDataSubject.bloodPressureChanged(sbp, dbp, 0);
        }
    }

}

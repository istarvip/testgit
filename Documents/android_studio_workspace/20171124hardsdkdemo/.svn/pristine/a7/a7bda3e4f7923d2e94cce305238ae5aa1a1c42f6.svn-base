package com.walnutin.hardsdkdemo.ProductList.wyp;

import android.content.Context;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.BleBaseSdk;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.utils.GlobalValue;

import java.util.Map;

/**
 * 作者：MrJiang on 2017/1/12 17:24
 */
public class WypSdk extends BleBaseSdk {
    private static final String TAG = "WypSdk";
    private com.walnutin.hardsdkdemo.ProductList.wyp.WriteCommand mWriteCommand;
    com.walnutin.hardsdkdemo.ProductList.wyp.DataProcessing mDataProcessing;

    private static WypSdk mInstance;

    private WypSdk() {
    }

    public static ICommonSDKIntf getInstance() {
        Log.d(TAG, "getInstance: ");
        if (mInstance == null) {
            Log.d(TAG, "getInstance: 1");
            mInstance = new WypSdk();
        }
        return mInstance;
    }

    @Override
    public boolean initialize(Context context) {
        if (!super.initialize(context)) {
            return false;
        }
        mWriteCommand = new WriteCommand(mBluetoothLeService);
        mDataProcessing = DataProcessing.getInstance();
        mDataProcessing.setHeartRateListener(this);
        mDataProcessing.setSleepListener(this);
        mDataProcessing.setStepListener(this);
        mDataProcessing.setDataCallback(this);
        return true;
    }

    @Override
    public void initService() {
        mBLEServiceOperate.startBindService(GlobalValue.FACTORY_WYP);
    }

    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {
        mWriteCommand.command1();  //simple
    }

    @Override
    public void findBand(int num) {

    }

    @Override
    public void setHeightAndWeight(int height, int weight, int var3) {

    }


    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time) {

    }

    @Override
    public void resetBracelet() {

    }


    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
    }


    @Override
    public void sendOffHookCommand() {
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

    @Override
    public int getAlarmNum() {
        return 0;
    }

    @Override
    public void noticeRealTimeData() {

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

    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {

    }

    @Override
    public void openFuncRemind(int type, boolean isOpen) {

    }

    @Override
    public void syncBraceletDataToDb() {

    }

    @Override
    public void refreshBleServiceUUID() {
        mBluetoothLeService.getCurrentUUID(GlobalValue.FACTORY_WYP);
    }


    @Override
    public void OnConnetionStateResult(boolean result, int status) {
        System.out.println("----------data: " + status);
        if (status == GlobalValue.DISCONNECT_MSG) {
            HardSdk.getInstance().setDevConnected(false);
            mIDataCallback.onResult(null, true, GlobalValue.DISCONNECT_MSG);
        } else if (status == GlobalValue.CONNECTED_MSG) {
            mIDataCallback.onResult(null, true, GlobalValue.CONNECTED_MSG);
            mIDataCallback.onResult(null, true, GlobalValue.SYNC_FINISH);
        } else if (status == GlobalValue.CONNECT_TIME_OUT_MSG) {
            mIDataCallback.onResult(null, true, GlobalValue.CONNECT_TIME_OUT_MSG);
        }
    }

    @Override
    public void onSleepChange() {
        //收到睡眠数据
    }


    @Override
    public void onHeartRateChange(int heartRate, int state) {
        if (iDataSubject != null) {
            iDataSubject.heartRateChanged(heartRate, state);
        }
    }

    @Override
    public void onStepChange(int steps, float distance, int calories) {

    }

    @Override
    public void onResult(Object data, boolean state, int flag) {
        super.onResult(data, state, flag);
    }

    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {

    }
}

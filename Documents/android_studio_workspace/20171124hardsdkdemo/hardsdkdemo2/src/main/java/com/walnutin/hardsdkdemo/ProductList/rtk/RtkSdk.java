package com.walnutin.hardsdkdemo.ProductList.rtk;

import android.content.Context;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.BleBaseSdk;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IRealDataSubject;
import com.walnutin.hardsdkdemo.utils.GlobalValue;

import java.util.Map;

/**
 * Created by chenliu on 2017/2/23.
 */

public class RtkSdk extends BleBaseSdk {

    final String TAG = "RtkSdk";
    private WriteCommand mWriteCommand;
    private DataProcessing mDataProcessing;

    private static RtkSdk mInstance;
    private RtkSdk(){
    };

    public static ICommonSDKIntf getInstance() {
        if(mInstance == null){
            mInstance = new RtkSdk();
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

        mBluetoothLeService.setICallback(this);
        return true;
    }

    @Override
    public void initService() {
        mBLEServiceOperate.startBindService(GlobalValue.FACTORY_RTK);
    }

    @Override
    public void refreshBleServiceUUID() {
        mBluetoothLeService.getCurrentUUID(GlobalValue.FACTORY_RTK);
    }

    @Override
    public void findBand(int num) {
        mWriteCommand.findBand(num);
    }

    @Override
    public void stopVibration() {

    }

    @Override
    public void resetBracelet() {

    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {

    }

    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {

    }

    @Override
    public void setUnLostRemind(boolean isOpen) {

    }

    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {

    }

    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time) {

    }

    @Override
    public void setHeightAndWeight(int height, int weight, int screenOnTime) {

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

    }

    @Override
    public void syncAllStepData() {
        mIDataCallback.onResult(null,true,GlobalValue.SYNC_FINISH);
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

    @Override
    public void setRealDataSubject(IRealDataSubject iDataSubject) {

    }

    @Override
    public void openFuncRemind(int type, boolean isOpen) {

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
    public void OnConnetionStateResult(boolean result, int status) {
        Log.d(TAG, "OnConnetionStateResult: status:"+status);
        if (status == GlobalValue.DISCONNECT_MSG) {
            Log.d(TAG, "OnConnetionStateResult: ");
            HardSdk.getInstance().setDevConnected(false);
            mIDataCallback.onResult(null,true,GlobalValue.DISCONNECT_MSG);
        } else if (status == GlobalValue.CONNECTED_MSG) {
            mIDataCallback.onResult(null,true,GlobalValue.CONNECTED_MSG);
        } else if (status == GlobalValue.CONNECT_TIME_OUT_MSG) {
            mIDataCallback.onResult(null,true,GlobalValue.CONNECT_TIME_OUT_MSG);
        }

    }

    @Override
    public void onResult(Object data, boolean state, int flag) {
       super.onResult(data,state,flag);
    }

    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {

    }

    @Override
    public void onHeartRateChange(int heartRate, int state) {

    }

    @Override
    public void onSleepChange() {

    }

    @Override
    public void onStepChange(int steps, float distance, int calories) {

    }
}

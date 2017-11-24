package com.walnutin.hardsdkdemo.ProductList;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.htsmart.wristband.WristbandApplication;
import com.veryfit.multi.nativeprotocol.ProtocolUtils;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.DeviceScanInterfacer;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IBleServiceInit;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardScanCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardSdkCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IRealDataSubject;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.utils.Config;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.UUIDParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenliu on 2017/3/14.
 */

public class HardSdk implements IDataCallback, IRealDataSubject, DeviceScanInterfacer, IBleServiceInit {

    private final static String TAG = HardSdk.class.getSimpleName();
    private static HardSdk mHardSdk;
    private ICommonSDKIntf mICommonImpl;
    private String mTempDeviceAddr;
    private boolean isConnecting;
    private Application mContext;

    private final int STEP_CHANGED = 10;
    private final int HEART_CHANGED = 11;
    private final int SLEEP_CHANGED = 12;

    private String mAccount = "visitor";
    private boolean isSyncing = false;
    private String mDeviceAddr;  //连接成功后赋值
    private boolean isDevConnected;  //赋值
    private String mGlobalFactoryName;
    private List<IHardScanCallback> mScanCallbackList = new ArrayList();
    private boolean isSyncingStart = false;
    private int syncTimeOutValue = 120000;


    private List<IHardSdkCallback> mIHardSdkCallbackList = new ArrayList<>();
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GlobalValue.DISCONNECT_MSG) {
                Log.d(TAG, "handleMessage:  removeCallbacks(timeoutTask);  1 ");
                mHandler.removeCallbacks(timeoutTask);
                isConnecting = false;
                isDevConnected = false;
            } else if (msg.what == GlobalValue.CONNECTED_MSG) {
                mHandler.removeCallbacks(timeoutTask);
                mDeviceAddr = mTempDeviceAddr;
                isConnecting = false;
                isDevConnected = true;
            } else if (msg.what == GlobalValue.CONNECT_TIME_OUT_MSG) {
                isConnecting = false;
                isDevConnected = false;
            } else if (msg.what == GlobalValue.SYNC_FINISH) {
                syncStartCountDownTimer.cancel();
                mHandler.removeCallbacks(syncTimeOutTask);
                isSyncingStart = false;
                syncBraceletDataToDb();
                Log.d(TAG, " 同步完成");
            } else if (msg.what == GlobalValue.STEP_SYNC_START) {
                isSyncingStart = true;
                Log.d(TAG, " 同步步数中");
                syncStartCountDownTimer.cancel();
                mHandler.removeCallbacks(syncTimeOutTask);
                mHandler.postDelayed(syncTimeOutTask, syncTimeOutValue);
            } else if (msg.what == GlobalValue.SLEEP_SYNCING) {
                Log.d(TAG, " 同步睡眠中");
            } else if (msg.what == GlobalValue.SYNC_TIME_OUT) {
                isSyncingStart = false;
            } else if (msg.what == GlobalValue.HEART_SYNCING) {
                Log.d(TAG, "同步心率中 ");
            } else if (msg.what == GlobalValue.INIT_BLESERVICE_OK) {
                Log.d(TAG, "初始化Ble服务成功 ");
                refreshBleServiceUUID();
                reConnect();
            } else if (msg.what == STEP_CHANGED) {
                if (msg.obj != null) {
                    StepInfos stepInfos = (StepInfos) msg.obj;
                    for (IHardSdkCallback hardSdkCallbackImpl : mIHardSdkCallbackList) {
                        if (hardSdkCallbackImpl != null) {
                            hardSdkCallbackImpl.onStepChanged(stepInfos.getStep(), stepInfos.getDistance(), stepInfos.getCalories(), stepInfos.isFinish_status());
                        }
                    }
                }
            } else if (msg.what == HEART_CHANGED) {
                if (msg.obj != null) {
                    HeartRateModel heartRateModel = (HeartRateModel) msg.obj;
                    for (IHardSdkCallback hardSdkCallbackImpl : mIHardSdkCallbackList) {
                        if (hardSdkCallbackImpl != null) {
                            hardSdkCallbackImpl.onHeartRateChanged(heartRateModel.getCurrentRate(), heartRateModel.getStatus());
                        }
                    }
                }
            } else if (msg.what == SLEEP_CHANGED) {
                if (msg.obj != null) {
                    SleepModel sleepModel = (SleepModel) msg.obj;
                    for (IHardSdkCallback hardSdkCallbackImpl : mIHardSdkCallbackList) {
                        if (hardSdkCallbackImpl != null) {
                            hardSdkCallbackImpl.onSleepChanged(sleepModel.getLightTime(), sleepModel.getDeepTime(), sleepModel.getAllDurationTime(), sleepModel.getSleepStatusArray(), sleepModel.getTimePointArray(), sleepModel.getDuraionTimeArray());
                        }
                    }
                }
            }
            for (IHardSdkCallback iHardSdkCallbackImpl : mIHardSdkCallbackList) {
                if (iHardSdkCallbackImpl != null) {
                    iHardSdkCallbackImpl.onCallbackResult(msg.what, true, msg.obj);
                }
            }
        }
    };


    private HardSdk() {

    }

    public static HardSdk getInstance() {
        if (mHardSdk == null) {
            mHardSdk = new HardSdk();
        }
        return mHardSdk;
    }


    public boolean initialize(Context context) {
        Log.d(TAG, "initialize: mICommonImpl:" + mICommonImpl);
        return mICommonImpl.initialize(context);
    }


    /**
     * 更新要调用的sdk对象
     *
     * @param factoryName
     * @param deviceName
     * @param deviceAddr
     * @param applicationContext
     * @return
     */
    public boolean refreshBleServiceUUID(String factoryName, String deviceName, String deviceAddr, Context applicationContext) {
        if (isConnecting) {
            return false;
        }
        mTempDeviceAddr = deviceAddr;
        ICommonSDKIntf commonSDKIntf = ProductFactory.getInstance().creatSDKImplByUUID(factoryName, applicationContext);
        setICommonImpl(commonSDKIntf);
        mICommonImpl.setOnServiceInitListener(this);
        Log.d(TAG, "refreshBleServiceUUID: run");
        if (!initialize(applicationContext)) {
            return false;
        }

        Log.d(TAG, "refreshBleServiceUUID: run1");
        setIDataCallBack(this);
        setRealDataSubject(this);

        if (mICommonImpl.isThirdSdk()) {
            refreshBleServiceUUID();
            reConnect();
        }
        return true;
    }


    public void refreshBleServiceUUID() {
        mICommonImpl.refreshBleServiceUUID();
    }

    public void reConnect() {
        if (!isConnecting) {
            isConnecting = true;
            mHandler.postDelayed(timeoutTask, Config.BLE_CONNECT_TIME_OUT_MILLISECOND);
            mICommonImpl.connect(mTempDeviceAddr);
        }
    }

    public void disconnect() {
//        mHandler.removeCallbacks(timeoutTask);
        mICommonImpl.disconnect();
    }


    public void readRssi() {
        mICommonImpl.readRssi();
    }


    public void findBand(int num) {
        mICommonImpl.findBand(num);
    }


    public void stopVibration() {
        mICommonImpl.stopVibration();
    }


    public void resetBracelet() {
        mICommonImpl.resetBracelet();
    }


    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {
        mICommonImpl.sendCallOrSmsInToBLE(number, smsType, contact, content);
    }


    public void sendQQWeChatTypeCommand(int type, String body) {
        mICommonImpl.sendQQWeChatTypeCommand(type, body);
    }


    public void setUnLostRemind(boolean isOpen) {
        mICommonImpl.setUnLostRemind(isOpen);
    }


    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {
        mICommonImpl.setAlarmClcok(flag, weekPeroid, hour, minitue, isOpen);
    }


    public void setSedentaryRemindCommand(int isOpen, int time) {
        mICommonImpl.sendSedentaryRemindCommand(isOpen, time);
    }


    public void setHeightAndWeight(int height, int weight, int screenOnTime) {
        mICommonImpl.setHeightAndWeight(height, weight, screenOnTime);
    }


    public void sendOffHookCommand() {
        mICommonImpl.sendOffHookCommand();
    }


    public void startRateTest() {
        mICommonImpl.startRateTest();
    }


    public void stopRateTest() {
        mICommonImpl.stopRateTest();
    }


    public void syncAllStepData() {
        syncStartCountDownTimer.start();
    }


    public void syncAllHeartRateData() {
        mICommonImpl.syncAllHeartRateData();
    }


    public void syncAllSleepData() {
        mICommonImpl.syncAllSleepData();
    }


    public Map<Integer, Integer> queryOneHourStep(String date) {
        return mICommonImpl.queryOneHourStep(date);
    }

    public List<HeartRateModel> queryOneDayHeartRate(String date) {
        Log.d(TAG, "queryOneDayHeartRate: 查询心率历史");
        return SqlHelper.instance().getOneDayHeartRateInfo(mAccount, date);
    }

    public StepInfos queryOneDayStep(String date) {
        Log.d(TAG, "queryOneDayStep: date:" + date);
        return SqlHelper.instance().getOneDateStep(mAccount, date);
    }

    public SleepModel queryOneDaySleepInfo(String Date) {
        return SqlHelper.instance().getOneDaySleepListTime(mAccount, Date);
    }

    public List<SleepModel> queryListSleepInfos(String startDate, String endDate) {
        return SqlHelper.instance().getSleepListByTime(mAccount, startDate, endDate);
    }


    public void syncBraceletDataToDb() {
        mICommonImpl.syncBraceletDataToDb();
    }


    public void setRealDataSubject(IRealDataSubject iDataSubject) {
        mICommonImpl.setRealDataSubject(iDataSubject);
    }


    public void openFuncRemind(int type, boolean isOpen) {
        mICommonImpl.openFuncRemind(type, isOpen);
    }


    public boolean isSupportHeartRate(String deviceName) {
        return mICommonImpl.isSupportHeartRate(deviceName);
    }


    public boolean isSupportBloodPressure(String deviceName) {
        return mICommonImpl.isSupportBloodPressure(deviceName);
    }


    public boolean isSupportUnLostRemind(String deviceName) {
        return mICommonImpl.isSupportUnLostRemind(deviceName);
    }


    public int getAlarmNum() {
        return mICommonImpl.getAlarmNum();
    }


    public void noticeRealTimeData() {
        mICommonImpl.noticeRealTimeData();
    }


    public void queryDeviceVesion() {
        mICommonImpl.queryDeviceVesion();
    }


    public boolean isVersionAvailable(String version) {
        return mICommonImpl.isVersionAvailable(version);
    }


    public void startUpdateBLE() {
        mICommonImpl.startUpdateBLE();
    }


    public void cancelUpdateBle() {
        mICommonImpl.cancelUpdateBle();
    }


    public void readBraceletConfig() {
        mICommonImpl.readBraceletConfig();
    }


    public void setIDataCallBack(IDataCallback iDataCallBack) {
        mICommonImpl.setIDataCallBack(iDataCallBack);
    }

    public void setICommonImpl(ICommonSDKIntf mICommonImpl) {
        this.mICommonImpl = mICommonImpl;
    }

    @Override
    public void onResult(Object data, boolean state, int flag) {
        Message message = mHandler.obtainMessage();
        message.what = flag;
        message.obj = data;
        mHandler.sendMessage(message);
    }

    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {

    }

    public void setHardSdkCallback(IHardSdkCallback mHardSdkCallbackImpl) {
        if (mHardSdkCallbackImpl != null && mIHardSdkCallbackList != null && !mIHardSdkCallbackList.contains(mHardSdkCallbackImpl)) {
            mIHardSdkCallbackList.add(mHardSdkCallbackImpl);
        }
    }

    public void removeHardSdkCallback(IHardSdkCallback mHardSdkCallbackImpl) {
        if (mHardSdkCallbackImpl != null && mIHardSdkCallbackList != null && mIHardSdkCallbackList.contains(mHardSdkCallbackImpl)) {
            mIHardSdkCallbackList.remove(mHardSdkCallbackImpl);
        }
    }


    private Runnable timeoutTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: timeoutTask");
            mHandler.sendEmptyMessage(GlobalValue.CONNECT_TIME_OUT_MSG);
        }
    };

    @Override
    public void stepChanged(int step, float distance, int calories, boolean finish_status) {
        Message message = mHandler.obtainMessage(STEP_CHANGED);
        StepInfos stepInfos = new StepInfos();
        stepInfos.setStep(step);
        stepInfos.setDistance(distance);
        stepInfos.setCalories(calories);
        stepInfos.setFinish_status(finish_status);
        message.obj = stepInfos;
        mHandler.sendMessage(message);
    }

    @Override
    public void sleepChanged(int lightTime, int deepTime, int sleepAllTime, int[] sleepStatusArray, int[] timePointArray, int[] duraionTimeArray) {
        SleepModel sleepModel = new SleepModel();
        sleepModel.setLightTime(lightTime);
        sleepModel.setDeepTime(deepTime);
        sleepModel.setAllDurationTime(sleepAllTime);
        sleepModel.setSleepStatusArray(sleepStatusArray);
        sleepModel.setTimePointArray(timePointArray);
        sleepModel.setDuraionTimeArray(duraionTimeArray);
        Message message = mHandler.obtainMessage(SLEEP_CHANGED);
        message.obj = sleepModel;
        mHandler.sendMessage(message);
    }

    @Override
    public void heartRateChanged(int rate, int status) {
        HeartRateModel heartRateModel = new HeartRateModel();
        heartRateModel.setCurrentRate(rate);
        heartRateModel.setStatus(status);
        Message message = mHandler.obtainMessage(HEART_CHANGED);
        message.obj = heartRateModel;
        mHandler.sendMessage(message);
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String mAccount) {
        this.mAccount = mAccount;
    }

    public String getDeviceAddr() {
        return mDeviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.mDeviceAddr = deviceAddr;
    }

    public boolean isDevConnected() {
        return isDevConnected;
    }

    public void setDevConnected(boolean devConnected) {
        isDevConnected = devConnected;
    }

    public String getGlobalFactoryName() {
        return mGlobalFactoryName;
    }

    public void setGlobalFactoryName(String globalFactoryName) {
        this.mGlobalFactoryName = globalFactoryName;
    }

    public boolean isSyncing() {
        return isSyncing;
    }

    public void setSyncing(boolean syncing) {
        isSyncing = syncing;
    }

    public void init(Application myApplication) {
        mContext = myApplication;
        ProtocolUtils.getInstance().init(myApplication);
        WristbandApplication.init(myApplication);
        com.yc.peddemo.sdk.BLEServiceOperate.getInstance(mContext).getBleService();

        BLECommonScan.getInstance(mContext).setDeviceScanInterfacer(this);
    }


    public void setHardScanCallback(IHardScanCallback callback) {
        if (callback != null && !mScanCallbackList.contains(callback)) {
            mScanCallbackList.add(callback);
        }
    }


    public void removeHardScanCallback(IHardScanCallback callback) {
        if (callback != null && mScanCallbackList.contains(callback)) {
            mScanCallbackList.remove(callback);
        }
    }

    @Override
    public void LeScanCallback(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String serviceUUIDString = UUIDParser.getInstance().getServiceUUIDString(scanRecord);
        Log.d(TAG, "LeScanCallback: deviceName:" + device.getName() + "  serviceUUIDString:" + serviceUUIDString);
        if (device.getName() != null) {
            if (serviceUUIDString != null) {
                String factoryNameByUUID = ModelConfig.getInstance().getFactoryNameByUUID(serviceUUIDString, device.getName());
                if (factoryNameByUUID != null) {
                    Log.d(TAG, "LeScanCallback: factoryNameByUUID:" + factoryNameByUUID);
                    for (IHardScanCallback callback : mScanCallbackList) {
                        Log.d(TAG, "LeScanCallback: callback:" + callback +"\n\n");
                        callback.onFindDevice(device, rssi, factoryNameByUUID, scanRecord);
                    }
                }
            }
        }
    }


    public boolean isSupportBle4_0() {
        return BLECommonScan.getInstance(mContext).isSupportBle4_0();
    }


    public void startScan() {
        BLECommonScan.getInstance(mContext).startScan();
    }


    public void stopScan() {
        BLECommonScan.getInstance(mContext).stopScan();
    }

    private CountDownTimer syncStartCountDownTimer = new CountDownTimer(60000, 2000) {

        @Override
        public void onTick(long l) {
            Log.d(TAG, "onTick: run");
            if (!isSyncingStart) {
                mICommonImpl.syncAllStepData();
            } else {
                this.cancel();
            }
        }

        @Override
        public void onFinish() {

        }
    };


    private Runnable syncTimeOutTask = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(GlobalValue.SYNC_TIME_OUT);
        }
    };

    public int getSyncTimeOutValue() {
        return syncTimeOutValue;
    }

    public void setSyncTimeOutValue(int syncTimeOutValue) {
        this.syncTimeOutValue = syncTimeOutValue;
    }


    public Context getContext() {
        return mContext;
    }


    public boolean isBleEnabled() {
        return BLECommonScan.getInstance(mContext).isBleEnabled();
    }


    @Override
    public void onBleServiceInitOK() {
        mHandler.sendEmptyMessage(GlobalValue.INIT_BLESERVICE_OK);
    }
}

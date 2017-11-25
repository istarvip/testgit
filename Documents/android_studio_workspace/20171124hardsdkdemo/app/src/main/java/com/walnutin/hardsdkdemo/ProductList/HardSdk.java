package com.walnutin.hardsdkdemo.ProductList;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.htsmart.wristband.WristbandApplication;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.DeviceScanInterfacer;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IBleServiceInit;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardScanCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardSdkCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IRealDataSubject;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.BloodPressure;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.R;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.UUIDParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by chenliu on 2017/3/14.
 * 界面开发人员搜索关键字"界面开发"，跳转到需要调用的方法说明。
 * 私有方法不需关注，为内部实现。
 */

public class HardSdk implements IDataCallback, IRealDataSubject, DeviceScanInterfacer, IBleServiceInit {

    private final static String TAG = HardSdk.class.getSimpleName();
    private static HardSdk mHardSdk;

    /**
     * 不同厂商的实现类
     */
    private ICommonSDKIntf mICommonImpl;
    private String mTempDeviceAddr;
    private boolean isConnecting;

    /**
     * 设备是否已连接
     */
    private boolean isDevConnected;
    /**
     * 是否是手动解绑的，否则会自动重连
     */
    private boolean isManualOff;  //手动解绑
    private boolean mIsContainsSleepData;  //同步时是否同步睡眠数据，当天第二次同步时，在之前已同步过睡眠数据后，可以不同步睡眠数据来节省时间。
    private Application mContext;

    private final int STEP_CHANGED = 10;
    private final int HEART_CHANGED = 11;
    private final int SLEEP_CHANGED = 12;
    private final int BLOODPRESSURE_CHANGED = 13;

    /**
     * 根据不同账号，区分保存数据，在用户登录后设置该值。
     * 如没有登录，则默认所有为visitor
     */
    private String mAccount = "visitor";

    /**
     * 同步过程中一般不允许进行设备设置操作
     */
    private boolean isSyncing = false;

    /**
     * 连接成功后保存当前已连接设备地址
     */
    private String mDeviceAddr;  //连接成功后赋值

    /**
     * 连接成功后保存当前已连接设备厂商名字,以此区分不同手环
     */
    private String mGlobalFactoryName;
    private List<IHardScanCallback> mScanCallbackList = new ArrayList();
    private boolean isSyncingStart = false;
    private int syncTimeOutValue = 120000;

    /**
     * 非sdk,连接成功后，需要等待服务初始化成功后才能进行其他操作。
     */
    public boolean isInitBleServcieOK;
    /**
     * 连接一次的超时时间。
     */
    private final long BLE_CONNECT_TIME_OUT_MILLISECOND = 30000;

    /**
     * 如果第一次断开，几秒钟内不告知app，静默重连，如果连接不成功后，则向上层通知连接失败。
     */
    private boolean isOneDisConnect = true;
    private int reConnectTime = 0;

    /**
     * 手环一般先逻辑上绑定，之后每次是逻辑上连接。解绑一个手环后才能进行绑定其他手环。
     * 如果是绑定操作，针对意外绑定不成功情况，静默重绑定3次。
     */
    private boolean mIsBindOpr;

    /**
     * 当静默重绑定3次或者连接失败静默重连失败1次后，向上层发送连接失败通知
     */
    private Runnable sendDoDisconnectMsg = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(GlobalValue.DISCONNECT_MSG);
        }
    };

    private List<IHardSdkCallback> mIHardSdkCallbackList = new ArrayList<>();


    /**
     * 针对底层回调 连接和同步情况进行预处理，之后返给上层
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GlobalValue.BIND_FAILED_MSG) {
                //绑定失败,内部重连
                Log.d(TAG, "绑定失败，开始重新绑定");
                isConnecting = false;
                mHandler.removeCallbacks(timeoutTask);
                reConnect();
                if (reConnectTime == 3) {
                    Log.d(TAG, "handleMessage: 3次绑定失败，发送连接失败通知");
                    isOneDisConnect = false;
                    setIsBindOpr(false);
                    mHandler.post(sendDoDisconnectMsg);
                } else if (reConnectTime < 3) {
                    reConnectTime++;
                    Log.d(TAG, "handleMessage: reConnectTime++:" + reConnectTime);
                }
            } else if (msg.what == GlobalValue.ALREDAY_BIND_MSG) {
                //手环已绑定，提示用户该手环已绑定。
                Log.d(TAG, "handleMessage: 手环已被绑定");
                Toast.makeText(getContext(), R.string.already_bind, Toast.LENGTH_LONG).show();
                mHandler.post(sendDoDisconnectMsg);
            } else if (msg.what == GlobalValue.DISCONNECT_MSG) {

                if (mIsBindOpr) {
                    Log.d(TAG, "handleMessage: 是绑定失败，准备重连");
                    mHandler.sendEmptyMessage(GlobalValue.BIND_FAILED_MSG);
                    return;
                }
                setIsBindOpr(false);

                Log.d(TAG, "handleMessage:  连接断开");
                mHandler.removeCallbacks(timeoutTask);
                isConnecting = false;
                isDevConnected = false;
                isSyncing = false;
                //如果是第一次连接中断，尝试重连，再次连接失败则向上发出断开命令。
                if (isOneDisConnect && isManualOff == false) {
                    Log.d(TAG, "handleMessage: 发送一次静默重连，3.5秒后通知断开");
                    reConnect();
                    isOneDisConnect = false;
                    mHandler.removeCallbacks(sendDoDisconnectMsg);
                    mHandler.postDelayed(sendDoDisconnectMsg, 3500);
                    return;
                }
            } else if (msg.what == GlobalValue.CONNECTED_MSG) {
                Log.d(TAG, "handleMessage: 连接成功");
                mHandler.removeCallbacks(timeoutTask);
                mHandler.removeCallbacks(sendDoDisconnectMsg);
                mDeviceAddr = mTempDeviceAddr;
                isConnecting = false;
                isDevConnected = true;
                isOneDisConnect = true;
                isManualOff = false;
                reConnectTime = 0;
                setIsBindOpr(false);
            } else if (msg.what == GlobalValue.CONNECT_TIME_OUT_MSG) {
                isConnecting = false;
                isDevConnected = false;
                setIsBindOpr(false);
            } else if (msg.what == GlobalValue.SYNC_FINISH) {
                syncStartCountDownTimer.cancel();
                mHandler.removeCallbacks(syncTimeOutTask);
                isSyncingStart = false;
                isSyncing = false;
                syncBraceletDataToDb();
                Log.d(TAG, " 同步完成");
            } else if (msg.what == GlobalValue.STEP_SYNC_START) {
                isSyncingStart = true;
                Log.d(TAG, " 同步步数中");
                syncStartCountDownTimer.cancel();
                mHandler.removeCallbacks(syncTimeOutTask);
                mHandler.postDelayed(syncTimeOutTask, syncTimeOutValue);
            } else if (msg.what == GlobalValue.STEP_FINISH) {
                Log.d(TAG, "onCallbackResult: 同步计步完成");
                syncAllHeartRateData();
            } else if (msg.what == GlobalValue.HEART_FINISH) {
                Log.d(TAG, "onCallbackResult: 同步心率完成");
                if (mIsContainsSleepData) {
                    syncAllSleepData();
                } else {
                    mHandler.sendEmptyMessage(GlobalValue.SYNC_FINISH);
                }
            } else if (msg.what == GlobalValue.SYNC_FINISH) {
                Log.d(TAG, "onCallbackResult: 同步完成");
            } else if (msg.what == GlobalValue.SLEEP_SYNCING) {
                Log.d(TAG, " 同步睡眠中");
            } else if (msg.what == GlobalValue.SYNC_TIME_OUT) {
                isSyncingStart = false;
                isSyncing = false;
            } else if (msg.what == GlobalValue.HEART_SYNCING) {
                Log.d(TAG, "同步心率中 ");
            } else if (msg.what == GlobalValue.INIT_BLESERVICE_OK) {
                Log.d(TAG, "初始化Ble服务成功 ");
                doRefreshBleServiceUUIDAndConnectBand();
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
            } else if (msg.what == BLOODPRESSURE_CHANGED) {
                if (msg.obj != null) {
                    BloodPressure sleepModel = (BloodPressure) msg.obj;
                    for (IHardSdkCallback hardSdkCallbackImpl : mIHardSdkCallbackList) {
                        if (hardSdkCallbackImpl != null) {
                            hardSdkCallbackImpl.onBloodPressureChanged(sleepModel.getDiastolicPressure(), sleepModel.getSystolicPressure(), sleepModel.status);
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

    public void setAutoHealthTest(boolean healthTest) {
        mICommonImpl.startAutoHeartTest(healthTest);
    }

    public boolean initialize(Context context) {
        Log.d(TAG, "initialize: mICommonImpl:" + mICommonImpl);
        return mICommonImpl.initialize(context);
    }


    /**
     * 在绑定手环的时候调用此方法进行绑定。
     * 统一通过disconnect()方法断开。
     * 界面开发关注使用此方法
     *
     * @param factoryName 厂商名称，通过搜索回调方法获取。格式为 GlobalValue中的值
     * @param deviceName  通过搜索回调方法获取
     * @param deviceAddr  通过搜索回调方法获取
     * @return true:成功    false：则表示不支持蓝牙4.0  或者初始化失败
     */
    public boolean bindBracelet(String factoryName, String deviceName, String deviceAddr) {
        setIsBindOpr(true);  //此方法只是在绑定时如果失败，静默重试3次，没有其他功能。
        return refreshBleServiceUUIDAndConnectBand(factoryName, deviceName, deviceAddr);
    }

    /**
     * 在再次连接已绑定手环的时候调用此方法进行连接。
     * 统一通过disconnect()方法断开。
     * 界面开发关注使用此方法
     *
     * @param factoryName 厂商名称，通过搜索回调方法获取。格式为 GlobalValue中的值
     * @param deviceName  通过搜索回调方法获取
     * @param deviceAddr  通过搜索回调方法获取
     * @return true:成功    false：则表示不支持蓝牙4.0  或者初始化失败
     */
    public boolean connectBracelet(String factoryName, String deviceName, String deviceAddr) {
        return refreshBleServiceUUIDAndConnectBand(factoryName, deviceName, deviceAddr);
    }


    /**
     * 更新要调用的sdk对象，意外断开再次重连使用reConnect()方法。
     *
     * @param factoryName
     * @param deviceName
     * @param deviceAddr
     * @return true:已开始尝试连接  false:不支持蓝牙4.0 或者ble服务器未初始化成功。
     */
    private boolean refreshBleServiceUUIDAndConnectBand(String factoryName, String deviceName, String deviceAddr) {
        if (isConnecting) {
            return false;
        }
        mTempDeviceAddr = deviceAddr;
        ICommonSDKIntf commonSDKIntf = ProductFactory.getInstance().creatSDKImplByUUID(factoryName, mContext);
        setICommonImpl(commonSDKIntf);
        mICommonImpl.setOnServiceInitListener(this);
        if (!initialize(mContext)) {
            return false;
        }
        setIDataCallBack(this);
        setRealDataSubject(this);

        if (mICommonImpl.isThirdSdk() || isInitBleServcieOK) {
            doRefreshBleServiceUUIDAndConnectBand();
            reConnect();
            return true;
        }
        return false;
    }

    private void doRefreshBleServiceUUIDAndConnectBand() {
        mICommonImpl.refreshBleServiceUUID();
    }


    /**
     * 意外断开，重连时使用此方法。再次打开app时，不使用此方法重连，只用于之前连接成功过，app未关闭之前的重连。
     * 界面开发关注使用此方法
     */
    public void reConnect() {
        if (!isConnecting) {
            isConnecting = true;
            Log.d(TAG, "reConnect: 开始超时倒计时:" + BLE_CONNECT_TIME_OUT_MILLISECOND);
            mHandler.postDelayed(timeoutTask, BLE_CONNECT_TIME_OUT_MILLISECOND);
            mHandler.sendEmptyMessage(GlobalValue.ALREDAY_LINKING);
            mICommonImpl.connect(mTempDeviceAddr);
        }
    }


    /**
     * 主动断开
     */
    public void disconnect() {
//        mHandler.removeCallbacks(timeoutTask);
        Log.d(TAG, "disconnect: 手动执行断开命令");
        isManualOff = true;
        mICommonImpl.disconnect();
    }


    /**
     * 有些手环需要主动调用查询蓝牙信号值
     * 现在一般用不到此功能。
     * todo 未确保所有手环都返回值。
     */
    public void readRssi() {
        mICommonImpl.readRssi();
    }


    /**
     * 查找手环功能，手环会震动.
     *
     * @param num
     */
    public void findBand(int num) {
        mICommonImpl.findBand(num);
    }

    /**
     * 有些手环需要主动查询电量
     * todo 未确保所有手环都返回
     */
    public void findBattery() {
        mICommonImpl.findBattery();
    }


    /**
     * 有些厂商需要主动调用停止手环震动方法。
     */
    public void stopVibration() {
        mICommonImpl.stopVibration();
    }

    /**
     * 恢复出厂设置功能
     */
    public void resetBracelet() {
        mICommonImpl.resetBracelet();
    }


    /**
     * 来电和短信的推送
     *
     * @param number  来电号码，如果能读取联系人姓名则优先显示联系人姓名
     * @param smsType 短信：GlobalValue.TYPE_MESSAGE_SMS;    来电：GlobalValue.TYPE_MESSAGE_PHONE;
     * @param contact 联系人姓名
     * @param content 短信内容
     */
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {
        mICommonImpl.sendCallOrSmsInToBLE(number, smsType, contact, content);
    }


    /**
     * 消息推送 type 参考 GlobalValue
     *
     * @param type
     * @param body
     */
    public void sendQQWeChatTypeCommand(int type, String body) {
        mICommonImpl.sendQQWeChatTypeCommand(type, body);
    }

    /**
     * 设置手环防丢功能开关。部分手环支持
     *
     * @param isOpen
     */
    public void setUnLostRemind(boolean isOpen) {
        mICommonImpl.setUnLostRemind(isOpen);
    }


    /**
     * 设置单个闹钟
     *
     * @param flag
     * @param weekPeroid
     * @param hour
     * @param minitue
     * @param isOpen
     */
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {
        mICommonImpl.setAlarmClcok(flag, weekPeroid, hour, minitue, isOpen, "", "");
    }


    /**
     * 从手环读取闹钟列表。部分手环厂商支持
     */
    public void readAlarmListFromBand() {
        mICommonImpl.readAlarmListFromBand();
    }

    /**
     * 统一设置闹钟列表
     *
     * @param clockList
     */
    public void setAlarmList(List clockList) {
        mICommonImpl.setAlarmList(clockList);
    }


    /**
     * 设置久坐提醒
     *
     * @param isOpen
     * @param time      间隔时间
     * @param startTime "09：00"格式
     * @param endTime   "17：00"格式
     * @param isDisturb 勿扰模式 开启后中午12点到14点手环不提醒（个别厂商手环支持）
     */
    public void setSedentaryRemindCommand(int isOpen, int time, String startTime, String endTime, boolean isDisturb) {
        mICommonImpl.sendSedentaryRemindCommand(isOpen, time, "09:00", "17:00", isDisturb);
    }

    /**
     * 设置身高体重性别年龄
     *
     * @param height
     * @param weight
     * @param age
     * @param sexform  "男"  "女"
     * @param birthday 格式："1970-10-19"
     */
    public void setHeightAndWeight(int height, int weight, int age, String sexform, String birthday) {
        mICommonImpl.setHeightAndWeight(height, weight, age, sexform, birthday);
    }

    /**
     * 亮屏时间
     *
     * @param screenOnTime 单位：秒
     */
    public void setScreenOnTime(int screenOnTime) {
        mICommonImpl.setScreenOnTime(screenOnTime);
    }


    /**
     * 针对优创意手环来电后的挂断操作，发送给手环停止显示来电提醒。  另外需调用stopVibration让手环停止震动
     */
    public void sendOffHookCommand() {
        mICommonImpl.sendOffHookCommand();
    }


    /**
     * 主动开始心率测试，会通过心率实时回调接口返回值
     */
    public void startRateTest() {
        mICommonImpl.startRateTest();
    }


    /**
     * 停止心率测试，配合上面使用
     */
    public void stopRateTest() {
        mICommonImpl.stopRateTest();
    }


    /**
     * 同步的主要开始方法，从计步开始同步。
     * 把手环内存储的多天计步同步到sdk数据库中
     */
    private void syncAllStepData() {
        //  syncStartCountDownTimer.start();
        mICommonImpl.syncAllStepData();
        isSyncing = true;
    }

    /**
     * 同步心率步骤。
     * 把手环内存储的多天心率同步到sdk数据库中
     */
    private void syncAllHeartRateData() {
        mICommonImpl.syncAllHeartRateData();
    }


    /**
     * 同步睡眠步骤。
     * 把手环内存储的多天睡眠同步到sdk数据库中
     */
    private void syncAllSleepData() {
        mICommonImpl.syncAllSleepData();
    }

    /**
     * 查询每小时步数   内部调用
     *
     * @param date 传入日期 格式："2017-09-10"
     * @return key:小时,24小时制   value：当前小时步数
     */
    public Map<Integer, Integer> queryOneHourStep(String date) {
        return mICommonImpl.queryOneHourStep(date);
    }

    /**
     * 内部调用
     *
     * @param date 传入日期 格式："2017-09-10"
     * @return HeartRateModel集合
     */
    public List<HeartRateModel> queryOneDayHeartRate(String date) {
        Log.d(TAG, "queryOneDayHeartRate: 查询心率历史");
        return SqlHelper.instance().getOneDayHeartRateInfo(mAccount, date);
    }

    /**
     * 根据日期查询某一天的总计步
     * 内部调用
     *
     * @param date 传入日期 格式："2017-09-10"
     * @return 参考StepInfos类
     */
    public StepInfos queryOneDayStep(String date) {
        Log.d(TAG, "queryOneDayStep: date:" + date);
        return SqlHelper.instance().getOneDateStep(mAccount, date);
    }

    /**
     * 查询同步后sdk内保存的某一天睡眠数据
     * 内部调用
     *
     * @param Date 传入日期 格式："2017-09-10"
     * @return SleepModel
     */
    public SleepModel queryOneDaySleepInfo(String Date) {
        return SqlHelper.instance().getOneDaySleepListTime(mAccount, Date);
    }

    /**
     * 查询同步后sdk内保存的某几天睡眠数据
     * 内部调用
     *
     * @param startDate 传入日期 格式："2017-09-10"
     * @param endDate   传入日期 格式："2017-09-10"
     * @return 多天睡眠集合
     */
    public List<SleepModel> queryListSleepInfos(String startDate, String endDate) {
        return SqlHelper.instance().getSleepListByTime(mAccount, startDate, endDate);
    }


    /**
     * 同步完成之后，把手环数据保存在sdk数据库内
     */
    private void syncBraceletDataToDb() {
        mICommonImpl.syncBraceletDataToDb();
    }


    /**
     * 设置实时数据回调接口实现类
     * 界面开发，设置传入此接口实现类，计步、心率、血压、睡眠数据如果实时有变化（例如主动开始测量心率、血压），会有回调。
     * 某些手环会实时回调计步变化（例如优创意），心率 血压一般是在界面主动开始测量后，实时返回接口会返回实时数据。
     * 睡眠是在同步完成后，如果睡眠数据有变化，则会通过回调接口上传至界面，供界面绘制当天睡眠图表用。
     *
     * @param iDataSubject
     */
    public void setRealDataSubject(IRealDataSubject iDataSubject) {
        mICommonImpl.setRealDataSubject(iDataSubject);
    }


    /**
     * 针对手环的功能列表，进行每一项开关设置。个别厂商手环用到此方法。
     *
     * @param type   GlobalValue中的
     * @param isOpen 开关
     */
    public void openFuncRemind(int type, boolean isOpen) {
        mICommonImpl.openFuncRemind(type, isOpen);


    }


    /**
     * 解绑操作。
     * 针对个别手环硬件绑定功能，需要时调用
     */
    public void unBindUser() {
        mICommonImpl.unBindUser();
    }


    /**
     * 同步开始方法
     * 界面开发调用此方法开始同步，同步完成、同步超时后会有回调,
     *
     * @param isContainsSleepData 是否同步睡眠数据，若当天已同步过睡眠，再次同步时可以不再同步睡眠节省时间。
     */
    public void syncAllDataFromBracelet(boolean isContainsSleepData) {
        //每个厂商sdk 先从计步开始同步，然后同步心率，血压，睡眠
//        mICommonImpl.startSyncBraceletData(isContainsSleepData);

        mIsContainsSleepData = isContainsSleepData;
        syncAllStepData();
    }


    /**
     * 当前已连接手环是否支持心率功能
     * 暂未实现
     *
     * @param deviceName
     * @return
     */
    public boolean isSupportHeartRate(String deviceName) {
        return mICommonImpl.isSupportHeartRate(deviceName);
    }

    /**
     * 当前已连接手环是否支持血压功能
     * 暂未实现
     *
     * @param deviceName
     * @return
     */
    public boolean isSupportBloodPressure(String deviceName) {
        return mICommonImpl.isSupportBloodPressure(deviceName);
    }

    /**
     * 当前已连接手环是否支持防丢功能
     * 暂未实现
     *
     * @param deviceName
     * @return
     */
    public boolean isSupportUnLostRemind(String deviceName) {
        return mICommonImpl.isSupportUnLostRemind(deviceName);
    }


    /**
     * 不同的厂商手环闹钟个数不一致
     * 未实现
     *
     * @return
     */
    public int getSupportAlarmNum() {
        return mICommonImpl.getSupportAlarmNum();
    }


    /**
     * 非实时返回步数的手环需要调用此方法主动返回实时数据
     * 未实现
     */
    public void noticeRealTimeData() {
        mICommonImpl.noticeRealTimeData();
    }


    /**
     * 查询当前手环固件版本
     * 流程说明欠缺。 todo
     */
    public void queryDeviceVesion() {
        mICommonImpl.queryDeviceVesion();
    }


    public boolean isVersionAvailable(String version) {
        return mICommonImpl.isVersionAvailable(version);
    }


    public void syncNoticeConfig() {
        mICommonImpl.syncNoticeConfig();
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


    /**
     * 和底层交互的回调设置
     *
     * @param iDataCallBack
     */
    private void setIDataCallBack(IDataCallback iDataCallBack) {
        mICommonImpl.setIDataCallBack(iDataCallBack);
    }

    /**
     * 设置不同厂商实现类，如果为null将出现问题
     *
     * @param mICommonImpl
     */
    public void setICommonImpl(ICommonSDKIntf mICommonImpl) {
        this.mICommonImpl = mICommonImpl;
    }

    /**
     * 底层上传回来的标志
     * 内部调用
     *
     * @param data
     * @param state
     * @param flag
     */
    @Override
    public void onResult(Object data, boolean state, int flag) {
        Message message = mHandler.obtainMessage();
        message.what = flag;
        message.obj = data;
        mHandler.sendMessage(message);
    }

    /**
     * 同步过程中的一些过程回调
     * 内部调用
     *
     * @param data
     * @param state
     * @param status
     */
    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {

    }

    /**
     * 通过此方法加入界面回调，当sdk有回调时通知界面
     * 界面开发使用此方法，在需要回调的界面注册后监听回调，参考Demo中MainActivity的使用
     *
     * @param mHardSdkCallbackImpl
     */
    public void setHardSdkCallback(IHardSdkCallback mHardSdkCallbackImpl) {
        if (mHardSdkCallbackImpl != null && mIHardSdkCallbackList != null && !mIHardSdkCallbackList.contains(mHardSdkCallbackImpl)) {
            mIHardSdkCallbackList.add(mHardSdkCallbackImpl);
        }
    }

    /**
     * 当界面不再需要回调时，注销回调
     * 界面开发使用此方法，在界面取消监听回调
     *
     * @param mHardSdkCallbackImpl
     */
    public void removeHardSdkCallback(IHardSdkCallback mHardSdkCallbackImpl) {
        if (mHardSdkCallbackImpl != null && mIHardSdkCallbackList != null && mIHardSdkCallbackList.contains(mHardSdkCallbackImpl)) {
            mIHardSdkCallbackList.remove(mHardSdkCallbackImpl);
        }
    }


    /**
     * 连接超时任务
     */
    private Runnable timeoutTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: timeoutTask");
            mHandler.sendEmptyMessage(GlobalValue.CONNECT_TIME_OUT_MSG);
        }
    };


    /**
     * 实时计步数值内部返回
     *
     * @param step          步数
     * @param distance      距离     单位： 公里
     * @param calories      卡路里    单位：千卡（还未确定）
     * @param finish_status 暂时无用
     */
    @Override
    public void stepChanged(int step, float distance, int calories, boolean finish_status) {
        Message message = mHandler.obtainMessage(STEP_CHANGED);
        StepInfos stepInfos = new StepInfos();
        stepInfos.setStep(step);
        stepInfos.setFinish_status(finish_status);
        stepInfos.setDistance(distance);
        stepInfos.setCalories(calories);
        message.obj = stepInfos;
        mHandler.sendMessage(message);
    }

    /**
     * 当睡眠变化时，内部返回
     *
     * @param lightTime        浅睡
     * @param deepTime         深睡
     * @param sleepAllTime     睡眠总时长
     * @param sleepStatusArray 睡眠状态集合  0深睡 1浅睡 2清醒
     * @param timePointArray   结束时间点集合  距离当天0点0分的分钟数， 例如80代表1点20分。
     * @param duraionTimeArray 状态时长集合  30表示对应的另外2个集合中状态时长为30分钟。向前30分钟。
     */
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


    /**
     * 心率变化时内部回调
     *
     * @param rate
     * @param status
     */
    @Override
    public void heartRateChanged(int rate, int status) {
        HeartRateModel heartRateModel = new HeartRateModel();
        heartRateModel.setCurrentRate(rate);
        heartRateModel.setStatus(status);
        Message message = mHandler.obtainMessage(HEART_CHANGED);
        message.obj = heartRateModel;
        mHandler.sendMessage(message);
    }


    /**
     * 血压变化时的内部回调
     * 未实现 todo
     *
     * @param sbp
     * @param dpb
     */
    @Override
    public void bloodPressureChanged(int sbp, int dpb, int status) {
        BloodPressure bpMode = new BloodPressure();
        bpMode.setDiastolicPressure(sbp);
        bpMode.setSystolicPressure(dpb);
        bpMode.status = status;
        Message message = mHandler.obtainMessage(BLOODPRESSURE_CHANGED);
        message.obj = bpMode;
        mHandler.sendMessage(message);
    }


    /**
     * 所有有关当前账号的信息都通过这里保存和取，保存数据都会根据这里的账号做区分。
     *
     * @return 当前登录和使用的账号
     */
    public String getAccount() {
        return mAccount;
    }

    /**
     * 所有有关当前账号的信息都通过这里保存，保存数据都会根据这里的账号做区分。
     */
    public void setAccount(String mAccount) {
        this.mAccount = mAccount;
    }


    /**
     * 获取当前已连接设备的mac地址
     * 未测试
     *
     * @return
     */
    public String getDeviceAddr() {
        return mDeviceAddr;
    }


    /**
     * 保存当前已连接设备的mac地址
     * 未实现
     *
     * @param deviceAddr
     */
    public void setDeviceAddr(String deviceAddr) {
        this.mDeviceAddr = deviceAddr;
    }


    /**
     * 所有有关当前手环是否已连接的状态都通过这里判断
     *
     * @return
     */
    public boolean isDevConnected() {
        return isDevConnected;
    }

    /**
     * 设置当前手环是否已在连接状态
     * 不需要调用，此属性已经跟随底层回调自动修改。
     *
     * @param devConnected
     */
    private void setDevConnected(boolean devConnected) {
        isDevConnected = devConnected;
    }

    /**
     * 获取当前已连接手环的厂商名称
     * 未测试
     *
     * @return GlobalValue中的厂商名称
     */
    public String getGlobalFactoryName() {
        return mGlobalFactoryName;
    }

    /**
     * 设置当前已连接手环的厂商名称
     * 勿调用
     *
     * @param globalFactoryName
     */
    public void setGlobalFactoryName(String globalFactoryName) {
        this.mGlobalFactoryName = globalFactoryName;
    }

    /**
     * 判断当时是否处于同步中，如果在同步中，则不允许进行开关设置。
     * 界面开发使用此方法判断是否在同步流程中。
     *
     * @return true：在同步中    false：不在同步中
     */
    public boolean isSyncing() {
        return isSyncing;
    }

    /**
     * 设置是否在同步中
     * 内部已改变isSyncing值，不需调用此方法
     *
     * @param syncing
     */
    private void setSyncing(boolean syncing) {
        isSyncing = syncing;
    }

    /**
     * 此方法需要在application中调用，进行sdk初始化。否则sdk无法正常运行
     * 界面开发需注意
     *
     * @param myApplication
     */
    public void init(Application myApplication) {
        mContext = myApplication;
        WristbandApplication.init(myApplication);//初始化fitcloud
        BLECommonScan.getInstance(mContext).setDeviceScanInterfacer(this);
        com.yc.pedometer.sdk.BLEServiceOperate.getInstance(getContext()).getBleService();  //初始化优创意
    }


    /**
     * 设置搜索蓝牙设备的回调，当搜索到新设备时，会进行回调
     * 界面开发需在搜索页面设置接口实现类，监听回调。
     *
     * @param callback 回调实现类
     */
    public void setHardScanCallback(IHardScanCallback callback) {
        if (callback != null && !mScanCallbackList.contains(callback)) {
            mScanCallbackList.add(callback);
        }
    }


    /**
     * 删除搜索蓝牙设备的回调，
     * 界面开发在搜索页面不需要回调时可以删除回调。
     *
     * @param callback 回调实现类
     */
    public void removeHardScanCallback(IHardScanCallback callback) {
        if (callback != null && mScanCallbackList.contains(callback)) {
            mScanCallbackList.remove(callback);
        }
    }

    /**
     * 搜索到的设备的过滤，通过16位uuid过滤出只符合moduleConfig类中添加的uuid16的值，
     * 如果需要屏蔽过滤，则修改此方法。
     *
     * @param device     系统回调的device
     * @param rssi       线损值 也就是信号强度
     * @param scanRecord 包含uuid等信息，用来分析和过滤使用
     */
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
                        Log.d(TAG, "LeScanCallback: callback:" + callback + "\n\n");
                        callback.onFindDevice(device, rssi, factoryNameByUUID, scanRecord);
                    }
                }
            }
        }
    }


    /**
     * 未知，未测试
     * 可能是翻腕亮屏
     *
     * @param isOpen
     */
    public void setPalmingState(boolean isOpen) {
        mICommonImpl.sendPalmingStatus(isOpen);
    }

    /**
     * 未测试
     *
     * @return
     */
    public boolean isSupportBle4_0() {
        return BLECommonScan.getInstance(mContext).isSupportBle4_0();
    }


    /**
     * 搜索页面调用此方法开始搜索，并通过回调监听新搜索到的设备
     */
    public void startScan() {
        BLECommonScan.getInstance(mContext).startScan();
    }


    /**
     * 搜索页面需要停止搜索时调用。请在页面关闭前记得调用。
     */
    public void stopScan() {
        BLECommonScan.getInstance(mContext).stopScan();
    }


    /**
     * 同步超时计时器
     */
    private CountDownTimer syncStartCountDownTimer = new CountDownTimer(60000, 7000) {

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


    /**
     * 同步超时任务
     */
    private Runnable syncTimeOutTask = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(GlobalValue.SYNC_TIME_OUT);
        }
    };

    /**
     * 未测试
     *
     * @return
     */
    public int getSyncTimeOutValue() {
        return syncTimeOutValue;
    }

    /**
     * 未测试
     *
     * @param syncTimeOutValue
     */
    public void setSyncTimeOutValue(int syncTimeOutValue) {
        this.syncTimeOutValue = syncTimeOutValue;
    }


    /**
     * 需要context可以从这里取
     *
     * @return
     */
    public Context getContext() {
        return mContext;
    }


    /**
     * 判断当前蓝牙是否打开
     */
    public boolean isBleEnabled() {
        return BLECommonScan.getInstance(mContext).isBleEnabled();
    }


    /**
     * 当底层蓝牙服务开启成功之后，回调此方法。
     */
    @Override
    public void onBleServiceInitOK() {
        mHandler.sendEmptyMessage(GlobalValue.INIT_BLESERVICE_OK);
        isInitBleServcieOK = true;
    }


    public void onDestory() {
        if (mICommonImpl != null) {
            mICommonImpl.disconnect();
        }
    }


    //暂时无用
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 在某些情况下，清除连接超时的任务
     */
    public void removeTimeOutTimer() {
        Log.d(TAG, "removeTimeOutTimer: run");
        mHandler.removeCallbacks(timeoutTask);
    }

    /**
     * 当绑定操作时，调用此方法，这样当绑定失败时，可以静默在后台进行3次重连，
     * 如果重连3次不成功，才向上层发送连接失败。
     *
     * @param isBindOpr
     */
    private void setIsBindOpr(boolean isBindOpr) {
        mIsBindOpr = isBindOpr;
    }


    /**
     * 针对个别厂商手环 读取手环的消息通知配置，
     */
    public void readNoticeConfigFromBand() {
        mICommonImpl.readNoticeConfigFromBand();
    }

    //todo 查找和实现固件升级统一接口
    //todo 优创意新型号，sdk方法消息推送和来电不支持，需调试
    //todo 多次开始同步需要调整
    //todo 查询方法需要在sqlHelper里面调用，尽量在这里封装

    /**
     * 针对 维亿魄 手环 需要 读取闹钟列表
     */
    public void readClock() {
        mICommonImpl.readClock();
    }

    /**
     * @return 是否支持拍照模式
     */
    public boolean isSupportTakePhoto() {
        return mICommonImpl.isSupportTakePhoto();
    }

    /**
     * @return 是否支持查找手环 功能
     */
    public boolean isSupportFindBand() {
        return mICommonImpl.isSupportFindBand();
    }

    /**
     * @return 闹钟可以单个 和批量设置、 判断是否需要进行批量设置
     */
    public boolean isNeedAlarmListSetting() {
        return mICommonImpl.isNeedAlarmListSetting();
    }

    /**
     * @return 是否支持抬手亮屏
     */
    public boolean isSupportWristScreen() {
        return mICommonImpl.isSupportWristScreen();
    }

    /**
     * 返回支持 消息提醒功能     ///wechat 位数 0 qq 1 facebookmsg 2 whatspp 3 twitter 4 skype 5 Line 6 linkedin 7  Instagram 8 snapchat 9
     * //1代表支持，0代表不支持  1111111111 代表 全       支持  1000000011 代表支持 WeChat、Instagram 、snapchat
     */
    public String getSupportNoticeFunction() {
        return mICommonImpl.getSupportNoticeFunction();

    }

    /**
     * @return 是否支持久坐 在某段时间内设置
     */
    public boolean isSupportSetSedentarinessTime() {
        return mICommonImpl.isSupportSetSedentarinessTime();

    }

    /**
     * // 是否支持 查找手机
     *
     * @return
     */
    public boolean isFindPhone() {
        return mICommonImpl.isFindPhone();
    }

    /**
     * @return 是否支持 手环横竖屏 操作
     */
    public boolean isSupportControlHVScreen() {
        return mICommonImpl.isSupportControlHVScreen();
    }

    /**
     * //横竖屏切换
     *
     * @param type
     * @return
     */
    public void setControlHVScreen(int type) {
        mICommonImpl.setControlHVScreen(type);
    }

    /**
     * 开始测量血压
     */
    public void startBloodTest() {

    }

    /**
     * 结束血压测量
     */
    public void stopBloodTest() {

    }
}

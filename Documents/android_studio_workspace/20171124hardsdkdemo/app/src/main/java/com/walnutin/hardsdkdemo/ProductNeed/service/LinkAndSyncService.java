//package com.walnutin.hardsdkdemo.ProductNeed.service;
//
//import android.app.AlarmManager;
//import android.app.Service;
//import android.bluetooth.BluetoothAdapter;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.walnutin.hardsdkdemo.MyApplication;
//import com.walnutin.hardsdkdemo.ProductList.HardSdk;
//import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardSdkCallback;
//import com.walnutin.hardsdkdemo.ProductNeed.entity.NoticeInfo;
//import com.walnutin.hardsdkdemo.R;
//import com.walnutin.hardsdkdemo.utils.Config;
//import com.walnutin.hardsdkdemo.utils.DeviceSharedPf;
//import com.walnutin.hardsdkdemo.utils.GlobalValue;
//import com.walnutin.hardsdkdemo.utils.MySharedPf;
//import com.walnutin.hardsdkdemo.utils.TimeUtil;
//import com.walnutin.hardsdkdemo.utils.Utils;
//import com.walnutin.hardsdkdemo.utils.WeekUtils;
//
//import java.util.List;
//
///**
// * 作者：MrJiang on 2017/4/10 11:25
// */
//public class LinkAndSyncService extends Service implements IHardSdkCallback {
//
//    final String TAG = LinkAndSyncService.class.getSimpleName();
//
//    MySharedPf mySharedPf;
//    private Context mContext;
//    boolean isAttempLinking = false;  // 尝试连接
//    LinkTime linkTime;
//    boolean stepSyncStarted = false;
//    String lastSyncTime = "";
//    private String toastString;
//    int rssi_value;
//    int lostIndex = 0;
//    DeviceOtherInfoManager deviceOtherInfoManager;
//    private int FINISH_TIME = 1000 * 60 * 20;  //倒计时 十分钟
//    private int ONE_TRY = 1000 * 9;         // 每5秒钟尝试一次
//    private boolean isStartingLinking;
//    NoticeInfo noticeInfo;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        mySharedPf = MySharedPf.getInstance(getApplicationContext());
//        MyApplication.tmpDeviceName = mySharedPf.getString("device_name");
//        MyApplication.tmpDeviceAddr = mySharedPf.getString("device_address");
//        MyApplication.tmpFactoryName = mySharedPf.getString("device_factory");
//        deviceOtherInfoManager = DeviceOtherInfoManager.getInstance(getApplicationContext());
//        linkTime = new LinkTime(FINISH_TIME, ONE_TRY);
//        checkSyncStatus = new SycnStatusListener(1000 * 80, 3000);
//        noticeInfo = NoticeInfoManager.getInstance(getApplicationContext()).getLocalNoticeInfo();
//        clockManager = ClockManager.getInstance(getApplicationContext());
//        EventBus.getDefault().register(this);
//        HardSdk.getInstance().setHardSdkCallback(this);
//
//        registerReceiver();
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        if (MyApplication.tmpDeviceAddr != null && MyApplication.isDevConnected == false) {
//            isStartingLinking = true;
//            isAttempLinking = false;
//            Log.d(TAG, "onStartCommand: linkTime.cancel()1");
//            linkTime.cancel();
//            if (MyApplication.tmpFactoryName != null && !MyApplication.tmpFactoryName.equals("null")) {
//                refreshSdkByUUID(MyApplication.tmpFactoryName, MyApplication.tmpDeviceName, MyApplication.tmpDeviceAddr);
//            }
//        }
//
//        MyApplication.isFromNLService = intent.getBooleanExtra("isFromNLService", false);
//
//        if (MyApplication.isDevConnected == true) {
//            if (mySharedPf.getBoolean("isFirstRunApp", true)) {
//                Log.d(TAG, "onStartCommand: do connectOper();3");
//                connectOper();
//            } else if (!MyApplication.isFromNLService) {
//                doSyncBraceletDev();
//            }
//        }
//
//        return super.onStartCommand(intent, flags, startId);
//
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    public void refreshSdkByUUID(String factoryName, String deviceName, String deviceAddr) {
//        //   Log.d("myresult", "refreshSdkByUUID: HardSdk.getInstance()" + HardSdk.getInstance());
//        if (!HardSdk.getInstance().refreshBleServiceUUIDAndConnectBand(factoryName, deviceName, deviceAddr, getApplicationContext())) {
////            stopSelf();
//            // Toast.makeText(this, "初始化失败", Toast.LENGTH_LONG).show();
//        }
//
//
//    }
//
//    private void registerReceiver() {
//
//        IntentFilter mFilter2 = new IntentFilter();
//        mFilter2.addAction(Config.NOTICE_ACTION);
//        mFilter2.addAction(Config.NOTICE_MSG_ACTION);
//        mFilter2.addAction(Config.NOTICE_PHONE_ACTION);
//        registerReceiver(mDeviceNoticeReceiver, mFilter2);
//        registerReceiver(mReceiver, makeFilter());
//    }
//
//
//    String contact;
//    boolean isPhone = false;
//    boolean isOther = false; // 暂代指红包模式
//    private BroadcastReceiver mDeviceNoticeReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //     System.out.println("");
//            if (HardSdk.getInstance() == null) {
//                return;
//            }
//            String action = intent.getAction();
//            if (action.equals(Config.NOTICE_ACTION)) {
//                String type = intent.getStringExtra("type");
//                Log.d(TAG, "onReceive: type:" + type);
//                if (type.equals("msg")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_SMS, content);
//                } else if (type.equals("WeChat")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    System.out.println("content:" + content);
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_WECHAT, content);
//                } else if (type.equals("qq")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_QQ, content);
//                } else if (type.equals("facebook")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_FACEBOOK, content);
//                } else if (type.equals("whatsapp")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_WHATSAPP, content);
//                } else if (type.equals("twitter")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_TWITTER, content);
//                } else if (type.equals("linkedin")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_LINKEDIN, content);
//                } else if (type.equals("other")) {
//                    String content = intent.getStringExtra("content");
//                    if (content == null) {
//                        return;
//                    }
//                    HardSdk.getInstance().sendQQWeChatTypeCommand(GlobalValue.TYPE_MESSAGE_OTHERSMS, content);
//                } else if (type.equals("redpacket")) {
//                    if (intent.getStringExtra("content") != null) {
//                        contact = intent.getStringExtra("content");  //
//                        isOther = true;
//                        isPhone = false;
//                        HardSdk.getInstance().sendCallOrSmsInToBLE("18888888888", GlobalValue.TYPE_MESSAGE_SMS, "", contact);
//                        mHandler.removeMessages(GlobalValue.RED_PACTAGE);
//                        Message msg = new Message();
//                        msg.what = GlobalValue.RED_PACTAGE;
//                        msg.obj = intent.getStringExtra("contact");
//                        mHandler.sendMessageDelayed(msg, 2000);
//                    }
//
//                }
//            } else if (action.equals(Config.NOTICE_MSG_ACTION)) {
//                String contactName = intent.getStringExtra("contacts");
//                //     System.out.println("contacts: " + contact);
//                isOther = false;
//                if (contactName == null) {
//                    return;
//                }
//                contact = Utils.getContactNameFromPhoneNum(getApplicationContext(), contactName);
//                isPhone = false;
//                String content = intent.getStringExtra("content");
//                if (content == null) {
//                    return;
//                }
//                HardSdk.getInstance().sendCallOrSmsInToBLE(contactName, GlobalValue.TYPE_MESSAGE_SMS, contact, content);
//            } else if (action.equals(Config.NOTICE_PHONE_ACTION)) {
//                String state = intent.getStringExtra("state");
//                if (state.equals("CALL_STATE_RINGING")) {
//                    String contactName = intent.getStringExtra("contacts");
//                    isOther = false;
//                    if (contactName == null) {
//                        return;
//                    }
//                    contact = Utils.getContactNameFromPhoneNum(getApplicationContext(), contactName);
//                    System.out.println("contact: " + contact);
//                    isPhone = true;
//                    HardSdk.getInstance().sendCallOrSmsInToBLE(contactName, GlobalValue.TYPE_MESSAGE_PHONE, contact, null);
//                } else if (state.equals("CALL_STATE_OFFHOOK")) {
//                    HardSdk.getInstance().sendOffHookCommand();
//                }
//            }
//        }
//    };
//
//
//    @Subscribe
//    public void syncBraceletDev(StepChangeNotify.SyncData sync) {
//        doSyncBraceletDev();
//
//    }
//
//    private void doSyncBraceletDev() {
//        Utils.showToast(getApplicationContext(), getString(R.string.startSync));
//        EventBus.getDefault().post(new SyncStatus(true));  // 同步开始
//        isSyncTimeOut = false;
//        MyApplication.isSyncing = true;
//        stepSyncStarted = false;
//        DeviceSharedPf.getInstance(getApplicationContext()).setString("lastsyncSleepTime" + MyApplication.deviceAddr,
//                "2010"); // 设置同步日期
//
//        lastSyncTime = "2010"; // 设置同步日期
//        HardSdk.getInstance().syncAllStepData();
//        //    checkSyncStatus.cancel();
//        //   checkSyncStatus.start();
//        mHandler.removeCallbacks(findBracelet);
//    }
//
//    boolean isFailed = false;
//
//    @Override
//    public void onCallbackResult(int flag, boolean state, Object data) {
//        switch (flag) {
//            case GlobalValue.STEP_SYNC_START:
//                stepSyncStarted = true;
//                break;
//            case GlobalValue.HEART_FINISH:
//                if (lastSyncTime.equals(TimeUtil.getCurrentDate())) { //是同一天
//                    onCallbackResult(GlobalValue.SYNC_FINISH, true, null);
//                    return;
//                }
//                HardSdk.getInstance().syncAllSleepData(); //开始同步睡眠
//                break;
//            case GlobalValue.STEP_FINISH:
//                if (MyApplication.isSyncing == false) {  // 同步完成，规避重复完成
//                    return;
//                }
//                stepSyncStarted = true;
//                HardSdk.getInstance().syncAllHeartRateData();
//                break;
//            case GlobalValue.SYNC_FAILED:
//                if (MyApplication.isSyncing == false) {  // 同步失败，规避重复完成
//                    return;
//                }
//                isFailed = true;
//
//                syncFinishedOper();
//                break;
//            case GlobalValue.SYNC_FINISH:
//
//                if (MyApplication.isSyncing == false) {  // 同步完成，规避重复完成
//                    return;
//                }
//                isFailed = false;
//
//                syncFinishedOper();
//                break;
//            case GlobalValue.READ_RSSI_VALUE:    //得到线损值
//                if (data == null) {
//                    return;
//                }
//                rssi_value = (int) data;
//                System.out.println("rssi_value:" + rssi_value);
//                if (deviceOtherInfoManager.isUnLost() && rssi_value <= -87) { // 智能防丢
//                    lostIndex++;
//
//                } else {
//                    lostIndex = 0;
//                }
//                break;
//            //连接超时：
//            case GlobalValue.CONNECT_TIME_OUT_MSG:
//                Log.d(TAG, "handleMessage: CONNECT_TIME_OUT_MSG + MyApplication.isManualOff:" + MyApplication.isManualOff);
//                if (MyApplication.isManualOff) {
//                    toastString = getString(R.string.braceletTimeOutnoreconnect);
//                } else {
//                    toastString = getString(R.string.braceletTimeOut);
//                }
//            case GlobalValue.DISCONNECT_MSG: //连接断开
//                Log.d(TAG, "handleMessage: DISCONNECT_MSG isAttempLinking" + isAttempLinking);
//                isStartingLinking = false;
//                if (flag == GlobalValue.DISCONNECT_MSG) {
//                    toastString = getString(R.string.braceletbreak);
//                }
//                if (isAttempLinking == false) {
//                    Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_SHORT).show();
//                }
//                if (lostIndex >= 3) {
//                    if (lostIndex >= 3) {
//                        Intent intent = new Intent("lost_alarm");
//                        MyApplication.getContext().sendBroadcast(intent);
//                    }
//                }
//                lostIndex = 0;
//                disconnectOper();
//                break;
//            case GlobalValue.CONNECTED_MSG:
//                //    System.out.println(" 同步 已连接： " + DeviceLinkService.this);
//                //      System.out.println("同步已连接：lastSyncTime:" + lastSyncTime);
//                Log.d(TAG, "handleMessage: CONNECTED_MSG");
//                Utils.showToast(getApplicationContext(), getString(R.string.startSync));
//                connectOper();
//                //   mHandler.removeCallbacksAndMessages(null);
//
//                break;
//        }
//    }
//
//    private void connectOper() {
//        MyApplication.isManualOff = false;
//        MyApplication.isDevConnected = true;
//        mySharedPf.setString("device_name", MyApplication.tmpDeviceName);
//        mySharedPf.setString("device_address", MyApplication.tmpDeviceAddr);
//        mySharedPf.setString("device_factory", MyApplication.tmpFactoryName);
//        MyApplication.deviceAddr = MyApplication.tmpDeviceAddr;
//        MyApplication.deviceName = MyApplication.tmpDeviceName;
//        lastSyncTime = DeviceSharedPf.getInstance(getApplicationContext()).getString("lastsyncSleepTime" + MyApplication.deviceAddr, "2010");
//        linkTime.cancel();
//        isAttempLinking = false;
//        if (MyApplication.isFromNLService == true) { // 从消息通知栏  同步 而来
//            MyApplication.isSyncing = false;
//            return;
//        }
//
//        MyApplication.isSyncing = true;  // 开始同步
//        Log.d(TAG, "connectOper: linkTime.cancel()2");
//        stepSyncStarted = false;
//        HardSdk.getInstance().syncAllStepData();
//        checkSyncStatus.cancel();
//        checkSyncStatus.start();  // 检测同步状态
//        EventBus.getDefault().post(new SyncStatus(true));  // 同步开始
//
////        if (deviceOtherInfoManager.isUnLost()) {
////            if (HardSdk.getInstance() != null) {
////                mHandler.removeCallbacks(findBracelet);
////                mHandler.postDelayed(findBracelet, 20000);
////            }
////        }
//    }
//
//    private void disconnectOper() {
//        MyApplication.isDevConnected = false;
//        if (MyApplication.isManualOff == true) {
//            Log.d(TAG, "disconnectOper: 是手动断开");
//        } else {
//            if (isAttempLinking == false) { // 是不是在尝试连接
//                Log.d(TAG, "connectOper: linkTime.cancel()3");
//                linkTime.cancel();
//                linkTime.start();
//            }
//        }
//        MyApplication.deviceName = "";
//        MyApplication.deviceAddr = "";
//        configHandler.removeCallbacks(null);
//        checkSyncStatus.cancel();
//        MyApplication.isDevConnected = false;
//        EventBus.getDefault().post(new SyncStatus(false));  // 同步结束
//        mHandler.removeCallbacksAndMessages(null); // 取消Handle所有消息
//    }
//
//    private void syncFinishedOper() {
//        MyApplication.isFromNLService = false;  //重新进入 app 归位
//
//        mySharedPf.setBoolean("isFirstRunApp", false);
//        checkSyncStatus.cancel();
//        DeviceSharedPf.getInstance(getApplicationContext()).setString("lastsyncSleepTime" + MyApplication.deviceAddr, TimeUtil.getCurrentDate()); // 设置同步日期
//
//
//        if (0 == DeviceSharedPf.getInstance(getApplicationContext()).getInt("isWHSynced", 0)) // 身高体重是否同步完
//        {
//            setHeightAndWeight();
//        }
////        mHandler.removeMessages(GlobalValue.SYNC_FINISH);   //?
//
//        //     HardSdk.getInstance().readBraceletConfig();
//
//
//        HardSdk.getInstance().syncBraceletDataToDb();
//
//        syncConfig(); //同步手机配置到手环
//
//
//    }
//
//    void setHeightAndWeight() {
//        // int time = intent.getIntExtra("screentime", 5); //默认5s
//        //    int height = Integer.valueOf(mySharedPf.getString("height", "170"));
//        //     int weight = Integer.valueOf(mySharedPf.getString("weight", "60").split("\\.")[0]);
//        //    HardSdk.getInstance().setHeightAndWeight(height, weight, deviceOtherInfoManager.getLightScreenTime());
//        //     DeviceSharedPf.getInstance(getApplicationContext()).setInt("isWHSynced", 1);
//    }
//
//
//    Runnable findBracelet = new Runnable() {
//        @Override
//        public void run() {
//            if (MyApplication.isSyncing == false && MyApplication.isDevConnected == true) {
//                // if (Utils.isBackground(getApplicationContext())) {
//                HardSdk.getInstance().findBattery();
//                //    }
//            }
//            mHandler.postDelayed(this, 30000);
//        }
//    };
//
//
//    private Handler configHandler = new Handler() {
//        @Override
//        public void dispatchMessage(Message msg) {
//            super.dispatchMessage(msg);
//            if (msg.what == 10) {
//                if (MyApplication.isFromNLService == false) {
//                    if (isFailed == true) {
//                        Utils.showToast(getApplicationContext(), getString(R.string.syncFailed));
//                        return;
//                    }
//                    if (isSyncTimeOut == true) {
//                        if(MyApplication.isDevConnected){
//                            Utils.showToast(getApplicationContext(), getString(R.string.syncTimeOut));
//                        }
//                    } else {
//                        if(MyApplication.isDevConnected){
//                            Utils.showToast(getApplicationContext(), getString(R.string.syncFinish));
//                        }
//                    }
//                }
//                mHandler.removeCallbacks(findBracelet);
//                mHandler.postDelayed(findBracelet, 10000);
//            }
//        }
//    };
//
//    List<Clock> clockList;
//    int clockSize = 0;
//
//    private void syncConfig() {
//        clockList = clockManager.getLocalAlarmInfo(mySharedPf.getString("device_factory", GlobalValue.FACTORY_FITCLOUD));
//        clockSize = clockList.size();
//        configHandler.removeCallbacks(null);
//        configFlag = 0;
//        configHandler.post(syncConfigRunable);
//    }
//
//    int configFlag = 0;
//    AlarmManager alarmManager;
//    ClockManager clockManager;
//
//
//    Runnable syncConfigRunable = new Runnable() {
//        @Override
//        public void run() {
//
//            try {
//
//                if (configFlag == 4) {
//                    Log.d(TAG, "run: 同步闹钟列表了："+clockList.size());
//                    HardSdk.getInstance().setAlarmList(clockList);
//                    clockSize = 0;
//                    configHandler.postDelayed(this, 1500);
//                } else if (clockSize == 0 && configFlag > 3) {
//                    configHandler.removeCallbacks(null);
//                    EventBus.getDefault().post(new SyncStatus(false));  // 同步结束
//                    MyApplication.isSyncing = false;
//                    configHandler.sendEmptyMessage(10);
//                    return;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            switch (configFlag) {
//                case 0:
//                    HardSdk.getInstance().setSedentaryRemindCommand(deviceOtherInfoManager.isLongSitRemind() == true ? 1 : 0, deviceOtherInfoManager.getLongSitTime(), deviceOtherInfoManager.isDisturbRemind());
//                    configHandler.postDelayed(this, 700);
//                    //  HardSdk.getInstance().openFuncRemind(GlobalValue.TYPE_MESSAGE_PHONE, noticeInfo.isEnablePhone);
//                    break;
//
//                case 1:
//                    HardSdk.getInstance().setPalmingState(deviceOtherInfoManager.isPalming());
//                    configHandler.postDelayed(this, 700);
//                    break;
//                case 2:
//                    HardSdk.getInstance().setAutoHealthTest(deviceOtherInfoManager.isAutoHeartTest());
//                    configHandler.postDelayed(this, 700);
//
//                    break;
//                case 3:
//                    HardSdk.getInstance().syncNoticeConfig();
//                    configHandler.postDelayed(this, 3500);
//                    //      HardSdk.getInstance().findBattery();
//                    break;
//
//            }
//
//            configFlag++;
//
//        }
//    };
//
//
//    void packageSendInfo(String time, String repeatValue, int flag, int cycle, boolean isOpen, String type, String tip) {
//        if (time != null && time.length() > 0 && repeatValue != null && repeatValue.length() >= 2) {
//            String[] times = time.split(":");
//            int hour = Integer.parseInt(times[0]);
//            int minitue = Integer.parseInt(times[1]);
//            byte weekPeroid = 0;
//            if (cycle == 0) {  // 每天的闹钟
//                weekPeroid = Config.EVERYDAY;
//            } else {
//                weekPeroid = WeekUtils.getWeekByteByReapeat(cycle); // 得到星期的信息
//            }
//
//
//            HardSdk.getInstance().setAlarmClcok(flag, weekPeroid, hour, minitue, isOpen);
//        }
//    }
//
//    @Override
//    public void onStepChanged(int step, float distance, int calories, boolean finish_status) {
//
//        Log.i(TAG, step + "" + " distance: " + distance);
//    }
//
//    @Override
//    public void onSleepChanged(int lightTime, int deepTime, int sleepAllTime, int[] sleepStatusArray, int[] timePointArray, int[] duraionTimeArray) {
//
//    }
//
//    @Override
//    public void onHeartRateChanged(int rate, int status) {
//
//    }
//
//
//    Handler mHandler = new Handler() {
//        @Override
//        public void dispatchMessage(Message msg) {
//            super.dispatchMessage(msg);
//        }
//    };
//
//
//    @Subscribe
//    public void startReConnectTimer(StartReConnectTimer startReConnectTimer) {
//        if (linkTime == null || MyApplication.isDevConnected || MyApplication.isManualOff) {
//            return;
//        }
//        if (startReConnectTimer.isStart()) {
//            Log.d(TAG, "startReConnectTimer: 开启了重连接任务");
//            linkTime.start();
//        } else {
//            Log.d(TAG, "startReConnectTimer: 取消了重连接任务");
//            Log.d(TAG, "connectOper: linkTime.cancel()4");
//            linkTime.cancel();
//            if (!MyApplication.isDevConnected) {
//                EventBus.getDefault().post(new ConnectStateText(getString(R.string.noconnected)));
//            }
//        }
//    }
//
//
//    //下面3个计时器
//    public class LinkTime extends CountDownTimer {
//
//        public LinkTime(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            //   MyApplication.ins;    // 尝试连接
//            isAttempLinking = true;
//            Log.d(TAG, "onTick: run");
//            if (MyApplication.tmpDeviceAddr != null && HardSdk.getInstance() != null) {
//                Log.d(TAG, "onTick: 尝试重连接");
//                HardSdk.getInstance().reConnect();
//            }
//        }
//
//        @Override
//        public void onFinish() {
//            isAttempLinking = false;
//            this.cancel();
//            Log.d(TAG, "onFinish: 执行了onfinish");
//            //重连接超时后，设置界面为未连接. // TODO: 2017/9/19
//            if (!MyApplication.isDevConnected) {
//                Log.d(TAG, "onFinish: 发送修改 未连接 文字");
//                HardSdk.getInstance().removeTimeOutTimer();
//                EventBus.getDefault().post(new ConnectStateText(getString(R.string.noconnected)));
//            }
//        }
//    }
//
//    boolean isSyncTimeOut = false;
//    SycnStatusListener checkSyncStatus;
//
//    public class SycnStatusListener extends CountDownTimer {
//
//        public SycnStatusListener(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            //    System.out.println("同步 记步状态开始同步" + millisUntilFinished);
//
//            if (stepSyncStarted == true) {
//                //   this.onFinish();
//                //   this.cancel();
//            } else {
//                //       HardSdk.getInstance().syncAllStepData();
//            }
//            isSyncTimeOut = false;
//        }
//
//        @Override
//        public void onFinish() {
//            this.cancel();
//            if (MyApplication.isDevConnected == true) {
//                if (!GlobalValue.FACTORY_YCY.equals(MyApplication.globalFactoryName)) {
//                    isSyncTimeOut = true;
//                    onCallbackResult(GlobalValue.SYNC_FINISH, true, null);
//                } else {
////                    mHandler.sendEmptyMessageDelayed(GlobalValue.SYNC_FINISH, 120 * 1000);
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onCallbackResult(GlobalValue.SYNC_FINISH, true, null);
//                        }
//                    }, 70 * 1000);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        MyApplication.isManualOff = true;
//        Log.d(TAG, "onDestroy: LinkAndSyncService onDestory执行了");
//        HardSdk.getInstance().disconnect();// unBindService
//        linkTime.cancel();
//        isAttempLinking = false;
//        unregisterReceiver(mReceiver);
//        unregisterReceiver(mDeviceNoticeReceiver);
//        EventBus.getDefault().unregister(this);
//    }
//
//
//    private IntentFilter makeFilter() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        return filter;
//    }
//
//
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //    LogUtil.e(TAG, "onReceive---------");
//            switch (intent.getAction()) {
//                case BluetoothAdapter.ACTION_STATE_CHANGED:
//                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
//                    switch (blueState) {
//                        case BluetoothAdapter.STATE_TURNING_ON:
//                            break;
//                        case BluetoothAdapter.STATE_ON:
//                            linkTime.cancel();
//                            linkTime.start();
//                            break;
//                        case BluetoothAdapter.STATE_TURNING_OFF:
//                            break;
//                        case BluetoothAdapter.STATE_OFF:
//                            linkTime.cancel();
//                            break;
//                    }
//                    break;
//            }
//        }
//    };
//}

package com.walnutin.hardsdkdemo.ProductList.wyp;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.base.IConnectResponse;
import com.veepoo.protocol.listener.base.INotifyResponse;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.listener.data.IAlarmDataListener;
import com.veepoo.protocol.listener.data.IBPDetectDataListener;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.listener.data.ICameraDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IFindDeviceDatalistener;
import com.veepoo.protocol.listener.data.IFindPhonelistener;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.listener.data.INightTurnWristeDataListener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISleepDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.model.datas.AlarmData;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.datas.BatteryData;
import com.veepoo.protocol.model.datas.BpData;
import com.veepoo.protocol.model.datas.FindDeviceData;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.HeartData;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.datas.NightTurnWristeData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.Alarm2Setting;
import com.veepoo.protocol.model.settings.AlarmSetting;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.ContentSmsSetting;
import com.veepoo.protocol.model.settings.ContentSocailSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.model.settings.LongSeatSetting;
import com.veepoo.protocol.operate.BPOperater;
import com.veepoo.protocol.operate.CameraOperater;
import com.veepoo.protocol.operate.HeartOperater;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductList.ThirdBaseSdk;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.BloodPressure;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Clock;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfo;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.ProductNeed.manager.NoticeInfoManager;
import com.walnutin.hardsdkdemo.utils.Config;
import com.walnutin.hardsdkdemo.utils.DateUtils;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;
import com.walnutin.hardsdkdemo.utils.WeekUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WypSdk extends ThirdBaseSdk {
    public static final String TAG = WypSdk.class.getSimpleName();
    private static WypSdk mInstance;
    private Context mContext;
    private boolean is24Hourmodel = true;
    VPOperateManager vpOperateManager;

    private Handler mMyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GlobalValue.CONFIRM_PWD_OK:
                    startSyncStep();
                    break;

            }
        }
    };

    private void startSyncStep() {
        mIDataCallBack.onResult(null, true, GlobalValue.STEP_SYNC_START);
        vpOperateManager.readSportStep(writeResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {
                Log.i(TAG, "onSportDataChange: " + sportData.toString());
                mIRealDataSubject.stepChanged(sportData.getStep(), (float) sportData.getDis(), (int) (sportData.getKcal()), true);
                mIDataCallBack.onResult(null, true, GlobalValue.STEP_FINISH);
            }
        });
    }


    public static Object getInstance() {
        if (mInstance == null) {
            mInstance = new WypSdk();
        }
        return mInstance;
    }

    @Override
    public boolean initialize(Context context) {
        mContext = HardSdk.getInstance().getContext();
        vpOperateManager = VPOperateManager.getMangerInstance(mContext);
        vpOperateManager.settingFindPhoneListener(new IFindPhonelistener() {
            @Override
            public void findPhone() {
                mIDataCallBack.onResult(null, true, GlobalValue.FIND_PHONE);
            }
        });
        return true;
    }

    @Override
    public void connect(String addr) {
        Log.d(TAG, "connect: into");
        VPOperateManager.getMangerInstance(mContext).connectDevice(addr, new IConnectResponse() {
            @Override
            public void connectState(int i, BleGattProfile bleGattProfile, boolean b) {
                Log.d(TAG, "connect: into connectState");
                if (i == Code.REQUEST_SUCCESS) {
                    mIDataCallBack.onResult(null, false, GlobalValue.CONNECTED_MSG);
                } else {
                    Log.e(TAG, "connectState: 连接失败 i:" + i);
                }
            }
        }, new INotifyResponse() {
            @Override
            public void notifyState(int i) {
                if (i != Code.REQUEST_SUCCESS) {
                    Log.d(TAG, "notifyState: 设置连接监听失败");
                }
            }
        });
    }

    @Override
    public void disconnect() {
        vpOperateManager.disconnectWatch(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
                Log.d(TAG, "onResponse: i:" + i);
                if (i == Code.REQUEST_SUCCESS) {
                } else {
                    Log.d(TAG, "notifyState: 断开连接失败");
                }
                mIDataCallBack.onResult(null, false, GlobalValue.DISCONNECT_MSG);

            }
        });
    }

    @Override
    public void readRssi() {

    }


    @Override
    public void findBand(int num) {

    }

    @Override
    public void stopVibration() {

    }

    @Override
    public void resetBracelet() {

    }

    @Override
    public void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content) {
        /**电话,可以只传电话号码**/
//            ContentSetting contentphoneSetting0 = new ContentPhoneSetting(ESocailMsg.PHONE, contactMsgLength, allMsgLenght,"0755-86562490");
        /**电话,传联系人姓名以及电话号码，最终显示的联系人姓名**/
//            ContentSetting contentphoneSetting1 = new ContentPhoneSetting(ESocailMsg.PHONE, contactMsgLength, allMsgLenght,"深圳市维亿魄科技有限公司", "0755-86562490");
//            VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentphoneSetting0);

        /**短信，可以只传电话号码**/
        //   ContentSetting contentsmsSetting0 = new ContentSmsSetting(ESocailMsg.SMS, 0, contact.length(), contact, content);
        /**短信，传联系人姓名以及电话号码，最终显示的联系人姓名**/

        if (smsType == GlobalValue.TYPE_MESSAGE_SMS) {
            ContentSetting contentsmsSetting1 = new ContentSmsSetting(ESocailMsg.SMS, 0, contact.length(), contact, content);
            VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentsmsSetting1);
        } else {
            ContentPhoneSetting contentPhoneSetting = new ContentPhoneSetting(ESocailMsg.PHONE, 0, contact.length(), contact);
            VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentPhoneSetting);

        }

    }

    @Override
    public void sendQQWeChatTypeCommand(int type, String body) {
        if (type == GlobalValue.TYPE_MESSAGE_QQ) {
            ContentSetting contentsociaSetting = new ContentSocailSetting(ESocailMsg.QQ, 0, body.length(), "", body);
            vpOperateManager.sendSocialMsgContent(writeResponse, contentsociaSetting);
        } else if (type == GlobalValue.TYPE_MESSAGE_WECHAT) {
            ContentSetting contentsociaSetting = new ContentSocailSetting(ESocailMsg.WECHAT, 0, body.length(), "", body);
            vpOperateManager.sendSocialMsgContent(writeResponse, contentsociaSetting);

        } else if (type == GlobalValue.TYPE_MESSAGE_FACEBOOK) { // 添加facebook 消息提醒
            ContentSetting contentsociaSetting = new ContentSocailSetting(ESocailMsg.FACEBOOK, 0, body.length(), "", body);
            vpOperateManager.sendSocialMsgContent(writeResponse, contentsociaSetting);
        } else if (type == GlobalValue.TYPE_MESSAGE_WHATSAPP) {
            ContentSetting contentsociaSetting = new ContentSocailSetting(ESocailMsg.WHATS, 0, body.length(), "", body);
            vpOperateManager.sendSocialMsgContent(writeResponse, contentsociaSetting);
        } else {
            ContentSetting contentsociaSetting = new ContentSocailSetting(ESocailMsg.OTHER, 0, body.length(), "", body);
            vpOperateManager.sendSocialMsgContent(writeResponse, contentsociaSetting);
        }
    }

    @Override
    public void setUnLostRemind(boolean isOpen) {
        vpOperateManager.settingFindDevice(writeResponse, new IFindDeviceDatalistener() {
            @Override
            public void onFindDevice(FindDeviceData findDeviceData) {

            }
        }, isOpen);
    }


    WriteResponse writeResponse = new WriteResponse();

    /**
     * 写入的状态返回
     */
    class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            //  Logger.t(TAG).i("write cmd status:" + code);

        }
    }

    @Override
    public void sendSedentaryRemindCommand(int isOpen, int time, String startTime, String endTime, boolean isDisturb) {

        int startHour = Integer.valueOf(startTime.split(":")[0]);
        int startMinitue = Integer.valueOf(startTime.split(":")[1]);
        int endHour = Integer.valueOf(endTime.split(":")[0]);
        int endMinitue = Integer.valueOf(endTime.split(":")[1]);
        LongSeatSetting longSeatSetting = new LongSeatSetting(startHour, startMinitue, endHour, endMinitue, time, isOpen == 0 ? false : true);
        vpOperateManager.settingLongSeat(writeResponse, longSeatSetting, new ILongSeatDataListener() {
            @Override
            public void onLongSeatDataChange(LongSeatData longSeatData) {
                Log.i(TAG, "onLongSeatDataChange:" + longSeatData.toString());
            }
        });
    }

    @Override
    public void setHeightAndWeight(int height, int weight, int age, String sexfrom, String birthday) {
        ESex eSex = ESex.WOMEN;
        if (sexfrom.equals("男")) {
            eSex = ESex.MAN;
        }
        vpOperateManager.syncPersonInfo(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new IPersonInfoDataListener() {
            @Override
            public void OnPersoninfoDataChange(EOprateStauts eOprateStauts) {
                Log.i(TAG, "EOprateStauts:" + eOprateStauts.toString());
            }
        }, new PersonInfoData(eSex, height, weight, age, MySharedPf.getInstance(mContext).getStepGoal()))
        ;
    }

    @Override
    public void sendOffHookCommand() {

    }

    @Override
    public void sendPalmingStatus(boolean isOpen) {
        TimeData startTime = new TimeData(0, 0);
        TimeData endTime = new TimeData(23, 59);
        vpOperateManager.settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
            @Override
            public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                Log.i(TAG, "NightTurnWristeData:" + nightTurnWristeData.toString());

            }
        }, isOpen, startTime, endTime);
    }

    @Override
    public void startRateTest() {
        vpOperateManager.startDetectHeart(writeResponse, new IHeartDataListener() {
            @Override
            public void onDataChange(HeartData heartData) {
                if (heartData.getHeartStatus() == HeartOperater.HeartStatus.STATE_HEART_NORMAL
                        || heartData.getHeartStatus() == HeartOperater.HeartStatus.STATE_INIT) {
                    mIRealDataSubject.heartRateChanged(heartData.getData(), GlobalValue.RATE_TEST_TESTING);
                } else {
                }
            }
        });
    }

    @Override
    public void stopRateTest() {
        vpOperateManager.stopDetectHeart(writeResponse);
        mIRealDataSubject.heartRateChanged(0, GlobalValue.RATE_TEST_FINISH);

    }

    @Override
    public void syncAllStepData() {
        confirmDevicePwd();


    }

    @Override
    public void syncAllHeartRateData() {
        vpOperateManager.readOriginDataSingleDay(writeResponse, new IOriginDataListener() {
            @Override
            public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

            }

            @Override
            public void onReadOriginProgress(float v) {

            }

            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {

            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                Log.i(TAG, "onOringinHalfHourDataChange: " + originHalfHourData.toString());
                List<HalfHourRateData> halfHourRateDataList = originHalfHourData.getHalfHourRateDatas();
                dealWithStep(originHalfHourData.getHalfHourSportDatas());
                dealWithHeartList(halfHourRateDataList);
                dealWithBp(originHalfHourData.getHalfHourBps());

            }

            @Override
            public void onReadOriginComplete() {
                mIDataCallBack.onResult(null, true, GlobalValue.HEART_FINISH);
            }
        }, 0, 1, 1);
    }

    private void dealWithBp(List<HalfHourBpData> halfHourBps) {
        if (halfHourBps != null) {
            List<BloodPressure> bloodPressureArrayList = new ArrayList<>();
            for (HalfHourBpData halfHourRateData : halfHourBps) {
                if (halfHourRateData.getLowValue() > 0) {
                    BloodPressure bloodPressure = new BloodPressure();
                    bloodPressure.account = HardSdk.getInstance().getAccount();
                    bloodPressure.testMomentTime = halfHourRateData.getTime().getDateForDb() + " " + halfHourRateData.getTime().getColck() + ":00";
                    bloodPressure.diastolicPressure = halfHourRateData.getHighValue();
                    bloodPressure.systolicPressure = halfHourRateData.getLowValue();
                    bloodPressureArrayList.add(0, bloodPressure);
                }
            }
            SqlHelper.instance().syncBraceletBloodPressureData(bloodPressureArrayList);
        }
    }


    @Override
    public void syncAllSleepData() {
        int yesterday = 1;
        vpOperateManager.readSleepDataSingleDay(writeResponse, new ISleepDataListener() {
            @Override
            public void onSleepDataChange(SleepData sleepData) {
                Log.i(TAG, "onSleepDataChange: " + sleepData.toString());
                dealWithSleep(sleepData);

            }

            @Override
            public void onSleepProgress(float progress) {
                Log.i(TAG, "syncAllSleepData: " + progress);
            }

            @Override
            public void onSleepProgressDetail(String day, int packagenumber) {

            }

            @Override
            public void onReadSleepComplete() {
                mIDataCallBack.onResult(null, true, GlobalValue.SYNC_FINISH);
            }
        }, yesterday, 1);
    }

    private void dealWithSleep(SleepData sleepData) {
        if (sleepData != null) {
            String sleepStatus = sleepData.getSleepLine(); // 睡眠柱
            SleepModel sleepTimeInfo = new SleepModel();
            sleepTimeInfo.account = HardSdk.getInstance().getAccount();
            sleepTimeInfo.deepTime = sleepData.getDeepSleepTime();
            sleepTimeInfo.lightTime = sleepData.getLowSleepTime();
            sleepTimeInfo.totalTime = sleepData.getAllSleepTime();
            sleepTimeInfo.soberTime = sleepTimeInfo.totalTime - sleepTimeInfo.deepTime - sleepTimeInfo.getLightTime();
            sleepTimeInfo.date = TimeUtil.offSetDate(sleepData.getDate(), 1); //
            int start = sleepData.getSleepDown().getHMValue();
            toC(sleepTimeInfo.date, sleepStatus, start, sleepTimeInfo.getTotalTime(), sleepTimeInfo.getDeepTime(), sleepTimeInfo.getLightTime(), sleepTimeInfo.getSoberTime());
        }
    }


    void toC(String date, String valid, int start, int total, int deepTime, int light, int soberTime) {

        ArrayList<Integer> timePointArray = new ArrayList<Integer>();
        ArrayList<Integer> sleepStatusArray = new ArrayList<Integer>();
        ArrayList<Integer> duraionTimeArray = new ArrayList<Integer>();
        int startSleepMinitue = start;
        String sleepData = valid;
        int len = sleepData.length();
        for (int i = 0; i < len; i++) {
            int value = Integer.valueOf((sleepData.substring(i, i + 1)));
            duraionTimeArray.add(5);
            timePointArray.add((startSleepMinitue + 5 * i + 5) % 1440); // 每一段结束时刻
            if (value == 0) {
                sleepStatusArray.add(1); // 2清醒 1浅睡 0 深睡
            } else if (value == 1) {
                sleepStatusArray.add(0); // 2清醒 1浅睡 0 深睡
            } else {
                sleepStatusArray.add(2);
            }

        }
        correctSleepArray(date, sleepStatusArray, duraionTimeArray, timePointArray, 5, total, deepTime, light, soberTime);
    }

    void correctSleepArray(String date, List<Integer> sleepStatusArray,
                           List<Integer> duraionTimeArray, List<Integer> timePointArray,
                           int duraionTime, int total, int deepTime, int light, int soberTime) {
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
        System.out.println("------------------------------");
        len = newsleepStatusArray.size();
        int[] dtArray = new int[len];
        int[] statusArray = new int[len];
        int[] pointArray = new int[len];
        System.out.println(len);
        for (int i = 0; i < len; i++) {
            statusArray[i] = newsleepStatusArray.get(i);
            dtArray[i] = newduraionTimeArray.get(i);
            pointArray[i] = newtimePointArray.get(i);
            System.out
                    .print(" sleepStatusArray: " + newsleepStatusArray.get(i));
            System.out.print("  duraionTimeArray: "
                    + newduraionTimeArray.get(i));
            System.out.println("  timePointArray: " + newtimePointArray.get(i));
        }

        if (total > 0) {
            List<SleepModel> sleepModelList = new ArrayList<>();
            SleepModel sleepModel = new SleepModel();
            sleepModel.account = HardSdk.getInstance().getAccount();
            sleepModel.duraionTimeArray = dtArray;
            sleepModel.sleepStatusArray = statusArray;
            sleepModel.timePointArray = pointArray;
            sleepModel.deepTime = deepTime;
            sleepModel.lightTime = light;
            sleepModel.totalTime = total;
            sleepModel.soberTime = soberTime;
            sleepModel.date = date;
            sleepModelList.add(sleepModel);
            SqlHelper.instance().syncBraceletSleepData(sleepModelList);

            if (sleepModel.date.equals(TimeUtil.getCurrentDate())) {
                mIRealDataSubject.sleepChanged(sleepModel.getLightTime(), sleepModel.getDeepTime(), sleepModel.getTotalTime(),
                        sleepModel.getSleepStatusArray(), sleepModel.getTimePointArray(), sleepModel.getDuraionTimeArray());
            }
        }

    }

    private void dealWithStep(List<HalfHourSportData> halfHourSportDatas) {
        StepInfos stepInfos = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(), TimeUtil.getCurrentDate());

        int cal = 0;
        float dis = 0;
        int step = 0;
        if (halfHourSportDatas != null) {
            Map<Integer, Integer> stepTodayOneHourInfoMap = new LinkedHashMap<>();
            for (HalfHourSportData halfHourSportData : halfHourSportDatas) {
                cal += halfHourSportData.getCalValue();
                dis += halfHourSportData.getDisValue();
                step += halfHourSportData.getStepValue();
                TimeData time = halfHourSportData.getTime();
                if (stepTodayOneHourInfoMap.containsKey(time.getHour())) {
                    stepTodayOneHourInfoMap.put(time.getHour(), stepTodayOneHourInfoMap.get(time.getHour()) + halfHourSportData.getStepValue()); // 每小时累加记步数据
                } else {
                    stepTodayOneHourInfoMap.put(time.getHour(), halfHourSportData.getStepValue());
                }
            }

            stepInfos.setAccount(HardSdk.getInstance().getAccount());
            stepInfos.setDates(TimeUtil.getCurrentDate());
            stepInfos.setStepOneHourInfo(stepTodayOneHourInfoMap);
            stepInfos.setDistance(dis);
            stepInfos.setCalories(cal);
            stepInfos.setStep(step);
            SqlHelper.instance().insertOrUpdateTodayStep(stepInfos);  // 添加到数据库

        }

    }


    private void dealWithHeartList(List<HalfHourRateData> halfHourRateDataList) {
        if (halfHourRateDataList != null) {
            List<HeartRateModel> heartRateModelList = new ArrayList<>();
            for (HalfHourRateData halfHourRateData : halfHourRateDataList) {
                if (halfHourRateData.getRateValue() > 0) {
                    HeartRateModel heartRateModel = new HeartRateModel();
                    heartRateModel.account = HardSdk.getInstance().getAccount();
                    heartRateModel.testMomentTime = halfHourRateData.getTime().getDateForDb() + " " + halfHourRateData.getTime().getColck() + ":00";
                    heartRateModel.currentRate = halfHourRateData.getRateValue();
                    heartRateModelList.add(0, heartRateModel);
                }
            }
            SqlHelper.instance().syncBraceletHeartData(heartRateModelList);
        }
    }


    @Override
    public Map<Integer, Integer> queryOneHourStep(String date) {
        return null;
    }

    @Override
    public void syncBraceletDataToDb() {
        String lastSyncToDbTime = SqlHelper.instance().getLastSyncStepDate(HardSdk.getInstance().getAccount());

        int gapDay = 0;
        try {
            gapDay = DateUtils.daysBetween(lastSyncToDbTime, TimeUtil.getCurrentDate());
            String lastSyncDate = MySharedPf.getInstance(mContext).getString(HardSdk.getInstance().getAccount() + "_lastData_" + HardSdk.getInstance().getDeviceAddr(), "1997-01-01");
            int lastGap = DateUtils.daysBetween(lastSyncDate, TimeUtil.getCurrentDate()); // 上次同步的具体日期与今天的差值
            if (gapDay < 2 && lastGap < 1) {
                return;
            }

            if (gapDay > 7) {
                gapDay = 7;
            }
            Log.i(TAG, "gap:" + gapDay);
            vpOperateManager.readOriginDataFromDay(writeResponse, new IOriginDataListener() {
                @Override
                public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {
                }

                @Override
                public void onReadOriginProgress(float v) {
                }

                @Override
                public void onOringinFiveMinuteDataChange(OriginData originData) {
                }

                @Override
                public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                    dealWithStep(originHalfHourData.getHalfHourSportDatas());
                    dealWithHeartList(originHalfHourData.getHalfHourRateDatas());
                    dealWithBp(originHalfHourData.getHalfHourBps());
                    Log.i(TAG, "aftersync onOringinHalfHourDataChange:" + originHalfHourData.toString());

                }

                @Override
                public void onReadOriginComplete() {
                    MySharedPf.getInstance(mContext).setString(HardSdk.getInstance().getAccount() + "_lastData_" + HardSdk.getInstance().getDeviceAddr(), TimeUtil.getCurrentDate());

                }
            }, 1, 0, gapDay);


            vpOperateManager.readSleepDataFromDay(writeResponse, new ISleepDataListener() {
                        @Override
                        public void onSleepDataChange(SleepData sleepData) {
                            dealWithSleep(sleepData);
                            Log.i(TAG, "aftersync  onSleepDataChange:" + sleepData.toString());
                        }

                        @Override
                        public void onSleepProgress(float progress) {
                        }

                        @Override
                        public void onSleepProgressDetail(String day, int packagenumber) {
                        }

                        @Override
                        public void onReadSleepComplete() {
                        }
                    }
                    , 1, gapDay);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void openFuncRemind(int type, boolean isOpen) {

        NoticeInfoManager noticeInfoManager = NoticeInfoManager.getInstance(mContext);
        noticeInfoManager.getLocalNoticeInfo();
        FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
        socailMsgData.setOther(noticeInfoManager.isAllowOther() ? EFunctionStatus.SUPPORT_OPEN : EFunctionStatus.SUPPORT_CLOSE);
        socailMsgData.setWechat(noticeInfoManager.isEnableWeChat() ? EFunctionStatus.SUPPORT_OPEN : EFunctionStatus.SUPPORT_CLOSE);
        socailMsgData.setQq(noticeInfoManager.isEnableQQ() ? EFunctionStatus.SUPPORT_OPEN : EFunctionStatus.SUPPORT_CLOSE);
        socailMsgData.setFacebook(noticeInfoManager.isEnableFaceBook() ? EFunctionStatus.SUPPORT_OPEN : EFunctionStatus.SUPPORT_CLOSE);
        socailMsgData.setTwitter(noticeInfoManager.isEnableTwitter() ? EFunctionStatus.SUPPORT_OPEN : EFunctionStatus.SUPPORT_CLOSE);

        socailMsgData.setPhone(noticeInfoManager.isEnablePhone() ? EFunctionStatus.SUPPORT : EFunctionStatus.UNSUPPORT);
        socailMsgData.setMsg(noticeInfoManager.isEnableMsg() ? EFunctionStatus.SUPPORT : EFunctionStatus.UNSUPPORT);

        vpOperateManager.settingSocialMsg(writeResponse, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {

            }
        }, socailMsgData);

    }

    @Override
    public boolean isSupportHeartRate(String deviceName) {
        return isSupportHeart;
    }

    @Override
    public boolean isSupportBloodPressure(String deviceName) {
        return isSupportBp;
    }

    @Override
    public boolean isSupportUnLostRemind(String deviceName) {
        return false;
    }

    @Override
    public int getSupportAlarmNum() {
        if (isSupportAlarm2 == true) {
            return 10;
        } else
            return 3;
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
    public void unBindUser() {

    }

    @Override
    public void findBattery() {
        vpOperateManager.readBattery(writeResponse, new IBatteryDataListener() {
            @Override
            public void onDataChange(BatteryData batteryData) {
                Log.i(TAG, "onDataChange:" + batteryData.toString());
                mIDataCallBack.onResult(batteryData.getBatteryLevel() * 25, true, GlobalValue.BATTERY);

            }
        });
    }

    @Override
    public void startAutoHeartTest(boolean isAuto) {
        boolean isHaveMetricSystem = true;
        boolean isMetric = true;
        boolean is24Hour = true;
        boolean isOpenAutoHeartDetect = isAuto;
        boolean isOpenAutoBpDetect = isAuto;
        CustomSetting customSetting = new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect);
        vpOperateManager.changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {

            }
        }, customSetting);
    }


    @Override
    public void setAlarmList(List clockList) {

        if (clockList != null && clockList.size() > 0) {
        } else {
            return;
        }

        if (isSupportAlarm2 == false) {
            List<AlarmSetting> alarmSettingList = new ArrayList<>();
            int len = clockList.size();
            for (int i = len; i < 3; i++) {
                alarmSettingList.add(new AlarmSetting(i, 0, false));
            }
            for (Object clock : clockList) {
                int hour = Integer.parseInt(((Clock) clock).getTime().split(":")[0]);
                int min = Integer.parseInt(((Clock) clock).getTime().split(":")[1]);
                AlarmSetting alarmSetting = new AlarmSetting(hour, min, ((Clock) clock).isEnable);
                alarmSettingList.add(alarmSetting);
            }
            vpOperateManager.settingAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    Log.i(TAG, "onAlarmDataChangeListener: " + alarmData.toString());
                }
            }, alarmSettingList);
        } else {
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

            configHandler.postDelayed(this, 299);
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

            setAlarmClcok(flag, weekPeroid, hour, minitue, isOpen, type, tip);  //发送设置闹钟广播
        }
    }


    @Override
    public void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen, String typ, String tip) {
        if (isSupportAlarm2 == false) {
            return;
        }
        String[] weeks = WeekUtils.parseRepeat(weekPeroid, 1, GlobalValue.LANGUAGE_CHINESE).split(",");
        String repeat = "0000000";
        StringBuilder reBuild = new StringBuilder(repeat);
        for (int i = 0; i < weeks.length; i++) {
            switch (Integer.parseInt(weeks[i])) {
                case 1:
                    reBuild.replace(6, 7, "1");
                    break;
                case 2:
                    reBuild.replace(5, 6, "1");
                    break;
                case 3:
                    reBuild.replace(4, 5, "1");
                    break;
                case 4:
                    reBuild.replace(3, 4, "1");
                    break;
                case 5:
                    reBuild.replace(2, 3, "1");
                    break;
                case 6:
                    reBuild.replace(1, 2, "1");
                    break;
                case 7:
                    reBuild.replace(0, 1, "1");
                    break;
            }
        }
        Alarm2Setting alarm2Setting = new Alarm2Setting();
        alarm2Setting.setAlarmHour(hour);
        alarm2Setting.setAlarmId(flag + 1);
        alarm2Setting.setAlarmMinute(minitue);
        alarm2Setting.setOpen(isOpen);
        alarm2Setting.setRepeatStatus(reBuild.toString());
        Log.i(TAG, "onAlarmDataChangeListListener: 设置 " + System.currentTimeMillis());

        vpOperateManager.modifyAlarm2(writeResponse, new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                Log.i(TAG, "onAlarmDataChangeListListener: 返回" + System.currentTimeMillis() + " \n" + alarmData2.toString());
            }
        }, alarm2Setting);
    }


    @Override
    public void readClock() {
        if (isSupportAlarm2 == true) {
            vpOperateManager.readAlarm2(writeResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    List<Alarm2Setting> alarm2SettingList = alarmData2.getAlarm2SettingList();
                    List<Clock> clockList = new ArrayList<Clock>();
                    for (Alarm2Setting alarm2Setting : alarm2SettingList) {
                        int hour = alarm2Setting.alarmHour;
                        int min = alarm2Setting.alarmMinute;
                        int serial = alarm2Setting.getAlarmId();
                        boolean isOpen = alarm2Setting.isOpen;
                        String repeat = alarm2Setting.getRepeatStatus();
                        int cycle = (repeat.substring(6, 7).equals("0") ? 0 : 1) * 1 // 周一
                                + ((repeat.substring(5, 6).equals("0")) ? 0 : 1) * 2 // 周二
                                + ((repeat.substring(4, 5).equals("0")) ? 0 : 1) * 4 // 周三
                                + ((repeat.substring(3, 4).equals("0")) ? 0 : 1) * 8 // 周四
                                + ((repeat.substring(2, 3).equals("0")) ? 0 : 1) * 16 // 周五
                                + ((repeat.substring(1, 2).equals("0")) ? 0 : 1) * 32 // 周六
                                + ((repeat.substring(0, 1).equals("0")) ? 0 : 1) * 64; // 周日
                        Clock clock = new Clock();
                        clock.setSerial(serial);
                        clock.setTime(hour + ":" + min);
                        clock.setEnable(isOpen);
                        clock.setRepeat(cycle);
                        clockList.add(clock);
                        Log.i(TAG, "onAlarmDataChangeListListener:" + clock.toString());

                    }
                    mIDataCallBack.onResult(clockList, true, GlobalValue.READ_ALARM_OK);
                }
            });
        } else {
            vpOperateManager.readAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    List<AlarmSetting> alarmSettingList = alarmData.getAlarmSettingList();
                    List<Clock> clockList = new ArrayList<>();
                    int i = 0;
                    for (AlarmSetting alarmSetting : alarmSettingList) {
                        Clock clock = new Clock();
                        clock.setSerial(++i);
                        clock.setTime(alarmSetting.getHour() + ":" + alarmSetting.getMinute());
                        clock.setEnable(alarmSetting.isOpen());
                        clock.setRepeat(127);
                        clockList.add(clock);
                    }
                    mIDataCallBack.onResult(clockList, true, GlobalValue.READ_ALARM_OK);

                }
            });
        }
    }


    @Override
    public void syncNoticeConfig() {

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
    public void writeCommand(String hexCmd) {

    }

    @Override
    public void openTakePhotoFunc(boolean isOpen) {

        if (isOpen) {
            vpOperateManager.startCamera(writeResponse, new ICameraDataListener() {
                @Override
                public void OnCameraDataChange(CameraOperater.COStatus oprateStauts) {

                    if (oprateStauts == CameraOperater.COStatus.TAKEPHOTO_CAN) {
                        mIDataCallBack.onResult(null, true, GlobalValue.DISCOVERY_DEVICE_SHAKE);
                    }
                }
            });
        } else {
            vpOperateManager.stopCamera(writeResponse, new ICameraDataListener() {
                @Override
                public void OnCameraDataChange(CameraOperater.COStatus oprateStauts) {

                }
            });
        }
    }

    boolean isSupportAlarm2;
    boolean isSupportBp;
    boolean isSupportHeart;

    public void confirmDevicePwd() {
        Log.d(TAG, "confirmDevicePwd: run");
        vpOperateManager.confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
                Log.d(TAG, "confirmDevicePwd onResponse: i:" + i);
                if (i == Code.REQUEST_SUCCESS) {
                    Log.d(TAG, "onResponse: confirmDevicePwd_SUCCESS");
                    mMyHandler.sendEmptyMessage(GlobalValue.CONFIRM_PWD_OK);
                } else {
                    Log.e(TAG, "onResponse: confirmDevicePwd_fail");
                }
            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {


            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                Log.i(TAG, "onFunctionSupportDataChange:" + functionSupport.toString());
                Log.i(TAG, "getAlarm2:" + functionSupport.getAlarm2());
                isSupportAlarm2 = functionSupport.getAlarm2().equals(EFunctionStatus.SUPPORT) ? true : false;

                isSupportBp = functionSupport.getBp().equals(EFunctionStatus.SUPPORT) ? true : false;
                isSupportHeart = functionSupport.getHeartDetect().equals(EFunctionStatus.SUPPORT) ? true : false;
                isSupportTakePhoto = functionSupport.getCamera().equals(EFunctionStatus.SUPPORT) ? true : false;


            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                Log.i(TAG, "onSocialMsgSupportDataChange:" + socailMsgData.toString());

                supportMsg = "1100000000";
                StringBuilder sb = new StringBuilder(supportMsg);

                boolean isSupportFb = socailMsgData.getFacebook().equals(EFunctionStatus.UNSUPPORT) ? false : true;
                boolean isSupportWhatapp = socailMsgData.getWhats().equals(EFunctionStatus.UNSUPPORT) ? false : true;
                boolean isSupportTwitter = socailMsgData.getTwitter().equals(EFunctionStatus.UNSUPPORT) ? false : true;
                boolean isSupportSkype = socailMsgData.getSkype().equals(EFunctionStatus.UNSUPPORT) ? false : true;
                boolean isSupportLine = socailMsgData.getLine().equals(EFunctionStatus.UNSUPPORT) ? false : true;
                boolean isSupportLinkIn = socailMsgData.getLinkin().equals(EFunctionStatus.UNSUPPORT) ? false : true;
                boolean isSupportInstagram = socailMsgData.getInstagram().equals(EFunctionStatus.UNSUPPORT) ? false : true;
                boolean isSupportSnapchat = socailMsgData.getSnapchat().equals(EFunctionStatus.UNSUPPORT) ? false : true;

                if (isSupportFb) {
                    sb.replace(2, 3, "1");
                }
                if (isSupportWhatapp) {
                    sb.replace(3, 4, "1");
                }
                if (isSupportTwitter) {
                    sb.replace(4, 5, "1");
                }
                if (isSupportSkype) {
                    sb.replace(5, 6, "1");
                }
                if (isSupportLine) {
                    sb.replace(6, 7, "1");
                }
                if (isSupportLinkIn) {
                    sb.replace(7, 8, "1");
                }
                if (isSupportInstagram) {
                    sb.replace(8, 9, "1");
                }
                if (isSupportSnapchat) {
                    sb.replace(9, 10, "1");
                }
                supportMsg = sb.toString();

            }
        }, "0000", is24Hourmodel);
    }

///wechat 位数 0 qq 1 facebookmsg 2 whatspp 3 twitter 4 skype 5 Line 6 linkedin 7  Instagram 8 snapchat 9


    boolean isSupportTakePhoto;

    @Override
    public boolean isSupportTakePhoto() {
        return isSupportTakePhoto;
    }

    boolean isSupportFindBand;

    @Override
    public boolean isSupportFindBand() {
        return isSupportFindBand;
    }

    @Override
    public boolean isSupportLostRemind() {
        return true;
    }

    public boolean isNeedAlarmListSetting() {
        return true;
    }

    @Override
    public boolean isSupportWristScreen() {
        return true;
    }

    //wechat 位数 0 qq 1 facebookmsg 2 whatspp 3 twitter 4 Line 5 linkedin 6 Instagram 7
    //1代表支持，0代表不支持  11111111 代表 全       支持  10000011 代表支持 WeChat 、linkedin、Instagram

    String supportMsg;

    @Override
    public String getSupportNoticeFunction() {
        return supportMsg;
    }

    @Override
    public boolean isSupportSetSedentarinessTime() {
        return true;
    }

    @Override
    public boolean isFindPhone() {
        if (isSupportAlarm2 == false) {
            return false;
        }
        return true;
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
        vpOperateManager.startDetectBP(writeResponse, new IBPDetectDataListener() {
            @Override
            public void onDataChange(BpData bpData) {
                Log.i(TAG, bpData.toString());
                if (bpData.getProgress() == 100) {
                    mIRealDataSubject.bloodPressureChanged(bpData.getHighPressure(), bpData.getLowPressure(), GlobalValue.BLOODPRESSURE_TEST_FINISH);
                }
            }
        }, EBPDetectModel.DETECT_MODEL_PUBLIC);
    }

    @Override
    public void stopBloodTest() {
        vpOperateManager.stopDetectBP(writeResponse, EBPDetectModel.DETECT_MODEL_PUBLIC);
    }
}


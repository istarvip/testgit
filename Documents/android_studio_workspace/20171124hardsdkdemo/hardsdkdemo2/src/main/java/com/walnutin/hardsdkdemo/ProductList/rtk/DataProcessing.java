package com.walnutin.hardsdkdemo.ProductList.rtk;

import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductList.hch.HchDb;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataProcessing;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHeartRateListener;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ISleepListener;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IStepListener;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenliu on 2017/1/13.
 */

public class DataProcessing implements IDataProcessing {


    private IHeartRateListener mIHeartRateListener;
    private IStepListener mIStepListener;
    private ISleepListener mISleepListener;
    private static DataProcessing mDataProcessing;
    private String bufferData = "";
    private IDataCallback mIDataCallBack;
    private final String TAG = DataProcessing.class.getSimpleName();

    private DataProcessing() {

    }

    public static DataProcessing getInstance() {
        if (mDataProcessing == null) {
            mDataProcessing = new DataProcessing();
        }
        return mDataProcessing;
    }

    public void processingData(byte[] value) {

        String data = DigitalTrans.byteArrHexToString(value);
        Log.d(TAG, "processingData: data:"+data);

//        if (data.startsWith("68") && data.endsWith("16")) {
//            bufferData = data;
//            //    Log.i(TAG, data);
//        } else {
//            bufferData += data;
//        }
//
//        if (bufferData.startsWith("68") && data.endsWith("16")) {
//            System.out.println("bufferData: " + bufferData + "  -----------  ");
//
//            String head = bufferData.substring(2, 4);
//            if (head.equals("86")) {  // 心率
//                dealData_86(bufferData);
//            } else if (bufferData.toUpperCase().equals("68A000000816")) {  // 校时完成
//                mIDataCallback.onCallbackResult(true, GlobalValue.SYNC_TIME_OK);
//            } else if (head.equals("A5")) {
//                dealData_A5(bufferData);
//            } else if (head.toUpperCase().equals("A6")) {           // 全天总数据  同步中
//                if (lastBufferData.equals(bufferData)) {
//                    bufferData = "";
//                    return;
//                }
//                lastBufferData = bufferData;
//                if (DigitalTrans.getHexTodayDate().equals(bufferData.substring(8, 14))) {  // 同步今天
//                    dealData_A6(bufferData);
//                    bufferData = "";
//                    return;
//                }
//                /*
//                * 同步历史
//                * */
//                if (bufferData.substring(4, 6).equals("20")) {
//                    dealOfflineStep(bufferData);
//                } else if (bufferData.substring(4, 6).toUpperCase().equals("C4")) { // 回答记步完
//                    dealDetailStep(bufferData);
//                    mIDataCallback.onSynchronizingResult(bufferData, true, GlobalValue.OFFLINE_STEP_SYNC_OK);
//                } else if (bufferData.substring(4, 6).toUpperCase().equals("BA")) {  // 回答心率
//                    dealHeartData(bufferData);
//                    mIDataCallback.onSynchronizingResult(bufferData, false, GlobalValue.OFFLINE_HEART_SYNCING); // false 表示 非今日
//
//                } else if (bufferData.substring(4, 6).toUpperCase().equals("28")) { // 回答睡眠
//                    dealSleep(bufferData);
//                    mIDataCallback.onSynchronizingResult(bufferData, true, GlobalValue.OFFLINE_SLEEP_SYNC_OK);
//                }
//            } else if (head.toUpperCase().equals("A7")) {
//                mIDataCallback.onCallbackResult(true, GlobalValue.SYNC_FINISH);
//            }
//
//            bufferData = "";
//        }

    }


    public void setHeartRateListener(IHeartRateListener iHeartRateListener) {
        this.mIHeartRateListener = iHeartRateListener;
    }


    public void setStepListener(IStepListener iStepListener) {
        this.mIStepListener = iStepListener;
    }

    public void setSleepListener(ISleepListener iSleepListener) {
        this.mISleepListener = iSleepListener;
    }

    /**
     * 向hchsdk上传处理后的结果
     *
     * @param dataCallback
     */
    public void setDataCallback(IDataCallback dataCallback) {
        this.mIDataCallBack = dataCallback;
    }

    private void dealData_A5(String bufferData) {

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2000 + DigitalTrans.hexStringToAlgorism(bufferData.substring(12, 14)));//年
        c1.set(Calendar.MONTH, DigitalTrans.hexStringToAlgorism(bufferData.substring(10, 12)) - 1);//月
        c1.set(Calendar.DATE, DigitalTrans.hexStringToAlgorism(bufferData.substring(8, 10)));//日

        int day1 = c1.get(Calendar.DAY_OF_YEAR);
        String today = DigitalTrans.getHexTodayDate();

        c1.set(Calendar.YEAR, 2000 + DigitalTrans.hexStringToAlgorism(today.substring(4, 6)));//年
        c1.set(Calendar.MONTH, DigitalTrans.hexStringToAlgorism(today.substring(2, 4)) - 1);//月
        c1.set(Calendar.DATE, DigitalTrans.hexStringToAlgorism(today.substring(0, 2)));//日

        int day2 = c1.get(Calendar.DAY_OF_YEAR);

        if (bufferData.substring(4, 6).toUpperCase().equals("1B")) { // 回答疲劳度
            mIDataCallBack.onSynchronizingResult(bufferData, true, GlobalValue.FATIGE);
        }
        if (Math.abs(day1 - day2) == 1) {        // 历史同步完成
            mIDataCallBack.onResult(null,true, GlobalValue.OFFLINE_SYNC_OK);
        }
    }

    private void dealData_86(String bufferData) {

        if (bufferData.length() < 20) {
            String turnStatus = bufferData.substring(8, 10); // 开关
            if (turnStatus.equals("02")) { // 6886010002F116 关闭状态            6886010001F016 打开状态
                mIHeartRateListener.onHeartRateChange(0, GlobalValue.RATE_TEST_FINISH);
            } else {
                mIHeartRateListener.onHeartRateChange(0, 0);
            }
        } else {
            int heartValue = Integer.valueOf(DigitalTrans.hexStringToAlgorism(bufferData.substring(10, 12))); // 心率值
            mIHeartRateListener.onHeartRateChange(heartValue, 0);

            String step = DigitalTrans.reverseHexHighTwoLow(bufferData.substring(12, 20)); //总记步
            int stepNum = DigitalTrans.hexStringToAlgorism(step);

            String dis = DigitalTrans.reverseHexHighTwoLow(bufferData.substring(20, 28)); //总路程
            int disNum = DigitalTrans.hexStringToAlgorism(dis);
            float distance = disNum / 1000f;

            String cal = DigitalTrans.reverseHexHighTwoLow(bufferData.substring(28, 34)); //总卡路里
            int calNum = DigitalTrans.hexStringToAlgorism(cal);

            System.out.println("step: " + stepNum + " dis: " + distance + " cal: " + calNum);
            mIStepListener.onStepChange(stepNum, distance, calNum);
        }
    }

    String lastBufferData = "";

    private void dealData_A6(String bufferData) {
        //   System.out.println("lastBufferData:" + lastBufferData);

        if (bufferData.substring(4, 6).equals("20")) {
            dealRealTimeStep(bufferData);
        } else if (bufferData.substring(4, 6).toUpperCase().equals("C4")) { // 回答全天详细记步的数据
            dealRealTimeDetailStep(bufferData);
        } else if (bufferData.substring(4, 6).toUpperCase().equals("BA")) {  // 心率详细数据
            dealRealTimeHeartRate(bufferData);
        } else if (bufferData.substring(4, 6).toUpperCase().equals("28")) { // 睡眠详细数据
            dealRealTimeSleep(bufferData);
        }

    }


    private void dealRealTimeHeartRate(String bufferData) {
        dealHeartData(bufferData);

        if (Integer.valueOf(bufferData.substring(18, 20)) == 8) {
            mIDataCallBack.onResult(null,true, GlobalValue.HEART_FINISH);
            return;
        }
        mIDataCallBack.onSynchronizingResult(bufferData, true, GlobalValue.OFFLINE_HEART_SYNCING);
    }

    private void dealHeartData(String bufferData) {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2000 + DigitalTrans.hexStringToAlgorism(bufferData.substring(12, 14)));//年
        c1.set(Calendar.MONTH, DigitalTrans.hexStringToAlgorism(bufferData.substring(10, 12)) - 1);//月
        c1.set(Calendar.DATE, DigitalTrans.hexStringToAlgorism(bufferData.substring(8, 10)));//日
        String sf = TimeUtil.formatYMD(c1.getTime()); // sf 为 日期 如2017-01-01
        int serial = Integer.parseInt(bufferData.substring(18, 20)); // 包号
        List<HeartRateModel> heartRateModelList = new ArrayList<>();

        String data = bufferData.substring(20, 20 + 180 * 2); // 心率数据部分
        String heartValue = "";

        if (sf.equals(TimeUtil.getCurrentDate())) {
            int hour = c1.get(Calendar.HOUR_OF_DAY);
            if ((serial - 1) * 3 > hour) {
                return;
            }
        }

        for (int i = 0; i < 180; i++) {
            heartValue = data.substring(i * 2, i * 2 + 2);
            if (!heartValue.equals("FF")) {
                int value = DigitalTrans.hexStringToAlgorism(heartValue);
                if (value > 0) {
                    HeartRateModel heartRateModel = new HeartRateModel();
                    int miniute = 180 * (serial - 1) + i;
                    heartRateModel.currentRate = value;
                    heartRateModel.testMomentTime = TimeUtil.MinitueToDetailTimeByDate(sf, miniute);
                    heartRateModel.account = HardSdk.getInstance().getAccount();
                    heartRateModelList.add(heartRateModel);
                    //    System.out.println(heartRateModel.testMomentTime + " -> " + value);
                }
            }
        }

        SqlHelper.instance().syncBraceletHeartData(heartRateModelList);

    }


    private void dealRealTimeSleep(String bufferData) {
        dealSleep(bufferData);

        mIDataCallBack.onResult(null,true, GlobalValue.SLEEP_SYNC_OK);


    }

    private void dealSleep(String bufferData) {

        String date = DigitalTrans.getDateByHexString(bufferData.substring(8, 14)); // 得到睡眠日期

        String beforeOneDate = DigitalTrans.getBeforeOneDateByHexString(bufferData.substring(8, 14)); // 得到睡眠日期

        String data = bufferData.substring(16, 16 + 36 * 2); // 睡眠部分数据

        HchDb.insertOrUpdate(date, data);

        String beforeData = HchDb.getSleepDataByDate(beforeOneDate);

        //   beforeData = "FE9BAFBABAAABEEAAAAAABAAABAAFFFFFFFFFFFABFFFFFFFFFFFFFFFFFFFFFFFFFFBFFFF";
        //  data = "FFEABDAFFFABCDFFFFFFFFFBFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        parseSleep(date, beforeData, data); // 指定同步日期 前一天 和指定日期的睡眠数据

        if (date.equals(TimeUtil.getCurrentDate())) { //如果是今天
            mISleepListener.onSleepChange();
        }
    }

    private void parseSleep(String date, String beforeData, String data) {
        //  String beforeData ="FE9BAFBABAAABEEAAAAAABAAABAAFFFFFFFFFFFABFFFFFFFFFFFFFFFFFFFABFFFFFFFFFF";
        //   String data ="FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFAFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        //beforeData = null;
        System.out.println(" start tiime: " + System.currentTimeMillis());
        //System.out.println(data.substring(20 * 2, 20 * 2 + 2));
        int wakeIndex = 0; // 醒来起床时状态的字节数组下标
        int todayStartSleepIndex = 0;
        ArrayList<Integer> timePointArray = new ArrayList<Integer>();
        ArrayList<Integer> sleepStatusArray = new ArrayList<Integer>();
        ArrayList<Integer> duraionTimeArray = new ArrayList<Integer>();
        int deepTime = 0;
        int lightTime = 0;
        int wakeTime = 0;
        String validSleep = "";
        if (beforeData == null) {
            for (int i = 0; i < 27; i++) {
                String wake = data.substring(54 - i * 2, 54 - i * 2 + 2);
                if (!wake.equals("FF")) {
                    wakeIndex = 27 - i;
                    break;
                }
            }
            if (wakeIndex == 0) {
                return;
            }
            for (int i = 0; i < 27; i++) {
                String wake = data.substring(i * 2, i * 2 + 2);
                if (!wake.equals("FF")) {
                    todayStartSleepIndex = i;
                    break;
                }
            }
            if (todayStartSleepIndex >= wakeIndex) {
                return;
            }
            validSleep = data.substring(todayStartSleepIndex * 2,
                    wakeIndex * 2 + 2);
        } else {
            data = beforeData + data;
            for (int i = 62; i > 27; i--) {
                String wake = data.substring(i * 2, i * 2 + 2);
                if (!wake.equals("FF")) {
                    wakeIndex = i;
                    break;
                }
            }

            if (wakeIndex <= 27) {
                return;
            }

            for (int i = 27; i < 63; i++) { // 昨日18点 - 今天 18点 区间
                String wake = data.substring(i * 2, i * 2 + 2);
                if (!wake.equals("FF")) {
                    todayStartSleepIndex = i;
                    break;
                }
            }

            if (todayStartSleepIndex >= wakeIndex) {
                return;
            }
            validSleep = data.substring(todayStartSleepIndex * 2,
                    wakeIndex * 2 + 2);

        }

        int totalTime = validSleep.length() / 2 * 40; // 总睡眠
        String binaryString = DigitalTrans.hexStringToBinary(validSleep);
        //   System.out.println("validSleep:" + validSleep);
        int endMinitue = 40 * todayStartSleepIndex;
        //   System.out.println("startMinitue: " + endMinitue); // 结束时刻
        int len = binaryString.length() / 2;

        for (int i = 0; i < len; i++) {
            int value = DigitalTrans.binaryToAlgorism(binaryString
                    .substring(i * 2, i * 2 + 2));
            duraionTimeArray.add(10);
            timePointArray.add((endMinitue + 10 * i + 10) % 1440); // 每一段结束时刻
            if (value == 0 || value == 3) {
                sleepStatusArray.add(2); // 2清醒 1浅睡 0 深睡
                wakeTime += 10;
            } else if (value == 1) {
                sleepStatusArray.add(1); // 2清醒 1浅睡 0 深睡
                lightTime += 10;

            } else if (value == 2) {
                sleepStatusArray.add(0); // 2清醒 1浅睡 0 深睡
                deepTime += 10;
            }

        }

        //   System.out.println("total: " + totalTime + " light: " + lightTime
        //            + " deep: " + deepTime + " wake：" + wakeTime);
        correctSleepArray(date, sleepStatusArray, duraionTimeArray, timePointArray, totalTime, lightTime, deepTime);
        //     System.out.println(" end time: " + System.currentTimeMillis());
    }

    void correctSleepArray(String date, List<Integer> sleepStatusArray,
                           List<Integer> duraionTimeArray, List<Integer> timePointArray, int total, int light, int deepTime) {
        ArrayList<Integer> newtimePointArray = new ArrayList<Integer>();
        ArrayList<Integer> newsleepStatusArray = new ArrayList<Integer>();
        ArrayList<Integer> newduraionTimeArray = new ArrayList<Integer>();
        int len = sleepStatusArray.size();
//        for (int i = 0; i < len; i++) {
//            System.out.print(" sleepStatusArray: " + sleepStatusArray.get(i));
//            System.out.print("  duraionTimeArray: " + duraionTimeArray.get(i));
//            System.out.println("  timePointArray: " + timePointArray.get(i));
//
//        }
        //     System.out.println("-----------------------------");
        //    len = sleepStatusArray.size();
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
            newduraionTimeArray.add(10 * (index + 1));
            newtimePointArray.add((timePointArray.get(i) + 10 * index) % 1440);
            i = i + index + 1;
        }
        //     System.out.println("------------------------------");
        len = newsleepStatusArray.size();
        int[] dtArray = new int[len];
        int[] statusArray = new int[len];
        int[] pointArray = new int[len];
        for (int i = 0; i < len; i++) {
            statusArray[i] = newsleepStatusArray.get(i);
            dtArray[i] = newduraionTimeArray.get(i);
            pointArray[i] = newtimePointArray.get(i);
//            System.out.print(" sleepStatusArray: " + newsleepStatusArray.get(i));
//            System.out.print("  duraionTimeArray: " + newduraionTimeArray.get(i));
//            System.out.println("  timePointArray: " + newtimePointArray.get(i));
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
            sleepModel.date = date;
            sleepModelList.add(sleepModel);

            SqlHelper.instance().syncBraceletSleepData(sleepModelList);
        }
    }

    private void dealRealTimeDetailStep(String bufferData) {

        dealDetailStep(bufferData);
        mIDataCallBack.onResult(null,true, GlobalValue.STEP_FINISH); // 记步完成

    }

    public void dealDetailStep(String bufferData) {

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2000 + DigitalTrans.hexStringToAlgorism(bufferData.substring(12, 14)));//年
        c1.set(Calendar.MONTH, DigitalTrans.hexStringToAlgorism(bufferData.substring(10, 12)) - 1);//月
        c1.set(Calendar.DATE, DigitalTrans.hexStringToAlgorism(bufferData.substring(8, 10)));//日

        String time = TimeUtil.formatYMD(c1.getTime());

        StepInfos stepInfos = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(), time);

        String data = bufferData.substring(16, 16 + 8 * 24);
        Map<Integer, Integer> stepOneHourInfo = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            String val = data.substring(i * 8, i * 8 + 8);
            if (!val.equals("FFFFFFFF")) {
                int hourStep = DigitalTrans.hexStringToAlgorism(DigitalTrans.reverseHexHighTwoLow(val));
                int hour = 60 * i;
                stepOneHourInfo.put(hour, hourStep);
            }
        }

        stepInfos.setStepOneHourInfo(stepOneHourInfo);
        SqlHelper.instance().insertOrUpdateTodayStep(stepInfos);
    }

    private void dealOfflineStep(String bufferData) {
        dealStep(bufferData);
        mIDataCallBack.onResult(bufferData, true, GlobalValue.OFFLINE_STEP_SYNCING);
    }

    private void dealRealTimeStep(String bufferData) {

        dealStep(bufferData);
        mIDataCallBack.onResult(null,true, GlobalValue.STEP_SYNCING); // 记步完成

    }

    public void dealStep(String bufferData) {
        String step = DigitalTrans.reverseHexHighTwoLow(bufferData.substring(16, 24)); //总记步
        int stepNum = DigitalTrans.hexStringToAlgorism(step);
        String cal = DigitalTrans.reverseHexHighTwoLow(bufferData.substring(24, 32)); //总卡路里
        int calNum = DigitalTrans.hexStringToAlgorism(cal);
        String dis = DigitalTrans.reverseHexHighTwoLow(bufferData.substring(32, 40)); //总路程
        int disNum = DigitalTrans.hexStringToAlgorism(dis);
        float distance = disNum / 1000f;

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2000 + DigitalTrans.hexStringToAlgorism(bufferData.substring(12, 14)));//年
        c1.set(Calendar.MONTH, DigitalTrans.hexStringToAlgorism(bufferData.substring(10, 12)) - 1);//月
        c1.set(Calendar.DATE, DigitalTrans.hexStringToAlgorism(bufferData.substring(8, 10)));//日

        String time = TimeUtil.formatYMD(c1.getTime());

        StepInfos stepInfos = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(), time);
        stepInfos.setStep(stepNum);
        stepInfos.setDistance(distance);
        stepInfos.setCalories(calNum);
        stepInfos.setAccount(HardSdk.getInstance().getAccount());
        stepInfos.setDates(TimeUtil.getCurrentDate());
        SqlHelper.instance().insertOrUpdateTodayStep(stepInfos);

        if (time.equals(TimeUtil.getCurrentDate())) {
            mIStepListener.onStepChange(stepNum, distance, calNum);
        }

    }

}

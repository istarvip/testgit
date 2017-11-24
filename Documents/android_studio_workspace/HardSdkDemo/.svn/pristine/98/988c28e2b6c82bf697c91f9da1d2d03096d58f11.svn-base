package com.walnutin.hardsdkdemo.ProductList.rtk;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataProcessing;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IRealDataListener;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ISleepListener;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.ExerciseData;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Lats;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SportData;
import com.walnutin.hardsdkdemo.ProductNeed.entity.TenData;
import com.walnutin.hardsdkdemo.utils.Config;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chenliu on 2017/1/13.
 */

public class DataProcessing implements IDataProcessing {


    // private IHeartRateListener mIHeartRateListener;
    // private IStepListener mIStepListener;
    private ISleepListener mISleepListener;
    private IRealDataListener mIRealDataListener;
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

        String cmd = data.substring(0, 2); // 包头
        //  Log.d(TAG, "processingData: data:" + data);

        if (cmd.toUpperCase().toString().equals("2E")) {
            dealWith2E(data);

        } else if (cmd.equals("33")) {  // 实时数据
            dealWith33(data);

        } else if (cmd.equals("36")) {  // 运动数据
            dealWith36(data);
        } else if (cmd.equals("38")) {  //十分钟数据 自动上传

            dealWithTenDataAuto(data);
        } else if (cmd.equals("39")) {  //十分钟数据 手动上传

            dealWithTenDataByHandles(data);
        } else if (cmd.toUpperCase().toString().equals("1A")) { //睡眠
            dealWithSleep(data);
        } else if (cmd.toUpperCase().toString().equals("54")) { //睡眠 天详细数据
            dealWithDayDetailSleep(data);
        } else if (cmd.toUpperCase().toString().equals("4D")) { //锻炼数据
            dealWithExcise(data);
        } else if (cmd.toUpperCase().toString().equals("53")) { //地图数据
            dealWithMap(data);
        }

    }

    //    private boolean isGetVersion;
    //2E 16 08 00 01 00 15 12 00 3C 02 9CE6245277DE0000
    // 2E 17 02 01 00 01 21 00 00 34 01 7D 86C9BABCE10000
    private void dealWith2E(String data) {  //版本信息

//        if(isGetVersion){
//            return;
//        }
        int type = 256 * DigitalTrans.hexStringToAlgorism(data.substring(2, 4)) +
                DigitalTrans.hexStringToAlgorism(data.substring(4, 6));
        int version = 256 * DigitalTrans.hexStringToAlgorism(data.substring(18, 20)) +
                DigitalTrans.hexStringToAlgorism(data.substring(20, 22));
        String ziku = data.substring(6, 8);
        MySharedPf.getInstance(HardSdk.getInstance().getContext()).setZikuType(ziku); // 字库类型
        boolean isSupportBlood = Integer.valueOf(data.substring(8, 10)) == 1 ? true : false;
        boolean isSupportGPS = Integer.valueOf(data.substring(10, 12)) == 1 ? true : false;
        MySharedPf.getInstance(HardSdk.getInstance().getContext()).setIsSupportBlood(isSupportBlood); //
        MySharedPf.getInstance(HardSdk.getInstance().getContext()).setIsSupportGPS(isSupportGPS); //
        MySharedPf.getInstance(HardSdk.getInstance().getContext()).setBraceletType(type); //
        MySharedPf.getInstance(HardSdk.getInstance().getContext()).setBraceletVersion(version); //


        mIDataCallBack.onResult(null, true, GlobalValue.Firmware_Version);
//        isGetVersion= true;
//        EventBus.getDefault().post(new CommonSupportType()); //todo 此处发送手环类型，供界面逻辑转换。暂时屏蔽.
    }

    String daySleep = "";

    private void dealWithDayDetailSleep(String data) {

        int serialTotal = DigitalTrans.hexStringToAlgorism(data.substring(2, 4));
        int serial = DigitalTrans.hexStringToAlgorism(data.substring(4, 6));
        String validSleep = data.substring(6, data.length());  //

        if (serial == 0) {
            daySleep = "";
        }
        daySleep += validSleep;
        if (serial >= 10) {
            mIDataCallBack.onResult(null, true, GlobalValue.DAY_SLEEP_OK);
        }
        Log.i(TAG, "dealWithDayDetailSleep: " + serial);
    }


    //1a141108 0f000000 0011017e 01000000 001605
    private void dealWithSleep(String data) {
        //    int yearH = DigitalTrans.hexStringToAlgorism(data.substring(2, 4));
        //    int yearL = DigitalTrans.hexStringToAlgorism(data.substring(4, 6));
        int month = DigitalTrans.hexStringToAlgorism(data.substring(6, 8));
        int day = DigitalTrans.hexStringToAlgorism(data.substring(8, 10));
        int year = DigitalTrans.hexStringToAlgorism(data.substring(4, 6) + data.substring(2, 4));
        String time = DigitalTrans.formatData(String.valueOf(year)) + "-" + DigitalTrans.formatData(month + "") + "-" + DigitalTrans.formatData(day + "");
        if (Calendar.getInstance().get(Calendar.YEAR) < Integer.valueOf(String.valueOf(year))) {
            return;
        }
        int deepSleep = DigitalTrans.hexStringToAlgorism(data.substring(18, 20)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(20, 22));
        int lightSleep = DigitalTrans.hexStringToAlgorism(data.substring(22, 24)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(24, 26));
        int soberSleep = DigitalTrans.hexStringToAlgorism(data.substring(26, 28)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(28, 30));
        int soberTimes = DigitalTrans.hexStringToAlgorism(data.substring(30, 32)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(32, 34));

        SleepModel sleepModel = SqlHelper.instance().getOneDaySleepListTime(HardSdk.getInstance().getAccount(), time);
        sleepModel.account = HardSdk.getInstance().getAccount();
        sleepModel.date = time;
        sleepModel.deepTime = deepSleep;
        sleepModel.lightTime = lightSleep;
        sleepModel.soberTime = soberSleep;
        sleepModel.soberNum = soberTimes; // 清醒次数
        sleepModel.totalTime = deepSleep + soberSleep + lightSleep;

        int startTime = DigitalTrans.hexStringToAlgorism(data.substring(34, 36)) * 60
                + DigitalTrans.hexStringToAlgorism(data.substring(36, 38)); // 多少分钟

        sleepModel.startSleep = TimeUtil.MinitueToHHMM(startTime);
        int endTime = (startTime + sleepModel.totalTime) % 1440;
        sleepModel.endSleep = TimeUtil.MinitueToHHMM(endTime);

        //   Log.i(TAG, "sleep time: " + time + " startTime: " + startTime + " HH:MM ->" + sleepModel.startSleep + " endSleep:" + sleepModel.endSleep);

        if (time.equals("00-00-00")) {
//            EventBus.getDefault().post(new SleepChange());   //todo 睡眠变化的广播，应该通过回调告知,暂时屏蔽
            mIDataCallBack.onResult(null, true, GlobalValue.SLEEP_SYNC_OK);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIDataCallBack.onResult(null, true, GlobalValue.SYNC_FINISH);
                }
            }, 3000);
        } else {
            if (daySleep.length() > 0 && time.equals(TimeUtil.getYesterdayDate()) && sleepModel.totalTime > 0) {
                String binaryString = DigitalTrans.hexStringToBinary(daySleep);
                binaryString = getValidBinarySleep(binaryString, startTime, sleepModel.totalTime);
                if (startTime > 18 * 60) {
                    time = TimeUtil.getCurrentDate();
                }
                toC(time, binaryString, startTime, sleepModel.totalTime, sleepModel.deepTime, sleepModel.getLightTime(), sleepModel.getSoberTime());
//                EventBus.getDefault().post(new SleepChange());//todo 睡眠变化的广播，应该通过回调告知,暂时屏蔽
            } else {
                SqlHelper.instance().insertOrUpdateSleepData(HardSdk.getInstance().getAccount(), sleepModel);
            }
        }
    }

    void toC(String date, String valid, int start, int total, int deepTime, int light, int soberTime) {

        ArrayList<Integer> timePointArray = new ArrayList<Integer>();
        ArrayList<Integer> sleepStatusArray = new ArrayList<Integer>();
        ArrayList<Integer> duraionTimeArray = new ArrayList<Integer>();
//        int deepTime = 0;
//        int lightTime = 0;
//        int wakeTime = 0;
        int endMinitue = start;

        String binaryString = valid;
        int len = binaryString.length();
        for (int i = 0; i < len; i++) {
            int value = DigitalTrans.binaryToAlgorism(binaryString.substring(i, i + 1));
            duraionTimeArray.add(1);
            timePointArray.add((endMinitue + 1 * i + 1) % 1440); // 每一段结束时刻
            if (value == 0) {
                sleepStatusArray.add(1); // 2清醒 1浅睡 0 深睡
                //      lightTime += 1;
            } else if (value == 1) {
                sleepStatusArray.add(0); // 2清醒 1浅睡 0 深睡
                //      deepTime += 1;

            }

        }
        //    System.out.println(" light: " + lightTime  + " deep: " + deepTime + " wake：" + wakeTime);
        correctSleepArray(date, sleepStatusArray, duraionTimeArray, timePointArray, 1, total, deepTime, light, soberTime);
        //     System.out.println(" end tiime: " + System.currentTimeMillis());
    }

    static void correctSleepArray(String date, List<Integer> sleepStatusArray,
                                  List<Integer> duraionTimeArray, List<Integer> timePointArray,
                                  int duraionTime, int total, int deepTime, int light, int soberTime) {
        ArrayList<Integer> newtimePointArray = new ArrayList<Integer>();
        ArrayList<Integer> newsleepStatusArray = new ArrayList<Integer>();
        ArrayList<Integer> newduraionTimeArray = new ArrayList<Integer>();
        int len = sleepStatusArray.size();
        for (int i = 0; i < len; i++) {
            System.out.print(" sleepStatusArray: " + sleepStatusArray.get(i));
            System.out.print("  duraionTimeArray: " + duraionTimeArray.get(i));
            System.out.println("  timePointArray: " + timePointArray.get(i));

        }
        len = sleepStatusArray.size();
        for (int i = 0; i < sleepStatusArray.size(); ) {
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
            newtimePointArray
                    .add((timePointArray.get(i) + duraionTime * index) % 1440);
            i = i + index + 1;
        }
        System.out.println("------------------------------");
        len = newsleepStatusArray.size();
        int[] dtArray = new int[len];
        ;
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
        }

    }

    String getValidBinarySleep(String daySleep, int startSleep, int totalTime) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minitue = calendar.get(Calendar.MINUTE);

        int yesterStartIndex = hour * 60 + minitue + 1440 - 1496;
        int startIndex = startSleep - yesterStartIndex;
        if (startIndex < 0) {
            startIndex = 0;
        }
        return daySleep.substring(startIndex, startIndex + totalTime);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private void dealWithTenDataAuto(String data) {

        int heart = DigitalTrans.hexStringToAlgorism(data.substring(2, 4));
        int step = 256 * DigitalTrans.hexStringToAlgorism(data.substring(4, 6)) +
                DigitalTrans.hexStringToAlgorism(data.substring(6, 8));
        int serial = 256 * DigitalTrans.hexStringToAlgorism(data.substring(8, 10)) +
                DigitalTrans.hexStringToAlgorism(data.substring(10, 12));

        int dbp = DigitalTrans.hexStringToAlgorism(data.substring(12, 14)); //dbp
        int sbp = DigitalTrans.hexStringToAlgorism(data.substring(14, 16)); //sbp


        TenData tenData = new TenData();
        tenData.setDate(TimeUtil.getCurrentDate());
        tenData.setAccount(HardSdk.getInstance().getAccount());
        tenData.setMoment(serial * 10);
        tenData.setSbp(sbp);
        tenData.setDbp(dbp);
        if (heart != 255) {
            tenData.setHeart(heart);
        }
        tenData.setStep(step);
        SqlHelper.instance().insertOrUpdateTenData(tenData);
    }

    List<TenData> tenDataList = new ArrayList<>();

    private boolean isSerialAlready;

    private void dealWithTenDataByHandles(String data) {
        int heart = DigitalTrans.hexStringToAlgorism(data.substring(2, 4));
        int step = 256 * DigitalTrans.hexStringToAlgorism(data.substring(4, 6)) +
                DigitalTrans.hexStringToAlgorism(data.substring(6, 8));

        int serial = 256 * DigitalTrans.hexStringToAlgorism(data.substring(8, 10)) +
                DigitalTrans.hexStringToAlgorism(data.substring(10, 12));

        int dbp = DigitalTrans.hexStringToAlgorism(data.substring(12, 14)); //dbp
        int sbp = DigitalTrans.hexStringToAlgorism(data.substring(14, 16)); //sbp

        int currentMinitue = Calendar.getInstance().get(Calendar.MINUTE);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMoment = (hour * 60 + currentMinitue) / 10;

        TenData tenData = new TenData();
        tenData.setDate(TimeUtil.getCurrentDate());
        tenData.setAccount(HardSdk.getInstance().getAccount());
        tenData.setMoment(serial * 10);
        tenData.setSbp(sbp);
        tenData.setDbp(dbp);
        if (heart != 255) {
            tenData.setHeart(heart);
        }
        tenData.setStep(step);

        if (serial == 0) {
            isSerialAlready = false;
            tenDataList.clear();
        }

        tenDataList.add(tenData);
        //   Log.i(TAG, " serial: " + serial + " currentMoment: " + currentMoment + " sbp:" + sbp + " dbp:" + dbp);
        if (serial >= currentMoment && isSerialAlready == false) {
            isSerialAlready = true;
            SqlHelper.instance().insertTenDataList(tenDataList);
            mIDataCallBack.onResult(null, true, GlobalValue.STEP_SYNCING);
        }

        //     System.out.println("sdb:" + sbp + " dbp:" + dbp + " serial:" + serial + " step:" + step + " heart:" + heart + "currentMoment:" + currentMoment);

    }

    List<Lats> latsList = new ArrayList<>();
    ExerciseData exerciseData;

    //53021a01 1411080f 08343000 12745801 184ec906      53010A01E1070701091E3200AC525801A879C906
    private void dealWithMap(String data) {
        int totalSerial = DigitalTrans.hexStringToAlgorism(data.substring(4, 6));
        int currentSerial = DigitalTrans.hexStringToAlgorism(data.substring(6, 8));

        if (currentSerial == 1) {
            latsList.clear();
            //  int yearH = DigitalTrans.hexStringToAlgorism(data.substring(8, 10));
            //  int yearL = DigitalTrans.hexStringToAlgorism(data.substring(10, 12));
            int year = DigitalTrans.hexStringToAlgorism(data.substring(10, 12) + data.substring(8, 10));
            int month = DigitalTrans.hexStringToAlgorism(data.substring(12, 14));
            int day = DigitalTrans.hexStringToAlgorism(data.substring(14, 16));
            int hour = DigitalTrans.hexStringToAlgorism(data.substring(16, 18));
            int minitue = DigitalTrans.hexStringToAlgorism(data.substring(18, 20));

            String longitude = data.substring(24, 32);
            String latitude = data.substring(32, 40);

            float lat = DigitalTrans.hexStringToAlgorism(DigitalTrans.reverseHexHighTwoLow(latitude)) / 1000000f;   // 纬度
            float lon = DigitalTrans.hexStringToAlgorism(DigitalTrans.reverseHexHighTwoLow(longitude)) / 1000000f; // 经度

            String date = DigitalTrans.formatData(String.valueOf(year)) + "-" + DigitalTrans.formatData(month + "") +
                    "-" + DigitalTrans.formatData(day + "" + " " + DigitalTrans.formatData(String.valueOf(hour)) + ":" +
                    DigitalTrans.formatData(String.valueOf(minitue))); // yyyy-MM-dd HH:mm
            Log.i(TAG, "date:dealWithMap " + data);
            exerciseData = SqlHelper.instance().getExciseDataByDate(HardSdk.getInstance().getAccount(), date);
            if (exerciseData == null) {
                exerciseData = new ExerciseData();
                exerciseData.setAccount(HardSdk.getInstance().getAccount());
                exerciseData.setDate(date);
            }
            //      Log.i(TAG, "lat: " + lat + " long: " + lon);
            latsList.add(new Lats(lon, lat));  // 添加经纬度
        } else {
            String longitude = data.substring(8, 16);
            String latitude = data.substring(16, 24);

            String longitude2 = data.substring(24, 32);
            String latitude2 = data.substring(32, 40);

            float lat = DigitalTrans.hexStringToAlgorism(DigitalTrans.reverseHexHighTwoLow(latitude)) / 1000000f;   // 纬度
            float lon = DigitalTrans.hexStringToAlgorism(DigitalTrans.reverseHexHighTwoLow(longitude)) / 1000000f; // 经度
            float lat2 = DigitalTrans.hexStringToAlgorism(DigitalTrans.reverseHexHighTwoLow(latitude2)) / 1000000f;   // 纬度
            float lon2 = DigitalTrans.hexStringToAlgorism(DigitalTrans.reverseHexHighTwoLow(longitude2)) / 1000000f; // 经度

            //     Log.i(TAG, "lat: " + lat + " long: " + lon);
            if (lat < 1) {
                return;
            }
            latsList.add(new Lats(lon, lat));  // 添加经纬度
            latsList.add(new Lats(lon2, lat2));  // 添加经纬度

        }
        if (currentSerial == totalSerial && totalSerial != 255) {  // 结束包
            if (TextUtils.isEmpty(exerciseData.getScreenShortPath())) { // 当 地图 没有时才需要 生成地图
                Gson gson = new Gson();
                String latAddr = gson.toJson(latsList);
                //   Log.i(TAG, "latAddr:" + latAddr);
                exerciseData.setLatLngs(latAddr);
                exerciseData.setScreenShortPath(Environment.getExternalStorageDirectory() + "/ruiteke/" + exerciseData.getDate() + ".png");
//                EventBus.getDefault().post(exerciseData);  //todo 数值变化，通知外界，此处应修改优雅
            }
            SqlHelper.instance().insertOrUpdateExcise(exerciseData);
        }

    }


    //<4df10100 00240000 02141108 0e0a1037 00000c0d>
    private void dealWithExcise(String data) {
        handler.removeCallbacksAndMessages(null);
        int step = DigitalTrans.hexStringToAlgorism(data.substring(2, 4)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(4, 6));
        int distance = (DigitalTrans.hexStringToAlgorism(data.substring(6, 8)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(8, 10))) * 10; // 米

        int calo = DigitalTrans.hexStringToAlgorism(data.substring(10, 12)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(12, 14));


        int circle = DigitalTrans.hexStringToAlgorism(data.substring(14, 16));

        int type = DigitalTrans.hexStringToAlgorism(data.substring(16, 18));
        int year = DigitalTrans.hexStringToAlgorism(data.substring(20, 22) + data.substring(18, 20));

        //    int yearH = DigitalTrans.hexStringToAlgorism(data.substring(18, 20));
        //    int yearL = DigitalTrans.hexStringToAlgorism(data.substring(20, 22));
//        if (Calendar.getInstance().get(Calendar.YEAR) < Integer.valueOf(String.valueOf(yearH) + String.valueOf(yearL))) {
//            return;
//        }
        int month = DigitalTrans.hexStringToAlgorism(data.substring(22, 24));
        int day = DigitalTrans.hexStringToAlgorism(data.substring(24, 26));
        int hour = DigitalTrans.hexStringToAlgorism(data.substring(26, 28));
        int minitue = DigitalTrans.hexStringToAlgorism(data.substring(28, 30));
        int second = DigitalTrans.hexStringToAlgorism(data.substring(30, 32));
        int week = DigitalTrans.hexStringToAlgorism(data.substring(32, 34));

        String date = DigitalTrans.formatData(String.valueOf(year)) + "-" + DigitalTrans.formatData(month + "") +
                "-" + DigitalTrans.formatData(day + "" + " " + DigitalTrans.formatData(String.valueOf(hour)) + ":" +
                DigitalTrans.formatData(String.valueOf(minitue))); // yyyy-MM-dd HH:mm
        //4D0000000000000202E107081E0A082400000304
        Log.i(TAG, " dealWithExcise date:" + date);
        if (step == 0 && calo == 0 && distance == 0 || data.startsWith("00-00-00")) {
            mIDataCallBack.onResult(null, true, GlobalValue.SYNC_FINISH);

            return;
        }
        int exciseHour = DigitalTrans.hexStringToAlgorism(data.substring(34, 36));
        int exciseM = DigitalTrans.hexStringToAlgorism(data.substring(36, 38));
        int exciseSecond = DigitalTrans.hexStringToAlgorism(data.substring(38, 40));
        int duration = exciseHour * 3600 + exciseM * 60 + exciseSecond;

        ExerciseData exerciseData = SqlHelper.instance().getExciseDataByDate(HardSdk.getInstance().getAccount(), date);
        if (exerciseData == null) {
            exerciseData = new ExerciseData();
            exerciseData.setAccount(HardSdk.getInstance().getAccount());
            exerciseData.setDate(date);
        }
        exerciseData.setDuration(duration);
        exerciseData.setType(type);
        exerciseData.setCircles(circle);
        exerciseData.setStep(step);
        exerciseData.setCalories(calo);
        exerciseData.setDistance(distance);
        switch (type) {
            case Config.WALK_TYPE:
                exerciseData.setTarget(MySharedPf.getInstance(HardSdk.getInstance().getContext()).getStepGoal());
                break;
            case Config.RUNNING_TYPE:
                exerciseData.setTarget(MySharedPf.getInstance(HardSdk.getInstance().getContext()).getRunGoal() * 1000); // 转化为 米 保存
                break;
            case Config.MOUNTAIN_TYPE:
                exerciseData.setTarget(MySharedPf.getInstance(HardSdk.getInstance().getContext()).getClimbGoal());  // 登山
                break;
            case Config.RIDING_TYPE:
                exerciseData.setTarget(MySharedPf.getInstance(HardSdk.getInstance().getContext()).getCyclingGoal() * 1000); // 骑行
                break;
            case Config.SWIMMING_TYPE:
                exerciseData.setTarget(MySharedPf.getInstance(HardSdk.getInstance().getContext()).getSwimGoal());
                break;

        }

        SqlHelper.instance().insertOrUpdateExcise(exerciseData);
    }

    private void dealWith36(String data) {  // 运动数据
        int maxHeart = DigitalTrans.hexStringToAlgorism(data.substring(30, 32));
        int minHeart = DigitalTrans.hexStringToAlgorism(data.substring(32, 34));
        int step = DigitalTrans.hexStringToAlgorism(data.substring(2, 4)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(4, 6));

        int calo = DigitalTrans.hexStringToAlgorism(data.substring(10, 12)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(12, 14));
        int calo2 = DigitalTrans.hexStringToAlgorism(data.substring(14, 16)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(16, 18));

        int distance = DigitalTrans.hexStringToAlgorism(data.substring(6, 8)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(8, 10)); // 米

        //   int yearH = DigitalTrans.hexStringToAlgorism(data.substring(18, 20));
        //  int yearL = DigitalTrans.hexStringToAlgorism(data.substring(20, 22));

        calo = calo2 + calo;
        int month = DigitalTrans.hexStringToAlgorism(data.substring(22, 24));
        int day = DigitalTrans.hexStringToAlgorism(data.substring(24, 26));
        int year = DigitalTrans.hexStringToAlgorism(data.substring(20, 22) + data.substring(18, 20));
        String time = DigitalTrans.formatData(String.valueOf(year)) + "-" + DigitalTrans.formatData(month + "") + "-" + DigitalTrans.formatData(day + "");
        Log.i(TAG, "Sport time:" + time + " calo: " + calo);

        if (Calendar.getInstance().get(Calendar.YEAR) < Integer.valueOf(String.valueOf(year))) {
            return;
        }
        SportData sportData = SqlHelper.instance().getOneDateSportData(HardSdk.getInstance().getAccount(), time);
        sportData.setAccount(HardSdk.getInstance().getAccount());
        sportData.setDate(time);
        sportData.setDistance(distance * 10);
        sportData.setStep(step);
        sportData.setCalories(calo);
        if (sportData.getStepGoal() == 0) {
            sportData.setStepGoal(MySharedPf.getInstance(HardSdk.getInstance().getContext()).getStepGoal());
        }
        if (maxHeart != 0 && minHeart != 0 && maxHeart != 255) {
            sportData.minHeart = minHeart;
            sportData.maxHeart = maxHeart;
            if (time.equals(TimeUtil.getCurrentDate())) {
                MySharedPf.getInstance(HardSdk.getInstance().getContext()).setInt(TimeUtil.getCurrentDate() + "_minHeart", minHeart);
                MySharedPf.getInstance(HardSdk.getInstance().getContext()).setInt(TimeUtil.getCurrentDate() + "_maxHeart", maxHeart);
            }
            if (sportData.currentHeart == 0 || sportData.currentHeart == 255) {
                sportData.currentHeart = (sportData.minHeart + sportData.maxHeart) / 2;
            }
        }

        if (time.equals("00-00-00")) {
            Log.d(TAG, "dealWith36: next");
            mIDataCallBack.onResult(null, true, GlobalValue.STEP_FINISH);
        } else {
            SqlHelper.instance().insertOrUpdateSportData(sportData);
        }
    }

    private void dealWith33(String data) {  // 实时记录
        int step = DigitalTrans.hexStringToAlgorism(data.substring(2, 4)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(4, 6));

        int distance = DigitalTrans.hexStringToAlgorism(data.substring(6, 8)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(8, 10)); // 米

        int calo = DigitalTrans.hexStringToAlgorism(data.substring(10, 12)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(12, 14));
        int jcalo = DigitalTrans.hexStringToAlgorism(data.substring(14, 16)) +
                256 * DigitalTrans.hexStringToAlgorism(data.substring(16, 18));
        calo += jcalo;
        int battery = DigitalTrans.hexStringToAlgorism(data.substring(18, 20)); //电量

        int dbp = DigitalTrans.hexStringToAlgorism(data.substring(24, 26)); //
        int sbp = DigitalTrans.hexStringToAlgorism(data.substring(20, 22)); //
        int heart = DigitalTrans.hexStringToAlgorism(data.substring(22, 24));
        //  Log.i(TAG, "dealWith33 dbp:" + dbp + " sbp: " + sbp);

        mIRealDataListener.onRealData(step, distance * 10, calo, heart, sbp, dbp, battery);

        SportData sportData = SqlHelper.instance().getOneDateSportData(HardSdk.getInstance().getAccount(), TimeUtil.getCurrentDate());

        sportData.setDate(TimeUtil.getCurrentDate());
        sportData.setAccount(HardSdk.getInstance().getAccount());
        sportData.setStep(step);
        sportData.setCalories(calo);
        sportData.setDistance(distance * 10); // 米
        sportData.stepGoal = (MySharedPf.getInstance(HardSdk.getInstance().getContext()).getStepGoal());
        sportData.setStepGoal(MySharedPf.getInstance(HardSdk.getInstance().getContext()).getStepGoal());

        int minHeart = MySharedPf.getInstance(HardSdk.getInstance().getContext()).getInt(TimeUtil.getCurrentDate() + "_minHeart", 1000);
        int maxHeart = MySharedPf.getInstance(HardSdk.getInstance().getContext()).getInt(TimeUtil.getCurrentDate() + "_maxHeart", 0);

        if (heart != 0 && heart != 255) {
            if (heart < minHeart) {
                MySharedPf.getInstance(HardSdk.getInstance().getContext()).setInt(TimeUtil.getCurrentDate() + "_minHeart", heart);
                sportData.setMinHeart(heart);

            }
            if (heart > maxHeart) {
                MySharedPf.getInstance(HardSdk.getInstance().getContext()).setInt(TimeUtil.getCurrentDate() + "_maxHeart", heart);
                sportData.setMaxHeart(heart);
            }
            sportData.setCurrentHeart(heart); //
        }
        //     System.out.println(sportData.toString());
        SqlHelper.instance().insertOrUpdateSportData(sportData);
    }


//    public void setHeartRateListener(IHeartRateListener iHeartRateListener) {
//        this.mIHeartRateListener = iHeartRateListener;
//    }
//
//
//    public void setStepListener(IStepListener iStepListener) {
//        this.mIStepListener = iStepListener;
//    }

    public void setSleepListener(ISleepListener iSleepListener) {
        this.mISleepListener = iSleepListener;
    }

    public void setmIRealDataListener(IRealDataListener iRealDataListener) {
        mIRealDataListener = iRealDataListener;
    }

    /**
     * 向hchsdk上传处理后的结果
     *
     * @param dataCallback
     */
    public void setDataCallback(IDataCallback dataCallback) {
        this.mIDataCallBack = dataCallback;
    }




}

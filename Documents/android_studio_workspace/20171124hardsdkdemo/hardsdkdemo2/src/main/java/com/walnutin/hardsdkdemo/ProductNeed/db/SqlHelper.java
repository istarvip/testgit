package com.walnutin.hardsdkdemo.ProductNeed.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HealthModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.LatLng;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.ProductNeed.entity.TrackInfo;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 作者：MrJiang on 2016/5/19 14:13
 */
public class SqlHelper {

    private static SqlHelper sqlHelper;

    private SqlHelper() {

    }

    public static SqlHelper instance() {
        if (sqlHelper == null) {
            sqlHelper = new SqlHelper();
        }
        return sqlHelper;
    }

    public boolean isExitStep(SQLiteDatabase db, String account, String date) {
        String sql = "select count(*) as num from stepinfo where account='" + account +
                "' and dates ='" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }

    //stepinfo(account varchar(20),dates varchar(30), step Integer,calories Integer,distance Float ,isUpLoad integer)
    synchronized public void insertOrUpdateTodayStep(StepInfos dailyInfo) {  // 添加或者更新今日的步数
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        //   System.out.println("insertOrUpdateTodayStep: " + dailyInfo.dates + " : " + dailyInfo.getStep());
        if (isExitStep(db, dailyInfo.getAccount(), dailyInfo.getDates())) {
            updateTodayStep(db, dailyInfo);
        } else {
            insertStep(db, dailyInfo);
        }
        db.close();
    }

    public void updateTodayStep(SQLiteDatabase db, StepInfos dailyInfo) {  //更新今日 statistic_step
        //   SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update stepinfo set step=?,calories=?,distance=? ,stepOneHourInfo=?," +
                "isUpLoad=? where account=? and dates=?";
        Gson gson = new Gson();
        String stepOneHourInfo = gson.toJson(dailyInfo.stepOneHourInfo); // 讲map集合转为json
        db.execSQL(sql, new Object[]{dailyInfo.getStep(), dailyInfo.getCalories(),
                dailyInfo.getDistance(), stepOneHourInfo, dailyInfo.isUpLoad(), dailyInfo.getAccount(), dailyInfo.getDates()});

    }

    public void insertStep(SQLiteDatabase db, StepInfos dailyInfo) {   // 添加今日 statistic_step 为
        //  SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        String sql = "insert into stepinfo values(?,?,?,?,?,?,?)";
        Gson gson = new Gson();
        String stepOneHourInfo = gson.toJson(dailyInfo.stepOneHourInfo); // 讲map集合转为json
        db.execSQL(sql, new Object[]{dailyInfo.getAccount(), dailyInfo.getDates(), dailyInfo.getStep(),
                dailyInfo.getCalories(), dailyInfo.getDistance(), stepOneHourInfo, dailyInfo.isUpLoad()});
        //     db.close();
    }

    public void updateHistoryNotUpLoadStep(String account) {    // 更新之前未提交的 步数状态 为true
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update stepinfo set isUpLoad = 1 where account='" + account + "'";
        db.execSQL(sql, null);
        db.close();
    }

    /**
     * 得到某一段日期 的详细步数情况
     */

    public List<StepInfos> getLastDateStep(String account, String startTime, String endTime) {
        List<StepInfos> dailyInfos = new ArrayList<>();
        String sql = "select * from stepinfo where account =? and dates between ? and ?  order by dates";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime, endTime});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StepInfos dailyInfo = new StepInfos();
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setUpLoad(cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                String mapJson = cursor.getString(cursor.getColumnIndex("stepOneHourInfo"));
                Gson gson = new Gson();
                dailyInfo.stepOneHourInfo = gson.fromJson(mapJson, new TypeToken<Map<Integer, Integer>>() {
                }.getType());

                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                dailyInfos.add(dailyInfo);
                //      dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //        dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
        }
        return dailyInfos;
    }

    public StepInfos getOneDateStep(String account, String startTime) {
        StepInfos dailyInfo = new StepInfos();
        String sql = "select * from stepinfo where account =? and dates =?";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setUpLoad(cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                String mapJson = cursor.getString(cursor.getColumnIndex("stepOneHourInfo"));
                Gson gson = new Gson();
                dailyInfo.stepOneHourInfo = gson.fromJson(mapJson, new TypeToken<Map<Integer, Integer>>() {
                }.getType());

                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                //      dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //        dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
            cursor.close();
        }
        return dailyInfo;
    }

    /**
     * 得到指定日期 的
     */

    public List<StepInfos> getWeekLastDateStep(String account, String startTime, String endTime) { //不用 每个时辰的详细步数
        List<StepInfos> dailyInfos = new ArrayList<>();
        String sql = "select * from stepinfo where account =? and dates between ? and ? order by dates";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime, endTime});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StepInfos dailyInfo = new StepInfos();
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setUpLoad(cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                String mapJson = cursor.getString(cursor.getColumnIndex("stepOneHourInfo"));
                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                dailyInfos.add(dailyInfo);
                //      dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //        dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
        }
        return dailyInfos;
    }

    /**
     * 得到指定月份的详细步数情况
     */

    public List<StepInfos> getMonthStepListByMonth(String account, String time) {
        List<StepInfos> dailyInfos = new ArrayList<>();
        String sql = "select * from stepinfo where account =? and dates like '" + time + "%' order by dates";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StepInfos dailyInfo = new StepInfos();
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setUpLoad(cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                dailyInfos.add(dailyInfo);
                //      dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //        dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
        }
        return dailyInfos;
    }


    /*
    * 读取未上传到服务器中的数据
    * */
    synchronized public List<StepInfos> getStepsByIsUpLoad(String account, int isUpLoad) {  // 读取之前没有提交的步数
        List<StepInfos> dailyInfos = new ArrayList<>();
        String sql = "select * from stepinfo where account =? and isUpLoad=?";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, String.valueOf(isUpLoad)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StepInfos dailyInfo = new StepInfos();
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));

                String mapJson = cursor.getString(cursor.getColumnIndex("stepOneHourInfo"));
                Gson gson = new Gson();
                dailyInfo.stepOneHourInfo = gson.fromJson(mapJson, new TypeToken<Map<Integer, Integer>>() {
                }.getType());

                dailyInfo.setUpLoad(cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                //     dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //     dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
            cursor.close();
        }
        return dailyInfos;
    }

    //heartRateinfo(rId Integer primary key AUTOINCREMENT,account varchar(20),currentRate Integer," +
    //  "durationTime Integer,testMomentTime varchar(20),heartTrendMap text,isUpLoad Integer )

    public boolean isExitHeartRate(SQLiteDatabase db, String account, String date) { // 排除最后秒信息
        date = date.substring(0, date.lastIndexOf(":"));   // date = 2016-08-10 10:10
        String sql = "select count(*) as num from heartRateinfo where account='" + account +
                "' and testMomentTime like'" + date + "%'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }


    synchronized public void insertBraceletHeartRate(SQLiteDatabase db, HeartRateModel heartRateModel) { // 添加手环的数据到心率数据
        Gson gson = new Gson();
        String heartTrendMap = gson.toJson(heartRateModel.heartTrendMap); // 讲map集合转为json
        String sql = "insert into heartRateinfo values(?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{heartRateModel.account, heartRateModel.currentRate, heartRateModel.durationTime, heartRateModel.testMomentTime, heartTrendMap, heartRateModel.isRunning, 0});
    }

    synchronized public void insertHeartRate(String account, HeartRateModel heartRateModel) { // 添加一次心率到数据库
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        Gson gson = new Gson();
        String heartTrendMap = gson.toJson(heartRateModel.heartTrendMap); // 讲map集合转为json
        String sql = "insert into heartRateinfo values(?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{account, heartRateModel.currentRate, heartRateModel.durationTime, heartRateModel.testMomentTime, heartTrendMap, heartRateModel.isRunning, 0});
        db.close();
    }

    synchronized public void insertTrackInfo(String account, TrackInfo trackInfo) { // 添加一次心率到数据库
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        Gson gson = new Gson();
        String latLngList = gson.toJson(trackInfo.getLatLngList()); // 讲map集合转为json
        String sql = "insert into trackinfo values(?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{account, trackInfo.durationTime, trackInfo.getTime(), trackInfo.getDistance(), trackInfo.getSpeed(), latLngList,trackInfo.type, 0});
        db.close();
    }

    synchronized public List getOneDayTrackInfo(String account, String date) { // 得到某一天的心率集合数据
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<TrackInfo> trackInfoList = new ArrayList<>();
        String sql = "select * from trackinfo where account='" + account + "' and date like '" + date + "%' order by date";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                TrackInfo trackInfo = new TrackInfo();
                trackInfo.setAccount(account);
                trackInfo.durationTime = cursor.getInt(cursor.getColumnIndex("durationTime"));
                trackInfo.speed = cursor.getFloat(cursor.getColumnIndex("speed"));
                trackInfo.distance = cursor.getFloat(cursor.getColumnIndex("distance"));
                trackInfo.time = cursor.getString(cursor.getColumnIndex("date"));
                trackInfo.type = cursor.getInt(cursor.getColumnIndex("type"));
                String mapJson = cursor.getString(cursor.getColumnIndex("trackRecord"));
                trackInfo.latLngList = gson.fromJson(mapJson, new TypeToken<List<LatLng>>() {
                }.getType());
                trackInfoList.add(trackInfo);
            }
            cursor.close();
        }

        return trackInfoList;
    }

    synchronized public List getHistoryTrackInfo(String account) { // 得到某一天的心率集合数据
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<TrackInfo> trackInfoList = new ArrayList<>();
        String sql = "select * from trackinfo where account='" + account + "' order by date";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                TrackInfo trackInfo = new TrackInfo();
                trackInfo.setAccount(account);
                trackInfo.durationTime = cursor.getInt(cursor.getColumnIndex("durationTime"));
                trackInfo.speed = cursor.getFloat(cursor.getColumnIndex("speed"));
                trackInfo.distance = cursor.getFloat(cursor.getColumnIndex("distance"));
                trackInfo.time = cursor.getString(cursor.getColumnIndex("date"));
                trackInfo.type = cursor.getInt(cursor.getColumnIndex("type"));
                String mapJson = cursor.getString(cursor.getColumnIndex("trackRecord"));
                trackInfo.latLngList = gson.fromJson(mapJson, new TypeToken<List<LatLng>>() {
                }.getType());
                trackInfoList.add(trackInfo);
            }
            cursor.close();
        }

        return trackInfoList;
    }


    synchronized public void updateHeartRate(String account) { // 更新 数据库
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update heartRateinfo set isUpLoad = 1 where account='" + account + "'";
        //   System.out.println("sql: " + sql);
        db.execSQL(sql);
        db.close();

    }

    synchronized public List getUnUpLoadToServerHeartRate(String account) { // 得到没有上传的HeartRate 集合
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        String sql = "select * from heartRateinfo where isUpLoad =0 and account='" + account + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                HeartRateModel heartRateModel = new HeartRateModel();
                heartRateModel.account = account;
                heartRateModel.currentRate = cursor.getInt(cursor.getColumnIndex("currentRate"));
                heartRateModel.durationTime = cursor.getInt(cursor.getColumnIndex("durationTime"));
                heartRateModel.testMomentTime = cursor.getString(cursor.getColumnIndex("testMomentTime"));
                heartRateModel.isRunning = cursor.getInt(cursor.getColumnIndex("isRunning")) == 1 ? true : false;
                String mapJson = cursor.getString(cursor.getColumnIndex("heartTrendMap"));
                heartRateModel.heartTrendMap = gson.fromJson(mapJson, new TypeToken<Map<Long, Integer>>() {
                }.getType());
                heartRateModelList.add(heartRateModel);
            }
            cursor.close();
        }

        return heartRateModelList;
    }

    synchronized public List getOneDayHeartRateInfo(String account, String date) { // 得到某一天的心率集合数据
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        String sql = "select * from heartRateinfo where account='" + account + "' and testMomentTime like '" + date + "%' order by testMomentTime";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                HeartRateModel heartRateModel = new HeartRateModel();
                heartRateModel.currentRate = cursor.getInt(cursor.getColumnIndex("currentRate"));
                if (heartRateModel.currentRate > 0) {
                    heartRateModel.account = account;
                    heartRateModel.durationTime = cursor.getInt(cursor.getColumnIndex("durationTime"));
                    heartRateModel.testMomentTime = cursor.getString(cursor.getColumnIndex("testMomentTime"));
                    heartRateModel.isRunning = cursor.getInt(cursor.getColumnIndex("isRunning")) == 1 ? true : false;
                    String mapJson = cursor.getString(cursor.getColumnIndex("heartTrendMap"));
                    heartRateModel.heartTrendMap = gson.fromJson(mapJson, new TypeToken<Map<Long, Integer>>() {
                    }.getType());
                    heartRateModelList.add(heartRateModel);
                }
            }
            cursor.close();
        }

        return heartRateModelList;
    }

    synchronized public HeartRateModel getOneDayRecentHeartRateInfo(String account, String date) { // 得到某一天的心率集合数据
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        HeartRateModel heartRateModel = new HeartRateModel();
        String sql = "select * from heartRateinfo where account='" + account + "' and testMomentTime like '" + date + "%' order by testMomentTime desc limit 0,1";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                heartRateModel.account = account;
                heartRateModel.currentRate = cursor.getInt(cursor.getColumnIndex("currentRate"));
                heartRateModel.durationTime = cursor.getInt(cursor.getColumnIndex("durationTime"));
                heartRateModel.testMomentTime = cursor.getString(cursor.getColumnIndex("testMomentTime"));
                heartRateModel.isRunning = cursor.getInt(cursor.getColumnIndex("isRunning")) == 1 ? true : false;
                String mapJson = cursor.getString(cursor.getColumnIndex("heartTrendMap"));
                heartRateModel.heartTrendMap = gson.fromJson(mapJson, new TypeToken<Map<Long, Integer>>() {
                }.getType());
            }
            cursor.close();
        }

        return heartRateModel;
    }

    synchronized public List getHeartRateListByTime(String account, String startTime, String endTime) { // 得到指定日期的心率集合
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        String sql = "select * from heartRateinfo where account =? and testMomentTime between ? and ? order by testMomentTime";
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime, endTime});

        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                HeartRateModel heartRateModel = new HeartRateModel();
                heartRateModel.account = account;
                heartRateModel.currentRate = cursor.getInt(cursor.getColumnIndex("currentRate"));
                heartRateModel.durationTime = cursor.getInt(cursor.getColumnIndex("durationTime"));
                heartRateModel.testMomentTime = cursor.getString(cursor.getColumnIndex("testMomentTime"));
                heartRateModel.isRunning = cursor.getInt(cursor.getColumnIndex("isRunning")) == 1 ? true : false;

                heartRateModelList.add(heartRateModel);
            }
            cursor.close();
        }

        return heartRateModelList;
    }


    synchronized public List getMonthHeartRateListByTime(String account, String month) { // 得到指定月的心率集合
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        String sql = "select * from heartRateinfo where account =? and testMomentTime like '" + month + "%' order by testMomentTime";
        Cursor cursor = db.rawQuery(sql, new String[]{account});

        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                HeartRateModel heartRateModel = new HeartRateModel();
                heartRateModel.account = account;
                heartRateModel.currentRate = cursor.getInt(cursor.getColumnIndex("currentRate"));
                heartRateModel.durationTime = cursor.getInt(cursor.getColumnIndex("durationTime"));
                heartRateModel.testMomentTime = cursor.getString(cursor.getColumnIndex("testMomentTime"));
                heartRateModel.isRunning = cursor.getInt(cursor.getColumnIndex("isRunning")) == 1 ? true : false;

                heartRateModelList.add(heartRateModel);
            }
            cursor.close();
        }

        return heartRateModelList;
    }


    public boolean isExitSleepData(SQLiteDatabase db, String account, String date) {
        String sql = "select count(*) as num from sleepinfo where account='" + account +
                "' and date ='" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }

    synchronized public void insertOrUpdateSleepData(String account, SleepModel sleepModel) { // 睡眠操作
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        if (isExitSleepData(db, account, sleepModel.date)) {
            updateSleep(db, account, sleepModel);
        } else {
            insertSleepData(db, sleepModel);
        }
        db.close();
    }


    synchronized private void insertSleepData(SQLiteDatabase db, SleepModel sleepModel) { // 添加一次睡眠到数据库
        Gson gson = new Gson();
        String duraionTimeArray = gson.toJson(sleepModel.duraionTimeArray); // int[]集合转为json
        String timePointArray = gson.toJson(sleepModel.timePointArray); // int[]集合转为json
        String sleepStatusArray = gson.toJson(sleepModel.sleepStatusArray); // int[]集合转为json
        String sql = "insert into sleepinfo values(?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{sleepModel.account, sleepModel.date, sleepModel.lightTime, sleepModel.deepTime,
                sleepModel.totalTime, duraionTimeArray, timePointArray, sleepStatusArray, 0});

    }


    synchronized private void updateSleep(SQLiteDatabase db, String account, SleepModel sleepModel) {  //更新今日 sleep

//        String sql = "update healthinfo set heartScore=?,sleepScore=?,stepScore=? ," +
//                "isUpLoad=? where account=? and date=?";
//
//
        Gson gson = new Gson();
        String duraionTimeArray = gson.toJson(sleepModel.duraionTimeArray); // int[]集合转为json
        String timePointArray = gson.toJson(sleepModel.timePointArray); // int[]集合转为json
        String sleepStatusArray = gson.toJson(sleepModel.sleepStatusArray); // int[]集合转为json
        //  String sql = "insert into sleepinfo values(?,?,?,?,?,?,?,?,?)";
        String sql = "update sleepinfo set lightTime=?,deepTime=?,totalTime=?,duraionTimeArray=?,timePointArray=?,sleepStatusArray=?," +
                " isUpLoad =0 where account=? and date = ?";

        db.execSQL(sql, new Object[]{sleepModel.lightTime, sleepModel.deepTime,
                sleepModel.totalTime, duraionTimeArray, timePointArray, sleepStatusArray, account, sleepModel.date});

        //    db.execSQL(sql);

    }

    synchronized public void updateSleepState(String account) {  //更新今日 sleep
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update sleepinfo set isUpLoad = 1 where account='" + account + "'";
        //   System.out.println("sql: " + sql);
        db.execSQL(sql);
        db.close();
    }

    synchronized public List getUnUpLoadSleepDataToServer(String account) { // 得到没有上传的SleepInfo 集合
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<SleepModel> sleepModelArrayList = new ArrayList<>();
        String sql = "select * from sleepinfo where isUpLoad =0 and account='" + account + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                SleepModel sleepModel = new SleepModel();
                sleepModel.totalTime = cursor.getInt(cursor.getColumnIndex("totalTime"));
                if (sleepModel.totalTime > 0) {
                    sleepModel.account = account;
                    sleepModel.date = cursor.getString(cursor.getColumnIndex("date"));
                    sleepModel.lightTime = cursor.getInt(cursor.getColumnIndex("lightTime"));
                    sleepModel.deepTime = cursor.getInt(cursor.getColumnIndex("deepTime"));
                    String duraionTimeArray = cursor.getString(cursor.getColumnIndex("duraionTimeArray")); // string转 int[]
                    String timePointArray = cursor.getString(cursor.getColumnIndex("timePointArray")); // string转 int[]
                    String sleepStatusArray = cursor.getString(cursor.getColumnIndex("sleepStatusArray")); // string转 int[]
                    sleepModel.duraionTimeArray = gson.fromJson(duraionTimeArray, new TypeToken<int[]>() {
                    }.getType());
                    sleepModel.timePointArray = gson.fromJson(timePointArray, new TypeToken<int[]>() {
                    }.getType());
                    sleepModel.sleepStatusArray = gson.fromJson(sleepStatusArray, new TypeToken<int[]>() {
                    }.getType());

                    sleepModelArrayList.add(sleepModel);
                }
            }
            cursor.close();
        }

        return sleepModelArrayList;
    }

    synchronized public List getSleepListByTime(String account, String startTime, String endTime) { // 得到一段时间内的睡眠
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<SleepModel> sleepModelArrayList = new ArrayList<>();
        //   String sql = "select * from sleepinfo where isUpLoad =0 and account='" + account + "'";
        String sql = "select * from sleepinfo where account =? and date between ? and ? order by date";
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime, endTime});

        //  Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                SleepModel sleepModel = new SleepModel();
                sleepModel.account = account;
                sleepModel.date = cursor.getString(cursor.getColumnIndex("date"));
                sleepModel.lightTime = cursor.getInt(cursor.getColumnIndex("lightTime"));
                sleepModel.deepTime = cursor.getInt(cursor.getColumnIndex("deepTime"));
                sleepModel.totalTime = cursor.getInt(cursor.getColumnIndex("totalTime"));
                String duraionTimeArray = cursor.getString(cursor.getColumnIndex("duraionTimeArray")); // int[]集合转为json
                String timePointArray = cursor.getString(cursor.getColumnIndex("timePointArray")); // int[]集合转为json
                String sleepStatusArray = cursor.getString(cursor.getColumnIndex("sleepStatusArray")); // int[]集合转为json
                sleepModel.duraionTimeArray = gson.fromJson(duraionTimeArray, new TypeToken<int[]>() {
                }.getType());
                sleepModel.timePointArray = gson.fromJson(timePointArray, new TypeToken<int[]>() {
                }.getType());
                sleepModel.sleepStatusArray = gson.fromJson(sleepStatusArray, new TypeToken<int[]>() {
                }.getType());

                sleepModelArrayList.add(sleepModel);
            }
            cursor.close();
        }

        return sleepModelArrayList;
    }

    synchronized public SleepModel getOneDaySleepListTime(String account, String startTime) { // 得到某一天时间内的睡眠
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        SleepModel sleepModel = new SleepModel();
        String sql = "select * from sleepinfo where account =? and date =?";
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime});

        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                sleepModel.account = account;
                sleepModel.date = cursor.getString(cursor.getColumnIndex("date"));
                sleepModel.lightTime = cursor.getInt(cursor.getColumnIndex("lightTime"));
                sleepModel.deepTime = cursor.getInt(cursor.getColumnIndex("deepTime"));
                sleepModel.totalTime = cursor.getInt(cursor.getColumnIndex("totalTime"));
                String duraionTimeArray = cursor.getString(cursor.getColumnIndex("duraionTimeArray")); // int[]集合转为json
                String timePointArray = cursor.getString(cursor.getColumnIndex("timePointArray")); // int[]集合转为json
                String sleepStatusArray = cursor.getString(cursor.getColumnIndex("sleepStatusArray")); // int[]集合转为json
                sleepModel.duraionTimeArray = gson.fromJson(duraionTimeArray, new TypeToken<int[]>() {
                }.getType());
                sleepModel.timePointArray = gson.fromJson(timePointArray, new TypeToken<int[]>() {
                }.getType());
                sleepModel.sleepStatusArray = gson.fromJson(sleepStatusArray, new TypeToken<int[]>() {
                }.getType());

            }
            cursor.close();
        }

        return sleepModel;
    }


    synchronized public List getWeekSleepListByTime(String account, String startTime, String endTime) { // 周模式 不需要 三个数组 得到一段时间内的睡眠
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<SleepModel> sleepModelArrayList = new ArrayList<>();
        //   String sql = "select * from sleepinfo where isUpLoad =0 and account='" + account + "'";
        String sql = "select * from sleepinfo where account =? and date between ? and ? order by date";
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime, endTime});

        //  Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                SleepModel sleepModel = new SleepModel();
                sleepModel.account = account;
                sleepModel.date = cursor.getString(cursor.getColumnIndex("date"));
                sleepModel.lightTime = cursor.getInt(cursor.getColumnIndex("lightTime"));
                sleepModel.deepTime = cursor.getInt(cursor.getColumnIndex("deepTime"));
                sleepModel.totalTime = cursor.getInt(cursor.getColumnIndex("totalTime"));

                sleepModelArrayList.add(sleepModel);
            }
            cursor.close();
        }

        return sleepModelArrayList;
    }


    synchronized public List getMonthSleepListByMonth(String account, String time) { // 月模式 得到一月 time= 2016-08
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        List<SleepModel> sleepModelArrayList = new ArrayList<>();
        String sql = "select * from sleepinfo where account =? and date like '" + time + "%' order by date";
        Cursor cursor = db.rawQuery(sql, new String[]{account});

        //  Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                SleepModel sleepModel = new SleepModel();
                sleepModel.account = account;
                sleepModel.date = cursor.getString(cursor.getColumnIndex("date"));
                sleepModel.lightTime = cursor.getInt(cursor.getColumnIndex("lightTime"));
                sleepModel.deepTime = cursor.getInt(cursor.getColumnIndex("deepTime"));
                sleepModel.totalTime = cursor.getInt(cursor.getColumnIndex("totalTime"));

                sleepModelArrayList.add(sleepModel);
            }
            cursor.close();
        }

        return sleepModelArrayList;
    }


    public boolean isExitHealth(SQLiteDatabase db, String account, String date) {
        String sql = "select count(*) as num from healthinfo where account='" + account +
                "' and date ='" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }

    //stepinfo(account varchar(20),dates varchar(30), step Integer,calories Integer,distance Float ,isUpLoad integer)
    synchronized public void insertOrUpdateTodayHealth(HealthModel healthModel) {  // 添加或者更新今日的步数
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        if (isExitHealth(db, healthModel.getAccount(), healthModel.getDate())) {
            updateTodayHealth(db, healthModel);
        } else {
            insertHealth(db, healthModel);
        }
        db.close();
    }

    public void updateTodayHealth(SQLiteDatabase db, HealthModel healthModel) {  //更新今日 statistic_step
        //   SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update healthinfo set heartScore=?,sleepScore=?,stepScore=? ," +
                "isUpLoad=? where account=? and date=?";
        db.execSQL(sql, new Object[]{healthModel.getHeartScore(), healthModel.getSleepScore(),
                healthModel.getStepScore(), healthModel.isUpLoad, healthModel.getAccount(), healthModel.getDate()});
    }

    public void insertHealth(SQLiteDatabase db, HealthModel healthModel) {   // 添加今日 statistic_step 为
        //  SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "insert into healthinfo values(?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{healthModel.getAccount(), healthModel.getDate(), healthModel.getHeartScore(),
                healthModel.getSleepScore(), healthModel.getStepScore(), healthModel.isUpLoad});
        //     db.close();
    }

    public void updateHistoryNotUpLoadHealth(String account) {    // 更新之前未提交的 步数状态 为true
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update healthinfo set isUpLoad = 1 where account='" + account + "'";
        db.execSQL(sql, null);
        db.close();
    }

    /*
    * 读取未上传到服务器中的数据
    * */
    synchronized public List<HealthModel> getUnUpLoadHealthDataToServer(String account, int isUpLoad) {  // 读取之前没有提交的步数
        List<HealthModel> dailyInfos = new ArrayList<>();
        String sql = "select * from healthinfo where account =? and isUpLoad=?";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, String.valueOf(isUpLoad)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HealthModel dailyInfo = new HealthModel();
                dailyInfo.setAccount(account);
                dailyInfo.stepScore = (cursor.getInt(cursor.getColumnIndex("stepScore")));
                dailyInfo.sleepScore = (cursor.getInt(cursor.getColumnIndex("sleepScore")));
                dailyInfo.heartScore = (cursor.getInt(cursor.getColumnIndex("heartScore")));
                dailyInfo.isUpLoad = (cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                dailyInfo.date = (cursor.getString(cursor.getColumnIndex("date")));
                //     dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //     dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
            cursor.close();
        }
        return dailyInfos;
    }

    synchronized public HealthModel getOneDayHealthModel(String account, String date) {  // 读取之前没有提交的步数
        HealthModel dailyInfo = new HealthModel();
        String sql = "select * from healthinfo where account =? and date=?";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, String.valueOf(date)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                dailyInfo.setAccount(account);
                dailyInfo.stepScore = (cursor.getInt(cursor.getColumnIndex("stepScore")));
                dailyInfo.sleepScore = (cursor.getInt(cursor.getColumnIndex("sleepScore")));
                dailyInfo.heartScore = (cursor.getInt(cursor.getColumnIndex("heartScore")));
                dailyInfo.isUpLoad = (cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                dailyInfo.date = (cursor.getString(cursor.getColumnIndex("date")));
            }
            cursor.close();
        }
        return dailyInfo;
    }


    public boolean syncAllHealthInfos(List<HealthModel> healthModelList) {
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        boolean sync = false;
        db.execSQL("delete from healthinfo");

        db.beginTransaction();

        try {
            for (HealthModel healthModel : healthModelList) {
                String sql = "insert into healthinfo values(?,?,?,?,?,?)";
                db.execSQL(sql, new Object[]{healthModel.getAccount(), healthModel.getDate(), healthModel.getHeartScore(),
                        healthModel.getSleepScore(), healthModel.getStepScore(), 1});
            }
            db.setTransactionSuccessful();
            sync = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return sync;
    }


    public boolean syncAllStepInfos(List<StepInfos> stepInfosList) {
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        boolean sync = false;
        db.execSQL("delete from stepinfo");

        db.beginTransaction();

        try {

            Gson gson = new Gson();
            for (StepInfos dailyInfo : stepInfosList) {
                String sql = "insert into stepinfo values(?,?,?,?,?,?,?)";
                String stepOneHourInfo = gson.toJson(dailyInfo.stepOneHourInfo); // 讲map集合转为json
                db.execSQL(sql, new Object[]{dailyInfo.getAccount(), dailyInfo.getDates(), dailyInfo.getStep(),
                        dailyInfo.getCalories(), dailyInfo.getDistance(), stepOneHourInfo, 1}); //已上传
            }
            db.setTransactionSuccessful();
            sync = true;
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

        return sync;
    }

    public boolean syncAllHeartRateInfos(List<HeartRateModel> heartRateModelList) {
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        boolean sync = false;

        db.execSQL("delete from heartRateinfo");

        db.beginTransaction();
        try {
            Gson gson = new Gson();
            for (HeartRateModel heartRateModel : heartRateModelList) {
                String heartTrendMap = gson.toJson(heartRateModel.heartTrendMap); // 讲map集合转为json
                String sql = "insert into heartRateinfo values(?,?,?,?,?,?,?)";
                db.execSQL(sql, new Object[]{heartRateModel.account, heartRateModel.currentRate,
                        heartRateModel.durationTime, heartRateModel.testMomentTime, heartTrendMap, heartRateModel.isRunning, 1});

            }
            db.setTransactionSuccessful();
            sync = true;
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
        return sync;
    }

    public boolean syncAllSleepInfos(List<SleepModel> sleepModelList) { // 下载服务器所有心率数据
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        db.execSQL("delete from sleepinfo");

        db.beginTransaction();
        boolean sync = false;
        try {
            Gson gson = new Gson();
            for (SleepModel sleepModel : sleepModelList) {
                String duraionTimeArray = gson.toJson(sleepModel.duraionTimeArray); // int[]集合转为json
                String timePointArray = gson.toJson(sleepModel.timePointArray); // int[]集合转为json
                String sleepStatusArray = gson.toJson(sleepModel.sleepStatusArray); // int[]集合转为json
                String sql = "insert into sleepinfo values(?,?,?,?,?,?,?,?,?)";
                db.execSQL(sql, new Object[]{sleepModel.account, sleepModel.date, sleepModel.lightTime, sleepModel.deepTime,
                        sleepModel.totalTime, duraionTimeArray, timePointArray, sleepStatusArray, 1});

            }
            db.setTransactionSuccessful();
            sync = true;
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

        return sync;
    }


    public void insertTestStep(String account) {

        List<StepInfos> stepInfosList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            StepInfos stepInfos = new StepInfos();
            stepInfos.setUpLoad(1);
            stepInfos.dates = TimeUtil.formatYMD(calendar.getTime());
            stepInfos.step = random.nextInt(12000);
            stepInfos.calories = random.nextInt(200);
            Map<Integer, Integer> hour = new LinkedHashMap<>();
            hour.put(360, random.nextInt(5000));
            hour.put(420, random.nextInt(5000));
            hour.put(480, random.nextInt(5000));
            hour.put(660, random.nextInt(5000));
            hour.put(720, random.nextInt(5000));
            hour.put(780, random.nextInt(5000));
            hour.put(840, random.nextInt(5000));
            hour.put(960, random.nextInt(5000));
            hour.put(1020, random.nextInt(5000));
            hour.put(1080, random.nextInt(5000));
            stepInfos.setStepOneHourInfo(hour);
            stepInfos.setAccount(account);
            calendar.add(Calendar.DATE, 1);
            stepInfosList.add(stepInfos);
        }

        syncAllStepInfos(stepInfosList);


    }

    public void insertTestSleep(String account) {

        List<SleepModel> sleepModelList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            SleepModel sleepModel = new SleepModel();
            sleepModel.date = TimeUtil.formatYMD(calendar.getTime());
            sleepModel.totalTime = random.nextInt(720);
            sleepModel.account = account;
            calendar.add(Calendar.DATE, 1);
            sleepModelList.add(sleepModel);
        }
        syncAllSleepInfos(sleepModelList);
    }

    public void insertTestHeart(String account) {
        List<HeartRateModel> heartRateModelList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            HeartRateModel heartRateModel = new HeartRateModel();
            heartRateModel.account = account;
            heartRateModel.currentRate = random.nextInt(100) + 40;
            Map<Long, Integer> map = new LinkedHashMap<>();
            map.put(1477360800000l, random.nextInt(100) + 40);

            map.put(1477360920000l, random.nextInt(100) + 40);


            map.put(1477364520000l, random.nextInt(100) + 40);
            map.put(1477368120000l, random.nextInt(100) + 40);
            map.put(1477371720000l, random.nextInt(100) + 40);

            map.put(1477375320000l, random.nextInt(100) + 40);
            map.put(1477382520000l, random.nextInt(100) + 40);

            if (i % 3 == 0) {
                heartRateModel.testMomentTime = TimeUtil.timeStamp2FullDate(1477382520000l);
            } else if (i % 3 == 1) {
                heartRateModel.testMomentTime = TimeUtil.timeStamp2FullDate(1477360800000l);

            } else {
                heartRateModel.testMomentTime = TimeUtil.timeStamp2FullDate(1477364520000l);
            }

            heartRateModel.heartTrendMap = map;
            heartRateModelList.add(heartRateModel);
        }

        syncAllHeartRateInfos(heartRateModelList);
    }


    public String getLastSyncSleepDate(String account) { //得到最近上一次的 睡眠时间 也就是
        String sql = "select date from sleepinfo where account='" + account + "' order by date desc limit 1,2";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String date = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                date = cursor.getString(cursor.getColumnIndex("date"));
            }
        }

        db.close();
        return date;
    }


    public String getLastSyncStepDate(String account) { //得到最近同步一次的步数时间
        String sql = "select dates from stepinfo where account='" + account + "' order by dates desc limit 1,2";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String date = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                date = cursor.getString(cursor.getColumnIndex("dates"));
            }
        }

        db.close();
        return date;
    }

    synchronized public void syncBraceletStepData(List<StepInfos> stepInfosList) {
        if (stepInfosList == null || stepInfosList.size() == 0) {
            return;
        }
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        db.beginTransaction();
        try {
            for (StepInfos dailyInfo : stepInfosList) {
                if (isExitStep(db, dailyInfo.getAccount(), dailyInfo.getDates())) {
                    updateTodayStep(db, dailyInfo);
                } else {
                    insertStep(db, dailyInfo);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    synchronized public void syncBraceletSleepData(List<SleepModel> sleepModelList) {
        if (sleepModelList == null || sleepModelList.size() == 0) {
            return;
        }
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        db.beginTransaction();
        try {
            for (SleepModel sleepModel : sleepModelList) {
                if (isExitSleepData(db, sleepModel.account, sleepModel.date)) {
                    if (sleepModel.date.equals(TimeUtil.getCurrentDate())) {
                        updateSleep(db, sleepModel.account, sleepModel);
                    }
                } else {
                    insertSleepData(db, sleepModel);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }


    synchronized public void syncBraceletHeartData(List<HeartRateModel> heartRateModelList) {
        if (heartRateModelList == null || heartRateModelList.size() == 0) {
            return;
        }
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        db.beginTransaction();
        try {
            for (HeartRateModel heartRateModel : heartRateModelList) {
                if (isExitHeartRate(db, heartRateModel.account, heartRateModel.testMomentTime)) {
                    //     updateSleep(db, sleepModel.account, sleepModel);
                } else {
                    insertBraceletHeartRate(db, heartRateModel);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }


}

package com.walnutin.hardsdkdemo.ProductNeed.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walnutin.hardsdkdemo.ProductNeed.entity.BloodPressure;
import com.walnutin.hardsdkdemo.ProductNeed.entity.ExerciseData;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HealthModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.LatLng;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SportData;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.ProductNeed.entity.TenData;
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

    /**
     * 判断某天是否有计步同步数
     * 界面开发不需关注此方法
     *
     * @param db      不同账号数据库不同
     * @param account
     * @param date
     * @return
     */
    private boolean isExitStep(SQLiteDatabase db, String account, String date) {
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

    /**
     * 增加或者更新计步信息
     * 界面开发调用此方法更新计步信息
     *
     * @param dailyInfo 包含日期信息、计步值等
     */
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

    /**
     * 更新某天计步
     * 不需关注此方法
     *
     * @param db
     * @param dailyInfo
     */
    private void updateTodayStep(SQLiteDatabase db, StepInfos dailyInfo) {  //更新今日 statistic_step
        //   SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update stepinfo set step=?,calories=?,distance=? ,stepOneHourInfo=?," +
                "isUpLoad=? where account=? and dates=?";
        Gson gson = new Gson();
        String stepOneHourInfo = gson.toJson(dailyInfo.stepOneHourInfo); // 讲map集合转为json
        db.execSQL(sql, new Object[]{dailyInfo.getStep(), dailyInfo.getCalories(),
                dailyInfo.getDistance(), stepOneHourInfo, dailyInfo.isUpLoad(), dailyInfo.getAccount(), dailyInfo.getDates()});

    }

    /**
     * 插入某天计步
     *
     * @param db
     * @param dailyInfo
     */
    private void insertStep(SQLiteDatabase db, StepInfos dailyInfo) {   // 添加今日 statistic_step 为
        //  SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        String sql = "insert into stepinfo values(?,?,?,?,?,?,?)";
        Gson gson = new Gson();
        String stepOneHourInfo = gson.toJson(dailyInfo.stepOneHourInfo); // 讲map集合转为json
        db.execSQL(sql, new Object[]{dailyInfo.getAccount(), dailyInfo.getDates(), dailyInfo.getStep(),
                dailyInfo.getCalories(), dailyInfo.getDistance(), stepOneHourInfo, dailyInfo.isUpLoad()});
        //     db.close();
    }

    /**
     * 当有服务器时，计步信息提交成功后，更新本地当前账号所有计步为已上传状态
     *
     * @param account
     */
    public void updateHistoryNotUpLoadStep(String account) {    // 更新之前未提交的 步数状态 为true
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update stepinfo set isUpLoad = 1 where account='" + account + "'";
        db.execSQL(sql, null);
        db.close();
    }

    /**
     * 查询某一段日期 的详细步数情况，包含每天24小时详情，速度慢一些。
     *
     * @param startTime 格式: "2017-10-17"
     * @param endTime   格式: "2017-10-17"
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


    /**
     * 查询某一天计步信息
     *
     * @param account
     * @param startTime 格式: "2017-10-17"
     * @return
     */
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

    /**
     * 查询周计步相关信息，不包含每天24小时详情
     *
     * @param account
     * @param startTime 格式: "2017-10-17"
     * @param endTime   格式: "2017-10-17"
     * @return
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
//                String mapJson = cursor.getString(cursor.getColumnIndex("stepOneHourInfo"));
                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                dailyInfos.add(dailyInfo);
                //      dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //        dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
        }
        return dailyInfos;
    }

    /**
     * 查询指定月份的详细步数情况
     *
     * @param account 通过hardsdk内保存和获取
     * @param time    格式："yyyy-MM"
     * @return
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


    /**
     * 查询出未上传到服务器中的计步数据
     *
     * @param account
     * @param isUpLoad 1：上传过  0：未上传
     * @return
     */
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


    /**
     * 查看是否存在心率数据
     * 不需关注
     *
     * @param db
     * @param account
     * @param date
     * @return
     */
    private boolean isExitHeartRate(SQLiteDatabase db, String account, String date) { // 排除最后秒信息
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


    /**
     * 插入测量心率数值，包含整个测量中数据
     *
     * @param db
     * @param heartRateModel
     */
    synchronized private void insertBraceletHeartRate(SQLiteDatabase db, HeartRateModel heartRateModel) { // 添加手环的数据到心率数据
        Gson gson = new Gson();
        String heartTrendMap = gson.toJson(heartRateModel.heartTrendMap); // 讲map集合转为json
        String sql = "insert into heartRateinfo values(?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{heartRateModel.account, heartRateModel.currentRate, heartRateModel.durationTime, heartRateModel.testMomentTime, heartTrendMap, heartRateModel.isRunning, 0});
    }

    /**
     * 插入测量心率数值，包含整个测量中数据
     * 界面开发在主动测量心率结束后，调用此方法保存数据
     *
     * @param account
     * @param heartRateModel 心率对象
     */
    synchronized public void insertHeartRate(String account, HeartRateModel heartRateModel) { // 添加一次心率到数据库
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        Gson gson = new Gson();
        String heartTrendMap = gson.toJson(heartRateModel.heartTrendMap); // 讲map集合转为json
        String sql = "insert into heartRateinfo values(?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{account, heartRateModel.currentRate, heartRateModel.durationTime, heartRateModel.testMomentTime, heartTrendMap, heartRateModel.isRunning, 0});
        db.close();
    }


    /**
     * 添加运动轨迹
     *
     * @param account   确保唯一的id
     * @param trackInfo 轨迹对象
     */
    synchronized public void insertTrackInfo(String account, TrackInfo trackInfo) { // 添加一次运动轨迹到数据库
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        Gson gson = new Gson();
        String latLngList = gson.toJson(trackInfo.getLatLngList()); // 讲map集合转为json
        String sql = "insert into trackinfo values(?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{account, trackInfo.durationTime, trackInfo.getTime(), trackInfo.getDistance(), trackInfo.getSpeed(), latLngList, trackInfo.type, 0});
        db.close();
    }


    /**
     * 获取某一天运动轨迹详情
     *
     * @param account
     * @param date    格式："yyyy-MM-dd"
     * @return
     */
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

    /**
     * 获取该账号所有历史轨迹集合
     *
     * @param account
     * @return
     */
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


    /**
     * 更新心率上传状态为已上传
     *
     * @param account
     */
    synchronized private void updateHeartRate(String account) { // 更新 数据库
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update heartRateinfo set isUpLoad = 1 where account='" + account + "'";
        //   System.out.println("sql: " + sql);
        db.execSQL(sql);
        db.close();

    }

    /**
     * 获取未上传至服务器的心率集合
     *
     * @param account
     * @return
     */
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


    /**
     * 查询某一天心率集合,不包含每次测量详情map
     *
     * @param account
     * @param date    格式："yyyy-MM-dd"
     * @return
     */
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

    /**
     * 查询某一天心率集合,包含每次测量详情map，速度相对慢一些
     *
     * @param account
     * @param date
     * @return
     */
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


    /**
     * 得到指定日期的心率集合，不包含每一条中详情
     *
     * @param account
     * @param startTime 格式： "yyyy-MM-dd"
     * @param endTime   格式： "yyyy-MM-dd"
     * @return
     */
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


    /**
     * 查询指定月的心率集合
     *
     * @param account
     * @param month   格式： "yyyy-MM"
     * @return
     */
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


    /**
     * 内部调用
     *
     * @param db
     * @param account
     * @param date
     * @return
     */
    private boolean isExitSleepData(SQLiteDatabase db, String account, String date) {
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

    /**
     * 插入更新睡眠数据
     *
     * @param account
     * @param sleepModel
     */
    synchronized public void insertOrUpdateSleepData(String account, SleepModel sleepModel) { // 睡眠操作
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        if (isExitSleepData(db, account, sleepModel.date)) {
            updateSleep(db, account, sleepModel);
        } else {
            insertSleepData(db, sleepModel);
        }
        db.close();
    }


    /**
     * 内部调用
     * 插入睡眠数据
     *
     * @param db
     * @param sleepModel
     */
    synchronized private void insertSleepData(SQLiteDatabase db, SleepModel sleepModel) { // 添加一次睡眠到数据库
        Gson gson = new Gson();
        String duraionTimeArray = gson.toJson(sleepModel.duraionTimeArray); // int[]集合转为json
        String timePointArray = gson.toJson(sleepModel.timePointArray); // int[]集合转为json
        String sleepStatusArray = gson.toJson(sleepModel.sleepStatusArray); // int[]集合转为json
        String sql = "insert into sleepinfo values(?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{sleepModel.account, sleepModel.date, sleepModel.lightTime, sleepModel.deepTime,
                sleepModel.totalTime, duraionTimeArray, timePointArray, sleepStatusArray, 0});

    }


    /**
     * 内部调用
     * 更新睡眠数据
     *
     * @param db
     * @param account
     * @param sleepModel
     */
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


    /**
     * 更新所有睡眠数据上传状态为已上传
     * 界面开发在上传数据至服务器后调用此方法
     *
     * @param account
     */
    synchronized public void updateSleepState(String account) {  //更新今日 sleep
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update sleepinfo set isUpLoad = 1 where account='" + account + "'";
        //   System.out.println("sql: " + sql);
        db.execSQL(sql);
        db.close();
    }

    /**
     * 查询未上传睡眠数据集合
     *
     * @param account
     * @return
     */
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

    /**
     * 查询一段时间内睡眠集合，包含每一天的详情(3个集合)
     *
     * @param account
     * @param startTime 格式："yyyy-MM-dd"
     * @param endTime   格式："yyyy-MM-dd"
     * @return
     */
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

    /**
     * 得到某一天睡眠数据
     *
     * @param account
     * @param startTime 格式："yyyy-MM-dd"
     * @return
     */
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


    /**
     * 获取周睡眠详情，不包含每天的睡眠详情(不包含3个数组)，总时长等是有值的
     *
     * @param account
     * @param startTime 格式："yyyy-MM-dd"
     * @param endTime   格式："yyyy-MM-dd"
     * @return
     */
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


    /**
     * 获取月睡眠集合
     *
     * @param account
     * @param time    格式："yyyy-MM"
     * @return
     */
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


    /**
     * 内部调用
     */
    private boolean isExitHealth(SQLiteDatabase db, String account, String date) {
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

    /**
     * 根据计步、心率等信息算出的健康信息
     * 已过时
     *
     * @param healthModel
     */
    synchronized public void insertOrUpdateTodayHealth(HealthModel healthModel) {  // 添加或者更新今日的步数
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        if (isExitHealth(db, healthModel.getAccount(), healthModel.getDate())) {
            updateTodayHealth(db, healthModel);
        } else {
            insertHealth(db, healthModel);
        }
        db.close();
    }

    /**
     * 已过时
     *
     * @param db
     * @param healthModel
     */
    public void updateTodayHealth(SQLiteDatabase db, HealthModel healthModel) {  //更新今日 statistic_step
        //   SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update healthinfo set heartScore=?,sleepScore=?,stepScore=? ," +
                "isUpLoad=? where account=? and date=?";
        db.execSQL(sql, new Object[]{healthModel.getHeartScore(), healthModel.getSleepScore(),
                healthModel.getStepScore(), healthModel.isUpLoad, healthModel.getAccount(), healthModel.getDate()});
    }


    /**
     * 已过时
     *
     * @param db
     * @param healthModel
     */
    public void insertHealth(SQLiteDatabase db, HealthModel healthModel) {   // 添加今日 statistic_step 为
        //  SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "insert into healthinfo values(?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{healthModel.getAccount(), healthModel.getDate(), healthModel.getHeartScore(),
                healthModel.getSleepScore(), healthModel.getStepScore(), healthModel.isUpLoad});
        //     db.close();
    }

    /**
     * 已过时
     *
     * @param account
     */
    public void updateHistoryNotUpLoadHealth(String account) {    // 更新之前未提交的 步数状态 为true
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update healthinfo set isUpLoad = 1 where account='" + account + "'";
        db.execSQL(sql, null);
        db.close();
    }

    /*
    * 读取未上传到服务器中的数据
    * 已过时
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

    /**
     * 已过时
     */
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


    /**
     * 已过时
     *
     * @param healthModelList
     * @return
     */
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


    /**
     * 内部调用,或者从服务器下载用户所有计步信息后插入数据库
     *
     * @param stepInfosList 从服务器或者手环拉取的所有计步信息
     * @return
     */
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


    /**
     * 内部调用,或者从服务器下载用户所有心率信息后插入数据库
     *
     * @param heartRateModelList 从服务器或者手环拉取的所有心率信息
     * @return
     */
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


    /**
     * 内部调用,或者从服务器下载用户所有睡眠信息后插入数据库
     *
     * @param sleepModelList 从服务器或者手环拉取的所有睡眠信息
     * @return
     */
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


    /**
     * 内部调用，测试用
     *
     * @param account
     */
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


    /**
     * 内部调用，测试用
     *
     * @param account
     */
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


    /**
     * 内部调用，测试用
     *
     * @param account
     */
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


    /**
     * 查询数据库中获取最近一次睡眠记录
     * 已过时
     *
     * @param account
     * @return
     */
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


    /**
     * 已过时
     *
     * @param account
     * @return
     */
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

    /**
     * 内部调用
     * 从手环同步计步数据到数据库
     *
     * @param stepInfosList
     */
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


    /**
     * 内部调用
     * 从手环同步睡眠数据到数据库
     *
     * @param sleepModelList
     */
    synchronized public void syncBraceletSleepData(List<SleepModel> sleepModelList) {
        System.out.println("syncBraceletSleepData: " + sleepModelList.size());
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


    /**
     * 内部调用
     * 从手环同步心率数据到数据库
     *
     * @param heartRateModelList
     */
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

    /**
     * 针对个别厂商使用。
     * 不需关注
     *
     * @param dailyInfo
     */
    synchronized public void insertOrUpdateTenData(TenData dailyInfo) {  // 添加或者更新今日的步数
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        if (isExitTenData(db, dailyInfo.getAccount(), dailyInfo.getDate(), dailyInfo.moment)) {
            updateTenData(db, dailyInfo);
        } else {
            insertTenData(db, dailyInfo);
        }
        db.close();
    }


    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public boolean isExitTenData(SQLiteDatabase db, String account, String date, int moment) {
        String sql = "select count(*) as num from tendatainfo where account='" + account +
                "' and date ='" + date + "'" + " and moment =" + moment;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public void updateTenData(SQLiteDatabase db, TenData sportData) {  //更新今日 statistic_step
        //   SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update tendatainfo set step=?, heart =?,dbp=?,sbp=? where account=? and date=? and moment =?";

        db.execSQL(sql, new Object[]{sportData.getStep(),
                sportData.getHeart(), sportData.getDbp(), sportData.getSbp(),
                sportData.getAccount(), sportData.getDate(), sportData.getMoment()});

    }

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public void insertTenData(SQLiteDatabase db, TenData dailyInfo) {   // 添加今日 statistic_step 为
        //  SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        String sql = "insert into tendatainfo values(?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{dailyInfo.getAccount(), dailyInfo.getDate(), dailyInfo.getStep(),
                dailyInfo.getHeart(), dailyInfo.getDbp(), dailyInfo.getSbp(), dailyInfo.getMoment()});
    }


    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public void insertTenDataList(List<TenData> tenDataList) {
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        db.beginTransaction();
        try {
            for (TenData dailyInfo : tenDataList) {
                if (isExitTenData(db, dailyInfo.getAccount(), dailyInfo.getDate(), dailyInfo.moment)) {

                } else {
                    insertTenData(db, dailyInfo);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

        return;
    }

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public synchronized ExerciseData getExciseDataByDate(String account, String date) { // 根据 详细时间查询 运动数据
        ExerciseData dailyInfo = null;
        String sql = "select * from exerciseinfo where account =? and date=? order by date desc";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, date});
        if (cursor != null) {
            if (cursor.moveToNext()) {
                dailyInfo = new ExerciseData();
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setType(cursor.getInt(cursor.getColumnIndex("type")));
                dailyInfo.haiba = (cursor.getInt(cursor.getColumnIndex("haiba")));
                dailyInfo.setCircles(cursor.getInt(cursor.getColumnIndex("circles")));
                dailyInfo.target = (cursor.getInt(cursor.getColumnIndex("target")));
                dailyInfo.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                dailyInfo.setDate(cursor.getString(cursor.getColumnIndex("date")));
                dailyInfo.setLatLngs(cursor.getString(cursor.getColumnIndex("latLngs")));
                dailyInfo.setScreenShortPath(cursor.getString(cursor.getColumnIndex("screenShortPath")));
                //      dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                //        dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
            cursor.close();
        }
        return dailyInfo;
    }

    //account varchar(20),date varchar(30), step Integer,distance Float,calories Integer, circles integer,
    // duration Integer,latLngs varchar(1000) ,screenShortPath varchar(100))

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    synchronized public void insertOrUpdateExcise(ExerciseData dailyInfo) {  // 添加或者更新今日的步数
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        //   System.out.println("insertOrUpdateTodayStep: " + dailyInfo.dates + " : " + dailyInfo.getStep());
        if (isExitExcise(db, dailyInfo.getAccount(), dailyInfo.getDate())) {
            updateExcise(db, dailyInfo);
        } else {
            insertExcise(db, dailyInfo);
        }
        db.close();
    }

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public void insertExcise(SQLiteDatabase db, ExerciseData dailyInfo) {   // 添加今日 statistic_step 为
        //  SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        String sql = "insert into exerciseinfo values(?,?,?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{dailyInfo.getAccount(), dailyInfo.getDate(), dailyInfo.getStep(),
                dailyInfo.getDistance(), dailyInfo.getCalories(), dailyInfo.haiba, dailyInfo.getType(), dailyInfo.getCircles(), dailyInfo.getDuration()
                , dailyInfo.getLatLngs(), dailyInfo.getScreenShortPath(), dailyInfo.getTarget()});
        //     db.close();
    }

    //account varchar(20),date varchar(30), step Integer,distance Float,calories Integer, circles integer,
    // duration Integer,latLngs varchar(1000) ,screenShortPath varchar(100))

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public void updateExcise(SQLiteDatabase db, ExerciseData dailyInfo) {  //更新今日 statistic_step

        //   SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update exerciseinfo set step=?,distance=? ,calories=?,type =?, circles=?," +
                "duration=?,latLngs=?, screenShortPath=? , haiba=?,target =? where account=? and date=?";
        Gson gson = new Gson();
        db.execSQL(sql, new Object[]{dailyInfo.getStep(), dailyInfo.getDistance(),
                dailyInfo.getCalories(), dailyInfo.getType(), dailyInfo.getCircles(), dailyInfo.getDuration(),
                dailyInfo.getLatLngs(), dailyInfo.getScreenShortPath(), dailyInfo.haiba, dailyInfo.getTarget(), dailyInfo.getAccount(), dailyInfo.getDate()});

    }

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public boolean isExitExcise(SQLiteDatabase db, String account, String date) {  // date 为 yyyy-MM-dd HH:mm
        String sql = "select count(*) as num from exerciseinfo where account='" + account +
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

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public synchronized SportData getOneDateSportData(String account, String time) {
//        Log.d(TAG, "getOneDateSportData: starttime"+ System.currentTimeMillis());
        SportData dailyInfo = new SportData();
        String sql = "select * from sportinfo where account =? and date =?";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, time});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setCurrentHeart(cursor.getInt(cursor.getColumnIndex("currentHeart")));
                dailyInfo.setMinHeart(cursor.getInt(cursor.getColumnIndex("minHeart")));
                dailyInfo.setMaxHeart(cursor.getInt(cursor.getColumnIndex("maxHeart")));
                dailyInfo.setStepGoal(cursor.getInt(cursor.getColumnIndex("stepGoal")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setDate(cursor.getString(cursor.getColumnIndex("date")));

            }
            cursor.close();
        }
//        Log.d(TAG, "getOneDateSportData:  endtime "+ System.currentTimeMillis());
        return dailyInfo;
    }


    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    synchronized public void insertOrUpdateSportData(SportData dailyInfo) {  // 添加或者更新今日的步数
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        //   System.out.println("insertOrUpdateTodayStep: " + dailyInfo.dates + " : " + dailyInfo.getStep());
        if (isExitSportData(db, dailyInfo.getAccount(), dailyInfo.getDate())) {
            updateSportData(db, dailyInfo);
        } else {
            insertSportData(db, dailyInfo);
        }
        db.close();
    }

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public boolean isExitSportData(SQLiteDatabase db, String account, String date) {
        String sql = "select count(*) as num from sportinfo where account='" + account +
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

    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public void updateSportData(SQLiteDatabase db, SportData sportData) {  //更新今日 statistic_step
        //   SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        String sql = "update sportinfo set step=?,calories=?,distance=? , currentHeart =?,maxHeart=?," +
                "minHeart=? where account=? and date=?";

        db.execSQL(sql, new Object[]{sportData.getStep(), sportData.getCalories(),
                sportData.getDistance(), sportData.getCurrentHeart(), sportData.getMaxHeart(), sportData.getMinHeart(), sportData.account, sportData.date});
    }


    /**
     * 针对个别厂商使用。
     * 不需关注
     */
    public void insertSportData(SQLiteDatabase db, SportData dailyInfo) {   // 添加今日 statistic_step 为
        //  SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        String sql = "insert into sportinfo values(?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{dailyInfo.getAccount(), dailyInfo.getDate(), dailyInfo.getStep(),
                dailyInfo.getDistance(), dailyInfo.getCalories(), dailyInfo.getCurrentHeart(), dailyInfo.getMinHeart(), dailyInfo.getMaxHeart(), dailyInfo.getStepGoal()});
    }

    //todo 血压部分补充中


    synchronized public void syncBraceletBloodPressureData(List<BloodPressure> bloodPressureList) {
        if (bloodPressureList == null || bloodPressureList.size() == 0) {
            return;
        }
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();

        db.beginTransaction();
        try {
            for (BloodPressure bloodPressure : bloodPressureList) {
                if (isExitBloodPressure(db, bloodPressure.account, bloodPressure.testMomentTime)) {
                    //     updateSleep(db, sleepModel.account, sleepModel);
                } else {
                    insertBraceletBloodPressure(db, bloodPressure);
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

    public boolean isExitBloodPressure(SQLiteDatabase db, String account, String date) { // 排除最后秒信息
        date = date.substring(0, date.lastIndexOf(":"));   // date = 2016-08-10 10:10
        String sql = "select count(*) as num from bloodPressureinfo where account='" + account +
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


    synchronized public void insertBraceletBloodPressure(SQLiteDatabase db, BloodPressure bloodPressure) { // 添加手环的数据到心率数据
        String sql = "insert into bloodPressureinfo values(?,?,?,?,?)";
        db.execSQL(sql, new Object[]{bloodPressure.account, bloodPressure.diastolicPressure, bloodPressure.systolicPressure, bloodPressure.testMomentTime, bloodPressure.durationTime});
    }
}

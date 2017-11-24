package com.walnutin.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.walnutin.entity.DailyInfo;

import java.util.ArrayList;
import java.util.List;

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


    public void insertOrUpdate(List<DailyInfo> dailyInfoList) {          // 数据库没有指定数据时插入 有 数据时就更新
        SQLiteDatabase db = DBOpenHelper.getInstance().getWritableDatabase();
        db.beginTransaction();
        try {
            for (DailyInfo dailyInfo : dailyInfoList) {
                if (isExitStep(db, dailyInfo.getAccount(), dailyInfo.getDates())) {
                    updateStep(db, dailyInfo);
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

      //  Toast.makeText(MyApplication.getContext(),"更新成功",Toast.LENGTH_SHORT).show();
    }

    public boolean isExitStep(SQLiteDatabase db, String account, String date) {
        String sql = "select count(*) as num from stepinfo where account='" + account +
                "' and dates ='" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null ) {
            cursor.moveToNext();
           if(cursor.getInt(cursor.getColumnIndex("num")) > 0) {
               return true;
           }
        }
        return false;
    }

    public void updateStep(SQLiteDatabase db, DailyInfo dailyInfo) {
        String sql = "update stepinfo set step=?,calories=?,distance=? ," +
                "isUpLoad=?,weekOfYear=?,weekDateFormat=? where account=? and dates=?";
        db.execSQL(sql, new Object[]{dailyInfo.getStep(), dailyInfo.getCalories(),
                dailyInfo.getDistance(), dailyInfo.isUpLoad(), dailyInfo.getWeekOfYear(),
                dailyInfo.getWeekDateFormat(), dailyInfo.getAccount(), dailyInfo.getDates()});
    }

    public void insertStep(SQLiteDatabase db, DailyInfo dailyInfo) {
        String sql = "insert into stepinfo values(?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{dailyInfo.getSid(), dailyInfo.getAccount(),
                dailyInfo.getUid(), dailyInfo.getDates(), dailyInfo.getStep(),
                dailyInfo.getCalories(), dailyInfo.getDistance(), dailyInfo.isUpLoad(),dailyInfo.getWeekOfYear(),dailyInfo.getWeekDateFormat()});
    }

    /**
     * 得到指定日期 的详细步数情况
     */

    public List<DailyInfo> getLastDateStep(String account, String startTime, String endTime) {
        List<DailyInfo> dailyInfos = new ArrayList<>();
        String sql = "select * from stepinfo where account =? and dates between ? and ?";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, startTime, endTime});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setUpLoad(cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
        }
        return dailyInfos;
    }

    /*
    * 读取未上传到服务器中的数据
    * */
    public List<DailyInfo> getStepsByIsUpLoad(String account, int isUpLoad) {
        List<DailyInfo> dailyInfos = new ArrayList<>();
        String sql = "select * from stepinfo where account =? and isUpLoad=?";
        SQLiteDatabase db = DBOpenHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{account, String.valueOf(isUpLoad)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.setAccount(account);
                dailyInfo.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                dailyInfo.setCalories(cursor.getInt(cursor.getColumnIndex("calories")));
                dailyInfo.setDistance(cursor.getFloat(cursor.getColumnIndex("distance")));
                dailyInfo.setUpLoad(cursor.getInt(cursor.getColumnIndex("isUpLoad")));
                dailyInfo.setDates(cursor.getString(cursor.getColumnIndex("dates")));
                dailyInfo.setWeekOfYear(cursor.getInt(cursor.getColumnIndex("weekOfYear")));
                dailyInfo.setWeekDateFormat(cursor.getString(cursor.getColumnIndex("weekDateFormat")));
            }
        }
        return dailyInfos;
    }

    private void updateStepData(String account, String date){

    }
}

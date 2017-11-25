package com.walnutin.hardsdkdemo.ProductList.fitcloud;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;

import java.util.List;
import java.util.Map;

/**
 * 作者：MrJiang on 2017/2/10 11:31
 */
public class FitCloudDb extends SQLiteOpenHelper {
    private static String db_name = "db_fitcloud";
    private static int db_version = 1;
    private static FitCloudDb instance;
    String stepInfo = "create table stepInfo(date varchar(30), stepDailyData varchar(400))";
    String sleepInfo = "create table sleepInfo(date varchar(30), sleepData varchar(400))";

    public static FitCloudDb getInstance() {
        if (instance == null) {
            instance = new FitCloudDb(HardSdk.getInstance().getContext());
        }
        return instance;
    }

    public static void destory() {
        FitCloudDb.instance = null;
    }

    public FitCloudDb(Context context) {
        super(context, db_name, null, db_version);
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(stepInfo);  //记步表格
        db.execSQL(sleepInfo);  //记步表格

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private static boolean isExistStepData(SQLiteDatabase db, String date, String stepData) { //date = 2016-10-10
        String sql = "select count(*) as num from stepInfo where date='" + date +
                "' and stepDailyData ='" + stepData + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }


    static public void insertOrUpdateStep(String date, String stepData) {
        SQLiteDatabase db = FitCloudDb.getInstance().getWritableDatabase();
        String sql = "";
        if (isExistStepData(db, date, stepData)) {
            sql = "update stepInfo set date=? and stepDailyData =?";
        } else {
            sql = "insert into stepInfo values(?,?)";
        }
        System.out.println("sql: " + sql);
        db.execSQL(sql, new String[]{date, stepData});
        db.close();
    }

    public static Map<Integer, Integer> getDailyStepDataByDate(String date) {
        String sql = "select stepDailyData from stepInfo where date='" + date + "'";
        SQLiteDatabase db = FitCloudDb.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String sleepData = cursor.getString(cursor.getColumnIndex("stepDailyData"));
                cursor.close();

                Gson gson = new Gson();
                Map<Integer, Integer> map = gson.fromJson(sleepData, new TypeToken<Map<Integer, Integer>>() {
                }.getType());

                return map;
            }
        }
        return null;
    }


    public static List<DbSyncRawDataEntity> getDailySleepDataByDate(String date) {
        String sql = "select sleepData from sleepInfo where date='" + date + "'";
        SQLiteDatabase db = FitCloudDb.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String sleepData = cursor.getString(cursor.getColumnIndex("sleepData"));
                cursor.close();
                Gson gson = new Gson();
                List<DbSyncRawDataEntity> map = gson.fromJson(sleepData, new TypeToken<List<DbSyncRawDataEntity>>() {
                }.getType());

                return map;
            }
        }
        return null;
    }


    private static boolean isExistSleepData(SQLiteDatabase db, String date) { //date = 2016-10-10
        String sql = "select count(*) as num from sleepInfo where date='" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }

    static public void insertOrUpdateSleep(String date, String sleepData) {
        SQLiteDatabase db = FitCloudDb.getInstance().getWritableDatabase();
        String sql = "";
        if (isExistSleepData(db, date)) {
            sql = "update sleepInfo set date=? and sleepData =?";
        } else {
            sql = "insert into sleepInfo values(?,?)";
        }
        System.out.println("sql: " + sql);
        db.execSQL(sql, new String[]{date, sleepData});
        db.close();
    }

}

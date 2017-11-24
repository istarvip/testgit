package com.walnutin.hardsdkdemo.ProductList.hch;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrJiang on 2017/2/10 11:31
 */
public class HchDb extends SQLiteOpenHelper {
    private static String db_name = "db_hch";
    private static int db_version = 1;
    private static HchDb instance;
    String sleepTable = "create table sleepinfo(date varchar(30), sleepData varchar(200))";
    String heartTable = "create table heartRateinfo(date varchar(30), heartRateData varchar(400))";

    public static HchDb getInstance() {
        if (instance == null) {
            instance = new HchDb(HardSdk.getInstance().getContext());
        }
        return instance;
    }

    public static void destory() {
        HchDb.instance = null;
    }

    public HchDb(Context context) {
        super(context, db_name, null, db_version);
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sleepTable);  //睡眠缓存表格
        db.execSQL(heartTable);  //睡眠心率表格

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    static public void insertOrUpdateHeartRate(String date, String heartRate) {
        SQLiteDatabase db = HchDb.getInstance().getWritableDatabase();
        String sql = "";
        if (isExistHeartRateData(db, date, heartRate)) {
            sql = "update heartRateinfo set date=? and heartRateData =?";
        } else {
            sql = "insert into heartRateinfo values(?,?)";
        }
        System.out.println("sql: heartRateinfo " + sql);
        db.execSQL(sql, new String[]{date, heartRate});
        db.close();
    }

    private static boolean isExistHeartRateData(SQLiteDatabase db, String date, String heartRate) { //date = 2016-10-10
        String sql = "select count(*) as num from heartRateinfo where date='" + date +
                "' and heartRateData ='" + heartRate + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }


    public static List<String> getHeartRateDataByDate(String date) {
        String sql = "select heartRateData from heartRateinfo where date='" + date + "'";
        SQLiteDatabase db = HchDb.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        List<String> heartRateList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String heartRate = cursor.getString(cursor.getColumnIndex("heartRateData"));
                heartRateList.add(heartRate);
            }
            cursor.close();

        }
        return heartRateList;
    }

    private static boolean isExistSleepData(SQLiteDatabase db, String date, String sleepData) { //date = 2016-10-10
        String sql = "select count(*) as num from sleepinfo where date='" + date +
                "' and sleepData ='" + sleepData + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToNext();
            if (cursor.getInt(cursor.getColumnIndex("num")) > 0) {
                return true;
            }
        }
        return false;
    }

    static public void insertOrUpdate(String date, String sleepData) {
        SQLiteDatabase db = HchDb.getInstance().getWritableDatabase();
        String sql = "";
        if (isExistSleepData(db, date, sleepData)) {
            sql = "update sleepinfo set date=? and sleepData =?";
        } else {
            sql = "insert into sleepinfo values(?,?)";
        }
        System.out.println("sql: " + sql);
        db.execSQL(sql, new String[]{date, sleepData});
        db.close();
    }

    public static String getSleepDataByDate(String date) {
        String sql = "select sleepData from sleepinfo where date='" + date + "'";
        SQLiteDatabase db = HchDb.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String sleepData = cursor.getString(cursor.getColumnIndex("sleepData"));
                cursor.close();
                return sleepData;
            }
        }
        return null;
    }

    public void printAllSleepData() {
        String sql = "select *from sleepinfo ";
        SQLiteDatabase db = HchDb.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //   cursor.moveToNext();

                String sleepData = cursor.getString(cursor.getColumnIndex("sleepData"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                System.out.println("HcH date: " + date);
                System.out.println("HcH sleepData: " + sleepData);
            }
            cursor.close();
        }
    }

    public void printAllHeartRateData() {
        String sql = "select *from heartRateinfo ";
        SQLiteDatabase db = HchDb.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String heartRateData = cursor.getString(cursor.getColumnIndex("heartRateData"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                System.out.println("HcH date: " + date);
                System.out.println("HcH heartRateData: " + heartRateData);
            }
            cursor.close();
        }
    }
}

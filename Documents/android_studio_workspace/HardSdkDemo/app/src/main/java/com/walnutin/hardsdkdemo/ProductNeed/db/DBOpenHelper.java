package com.walnutin.hardsdkdemo.ProductNeed.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;


/**
 * by jiang
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static String db_name = "db_hard";
    private static int db_version = 1;
    private static DBOpenHelper instance;


    String stepTable = "create table stepinfo(account varchar(20),dates varchar(30), step Integer,calories Integer,distance Float ,stepOneHourInfo varchar(200),isUpLoad integer)";

    String heartRateTable = "create table heartRateinfo(account varchar(20),currentRate Integer," +
            "durationTime Integer,testMomentTime varchar(20),heartTrendMap varchar(1000),isRunning Integer,isUpLoad Integer )";

    String sleepTable = "create table sleepinfo(account varchar(20),date varchar(20), " +
            "lightTime Integer,deepTime Integer,totalTime Integer," +
            "duraionTimeArray varchar(100),timePointArray varchar(100),sleepStatusArray varchar(100) ,isUpLoad Integer)";

    String healthTable = "create table healthinfo(account varchar(20),date varchar(30), heartScore Integer,sleepScore Integer,stepScore Ingeger ,isUpLoad integer)";


    String trackTable = "create table trackinfo(account varchar(20),durationTime Integer,date varchar(30),  distance Float,speed Float,trackRecord varchar(1000) ,type integer,isUpLoad integer)";


    String sportTable = "create table sportinfo(account varchar(20),date varchar(30), step Integer, distance Float,calories Integer,currentHeart integer,minHeart Integer, maxHeart Integer,stepGoal Integer)";

    String tenTable = "create table tendatainfo(account varchar(20),date varchar(30), step Integer, heart integer,dbp integer,sbp Integer,moment Integer)";

    String exerciseTable = "create table exerciseinfo(account varchar(20),date varchar(30), step Integer,distance Float,calories Integer, haiba integer,type integer, circles integer, duration Integer,latLngs varchar(1000) ,screenShortPath varchar(100),target integer)";
    String bloodPressureTable = "create table bloodPressureinfo(account varchar(20), diastolicPressure Integer, systolicPressure Integer,testMomentTime varchar(20),durationTime varchar(20))";


    public static DBOpenHelper getInstance() {

        if (instance == null) {
            instance = new DBOpenHelper(HardSdk.getInstance().getContext());
        }
        return instance;
    }

    public static void destory() {
        DBOpenHelper.instance = null;
    }

    public DBOpenHelper(Context context) {
        super(context, db_name, null, db_version);
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(stepTable);  //记步缓存表格
        db.execSQL(heartRateTable); // 心率缓存表格
        db.execSQL(sleepTable);
        db.execSQL(healthTable);
        db.execSQL(trackTable);
        db.execSQL(sportTable);
        db.execSQL(tenTable);
        db.execSQL(exerciseTable);
        db.execSQL(bloodPressureTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

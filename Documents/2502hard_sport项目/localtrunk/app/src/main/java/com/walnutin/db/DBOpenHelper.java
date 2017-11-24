package com.walnutin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.walnutin.activity.MyApplication;

/**
 * by jiang
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static String db_name = "db_hard";
    private static int db_version = 1;
    private static DBOpenHelper instance;

    String userinfoTable = "CREATE TABLE userinfo(uid integer primary key AUTOINCREMENT, nickname varchar(50) ,access_token varchar(50)," +
            " avatar varchar(200),phoneNum varchar(12) ,weight varchar(10), height varchar(10),gender varchar(5), age integer,is_email varchar(5)," +
            " is_qq varchar(5),is_sina varchar(5),is_wechat varchar(5),target integer,email varchar(20) )";
    String stepinfoTable = "CREATE TABLE stepinfo (userid Integer,stepid integer , stepNum integer,currentTimestamp NUMBER,location varchar(100),whichDay DATE,weatherTemp varchar(20), weatherCondition varchar(20),weatherDescrible varchar(100) )";
    String groupInfoTable = "create table groupinfo(gid integer ,groupMaster integer , groupName varchar(50), groupMember integer,groupType varchar(10),groupDescrible varchar(200))";
    String user_groupTable = "create table user_group(userid integer,groupid integer)";
    String departmentTable = "CREATE TABLE department (gid integer , userid integer,departmentid integer, departmentName varchar(20) )";

    String stepTable = "create table stepinfo(sid Integer, account varchar(20),uid Integer, " +
            "dates varchar(30), step Integer,calories Integer,distance Float ,isUpLoad integer," +
            "weekOfYear varchar(30), weekDateFormat varchar(30) )";

    public static DBOpenHelper getInstance() {

        if (instance == null) {
            instance = new DBOpenHelper(MyApplication.getContext());
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
        db.execSQL(stepTable);
//        db.execSQL(userinfoTable);
//        db.execSQL(stepinfoTable);
//        db.execSQL(groupInfoTable);
//        db.execSQL(user_groupTable);
//        db.execSQL(departmentTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql;
        if (oldVersion < 2) {

        }
    }


}

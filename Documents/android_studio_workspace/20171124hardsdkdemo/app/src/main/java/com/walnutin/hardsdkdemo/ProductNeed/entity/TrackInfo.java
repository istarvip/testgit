package com.walnutin.hardsdkdemo.ProductNeed.entity;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 作者：MrJiang on 2017/3/22 15:27
 */
public class TrackInfo implements Parcelable {
    public List<LatLng> latLngList;
    public String time;    // 格式："yyyy-MM-dd HH:mm:ss"
    public int durationTime;  //格式： 秒数
    public String userId;    //没用
    public String account;
    public float distance;   // 单位: 米或公里
    public float speed;      // 单位：根据界面保存统一就好  米或者公里
    public int isUpLoad;
    public int type; //地图类型     //config类中的 TYPE_GD代表高德   TYPE_GOOGLE代表谷歌

//    public TrackInfo(Parcel in) {
//        latLngList = in.createTypedArrayList(LatLng.CREATOR);
//        time = in.readString();
//        durationTime = in.readInt();
//        userId = in.readString();
//        account = in.readString();
//        distance = in.readFloat();
//        speed = in.readFloat();
//        isUpLoad = in.readInt();
//    }
//
    public TrackInfo() {

    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeTypedList(latLngList);
//        dest.writeString(time);
//        dest.writeInt(durationTime);
//        dest.writeString(userId);
//        dest.writeString(account);
//        dest.writeFloat(distance);
//        dest.writeFloat(speed);
//        dest.writeInt(isUpLoad);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Creator<TrackInfo> CREATOR = new Creator<TrackInfo>() {
//        @Override
//        public TrackInfo createFromParcel(Parcel in) {
//            return new TrackInfo(in);
//        }
//
//        @Override
//        public TrackInfo[] newArray(int size) {
//            return new TrackInfo[size];
//        }
//    };

    protected TrackInfo(Parcel in) {
        latLngList = in.createTypedArrayList(LatLng.CREATOR);
        time = in.readString();
        durationTime = in.readInt();
        userId = in.readString();
        account = in.readString();
        distance = in.readFloat();
        speed = in.readFloat();
        isUpLoad = in.readInt();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(latLngList);
        dest.writeString(time);
        dest.writeInt(durationTime);
        dest.writeString(userId);
        dest.writeString(account);
        dest.writeFloat(distance);
        dest.writeFloat(speed);
        dest.writeInt(isUpLoad);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrackInfo> CREATOR = new Creator<TrackInfo>() {
        @Override
        public TrackInfo createFromParcel(Parcel in) {
            return new TrackInfo(in);
        }

        @Override
        public TrackInfo[] newArray(int size) {
            return new TrackInfo[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public int getIsUpLoad() {
        return isUpLoad;
    }

    public void setIsUpLoad(int isUpLoad) {
        this.isUpLoad = isUpLoad;
    }
}

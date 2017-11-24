package com.walnutin.hardsdkdemo.utils;

import android.location.Location;


import com.walnutin.hardsdkdemo.ProductNeed.entity.LatLng;

import java.util.List;

/**
 * 作者：MrJiang on 2017/3/22 16:03
 */
public class SportUtil {

    static public double getKmDistance(LatLng start, LatLng end) {
        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        // float t = AMapUtils.calculateLineDistance(start, end);
        // Log.i("Sport","SportUtil:d " + d);
        //   Log.i("Sport","SportUtil:tt " + t);
        return d;
    }

    static public double getMetreDistance(LatLng start, LatLng end) {
        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        // float t = AMapUtils.calculateLineDistance(start, end);
        // Log.i("Sport","SportUtil:d " + d);
        //   Log.i("Sport","SportUtil:tt " + t);
        return d * 1000;
    }

    static public double getMetreDistance(Location start, Location end) {
        double lon1 = (Math.PI / 180) * start.getLongitude();
        double lon2 = (Math.PI / 180) * end.getLongitude();
        double lat1 = (Math.PI / 180) * start.getLatitude();
        double lat2 = (Math.PI / 180) * end.getLatitude();
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        // float t = AMapUtils.calculateLineDistance(start, end);
        // Log.i("Sport","SportUtil:d " + d);
        //   Log.i("Sport","SportUtil:tt " + t);
        return d * 1000;
    }

    static public double getKmDistance(Location start, Location end) {
        double lon1 = (Math.PI / 180) * start.getLongitude();
        double lon2 = (Math.PI / 180) * end.getLongitude();
        double lat1 = (Math.PI / 180) * start.getLatitude();
        double lat2 = (Math.PI / 180) * end.getLatitude();
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        // float t = AMapUtils.calculateLineDistance(start, end);
        // Log.i("Sport","SportUtil:d " + d);
        //   Log.i("Sport","SportUtil:tt " + t);
        return d;
    }

    int distance = 0;

    public static float calcLocationDistance(int startPos, List<Location> latLngList) {

        if (startPos == 0) {
            return 0;
        }

        int len = latLngList.size();
        float distance = 0;
        for (int i = startPos; i < len - 1; i++) {
            distance += getKmDistance(latLngList.get(i), latLngList.get(i - 1));
        }
        return distance;
    }

    public static float calcLocationSpeed(int mode, List<Location> latLngList, int mill) {  // mode 0为 实时速度  mode为1 为总平均速度
        int len = latLngList.size();
        float distance = 0;
        if (mill == 0) {
            return 0;
        }

        if (mode == 0) {
            for (int i = 0; i < len - 1; i++) {
                distance += getKmDistance(latLngList.get(i), latLngList.get(i + 1));
            }

            return (distance * 3600f) / mill;
        } else {
            for (int i = 0; i < len - 1; i++) {  // 取最后 五个值 计算当前速度值
                distance += getKmDistance(latLngList.get(i), latLngList.get(i + 1));
            }

            if (mill == 0) {
                return 0;
            }
            return (distance * 3600f) / mill;
        }

    }

    public static float calcDistance(int startPos, List<LatLng> latLngList) {

        if (startPos == 0) {
            return 0;
        }

        int len = latLngList.size();
        float distance = 0;
        for (int i = startPos; i < len - 1; i++) {
            distance += getKmDistance(latLngList.get(i), latLngList.get(i - 1));
        }
        return distance;
    }

    public static float calcSpeed(int mode, List<LatLng> latLngList, int mill) {  // mode 0为 实时速度  mode为1 为总平均速度
        int len = latLngList.size();
        float distance = 0;
        if (mill == 0) {
            return 0;
        }

        if (mode == 0) {
            for (int i = 0; i < len - 1; i++) {
                distance += getKmDistance(latLngList.get(i), latLngList.get(i + 1));
            }

            return (distance * 3600f) / mill;
        } else {
            for (int i = 0; i < len - 1; i++) {  // 取最后 五个值 计算当前速度值
                distance += getKmDistance(latLngList.get(i), latLngList.get(i + 1));
            }

            if (mill == 0) {
                return 0;
            }
            return (distance * 3600f) / mill;
        }

    }


}

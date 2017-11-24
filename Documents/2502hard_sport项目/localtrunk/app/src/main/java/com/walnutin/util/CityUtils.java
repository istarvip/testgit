package com.walnutin.util;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.walnutin.activity.MyApplication;

/**
 * 作者：MrJiang on 2016/6/14 15:57
 */
public class CityUtils {
    String city = null;
    private LocationClient mLocClient;
    private MyLocationData locData;
    private static CityUtils cityUtils = null;

    private CityUtils() {
        mLocClient = new LocationClient(MyApplication.instance());
        mLocClient.registerLocationListener(myListener);
        setLocationOption();
    }

    static public CityUtils getInstance() {
        if (cityUtils == null) {
            cityUtils = new CityUtils();
        }
        return cityUtils;
    }

    public void startLoc() {
        mLocClient.start();
        mLocClient.requestLocation();
    }

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving); // 省电模式
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
    }

    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null)
                return;
            //   locData.
            if (bdLocation.getCity() != null) {
                city = bdLocation.getCity();
                mLocClient.stop();
            }
            System.out.println("cityUtil" + city);
        }
    };

    public String getCity() {
        //  mLocClient.unRegisterLocationListener(myListener);
        return city;
    }

    public void release() {
        if (myListener != null && mLocClient != null) {
            mLocClient.stop();
            mLocClient.unRegisterLocationListener(myListener);

        }
        cityUtils = null;
    }
}

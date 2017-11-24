package com.walnutin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.walnutin.entity.WeatherInfo;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 作者：MrJiang on 2016/6/14 17:06
 */
public class WeatherSpUtil {

    public static WeatherSpUtil spUtil;
    private Context context;

    private WeatherSpUtil(Context context1) {
        context = context1;
    }

    public static WeatherSpUtil instance(Context context) {
        if (spUtil == null) {

            spUtil = new WeatherSpUtil(context);
        }
        return spUtil;
    }

    public synchronized void saveWeatherInfolistToSp( WeatherInfo weatherInfo) {

        if (weatherInfo == null) {
            return;
        }
        /* 第一步先开启流,定义数组字节输出流 */
        ByteArrayOutputStream tasteBAOS = new ByteArrayOutputStream();
        /* 对象输出流 */
        ObjectOutputStream tasteOOS = null;
        /*把数组字节流加入到对象输出流（把tasteBAOS绑定到tasteOOS，那么只要运行tasteOOS.writeObject（）就会把数据传递到tasteBAOS中*/
        try {
            tasteOOS = new ObjectOutputStream(tasteBAOS);
        /*把对象写入到对象输出流*/
            tasteOOS.writeObject(weatherInfo);
        /*关闭流*/
            tasteBAOS.close();
            tasteOOS.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.d("result", "save sp IOException -----------> " + e1);
        }

        /* 第二步转换二进制数据为文本数据 Base64.encode转换二进制数据为文本数据) */
        String tasteBase64 = null;
        try {
            tasteBase64 = new String(Base64.encodeBase64(tasteBAOS.toByteArray()));
            Log.d("result", "save sp tasteBase64 ----------->: " + tasteBase64);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("result", "save sp Exception -----------> " + e);
        }
        SharedPreferences sp = context.getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("weather", tasteBase64);
        editor.commit();
    }

    /**
     * 从 sp中获得缓存的数据
     *
     * @paramcontext
     * @return
     */
    public synchronized WeatherInfo getDailyInfolistFromSp() {
        WeatherInfo weatherInfo = null;
        SharedPreferences sp = context.getSharedPreferences("WeatherInfo", Context.MODE_PRIVATE);
        String toastBase64 = sp.getString("weather", null);
        //如果返回数据为空,则没缓存,返回2周空的数据
        if (toastBase64 == null) {
            return null;
        }
        Log.d("result", "get sp tasteBase64 ----------->: " + toastBase64);
        // 第一步：转换文本数据为二进制数据
        byte[] cateBase64Bytes = Base64.decodeBase64(toastBase64.getBytes());
        // 开启输入流数组字节流
        ByteArrayInputStream cateBAIS = new ByteArrayInputStream(cateBase64Bytes);
        // 开启对象输入流
        ObjectInputStream cateOIS;
        try {
            // 数组字流绑定对象流（把数组转化为对象）
            cateOIS = new ObjectInputStream(cateBAIS);
            // 这里的cateList 就是我们得到的List对象
            weatherInfo = (WeatherInfo) cateOIS.readObject();
            if (weatherInfo != null) {
                //       Log.d("result", "get sp cateList.size ----------->: " + cateList.size());
            }
            cateOIS.close();
            cateBAIS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherInfo;
    }

}

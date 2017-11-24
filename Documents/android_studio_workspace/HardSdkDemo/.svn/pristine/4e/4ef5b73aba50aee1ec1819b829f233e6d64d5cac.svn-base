package com.walnutin.hardsdkdemo.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.mob.tools.network.KVPair;
import com.mob.tools.network.NetworkHelper;
import com.mob.tools.utils.Hashon;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 作者：MrJiang on 2016/5/25 14:09
 */
public class NetUtils {
   static public String getIp() {

        String ip = null;
        try {
            NetworkHelper network = new NetworkHelper();
            ArrayList<KVPair<String>> values = new ArrayList<KVPair<String>>();
            values.add(new KVPair<String>("ie", "utf-8"));
            String resp = network.httpGet("http://pv.sohu.com/cityjson", values, null, null);
            resp = resp.replace("var returnCitySN = {", "{").replace("};", "}");
            ip = (String) (new Hashon().fromJson(resp).get("cip"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
       //System.out.println("ip:"+ip);
        return ip;
    }

    public static boolean isConnected(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected())
            {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setNetworkMethod(final Context context){
        //提示对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent=null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if(android.os.Build.VERSION.SDK_INT>10){
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                }else{
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();
    }

    public static boolean isWifi(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager)context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null)
                return false;
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            if (networkinfo != null
                    && networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Bitmap getImgBitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL myFileUrl = new URL(imageUri);
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                conn.disconnect();
                is.close();
                is.reset();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}

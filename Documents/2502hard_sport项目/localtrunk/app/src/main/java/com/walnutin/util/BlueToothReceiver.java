

package com.walnutin.util;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BlueToothReceiver extends BroadcastReceiver
{
    private MySharedPf mySharedPf;
    private String btMessage="";
    //监听蓝牙状态

    @Override
    public void onReceive(Context context, Intent intent)
    {
        mySharedPf = MySharedPf.getInstance(context);
        String action = intent.getAction();
//        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


        Log.i("TAG---BlueTooth", "接收到蓝牙状态改变广播！！");
       if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
        {
//            Toast.makeText(context, device.getName() + "已连接", Toast.LENGTH_LONG).show();
//            btMessage=device.getName()+"设备已连接！！";
//            mySharedPf.setString("BlueToothState","connected");

        }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
        {
//            Toast.makeText(context, device.getName() + "蓝牙连接已断开！！！", Toast.LENGTH_LONG).show();
//            btMessage=device.getName()+"蓝牙连接已断开！！";
//            mySharedPf.setString("BlueToothState","Disconnected");
        }


    }

}

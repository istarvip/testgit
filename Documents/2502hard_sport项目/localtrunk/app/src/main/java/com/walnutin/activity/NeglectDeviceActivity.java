package com.walnutin.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.walnutin.eventbus.CommonBlueMsg;
import com.walnutin.hard.R;
import com.walnutin.util.ClsUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

/**
 * Created by assa on 2016/5/27.
 */
public class NeglectDeviceActivity extends Activity implements View.OnClickListener{
    private Set<BluetoothDevice> connectedDevices;
    private TextView ignore_device;
    String deviceAddr;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_neglectdevice);
       deviceAddr = getIntent().getStringExtra("deviceaddr");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        init();

    }

    private void init() {
        ignore_device= (TextView) findViewById(R.id.ignore_device);
        ignore_device.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ignore_device:
                BluetoothDevice bluetoothDevice =  bluetoothAdapter.getRemoteDevice(deviceAddr);
                boolean isSuccess =false;
                try {
                   isSuccess =  ClsUtils.cancelBondProcess(bluetoothDevice.getClass(),bluetoothDevice);
                    if(isSuccess){
                   //     Toast.makeText(getApplicationContext(),"忽略成功 ",Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new CommonBlueMsg(true));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

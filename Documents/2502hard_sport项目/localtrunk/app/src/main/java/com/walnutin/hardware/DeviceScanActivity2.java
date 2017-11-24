package com.walnutin.hardware;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.walnutin.hard.R;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.DeviceScanInterfacer;

public class DeviceScanActivity2 extends AppCompatActivity implements DeviceScanInterfacer {

    private BLEServiceOperate mBLEServiceOperate;
    private boolean mScanning;
    private final long SCAN_PERIOD = 60000 * 5;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 0:


                    break;
                case 1:

                    break;

                default:


                    break;
            }



        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan2);








        // 用于BluetoothLeService实例化准备,必须
        mBLEServiceOperate = BLEServiceOperate
                .getInstance(getApplicationContext());


        if (!mBLEServiceOperate.isSupportBle4_0()) {
            Toast.makeText(this, R.string.not_support_ble, Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }


        mBLEServiceOperate.setDeviceScanListener(this);//for DeviceScanInterfacer
    }


    @Override
    protected void onResume() {
        super.onResume();
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }





    @Override
    public void LeScanCallback(BluetoothDevice bluetoothDevice, int i) {

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mBLEServiceOperate.unBindService();// unBindService
    }



    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEServiceOperate.stopLeScan();
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBLEServiceOperate.startLeScan();
        } else {
            mScanning = false;
            mBLEServiceOperate.stopLeScan();
        }
        invalidateOptionsMenu();
    }

}

package com.walnutin.hardsdkdemo.ProductList;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IBleServiceInit;
import com.walnutin.hardsdkdemo.utils.GlobalValue;


/**
 * 作者：MrJiang on 2017/1/10 10:13
 * 绑定BluetoothLeService服务，并调用服务内方法
 */
public class BLEServiceOperate {
    private static BLEServiceOperate bleServiceOperate;
    private Context context;
    private BluetoothLeService mBluetoothLeService;
    private final static String TAG = BLEServiceOperate.class.getSimpleName();
    private boolean isServiceConnection;
    private BluetoothAdapter bluetoothAdapter;
    private IBleServiceInit mBleServiceInitImpl;

    private BLEServiceOperate(final Context context) {
        this.context = context;
    }

    public static BLEServiceOperate getInstance(Context context) {
        if (bleServiceOperate == null) {
            bleServiceOperate = new BLEServiceOperate(context);
        }
        return bleServiceOperate;
    }

    public void startBindService(String factoryName) {
        Log.d(TAG, "startBindService: run");
        Intent intent = new Intent(context, BluetoothLeService.class);
        intent.putExtra(GlobalValue.FACTORY_NAME, factoryName);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isSupportBle4_0() {

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: run");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                //     finish();
            }
            Log.e(TAG, " initialize Bluetooth success ");
            isServiceConnection = true;
            //    System.out.println("initialize mBle time: " + System.currentTimeMillis());
            // Automatically connects to the device upon successful start-up initialization.
            mBleServiceInitImpl.onBleServiceInitOK();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    public void connect(String addr) {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(addr);
        }
    }

    public BluetoothLeService getBleService() {
        return mBluetoothLeService;
    }

    public void disConnect() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
        }
    }

    public void unBindService() {
        if (context != null && serviceConnection != null) {
            try {
                context.unbindService(serviceConnection);
                isServiceConnection = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isServiceConnection() {
        return isServiceConnection;
    }

    public void setOnBleServiceInitListener(IBleServiceInit bleServiceInitImpl) {
        mBleServiceInitImpl = bleServiceInitImpl;
    }
}

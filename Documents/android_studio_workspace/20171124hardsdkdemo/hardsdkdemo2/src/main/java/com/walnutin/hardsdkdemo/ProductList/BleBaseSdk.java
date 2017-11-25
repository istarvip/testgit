package com.walnutin.hardsdkdemo.ProductList;

import android.content.Context;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IBleServiceInit;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IConnectionStateCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHeartRateListener;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IRealDataSubject;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ISleepListener;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IStepListener;
import com.walnutin.hardsdkdemo.utils.GlobalValue;


/**
 * Created by chenliu on 2017/2/24.
 */

public abstract class BleBaseSdk implements ICommonSDKIntf,IBleServiceInit, IConnectionStateCallback, IStepListener, IHeartRateListener, ISleepListener, IDataCallback {
    protected BLEServiceOperate mBLEServiceOperate;
    protected BluetoothLeService mBluetoothLeService;
    protected Context mContext;
    protected IRealDataSubject iDataSubject;
    final String TAG = BleBaseSdk.class.getSimpleName();
    protected IDataCallback mIDataCallback;
    private IBleServiceInit mBleServiceImpl;

    @Override
    public boolean initialize(Context context) {
        Log.d(TAG, "initialize: run 0");
        mContext = context;
        mBLEServiceOperate = BLEServiceOperate.getInstance(mContext);

        try {
            if (!mBLEServiceOperate.isSupportBle4_0()) {
                return false;
            }
            Log.d(TAG, "initialize: run");
            mBLEServiceOperate.setOnBleServiceInitListener(this);
            initService();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void connect(String addr) {
        if (mBLEServiceOperate != null) {
            mBLEServiceOperate.connect(addr);
        }
    }

    @Override
    public void disconnect() {
        Log.i(TAG, "disconnect");
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
        }
    }

    @Override
    public void readRssi() {
        mBluetoothLeService.readRssi();
    }


    @Override
    public void setRealDataSubject(IRealDataSubject iDataSubject) {
        this.iDataSubject = iDataSubject;
    }


    @Override
    public void setIDataCallBack(IDataCallback iDataCallBack) {
        this.mIDataCallback = iDataCallBack;
    }

    @Override
    public void onResult(Object data, boolean state, int flag) {
        if(flag == GlobalValue.READ_RSSI_VALUE){
            mIDataCallback.onResult(data,state,flag);
        }
    }

    @Override
    public void onSynchronizingResult(String data, boolean state, int status) {
    }

    @Override
    public void onBleServiceInitOK() {
        mBluetoothLeService = mBLEServiceOperate.getBleService();
        Log.d(TAG, "onBleServiceInitOK: mBluetoothLeService:"+mBluetoothLeService);
        if (mBluetoothLeService != null) {
            mBluetoothLeService.setICallback(this);
            mBluetoothLeService.setIDataCallback(this);
            if(mBleServiceImpl!=null){
                mBleServiceImpl.onBleServiceInitOK();
                Log.d(TAG, "onBleServiceInitOK: mBluetoothLeService2:");
            }
        }
    }

    @Override
    public void setOnServiceInitListener(IBleServiceInit bleServiceImpl) {
        mBleServiceImpl = bleServiceImpl;
    }

    @Override
    public boolean isThirdSdk() {
        return false;
    }
}

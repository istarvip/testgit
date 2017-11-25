package com.walnutin.hardsdkdemo.ProductList;


import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IBleServiceInit;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ICommonSDKIntf;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IRealDataSubject;

/**
 * Created by chenliu on 2017/3/7.
 */

public abstract class ThirdBaseSdk implements ICommonSDKIntf {

    protected IRealDataSubject mIRealDataSubject;
    protected IDataCallback mIDataCallBack;

    @Override
    public void initService() {
    }

    @Override
    public void refreshBleServiceUUID() {
    }

    @Override
    public void setOnServiceInitListener(IBleServiceInit bleServiceInitImpl) {
    }

    @Override
    public void setRealDataSubject(IRealDataSubject iDataSubject) {
        this.mIRealDataSubject = iDataSubject;
    }

    @Override
    public void setIDataCallBack(IDataCallback iDataCallBack) {
        this.mIDataCallBack = iDataCallBack;
    }

    @Override
    public boolean isThirdSdk() {
        return true;
    }
}

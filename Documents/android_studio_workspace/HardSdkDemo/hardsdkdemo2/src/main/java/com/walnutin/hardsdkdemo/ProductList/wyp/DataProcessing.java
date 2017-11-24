package com.walnutin.hardsdkdemo.ProductList.wyp;


import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IDataProcessing;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHeartRateListener;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.ISleepListener;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IStepListener;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;

/**
 * Created by chenliu on 2017/1/13.
 */

public class DataProcessing implements IDataProcessing {


    private IHeartRateListener mIHeartRateListener;
    private IStepListener mIStepListener;
    private ISleepListener mISleepListener;
    private static DataProcessing mDataProcessing;
    private IDataCallback mDataCallback;

    private DataProcessing() {

    }

    public static DataProcessing getInstance() {
        if (mDataProcessing == null) {
            mDataProcessing = new DataProcessing();
        }
        return mDataProcessing;
    }

    public void processingData(byte[] value) {
        //解析头
        //switch 头
        //case 心率
        //        处理心率
        //case 血压
        //case 计步

        //如果是心率
        ///  mIHeartRateListener.onHeartRateChange(55,);

        //如果是计步
        //   mIStepListener.onStepChange(43);

        String data = DigitalTrans.byteArrHexToString(value);

        if (data.substring(0, 2).toUpperCase().equals("D0")) {
            int val = Integer.valueOf(DigitalTrans.hexStringToAlgorism(data.substring(2, 4)));
            System.out.println("value: " + val);
            if (data.length() == 6) {
                mIHeartRateListener.onHeartRateChange(val, 0);
            } else {
                if (val == 1) {
                    mIHeartRateListener.onHeartRateChange(0, GlobalValue.RATE_TEST_FINISH);
                } else {
                    mIHeartRateListener.onHeartRateChange(val, 0);
                }
            }
        }

    }


    public void setHeartRateListener(IHeartRateListener iHeartRateListener) {
        this.mIHeartRateListener = iHeartRateListener;
    }


    public void setStepListener(IStepListener iStepListener) {
        this.mIStepListener = iStepListener;
    }

    public void setSleepListener(ISleepListener iSleepListener) {
        this.mISleepListener = iSleepListener;
    }


    /**
     * 向wypsdk上传处理后的结果
     * @param dataCallback
     */
    public void setDataCallback(IDataCallback dataCallback){
        this.mDataCallback = dataCallback;
    }



}

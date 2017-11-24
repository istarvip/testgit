package com.walnutin.hardsdkdemo.ProductList;

import android.content.Context;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.hch.HchSdk;
import com.walnutin.hardsdkdemo.ProductList.rtk.RtkSdk;
import com.walnutin.hardsdkdemo.ProductList.wyp.WypSdk;
import com.walnutin.hardsdkdemo.ProductList.ycy.YcySdk;
import com.walnutin.hardsdkdemo.ProductNeed.entity.BandModel;
import com.walnutin.hardsdkdemo.utils.GlobalValue;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by chenliu on 2017/1/18.
 * 只和厂家UUID有关
 */

public class ModelConfig {
    private static final String TAG = "ModelConfig";

    private final UUID HCH_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final UUID HCH_NOTIFY_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private final UUID HCH_CONF_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    private final UUID WYP_SERVICE_UUID = UUID.fromString("F0080001-0451-4000-B000-000000000000");
    private final UUID WYP_NOTIFY_CHAR_UUID = UUID.fromString("F0080002-0451-4000-B000-000000000000");
    private final UUID WYP_CONF_CHAR_UUID = UUID.fromString("F0080003-0451-4000-B000-000000000000");



    private final UUID RTK_SERVICE_UUID = UUID.fromString("14701820-620a-3973-7c78-9cfff0876abd");
    private final UUID RTK_NOTIFY_CHAR_UUID = UUID.fromString("14702853-620a-3973-7c78-9cfff0876abd");
    private final UUID RTK_CONF_CHAR_UUID = UUID.fromString("14702856-620a-3973-7c78-9cfff0876abd");

    private final UUID DEC_1 = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    private final UUID DEC_2 = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private final String HCH_SERVICE_UUID_16 = "FFF0";
    private final String HCH_SERVICE_UUID2_16 = "180D";
    private final String WYP_SERVICE_UUID_16 = "FEE7";
    private final String WYP_SERVICE_UUID2_16 = "FFFF";
    private final String WYP_SERVICE_UUID3_16 = "0001";
    private final String YCY_SERVICE_UUID_16 = "5554";
    private final String HCH_SERVICE_UUID_180D = "180D"; //FFF0
    private final String HCH_SERVICE_UUID_FFF0 = "FFF0"; //FFF0
    private final String FITCLOUD_SERVICE_UUID_16 = "FEE7";
    private final String AD_SERVICE_UUID_16 = "0AF0";
    private final String RTK_SERVICE_UUID_16 = "BD6A87F0FF9C787C73390A6220187014";
    private final String RTK_SERVICE_UUID_16_2 = "1530";

    private ArrayList<BandModel> bandModelList = new ArrayList<>();
    private static ModelConfig modelConfig;
    private ModelConfig() {
        init();
    }
    private void init() {
        //todo 这里加入要增加的产品
        bandModelList.add(new BandModel(GlobalValue.FACTORY_YCY, true, YcySdk.class, null, null, null, null, YCY_SERVICE_UUID_16));
        bandModelList.add(new BandModel(GlobalValue.FACTORY_RTK, false, RtkSdk.class, com.walnutin.hardsdkdemo.ProductList.rtk.DataProcessing.class, RTK_SERVICE_UUID, RTK_NOTIFY_CHAR_UUID, RTK_CONF_CHAR_UUID, RTK_SERVICE_UUID_16));
        bandModelList.add(new BandModel(GlobalValue.FACTORY_RTK, false, RtkSdk.class, com.walnutin.hardsdkdemo.ProductList.rtk.DataProcessing.class, RTK_SERVICE_UUID, RTK_NOTIFY_CHAR_UUID, RTK_CONF_CHAR_UUID, RTK_SERVICE_UUID_16_2));

        bandModelList.add(new BandModel(GlobalValue.FACTORY_HCH, false, HchSdk.class, com.walnutin.hardsdkdemo.ProductList.hch.DataProcessing.class,
                HCH_SERVICE_UUID, HCH_NOTIFY_CHAR_UUID, HCH_CONF_CHAR_UUID, HCH_SERVICE_UUID_180D));
        bandModelList.add(new BandModel(GlobalValue.FACTORY_HCH, false, HchSdk.class, com.walnutin.hardsdkdemo.ProductList.hch.DataProcessing.class,
                HCH_SERVICE_UUID, HCH_NOTIFY_CHAR_UUID, HCH_CONF_CHAR_UUID, HCH_SERVICE_UUID_FFF0));
        bandModelList.add(new BandModel(GlobalValue.FACTORY_WYP, false, WypSdk.class, com.walnutin.hardsdkdemo.ProductList.wyp.DataProcessing.class,
                WYP_SERVICE_UUID, WYP_NOTIFY_CHAR_UUID, WYP_CONF_CHAR_UUID, WYP_SERVICE_UUID_16));
        bandModelList.add(new BandModel(GlobalValue.FACTORY_WYP, false, WypSdk.class, com.walnutin.hardsdkdemo.ProductList.wyp.DataProcessing.class,
                WYP_SERVICE_UUID, WYP_NOTIFY_CHAR_UUID, WYP_CONF_CHAR_UUID, WYP_SERVICE_UUID2_16));
    }


    public static ModelConfig getInstance() {
        if (modelConfig == null) {
            modelConfig = new ModelConfig();
        }
        return modelConfig;
    }


    public void initBleOperateByUUID(Context context, String factoryName) {

        Log.d(TAG, "initBleOperate: factoryNameFromXml:" + factoryName);
        HardSdk.getInstance().setGlobalFactoryName(factoryName);
        for (BandModel bandModel : bandModelList) {
            if (bandModel.isSdk()) {

            } else {
                BLEServiceOperate bleServiceOperate = BLEServiceOperate.getInstance(context);
                bleServiceOperate.startBindService(factoryName);
            }
        }
    }

    public String getFactoryNameByUUID(String uuid16, String name) {
        if ("FEE7".equals(uuid16) ) {
            if(name != null){
                Log.d(TAG, "getFactoryNameByUUID1: uuid16:"+uuid16  +  " name: "+name);
                if (name.toUpperCase().startsWith("H")) {
                    Log.d(TAG, "getFactoryNameByUUID: into HTS");
                    return GlobalValue.FACTORY_FITCLOUD;
                } else {
                    Log.d(TAG, "getFactoryNameByUUID: into WYP");
                    return GlobalValue.FACTORY_WYP;
                }
            }
        } else {
            Log.d(TAG, "getFactoryNameByUUID2: uuid16:"+uuid16  +  " name: "+name);
            for (BandModel bandModel : bandModelList) {
                if (bandModel.getServiceUUID16().equals(uuid16)) {
                    return bandModel.getFactoryName();
                }
            }
            //找不到任何一个，h开头认为是锐力，做兼容新机。
            if(name!=null && (name.startsWith("h") || (name.startsWith("H")))){
                return GlobalValue.FACTORY_FITCLOUD;
            }
        }
        return null;
    }


    public ArrayList<BandModel> getBandModelList() {
        return bandModelList;
    }


    /**
     * may be return  null
     *
     * @param factoryName
     * @return
     */
    public UUID getServiceUUID(String factoryName) {
        for (BandModel bandModel : bandModelList) {
            if (bandModel.getFactoryName().equals(factoryName)) {
                return bandModel.getServiceUUID();
            }
        }
        return null;
    }


    public UUID getNotifyUUID(String factoryName) {
        for (BandModel bandModel : bandModelList) {
            if (bandModel.getFactoryName().equals(factoryName)) {
                return bandModel.getNotifyUUID();
            }
        }
        return null;
    }


    public UUID getConfUUID(String factoryName) {
        for (BandModel bandModel : bandModelList) {
            if (bandModel.getFactoryName().equals(factoryName)) {
                return bandModel.getConfUUID();
            }
        }
        return null;
    }


    public UUID getDEC_2() {
        return DEC_2;
    }
}

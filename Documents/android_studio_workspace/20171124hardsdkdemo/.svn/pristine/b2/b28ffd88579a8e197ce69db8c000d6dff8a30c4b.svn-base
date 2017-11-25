package com.walnutin.hardsdkdemo.ProductNeed.Jinterface;

import android.content.Context;

import java.util.Map;

/**
 * 作者：MrJiang on 2017/1/12 11:16
 */
public interface ICommonSDKIntf {


    boolean initialize(Context context);

    /**
     * 蓝牙协议专属,sdk不用实现
     */
    void initService();

    /**
     * 蓝牙协议专属,sdk不用实现
     */
    void refreshBleServiceUUID();


    void connect(String addr);

    void disconnect();

    void readRssi();

    //震动
    void findBand(int num);

    void stopVibration();

    /**
     * 恢复出厂设置
     */
    void resetBracelet();


    /**
     * 来电话和短信
     *
     * @param number  电话号码
     * @param smsType 电话或者短信 电话GlobalValue.
     * @param contact
     * @param content
     */
    void sendCallOrSmsInToBLE(String number, int smsType, String contact, String content);

    /**
     * 消息推送
     *
     * @param type
     * @param body
     */
    void sendQQWeChatTypeCommand(int type, String body);


    /**
     * 防丢开关
     *
     * @param isOpen
     */
    void setUnLostRemind(boolean isOpen);


    void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen);

    void sendSedentaryRemindCommand(int isOpen, int time); //久坐提醒

    void setHeightAndWeight(int height, int weight, int screenOnTime);

    void sendOffHookCommand(); // 挂机 指令


    /**
     * 开始测试心率
     */
    void startRateTest();

    void stopRateTest();


    /**
     * 连接成功后，校时后，开始同步三块数据
     */
    void syncAllStepData();

    void syncAllHeartRateData();

    void syncAllSleepData();



    Map<Integer, Integer> queryOneHourStep(String date);

    /**
     * 同步完成后，查询数据转格式后写入数据库
     */
    void syncBraceletDataToDb();


    /**
     * 计步、睡眠、心率数据的实时回调接口
     *
     * @param iDataSubject
     */
    void setRealDataSubject(IRealDataSubject iDataSubject);


    /**
     * 开启手环内消息通知开关
     *
     * @param type
     * @param isOpen
     */
    void openFuncRemind(int type, boolean isOpen);


    //以下是判断各sdk支持功能，以及支持数量
    boolean isSupportHeartRate(String deviceName);

    boolean isSupportBloodPressure(String deviceName);

    boolean isSupportUnLostRemind(String deviceName);

    int getAlarmNum();


    void noticeRealTimeData();

    /*
    * 查询蓝牙版本
    *
    * */

    void queryDeviceVesion();

    boolean isVersionAvailable(String version);

    void startUpdateBLE(); //开始 更新

    void cancelUpdateBle(); // 取消更新

    /*
    *
    * 读取 手环配置信息
    * */
    void readBraceletConfig();


    void setIDataCallBack(IDataCallback iDataCallBack);

    void setOnServiceInitListener(IBleServiceInit bleServiceInitImpl);



    boolean isThirdSdk();
}

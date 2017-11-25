package com.walnutin.hardsdkdemo.ProductNeed.Jinterface;

import android.content.Context;

import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfo;

import java.util.List;
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

    void setAlarmClcok(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen, String typ, String tip);

    void sendSedentaryRemindCommand(int isOpen, int interval, String startTime, String endTime, boolean isDisturb);//久坐提醒


    void setHeightAndWeight(int height, int weight, int age, String sexfrom, String birthday);


    void sendOffHookCommand(); // 挂机 指令


    void sendPalmingStatus(boolean isOpen); // 挂机 指令


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

    int getSupportAlarmNum();


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

    void unBindUser();

    void findBattery();

    boolean isThirdSdk();

    void startAutoHeartTest(boolean isAuto);

    void setAlarmList(List clockList);

    void syncNoticeConfig();

    void readNoticeConfigFromBand();

    void readAlarmListFromBand();


    /**
     * 查询一天的数据，存储为StepInfo供上层使用
     *
     * @param date
     * @return
     */
    StepInfo queryOneDayStepInfo(String date);

    SleepModel querySleepInfo(String startDate, String endDate);

    List<HeartRateModel> queryRateOneDayDetailInfo(String date);


    /*
   *
   手环 设备开关 针对汇成和
   *
   * */
    void setDeviceSwitch(String type, boolean isOpen);

    void writeCommand(String hexValue);


    /**
     * 打开摇一摇拍照开关
     *
     * @param isOpen
     */
    void openTakePhotoFunc(boolean isOpen);

    boolean isSupportOffPower();

    boolean isSupportResetBand();

    void getSportTenData();

    void setScreenOnTime(int screenOnTime);

    void changeMetric(boolean isMetric);

    void changeAutoHeartRate(boolean isAuto);

    void changePalming(boolean palming);

    void setTarget(int target, int type);

    /**
     * 读取 闹钟 列表 针对 Wyp协议 闹钟 功能
     */
    void readClock();

    /**
     * 是否支持 拍照
     *
     * @return
     */
    boolean isSupportTakePhoto();

    boolean isSupportFindBand();

    /**
     * 是否支持 防丢 提醒
     *
     * @return
     */
    boolean isSupportLostRemind();

    /**
     * @return 是否需要 进行 闹钟列表集合 设置
     */
    boolean isNeedAlarmListSetting();

    /**
     * @return 支持 翻腕亮屏
     */
    boolean isSupportWristScreen();

    /**
     * 返回支持 消息提醒功能     ///wechat 位数 0 qq 1 facebookmsg 2 whatspp 3 twitter 4 skype 5 Line 6 linkedin 7  Instagram 8 snapchat 9
     * //1代表支持，0代表不支持  1111111111 代表 全       支持  1000000011 代表支持 WeChat、Instagram 、snapchat
     */
    String getSupportNoticeFunction(); //

    /**
     * 是否支持 久坐 时间段设置
     *
     * @return
     */
    boolean isSupportSetSedentarinessTime();

    /**
     * // 是否支持 查找手机
     *
     * @return
     */
    boolean isFindPhone();

    boolean isSupportControlHVScreen(); // 是否支持 横竖屏切换

    /**
     * //横竖屏切换
     *
     * @param type
     * @return
     */
    void setControlHVScreen(int type);

    /**
     * 开始测量血压
     */
    void startBloodTest();

    /**
     * 结束血压测量
     */
    void stopBloodTest();


}

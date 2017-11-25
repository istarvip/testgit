package com.walnutin.hardsdkdemo.ProductNeed.manager;

import android.content.Context;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.HeartRateModel;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：MrJiang on 2016/6/29 11:36
 */
public class HeartRateStatisticManage {

    private Context context;
    List<HeartRateModel> heartRateModelList;
    List<Integer> dayHourKey;
    List<Integer> dayHourValue;
    List<Integer> monthDayKey;
    List<Integer> monthDayValue;
    List<Integer> weekDayKey;
    List<Integer> weekDayValue;
    private static HeartRateStatisticManage instance;

    Map<String, List> mapDayHeartRateInfoList = new HashMap<>();
    Map<String, List> mapMonthHeartRateInfoList = new HashMap<>();
    Map<String, List> mapWeekHeartRateInfoList = new HashMap<>();

    private HeartRateStatisticManage(Context mcontext) {
        this.context = mcontext;
    }

    public static HeartRateStatisticManage getInstance(Context context) {
        if (instance == null) {
            instance = new HeartRateStatisticManage(context);
        }
        return instance;
    }

/*
*
* 统计心率天 模式
*
*
 *  */

    private static SimpleDateFormat mBirthdayFormat = new SimpleDateFormat(
            "M/dd yyyy");
    private static SimpleDateFormat mNormalFormat = new SimpleDateFormat(
            "yyyy-MM-dd");

    private static SimpleDateFormat mDetailTimeFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static String ConvertNormalTimeFromDate(String time) {
        try {
            Date date = mBirthdayFormat.parse(time);
            return mNormalFormat.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String ConvertDetailTime2NormalTime(String time) {
        try {
            Date date = mDetailTimeFormat.parse(time);
            return mNormalFormat.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }


    public static String toConvert(String date) { //"9/20 2016"转变成 2016-9-26格式
        String year = date.split(" ")[1].trim();
        String month = date.split(" ")[0].split("/")[0].trim();
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = date.split(" ")[0].split("/")[1].trim();
        date = year + "-" + month + "-" + day;
        return date;


    }

    public void setHeartRateModelList(List<HeartRateModel> heartRateModelList) {
        this.heartRateModelList = heartRateModelList;
    }

    /**
     * 查询某段时间内心率详情
     * @param startDate M/dd yyyy 格式
     * @param endDate M/dd yyyy
     */
    public void getDayModeHeartRateListByDate(String startDate, String endDate) {

        startDate = ConvertNormalTimeFromDate(startDate);
        endDate = ConvertNormalTimeFromDate(endDate);
        List<HeartRateModel> heartRateModelList = SqlHelper.instance().getHeartRateListByTime(HardSdk.getInstance().getAccount(), startDate, endDate);

//        for (HeartRateModel heartRateModel : heartRateModelList) {
//            mapDayHeartRateInfoList.put(heartRateModel.getTestMomentTime(), heartRateModel);
//        }

    }

    /**
     *
     * @param startDate 某天心率详情集合  M/dd yyyy
     * @return
     */
    public List<HeartRateModel> getDayModeHeartRateListByDate(String startDate) {  //startData格式为9/11 2-16 得到指定某一天心率集合

     //   startDate = ConvertNormalTimeFromDate(startDate); // 详细时间转换为 yyyy-MM-dd 格式
        if (mapDayHeartRateInfoList.containsKey(startDate) && !startDate.equals(TimeUtil.getCurrentDate())) {
            return mapDayHeartRateInfoList.get(startDate);
        }

        heartRateModelList = SqlHelper.instance().getOneDayHeartRateInfo(HardSdk.getInstance().getAccount(), startDate);

        mapDayHeartRateInfoList.put(startDate, heartRateModelList);

        return heartRateModelList;

    }

    /**
     * 查询一天所以心率集合
     * @param normalDate yyyy-MM-dd格式
     * @return
     */
    public List<HeartRateModel> getDayModeHeartRateListByNormalDate(String normalDate) {  //得到指定某一天心率集合

        if (!normalDate.equals(TimeUtil.getCurrentDate())) {
            if (mapDayHeartRateInfoList.containsKey(normalDate)) {
                return mapDayHeartRateInfoList.get(normalDate);
            }
        }

        heartRateModelList = SqlHelper.instance().getOneDayHeartRateInfo(HardSdk.getInstance().getAccount(), normalDate);

        mapDayHeartRateInfoList.put(normalDate, heartRateModelList);

        return heartRateModelList;

    }

    /**
     * 计算出 heartRateModelList 最低心率值
     * @param heartRateModelList
     * @return
     */
    public int calcLowHeartRateValue(List<HeartRateModel> heartRateModelList) {
        int lowRate = 10000;
        for (HeartRateModel heartRateMode : heartRateModelList) {
            if (heartRateMode.currentRate < lowRate) {
                lowRate = heartRateMode.currentRate; // 计算出最低心率值
            }
        }
        if (lowRate == 10000) {
            lowRate = 0;
        }
        return lowRate;
    }

    /**
     * 计算出 heartRateModelList 平均心率值
     * @param heartRateModelList
     * @return
     */
    public int calcCenterHeartRateValue(List<HeartRateModel> heartRateModelList) {
        int centerRate = 0;
        int sum = 0;
        int itemCount = 0;
        for (HeartRateModel heartRateMode : heartRateModelList) {
            sum += heartRateMode.currentRate;
            itemCount++;
        }

        if (itemCount != 0) {
            centerRate = sum / itemCount;
        }
        return centerRate;
    }

    /**
     * 计算出 heartRateModelList 最高心率值
     * @param heartRateModelList
     * @return
     */
    public int calcHighHeartRateValue(List<HeartRateModel> heartRateModelList) {
        int highRate = 0;
        for (HeartRateModel heartRateMode : heartRateModelList) {
            if (heartRateMode.currentRate > highRate) {
                highRate = heartRateMode.currentRate; // 计算出最大心率值
            }
        }
        if (highRate == 0) {
            highRate = 0;
        }
        return highRate;

    }

    /**
     *
     * @param heartRateModeList 最终计算出 dayHourKey  dayHourValue两个集合，用来绘制24小时图表
     * @return
     */
    public void calcPerHourCenterHeartRate(List<HeartRateModel> heartRateModeList) { // 计算一天 每个小时的 平均值映射
        dayHourKey = new ArrayList<>();
        dayHourValue = new ArrayList<>();

        int len = 0;
        if (heartRateModeList != null) {
            len = heartRateModeList.size();
        } else {
            return;
        }

        for (int i = 0; i < 24; i++) {
            List<HeartRateModel> hourRateList = getHourHeartRate(i, heartRateModeList);
            if (hourRateList.size() > 0) {
                dayHourKey.add(i);
                dayHourValue.add(calcCenterHeartRateValue(hourRateList)); // 得到这个小时的平均值
            }

        }


    }

    /**
     *
     * @param hour 指定某小时内 的所有心率集合 比如hour=5 代表5点这小时内的所有测过的心率集合
     * @param heartRateModeList
     * @return
     */
    private List getHourHeartRate(int hour, List<HeartRateModel> heartRateModeList) {   // 得到指定几小时的心率集合

        List<HeartRateModel> heartRateModeHourList = new ArrayList<>();

        for (HeartRateModel heartRateMode : heartRateModeList) {
            if (heartRateMode.getCurrentRate() > 0) {
                String time = TimeUtil.detailTimeToHmFormat(heartRateMode.getTestMomentTime()); // 得到 时和分
                int hourValue = Integer.parseInt(time.split(":")[0]);
                if (hour == hourValue) {
                    heartRateModeHourList.add(heartRateMode);         // 一个小时内的测得所有心率数据
                }
            }
        }
        return heartRateModeHourList;

    }

    /**
     * 跟随上一步，获取有心率的小时集合。
     * 例如： 0代表0点   4代表4点
     * 使用前提，调用过calcPerHourCenterHeartRate方法后再使用。
     * @return
     */
    public List<Integer> getOneDayHeartRateKeyDetails() {

        return dayHourKey;
    }

    /**
     * 跟随上一步，获取有心率的小时集合。
     * 值为平均心率值，和dayHourKey集合一一对应。
     * 使用前提，调用过calcPerHourCenterHeartRate方法后再使用。
     * @return
     */
    public List<Integer> getOneDayHeartRateValueDetails() {
        return dayHourValue;

    }




    /**
     * 获取某一周心率数据集合
     * @param startTime  某一周的开始的第一天日期    从周一开始      格式："yyyy-MM-dd"
     * @param position 无用
     * @return  心率集合
     */
    public List getWeekModeHeartRateListByDate(String startTime, int position) { //startTime ="2016-08-08"

//        if (mapWeekHeartRateInfoList.containsKey(startTime)) {
//            return mapWeekHeartRateInfoList.get(startTime);
//        }

        String endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(startTime.split("-")[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(startTime.split("-")[1]) - 1);
        calendar.set(Calendar.DATE, Integer.valueOf(startTime.split("-")[2]));
        calendar.add(Calendar.DATE, 6);
        endTime = TimeUtil.formatYMD(calendar.getTime());


        List heartRateModelList = SqlHelper.instance().getHeartRateListByTime(HardSdk.getInstance().getAccount(), startTime, endTime);
        if (!mapWeekHeartRateInfoList.containsKey(startTime)) {
            mapWeekHeartRateInfoList.put(startTime, heartRateModelList);
        }
        return heartRateModelList;
    }


    /**
     * 解析周心率集合为 界面图表所需映射
     * @param heartRateModeList  心率集合
     * 最终转换出 weekDayKey：0代表周一 4代表周五 6代表周日   weekDayValue：某一天的平均心率
     */
    public void calcPerDayCenterHeartRate(List<HeartRateModel> heartRateModeList) { // 计算每一天 的心率平均值映射
        weekDayKey = new ArrayList<>();
        weekDayValue = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            List<HeartRateModel> hourRateList = getDayHeartRate(i, heartRateModeList);
            if (hourRateList.size() > 0) {
                weekDayKey.add(i);
                weekDayValue.add(calcCenterHeartRateValue(hourRateList)); // 得到这个小时的平均值
            }

        }


    }

    /**
     *
     * @param week 周几的索引 周一为0
     * @param heartRateModeList 得到周几的心率集合
     * @return
     */
    private List getDayHeartRate(int week, List<HeartRateModel> heartRateModeList) {   // 得到指定一天的心率集合

        List<HeartRateModel> heartRateModeHourList = new ArrayList<>();

        for (HeartRateModel heartRateMode : heartRateModeList) {
            String time = TimeUtil.ConvertDetailTime2NormalTime(heartRateMode.getTestMomentTime()); // 得到 yyyy-MM-dd
            int weekOfDay = TimeUtil.getWeekIndexByDate(time) - 1;

            if (week == weekOfDay) {
                heartRateModeHourList.add(heartRateMode);         // 一天内测得的所有心率数据
            }
        }
        return heartRateModeHourList;

    }

    /**
     * 在calcPerDayCenterHeartRate方法后调用此方法获取key集合
     * @return  星期key
     */
    public List<Integer> getWeekDayKey() {
        return weekDayKey;
    }


    /**
     * 在calcPerDayCenterHeartRate方法后调用此方法获取value集合
     * @return  对应的步数值
     */
    public List<Integer> getWeekDayValue() {
        return weekDayValue;
    }

    /**
     * 获取某月的心率集合
     * @param month 某月的其中一天  格式:"yyyy-MM-dd"
     * @param position 无用
     * @return 心率集合
     *
     */
    public List getMonthModeHeartRateListByDate(String month, int position) { //month ="2016-08-08"

        month = month.substring(0, month.lastIndexOf("-"));

        if (mapMonthHeartRateInfoList.containsKey(month) && !TimeUtil.getCurrentDate().contains(month)) {
            return mapMonthHeartRateInfoList.get(month);
        }
        List heartRateModelList = SqlHelper.instance().getMonthHeartRateListByTime(HardSdk.getInstance().getAccount(), month);
        if (!mapMonthHeartRateInfoList.containsKey(month)) {
            mapMonthHeartRateInfoList.put(month, heartRateModelList);
        }
        return heartRateModelList;
    }


    /**
     * 处理月集合数据,生成月统计图的key value映射，用于绘图
     * @param heartRateModeList  月统计集合 date 为某一天数据 yyyy-MM-dd
     * 最终生成  monthDayKey  monthDayValue  映射
     */
    public void calcMonthPerDayCenterHeartRate(List<HeartRateModel> heartRateModeList, String date) { // 计算每一天 的心率平均值映射

        monthDayKey = new ArrayList<>();
        monthDayValue = new ArrayList<>();

        int mondayNum = TimeUtil.getDaysOfMonth(date);

        for (int i = 0; i < mondayNum; i++) {
            List<HeartRateModel> hourRateList = getMonthDayHeartRate(i, heartRateModeList);
            if (hourRateList.size() > 0) {
                monthDayKey.add(i);
                monthDayValue.add(calcCenterHeartRateValue(hourRateList)); // 得到这个小时的平均值
            }
        }
    }

    /**
     *
     * @param dayOfMonth 月中的哪一天 比如1号  dayOfMonth就为0
     * @param heartRateModeList 某一天的心率集合
     * @return
     */
    private List getMonthDayHeartRate(int dayOfMonth, List<HeartRateModel> heartRateModeList) {   // 得到指定一天的

        List<HeartRateModel> heartRateModeHourList = new ArrayList<>();

        for (HeartRateModel heartRateMode : heartRateModeList) {
            String time = TimeUtil.ConvertDetailTime2NormalTime(heartRateMode.getTestMomentTime()); // 得到 yyyy-MM-dd
            int monthOfDay = Integer.valueOf(time.split("-")[2]) - 1;
            if (dayOfMonth == monthOfDay) {
                heartRateModeHourList.add(heartRateMode);         // 一天内测得的所有心率数据
            }
        }
        return heartRateModeHourList;
    }

    /**
     * 在calcMonthPerDayCenterHeartRate后条用获取key集合
     * @return   0代表1号
     */
    public List<Integer> getMonthDayKey() {
        return monthDayKey;
    }

    /**
     * 在calcMonthPerDayCenterHeartRate后条用获取value集合
     * @return   与key对应的某天的心率值
     */
    public List<Integer> getMonthDayValue() {
        return monthDayValue;
    }
}

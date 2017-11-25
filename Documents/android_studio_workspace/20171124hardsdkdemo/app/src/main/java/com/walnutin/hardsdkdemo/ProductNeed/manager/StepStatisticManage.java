package com.walnutin.hardsdkdemo.ProductNeed.manager;

import android.content.Context;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.StepInfos;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 作者：MrJiang on 2016/6/29 11:36
 */
public class StepStatisticManage {

    private Context context;
    StepInfos stepInfos;
    //List<StepInfos> stepInfosList;
    List<Integer> dayHourKey;
    List<Integer> dayHourValue;
    List<Integer> monthDayKey;
    List<Integer> monthDayValue;
    List<Integer> weekDayKey;
    List<Integer> weekDayValue;
    private static StepStatisticManage instance;

    Map<String, StepInfos> mapDayStepInfoList = new HashMap<>();
    Map<String, List> mapMonthStepInfoList = new HashMap<>();
    Map<String, List> mapWeekStepInfoList = new HashMap<>();

    private StepStatisticManage(Context mcontext) {
        this.context = mcontext;
    }

    public static StepStatisticManage getInstance(Context context) {
        if (instance == null) {
            instance = new StepStatisticManage(context);
        }
        return instance;
    }

/*
*
* 统计步数 天 模式
*
*
 *  */

    private static SimpleDateFormat mBirthdayFormat = new SimpleDateFormat(
            "M/dd yyyy");
    private static SimpleDateFormat mNormalFormat = new SimpleDateFormat(
            "yyyy-MM-dd");


    /**
     * 格式转换
     * @param time
     * @return
     */
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


    /**
     * 反转格式
     * @param date
     * @return
     */
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


    /**
     * 返回某天计步详情
     * @param date  某一天  格式："yyyy-MM-dd"
     * @return
     */
    public StepInfos getDayModeStepByDate(String date) {

        //   date = ConvertNormalTimeFromDate(date);
        if (mapDayStepInfoList.containsKey(date) && !TimeUtil.getCurrentDate().equals(date)) {
            return mapDayStepInfoList.get(date);
        } else {
            StepInfos sif = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(), date);
            mapDayStepInfoList.put(date, sif);
            return sif;
        }

        //  return null;
    }

    /**
     * 得到今天计步详情
     * @param date  传入今天日期 格式："yyyy-MM-dd"
     * @return
     */
    public StepInfos getToDayModeStep(String date) {

        StepInfos sif = SqlHelper.instance().getOneDateStep(HardSdk.getInstance().getAccount(), date);
        mapDayStepInfoList.put(date, sif);
        return sif;
    }

    /**
     * 查询一段日期内的计步数据集合
     * @param startDate   格式："yyyy-MM-dd"
     * @param endDate  格式："yyyy-MM-dd"
     */
    public void getDayModeStepListByDate(String startDate, String endDate) {

        startDate = ConvertNormalTimeFromDate(startDate);
        endDate = ConvertNormalTimeFromDate(endDate);
        List<StepInfos> stepInfosList = SqlHelper.instance().getLastDateStep(HardSdk.getInstance().getAccount(), startDate, endDate);

        for (StepInfos stepInfos : stepInfosList) {
            mapDayStepInfoList.put(stepInfos.dates, stepInfos);
        }

    }

    /**
     * 把计步数据转换成界面所需横纵坐标映射
     * @param stepInfos  某一天的计步信息
     * 最终计算出 dayHourKey  dayHourValue两个集合，用来绘制24小时图表
     */
    public void resolveStepInfo(StepInfos stepInfos) {  //计算指定日期的步数与时间关系 横纵坐标系
        dayHourKey = new ArrayList<>();
        dayHourValue = new ArrayList<>();
        if (stepInfos.getStepOneHourInfo() == null) {
            return;
        }
        Set<Map.Entry<Integer, Integer>> set = stepInfos.getStepOneHourInfo().entrySet();
        Iterator<Map.Entry<Integer, Integer>> itor = set.iterator();

        while (itor.hasNext()) {
            Map.Entry<Integer, Integer> entry = itor.next();
            //System.out.println(entry.getKey() + " : " + entry.getValue());
            if (entry.getValue() > 0) {
                dayHourKey.add(entry.getKey() / 60);      //key 是保存的分钟数，  得到小时数
                dayHourValue.add(entry.getValue());
            }
        }
    }


    /**
     * 跟随上一步，获取有步数的小时集合。
     * 例如： 0代表0点   4代表4点
     * 使用前提，调用过resolveStepInfo方法后再使用。
     * @return
     */
    public List<Integer> getOneDayStepKeyDetails() {

        return dayHourKey;
    }

    /**
     * 跟随上一步，获取有步数的小时对应步数集合。
     * 值为步数，和dayHourKey集合一一对应。
     * 使用前提，调用过resolveStepInfo方法后再使用。
     * @return
     */
    public List<Integer> getOneDayStepHourValueDetails() {
        return dayHourValue;

    }


    /**
     * 获取计步集合的总步数。
     * @param stepInfosList
     * 使用前提： 已调用查询周或者月计步的方法，获取集合后调用。
     * @return  总步数
     */
    public int getTotalStep(List<StepInfos> stepInfosList) {
        int steps = 0;
        for (StepInfos stepInfos : stepInfosList) {
            steps += stepInfos.getStep();
        }
        return steps;
    }


    /**
     * 获取计步集合的总距离。
     * @param stepInfosList
     * 使用前提： 已调用查询周或者月计步的方法，获取集合后调用。
     * @return  总距离 单位：一般从手环同步过来保存为公里
     */
    public float getTotalDistance(List<StepInfos> stepInfosList) {
        float distance = 0;
        for (StepInfos stepInfos : stepInfosList) {
            distance += stepInfos.getDistance();
        }
        return distance;
    }


    /**
     * 获取计步集合的总卡路里。
     * @param stepInfosList
     * 使用前提： 已调用查询周或者月计步的方法，获取集合后调用。
     * @return  总卡路里 单位：一般从手环同步过来保存为卡，不是千卡。
     */
    public int getTotalCal(List<StepInfos> stepInfosList) {
        int cals = 0;
        for (StepInfos stepInfos : stepInfosList) {
            cals += stepInfos.getCalories();
        }
        return cals;
    }

    /**
     * 获取该集合中有步数的小时数。用来粗略统计用户当天走了几个小时。
     * @param stepInfosList
     * @return
     */
    public int getTotalSportTimes(List<StepInfos> stepInfosList) {
        int times = 0;
        for (StepInfos stepInfos : stepInfosList) {
            if (stepInfos.getStepOneHourInfo() != null) {
                times += stepInfos.getStepOneHourInfo().size();
            }
        }
        return times;
    }



    /**
     * 获取某一周计步数据集合
     * @param startTime  某一周的开始的第一天日期    从周一开始      格式："yyyy-MM-dd"
     * @param position 无用
     * @return  计步集合
     */
    public List getWeekModeStepListByDate(String startTime, int position) { //month ="2016-08-08"

        if (mapWeekStepInfoList.containsKey(startTime) && !TimeUtil.getCurrentDate().equals(startTime)) {
            return mapWeekStepInfoList.get(startTime);
        }

        String endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(startTime.split("-")[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(startTime.split("-")[1]) - 1);
        calendar.set(Calendar.DATE, Integer.valueOf(startTime.split("-")[2]));
        calendar.add(Calendar.DATE, 6);
        endTime = TimeUtil.formatYMD(calendar.getTime());

        // month = month.substring(0, month.lastIndexOf("-"));
        List stepInfosList = SqlHelper.instance().getWeekLastDateStep(HardSdk.getInstance().getAccount(), startTime, endTime);
        if (stepInfosList == null) {
            stepInfosList = new ArrayList<>();
        }
        //  System.out.println("stepInfosList:" + stepInfosList.size());
        if (!mapWeekStepInfoList.containsKey(startTime)) {
            mapWeekStepInfoList.put(startTime, stepInfosList);
        }
        return stepInfosList;
    }


    /**
     * 获取本周计步数据集合
     * @param startTime  本周的开始的第一天日期    从周一开始      格式："yyyy-MM-dd"
     * @return  计步集合
     */
    public List getTheWeekModeStepList(String startTime) { //month ="2016-08-08"


        String endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(startTime.split("-")[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(startTime.split("-")[1]) - 1);
        calendar.set(Calendar.DATE, Integer.valueOf(startTime.split("-")[2]));
        calendar.add(Calendar.DATE, 6);
        endTime = TimeUtil.formatYMD(calendar.getTime());

        // month = month.substring(0, month.lastIndexOf("-"));
        List stepInfosList = SqlHelper.instance().getWeekLastDateStep(HardSdk.getInstance().getAccount(), startTime, endTime);
        if (stepInfosList == null) {
            stepInfosList = new ArrayList<>();
        }
        mapWeekStepInfoList.put(startTime, stepInfosList);
        return stepInfosList;
    }


    /**
     * 解析周计步集合为 界面图表所需映射
     * @param stepInfoList  计步集合
     * 最终转换出 weekDayKey：0代表周一 4代表周五 6代表周日   weekDayValue：某一天的总步数
     */
    public void resolveWeekModeStepInfo(List<StepInfos> stepInfoList) {

        weekDayKey = new ArrayList<>();
        weekDayValue = new ArrayList<>();
        for (StepInfos stepInfos : stepInfoList) {
            if (stepInfos.getStep() > 0) {
                weekDayKey.add(TimeUtil.getWeekIndexByDate(stepInfos.getDates())-1); //根据日期计算是周几 的索引 周一为0
                weekDayValue.add(stepInfos.getStep());
            }
        }
    }

    /**
     * 在resolveWeekModeStepInfo方法后调用此方法获取key集合
     * @return  星期key
     */
    public List<Integer> getWeekDayKey() {
        return weekDayKey;
    }

    /**
     * 在resolveWeekModeStepInfo方法后调用此方法获取value集合
     * @return  对应的步数值
     */
    public List<Integer> getWeekDayValue() {
        return weekDayValue;
    }


    /**
     * 获取某月的计步集合
     * @param month     某月的其中一天  格式:"yyyy-MM-dd"
     * @param position  无用
     * @return   计步集合
     */
    public List getMonthModeStepListByDate(String month, int position) { //month ="2016-08-08"

        month = month.substring(0, month.lastIndexOf("-") );

        if (mapMonthStepInfoList.containsKey(month) && !TimeUtil.getCurrentDate().contains(month)) {
            return mapMonthStepInfoList.get(month);
        }

        List stepInfosList = SqlHelper.instance().getMonthStepListByMonth(HardSdk.getInstance().getAccount(), month);
        if (stepInfosList == null) {
            stepInfosList = new ArrayList<>();
        }
        //  System.out.println("stepInfosList:" + stepInfosList.size());
        if (!mapMonthStepInfoList.containsKey(month)) {
            mapMonthStepInfoList.put(month, stepInfosList);
        }
        return stepInfosList;
    }

    /**
     * 查询本月
     * @param month  本月的其中一天 格式:"yyyy-MM-dd"
     * @return
     */
    public List getTheMonthModeStepListBy(String month) { //month ="2016-08-08"

        month = month.substring(0, month.lastIndexOf("-"));


        List stepInfosList = SqlHelper.instance().getMonthStepListByMonth(HardSdk.getInstance().getAccount(), month);
        if (stepInfosList == null) {
            stepInfosList = new ArrayList<>();
        }
        mapMonthStepInfoList.put(month, stepInfosList);
        return stepInfosList;
    }


    /**
     * 处理月集合数据,生成月统计图的key value映射，用于绘图
     * @param stepInfoList  月统计集合
     * 最终生成  monthDayKey  monthDayValue  映射
     */
    public void resolveMonthModeStepInfo(List<StepInfos> stepInfoList) {

        monthDayKey = new ArrayList<>();
        monthDayValue = new ArrayList<>();
        for (StepInfos stepInfos : stepInfoList) {
            if (stepInfos.getStep() > 0) {
                monthDayKey.add(Integer.valueOf(stepInfos.getDates().split("-")[2]) - 1);
                monthDayValue.add(stepInfos.getStep());
            }
        }
    }

    /**
     * 在resolveMonthModeStepInfo后条用获取key集合
     * @return   0代表1号
     */
    public List<Integer> getMonthDayKey() {
        return monthDayKey;
    }

    /**
     * 在resolveMonthModeStepInfo后条用获取value集合
     * @return   与key对应的某天的计步值
     */
    public List<Integer> getMonthDayValue() {
        return monthDayValue;
    }
}

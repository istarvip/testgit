package com.walnutin.hardsdkdemo.ProductNeed.manager;

import android.content.Context;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.db.SqlHelper;
import com.walnutin.hardsdkdemo.ProductNeed.entity.SleepModel;
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
public class SleepStatisticManage {

    private Context context;

    List<Integer> monthDayKey;
    List<Integer> monthTotalSleepDayValue;
    List<Integer> monthDeepSleepDayValue;
    List<Integer> monthLightSleepDayValue;
    List<Integer> monthsoberSleepDayValue;

    List<Integer> weekDayKey;
    List<Integer> weekDeepSleepDayValue;
    List<Integer> weekLightSleepDayValue;
    List<Integer> weeksoberSleepDayValue;
    List<Integer> weekTotalSleepDayValue;
    private static SleepStatisticManage instance;
    private Map<String, SleepModel> mapSleepMode = new HashMap<>();
    Map<String, List> mapMonthSleepInfoList = new HashMap<>();
    Map<String, List> mapWeekSleepInfoList = new HashMap<>();
    public SleepModelImpl sleepModelImpl;

    private SleepStatisticManage(Context mcontext) {
        this.context = mcontext;

        sleepModelImpl = new SleepModelImpl(context);
    }

    public static SleepStatisticManage getInstance(Context context) {
        if (instance == null) {
            instance = new SleepStatisticManage(context);
        }
        return instance;
    }

/*
* 统计睡眠天 模式
**/
    private static SimpleDateFormat mBirthdayFormat = new SimpleDateFormat(
            "M/dd yyyy");
    private static SimpleDateFormat mNormalFormat = new SimpleDateFormat(
            "yyyy-MM-dd");

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
     * @param date yyyy-MM-dd 格式
     * @return 得到指定天的睡眠
     */
    public SleepModel getDayModeSleepByDate(String date) {

        //   date = ConvertNormalTimeFromDate(date);
        if (mapSleepMode.containsKey(date)) {
            return mapSleepMode.get(date);
        } else {
            SleepModel sm = SqlHelper.instance().getOneDaySleepListTime(HardSdk.getInstance().getAccount(), date);
            mapSleepMode.put(date, sm);
            return sm;
        }


    }

    /**
     * @param date yyyy-MM-dd 格式
     * @return 得到指定天的睡眠 如果是查询今天睡眠，建议调用此方法，其他调用上面的方法
     */
    public SleepModel getToDayModeSleep(String date) {

        SleepModel sm = SqlHelper.instance().getOneDaySleepListTime(HardSdk.getInstance().getAccount(), date);
        mapSleepMode.put(date, sm);
        return sm;
    }

    /**
     * 无用，留着测试用
     */
    public void generateVirtualData() {
        sleepModelImpl.testInsertSleepData();
    }

    /**
     * @param startDate 睡眠开始日期 M/dd yyyy
     * @param endDate   睡眠结束日期 M/dd yyyy 格式
     */

    public void getDayModeSleepListByDate(String startDate, String endDate) {
        startDate = ConvertNormalTimeFromDate(startDate);
        endDate = ConvertNormalTimeFromDate(endDate);
        List<SleepModel> sleepList = SqlHelper.instance().getSleepListByTime(HardSdk.getInstance().getAccount(), startDate, endDate);

        for (SleepModel sleepMode : sleepList) {
            mapSleepMode.put(sleepMode.date, sleepMode);
        }

    }

    /**
     * @param sleepMode 将查询到的睡眠 传递进来， 方便以后可以获取 深睡、浅睡、清醒时长等操作
     */
    public void setSleepMode(SleepModel sleepMode) {

        sleepModelImpl.setSleepModel(sleepMode);
    }

    /**
     * 传递 SleepMode 中的 timePointArray集合 进来
     *
     * @param timePointArray
     */
    public void setTimePointArray(int[] timePointArray) {
        sleepModelImpl.setTimePointArray(timePointArray);
    }

    /**
     * 计算出 开始睡眠时间
     */
    public void setStartSleep() {
        sleepModelImpl.setStartSleep();
    }

    /**
     * 计算出 起床时间
     */
    public void setEndSleep() {
        sleepModelImpl.setEndSleep();
    }

    /**
     * @return 得到睡眠总时长
     */
    public int getDurationLen() {
        return sleepModelImpl.getAllDurationTime();
    }

    /**
     * @return得到睡眠浅睡时长
     */
    public int getLightTime() {
        return sleepModelImpl.getLightTime();
    }

    /**
     * @return得到睡眠清醒时长
     */
    public int getSoberTime() {
        return sleepModelImpl.getSoberTime();
    }

    /**
     * @return得到睡眠深睡时长
     */
    public int getDeepTime() {
        return sleepModelImpl.getDeepTime();
    }

    /**
     * @return得到睡眠开始时间 调用之前 请先调用 setStartSleep方法
     */
    public String getStartSleep() {
        return sleepModelImpl.getStartSleep();
    }

    /**
     * @return得到睡眠结束时间 调用之前 请先调用 setendSleep方法
     */
    public String getEndSleep() {
        return sleepModelImpl.getEndSleep();
    }


    /**
     * @return 得到睡眠总时长
     */
    public int getTotalTime() {
        return sleepModelImpl.getTotalTime();
    }

    /**
     * @return 暂时无用 得到本次睡眠分值
     */
    public int getSleepScore() {

        return sleepModelImpl.getSleepScore();
    }

    /**
     * @return 得到睡眠状态集合
     */
    public int[] getSleepStatusArray() {
        return sleepModelImpl.getSleepStatusArray();
    }

    /**
     * @return 得到SleepModel中 durationStartPos
     */
    public List<Integer> getDurationStartPos() {
        return sleepModelImpl.getDurationStartPos();
    }

    /**
     * @return 得到SleepModel中 timePointArray 数组集合
     */
    public int[] getTimePointArray() {
        return sleepModelImpl.getTimePointArray();
    }

    /**
     * @return 得到SleepModel中 duraionTimeArray数组
     */
    public int[] getDuraionTimeArray() {
        return sleepModelImpl.getDuraionTimeArray();
    }



/*
*
* 统睡眠数 周 模式
*
*
 *  */

    /**
     * 获取本周睡眠数据集合
     *
     * @param startTime 本周的开始的第一天日期    从周一开始      格式："yyyy-MM-dd" 如果是本周的建议调用此方法、其他建议调用下面方法
     * @return 睡眠集合
     */

    public List<SleepModel> getTheWeekModeSleepList(String startTime) {
        String endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(startTime.split("-")[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(startTime.split("-")[1]) - 1);
        calendar.set(Calendar.DATE, Integer.valueOf(startTime.split("-")[2]));
        calendar.add(Calendar.DATE, 6);
        endTime = TimeUtil.formatYMD(calendar.getTime());
        List sleepList = SqlHelper.instance().getWeekSleepListByTime(HardSdk.getInstance().getAccount(), startTime, endTime);
        if (sleepList == null) {
            sleepList = new ArrayList<>();
        }
        mapWeekSleepInfoList.put(startTime, sleepList);
        return sleepList;
    }


    /**
     * 获取本周睡眠数据集合
     *
     * @param startTime 本周的开始的第一天日期    从周一开始      格式："yyyy-MM-dd" 非本周的建议调用此方法，本周的睡眠建议调用上面方法
     * @return 睡眠集合
     */

    public List getWeekModeSleepListByDate(String startTime, int position) { //startTime ="2016-08-08"

        if (mapWeekSleepInfoList.containsKey(startTime)) {
            return mapWeekSleepInfoList.get(startTime);
        }

        String endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(startTime.split("-")[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(startTime.split("-")[1]) - 1);
        calendar.set(Calendar.DATE, Integer.valueOf(startTime.split("-")[2]));
        calendar.add(Calendar.DATE, 6);
        endTime = TimeUtil.formatYMD(calendar.getTime());

        List sleepList = SqlHelper.instance().getWeekSleepListByTime(HardSdk.getInstance().getAccount(), startTime, endTime);
        if (sleepList == null) {
            sleepList = new ArrayList<>();
        }
        //  System.out.println("sleepInfosList:" + sleepInfosList.size());
        if (!mapWeekSleepInfoList.containsKey(startTime)) {
            mapWeekSleepInfoList.put(startTime, sleepList);
        }
        return sleepList;
    }


    /**
     * 最终转换出 weekDayKey：0代表周一 4代表周五 6代表周日   weekTotalSleepDayValue：某一天的总睡眠时长，
     * weekDeepSleepDayValue 深睡眠时长 weekLightSleepDayValue 浅睡眠时长  weeksoberSleepDayValue 清醒时长
     *
     * @param sleepModelList
     */
    public void resolveWeekModeSleepInfo(List<SleepModel> sleepModelList) {

        weekDayKey = new ArrayList<>();
        weekTotalSleepDayValue = new ArrayList<>();
        weekDeepSleepDayValue = new ArrayList<>();
        weekLightSleepDayValue = new ArrayList<>();
        weeksoberSleepDayValue = new ArrayList<>();
        for (SleepModel sleepModel : sleepModelList) {
            if (sleepModel.getTotalTime() > 1) {
                weekDayKey.add(TimeUtil.getWeekIndexByDate(sleepModel.date) - 1); //根据日期计算是周几 的索引 周一为1
                weekTotalSleepDayValue.add(sleepModel.getTotalTime());
                weekDeepSleepDayValue.add(sleepModel.getDeepTime());
                weekLightSleepDayValue.add(sleepModel.getLightTime());
                weeksoberSleepDayValue.add(sleepModel.getSoberTime());

            }
        }
    }

    /**
     * 在resolveWeekModeSleepInfo方法后调用此方法获取key集合
     *
     * @return 星期key
     */
    public List<Integer> getWeekDayKey() {
        return weekDayKey;
    }

    /**
     * 在resolveWeekModeSleepInfo方法后调用此方法获取value集合  总睡眠时长集合
     *
     * @return 对应的步数值
     */
    public List<Integer> getWeekTotalSleepDayValue() {
        return weekTotalSleepDayValue;
    }


    /**
     * @return 得到周深睡睡眠的value 集合， 与weekDayKey 一一对应
     */
    public List<Integer> getWeekDeepSleepDayValue() {
        return weekDeepSleepDayValue;
    }

    /**
     * @return 得到周浅睡睡眠的value 集合， 与weekDayKey 一一对应
     */
    public List<Integer> getWeekLightSleepDayValue() {
        return weekLightSleepDayValue;
    }

    /**
     * @return 得到周清醒睡睡眠的value 集合， 与weekDayKey 一一对应
     */
    public List<Integer> getWeeksoberSleepDayValue() {
        return weeksoberSleepDayValue;
    }
/*
*
* 统睡眠数 月 模式
*
*
 *  */

    /**
     * 获取某月的睡眠集合
     *
     * @param month 某月的其中一天  格式:"yyyy-MM-dd" 如果是获取今天睡眠、建议调用此方法，其他日期建议调用下面方面
     * @return 睡眠集合
     */
    public List getTheMonthModeStepList(String month) { //month ="2016-08-08"

        month = month.substring(0, month.lastIndexOf("-"));


        List sleepInfosList = SqlHelper.instance().getMonthSleepListByMonth(HardSdk.getInstance().getAccount(), month);
        if (sleepInfosList == null) {
            sleepInfosList = new ArrayList<>();
        }
        mapMonthSleepInfoList.put(month, sleepInfosList);
        return sleepInfosList;
    }

    /**
     * 获取某月的睡眠集合
     *
     * @param month 某月的其中一天  格式:"yyyy-MM-dd" 如果是获取非今天睡眠、建议调用此方法，今日日期建议调用上面方面
     * @return 睡眠集合
     */
    public List getMonthModeStepListByDate(String month, int position) { //month ="2016-08-08"

        month = month.substring(0, month.lastIndexOf("-"));

        if (mapMonthSleepInfoList.containsKey(month)) {
            return mapMonthSleepInfoList.get(month);
        }

        List sleepInfosList = SqlHelper.instance().getMonthSleepListByMonth(HardSdk.getInstance().getAccount(), month);
        if (sleepInfosList == null) {
            sleepInfosList = new ArrayList<>();
        }
        //  System.out.println("sleepInfosList:" + sleepInfosList.size());
        if (!mapMonthSleepInfoList.containsKey(month)) {
            mapMonthSleepInfoList.put(month, sleepInfosList);
        }
        return sleepInfosList;
    }


    /**
     * 处理月集合数据,生成月统计图的key value映射，用于绘图
     *
     * @param sleepModelList 月统计集合
     *                       最终生成  monthDayKey 是key值  monthTotalSleepDayValue monthDeepSleepDayValue  monthLightSleepDayValue  monthsoberSleepDayValue映射
     */
    public void resolveMonthModeSleepInfo(List<SleepModel> sleepModelList) {

        monthDayKey = new ArrayList<>();
        monthTotalSleepDayValue = new ArrayList<>();
        monthDeepSleepDayValue = new ArrayList<>();
        monthLightSleepDayValue = new ArrayList<>();
        monthsoberSleepDayValue = new ArrayList<>();
        for (SleepModel sleepModel : sleepModelList) {
            if (sleepModel.getTotalTime() > 1) {
                monthDayKey.add(Integer.valueOf(sleepModel.date.split("-")[2]) - 1);
                monthTotalSleepDayValue.add(sleepModel.getTotalTime());
                monthDeepSleepDayValue.add(sleepModel.getDeepTime());
                monthLightSleepDayValue.add(sleepModel.getLightTime());
                monthsoberSleepDayValue.add(sleepModel.soberTime);
            }
        }
    }

    /**
     * @param sleepModelList
     * @return 得到一段睡眠期间的平均睡眠时间
     */
    public int getPerSleepTotalTime(List<SleepModel> sleepModelList) { // 平均总时间
        int totalTime = 0;
        int index = 0;
        for (SleepModel sleepModel : sleepModelList) {
            if (sleepModel.totalTime > 0) {
                totalTime += sleepModel.totalTime;
                index++;
            }
        }
        if (index != 0) {
            totalTime = totalTime / index;
        }
        return totalTime;
    }

    /**
     * @param sleepModelList
     * @return 得到一段睡眠期间的平均深睡睡眠时间
     */
    public int getPerSleepDeepTime(List<SleepModel> sleepModelList) { // 平均深睡眠
        int deepTime = 0;
        int index = 0;
        for (SleepModel sleepModel : sleepModelList) {
            if (sleepModel.deepTime > 0) {
                deepTime += sleepModel.deepTime;
                index++;
            }
        }
        if (index != 0) {
            deepTime = deepTime / index;
        }
        return deepTime;
    }

    /**
     * @param sleepModelList
     * @return 得到一段睡眠期间的平均浅睡睡眠时间
     */
    public int getPerSleepLightTime(List<SleepModel> sleepModelList) { // 平均浅睡眠
        int lightTime = 0;
        int index = 0;
        for (SleepModel sleepModel : sleepModelList) {
            if (sleepModel.lightTime > 0) {
                lightTime += sleepModel.lightTime;
                index++;
            }
        }
        if (index != 0) {
            lightTime = lightTime / index;
        }
        return lightTime;
    }

    /**
     * @param sleepModelList
     * @return 得到一段睡眠期间的平均清醒睡眠时间
     */
    public int getPerSleepSoberTime(List<SleepModel> sleepModelList) { // 平均清醒
        int soberTime = 0;
        int index = 0;
        for (SleepModel sleepModel : sleepModelList) {
            soberTime += sleepModel.soberTime;
            index++;
        }
        if (index != 0) {
            soberTime = soberTime / index;
        }
        return soberTime;
    }

    /**
     * 在resolveMonthModeSleepInfo后条用获取key集合
     *
     * @return 0代表1号
     */
    public List<Integer> getMonthDayKey() {
        return monthDayKey;
    }

    /**
     * @return 得到月总睡眠的value 集合， 与monthDayKey 一一对应
     */
    public List<Integer> getMonthTotalSleepDayValue() {
        return monthTotalSleepDayValue;
    }

    /**
     * @return 得到月深睡睡眠的value 集合， 与monthDayKey 一一对应
     */
    public List<Integer> getMonthDeepSleepDayValue() {
        return monthDeepSleepDayValue;
    }

    /**
     * @return 得到月浅睡睡眠的value 集合， 与monthDayKey 一一对应
     */
    public List<Integer> getMonthLightSleepDayValue() {
        return monthLightSleepDayValue;
    }

    /**
     * @return 得到月清醒睡眠的value 集合， 与monthDayKey 一一对应
     */
    public List<Integer> getMonthsoberSleepDayValue() {
        return monthsoberSleepDayValue;
    }


}

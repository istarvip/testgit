package com.walnutin.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.walnutin.db.SqlHelper;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.WeekInfo;
import com.walnutin.activity.MyApplication;
import com.walnutin.util.Conversion;
import com.walnutin.util.DateUtils;
import com.walnutin.util.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by caro on 16/6/8.
 * <p/>
 * please instance this singon in application.maybe
 */
public class RunManager {
    private List<DailyInfo> dailyInfoList;//2周的数据temp
    private List<WeekInfo> weekInfoList;//一月的数据temp
    private List<DailyInfo> yearInfoList;//一年的数据temp
    private int currentWeekPosition = 1;//当周position默认为第二周
    private int currentDailyPosition = 0;//当天position
    private String startDay;
    private String endDay;
    private int todayPositon;           //指定日 所代表的今日
    private Date specilData;         //跳转到 指定日期 时间
    private static RunManager instance;
    private Context context;
    IDaySourceChange iDaySourceChange;
    IMonthSourceChange iMonthSourceChange;
    IYearSourceChange iYearSourceChange;

    public interface IDaySourceChange {
        public void noticeData(List<DailyInfo> df, int weekPos, int index);
    }

    public interface IMonthSourceChange {
        public void noticeData(List<WeekInfo> df, int weekPos, int index);
    }

    public interface IYearSourceChange {
        public void noticeData(List<DailyInfo> df, int weekPos, int index);
    }

    public void setOnIDaySourceChange(IDaySourceChange daySourceChange) {
        iDaySourceChange = daySourceChange;
    }

    public void setiMonthSourceChange(IMonthSourceChange iMonthSourceChange) {
        this.iMonthSourceChange = iMonthSourceChange;
    }

    public void setiYearSourceChange(IYearSourceChange iYearSourceChange) {
        this.iYearSourceChange = iYearSourceChange;
    }

    /**
     * @param context
     * @return
     */
    public static RunManager getInstance(Context context) {
        if (instance == null) {
            instance = new RunManager(context);
        }
        return instance;
    }

    public List<DailyInfo> getDailyInfoList() {
        return dailyInfoList;
    }

    /**
     * @param mcontext
     */
    public RunManager(Context mcontext) {
        this.context = mcontext;

    }

    public Date getSpecilData() {
        return specilData;
    }

    public void setSpecilData(Date specilData) {
        this.specilData = specilData;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public List<WeekInfo> getWeekInfoList() {
        return weekInfoList;
    }

    public List<DailyInfo> getYearInfoList() {
        return yearInfoList;
    }

    public void setYearInfoList(List<DailyInfo> yearInfoList) {
        this.yearInfoList = yearInfoList;
        if (iYearSourceChange != null) {
            iYearSourceChange.noticeData(yearInfoList, 0, currentDailyPosition);
        }
    }

    public int getTodayPositon() {
        return todayPositon;
    }

    public void setWeekInfoList(List<WeekInfo> weekInfoList) {
        this.weekInfoList = weekInfoList;
        if (iMonthSourceChange != null) {
            iMonthSourceChange.noticeData(weekInfoList, 0, currentDailyPosition);
        }
    }

    public synchronized void setDailyInfoList(List<DailyInfo> mdailyInfoList) {
        this.dailyInfoList = mdailyInfoList;

        if (iDaySourceChange != null) {
            iDaySourceChange.noticeData(dailyInfoList, currentWeekPosition, currentDailyPosition);
        }

    }

    public List<DailyInfo> getDailyInfoList(List<DailyInfo> mdailyInfoList) {
        return this.dailyInfoList;

    }

    /************************************************************/
    /**
     * @return 获得当周position
     */
    public int getCurrentWeekPosition() {
        return currentWeekPosition;
    }

    /**
     * 设置当周position
     *
     * @param currentWeekPosition
     */
    public synchronized void setCurrentWeekPosition(int currentWeekPosition) {

        this.currentWeekPosition = currentWeekPosition;

        if (iDaySourceChange != null) {
            iDaySourceChange.noticeData(dailyInfoList, currentWeekPosition, currentDailyPosition);
        }

    }


    /**
     * 获得当天position
     *
     * @return
     */
    public int getCurrentDailyPosition() {
        return currentDailyPosition;
    }

    /**
     * 设置当天position
     *
     * @param currentDailyPosition
     */
    public synchronized void setCurrentDailyPosition(int currentDailyPosition) {
        this.currentDailyPosition = currentDailyPosition;
    }
    /************************************************************/


    /**
     * 获得当天position
     *
     * @return
     */

    public void setTodayPositon(int todayPositon) {
        this.todayPositon = todayPositon;
    }

    public int getTodayPosition() {
        return todayPositon;
    }

    /**
     * according to:
     * DateUtils.getCurrentDayGapLastMonday() - 1
     * <p/>
     * 构造2周数据
     *
     * @return
     */
    private List<DailyInfo> getVirtualStepList() {
        List<DailyInfo> list = new ArrayList<DailyInfo>();
        int len = todayPositon  ;      //
        for (int i = 0; i <= len; i++) {
            DailyInfo dailyInfo = new DailyInfo();
            dailyInfo.setAccount(MyApplication.account);
            dailyInfo.setCalories(0);
            dailyInfo.setStep(0);
            dailyInfo.setDistance(0);
            dailyInfo.setDates(DateUtils.getBeforeDate(specilData,i - len));
            list.add(dailyInfo);
        }
      //  dailyInfoList = list;
        return list;
    }

    /*
    *
    * 虚拟指定日期 内 数据 ， startDay 必须为一周开始日期， endDay为周末
    * */
    private List<DailyInfo> getVirtualDayStepList() throws ParseException {
        List<DailyInfo> list = new ArrayList<DailyInfo>();
        int gapDays = DateUtils.daysBetween(startDay, endDay);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.stringToDate(startDay));
        for (int i = 0; i <= gapDays; i++) {
            DailyInfo dailyInfo = new DailyInfo();
            dailyInfo.setAccount(MyApplication.account);
            dailyInfo.setCalories(0);
            dailyInfo.setStep(0);
            dailyInfo.setDistance(0);
            dailyInfo.setDates(sdf.format(cal.getTime()));
            list.add(dailyInfo);
            cal.add(Calendar.DATE, 1);
        }
        dailyInfoList = list;
        return list;
    }

    public List<WeekInfo> getMonthVirtualList() {
        List<WeekInfo> list = new ArrayList<WeekInfo>();
        int len = 6;
        Date startDate = DateUtils.getMonthStart(specilData);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
      //  String weekStart;
       // String weekEnd;
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            WeekInfo dailyInfo = new WeekInfo();
            dailyInfo.setCalories(0);
            dailyInfo.setStep(0);
            dailyInfo.setDistance(0);
            dailyInfo.setDates(sdf.format(startDate));
            dailyInfo.setWeekOfYear(cal.get(Calendar.WEEK_OF_YEAR));
        //    weekStart = DateUtils.formatData(dailyInfo.getDates());
       //     weekEnd = DateUtils.formatData(sdf.format(DateUtils.dayToNextDate(startDate, 6)));
       //     dailyInfo.setWeekDateFormat(weekStart + "-" + weekEnd);
            list.add(dailyInfo);
            //  System.out.println(dailyInfo.getWeekDateFormat());
            startDate = DateUtils.oneWeekNext(startDate);
            cal.setTime(startDate);
        }
        weekInfoList = list;
        return list;
    }

    // 虚拟年份
    public List<DailyInfo> getYearVirtualList() {
        List<DailyInfo> list = new ArrayList<DailyInfo>();
        int len = 6;
        Date startDate = specilData;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, -5);
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            DailyInfo dailyInfo = new DailyInfo();
            dailyInfo.setCalories(0);
            dailyInfo.setStep(0);
            dailyInfo.setDistance(0);
            //    dailyInfo.setUid(cal.get(Calendar.WEEK_OF_YEAR)); // 一年中第几周
            dailyInfo.setDates(sdf.format(cal.getTime()));
            list.add(dailyInfo);
            cal.add(Calendar.MONTH, 1);
        }
        yearInfoList = list;
        return list;
    }

    /**
     * 根据当周获取当周最大步数
     *
     * @param
     * @return
     */
    public int getWeekMaxStep() {
        int maxValue = 0;
        if (dailyInfoList != null && dailyInfoList.size() > 0) {
            if (currentWeekPosition == 0) {  //第一周
                for (int i = 0; i < 7; i++) {
                    if (dailyInfoList.get(i).getStep() > maxValue) {
                        maxValue = dailyInfoList.get(i).getStep();
                    }
                }
            } else {
                for (int i = 7; i < dailyInfoList.size(); i++) {
                    if (dailyInfoList.get(i).getStep() > maxValue) {
                        maxValue = dailyInfoList.get(i).getStep();
                    }
                }
            }
        }

        return maxValue;
    }


    /**
     * 保存近2周数据到sp中
     *
     * @param context
     * @param DailyInfo
     */
    public synchronized void saveDailyInfolistToSp(Context context, List<DailyInfo> DailyInfo) {
        if (DailyInfo == null) {
            return;
        }
        String tasteBase64 = Conversion.listToString(DailyInfo);
        SharedPreferences sp = context.getSharedPreferences(MyApplication.account, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("DailyInfoList", tasteBase64);
        editor.commit();
    }

    public synchronized void saveWeekInfolistToSp(Context context, List<WeekInfo> DailyInfo) {
        if (DailyInfo == null) {
            return;
        }
        String tasteBase64 = Conversion.listToString(DailyInfo);
        SharedPreferences sp = context.getSharedPreferences(MyApplication.account, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("WeekInfoList", tasteBase64);
        editor.commit();
    }

    public synchronized void saveYearInfolistToSp(Context context, List<DailyInfo> DailyInfo) {
        if (DailyInfo == null) {
            return;
        }
        String tasteBase64 = Conversion.listToString(DailyInfo);
        SharedPreferences sp = context.getSharedPreferences(MyApplication.account, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("YearInfoList", tasteBase64);
        editor.commit();
    }

    /**
     * 从 sp中获得缓存的两周数据信息,如果缓存为空,则返回两周的空列表
     *
     * @param context
     * @return
     */
    public synchronized List<DailyInfo> getDailyInfolistFromSp(Context context) {
        List<DailyInfo> cateList = null;
        SharedPreferences sp = context.getSharedPreferences(MyApplication.account, Context.MODE_PRIVATE);
        String toastBase64 = sp.getString("DailyInfoList", null);
        //如果返回数据为空,则没缓存,返回2周空的数据
        if (toastBase64 == null) {
            return getVirtualStepList();
        }
        cateList = Conversion.stringToList(toastBase64);
        return cateList;
    }

    public synchronized List<WeekInfo> getWeekInfolistFromSp(Context context) {
        List<WeekInfo> cateList = null;
        SharedPreferences sp = context.getSharedPreferences(MyApplication.account, Context.MODE_PRIVATE);
        String toastBase64 = sp.getString("WeekInfoList", null);
        //如果返回数据为空,则没缓存,返回2周空的数据
        if (toastBase64 == null) {
            return getMonthVirtualList();
        }
        cateList = Conversion.stringToList(toastBase64);
        return cateList;
    }

    public synchronized List<DailyInfo> getYearInfolistFromSp(Context context) {
        List<DailyInfo> cateList = null;
        SharedPreferences sp = context.getSharedPreferences(MyApplication.account, Context.MODE_PRIVATE);
        String toastBase64 = sp.getString("YearInfoList", null);
        //如果返回数据为空,则没缓存,返回2周空的数据
        if (toastBase64 == null) {
            return getYearVirtualList();
        }
        cateList = Conversion.stringToList(toastBase64);
        return cateList;
    }

    // 纠正本地集合到今天的列表
    public List<DailyInfo> correctList(Context context) throws ParseException {
        List<DailyInfo> tmpList = this.getDailyInfolistFromSp(context);
        int listLength = tmpList.size();
        int gapLen = 0;
        if (listLength > 0) {
            String lastDataDate = tmpList.get(tmpList.size() - 1).getDates();
            gapLen = DateUtils.daysBetween(lastDataDate, TimeUtil.getCurrentDate());
        }
        if (gapLen >= 14) {          // 超过两周未登录， 重置前两周数据为空
            getVirtualStepList();
            return getVirtualStepList();
        }
        for (int i = 0; i < gapLen; i++) {
            DailyInfo dailyInfo = new DailyInfo();
            dailyInfo.setAccount(MyApplication.account);
            dailyInfo.setCalories(0);
            dailyInfo.setStep(0);
            dailyInfo.setDistance(0);
            dailyInfo.setDates(DateUtils.getBeforeDate(specilData,i + 1 - gapLen));
            tmpList.add(dailyInfo);
        }

        listLength = tmpList.size();
        for (int i = listLength; i > todayPositon + 1; i--) {
            tmpList.remove(0);
        }
        dailyInfoList = tmpList;
        return tmpList;
    }


    public List<DailyInfo> getLocalDayList() throws ParseException {
        dailyInfoList = getVirtualStepList();
        List<DailyInfo> tmpList = SqlHelper.instance().getLastDateStep(MyApplication.account, startDay, endDay);
        if (tmpList != null && tmpList.size() > 0) {
            for (DailyInfo dailyInfo : dailyInfoList) {
                for (DailyInfo tmp : tmpList) {
                    if (dailyInfo.getDates().equals(tmp.getDates())) {
                        dailyInfo.setStep(tmp.getStep());
                        dailyInfo.setCalories(tmp.getCalories());
                        dailyInfo.setUpLoad(tmp.isUpLoad());
                        dailyInfo.setDistance(tmp.getDistance());
                    }
                }
            }
        }

        return dailyInfoList;
    }


    public List<WeekInfo> getLocalWeekList() throws ParseException {
        weekInfoList = getMonthVirtualList();
        List<WeekInfo> tmpList = getWeekInfolistFromSp(MyApplication.getContext());
        if (tmpList != null && tmpList.size() > 0) {
            for (WeekInfo dailyInfo : weekInfoList) {
                for (WeekInfo tmp : tmpList) {
                    if (dailyInfo.getWeekOfYear() == tmp.getWeekOfYear()) {
                        dailyInfo.setStep(tmp.getStep());
                        dailyInfo.setCalories(tmp.getCalories());
                        dailyInfo.setUpLoad(tmp.isUpLoad());
                        dailyInfo.setDistance(tmp.getDistance());
                        dailyInfo.setDates(tmp.getDates());
                    }
                }
            }
        }

        return weekInfoList;
    }

    public List<DailyInfo> getLocalYearList() throws ParseException {
        yearInfoList = getYearVirtualList();
        List<DailyInfo> tmpList = getYearInfolistFromSp(MyApplication.getContext());
        if (tmpList != null && tmpList.size() > 0) {
            for (DailyInfo dailyInfo : yearInfoList) {
                for (DailyInfo tmp : tmpList) {
                    if (DateUtils.formatYearData(dailyInfo.getDates())
                            .equals(DateUtils.formatYearData(tmp.getDates()))) {
                        dailyInfo.setStep(tmp.getStep());
                        dailyInfo.setCalories(tmp.getCalories());
                        dailyInfo.setUpLoad(tmp.isUpLoad());
                        dailyInfo.setDistance(tmp.getDistance());
                    }
                }
            }
        }

        return yearInfoList;
    }




}

package com.walnutin.util;

import com.walnutin.entity.Week;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class DateUtils {

    private Week dates;

    // public

    // 得到 指定 date的那一周的所有日期
    static List<Week> getWeekDateList(Date date) {
        List<Week> dates = new ArrayList<Week>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if (weekDay == 1) {
            weekDay = 0;
        } else {
            weekDay = 1 - weekDay;
        }
        cal.add(Calendar.DATE, weekDay); // 移到一周的开始 日期

        for (int i = 0; i < 7; i++) {
            Week week = new Week();
            week.date = cal.getTime();
            week.dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            week.year = cal.get(Calendar.YEAR);
            week.month = cal.get(Calendar.MONTH) + 1;
            week.day = cal.get(Calendar.DATE);
            dates.add(week);
            cal.add(Calendar.DATE, 1);
        }

        return dates;
    }

    /**
     * 得到某一天对应周的周一
     */

    public static Date getWeekMonday(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if (weekDay == 1) {
            weekDay = 0;
        } else {
            weekDay = 1 - weekDay;
        }
        cal.add(Calendar.DATE, weekDay); // 移到一周的开始 日期
        //  System.out.println(sdf.format(cal.getTime()));
        return cal.getTime();

    }

    public static List<Week> getDateBetweenWeekList(Date startDate, Date endDate) throws ParseException {
        List<Week> weekList = new ArrayList<Week>();
        Date start = getWeekMonday(startDate);     //startDate 对应周的周一日期 比如6-19
        Date end = getWeekEndDay(endDate);         //endDate 对应周的周末日期  如  7-2
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int gapDate = daysBetween(sdf.format(start), sdf.format(end)); // 指定两个日期相差的天数
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        for (int i = 0; i <= gapDate; i++) {
            Week week = new Week();
            week.date = cal.getTime();
            week.dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            week.year = cal.get(Calendar.YEAR);
            week.month = cal.get(Calendar.MONTH) + 1;
            week.day = cal.get(Calendar.DATE);
            weekList.add(week);
            System.out.println(sdf.format(week.date));

            cal.add(Calendar.DATE, 1);

        }
        return weekList;
    }

    /**
     * 得到某一天对应周的周末
     */
    public static Date getWeekEndDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, 7 - weekDay); // 移到一周的开始 日期
        //   System.out.println(sdf.format(cal.getTime()));
        return cal.getTime();

    }


    // 得到 指定月份第一天日期
    public static Date getMonthStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_MONTH) - 1;
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if (week > 0) {
            cal.add(Calendar.DATE, -week * 7);
        }
        if (weekDay == 1) {
            weekDay = 0;
        } else {
            weekDay = 1 - weekDay;
        }

        cal.add(Calendar.DATE, weekDay);

        return cal.getTime();

    }

    // 得到 指定月份  月尾最后一天日期
    public static Date getMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_MONTH);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if (week < 6) {
            cal.add(Calendar.DATE, (6 - week) * 7);
        }
        if (weekDay == 7) {
            weekDay = 0;
        } else {
            weekDay = 7 - weekDay;
        }

        cal.add(Calendar.DATE, weekDay);

        return cal.getTime();

    }

    //构造一个月 的日期 数据
    public static List<Week> getMonthData(Date startDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDay);
        List<Week> weekList = new ArrayList<Week>();

        for (int i = 0; i < 6; i++) {
            Week week = new Week();
            week.date = cal.getTime();
            week.dayOfWeek = i;
            week.day = cal.get(Calendar.DATE);
            week.weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
            weekList.add(week);
            cal.add(Calendar.DATE, 7);
        }

        return weekList;
    }

    //得到一年 数据
    public static List<Week> getYearData(Date startDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDay);
        int mon = cal.get(Calendar.MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.MONTH, -mon);
        List<Week> weekList = new ArrayList<Week>();
        for (int i = 0; i < 12; i++) {
            Week week = new Week();
            week.date = cal.getTime();
            week.dayOfWeek = i;
            week.day = i + 1;
            weekList.add(week);
            //    System.out.println(sdf.format(week.date));
            cal.add(Calendar.MONTH, 1);
        }

        return weekList;
    }

    //得到一年 数据
    public static List<Week> getHalfYearData(Date startDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDay);
        int mon = cal.get(Calendar.MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.MONTH, -5);
        List<Week> weekList = new ArrayList<Week>();
        for (int i = 0; i < 6; i++) {
            Week week = new Week();
            week.date = cal.getTime();
            week.dayOfWeek = i;
            week.day =cal.get(Calendar.MONTH)+1;
            weekList.add(week);
            System.out.println(sdf.format(week.date));
            cal.add(Calendar.MONTH, 1);
        }


        return weekList;
    }

    // position 为 1 表示 这一周， 为0 表示 前一周， 为2 表示往后推一周
    public static List<Week> prevWeekDateList(int position, Date date) {

        Date date1 = date;
        if (position == 0) {
            date1 = oneWeekPrevious(date);
        }
        return getWeekDateList(date1);

    }

    // 根据指定日期 移动几天
    public static Date dayToNextDate(Date date, int pos) {
        return dateByAddingDate(date, 0, 0, pos, 0, 0, 0);
    }


    // 根据指定日期 推迟一周
    public static Date oneWeekNext(Date date) {
        return dateByAddingDate(date, 0, 0, 7, 0, 0, 0);
    }

    public static Date oneWeekPrevious(Date date) {
        return dateByAddingDate(date, 0, 0, -7, 0, 0, 0);
    }

    private static Date dateByAddingDate(Date date, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(date);
        localCalendar.add(Calendar.YEAR, paramInt1);
        localCalendar.add(Calendar.MONTH, paramInt2);
        localCalendar.add(Calendar.DATE, paramInt3);
        localCalendar.add(Calendar.HOUR_OF_DAY, paramInt4);
        localCalendar.add(Calendar.MINUTE, paramInt5);
        localCalendar.add(Calendar.SECOND, paramInt6);
        return localCalendar.getTime();
    }

    public static int getCurrentDayGapLastMonday() { //得到当前时间与上周一的 页数 ，
        Calendar calendar = Calendar.getInstance();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay + 7;
    }

    public static String getBeforeDate(Date date,int prev) {      // 往 推 prev天
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, prev);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(cal.getTime());
    }

    // 0为 上周一距离今天的数据位置， 1为三周数据
    public static int getTodayPosition(int type) {
        if (type == 0) {
            return getCurrentDayGapLastMonday();
        } else if (type == 1) {

        }
        return 0;
    }

    public static String formatData(String time) { //将年月日  转成月日 格式
        Date date = new Date();
//	   Calendar cal = Calendar.getInstance();
//	   cal.set(1016, 11, 30);
//	   date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("M/dd");
        return sdf1.format(date);


    }

    public static String formatYearData(String time) { //将年月日  转成年月 格式
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM");
        return sdf1.format(date);
    }

    public static String formatMonthData(String time) { //将年月日 转成 周格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatData="";
        try {
            Date   startDate = stringToDate(time);
            String weekStart;
            String weekEnd;
            weekStart= DateUtils.formatData(time);
            weekEnd = DateUtils.formatData(sdf.format(DateUtils.dayToNextDate(startDate,6)));
            formatData = weekStart+"-"+weekEnd;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return formatData;
    }



    public static int daysOfTwo(Date fDate, Date oDate) { //fDate 开始日期 比较两个日期相差的天数
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;

    }

    public static Date stringToDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date tmp;
        tmp = sdf.parse(date);
        return tmp;
    }

    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }


}



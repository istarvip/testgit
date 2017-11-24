package com.walnutin.util;

/**
 * 作者：MrJiang on 2016/8/17 18:25
 */
public class WeekUtils {

    public static byte getWeekByteByReapeat(int repeat) {
        byte weekPeroid = 0;
        try {
            String[] weeks = WeekUtils.parseRepeat(repeat, 1).split(",");
            for (int i = 0; i < weeks.length; i++) {
                switch (Integer.parseInt(weeks[i])) {
                    case 1:
                        weekPeroid |= Config.MONDAY;
                        break;
                    case 2:
                        weekPeroid |= Config.TUESDAY;
                        break;
                    case 3:
                        weekPeroid |= Config.WEDNESDAY;
                        break;
                    case 4:
                        weekPeroid |= Config.THURSDAY;
                        break;
                    case 5:
                        weekPeroid |= Config.FRIDAY;
                        break;
                    case 6:
                        weekPeroid |= Config.SATURDAY;
                        break;
                    case 7:
                        weekPeroid |= Config.SUNDAY;
                        break;
                }
            }
        } catch (Exception e) {

        }
        return weekPeroid;
    }


    public static String parseRepeat(int repeat, int flag) {
        String cycle = "";
        String weeks = "";
        if (repeat == 0) {
            repeat = 127;
        }
        if (repeat % 2 == 1) {
            cycle = "周一";
            weeks = "1";
        }
        if (repeat % 4 >= 2) {
            if ("".equals(cycle)) {
                cycle = "周二";
                weeks = "2";
            } else {
                cycle = cycle + "," + "周二";
                weeks = weeks + "," + "2";
            }
        }
        if (repeat % 8 >= 4) {
            if ("".equals(cycle)) {
                cycle = "周三";
                weeks = "3";
            } else {
                cycle = cycle + "," + "周三";
                weeks = weeks + "," + "3";
            }
        }
        if (repeat % 16 >= 8) {
            if ("".equals(cycle)) {
                cycle = "周四";
                weeks = "4";
            } else {
                cycle = cycle + "," + "周四";
                weeks = weeks + "," + "4";
            }
        }
        if (repeat % 32 >= 16) {
            if ("".equals(cycle)) {
                cycle = "周五";
                weeks = "5";
            } else {
                cycle = cycle + "," + "周五";
                weeks = weeks + "," + "5";
            }
        }
        if (repeat % 64 >= 32) {
            if ("".equals(cycle)) {
                cycle = "周六";
                weeks = "6";
            } else {
                cycle = cycle + "," + "周六";
                weeks = weeks + "," + "6";
            }
        }
        if (repeat / 64 == 1) {
            if ("".equals(cycle)) {
                cycle = "周日";
                weeks = "7";
            } else {
                cycle = cycle + "," + "周日";
                weeks = weeks + "," + "7";
            }
        }

        return flag == 0 ? cycle : weeks;
    }

}

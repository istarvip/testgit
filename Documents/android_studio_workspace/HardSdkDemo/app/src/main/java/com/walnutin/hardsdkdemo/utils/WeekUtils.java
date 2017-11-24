package com.walnutin.hardsdkdemo.utils;

/**
 * 作者：MrJiang on 2016/8/17 18:25
 */
public class WeekUtils {

    public static byte getWeekByteByReapeat(int repeat) {
        byte weekPeroid = 0;
        try {
            String[] weeks = WeekUtils.parseRepeat(repeat, 1,GlobalValue.LANGUAGE_CHINESE).split(",");
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


    public static String parseRepeat(int repeat, int flag,int language) {
        String cycle = "";
        String weeks = "";
        if (repeat == 0) {
            repeat = 127;
        }
        if (repeat % 2 == 1) {
            if(language == GlobalValue.LANGUAGE_CHINESE){
                cycle = "周一";
            }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                cycle = "MONDAY";
            }
            weeks = "1";
        }
        if (repeat % 4 >= 2) {
            if ("".equals(cycle)) {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周二";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "TUESDAY";
                }
                weeks = "2";
            } else {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周二";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "TUESDAY";
                }
                weeks = weeks + "," + "2";
            }
        }
        if (repeat % 8 >= 4) {
            if ("".equals(cycle)) {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周三";//
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "WEDNESDAY";//Wednesday
                }
                weeks = "3";
            } else {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周三";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "WEDNESDAY";
                }
                weeks = weeks + "," + "3";
            }
        }
        if (repeat % 16 >= 8) {
            if ("".equals(cycle)) {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周四";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "THURSDAY"; //Thursday
                }
                weeks = "4";
            } else {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周四";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "THURSDAY";
                }
                weeks = weeks + "," + "4";
            }
        }
        if (repeat % 32 >= 16) {
            if ("".equals(cycle)) {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周五";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "FRIDAY";
                }
                weeks = "5";
            } else {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周五";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "FRIDAY";
                }
                weeks = weeks + "," + "5";
            }
        }
        if (repeat % 64 >= 32) {
            if ("".equals(cycle)) {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周六";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "SATURDAY";//Saturday
                }
                weeks = "6";
            } else {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周六";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "SATURDAY";
                }
                weeks = weeks + "," + "6";
            }
        }
        if (repeat / 64 == 1) {
            if ("".equals(cycle)) {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周日";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "SUNDAY";
                }
                weeks = "7";
            } else {
                if(language == GlobalValue.LANGUAGE_CHINESE){
                    cycle = "周日";
                }else if(language == GlobalValue.LANGUAGE_ENGLISH){
                    cycle = "SUNDAY";
                }
                weeks = weeks + "," + "7";
            }
        }

        return flag == 0 ? cycle : weeks;
    }

}

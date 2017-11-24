package com.walnutin.view;

/**
 * Created by Administrator on 2016/7/28.
 */
public class TimeUtil {


    public static String MinutiToTime(int time) {

        String suffix = "";
        String prefix = "";
        time = time % 1440;
        int tmp = time / 60;
        suffix = String.valueOf(time % 60);
        prefix = String.valueOf(tmp);
        if (suffix.length() < 2) {
            suffix = "0" + suffix;
        }
        if (prefix.length() < 2) {
            prefix = "0" + prefix;
        }

        return String.valueOf(prefix + ":" + suffix);
    }

    public static String MinitueToPrefix(int time) {
        int tmp = time / 60;
        String prefix = String.valueOf(tmp);
        if (prefix.length() < 2) {
            prefix = "0" + prefix;
        }
        return prefix;
    }

    public static String MinitueToSuffix(int time) {
        time = time % 1440;
        String suffix = String.valueOf(time % 60);
        if (suffix.length() < 2) {
            suffix = "0" + suffix;
        }
        return suffix;
    }


}

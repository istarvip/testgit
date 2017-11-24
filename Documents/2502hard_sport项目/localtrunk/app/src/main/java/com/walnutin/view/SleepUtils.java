package com.walnutin.view;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */
public class SleepUtils {

    int[] duraionTimeArray = {15, 15, 60, 30, 15};
    int[] timePointArray = {1389, 1404, 24, 54, 69};
    int[] sleepStatusArray = {2, 1, 0, 1, 0};
    private List<Integer> perDurationTime;  //ÿһ�γ������ٷ���
    private List<Integer> durationStartPos; // ÿһ�ο�ʼλ��
    private List<Integer> durationStatus; // ÿһ��״̬�ı��
    String startSleep;
    String endSleep;
    private int allDurationTime;


    public int getDurationLen() {        // �������ٷ���
        int len = 0;
        for (int i = 0; i < duraionTimeArray.length; i++) {
            len += duraionTimeArray[i];
        }
        allDurationTime = len;
        return len;
    }

    List getPerDurationTime() {
        perDurationTime = new ArrayList<Integer>();
        for (int i = 0; i < duraionTimeArray.length; i++) {
            perDurationTime.add(duraionTimeArray[i]);
        }
        return perDurationTime;
    }

    public List<Integer> getDurationStartPos() {

        return durationStartPos;
    }


    public String  MinutiToTime(int time) {
        String suffix = "";
        String prefix ="";
        int tmp = time / 60;
        suffix = String.valueOf(tmp);
        prefix = String.valueOf(time % 60);
        if (suffix.length() < 2) {
            suffix = "0" + suffix;
        }
        if (prefix.length() < 2) {
            prefix = "0" + prefix;
        }

        return String.valueOf(prefix + ":" + suffix);
    }

    public List getDurationStatus() {
        durationStatus = new ArrayList<Integer>();
        for (int i = 0; i < sleepStatusArray.length; i++) {
            durationStatus.add(sleepStatusArray[i]);
        }
        return durationStatus;
    }

    public void setDurationStatus(List<Integer> durationStatus) {
        this.durationStatus = durationStatus;
    }

    public void setDurationStartPos(List<Integer> durationStartPos) {
        this.durationStartPos = durationStartPos;
    }

    public void setDuraionTimeArray(int[] duraionTimeArray) {
        this.duraionTimeArray = duraionTimeArray;
    }

    public void setTimePointArray(int[] timePointArray) {
        this.timePointArray = timePointArray;
        durationStartPos = new ArrayList<Integer>();
        int initValue = timePointArray[0] - duraionTimeArray[0];
        initValue = initValue < 0 ? initValue + 1440 : initValue;
        durationStartPos.add(initValue);
        for (int i = 1; i < timePointArray.length; i++) {
            initValue += duraionTimeArray[i - 1];
            durationStartPos.add(initValue % 1440);
        }

    }

    public void setSleepStatusArray(int[] sleepStatusArray) {
        this.sleepStatusArray = sleepStatusArray;
    }

    public int[] getDuraionTimeArray() {
        return duraionTimeArray;
    }

    public String getStartSleep() {
        startSleep = TimeUtil.MinutiToTime(durationStartPos.get(0));
        return startSleep;
    }

    public String getEndSleep() {
        endSleep = TimeUtil.MinutiToTime(timePointArray[timePointArray.length - 1]);
        return endSleep;
    }
}

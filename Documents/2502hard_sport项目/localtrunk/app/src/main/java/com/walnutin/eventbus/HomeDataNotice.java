package com.walnutin.eventbus;

/**
 * 作者：MrJiang on 2016/8/3 17:00
 */
public class HomeDataNotice {
    public static class StepChange {

    }

    public static class SleepChange {

    }

    public static class HeartRateChange {

    }

    public static class HeartRateRealChange {  //实时跳变
        public int isMeasuring;

        public HeartRateRealChange(int isMeasuring) {
            this.isMeasuring = isMeasuring;
        }

    }
}

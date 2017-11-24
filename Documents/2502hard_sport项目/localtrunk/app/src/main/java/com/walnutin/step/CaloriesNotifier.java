/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.walnutin.step;


import com.walnutin.activity.MyApplication;
import com.walnutin.util.MySharedPf;
import com.walnutin.util.PreferenceSettings;

/**
 * Calculates and displays the approximate calories.
 *
 * @author Levente Bagi
 */
public class CaloriesNotifier implements StepListener {

    public interface Listener {
        public void valueChanged(int value);

        public void passValue();
    }

    private Listener mListener;

    private static double METRIC_RUNNING_FACTOR = 1.02784823;
    private static double IMPERIAL_RUNNING_FACTOR = 0.75031498;

    private static double METRIC_WALKING_FACTOR = 0.708;
    private static double IMPERIAL_WALKING_FACTOR = 0.517;

    private double mCalories = 0;

    PreferenceSettings mSettings;
    MySharedPf mySharedPf;
    boolean mIsMetric;
    boolean mIsRunning;
    float mStepLength;
    float mBodyWeight;

    public CaloriesNotifier(Listener listener) {
        mListener = listener;
        mSettings = PreferenceSettings.getInstance(MyApplication.getContext());
        mySharedPf = MySharedPf.getInstance(MyApplication.getContext());
        reloadSettings();
    }

    public void setCalories(float calories) {
        mCalories = calories;
        notifyListener();
    }

    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mIsRunning = mSettings.isRunning();
        mStepLength = mSettings.getStepLength();
        mBodyWeight = mSettings.getBodyWeight();
        notifyListener();
    }

    public void resetValues() {
        mCalories = 0;
    }

    public void isMetric(boolean isMetric) {
        mIsMetric = isMetric;
    }

    public void setStepLength(float stepLength) {
        mStepLength = stepLength;
    }

    public void onStep() {
        System.out.println("mIsRunning: " + mIsRunning);
        if (mIsMetric) {
            mCalories +=
                    (mBodyWeight * (mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                            // Distance:
                            * mStepLength // centimeters
                            / 100000.0; // centimeters/kilometer
        } else {
            mCalories +=
                    (mBodyWeight * (mIsRunning ? IMPERIAL_RUNNING_FACTOR : IMPERIAL_WALKING_FACTOR))
                            // Distance:
                            * mStepLength // inches
                            / 63360.0; // inches/mile
        }

        notifyListener();
    }

    private void notifyListener() {
        mListener.valueChanged((int) mCalories);
    }

    public void passValue() {

    }


}


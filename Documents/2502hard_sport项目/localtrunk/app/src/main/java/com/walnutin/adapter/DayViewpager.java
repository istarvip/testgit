package com.walnutin.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.walnutin.manager.RunManager;
import com.walnutin.entity.DailyInfo;
import com.walnutin.activity.MyApplication;
import com.walnutin.hard.R;
import com.walnutin.view.DayStepView;

import java.util.List;

/**
 * Created by caro on 16/6/8.
 * day  data bind adapter
 */

public class DayViewpager  extends PagerAdapter {
    Context context;
    List<DailyInfo> dailyInfoList;

    public DayViewpager(Context context, List<DailyInfo> dailyInfoList) {
        this.context = context;
        this.dailyInfoList = dailyInfoList;
    }

    @Override
    public int getCount() {
        return dailyInfoList == null ? 0 : dailyInfoList.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.layout_dayview, null);
        DayStepView dayStepView = (DayStepView) view.findViewById(R.id.daystepview);
        DailyInfo dailyInfo = dailyInfoList.get(position);
        dayStepView.setDate(dailyInfo.getDates());
        dayStepView.setDaylyStep(String.valueOf(dailyInfo.getStep()));
        dayStepView.setWeeklyHighStep(String.valueOf(RunManager.getInstance(context).getWeekMaxStep()));
        if (position == RunManager.getInstance(MyApplication.getContext()).getTodayPosition() ){
            dayStepView.setTodayAniProgress((dailyInfo.getStep() * 360) / 10000);
        }else {
            dayStepView.setAnimProgress((dailyInfo.getStep() * 360) / 10000);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

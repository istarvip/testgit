package com.walnutin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walnutin.manager.RunManager;
import com.walnutin.adapter.WeekPagerAdapter;
import com.walnutin.eventbus.WeekChangeNotify;
import com.walnutin.hard.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2016/5/23.
 */
public class DayModeFragment extends BaseFragment {

    private WeekPagerAdapter weekPagerAdapter;
    private ViewPager weekViewPager;
    private RunManager runManager;
    private int currentWeekPosition = 1;//当周position


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daymode, container, false);
        runManager = RunManager.getInstance(getContext());
        weekViewPager = (ViewPager) view.findViewById(R.id.viewpage_slide_week);
        weekPagerAdapter = new WeekPagerAdapter(getChildFragmentManager());
        weekViewPager.setAdapter(weekPagerAdapter);
        initEvent();
        weekViewPager.setCurrentItem(currentWeekPosition);
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    private void initEvent() {
        weekViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentWeekPosition = position;
                runManager.setCurrentWeekPosition(currentWeekPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Subscribe
    public void onWeekChanged(WeekChangeNotify weekChangeNotify) {
        System.out.println("weekChangeNotify:"+weekChangeNotify);
        weekViewPager.setCurrentItem(weekChangeNotify.weekPos);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

}

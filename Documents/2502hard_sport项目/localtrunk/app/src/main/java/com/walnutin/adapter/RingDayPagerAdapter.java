package com.walnutin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.walnutin.entity.DailyInfo;
import com.walnutin.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class RingDayPagerAdapter extends FragmentStatePagerAdapter {

    private List<DailyInfo> dailyInfo =new ArrayList<>();
    private int currentWeekPosition;

    public RingDayPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
      //  return DayModeFragment.newInstance(position,currentWeekPosition, dailyInfo);
         return  null;
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    public   void setDailyInfoList(List<DailyInfo> inf) {
        this.dailyInfo = inf;
    //  notifyDataSetChanged();
    }

  public   void setCurrentWeekPosition(int weekPosition) {
        currentWeekPosition = weekPosition;
    //    notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        //      return MainActivity.NUM_ITEMS;
        return DateUtils.getCurrentDayGapLastMonday();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}

package com.walnutin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.walnutin.fragment.DayViewpagerFragment;

public class WeekPagerAdapter extends FragmentStatePagerAdapter {

    public WeekPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return DayViewpagerFragment.newInstance(position);
       // return  null;
    }

    @Override
    public int getCount() {
  //      return MainActivity.NUM_ITEMS;
        return  2;
    }

}

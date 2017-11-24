package com.walnutin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public  class FragmentsAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public FragmentsAdapter(FragmentManager fm,List<Fragment>fs) {
        super(fm);
        fragments = fs;
    }
    public  void setAdapterData(List<Fragment>fs){
        fragments = fs;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

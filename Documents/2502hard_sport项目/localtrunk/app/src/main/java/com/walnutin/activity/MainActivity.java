package com.walnutin.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.walnutin.adapter.FragmentsAdapter;
import com.walnutin.entity.UpdateModule;
import com.walnutin.fragment.ActivitysFragment;
import com.walnutin.fragment.BaseFragment;
import com.walnutin.fragment.GroupFragment;
import com.walnutin.fragment.HomeFragment;
import com.walnutin.fragment.MyFragment;
import com.walnutin.hard.R;
import com.walnutin.manager.TodayDataManager;
import com.walnutin.service.StepService;
import com.walnutin.util.MySharedPf;
import com.walnutin.util.PreferenceSettings;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    private final int[] mainLabel = {R.id.lable_viewpage, R.id.label_group, R.id.label_activity, R.id.label_me};
    private final int[] mainImageViewId = {R.id.viewpageid, R.id.groupid, R.id.activityid, R.id.meid};
    private int mImageArrayNotSelected[] = {R.drawable.homepage_notselected,
            R.drawable.group_notselected, R.drawable.activity_notselected, R.drawable.me_notselected};
    private int mImageArraySelected[] = {R.drawable.homepage_selected,
            R.drawable.group_selected, R.drawable.activity_selected, R.drawable.me_selected};

    private ImageView[] imageViews = new ImageView[4];
    private TextView[] textViews = new TextView[4];
    private FragmentsAdapter fragmentsAdapter;
    private ViewPager mViewPager;
    private BaseFragment groupFragment, activityFragment, myFragment, homeFragment;
    //  viewpageFragment,
    private ImageView running;
    List<Fragment> listFragment;
    private final int itemSize = 4;
    private PreferenceSettings mPedometerSettings;
    TodayDataManager todayDataManager;
    Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        MyApplication.account = MySharedPf.getInstance(this).getString("account");
        if (MyApplication.account == null) {
            MyApplication.account = "visitor";
        }
        todayDataManager = TodayDataManager.getInstance(this);

        initView();
        mPedometerSettings = PreferenceSettings.getInstance(MyApplication.getContext());
        startStepService();

        UpdateModule.getInstance().checkNewVersion(this);    // 检查新版本

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
          //      Intent WatchIntent = new Intent(MainActivity.this, com.walnutin.Presenter.LinkDetailActivity.class);
            //    startActivity(WatchIntent);
            }
        }, 1000);
        //   System.out.println("activity: main"+this);


//        Intent intent2 = new Intent();
//        intent2.setClass(this,PhoneSettingActivity.class);
//        startActivity(intent2);

    }

    private void initView() {
        for (int i = 0; i < textViews.length; i++) {       //初始化底部文字控件
            textViews[i] = (TextView) this.findViewById(mainLabel[i]);
            textViews[i].setOnClickListener(this);
        }
        for (int i = 0; i < mainImageViewId.length; i++) {       //初始化底部文字控件
            imageViews[i] = (ImageView) findViewById(mainImageViewId[i]);
            imageViews[i].setOnClickListener(this);
        }
        running = (ImageView) findViewById(R.id.runningid);
        running.setOnClickListener(this);
        listFragment = new ArrayList<Fragment>();
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

        //  viewpageFragment = new HomeNewPagerFragment();
        homeFragment = new HomeFragment();
        groupFragment = new GroupFragment();
        activityFragment = new ActivitysFragment();
        myFragment = new MyFragment();
        listFragment.add(homeFragment);
        listFragment.add(groupFragment);
        listFragment.add(activityFragment);
        listFragment.add(myFragment);
        fragmentsAdapter = new FragmentsAdapter(getSupportFragmentManager(), listFragment);
        mViewPager.setAdapter(fragmentsAdapter);

        setViewPagerListener();
        mViewPager.setCurrentItem(0, false);
        mViewPager.setOffscreenPageLimit(4);
        setBottomBackground(0);

    }

    void setViewPagerListener() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentPage = 0;

            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < itemSize) {
                    //System.out.println("onPageSelected " + position);
                    currentPage = position;
                    setBottomBackground(currentPage);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setBottomBackground(int index) {
        for (int i = 0; i < itemSize; i++) {
            textViews[i].setTextColor(getResources().getColor(R.color.font_defaults));
            imageViews[i].setImageResource(mImageArrayNotSelected[i]);
        }

        textViews[index].setTextColor(getResources().getColor(R.color.red_background_selected));
        imageViews[index].setImageResource(mImageArraySelected[index]);
    }

    @Override
    public void onClick(View view) {



        int id = view.getId();
        if (id == R.id.runningid) {
            Intent intent = new Intent(MainActivity.this, RunningActivity.class);
            startActivity(intent);
            return;
        }

        for (int i = 0; i < itemSize; i++) {
            if (id == mainImageViewId[i] || mainLabel[i] == id) {
                setBottomBackground(i);
                mViewPager.setCurrentItem(i, false);
                break;
            }
        }




    }

    private boolean mIsRunning = false;
    private boolean mQuitting = false;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);       //统计时长
        //   if (mIsRunning) {
        mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        //    }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindStepService();
        mPedometerSettings.saveTimestamp();
        handler.removeCallbacksAndMessages(null);
        //      Toast.makeText(getApplicationContext(), "退出了", Toast.LENGTH_SHORT).show();
    }

    private StepService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder) service).getService();

            mService.registerCallback(mCallback);
            //    mService.reloadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            //   ((HomeNewPagerFragment) viewpageFragment).refreshSteps( value);
            todayDataManager.setTodayStep(value);
            //     EventBus.getDefault().post(new StepChangeNotify.StepChanged(value));
        }

        public void distanceChanged(float value) {
            //   ((HomeNewPagerFragment) viewpageFragment).refreshDistance( value);
            todayDataManager.setTodayDistance(value);
        }

        public void caloriesChanged(int value) {
            //   ((HomeNewPagerFragment) viewpageFragment).refreshCalories(value);
            todayDataManager.setTodayCalories(value);
        }

        @Override
        public void timeChanged(int value) {
            // TODO Auto-generated method stub
            //    ((HomeNewPagerFragment) viewpageFragment).refreshSteps( value);
        }
    };

    private void startStepService() {
        bindStepService();
    }

    private void bindStepService() {
        bindService(new Intent(MainActivity.this, StepService.class), mConnection,
                Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        if (mConnection != null)
            unbindService(mConnection);
    }

    private void stopStepService() {
        if (mService != null) {
            stopService(new Intent(MainActivity.this, StepService.class));
        }
        mIsRunning = false;
    }

    long timeFirst;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() == 0) {
            moveTaskToBack(true);//true对任何Activity都适用
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            mViewPager.setCurrentItem(0);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }



}

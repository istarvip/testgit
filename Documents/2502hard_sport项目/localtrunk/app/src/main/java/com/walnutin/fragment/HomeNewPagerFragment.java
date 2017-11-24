package com.walnutin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.eventbus.UpdateNotify;
import com.walnutin.manager.RunManager;
import com.walnutin.db.SqlHelper;
import com.walnutin.entity.BaseInfo;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.DateType;
import com.walnutin.entity.WeekInfo;
import com.walnutin.eventbus.DailyChangNotify;
import com.walnutin.eventbus.DailyStepListResult;
import com.walnutin.eventbus.StepChangeNotify;
import com.walnutin.eventbus.WeekChangeNotify;
import com.walnutin.activity.MyApplication;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.manager.TodayDataManager;
import com.walnutin.util.DateUtils;
import com.walnutin.util.PreferenceSettings;
import com.walnutin.util.TimeUtil;
import com.walnutin.view.DayStepView;
import com.walnutin.view.SimpleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * reversion 210 --211
 * Created by Administrator on 2016/5/6.
 */
public class HomeNewPagerFragment extends BaseFragment implements OnClickListener, DayStepView.onSwipeGestureListener {

    private RelativeLayout topPanel;
    private int specialPosition = 0;
    private View view = null;
    private ImageView im_map;
    boolean isFirstLoc = true; // 是否首次定位
    private PreferenceSettings mPreferenceSetting;
    private DayStepView mDayStepView;
    private SimpleLableView todayStepSL;
    private SimpleLableView todayDistanceSL;
    private SimpleLableView todayCalSL;
    private List<DailyInfo> dailyInfoList;
    private List<WeekInfo> weekInfoList;
    private List<DailyInfo> yearInfoList;
    private int currentWeekPosition;//当周position
    private int currentDailyPosition;//当天position
    private int curSwipePosition;
    private String endDay;//昨天
    private String startDay;//上月
    private RunManager runManager;
    TextView txtDay;
    TextView txtWeek;
    TextView txtYear;
    DayModeFragment dayModeFragment;
    WeekModeFragment weekModeFragment;
    YearModeFragment yearModeFragment;
    private FragmentManager fragmentManager;
    FragmentTransaction transaction;
    private DateType dateType;
    TodayDataManager todayDataManager;
    ImageView oneKeyImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newhomepage, container, false);
        mPreferenceSetting = PreferenceSettings.getInstance(getActivity());
        EventBus.getDefault().register(this);
        runManager = RunManager.getInstance(getActivity());
        fragmentManager = getChildFragmentManager();
        todayDataManager =TodayDataManager.getInstance(getContext());


        initView();
        try {
            Calendar calendar = Calendar.getInstance();
            //       calendar.set(2016,3,1);
            loadData(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initView() {
        topPanel = (RelativeLayout) view.findViewById(R.id.topPanel);
        oneKeyImg = (ImageView) view.findViewById(R.id.oneKeyshare);
        //dayViewpager = (SmartViewPager) view.findViewById(R.id.lazyviewpager);
        mDayStepView = (DayStepView) view.findViewById(R.id.daystepview);
        mDayStepView.setPager((ViewPager) getActivity().findViewById(R.id.mViewPager));
        mDayStepView.setSwipeGestureListener(this);
        todayStepSL = (SimpleLableView) view.findViewById(R.id.today_step);
        todayDistanceSL = (SimpleLableView) view.findViewById(R.id.today_distance);
        todayCalSL = (SimpleLableView) view.findViewById(R.id.today_cal);
        txtDay = (TextView) view.findViewById(R.id.type_day);
        txtWeek = (TextView) view.findViewById(R.id.type_week);
        txtYear = (TextView) view.findViewById(R.id.type_year);
        im_map = (ImageView) view.findViewById(R.id.im_map);
        im_map.setOnClickListener(this);
        txtDay.setOnClickListener(this);
        txtWeek.setOnClickListener(this);
        txtYear.setOnClickListener(this);
        oneKeyImg.setOnClickListener(this);

    }

    private void loadData(Date date) throws ParseException {
        todayDataManager.setTodayDate(TimeUtil.getCurrentDate()); // 设置今天的日期

        Date today = date;
        runManager.setSpecilData(today);  // 设置指定日期时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date lastWeekDate = DateUtils.oneWeekPrevious(today); //往前推一周
        currentDailyPosition = DateUtils.daysBetween(sdf.format(DateUtils.getWeekMonday(lastWeekDate)), sdf.format(today)); //上周一距离 今日的索引
        curSwipePosition = currentDailyPosition;
        specialPosition = currentDailyPosition;

        endDay = DateUtils.getBeforeDate(today, 0);
        startDay = DateUtils.getBeforeDate(today, -specialPosition);
        runManager.setStartDay(startDay);
        runManager.setEndDay(endDay);

        currentWeekPosition = 1;//当前星期为第二周,即最新一周
        runManager.setCurrentWeekPosition(currentWeekPosition);//init position to manager
        runManager.setTodayPositon(specialPosition);
        runManager.setCurrentDailyPosition(currentDailyPosition);  //init position to manager

        try {
            dailyInfoList = runManager.getLocalDayList();//得到2周的数据
            weekInfoList = runManager.getLocalWeekList();
            yearInfoList = runManager.getLocalYearList();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mDayStepView.showData(dailyInfoList.get(currentDailyPosition), currentDailyPosition);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new DailyChangNotify(currentDailyPosition));
            }
        }, 100);

        HttpImpl.getInstance().getStepList(startDay, endDay, "日");
        txtDay.performClick();

        startDay = sdf.format(DateUtils.getMonthStart(today));
        endDay = sdf.format(DateUtils.getMonthEnd(today));
        System.out.println("weekOfYear: startDay" + startDay + " end:" + endDay);
        HttpImpl.getInstance().getStepList(startDay, endDay, "周");


        endDay = sdf.format(today);
        HttpImpl.getInstance().getStepList(null, endDay, "年");

    }

    @Subscribe
    public void updateData(UpdateNotify updateNotify) throws ParseException {
        loadData(new Date());
    }

    @Subscribe
    public void onWeekDayClicked(StepChangeNotify.WeekDayPostion wdp) {
        int pos = wdp.postion;
        currentWeekPosition = runManager.getCurrentWeekPosition();
        if (currentWeekPosition == 0) {
            curSwipePosition = pos;
            mDayStepView.showData(dailyInfoList.get(pos), pos);
            runManager.setCurrentDailyPosition(pos);
        } else if (currentWeekPosition == 1) {
            curSwipePosition = 7 + pos;
            mDayStepView.showData(dailyInfoList.get(7 + pos), 7 + pos);
            runManager.setCurrentDailyPosition(7 + pos);
        }
        updateStepView();
    }

    @Subscribe
    public void onMonthDayClicked(StepChangeNotify.MonthDayPostion wdp) {
        int pos = wdp.postion;
        refreshStepByClicked(weekInfoList.get(pos).getStep());
        refreshCaloriesByClicked(weekInfoList.get(pos).getCalories());
        refreshDistanceByClicked(weekInfoList.get(pos).getDistance());
    }

    @Subscribe
    public void onYearDayClicked(StepChangeNotify.YearDayPostion wdp) {
        int pos = wdp.postion;
        refreshStepByClicked(yearInfoList.get(pos).getStep());
        refreshCaloriesByClicked(yearInfoList.get(pos).getCalories());
        refreshDistanceByClicked(yearInfoList.get(pos).getDistance());
    }

    /**
     * 请求历史数据,并保存到数据库sp中
     *
     * @param dsr
     */
    @Subscribe
    public void onResultStepList(DailyStepListResult dsr) {
        int state = dsr.getState();

        if (dsr.datetype.equals("日")) {
            List<BaseInfo> tmpList = dsr.getStep();
            // TODO: 16/6/12 这里循环有问题. 应该里面嵌套
            if (tmpList != null && tmpList.size() > 0) {
                for (DailyInfo df : dailyInfoList) {  //
                    for (int i = 0; i < tmpList.size(); i++) {
                        BaseInfo tmp = tmpList.get(i);
                        if (df.getDates().equals(tmp.getDates())) {
                            df.setDistance(tmp.getDistance());
                            df.setStep(tmp.getStep());
                            df.setCalories(tmp.getCalories());
                            df.setUpLoad(1);
                        }
                    }
                }
                runManager.setDailyInfoList(dailyInfoList);
                Intent StepsIntent = new Intent("com.hard.stepChangeIntent");
                StepsIntent.putExtra("step", dailyInfoList.get(specialPosition).getStep());
                StepsIntent.putExtra("distance", dailyInfoList.get(specialPosition).getDistance());
                StepsIntent.putExtra("calories", dailyInfoList.get(specialPosition).getCalories());
                MyApplication._instance.sendBroadcast(StepsIntent);
                SqlHelper.instance().insertOrUpdate(dailyInfoList);
            }
        } else if (dsr.datetype.equals("周")) {
            List<BaseInfo> tmpList = dsr.getStep();
            // TODO: 16/6/12 这里循环有问题. 应该里面嵌套
            if (tmpList != null && tmpList.size() > 0) {
                for (int i = 0; i < weekInfoList.size(); i++) {  //
                    WeekInfo df = weekInfoList.get(i);
                    for (BaseInfo tmp : tmpList) {
                        System.out.println("weekOfYear: " + tmp.getWeekOfYear());
                        if (df.getWeekOfYear() == tmp.getWeekOfYear()) {
                            df.setDistance(tmp.getDistance());
                            df.setStep(tmp.getStep());
                            df.setCalories(tmp.getCalories());
                        }
                    }
                }
                runManager.setWeekInfoList(weekInfoList);
            }
        } else if (dsr.datetype.equals("年"))
        {
            List<BaseInfo> tmpList = dsr.getStep();
            // TODO: 16/6/12 这里循环有问题. 应该里面嵌套
            if (tmpList != null && tmpList.size() > 0) {
                for (int i = 0; i < yearInfoList.size(); i++) {  //
                    DailyInfo df = yearInfoList.get(i);
                    for (BaseInfo tmp : tmpList) {
                        if (df.getDates().contains(tmp.getDates())) {
                            df.setDistance(tmp.getDistance());
                            df.setStep(tmp.getStep());
                            df.setCalories(tmp.getCalories());
                            //   df.setDates(tmp.getDates());
                        }
                    }
                }
                runManager.setYearInfoList(yearInfoList);
            }
        }

    }

    @Override
    public void noticeNet(boolean netstate) {
        //  Toast.makeText(getContext(),"网络状态。。。"+netstate,Toast.LENGTH_SHORT).show();
    }

    private void updateStepView() {
        currentDailyPosition = runManager.getCurrentDailyPosition();
        refreshStepByClicked(dailyInfoList.get(currentDailyPosition).getStep());
        refreshCaloriesByClicked(dailyInfoList.get(currentDailyPosition).getCalories());
        refreshDistanceByClicked(dailyInfoList.get(currentDailyPosition).getDistance());
    }

    private void prevBeforeWeek() {
        EventBus.getDefault().post(new WeekChangeNotify(0));
        //   runManager.setCurrentWeekPosition(0);
    }

    private void turnAfterWeek() {
        EventBus.getDefault().post(new WeekChangeNotify(1));
        //    runManager.setCurrentWeekPosition(1);
    }


    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    private MyLocationData locData;
    private PopupWindow popupWindow;

    private void showCatePopWindowMenu(View view, Activity context) {
        if (popupWindow == null) {
            View layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.map_layout, null);
            popupWindow = new PopupWindow(context);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);//设置popwindow中的控件可以获得焦点
            popupWindow.setContentView(layout);

            mMapView = (TextureMapView) layout.findViewById(R.id.bd_map);
            mMapView.showZoomControls(false);
            mBaiduMap = mMapView.getMap();
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            //开启定位图层
            mBaiduMap.setMyLocationEnabled(true);

        }

        mMapView.onResume();
        mLocClient = new LocationClient(MyApplication.instance());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 高精度定位模式
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(10000);//10秒赞定位一次
        mLocClient.setLocOption(option);
        mLocClient.start();
        mLocClient.requestLocation();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mMapView.onPause();
                mLocClient.unRegisterLocationListener(myListener);
                mLocClient.stop();
                mLocClient = null;
                popupWindow = null;
            }
        });

        popupWindow.showAsDropDown(view, 0, 0);

    }


    @Override
    public void onClick(View v) {
        txtDay.setTextColor(getResources().getColor(R.color.font_defaults));
        txtWeek.setTextColor(getResources().getColor(R.color.font_defaults));
        txtYear.setTextColor(getResources().getColor(R.color.font_defaults));


        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.im_map:
                showCatePopWindowMenu(topPanel, getActivity());
                break;
            case R.id.oneKeyshare:
                OnekeyShare oks = new OnekeyShare();
                // 关闭sso授权
                oks.disableSSOWhenAuthorize();
                oks.setTitle("选择分享");
                oks.setTitleUrl("http://www.baidu.com");
                oks.setText("我是Hard");
                oks.setUrl("http://www.baidu.com");
                oks.setComment("我是测试评论文本");
                oks.setSite(getString(R.string.app_name));
                oks.setSiteUrl("http://www.baidu.com");
                // 启动分享GUI
                oks.show(getActivity());
                break;
            case R.id.type_day:
                if (dayModeFragment == null) {
                    dayModeFragment = new DayModeFragment();
                }
                dateType = DateType.DATE_TYPE;
                txtDay.setTextColor(getResources().getColor(R.color.red_background_notselected));
                transaction.replace(R.id.contain, dayModeFragment);
                transaction.addToBackStack("dayModeFragment");
                MobclickAgent.onEvent(getContext(),"home_day");
                break;
            case R.id.type_week:
                if (weekModeFragment == null) {
                    weekModeFragment = new WeekModeFragment();
                }
                txtWeek.setTextColor(getResources().getColor(R.color.red_background_notselected));
                dateType = DateType.MONTH_TYPE;
                transaction.replace(R.id.contain, weekModeFragment);
                transaction.addToBackStack("weekModeFragment");
                MobclickAgent.onEvent(getContext(),"home_week");
                break;
            case R.id.type_year:
                if (yearModeFragment == null) {
                    yearModeFragment = new YearModeFragment();
                }
                txtYear.setTextColor(getResources().getColor(R.color.red_background_notselected));
                dateType = DateType.YEAR_TYPE;
                transaction.replace(R.id.contain, yearModeFragment);
                transaction.addToBackStack("yearModeFragment");
                MobclickAgent.onEvent(getContext(),"home_month");
                break;

        }

        transaction.commit();
    }

    //DatePickerDialog dpd;
    @Override
    public void onResume() {
        super.onResume();
        //DatePickerDialog dpd = (DatePickerDialog) getActivity().getFragmentManager().findFragmentByTag("Datepickerdialog");
        //if (dpd != null) dpd.setOnDateSetListener(this);
        MobclickAgent.onPageStart("HomeNewPagerFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }


    @Override
    public void onPause() {
        super.onPause();
        int step = dailyInfoList.get(specialPosition).getStep();
        int cal = dailyInfoList.get(specialPosition).getCalories();
        float dis = dailyInfoList.get(specialPosition).getDistance();
        mPreferenceSetting.setSteps(step);
        mPreferenceSetting.setDistance(dis);
        mPreferenceSetting.setCalories(cal);
        mPreferenceSetting.saveTimestamp();
        // runManager.saveDailyInfolistToSp(getActivity(), dailyInfoList);
        SqlHelper.instance().insertOrUpdate(dailyInfoList);
        runManager.saveWeekInfolistToSp(getActivity(), weekInfoList);
        runManager.saveYearInfolistToSp(getActivity(), yearInfoList);

        MobclickAgent.onPageEnd("HomeNewPagerFragment");
    }


    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

//    @Subscribe
//    public void onEventMainThread(StepChangeNotify.StepChanged stepChanged) {
//        int p = stepChanged.step;
//    }

    int stepIndex = 0;

    private void refreshStepByClicked(int value) {
        if (currentDailyPosition == specialPosition) {
            EventBus.getDefault().post(new StepChangeNotify.NoticeStepToDailyFragment(value));
        }
        if (dateType == DateType.DATE_TYPE) {
            todayStepSL.setLabel("今日步数");
        } else if (dateType == DateType.MONTH_TYPE) {
            todayStepSL.setLabel("本周步数");
        } else if (dateType == DateType.YEAR_TYPE) {
            todayStepSL.setLabel("本月步数");
        }
        todayStepSL.setLabelValue(String.valueOf(value) + "步");
    }

    private void refreshCaloriesByClicked(int value) {
        todayCalSL.setLabelValue(String.valueOf(value) + "kCal");
    }

    private void refreshDistanceByClicked(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足
        todayDistanceSL.setLabelValue(String.valueOf(decimalFormat.format(value)) + "km");
    }


    /**
     * callback
     * 从服务中刷新今天的步数
     *
     * @param value
     */


    public void refreshSteps(int value) {
        if (currentDailyPosition == specialPosition && dateType == DateType.DATE_TYPE) {
            todayStepSL.setLabelValue(String.valueOf(value) + "步");
        }
        //    EventBus.getDefault().post(new StepChangeNotify.NoticeStepToDailyFragment(value));
        try {
            dailyInfoList.get(specialPosition).setStep(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (curSwipePosition == specialPosition && dateType == DateType.DATE_TYPE) {
            mDayStepView.showData(dailyInfoList.get(specialPosition), specialPosition);
        }

    }

    /**
     * callback
     * 从服务中刷新今天的kCal
     *
     * @param value
     */
    public void refreshCalories(int value) {
        if (currentDailyPosition == specialPosition && dateType == DateType.DATE_TYPE) {
            todayCalSL.setLabelValue(String.valueOf(value) + "kCal");
        }
        dailyInfoList.get(specialPosition).setCalories(value);

    }


    /**
     * callback
     * 从服务中得到步数距离
     *
     * @param value
     */
    public void refreshDistance(float value) {
        if (currentDailyPosition == specialPosition && dateType == DateType.DATE_TYPE) {
            DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            todayDistanceSL.setLabelValue(decimalFormat.format(value) + "km");
        }
        dailyInfoList.get(specialPosition).setDistance(value);

    }


    /**
     * 地图监听
     */
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mMapView == null)
                return;
            //	System.out.println("location: " + location.getAddrStr());
            locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 19);    //设置地图中心点以及缩放级别
            mBaiduMap.animateMapStatus(u, 2000);

        }
    };


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onLeftSwipe() {
        MobclickAgent.onEvent(getContext(),"home_left");
        if (curSwipePosition == dailyInfoList.size() - 1) {
            return;
        }
        curSwipePosition++;
        if (currentDailyPosition == 7 && currentDailyPosition > curSwipePosition) { //开始往前翻页
            prevBeforeWeek();
        } else if (currentDailyPosition == 6 && currentDailyPosition < curSwipePosition) //往后翻页
        {
            turnAfterWeek();
        }
        currentDailyPosition = curSwipePosition;
        runManager.setCurrentDailyPosition(currentDailyPosition);

        mDayStepView.showData(dailyInfoList.get(currentDailyPosition), currentDailyPosition);
        if (dateType != DateType.DATE_TYPE) {
            return;
        }
        EventBus.getDefault().post(new DailyChangNotify(currentDailyPosition));
        updateStepView();
    }

    @Override
    public void onRightSwipe() {
        MobclickAgent.onEvent(getContext(),"home_right");
        if (curSwipePosition == 0) {
            return;
        }
        curSwipePosition--;
        if (currentDailyPosition == 7 && currentDailyPosition > curSwipePosition) { //开始往前翻页
            prevBeforeWeek();
        } else if (currentDailyPosition == 6 && currentDailyPosition < curSwipePosition) //往后翻页
        {

            turnAfterWeek();
        }
        currentDailyPosition = curSwipePosition;
        runManager.setCurrentDailyPosition(currentDailyPosition);
        mDayStepView.showData(dailyInfoList.get(currentDailyPosition), currentDailyPosition);
        if (dateType != DateType.DATE_TYPE) {
            return;
        }
        EventBus.getDefault().post(new DailyChangNotify(currentDailyPosition));
        updateStepView();

    }
}

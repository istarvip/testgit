package com.walnutin.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.manager.RunManager;
import com.walnutin.entity.DailyInfo;
import com.walnutin.activity.MyApplication;
import com.walnutin.hard.R;

/**
 * Created by caro on 16/6/7.
 */
public class DayStepView extends RelativeLayout {
    private ViewPager pager;
    private Context context;
    private View mRootView;
    private TextView dateTV;//日期
    private ProgressCircle progressCircle;//进度条
    private TextView stepTV;//当天步数
    private TextView highStepTV;//本周最高步数
    private ImageView left,right;

    private int mTouchSlop, mDownX, mDownY, mTempX, totalMoveX, viewWidth;
    private boolean isSilding;
    private onSwipeGestureListener swipeListener;

    public DayStepView(Context context) {
        super(context);
    }

    public DayStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mRootView = View.inflate(context, R.layout.view_daystep, this);
        this.context = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        totalMoveX = 0;
        initView();
    }

    public void setPager(ViewPager pager) {
        this.pager = pager;
    }


    public DayStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewWidth = this.getWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("touchdebug", "onInterceptTouchEvent ACTION_DOWN");
                mDownX = mTempX = (int) ev.getRawX();
                mDownY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("touchdebug", "onInterceptTouchEvent ACTION_MOVE");
                int moveX = (int) ev.getRawX();
                // 满足此条件屏蔽SildingFinishLayout里面子类的touch事件
                if (Math.abs(moveX - mDownX) > mTouchSlop && Math.abs((int) ev.getRawY() - mDownY) < mTouchSlop) {
                    return true;
                }
                break;

        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //Log.i("touchdebug", "onTouchEvent ACTION_MOVE");
                pager.requestDisallowInterceptTouchEvent(true);
                int moveX = (int) event.getRawX();
                int deltaX = mTempX - moveX;
                mTempX = moveX;
                if (Math.abs(moveX - mDownX) > mTouchSlop
                        && Math.abs((int) event.getRawY() - mDownY) < mTouchSlop) {
                    isSilding = true;
                }

//                if (Math.abs(moveX - mDownX) >= 0 && isSilding) {
//                    totalMoveX += deltaX;
//                }
                totalMoveX += deltaX;
                break;
            case MotionEvent.ACTION_UP:
                pager.requestDisallowInterceptTouchEvent(true);
                isSilding = false;
                Log.i("touchdebug", "TotoalMoveX:" + totalMoveX + "viewVidth:" + viewWidth);
                if (Math.abs(totalMoveX) >= viewWidth / 10)
                {
                    if (totalMoveX > 0) {
                        swipeListener.onLeftSwipe();
                        Log.i("touchdebug", "onTouchEvent onLeftSwipe");
                    } else {
                        swipeListener.onRightSwipe();
                        Log.i("touchdebug", "onTouchEvent onRightSwipe");
                    }
                }
                totalMoveX = 0;
                break;

            case MotionEvent.ACTION_CANCEL:
                pager.requestDisallowInterceptTouchEvent(true);
                break;
        }

        return true;
    }


    public interface onSwipeGestureListener {
        public void onLeftSwipe();

        public void onRightSwipe();
    }

    public void setSwipeGestureListener(onSwipeGestureListener listener) {
        this.swipeListener = listener;
    }

    private void initView() {
        dateTV = (TextView) mRootView.findViewById(R.id.daily_date);
        progressCircle = (ProgressCircle) mRootView.findViewById(R.id.homepage_RingProgressView);
        stepTV = (TextView) mRootView.findViewById(R.id.homepage_currentsteps);
        highStepTV = (TextView) mRootView.findViewById(R.id.homepage_gloalsteps);
        left = (ImageView)mRootView.findViewById(R.id.arrow_left);
        right = (ImageView)mRootView.findViewById(R.id.arrow_right);

        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeListener.onRightSwipe();
            }
        });

        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeListener.onLeftSwipe();
            }
        });

    }

    /**
     * 设置当天步数
     *
     * @param dateStep
     * @return
     */
    public DayStepView setDaylyStep(String dateStep) {
        if (dateStep != null) {
            stepTV.setText(dateStep);
        }
        return this;
    }

    /**
     * 设置当天日期
     *
     * @param date
     * @return
     */
    public DayStepView setDate(String date) {
        if (date != null) {
            dateTV.setText(date);
        }
        return this;
    }

    /**
     * 设置本周最高步数
     *
     * @param date
     * @return
     */
    public DayStepView setWeeklyHighStep(String date) {
        if (date != null) {
            highStepTV.setText(date);
        }
        return this;
    }


    /**
     * 设置进度条
     *
     * @param progress
     * @return
     */
    public DayStepView setAnimProgress(int progress) {
        progressCircle.setAnimProgress(progress);
        return this;
    }


    public DayStepView setTodayAniProgress(int progress) {
        progressCircle.setTodayAniProgress(progress);
        return this;
    }


    public void showData(DailyInfo dailyInfo, int position) {
        setDate(dailyInfo.getDates());
        setDaylyStep(String.valueOf(dailyInfo.getStep()));
        setWeeklyHighStep(String.valueOf(RunManager.getInstance(context).getWeekMaxStep()));
        right.setVisibility(VISIBLE);
        left.setVisibility(VISIBLE);
        if(position == 0){
            left.setVisibility(GONE);
        }
        if (position == RunManager.getInstance(MyApplication.getContext()).getTodayPosition() ){
            setTodayAniProgress((dailyInfo.getStep() * 360) / 10000);
            right.setVisibility(GONE);
        }else {
            setAnimProgress((dailyInfo.getStep() * 360) / 10000);
        }
        //setTodayAniProgress((dailyInfo.getStep() * 360) / 10000);
    }

}

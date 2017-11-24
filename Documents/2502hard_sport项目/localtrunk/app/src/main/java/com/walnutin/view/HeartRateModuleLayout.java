package com.walnutin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walnutin.activity.MyApplication;
import com.walnutin.eventbus.StepChangeNotify;
import com.walnutin.hard.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */


public class HeartRateModuleLayout extends RelativeLayout implements View.OnClickListener {

    Context mContext;
    private View mRootView;
    HeartRateStraightLineNew heartRateStraightLine;
    TextView refRate;
    TextView realHeartRate;
    TextView lowValue;
    TextView NormalValue;
    TextView highValue;
    TextView txtMeasureRate;
    ImageView beforRate;
    ImageView nextRate;
    BarHeartRateStraightLine barHeartRateStraightLine;
    List<Integer> recentRateList;

    onItemClick onItemClick;


    public interface onItemClick {
        void transfCredits(int healthValue);

        void nextRate();

        void beforeRate();
    }

    public HeartRateModuleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_rate, this);
        initView();
    }

    public HeartRateModuleLayout(Context context) {
        super(context);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_rate, this);
        initView();
    }

    private void initView() {
        heartRateStraightLine = (HeartRateStraightLineNew) mRootView.findViewById(R.id.heartStraight);
        barHeartRateStraightLine = (BarHeartRateStraightLine) mRootView.findViewById(R.id.barheartStraight);
        refRate = (TextView) mRootView.findViewById(R.id.refRate);
        realHeartRate = (TextView) mRootView.findViewById(R.id.realHeartRate);
        lowValue = (TextView) mRootView.findViewById(R.id.lowValue); // ����ֵ
        NormalValue = (TextView) mRootView.findViewById(R.id.NormalValue); // ����ֵ
        highValue = (TextView) mRootView.findViewById(R.id.highValue); // ����ֵ
        beforRate = (ImageView) mRootView.findViewById(R.id.beforRate); // ����ֵ
        nextRate = (ImageView) mRootView.findViewById(R.id.nextRate); // ����ֵ

        txtMeasureRate = (TextView) mRootView.findViewById(R.id.startMeasure); // ����ֵ
        txtMeasureRate.setOnClickListener(this);
        beforRate.setOnClickListener(this);
        nextRate.setOnClickListener(this);
    }

    boolean isMeasuring = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startMeasure:
                if (MyApplication.isDevConnected) {
                    isMeasuring = !isMeasuring;
                    updateMeasureUI();
                } else {
                    Toast.makeText(mContext, "设备未连接...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.beforRate:
                if (onItemClick != null) {
                    onItemClick.beforeRate();
                }
                break;
            case R.id.nextRate:
                if (onItemClick != null) {
                    onItemClick.nextRate();
                }
                break;
        }

    }

    private void updateMeasureUI() {
        heartRateStraightLine.setMeasuringStatue(isMeasuring);
        if (isMeasuring == false) {
            txtMeasureRate.setText("开始测量");
            EventBus.getDefault().post(new StepChangeNotify.HeartMeasure(false));
        } else {
            txtMeasureRate.setText("停止测量");
            EventBus.getDefault().post(new StepChangeNotify.HeartMeasure(true));
        }
    }

    public void setOnItemClick(onItemClick onItemClick1) {
        onItemClick = onItemClick1;
    }

    public void setLowValue(int lowValueNum) {  // 偏低的次数
        lowValue.setText(String.valueOf(lowValueNum));
    }

    public void setNormalValue(int normalValueNum) {    // 正常的次数
        NormalValue.setText(String.valueOf(normalValueNum));
    }

    public void setHighValue(int highValueNum) {         // 偏高的次数
        highValue.setText(String.valueOf(highValueNum));
    }

    public void setHeartRate(int lowHeartRate, int centerHeart1, int HighHeartRate) {
        //  heartRateStraightLine.setHeartRate(lowHeartRate, centerHeart1, HighHeartRate);
    }

    public void setHighHeartRate(int HighHeartRate) {
        heartRateStraightLine.setHighHeartRate(HighHeartRate);
    }

    public void setCenterHeart(int centerHeart1) {  // 设置最近的值
        //   heartRateStraightLine.setCenterHeart(centerHeart1);
        barHeartRateStraightLine.setCenterHeart(centerHeart1);
    }

    public void setLowHeartRate(int lowHeartRate) {
        heartRateStraightLine.setLowHeartRate(lowHeartRate);
    }

    public void setRecentRateList(List list) {
        if (list.size() > 0) {
            realHeartRate.setText("心率" + list.get(0) + "次/分");
            recentRateList = list;
            heartRateStraightLine.setRecentRateList(recentRateList);
        }
    }

    public void setLeftBarRateList(List list) {
        if (list != null && list.size() > 0) {
            barHeartRateStraightLine.setRecentRateList(list);
        }
    }

    public void setMeasuring(boolean status) {
        isMeasuring = status;
        heartRateStraightLine.setMeasuringStatue(isMeasuring);
        if (isMeasuring == false) {
            txtMeasureRate.setText("开始测量");
        } else {
            txtMeasureRate.setText("停止测量");
        }
    }

    public boolean getMeasuringState() {
        return isMeasuring;
    }

    public TextView getRefRate() {
        return refRate;
    }

    public TextView getLowValue() {
        return lowValue;
    }

    public TextView getNormalValue() {
        return NormalValue;
    }

    public TextView getHighValue() {
        return highValue;
    }
}

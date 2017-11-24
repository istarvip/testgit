package com.walnutin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.hard.R;

import java.util.Date;

/**
 * Created by Administrator on 2016/7/28.
 */


public class HealthModuleLayout extends RelativeLayout {

    Context mContext;
    private View mRootView;
    HealthProgressView healthProgressView;
    TextView healthValue;
    TextView todayTime;
    TextView credits;
    TextView transfCredits;
    onItemClick onItemClick;

    private interface onItemClick {
        void transfCredits(int healthValue);
    }

    public HealthModuleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_main, this);
        initView();
    }

    public HealthModuleLayout(Context context) {
        super(context);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_main, this);

        initView();
    }

    private void initView() {
        healthProgressView = (HealthProgressView) mRootView.findViewById(R.id.healthProgress);
        healthValue = (TextView) mRootView.findViewById(R.id.health_value);
        todayTime = (TextView) mRootView.findViewById(R.id.toDay);
        credits = (TextView) mRootView.findViewById(R.id.credits); // ����ֵ
        transfCredits = (TextView) mRootView.findViewById(R.id.transfCredits); // ת�����ְ�ť

        transfCredits.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClick != null) {
                    onItemClick.transfCredits(Integer.valueOf(healthValue.getText().toString()));
                }
            }
        });
        //   todayTime.setText(new Date().getDay());
    }

    public void setHealthValue(int value) {
        healthValue.setText(String.valueOf(value));
    }

    public void setCreditsValue(int creditsValue) {
        credits.setText(String.valueOf(creditsValue));
    }

    public void setOnItemClick(onItemClick onItemClick1) {
        onItemClick = onItemClick1;
    }
}

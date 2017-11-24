package com.walnutin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.hard.R;

/**
 * Created by Administrator on 2016/7/28.
 */


public class StepModuleLayout extends RelativeLayout {

    Context mContext;
    private View mRootView;
    StepStraightLine stepProgressView;
    TextView goalstep;
    TextView realStep;
    TextView realCalo;
    TextView realDistance;
    onItemClick onItemClick;

    private interface onItemClick {
        void transfCredits(int healthValue);
    }

    public StepModuleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_step, this);
        initView();
    }

    public StepModuleLayout(Context context) {
        super(context);
        mContext = context;
        mRootView = View.inflate(context, R.layout.module_step, this);

        initView();
    }

    private void initView() {
        stepProgressView = (StepStraightLine) mRootView.findViewById(R.id.stepBar);
        goalstep = (TextView) mRootView.findViewById(R.id.goalstep);
        realStep = (TextView) mRootView.findViewById(R.id.realStep);
        realCalo = (TextView) mRootView.findViewById(R.id.realCalo); // ����ֵ
        realDistance = (TextView) mRootView.findViewById(R.id.realDistance); // ����ֵ
    }

    public void setOnItemClick(onItemClick onItemClick1) {
        onItemClick = onItemClick1;
    }

    public void setGoalStepValue(int goalStepValue) {
        stepProgressView.setMAXVALUE(goalStepValue);  //
        goalstep.setText(String.valueOf(goalStepValue));
    }

    public void setCurrentStep(int currentStep) {
        stepProgressView.setAniStep(currentStep);  // 设置当前 步数
        realStep.setText(String.valueOf(currentStep));
    }
    public void setCurrentCalo(int currentCalo){
        realCalo.setText(String.valueOf(currentCalo));
    }
    public void setCurrentDistance(String distance){
        realDistance.setText(distance);
    }
}

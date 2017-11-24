package com.walnutin.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.walnutin.hard.R;
import com.walnutin.view.RotateView;

/**
 * Created by assa on 2016/5/24.
 */
public class MyGoalActivity extends FragmentActivity implements View.OnClickListener{
    private TextView goal_return;
    private Button goal_confirm;
    private RotateView rotateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygoal);
        initView();
    }

    private void initView() {
        goal_return= (TextView) findViewById(R.id.goal_return);
        goal_return.setOnClickListener(this);
        goal_confirm= (Button) findViewById(R.id.goal_confirm);
        goal_confirm.setOnClickListener(this);
        rotateView = (RotateView) findViewById(R.id.rotate_ba);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goal_return:
                finish();
                break;
            case R.id.goal_confirm:
                finish();
                break;
        }
    }
}
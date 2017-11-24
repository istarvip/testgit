package com.walnutin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.walnutin.hard.R;
import com.walnutin.util.MySharedPf;

public class PhoneSettingActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout setting_data;
    private RelativeLayout setting_goal;
    private TextView setting_return;
    private TextView setting_bind;
    private Button btnLogout;
    MySharedPf preferenceSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonesetting);
        preferenceSettings = MySharedPf.getInstance(this);
        initView();
    }

    private void initView() {
        setting_data = (RelativeLayout) findViewById(R.id.setting_data);
        setting_data.setOnClickListener(this);
        setting_goal = (RelativeLayout) findViewById(R.id.setting_goal);
        setting_goal.setOnClickListener(this);
        setting_bind = (TextView) findViewById(R.id.setting_bind);
        setting_bind.setOnClickListener(this);
      //  setting_return = (TextView) findViewById(R.id.phone_setting_renturn);
      //  setting_return.setOnClickListener(this);
        btnLogout = (Button) findViewById(R.id.logout);
        btnLogout.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);       //统计时长
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_data:
                Intent intent = new Intent(PhoneSettingActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_goal:
                Intent intent_goal = new Intent(PhoneSettingActivity.this, MyGoalActivity.class);
                startActivity(intent_goal);
                break;
//            case R.id.phone_setting_renturn:
//                finish();
//                break;
            case R.id.logout:
                preferenceSettings.setToken(null);
                preferenceSettings.setString("password", null);
                Intent loginIntent = new Intent(PhoneSettingActivity.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.setting_bind:
                Intent bindIntent = new Intent(PhoneSettingActivity.this, BindThridActivity.class);
                startActivity(bindIntent);
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
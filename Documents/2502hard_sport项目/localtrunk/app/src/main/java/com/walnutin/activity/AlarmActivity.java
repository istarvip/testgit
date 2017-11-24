package com.walnutin.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.walnutin.adapter.AppListAdapter;
import com.walnutin.entity.NoticeDevice;
import com.walnutin.hard.R;
import com.walnutin.manager.AlarmManager;
import com.walnutin.manager.NoticeInfoManager;
import com.walnutin.util.Config;
import com.walnutin.util.Conversion;
import com.walnutin.util.LoadDataDialog;
import com.walnutin.util.WeekUtils;
import com.walnutin.view.LineItemView;
import com.walnutin.view.RelativeAlarmView;
import com.walnutin.view.SelectRemindCyclePopup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by assa on 2016/5/24.
 */
public class AlarmActivity extends BaseActivity {

    RelativeAlarmView rlFirst;
    RelativeAlarmView rlSecond;
    RelativeAlarmView rlThird;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmlist);
        alarmManager = AlarmManager.getInstance(getApplicationContext());
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        alarmManager.getLocalAlarmInfo();

        rlFirst.setAlarmState(alarmManager.isEnableFirstAlarm());
        if (alarmManager.getFirstAlarm() != null && !alarmManager.getFirstAlarm().equals("")) {
            rlFirst.setAlartTime(alarmManager.getFirstAlarm());
        }

        if (alarmManager.getFormatFirstAlarm() != null && !alarmManager.getFormatFirstAlarm().equals("")) {
            rlFirst.setAlartContent(alarmManager.getFormatFirstAlarm());
        }

        rlSecond.setAlarmState(alarmManager.isEnableSecondAlarm());
        if (alarmManager.getSecondAlarm() != null && !alarmManager.getSecondAlarm().equals("")) {
            rlSecond.setAlartTime(alarmManager.getSecondAlarm());
        }
        if (alarmManager.getFormatSecondAlarm() != null && !alarmManager.getFormatSecondAlarm().equals("")) {
            rlSecond.setAlartContent(alarmManager.getFormatSecondAlarm());
        }

        rlThird.setAlarmState(alarmManager.isEnableThirdAlarm());
        if (alarmManager.getThirdAlarm() != null && !alarmManager.getThirdAlarm().equals("")) {

            rlThird.setAlartTime(alarmManager.getThirdAlarm());
        }
        if (alarmManager.getFormatThirdAlarm() != null && !alarmManager.getFormatThirdAlarm().equals("")) {
            rlThird.setAlartContent(alarmManager.getFormatThirdAlarm());
        }
    }

    private void initView() {
        rlFirst = (RelativeAlarmView) findViewById(R.id.rlFirstAlarm);
        rlSecond = (RelativeAlarmView) findViewById(R.id.rlSecondAlarm);
        rlThird = (RelativeAlarmView) findViewById(R.id.rlThirdtAlarm);
    }

    static final int REQUEST_OK = 1;

    private void initEvent() {
        rlFirst.setOnItemClick(new RelativeAlarmView.OnItemClick() {
            @Override
            public void itemClick() {
                Intent intent = new Intent(AlarmActivity.this, AlarmSettingActivity.class);
                intent.putExtra("flag", 1);
                intent.putExtra("time", rlFirst.getTxtAlartTime().getText().toString());
                intent.putExtra("week", rlFirst.getTxtAlarmContent().getText().toString());
                intent.putExtra("repeat", alarmManager.getFirstRepeat());
                startActivityForResult(intent, 1);
            }

            @Override
            public void alarmSwitchClick(boolean state) {
                alarmManager.setEnableFirstAlarm(state);
                packageSendInfo(rlFirst.getTxtAlartTime().getText().toString(),
                        rlFirst.getTxtAlarmContent().getText().toString(), 1,
                        alarmManager.getFirstRepeat(), state);
            }
        });

        rlSecond.setOnItemClick(new RelativeAlarmView.OnItemClick() {
            @Override
            public void itemClick() {
                Intent intent = new Intent(AlarmActivity.this, AlarmSettingActivity.class);
                intent.putExtra("flag", 2);
                intent.putExtra("time", rlSecond.getTxtAlartTime().getText().toString());
                intent.putExtra("week", rlSecond.getTxtAlarmContent().getText().toString());
                intent.putExtra("repeat", alarmManager.getSecondtRepeat());

                startActivityForResult(intent, 2);
            }

            @Override
            public void alarmSwitchClick(boolean state) {
                alarmManager.setEnableFirstAlarm(state);
                packageSendInfo(rlSecond.getTxtAlartTime().getText().toString(),
                        rlSecond.getTxtAlarmContent().getText().toString(), 2,
                        alarmManager.getSecondtRepeat(), state);
            }
        });

        rlThird.setOnItemClick(new RelativeAlarmView.OnItemClick() {
            @Override
            public void itemClick() {
                Intent intent = new Intent(AlarmActivity.this, AlarmSettingActivity.class);
                intent.putExtra("flag", 3);
                intent.putExtra("time", rlThird.getTxtAlartTime().getText().toString());
                intent.putExtra("week", rlThird.getTxtAlarmContent().getText().toString());
                intent.putExtra("repeat", alarmManager.getThirdRepeat());
                startActivityForResult(intent, 3);
            }

            @Override
            public void alarmSwitchClick(boolean state) {
                alarmManager.setEnableFirstAlarm(state);
                packageSendInfo(rlThird.getTxtAlartTime().getText().toString(),
                        rlThird.getTxtAlarmContent().getText().toString(), 3,
                        alarmManager.getThirdRepeat(), state);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && requestCode == 1) {  // 第一个闹钟
            rlFirst.setAlarmState(true);
            rlFirst.setAlartTime(alarmManager.getFirstAlarm());
            rlFirst.setAlartContent(alarmManager.getFormatFirstAlarm());
        } else if (requestCode == 2 && resultCode == 1) {  // 第2个闹钟
            rlSecond.setAlarmState(true);
            rlSecond.setAlartTime(alarmManager.getSecondAlarm());
            rlSecond.setAlartContent(alarmManager.getFormatSecondAlarm());
        } else if (requestCode == 3 && resultCode == 1) {  // 第3个闹钟
            rlThird.setAlarmState(true);
            rlThird.setAlartTime(alarmManager.getThirdAlarm());
            rlThird.setAlartContent(alarmManager.getFormatThirdAlarm());
        }

    }

    void packageSendInfo(String time, String repeatValue, int flag, int cycle, boolean isOpen) {
        if (time != null && time.length() > 0 && repeatValue != null && repeatValue.length() >= 2) {
            String[] times = time.split(":");
            int hour = Integer.parseInt(times[0]);
            int minitue = Integer.parseInt(times[1]);
            byte weekPeroid = 0;
            if (cycle == 0) {  // 每天的闹钟
                weekPeroid = Config.EVERYDAY;
            } else {
                weekPeroid = WeekUtils.getWeekByteByReapeat(cycle); // 得到星期的信息
            }
            if (flag == 1) {
                alarmManager.setEnableFirstAlarm(isOpen);
            } else if (flag == 2) {
                alarmManager.setEnableThirdAlarm(isOpen);
            } else if (flag == 3) {
                alarmManager.setEnableThirdAlarm(isOpen);
            }

            sendClockState(flag, weekPeroid, hour, minitue, isOpen);  //发送设置闹钟广播
        }
    }

    private void sendClockState(int flag, byte week, int hour, int minitue, boolean isOpen) {
        Intent intent = new Intent(Config.CLOCK_SETTING);
        intent.putExtra("flag", flag);
        intent.putExtra("week", week);
        intent.putExtra("hour", hour);
        intent.putExtra("minitue", minitue);
        intent.putExtra("isOpen", isOpen);
        sendBroadcast(intent);

    }
}

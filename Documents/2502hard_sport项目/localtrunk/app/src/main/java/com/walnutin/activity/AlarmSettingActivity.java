package com.walnutin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.walnutin.hard.R;
import com.walnutin.manager.AlarmManager;
import com.walnutin.util.Config;
import com.walnutin.util.WeekUtils;
import com.walnutin.view.RelativeAlarmView;
import com.walnutin.view.SelectRemindCyclePopup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by assa on 2016/5/24.
 */
public class AlarmSettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView date_tv;
    private TimePickerView pvTime;
    private RelativeLayout repeat_rl;
    private TextView tv_repeat_value;
    private RelativeLayout allLayout;
    private Button set_btn;
    private String time;
    private int cycle;
    int flag = -1;
    String week;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsetting);
        flag = getIntent().getIntExtra("flag", -1);
        time = getIntent().getStringExtra("time");
        week = getIntent().getStringExtra("week");
        cycle = getIntent().getIntExtra("repeat", 0);

        alarmManager = AlarmManager.getInstance(getApplicationContext());

        initView();
        initEvent();

    }

    private void initEvent() {
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                time = getTime(date);
                date_tv.setText(time);
            }
        });

        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show();
            }
        });

    }

    private void initView() {
        allLayout = (RelativeLayout) findViewById(R.id.allLayout);
        set_btn = (Button) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(this);
        date_tv = (TextView) findViewById(R.id.date_tv);
        repeat_rl = (RelativeLayout) findViewById(R.id.repeat_rl);
        repeat_rl.setOnClickListener(this);
        tv_repeat_value = (TextView) findViewById(R.id.tv_repeat_value);
        pvTime = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));


        pvTime.setTime(calendar.getTime());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        date_tv.setText(time);
        tv_repeat_value.setText(week);
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    private void setClock() {
        if (time != null && time.length() > 0 && tv_repeat_value.getText().length() >= 2) {
            String[] times = time.split(":");
            int hour = Integer.parseInt(times[0]);
            int minitue = Integer.parseInt(times[1]);
            byte weekPeroid = 0;
            if (cycle == 0) {  // 每天的闹钟
                weekPeroid = Config.EVERYDAY;
            } else {
                weekPeroid = WeekUtils.getWeekByteByReapeat(cycle); // 得到星期的信息
            }
            sendClockState(flag, weekPeroid, hour, minitue, true);  //发送设置闹钟广播
            if (flag == 1) {
                alarmManager.setFirstRepeat(cycle);
                alarmManager.setFirstAlarm(time);
                alarmManager.setFormatFirstAlarm(tv_repeat_value.getText().toString());
                alarmManager.setEnableFirstAlarm(true);
            } else if (flag == 2) {
                alarmManager.setSecondtRepeat(cycle);
                alarmManager.setSecondAlarm(time);
                alarmManager.setFormatSecondAlarm(tv_repeat_value.getText().toString());
                alarmManager.setEnableThirdAlarm(true);
            } else if (flag == 3) {
                alarmManager.setThirdRepeat(cycle);
                alarmManager.setThirdAlarm(time);
                alarmManager.setFormatThirdAlarm(tv_repeat_value.getText().toString());
                alarmManager.setEnableThirdAlarm(true);
            }

            alarmManager.saveAlarmInfo();
            Intent intent = new Intent(AlarmSettingActivity.this, AlarmActivity.class);
            AlarmSettingActivity.this.setResult(1, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "未设置完全", Toast.LENGTH_SHORT).show();
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

    public void selectRemindCycle() {
        final SelectRemindCyclePopup fp = new SelectRemindCyclePopup(this);
        fp.showPopup(allLayout);
        fp.setOnSelectRemindCyclePopupListener(new SelectRemindCyclePopup
                .SelectRemindCyclePopupOnClickListener() {

            @Override
            public void obtainMessage(int flag, String ret) {
                switch (flag) {
                    // 星期一
                    case 0:
                        break;
                    // 星期二
                    case 1:
                        break;
                    // 星期三
                    case 2:
                        break;
                    // 星期四
                    case 3:
                        break;
                    // 星期五
                    case 4:
                        break;
                    // 星期六
                    case 5:
                        break;
                    // 星期日
                    case 6:
                        break;
                    // 确定
                    case 7:
                        int repeat = Integer.valueOf(ret);
                        tv_repeat_value.setText(WeekUtils.parseRepeat(repeat, 0));
                        cycle = repeat;
                        fp.dismiss();
                        break;
                    case 8:
                        tv_repeat_value.setText("每天");
                        cycle = 0;
                        fp.dismiss();
                        break;
                    case 9:
                        fp.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:
                selectRemindCycle();
                break;
            case R.id.set_btn:
                setClock();
                break;
            default:
                break;
        }
    }


}
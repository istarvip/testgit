package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.androidquery.AQuery;
//import com.walnutin.activity.MapActivity;
import com.walnutin.hard.R;
import com.walnutin.util.Config;
import com.walnutin.view.RunningExitPopupWindow;

public class RunningGoActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    Button btnLogin;
    Chronometer chronometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.running_timer);
        aQuery = new AQuery(this);
        initview();
    }

    int isRunningState = Config.RUNNING_START;

    int miss = 0;

    void initview() {
        Toast.makeText(getApplicationContext(), "-------------", Toast.LENGTH_SHORT).show();
        aQuery.id(R.id.running_img_play).clicked(this, "startTimer");
        aQuery.id(R.id.running_img_conntinue).clicked(this, "continueTimer").visibility(View.GONE);
        aQuery.id(R.id.running_img_end).clicked(this, "endTimer").visibility(View.GONE);
        chronometer = (Chronometer) findViewById(R.id.chronhms);
        //   chronometer.setText("00:00:00");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                miss++;
                chronometer.setText(FormatMiss(miss));
            }
        });
    }

    public static String FormatMiss(int miss) {

        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;

        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;

        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;

        return hh + ":" + mm + ":" + ss;

    }

    private void noticeStateChanged() {
        if (isRunningState == Config.RUNNING_START) {
            aQuery.id(R.id.running_img_play).background(R.drawable.running_pause);
            aQuery.id(R.id.running_img_conntinue).visibility(View.GONE);
            aQuery.id(R.id.running_img_end).visibility(View.GONE);
            isRunningState = Config.RUNNING_PAUSE;
        } else if (isRunningState == Config.RUNNING_PAUSE) {
            isRunningState = Config.RUNNING_CONTINUE;
            aQuery.id(R.id.running_img_play).visibility(View.INVISIBLE);
            aQuery.id(R.id.running_img_conntinue).visibility(View.VISIBLE);
            aQuery.id(R.id.running_img_end).visibility(View.VISIBLE);
        }
    }

    public void startTimer(View view) {

//        Intent intent =new Intent(RunningGoActivity.this, MapActivity.class);
//        startActivity(intent);

        // isRunningState = !isRunningState;
//        if (isRunningState == Config.RUNNING_START) {
//            chronometer.start();
//        } else {
//            chronometer.stop();
//        }
//        noticeStateChanged();
    }

    public void continueTimer(View view) {
        if (isRunningState == Config.RUNNING_CONTINUE) {
            isRunningState = Config.RUNNING_PAUSE;
            aQuery.id(R.id.running_img_play).background(R.drawable.running_pause).visibility(View.VISIBLE);
            aQuery.id(R.id.running_img_conntinue).visibility(View.GONE);
            aQuery.id(R.id.running_img_end).visibility(View.GONE);
        }
        chronometer.start();
    }

    RunningExitPopupWindow runningExitPopupWindow;

    public void endTimer(View view) {
        chronometer.stop();
        isRunningState = Config.RUNNING_START;
        runningExitPopupWindow = new RunningExitPopupWindow(RunningGoActivity.this, itemsOnClick);
        //显示窗口
        runningExitPopupWindow.setBackgroundDrawable(null);
        runningExitPopupWindow.showAtLocation(this.findViewById(R.id.rlmain), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            runningExitPopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                    runningExitPopupWindow.dismiss();
                default:
                    break;
            }

        }

    };
}
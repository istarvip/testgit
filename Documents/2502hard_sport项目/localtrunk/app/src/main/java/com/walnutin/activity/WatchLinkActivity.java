package com.walnutin.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walnutin.Presenter.*;
import com.walnutin.entity.DeviceOtherSetting;
import com.walnutin.eventbus.StepChangeNotify;
import com.walnutin.hard.R;
import com.walnutin.manager.DeviceOtherInfoManager;
import com.walnutin.util.Config;
import com.walnutin.view.LineItemView;
import com.walnutin.view.LongSitRemindPopupWindow;
import com.walnutin.view.MyHeightPopupWindow;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by assa on 2016/5/26.
 */
public class WatchLinkActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_linking;
    private TextView sleepSync;
    private TextView stepSync;
    private TextView heartSync;
    private TextView tv_state;
    private TextView ShunScreenTime; // 亮屏时间
    private BroadcastReceiver mStatusReceiver;
    private boolean connection_status;
    private LinearLayout llbelowView;
    LineItemView pushMsg;   // 消息推送
    LineItemView alarmSetting;   // 闹钟
    LineItemView findBracelet;   // 查找手环
    LineItemView longSitMinitueSet;   //久坐设置时间
    ImageView unLost;   // 只能防丢
    ImageView longSitNotice; // 久坐提醒
    DeviceOtherInfoManager deviceOtherInfoManager;
    LongSitRemindPopupWindow myHeightPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_link);
        deviceOtherInfoManager = DeviceOtherInfoManager.getInstance(getApplicationContext());

        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_linking = (TextView) findViewById(R.id.tv_linking);
        sleepSync = (TextView) findViewById(R.id.devSleepSync);
        heartSync = (TextView) findViewById(R.id.devHeartSync);
        stepSync = (TextView) findViewById(R.id.devStepSync);
        llbelowView = (LinearLayout) findViewById(R.id.llbelowView);
        pushMsg = (LineItemView) findViewById(R.id.pushMsg);
        alarmSetting = (LineItemView) findViewById(R.id.alarm);
        findBracelet = (LineItemView) findViewById(R.id.findBracelet);
        longSitMinitueSet = (LineItemView) findViewById(R.id.longSitMinitueSet);
        unLost = (ImageView) findViewById(R.id.unLost);
        longSitNotice = (ImageView) findViewById(R.id.longSitNotice);
        initEvent();
        initReceiver();
        initData();
    }

    private void initData() {
        deviceOtherInfoManager.getLocalDeviceOtherSettingInfo();
        minitue = String.valueOf(deviceOtherInfoManager.getLongSitTime());
        isUnLost = deviceOtherInfoManager.isUnLost();
        isLongsitRemind = deviceOtherInfoManager.isLongSitRemind();
        changeLongSitRemindState();
        changeUnLostState();
    }

    private void initEvent() {
        tv_linking.setOnClickListener(this);
        sleepSync.setOnClickListener(this);
        heartSync.setOnClickListener(this);
        stepSync.setOnClickListener(this);
        unLost.setOnClickListener(this);
        longSitNotice.setOnClickListener(this);


        pushMsg.setOnItemClick(new LineItemView.OnClickItemListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(WatchLinkActivity.this, NoticePushActivity.class);
                startActivity(intent);
            }
        });

        alarmSetting.setOnItemClick(new LineItemView.OnClickItemListener() {
            @Override
            public void onClick() {
                if (MyApplication.isDevConnected == false) {
                    Toast.makeText(getApplication(), "未连接手环", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(WatchLinkActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });

        findBracelet.setOnItemClick(new LineItemView.OnClickItemListener() {
            @Override
            public void onClick() {
                if (MyApplication.isDevConnected == false) {
                    Toast.makeText(getApplication(), "未连接手环", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Config.FINDBRACELET);
                sendBroadcast(intent);

            }
        });

        longSitMinitueSet.setOnItemClick(new LineItemView.OnClickItemListener() {
            @Override
            public void onClick() {
                if (MyApplication.isDevConnected == false) {
                    Toast.makeText(getApplication(), "未连接手环", Toast.LENGTH_SHORT).show();
                    return;
                }
                myHeightPopupWindow = new LongSitRemindPopupWindow(WatchLinkActivity.this, itemsOnClick);
                myHeightPopupWindow.showAtLocation(WatchLinkActivity.this.findViewById(R.id.llayot), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLineState(MyApplication.isDevConnected);

    }

    private void updateLineState(boolean status) {
        if (status) {
            tv_state.setText("已连接");
            llbelowView.setVisibility(View.VISIBLE);
        } else {
            tv_state.setText("未连接");
            //     llbelowView.setVisibility(View.GONE);
        }
    }

    private void initReceiver() {
        IntentFilter mFilter = new IntentFilter("ConnectedDevice");
        mStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connection_status = intent.getBooleanExtra("connection_status", false);
                System.out.println("******connection_status" + connection_status);
                if (connection_status) {
                    updateLineState(true);
                } else {
                    updateLineState(false);
                }
            }
        };
        registerReceiver(mStatusReceiver, mFilter);
    }

    boolean isUnLost = false;
    boolean isLongsitRemind = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_linking:
                Intent linkIntent = new Intent(WatchLinkActivity.this, com.walnutin.Presenter.LinkDetailActivity.class);
                startActivity(linkIntent);
                break;

            case R.id.devSleepSync:
                EventBus.getDefault().post(new StepChangeNotify.SyncSleep());
                break;
            case R.id.devHeartSync:
                EventBus.getDefault().post(new StepChangeNotify.SyncHeart());
                break;
            case R.id.devStepSync:
                EventBus.getDefault().post(new StepChangeNotify.SyncStep());
                break;
            case R.id.longSitNotice:
                if (MyApplication.isDevConnected == false) {
                    Toast.makeText(getApplication(), "未连接手环", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (minitue == null || deviceOtherInfoManager.getLongSitTime() == 0) {
                    Toast.makeText(getApplicationContext(), "请先设置久坐时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                isLongsitRemind = !isLongsitRemind;
                changeLongSitRemindState();
                sendLongSitBroad();
                break;
            case R.id.unLost:
                if (MyApplication.isDevConnected == false) {
                    Toast.makeText(getApplication(), "未连接手环", Toast.LENGTH_SHORT).show();
                    return;
                }
                isUnLost = !isUnLost;
                changeUnLostState();
                break;
        }
    }

    void changeUnLostState() {
        if (isUnLost == true) {
            unLost.setBackgroundResource(R.drawable.openblue);
        } else {
            unLost.setBackgroundResource(R.drawable.closeblue);
        }
        deviceOtherInfoManager.setUnLost(isUnLost);

    }

    void changeLongSitRemindState() {
        if (isLongsitRemind == true) {
            longSitNotice.setBackgroundResource(R.drawable.openblue);
        } else {
            longSitNotice.setBackgroundResource(R.drawable.closeblue);
        }
        deviceOtherInfoManager.setLongSitRemind(isUnLost);

    }

    String minitue = null;

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.height_sure:
                    if (minitue == null) {
                        Toast.makeText(WatchLinkActivity.this, "还未设置时间", Toast.LENGTH_SHORT).show();
                    } else {
                        myHeightPopupWindow.dismiss();
                        if (minitue != null) {
                            sendLongSitBroad();
                        }
                    }

                    break;
            }

        }
    };

    private void sendLongSitBroad() {
        Intent intent = new Intent(Config.LONGSitNotice);
        intent.putExtra("time", deviceOtherInfoManager.getLongSitTime()); // 久坐时间
        intent.putExtra("isOpen",deviceOtherInfoManager.isLongSitRemind());
        sendBroadcast(intent);
    }

    public void setMinituesSetting(String minituesSetting) {
        minitue = minituesSetting;
        int minite = Integer.parseInt(minituesSetting.split("min")[0]);
        deviceOtherInfoManager.setLongSitTime(minite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStatusReceiver != null) {
            unregisterReceiver(mStatusReceiver);
        }
    }
}

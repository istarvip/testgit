package com.walnutin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.hard.R;

/**
 * Created by MrJ on 16/6/7.
 */
public class RelativeAlarmView extends RelativeLayout implements View.OnClickListener {
    private View mRootView;
    private TextView txtAlartTime;
    private TextView txtAlarmContent;
    private ImageView imgOpenAlarm;
    RelativeLayout rl;
    String alartTimeString;
    String alarmString;
    OnItemClick onItemClick;

    public interface OnItemClick {
        public void itemClick();

        public void alarmSwitchClick(boolean state);
    }


    public RelativeAlarmView(Context context) {
        super(context);
    }

    public RelativeAlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = View.inflate(context, R.layout.alarm_item, this);
        initView();

    }

    private void initView() {
        txtAlartTime = (TextView) mRootView.findViewById(R.id.alarmTime);
        txtAlarmContent = (TextView) mRootView.findViewById(R.id.alarmSettting);
        imgOpenAlarm = (ImageView) mRootView.findViewById(R.id.openAlarm);

        rl = (RelativeLayout) mRootView.findViewById(R.id.rl);
        rl.setOnClickListener(this);
        imgOpenAlarm.setOnClickListener(this);

    }




    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setAlartTime(String labelStr) {

        if (labelStr != null) {
            txtAlartTime.setText(labelStr);
        }

    }

    public void setAlartContent(String labelStr) {
        if (labelStr != null) {
            txtAlarmContent.setText(labelStr);
        }

    }

    public void setAlarmState(boolean open) {
        if (open) {
            imgOpenAlarm.setBackgroundResource(R.drawable.openblue);
        } else {
            imgOpenAlarm.setBackgroundResource(R.drawable.closeblue);

        }
        isOpenAlarm = open;
    }

    public TextView getTxtAlartTime() {
        return txtAlartTime;
    }

    public TextView getTxtAlarmContent() {
        return txtAlarmContent;
    }

    boolean isOpenAlarm = false;

    @Override
    public void onClick(View v) {
        if (onItemClick == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.rl:
                onItemClick.itemClick();
                break;
            case R.id.openAlarm:
                isOpenAlarm = !isOpenAlarm;
                setAlarmState(isOpenAlarm);
                onItemClick.alarmSwitchClick(isOpenAlarm);
                break;
        }
    }

}

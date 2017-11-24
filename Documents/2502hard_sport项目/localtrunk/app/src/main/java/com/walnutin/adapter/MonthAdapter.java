package com.walnutin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walnutin.entity.Week;
import com.walnutin.eventbus.StepChangeNotify;
import com.walnutin.hard.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MonthAdapter extends BaseAdapter {

    Context mContext;
    List<Week> monList;
    private int position = -1;
    private String[] monDayString = new String[6];
    private String[] month = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    int mon;

    public MonthAdapter(Context context, List<Week> l) {
        mContext = context;
        monList = l;
        Calendar calendar = Calendar.getInstance();  //得到当前月份
        mon = calendar.get(Calendar.MONTH);
//        for (int i = 0; i < 6; i++) {
//            monDayString[i] = month[mon];
//        }
    }

    public void setMonDayString(String[] string) {
        monDayString = string;
    }

    public void setMonth(List<Week> d) {
        monList = d;
        this.notifyDataSetChanged();
    }

    public void setPositionClicked(int positio) {
        this.position = positio;
        notifyDataSetChanged();
        EventBus.getDefault().post(new StepChangeNotify.MonthDayPostion(positio));
    }

    @Override
    public int getCount() {
        return monList.size();
    }

    @Override
    public Object getItem(int i) {
        return monList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Week week = monList.get(i);
        Hoder hoder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.week_item, null);
            hoder = new Hoder();
            hoder.dayTextView = (TextView) view.findViewById(R.id.week_num);
            hoder.weeInfo = (TextView) view.findViewById(R.id.week_sum);
            view.setTag(hoder);
        } else {
            hoder = (Hoder) view.getTag();
        }

        hoder.dayTextView.setText(String.valueOf(week.day));
        hoder.weeInfo.setText(month[mon]);

        if (position == i) {
            hoder.dayTextView.setBackgroundResource(R.drawable.textroundstyle);
            hoder.dayTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            hoder.dayTextView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            hoder.dayTextView.setTextColor(mContext.getResources().getColor(R.color.half_gray));
        }
        return view;
    }

    class Hoder {
        public TextView dayTextView;
        public TextView weeInfo;
    }
}

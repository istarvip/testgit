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

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class YearAdapter extends BaseAdapter {

    Context mContext;
    List<Week> weekList;
    private int position =-1;
    String year;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    public YearAdapter(Context context, List<Week> l) {

        mContext = context;
        weekList = l;


    }

    public void setYearList(List<Week>d){
        weekList = d;
        this.notifyDataSetChanged();
    }

    public void setPositionClicked(int positio){
        this.position = positio;
        notifyDataSetChanged();
        EventBus.getDefault().post(new StepChangeNotify.YearDayPostion(positio));
    }

    @Override
    public int getCount() {
        return weekList.size();
    }

    @Override
    public Object getItem(int i) {
        return weekList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Week week = weekList.get(i);
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

     //   System.out.println(" weekAdapter........."+week.day);
        hoder.dayTextView.setText(String.valueOf(week.day));
        hoder.weeInfo.setText(sdf.format(week.date));

        if(position ==i){
            hoder.dayTextView.setBackgroundResource(R.drawable.textroundstyle);
            hoder.dayTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
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

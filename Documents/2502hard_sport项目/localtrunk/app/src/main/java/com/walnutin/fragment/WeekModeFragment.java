package com.walnutin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.walnutin.manager.RunManager;
import com.walnutin.adapter.MonthAdapter;
import com.walnutin.entity.DateType;
import com.walnutin.entity.Week;
import com.walnutin.entity.WeekInfo;
import com.walnutin.hard.R;
import com.walnutin.util.DateUtils;
import com.walnutin.view.LineChart;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class WeekModeFragment extends Fragment implements RunManager.IMonthSourceChange {
    MonthAdapter monthAdapter;
    List<Week> weekList;
    private LineChart lineChart;
    RunManager runManager;
    GridView weekGridView;
    List<WeekInfo> weekInfoList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_slip, container, false);
        runManager = RunManager.getInstance(getContext());
        weekGridView = (GridView) view.findViewById(R.id.gv_week_slip);
        lineChart = (LineChart) view.findViewById(R.id.linechat);
        lineChart.setLineChartType(DateType.MONTH_TYPE);
        lineChart.setMAXVALUE(200000);
        lineChart.setOnItemClicked(new LineChart.OnItemClicked() {
            @Override
            public void onItem(int pos) {
                monthAdapter.setPositionClicked(pos);
                posClicked(pos);
            }
        });

        weekList = DateUtils.getMonthData(DateUtils.getMonthStart(new Date())); // 得到这个月的周数据
        monthAdapter = new MonthAdapter(getActivity(), weekList);
        weekGridView.setNumColumns(6);
        weekGridView.setAdapter(monthAdapter);
        weekGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                monthAdapter.setPositionClicked(i);
                posClicked(i);
            }
        });
        runManager.setiMonthSourceChange(this);
        weekInfoList = runManager.getWeekInfoList();
        if (weekInfoList == null) {
            weekInfoList = runManager.getMonthVirtualList();
        }
        resetLineChart(0);

        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //   EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void resetLineChart(int position) {
        if (weekInfoList == null || weekInfoList.size() < 0)
            return;
        if (position == 0) { // 一周视图
            lineChart.setDailyList(weekInfoList.subList(0, 6));
        }
    }

    public void posClicked(int postion) {
        int pos = postion;
        lineChart.setTouchPos(pos);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //   EventBus.getDefault().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void noticeData(List<WeekInfo> df, int weekPos, int index) {
        weekInfoList = df;
        resetLineChart(0);
    }
}

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
import com.walnutin.adapter.YearAdapter;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.DateType;
import com.walnutin.entity.Week;
import com.walnutin.hard.R;
import com.walnutin.util.DateUtils;
import com.walnutin.view.LineChart;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class YearModeFragment extends Fragment implements RunManager.IYearSourceChange {

    YearAdapter yearAdapter;
    List<Week> weekList;
    private LineChart lineChart;
    RunManager runManager;
    GridView weekGridView;
    List<DailyInfo> yearInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_slip, container, false);

        runManager = RunManager.getInstance(getContext());
        weekGridView = (GridView) view.findViewById(R.id.gv_week_slip);
        lineChart = (LineChart) view.findViewById(R.id.linechat);
        lineChart.setLineChartType(DateType.YEAR_TYPE);
        lineChart.setMAXVALUE(1000000);
        lineChart.setOnItemClicked(new LineChart.OnItemClicked() {
            @Override
            public void onItem(int pos) {
                yearAdapter.setPositionClicked(pos);
                posClicked(pos);
            }
        });

        weekList = DateUtils.getHalfYearData(runManager.getSpecilData()); // 得到今年的数据
        yearAdapter = new YearAdapter(getActivity(), weekList);
        weekGridView.setNumColumns(6);
        weekGridView.setAdapter(yearAdapter);
        weekGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                yearAdapter.setPositionClicked(i);
                posClicked(i);
            }
        });

        yearInfoList = runManager.getYearInfoList();
        if (yearInfoList == null) {
            yearInfoList = runManager.getYearVirtualList();
        }
        resetLineChart(0);
        runManager.setiYearSourceChange(this);
        return view;

    }

    public void posClicked(int postion) {
        int pos = postion;
        lineChart.setTouchPos(pos);

    }

    private void resetLineChart(int position) {
        if (yearInfoList == null || yearInfoList.size() < 0)
            return;
        if (position == 0) { // 一周视图
            lineChart.setDailyList(yearInfoList.subList(0, 6));
        }
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        //    EventBus.getDefault().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void noticeData(List<DailyInfo> df, int weekPos, int index) {
        yearInfoList = df;
        resetLineChart(0);
    }
}

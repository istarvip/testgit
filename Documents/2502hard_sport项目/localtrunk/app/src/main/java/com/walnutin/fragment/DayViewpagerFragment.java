package com.walnutin.fragment;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.walnutin.manager.RunManager;
import com.walnutin.adapter.WeekAdapter;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.DateType;
import com.walnutin.entity.Week;
import com.walnutin.eventbus.DailyChangNotify;
import com.walnutin.hard.R;
import com.walnutin.util.DateUtils;
import com.walnutin.view.LineChart;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class DayViewpagerFragment extends BaseFragment implements RunManager.IDaySourceChange {

    GridView weekGridView;
    List<Week> weekList;
    List<DailyInfo> dailyInfoList;
    int defaultPostion = 1;
    WeekAdapter adapter;
    private int currentWeekPosition;//当周position
    private int currentDailyPosition;//当天position
    RunManager runManager;
    private BroadcastReceiver receiver;
    private LineChart lineChart;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_slip, container, false);
        runManager = RunManager.getInstance(getContext());
        weekGridView = (GridView) view.findViewById(R.id.gv_week_slip);
        lineChart = (LineChart) view.findViewById(R.id.linechat);
        lineChart.setLineChartType(DateType.DATE_TYPE);
        lineChart.setMAXVALUE(20000);
        lineChart.setOnItemClicked(new LineChart.OnItemClicked() {
            @Override
            public void onItem(int pos) {
                adapter.setPositionClicked(pos);
                posClicked(pos);
            }
        });

        loadWeekData(defaultPostion);
        adapter = new WeekAdapter(getActivity(), weekList);
        weekGridView.setAdapter(adapter);
        weekGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setPositionClicked(i);
                posClicked(i);
            }
        });
        runManager.setOnIDaySourceChange(this);
        dailyInfoList = runManager.getDailyInfoList();
        resetLineChart(defaultPostion);
        return view;

    }


    public void posClicked(int postion) {
        int pos = postion;
        lineChart.setTouchPos(pos);

    }

    private void resetLineChart(int position) {
        if (dailyInfoList == null || dailyInfoList.size() < 0)
            return;
        if (position == 0) { // 上一周
            lineChart.setDailyList(dailyInfoList.subList(0, 7));
        } else if (position == 1) {  //当前周
            lineChart.setDailyList(dailyInfoList.subList(7, dailyInfoList.size()));
        }
    }

    public static DayViewpagerFragment newInstance(int position) {
        DayViewpagerFragment weekFragment = new DayViewpagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        weekFragment.setArguments(args);
        return weekFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getArguments() != null ? getArguments().getInt("position") : 1;
        defaultPostion = position;
        EventBus.getDefault().register(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Subscribe
    public void onResultPosition(DailyChangNotify stepChangeNotify) {
        int postion = stepChangeNotify.position;
        if (postion > 6) {
            adapter.setPositionClicked(postion - 7);
            posClicked(postion-7);
        } else {
            adapter.setPositionClicked(postion);
            posClicked(postion);
        }

    }

    private void loadWeekData(int postion) {
        Date date = new Date();
        weekList = DateUtils.prevWeekDateList(postion, date);

    }

    public void updateSelected(int postion) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //   this.getActivity().unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void noticeData(List<DailyInfo> df, int weekPos, int index) {
        dailyInfoList = df;
        resetLineChart(weekPos);
    }
}

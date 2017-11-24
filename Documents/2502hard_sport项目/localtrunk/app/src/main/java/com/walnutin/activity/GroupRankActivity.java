package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.entity.GroupRankRecyclerAdapter;
import com.walnutin.entity.RankList;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.MySharedPf;
import com.walnutin.view.CustomLineLayoutManage;
import com.walnutin.view.DividerItemDecoration;
import com.walnutin.view.TopTitleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupRankActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    //    ListView pulistView;
    List<RankList> rankDayLists = new ArrayList<>();
    List<RankList> rankWeekLists = new ArrayList<>();
    List<RankList> rankMonthLists = new ArrayList<>();
    //private int currentType =
    //GroupRankAdapter groupRankAdapter;
    String dayType = "日";
    int pageDayNum = 1;
    int pageWeekNum = 1;
    int pageMonthNum = 1;
    int currentPageNum = 1;
    int pageSize = 20;
    int groupId = 0;
    int type = 1;
    String groupName = "";
    private RecyclerView recyclerView;
    private int lastVisibleItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomLineLayoutManage linearLayoutManager;
    GroupRankRecyclerAdapter recyclerAdapter;
    TopTitleLableView topTitleLableView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  System.out.println("Login SplashActivity:" + this + "  " + System.currentTimeMillis());
        setContentView(R.layout.group_rank);
        groupId = getIntent().getIntExtra("groupid", 0);
        //   Toast.makeText(getApplicationContext(), "id:" + groupId, Toast.LENGTH_SHORT).show();
        type = getIntent().getIntExtra("type", 1);
        groupName = getIntent().getStringExtra("groupName");

        EventBus.getDefault().register(this);
        initView();

        initEvent();
    }

    private void initView() {
        //   initData();
        aQuery = new AQuery(this);
        aQuery.id(R.id.group_today).clicked(this, "selectToday");
        aQuery.id(R.id.group_last_week).clicked(this, "selectLastWeek");
        aQuery.id(R.id.group_lastmonth).clicked(this, "selectLastMonth");
        topTitleLableView = (TopTitleLableView) this.findViewById(R.id.topTitle);

        topTitleLableView.setOnRightClickListener(new TopTitleLableView.OnRightClick() {
            @Override
            public void onClick() {
                groupInfo(null);
            }
        });
     //   topTitleLableView.getBackView().setCompoundDrawables(getResources().getDrawable(R.drawable.arrow_left),null,null,null);

        recyclerView = (RecyclerView) findViewById(R.id.group_rank_recycler);
        linearLayoutManager = new CustomLineLayoutManage(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //  linearLayoutManager.
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,null));
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.group_rank_swiperefreshlayout);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        swipeRefreshLayout.setColorSchemeResources(R.color.red_background_notselected,
                R.color.red_background_notselected, R.color.red_background_notselected,
                R.color.red_background_notselected);

        recyclerAdapter = new GroupRankRecyclerAdapter(this, rankWeekLists);
        recyclerView.setAdapter(recyclerAdapter);

        System.out.println("meayreL:" + recyclerView.getMeasuredHeight() + " :hegith:" + recyclerView.getHeight());
    }

    private void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //     Toast.makeText(getApplicationContext(), "上拉刷新拉，", Toast.LENGTH_SHORT).show();
                postToData(1);
                System.out.println("pull 下拉");
            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recyclerAdapter.getItemCount() &&
                        recyclerAdapter.getItemCount() >= pageSize) {
                    if (recyclerAdapter.getMoreDataState() != recyclerAdapter.NO_MORE_DATA) {         // 不用再往下加载数据
                        recyclerAdapter.changeMoreStatus(GroupRankRecyclerAdapter.LOADING_MORE);
                        loadMoreData(currentPageNum);
                        System.out.println("pull 上拉加载");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        selectToday(null);
    }

//    void initData() {
//        Random random = new Random();
//
//        for (int i = 0; i < 20; i++) {
//            RankList rankList = new RankList();
//            rankList.setStepnumber(random.nextInt(20000));
//            rankList.setHeadimage(null);
//            rankList.setNickname("马大哈：" + i);
//            rankWeekLists.add(rankList);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);       //统计时长
    }

    public void selectToday(View v) {
        MobclickAgent.onEvent(this,"group_day");
        aQuery.id(R.id.line_today).visibility(View.VISIBLE);
        aQuery.id(R.id.line_lastweek).visibility(View.INVISIBLE);
        aQuery.id(R.id.line_lastmonth).visibility(View.INVISIBLE);
        dayType = "日";
        currentPageNum = pageDayNum;
        recyclerAdapter.setRankListData(rankDayLists);
        if (currentPageNum <= 1) {
            postToData(currentPageNum);
        }
    }

    public void selectLastWeek(View v) {
        MobclickAgent.onEvent(this,"group_week");
        aQuery.id(R.id.line_today).visibility(View.INVISIBLE);
        aQuery.id(R.id.line_lastweek).visibility(View.VISIBLE);
        aQuery.id(R.id.line_lastmonth).visibility(View.INVISIBLE);
        dayType = "周";
        currentPageNum = pageWeekNum;
        recyclerAdapter.setRankListData(rankWeekLists);
        if (currentPageNum <= 1) {
            postToData(currentPageNum);
        }
    }

    public void selectLastMonth(View v) {
        MobclickAgent.onEvent(this,"group_month");
        aQuery.id(R.id.line_today).visibility(View.INVISIBLE);
        aQuery.id(R.id.line_lastweek).visibility(View.INVISIBLE);
        aQuery.id(R.id.line_lastmonth).visibility(View.VISIBLE);
        dayType = "月";
        currentPageNum = pageMonthNum;
        recyclerAdapter.setRankListData(rankMonthLists);
        if (currentPageNum <= 1) {
            postToData(currentPageNum);
        }
    }


    public void groupInfo(View v) {
        Intent intent = new Intent(GroupRankActivity.this, GroupInfoActivity.class);
        intent.putExtra("groupid",groupId);
        intent.putExtra("type", type);
        intent.putExtra("groupName",groupName);
        startActivity(intent);

    }

    private void postToData(int currentPageNu) {
        HttpImpl.getInstance().getPersonalGroupRank(MySharedPf.getInstance(getApplicationContext()).
                getString("account"), dayType, 1, pageSize, groupId, type);
    }

    boolean isLoadMore = false;

    private void loadMoreData(int currentPageNu) {
        isLoadMore = true;
        HttpImpl.getInstance().getPersonalGroupRank(MySharedPf.getInstance(getApplicationContext()).
                getString("account"), dayType, (currentPageNu - 1) * pageSize + 1, pageSize, groupId, type);
    }


    @Subscribe
    public void onResultOfGetRank(CommonGroupResult.GetPersonalRank personalRank) {
        if (personalRank.getState() == 0) {
            //     rankDayLists.addAll(personalRank.getRanking());
            if (dayType.equals("日")) {
                if (isLoadMore) {
                    recyclerAdapter.addMoreItem(personalRank.getRanking());
                    currentPageNum = ++pageDayNum;
                    rankDayLists.addAll(personalRank.getRanking());
                } else {
                    rankDayLists = personalRank.getRanking();
                    recyclerAdapter.setRankListData(rankDayLists);
                    currentPageNum = pageDayNum = 2;
                }

            } else if (dayType.equals("周")) {
                if (isLoadMore) {
                    recyclerAdapter.addMoreItem(personalRank.getRanking());
                    currentPageNum = ++pageWeekNum;
                    rankWeekLists.addAll(personalRank.getRanking());
                } else {
                    rankWeekLists = personalRank.getRanking();
                    recyclerAdapter.setRankListData(rankWeekLists);
                    currentPageNum = pageWeekNum = 2;
                }


                //         groupRankAdapter.notifyDataSetChanged();
            } else if (dayType.equals("月")) {

                if (isLoadMore) {
                    recyclerAdapter.addMoreItem(personalRank.getRanking());
                    currentPageNum = ++pageMonthNum;
                    rankWeekLists.addAll(personalRank.getRanking());
                } else {
                    rankMonthLists = personalRank.getRanking();
                    recyclerAdapter.setRankListData(rankMonthLists);
                    currentPageNum = pageMonthNum = 2;
                }


            }
            //      rankLists.a
            recyclerAdapter.changeMoreStatus(GroupRankRecyclerAdapter.PULLUP_LOAD_MORE);
        } else if (personalRank.getState() == 1) {
            Toast.makeText(getApplicationContext(), "输入数据不能为空", Toast.LENGTH_SHORT).show();
        } else if (personalRank.getState() == 2) {
            recyclerAdapter.changeMoreStatus(GroupRankRecyclerAdapter.NO_MORE_DATA);
            Toast.makeText(getApplicationContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
            //  recyclerAdapter.changeMoreStatus(GroupRankRecyclerAdapter.LOADING_MORE);
        } else if (personalRank.getState() == -1) {
            Toast.makeText(getApplicationContext(), "连接服务器异常", Toast.LENGTH_SHORT).show();
        }
//        pullToRefreshListView.onRefreshComplete();
        isLoadMore = false;

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
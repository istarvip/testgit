package com.walnutin.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.activity.GroupAddActivity;
import com.walnutin.activity.GroupCreateActivity;
import com.walnutin.activity.GroupRankActivity;
import com.walnutin.activity.MyApplication;
import com.walnutin.adapter.GroupListAdapter;
import com.walnutin.entity.FragGroupInfo;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.ACache;
import com.walnutin.util.DensityUtils;
import com.walnutin.util.MySharedPf;
import com.walnutin.util.NetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/5/6.
 */
public class GroupFragment extends BaseFragment implements View.OnClickListener {
    AQuery aQuery;
    ImageView group_add;
    ListView groupListView;
    TextView txtOpenNet;
    PopupWindow popupWindow;
    List<FragGroupInfo> groupList = new ArrayList<>();
    GroupListAdapter groupListAdapter;
    private static final String TAG = "GroupFragment";
    private ACache groupCache;
    Gson gson = new Gson();
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_group, container, false);
        aQuery = new AQuery(getActivity());
        group_add = (ImageView) view.findViewById(R.id.group_add);
        group_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGroup(view);
            }
        });
        EventBus.getDefault().register(this);
        initView();
        HttpImpl.getInstance().getPersonalGroupList(MySharedPf.getInstance(getContext()).getString("account"), 1, 100);

        groupCache = ACache.get(getActivity());
        return view;
    }

    public void initView() {
        //   initData();
        Log.d(TAG, "initView: 测试GroupFragment执行");
        groupListView = (ListView) view.findViewById(R.id.group_list);

        groupListAdapter = new GroupListAdapter(getActivity(), groupList);
        groupListView.setAdapter(groupListAdapter);
        groupListAdapter.notifyDataSetChanged();

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupFragment.this.getActivity(), GroupRankActivity.class);
                intent.putExtra("groupid", groupList.get(position).getGroupid());
                intent.putExtra("type", groupList.get(position).getType());
                intent.putExtra("groupName", groupList.get(position).getGroupName());

                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.group_rank_swiperefreshlayout);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        swipeRefreshLayout.setColorSchemeResources(R.color.red_background_notselected,
                R.color.red_background_notselected, R.color.red_background_notselected,
                R.color.red_background_notselected);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpImpl.getInstance().getPersonalGroupList(MySharedPf.getInstance(getContext()).getString("account"), 1, 100);

            }
        });

        txtOpenNet = (TextView) view.findViewById(R.id.rlOpenNet);
        txtOpenNet.setOnClickListener(this);
    }




    public void addGroup(View v) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.group_popwindow, null);
        TextView create = (TextView) view.findViewById(R.id.pop_createGroup);
        TextView join = (TextView) view.findViewById(R.id.pop_joinGroup);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //     Toast.makeText(getActivity(), "create", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GroupFragment.this.getActivity(), GroupCreateActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Toast.makeText(getActivity(), "join", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GroupFragment.this.getActivity(), GroupAddActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(view, DensityUtils.dip2px(getActivity(), 120), DensityUtils.dip2px(getActivity(), 100), true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(v);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlOpenNet:
                openNet(view);
                break;
        }

    }

    // 网络状态 监听
    @Override
    public void noticeNet(boolean netstate) {
        // 判断网络状态 以及列表
            if (netstate == false && groupList.size() == 0) {
                aQuery.id(R.id.rlOpenNet).visibility(View.VISIBLE);
            } else {
                aQuery.id(R.id.rlOpenNet).visibility(View.GONE);
            }

            if (netstate == true && isServerError == true) {
                openNet(null);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("groupFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("groupFragment");
    }

    private List<FragGroupInfo> getLocalGroupList() {
        List<FragGroupInfo> temp = (List<FragGroupInfo>) gson.fromJson(groupCache.getAsString(MyApplication.account), new TypeToken<List<FragGroupInfo>>() {
        }.getType());
        if (temp == null) {
            temp = new ArrayList<>();
        }
        return temp;
    }

    boolean isCreateGrouped = false;
    boolean isServerError = false;

    @Subscribe
    public void resultGetUserGroupList(CommonGroupResult.FragGroupList fragGroupList) {
        swipeRefreshLayout.setRefreshing(false);

        Log.w(TAG, "resultGetUserGroupList: 现在测试是否多次返回");
        if (fragGroupList.getState() == 0) {
            txtOpenNet.setVisibility(View.GONE);
            groupList = fragGroupList.getGroupuser();
            groupListAdapter.setGroupList(groupList);
            isServerError = false;
            groupCache.put(MyApplication.account, gson.toJson(groupList));
        //    return;
        } else if (fragGroupList.getState() ==2){
            return;
        }else{
            isServerError = true;
            groupList = getLocalGroupList();
            groupListAdapter.setGroupList(groupList);
        }
      //  Toast.makeText(getContext(), fragGroupList.getMsg(), Toast.LENGTH_SHORT).show();
        groupListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void resultUpdate(CommonGroupResult.NoticeUpdate noticeUpdate) {
        HttpImpl.getInstance().getPersonalGroupList(MySharedPf.getInstance(getContext()).getString("account"), 1, 100);

    }

    public void openNet(View view) {
        //   NetUtils.setNetworkMethod(getActivity());
        HttpImpl.getInstance().getPersonalGroupList(MySharedPf.getInstance(getContext()).getString("account"), 1, 100);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {      //创建群或加入群成功/再次更新列表
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}

//    public void initData() {
//        Random random = new Random();
//        for (int i = 0; i < 20; i++) {
//            FragGroupInfo fragGroupInfo = new FragGroupInfo();
//            fragGroupInfo.setGroupName("马大哈：" + i);
//            fragGroupInfo.setType(1);
//            int size = random.nextInt(5) + 1;
//            fragGroupInfo.setHeadcount(size);
//            List<String> stringList = new ArrayList<>();
//            for (int j = 0; j < size; j++) {
//                String str = "null";
//                stringList.add(str);
//                fragGroupInfo.setHeadimage(stringList);
//            }
//            groupList.add(fragGroupInfo);
//        }
//    }

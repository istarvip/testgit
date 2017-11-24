package com.walnutin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.walnutin.adapter.GroupMemberRecyclerAdapter;
import com.walnutin.entity.UserBean;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.manager.GroupManager;
import com.walnutin.view.CustomLineLayoutManage;
import com.walnutin.view.DividerItemDecoration;
import com.walnutin.view.TopTitleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 作者：MrJiang on 2016/7/5 14:33
 */
public class GroupAllMemberActivity extends BaseActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    GroupMemberRecyclerAdapter recyclerAdapter;
    private List<UserBean> userBeanList;
    GroupManager groupManager;
    int groupId;
    int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_member);
        groupId = getIntent().getIntExtra("groupid", 0);
        type = getIntent().getIntExtra("type", 1);
        groupManager = GroupManager.getInstance(this);
        EventBus.getDefault().register(this);
        topTitleLableView.getTitleView().setText("所有成员");
        //     userBeanList = (List<UserBean>) getIntent().getSerializableExtra("userList");
        initView();
        HttpImpl.getInstance().getGroupAllMemberById(groupId, type);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.group_member_recycler);
        linearLayoutManager = new CustomLineLayoutManage(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        recyclerAdapter = new GroupMemberRecyclerAdapter(this, userBeanList);
        ((GroupMemberRecyclerAdapter) recyclerAdapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemDelete(new GroupMemberRecyclerAdapter.IDeleteItem() {
            @Override
            public void deleteItem(int position) {
                HttpImpl.getInstance().kickMember(type, groupId, userBeanList.get(position).getAccount());
            }
        });

    }


    @Subscribe
    public void onResultAllMember(CommonGroupResult.GetGroupMemberResult result) {
        if (result.state == 0) {
            userBeanList = result.group;
            recyclerAdapter.setRankListData(userBeanList);
        }

    }

    @Subscribe
    public void onResultKickMember(CommonGroupResult.KickGroupMemberResult result) {
        if (result.state == 0) {

        } else {
            //    Toast.makeText(getApplicationContext(),"删除失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        groupManager.clearDeleteList();
    }
}

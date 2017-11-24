package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.adapter.GroupTransferAdapter;
import com.walnutin.adapter.IconAdapter;
import com.walnutin.entity.GroupDetailInfo;
import com.walnutin.entity.UserBean;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.view.ConfirmDialog;
import com.walnutin.view.RelativeLineGroupView;
import com.walnutin.view.TopTitleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupInfoActivity extends BaseActivity {
    private GridView group_portrait;
    private IconAdapter adapter = null;
    AQuery aQuery;
    int groupId = 0;
    int type = 1;
    String creator;

    private List<String> headList;
    private List<UserBean> userBeanList;
    GroupDetailInfo groupDetailInfo;
    RelativeLineGroupView groupNum;
    RelativeLineGroupView groupName;
    RelativeLayout rlmemberLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_groupdetailinfo);

        topTitleLableView.getRightView().setVisibility(View.GONE);
        groupNum = (RelativeLineGroupView) findViewById(R.id.group_num);
        groupName = (RelativeLineGroupView) findViewById(R.id.group_name);
        groupId = getIntent().getIntExtra("groupid", 0);
        type = getIntent().getIntExtra("type", 1);
        groupNum.setCenterContent(String.valueOf(groupId));
        groupName.setCenterContent(getIntent().getStringExtra("groupName"));
        aQuery = new AQuery(this);
        aQuery.id(R.id.exit_group).clicked(this, "exitGroup");
        aQuery.id(R.id.group_allMember).clicked(this, "allMember");
        aQuery.id(R.id.group_transfer).clicked(this, "groupTransferMember");
        aQuery.id(R.id.twoCode).clicked(this, "showCode");
        EventBus.getDefault().register(this);
        initView();
        HttpImpl.getInstance().getGroupInfoById(groupId, type);
    }

    private void initView() {
        group_portrait = (GridView) findViewById(R.id.group_portrait);
        adapter = new IconAdapter(this);
        group_portrait.setAdapter(adapter);

        topTitleLableView.setOnRightClickListener(new TopTitleLableView.OnRightClick() {
            @Override
            public void onClick() {
                Intent intent = new Intent(GroupInfoActivity.this, GroupSettingActivity.class);
                intent.putExtra("groupid", groupId);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    private void reBuildData() {
        updateView();

        userBeanList = groupDetailInfo.getUser();
        int size = userBeanList.size();
        size = size > 10 ? 10 : size;
        headList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UserBean userBean = userBeanList.get(i);
            headList.add(userBean.getHeadimage());
        }

        adapter.setIconList(headList);
    }

    private void updateView() {
        creator = groupDetailInfo.getAccount();
        if (MyApplication.account.equals(creator)) {
            topTitleLableView.getRightView().setVisibility(View.VISIBLE);
            aQuery.id(R.id.rlmemberLayout).visibility(View.VISIBLE);
            aQuery.id(R.id.rlgroupTransfer).visibility(View.VISIBLE);
            aQuery.id(R.id.exit_group).text("解散该群");
        }
    }

    String title="退出群组";
    public void exitGroup(View v) {
         title = "您确定退出该群吗";
        if (creator.equals(MyApplication.account)) {
            title = "您确定解散该群吗";
            //       Toast.makeText(getApplicationContext(), "您是群主，请先转让群给其他成员", Toast.LENGTH_SHORT).show();
            //     return;
        }
        final ConfirmDialog confirm = new ConfirmDialog(this, title, "", "取消", "确定");
        confirm.setCanceledOnTouchOutside(true);
        confirm.show();
        confirm.setOkListener(new ConfirmDialog.IOkListener() {
            @Override
            public void doOk() {
                HttpImpl.getInstance().exitGroup(type, groupId);
                confirm.dismiss();
            }

        });
        confirm.setCancellistener(new ConfirmDialog.ICancelListener() {
            @Override
            public void doCancel() {
                confirm.dismiss();
            }
        });

    }

    @Subscribe
    public void exitResult(CommonGroupResult.ExitGroupResult result) {
        if (result.state == 0 || result.state ==4) {
            EventBus.getDefault().post(new CommonGroupResult.NoticeUpdate(true));
            Toast.makeText(getApplicationContext(),result.msg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GroupInfoActivity.this, MainActivity
                    .class);
            startActivity(intent);
            finish();
        }
    }

    public void allMember(View v) {
        Intent intent = new Intent(this, GroupAllMemberActivity.class);
        //     intent.putExtra("userList", (Serializable) userBeanList);
        intent.putExtra("groupid", groupId);
        intent.putExtra("type", type);
        startActivity(intent);

    }

    public void groupTransferMember(View v) {
        Intent intent = new Intent(this, GroupTransferActivity.class);
        intent.putExtra("groupid", groupId);
        intent.putExtra("type", type);
        startActivity(intent);

    }

    public void showCode(View v) {
//        Intent intent = new Intent(this, GroupAllMemberActivity.class);
//        intent.putExtra("userList", (Serializable) userBeanList);
//        startActivity(intent);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onResultDetailGroupInfo(CommonGroupResult.GetGroupDetailResult groupResult) {
        if (groupResult.state == 0) {
            groupDetailInfo = groupResult.group;

            reBuildData();
        }
    }


}
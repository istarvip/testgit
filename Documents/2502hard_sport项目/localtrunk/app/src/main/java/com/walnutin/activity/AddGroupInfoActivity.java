package com.walnutin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.walnutin.adapter.IconAdapter;
import com.walnutin.entity.GroupDetailInfo;
import com.walnutin.entity.UserBean;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.MySharedPf;
import com.walnutin.view.TopTitleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by assa on 2016/6/6.
 */
public class AddGroupInfoActivity extends BaseActivity {
    private GridView group_portrait;
    private IconAdapter adapter = null;
    private AQuery aQuery;
    private List<String> headList;
    private List<UserBean> userBeanList;
    GroupDetailInfo groupDetailInfo;
    TopTitleLableView topTitleLableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_groupinfo);
        topTitleLableView = (TopTitleLableView) findViewById(R.id.topTitle);
        aQuery = new AQuery(this);
        aQuery.id(R.id.btnAddGroup).clicked(this, "addGroup");
        groupDetailInfo = (GroupDetailInfo) getIntent().getSerializableExtra("GroupInfo");
     //   topTitleLableView.getTitleView().setText(String.valueOf(groupDetailInfo.getGroupid()));
        aQuery.id(R.id.groupName).text(groupDetailInfo.getGroupName());
        EventBus.getDefault().register(this);
        //  aQuery.id(R.id)
        init();
    }


    private void init() {

        group_portrait = (GridView) findViewById(R.id.group_portrait);
        adapter = new IconAdapter(this);
        group_portrait.setAdapter(adapter);
        reBuildData();
    }

    private void reBuildData() {
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

    public void addGroup(View view) {

        if (groupDetailInfo.getVerify().equals("yes")) {
            Intent intent = new Intent(AddGroupInfoActivity.this, GroupVerifActivity.class);
            intent.putExtra("GroupInfo", groupDetailInfo);
            startActivity(intent);
        } else {
            HttpImpl.getInstance().addGroupNoVerify(MySharedPf.getInstance(getApplicationContext()).getString("account"),
                    groupDetailInfo.getType(), groupDetailInfo.getGroupid());
        }
    }

    @Subscribe
    public void onValifyResult(CommonGroupResult.AddGroupNoVerifyResult result) {
        if (result.state == 0) {
            EventBus.getDefault().post(new CommonGroupResult.NoticeUpdate(true));
            Toast.makeText(getApplicationContext(), "加群成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddGroupInfoActivity.this, MainActivity
                    .class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), result.msg, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}

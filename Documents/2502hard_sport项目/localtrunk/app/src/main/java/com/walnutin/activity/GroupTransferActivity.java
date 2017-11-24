package com.walnutin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.walnutin.adapter.GroupTransferAdapter;
import com.walnutin.entity.UserBean;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.Config;
import com.walnutin.view.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 作者：MrJiang on 2016/7/5 14:33
 */
public class GroupTransferActivity extends BaseActivity {
    private ListView listView;
    GroupTransferAdapter recyclerAdapter;
    private List<UserBean> userBeanList;
    int groupId;
    int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_member_transfer);
        topTitleLableView.getTitleView().setText("群主转让");
        //  userBeanList = (List<UserBean>) getIntent().getSerializableExtra("userList");
        groupId = getIntent().getIntExtra("groupid", 0);
        type = getIntent().getIntExtra("type", 1);
        EventBus.getDefault().register(this);
        initView();
        initEvent();
        HttpImpl.getInstance().getGroupAllMemberById(groupId, type);

    }

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
           //     Toast.makeText(getApplicationContext(), userBeanList.get(position).getNickname(), Toast.LENGTH_SHORT).show();
                if(MyApplication.account.equals(userBeanList.get(position).getAccount())){
                    return;
                }

                final ConfirmDialog confirmDialog = new ConfirmDialog(GroupTransferActivity.this, "确定转让", "", "取消", "确定");
                confirmDialog.show();
                confirmDialog.setCanceledOnTouchOutside(true);
                confirmDialog.setOkListener(new ConfirmDialog.IOkListener() {
                    @Override
                    public void doOk() {
                        UserBean userBean = userBeanList.get(position);
                        HttpImpl.getInstance().transferGroup(type, groupId, userBean.getAccount());

                    }
                });
                confirmDialog.setCancellistener(new ConfirmDialog.ICancelListener() {
                    @Override
                    public void doCancel() {
                        confirmDialog.dismiss();
                    }
                });
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
    public void transferResult(CommonGroupResult.TransferGroupResult result) {
        if (result.state == 0) {
            EventBus.getDefault().post(new CommonGroupResult.NoticeUpdate(true));
            Toast.makeText(getApplicationContext(), "转让群成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GroupTransferActivity.this, MainActivity
                    .class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.group_member_recycler);
        recyclerAdapter = new GroupTransferAdapter(this, userBeanList);
        listView.setAdapter(recyclerAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

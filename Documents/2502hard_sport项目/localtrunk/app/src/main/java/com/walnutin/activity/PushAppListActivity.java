package com.walnutin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.walnutin.manager.NoticeInfoManager;
import com.walnutin.adapter.AppListAdapter;
import com.walnutin.hard.R;
import com.walnutin.view.CorformPopupWindow;
import com.walnutin.view.TopTitleLableView;

/**
 * Created by assa on 2016/5/24.
 */
public class PushAppListActivity extends BaseActivity implements View.OnClickListener {
    TextView addMore;
    ListView listSoftView;
    AppListAdapter appListAdapter;
    NoticeInfoManager noticeInfoManager;
    int deleteItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listapp);
        noticeInfoManager = NoticeInfoManager.getInstance(getApplicationContext());
        initView();
        loadLocalData();
    }

    private void loadLocalData() {
        noticeInfoManager.getLocalNoticeInfo();
        appListAdapter.setIconList(noticeInfoManager.getSoftRemindList());
    }

    private void initView() {
        addMore = (TextView) findViewById(R.id.addSoft);
        addMore.setOnClickListener(this);
        listSoftView = (ListView) findViewById(R.id.appListView);
        appListAdapter = new AppListAdapter(this);
        listSoftView.setAdapter(appListAdapter);

        topTitleLableView.setOnBackListener(new TopTitleLableView.OnBackListener() {
            @Override
            public void onClick() {
                noticeInfoManager.saveNoticeInfo();
                finish();
            }
        });

        listSoftView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem = position;
                showDeleteDialog();
                //        appListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addSoft:
                startNoticeService();
                break;

        }
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sure:
                    if (deleteItem != -1) {
                        noticeInfoManager.getSoftRemindList().remove(deleteItem);
                        appListAdapter.notifyDataSetChanged();
                        deleteItem = -1;
                    }
                    headPopWindow.dismiss();
                    break;
            }
        }
    };

    private CorformPopupWindow headPopWindow;

    private void showDeleteDialog() {
        headPopWindow = new CorformPopupWindow(PushAppListActivity.this, itemsOnClick);
        headPopWindow.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            appListAdapter.setIconList(noticeInfoManager.getSoftRemindList());
        }
    }

    void startNoticeService() {
        Intent intent = new Intent(PushAppListActivity.this, ListAppActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        noticeInfoManager.saveNoticeInfo();
    }
}
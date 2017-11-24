package com.walnutin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.walnutin.adapter.GroupSearchResultAdapter;
import com.walnutin.entity.GroupSearchInfo;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class GroupAddActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    ClearEditText inputGroup;
    // LinearLayout jump_joinGroup;
    List<GroupSearchInfo> searchInfoList = new ArrayList<>();
    ListView listView;
    private GroupSearchResultAdapter groupSearchResultAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgroup);
        aQuery = new AQuery(this);
        aQuery.id(R.id.register_back).clicked(this, "register_back");
        EventBus.getDefault().register(this);
        initview();
    }

    public void register_back(View v) {
        finish();
    }

    void initview() {
        listView = (ListView) findViewById(R.id.jump_joinGroup);
        groupSearchResultAdapter = new GroupSearchResultAdapter(getApplicationContext(), searchInfoList);
        listView.setAdapter(groupSearchResultAdapter);
        inputGroup = (ClearEditText) findViewById(R.id.et_msg_search);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(GroupAddActivity.this, AddGroupInfoActivity.class);
//           //     intent.putExtra("GroupInfo", groupResult.groupDetailInfo);
//                startActivity(intent);
                HttpImpl.getInstance().getGroupInfoById(searchInfoList.get(position).getGroupid(),
                        searchInfoList.get(position).getType());
            }
        });

        inputGroup.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (inputGroup.getText().toString().trim().length() > 0) {
                        HttpImpl.getInstance().searchGroupList(inputGroup.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }

        });

    }

    @Subscribe
    public void onResultGroupList(CommonGroupResult.SearchGroupResult searchGroupResult) {
        if (searchGroupResult.getState() == 0) {
            if (searchGroupResult.getGroups() != null) {
                searchInfoList = searchGroupResult.getGroups();
                groupSearchResultAdapter.setGroupList(searchInfoList);
            } else {
                Toast.makeText(getApplicationContext(), "没有符合条件的群", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), searchGroupResult.getMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    @Subscribe
    public void onResultDetailGroupInfo(CommonGroupResult.GetGroupDetailResult groupResult) {
        if (groupResult.state == 0) {
            Intent intent = new Intent(GroupAddActivity.this, AddGroupInfoActivity.class);
            intent.putExtra("GroupInfo", groupResult.group);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
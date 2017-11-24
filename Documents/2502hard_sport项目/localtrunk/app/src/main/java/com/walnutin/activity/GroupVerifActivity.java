package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.walnutin.entity.GroupDetailInfo;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.MySharedPf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by assa on 2016/5/25.
 */
public class GroupVerifActivity extends Activity {
    TextView groud_next;
    TextView verif_return;
    EditText verify;
    GroupDetailInfo groupSearchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_verif);
        verify = (EditText) findViewById(R.id.et_msg_search);
        groupSearchInfo = (GroupDetailInfo) getIntent().getSerializableExtra("GroupInfo");
        EventBus.getDefault().register(this);

        initView();
    }

    private void initView() {
        groud_next = (TextView) findViewById(R.id.groud_next);
        verif_return = (TextView) findViewById(R.id.verif_return);
        verif_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        groud_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verify.getText().toString().trim().length()<1){
                    Toast.makeText(getApplicationContext(),"验证码未填写",Toast.LENGTH_SHORT).show();
                    return;
                }
                String account = MySharedPf.getInstance(getApplicationContext()).getString("account");
            //    String verify = groupSearchInfo.getVerify();
                int type = groupSearchInfo.getType();
                int groupId= groupSearchInfo.getGroupid();
                HttpImpl.getInstance().addGroup(account,verify.getText().toString().trim(),type,groupId);
            }
        });
    }

    @Subscribe
    public void onValifyResult(CommonGroupResult.AddGroupNeedVerifyResult result) {
        if (result.state == 0) {
            EventBus.getDefault().post(new CommonGroupResult.NoticeUpdate(true));
            Toast.makeText(getApplicationContext(), "加群成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GroupVerifActivity.this, MainActivity
                    .class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), result.msg, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

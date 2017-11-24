package com.walnutin.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.walnutin.entity.GroupInfo;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.Utils;
import com.walnutin.view.RelativeEditGroupView;
import com.walnutin.view.TopTitleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GroupSettingActivity extends BaseActivity {
    RelativeEditGroupView groupDescipse;
    RelativeEditGroupView groupGoal;
    RelativeEditGroupView companyName;
    RelativeEditGroupView companyScale;
    RelativeEditGroupView companyCreator;
    RelativeEditGroupView phone;
    LinearLayout companyGroupType;
    ImageView openValid;
    EditText valid;
    public GroupInfo groupInfo;
    int groupId;
    int type;  // 1为个人  2 为企业

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_setting);
        groupId = getIntent().getIntExtra("groupid", 0);
        type = getIntent().getIntExtra("type", 1);

        EventBus.getDefault().register(this);
        initView();
        initEvent();

        if (type == 2) {
            companyGroupType.setVisibility(View.VISIBLE);
            HttpImpl.getInstance().getCompanyGroupSettingInfo(groupId);
        } else {
            companyGroupType.setVisibility(View.GONE);
            HttpImpl.getInstance().getPersonalGroupSettingInfo(groupId);
        }
    }

    private void initEvent() {
        topTitleLableView.setOnRightClickListener(new TopTitleLableView.OnRightClick() {
            @Override
            public void onClick() {
                Gson gson = new Gson();
                GroupInfo gif = new GroupInfo();

                // 保存页面信息。。。。。。。。。
                if (groupDescipse.getCenterEditText().getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "描述不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (groupGoal.getCenterEditText().getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "目标不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (validType == true) {
                    if (valid.getText().toString().trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "验证密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    gif.setVerify(valid.getText().toString().trim());
                }
                gif.setAccount(groupInfo.getAccount());
                gif.setGroupId(groupId);
                gif.setGroupName(groupInfo.getGroupName());
                gif.setDescription(groupDescipse.getCenterEditText().getText().toString().trim());
                gif.setGoal(Integer.parseInt(groupGoal.getCenterEditText().getText().toString().trim()));
                if (type == 1) {
                    HttpImpl.getInstance().setPersonalGroupInfo(gson.toJson(gif));
                } else {
                    if (companyName.getCenterEditText().getText().toString().trim().length() <= 0) {
                        Toast.makeText(getApplicationContext(), "公司名不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (companyScale.getCenterEditText().getText().toString().trim().length() <= 0) {
                        Toast.makeText(getApplicationContext(), "公司规模不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (companyCreator.getCenterEditText().getText().toString().trim().length() <= 0) {
                        Toast.makeText(getApplicationContext(), "创建人不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (phone.getCenterEditText().getText().toString().trim().length() != 0 &&
                            !Utils.isMobileNO(phone.getCenterEditText().getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "手机号码不规范", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    gif.setCompanyName(companyName.getCenterEditText().getText().toString().trim());
                    gif.setScale(Integer.parseInt(companyScale.getCenterEditText().getText().toString().trim()));
                    gif.setUserName(companyCreator.getCenterEditText().getText().toString().trim());
                    gif.setMobile(phone.getCenterEditText().getText().toString().trim());
                    HttpImpl.getInstance().setCompanyGroupInfo(gson.toJson(gif));
                }
            }
        });


    }


    private void initView() {
        groupDescipse = (RelativeEditGroupView) findViewById(R.id.redt_inputGroupDescrible);
        groupGoal = (RelativeEditGroupView) findViewById(R.id.redt_inputGroupGoal);
        companyName = (RelativeEditGroupView) findViewById(R.id.redt_inputCompanyName);
        companyScale = (RelativeEditGroupView) findViewById(R.id.redt_inputGroupCompanyScale);
        companyCreator = (RelativeEditGroupView) findViewById(R.id.redt_inputGroupCreatorName);

        openValid = (ImageView) findViewById(R.id.isNeedValid);
        valid = (EditText) findViewById(R.id.edt_inputGroupVerify);

        companyGroupType = (LinearLayout) findViewById(R.id.lineGroupType);

        phone = (RelativeEditGroupView) findViewById(R.id.redt_inputGroupCompanyMail);
        groupGoal.getCenterEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        groupGoal.getCenterEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        companyScale.getCenterEditText().setInputType(InputType.TYPE_CLASS_NUMBER);


        openValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validType == true) {
                    openValid.setBackgroundResource(R.drawable.closeblue);
                    valid.setVisibility(View.GONE);
                } else {
                    valid.setVisibility(View.VISIBLE);
                    openValid.setBackgroundResource(R.drawable.openblue);
                }
                validType = !validType;
            }
        });
    }

    boolean validType = false;

    private void updateView() {
        groupDescipse.getCenterEditText().setText(groupInfo.getDescription());
        groupGoal.getCenterEditText().setText(String.valueOf(groupInfo.getGoal()));
        if (groupInfo.getVerify().equals("no")) {
            openValid.setBackgroundResource(R.drawable.closeblue);
            validType = false;
            valid.setVisibility(View.GONE);
        } else {
            openValid.setBackgroundResource(R.drawable.openblue);
            validType = true;
            valid.setVisibility(View.VISIBLE);
            valid.setText(groupInfo.getVerify());

        }
        if (type == 1) {
            return;
        }
        companyName.getCenterEditText().setText(groupInfo.getCompanyName());
        companyScale.getCenterEditText().setText(String.valueOf(groupInfo.getScale()));
        companyCreator.getCenterEditText().setText(groupInfo.getUserName());
        phone.getCenterEditText().setText(groupInfo.getMobile());

    }

    public void showCode(View v) {

    }

    @Subscribe
    public void onGetgroupSettingResult(CommonGroupResult.GroupSettingResult result) {

        if (result.state == 0) {
            groupInfo = result.groupI;
            updateView();
        }
    }

    @Subscribe
  public   void onSaveSettingInfoResult(CommonGroupResult.SetGroupResult result) {
        if (result.state == 0) {
            Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new CommonGroupResult.NoticeUpdate(true));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
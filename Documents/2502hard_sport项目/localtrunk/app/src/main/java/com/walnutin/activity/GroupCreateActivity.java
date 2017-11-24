package com.walnutin.activity;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.walnutin.entity.GroupInfo;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.MySharedPf;
import com.walnutin.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class GroupCreateActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    TextView junpGroup;
    LinearLayout addLinearLayout;
    List<EditText> listParts = new ArrayList<EditText>();

    enum GroupType {
        Person_GROUP_TYPE, Company_GROUP_TYPE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);
        aQuery = new AQuery(this);
        aQuery.id(R.id.register_back).clicked(this, "register_back");
        aQuery.id(R.id.group_txtCompany).clicked(this, "createCompany");
        aQuery.id(R.id.group_txtPerson).clicked(this, "createPerson");
        aQuery.id(R.id.btn_group_company_next).clicked(this, "nextPage");
        aQuery.id(R.id.group_addDepartment).clicked(this, "addDepartment");
        aQuery.id(R.id.btn_group_company_submit).clicked(this, "addGroupSubmit");
        aQuery.id(R.id.btn_group_personal_submit).clicked(this, "addPersonSubmit");
        aQuery.id(R.id.isNeedValid).clicked(this, "switchValide");
        aQuery.id(R.id.isCompanyNeedValid).clicked(this, "switchCompanyValide");
        addLinearLayout = (LinearLayout) findViewById(R.id.lladdDepartment);
        flag = 0;
        listParts.clear();
        initDepartInfo();
        EventBus.getDefault().register(this);
        //  initview();
    }

    boolean isNeed = false;

    public void switchValide(View v) {
        switch (v.getId()) {
            case R.id.isNeedValid:
                if (isNeed == false) {
                    aQuery.id(R.id.isNeedValid).background(R.drawable.openblue);
                    aQuery.id(R.id.edt_inputGroupVerify).visibility(View.VISIBLE);
                } else {
                    aQuery.id(R.id.isNeedValid).background(R.drawable.closeblue);
                    aQuery.id(R.id.edt_inputGroupVerify).visibility(View.GONE);
                }
                isNeed = !isNeed;
                break;
        }
    }

    boolean isCompanyNeed = false;

    public void switchCompanyValide(View v) {
        if (isCompanyNeed == false) {
            aQuery.id(R.id.isCompanyNeedValid).background(R.drawable.openblue);
            aQuery.id(R.id.edt_addGroupCompanyValidate).visibility(View.VISIBLE);
        } else {
            aQuery.id(R.id.isCompanyNeedValid).background(R.drawable.closeblue);
            aQuery.id(R.id.edt_addGroupCompanyValidate).visibility(View.GONE);
        }
        isCompanyNeed = !isCompanyNeed;

    }


    public void initDepartInfo() {
        {
            partName[0] = getApplicationContext().getResources().getString(R.string.depart3);
            partName[1] = getApplicationContext().getResources().getString(R.string.depart4);
            partName[2] = getApplicationContext().getResources().getString(R.string.depart5);
            partName[3] = getApplicationContext().getResources().getString(R.string.depart6);
            partName[4] = getApplicationContext().getResources().getString(R.string.depart7);
        }

    }

    public void register_back(View v) {
        finish();
    }

    GroupType groupType;

    public void createCompany(View v) {
        v.setBackgroundResource(R.drawable.redbackground_selector);
        aQuery.id(R.id.group_txtCompany).textColor(getResources().getColor(R.color.white));

        aQuery.id(R.id.group_txtPerson).background(R.color.white);
        aQuery.id(R.id.group_txtPerson).textColor(getResources().getColor(R.color.lable_text_color));

        aQuery.id(R.id.group_rl_person).visibility(View.GONE);
        aQuery.id(R.id.group_rl_company).visibility(View.VISIBLE);
        aQuery.id(R.id.group_rl_company_secondpage).visibility(View.GONE);
        groupType = GroupType.Company_GROUP_TYPE;
    }

    public void createPerson(View v) {
        v.setBackgroundResource(R.drawable.redbackground_selector);
        aQuery.id(R.id.group_txtPerson).textColor(getResources().getColor(R.color.white));

        aQuery.id(R.id.group_txtCompany).background(R.color.white);
        aQuery.id(R.id.group_txtCompany).textColor(getResources().getColor(R.color.lable_text_color));

        aQuery.id(R.id.group_rl_company).visibility(View.GONE);
        aQuery.id(R.id.group_rl_person).visibility(View.VISIBLE);
        aQuery.id(R.id.group_rl_company_secondpage).visibility(View.GONE);

        groupType = GroupType.Person_GROUP_TYPE;

    }

    public void nextPage(View v) {
        if(aQuery.id(R.id.edt_inputGroupCompanyMail).getText().toString().trim().length()>0&&
                !Utils.isMobileNO(aQuery.id(R.id.edt_inputGroupCompanyMail).getText().toString().trim())){
            Toast.makeText(getApplicationContext(),"手机号不合法",Toast.LENGTH_SHORT).show();
            return;
        }

        aQuery.id(R.id.group_rl_company).visibility(View.GONE);
        aQuery.id(R.id.group_rl_company_secondpage).visibility(View.VISIBLE);
    }

    public void addPersonSubmit(View v) {

        if (aQuery.id(R.id.edt_inputGroupName).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "群组名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.edt_inputGroupDescrible).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "描述不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.edt_inputGroupGoal).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "群目标不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNeed) {
            if (aQuery.id(R.id.edt_inputGroupVerify).getText().toString().trim().length() <= 0) {
                Toast.makeText(getApplicationContext(), "验证密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            aQuery.id(R.id.edt_inputGroupVerify).text("");
        }

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setAccount(MySharedPf.getInstance(getApplicationContext()).getString("account"));
        groupInfo.setDescription(aQuery.id(R.id.edt_inputGroupDescrible).getText().toString());
        groupInfo.setGroupName(aQuery.id(R.id.edt_inputGroupName).getText().toString());
        groupInfo.setGoal(Integer.valueOf(aQuery.id(R.id.edt_inputGroupGoal).getText().toString()));
        groupInfo.setVerify(aQuery.id(R.id.edt_inputGroupVerify).getText().toString().trim());
        groupInfo.setHeadimage(MySharedPf.getInstance(getApplicationContext()).getString("headimage"));
        HttpImpl.getInstance().addPersonalGroup(groupInfo);

    }

    public void addGroupSubmit(View v) {
        if (aQuery.id(R.id.edt_inputCompanyName).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "公司名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.edt_inputGroupCompanyScale).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "公司规模不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.edt_inputGroupCreatorName).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.edt_inputCompanyGroupName).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "群组名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.edt_inputGroupCompanyDescrible).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "描述不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.edt_inputGroupCompanyGoal).getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "群目标不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCompanyNeed) {
            if (aQuery.id(R.id.edt_addGroupCompanyValidate).getText().toString().trim().length() <= 0) {
                Toast.makeText(getApplicationContext(), "验证密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            aQuery.id(R.id.edt_addGroupCompanyValidate).text("");
        }

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setAccount(MySharedPf.getInstance(getApplicationContext()).getString("account"));
        groupInfo.setDescription(aQuery.id(R.id.edt_inputGroupCompanyDescrible).getText().toString());
        groupInfo.setGroupName(aQuery.id(R.id.edt_inputCompanyGroupName).getText().toString());
        groupInfo.setGoal(Integer.valueOf(aQuery.id(R.id.edt_inputGroupCompanyGoal).getText().toString()));
        groupInfo.setVerify(aQuery.id(R.id.edt_addGroupCompanyValidate).getText().toString().trim());
        groupInfo.setHeadimage(MySharedPf.getInstance(getApplicationContext()).getString("headimage"));
        groupInfo.setUserName(aQuery.id(R.id.edt_inputGroupCreatorName).getText().toString().trim());
        groupInfo.setScale(Integer.valueOf(aQuery.id(R.id.edt_inputGroupCompanyScale).getText().toString()));
        groupInfo.setCompanyName(aQuery.id(R.id.edt_inputCompanyName).getText().toString().trim());
        if(Utils.isMobileNO(aQuery.id(R.id.edt_inputGroupCompanyMail).getText().toString().trim())){
            groupInfo.setMobile(aQuery.id(R.id.edt_inputGroupCompanyMail).getText().toString().trim());

        }
        HttpImpl.getInstance().createCompanyGroup(groupInfo);
    }

    int flag = 0;
    int partSize = 5;
    String[] partName = new String[5];

    public void addDepartment(View v) {
        if (flag >= 5) {
            Toast.makeText(getApplicationContext(), "最多添加五个部门", Toast.LENGTH_SHORT).show();
            return;
        }
        View inflater = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.group_department_item, null);
        ((TextView) inflater.findViewById(R.id.txtPart))
                .setText(partName[flag]);
        listParts.add((EditText) inflater.findViewById(R.id.edt_inputGroupCompanyDepart));
        addLinearLayout.addView(inflater);
        flag++;
    }

    @Subscribe
    public void onGroupAddResult(CommonGroupResult.AddGroupResult result) {


        if (result.state == 0) {
            EventBus.getDefault().post(new CommonGroupResult.NoticeUpdate(true));
            Toast.makeText(getApplication(), result.msg, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(getApplication(), result.msg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
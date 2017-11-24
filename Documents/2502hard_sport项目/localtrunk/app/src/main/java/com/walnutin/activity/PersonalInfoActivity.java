package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.walnutin.eventbus.CommonUserUpdataResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.entity.UserBean;
import com.walnutin.util.MySharedPf;
import com.walnutin.view.BirthDateDialog;
import com.walnutin.util.CalendarDateUtils;
import com.walnutin.view.MyHeightPopupWindow;
import com.walnutin.view.MySexPopupWindow;
import com.walnutin.view.MyWeightPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PersonalInfoActivity extends Activity implements View.OnClickListener, TextWatcher {
    public static final int SUCESS = 0;
    private MySexPopupWindow mMySexPopupWindow;
    private Button personal_sex;
    private Button personal_birth;
    private Button personal_height;
    private Button personal_weight;
    private Button personal_finish;
    private TextView bt_return;
    private int FIRST_SET = 1;

    private ImageButton personal_sex_check;
    private ImageButton personal_birth_check;
    private ImageButton personal_hight_check;
    private ImageButton personal_weight_check;

    private String mHeight;
    private String mWeight;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private BirthDateDialog dateDialog;
    private MyHeightPopupWindow myHeightPopupWindow;
    private MyWeightPopupWindow myWeightPopupWindow;
    private MySharedPf shared;
    private boolean isFromRegActivity;
    private EditText personal_name;
    private boolean isNameLegal;
    private ImageButton personal_name_check;
    private static final String TAG = "PersonalInfoActivity";
    private RelativeLayout bottomLine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);
        initIntent();
        initView();
        settingDate();
        initEvent();

    }

    private void initIntent() {
        shared = MySharedPf.getInstance(this);
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("isFromReg", false)) {
            isFromRegActivity = true;
        }
        //TODO:如果是从注册页面过来的,就  显示最下栏,并把填写后的信息存储到sp中, 不管怎样都提交到服务器
    }

    private void initEvent() {
        EventBus.getDefault().register(this);
        if (!isFromRegActivity) {
            bottomLine.setVisibility(View.GONE);
        }
    }

    private void settingDate() {

        if (shared.getString("nickname") != null) {
            personal_name.setText(shared.getString("nickname"));
            personal_name_check.setBackgroundResource(R.drawable.right);
        }

        if (shared.getString("sex") != null) {
            personal_sex.setText(shared.getString("sex"));
            personal_sex_check.setBackgroundResource(R.drawable.right);
        }
        if (shared.getString("birth") != null) {
            personal_birth.setText(shared.getString("birth"));
            personal_birth_check.setBackgroundResource(R.drawable.right);
        }
        if (shared.getString("weight") != null ) {
            personal_weight.setText(shared.getString("weight"));
            personal_weight_check.setBackgroundResource(R.drawable.right);
        }
        if (shared.getString("height") != null ) {
            personal_height.setText(shared.getString("height") + "cm");
            personal_hight_check.setBackgroundResource(R.drawable.right);
        }


}


    private void initView() {
        personal_sex = (Button) findViewById(R.id.personal_sex);
        personal_sex.setOnClickListener(this);
        personal_name = (EditText) findViewById(R.id.personal_name);
        personal_name.addTextChangedListener(this);
        personal_birth = (Button) findViewById(R.id.personal_birth);
        personal_birth.setOnClickListener(this);
        personal_height = (Button) findViewById(R.id.personal_hight);
        personal_height.setOnClickListener(this);
        personal_weight = (Button) findViewById(R.id.personal_weight);
        personal_weight.setOnClickListener(this);
        bt_return = (TextView) findViewById(R.id.material_renturn);
        bt_return.setOnClickListener(this);
        personal_finish = (Button) findViewById(R.id.personal_finish);
        personal_finish.setOnClickListener(this);

        TextView toMainText = (TextView) findViewById(R.id.personal_toMain);
        ImageView toMainImage = (ImageView) findViewById(R.id.personal_right_arrow);
        bottomLine = (RelativeLayout) findViewById(R.id.personal_bottom_line);

        toMainText.setOnClickListener(this);
        toMainImage.setOnClickListener(this);

        personal_sex_check = (ImageButton) findViewById(R.id.personal_sex_check);
        personal_name_check = (ImageButton) findViewById(R.id.personal_name_check);
        personal_birth_check = (ImageButton) findViewById(R.id.personal_birth_check);
        personal_hight_check = (ImageButton) findViewById(R.id.personal_hight_check);
        personal_weight_check = (ImageButton) findViewById(R.id.personal_weight_check);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_sex:
                sexPopupWindow();
                break;
            case R.id.personal_birth:
                birthDialog();
                break;
            case R.id.personal_hight:
                showHeightPopup();
                String a = (String) personal_birth.getText();
                System.out.println("aaaa-------" + a);
                break;
            case R.id.personal_weight:
                showWeightPopup();
                break;
            case R.id.personal_finish:
                saveValue();
                break;
            case R.id.material_renturn:
                finish();
                break;
            case R.id.personal_toMain:
            case R.id.personal_right_arrow:
                toMainAcitivity();
                finish();
                break;

        }
    }

    String sexValue;
    String birthValue;
    String heightValue;
    String weightValue;
    String nickName;

    private void saveValue() {
        if (!isNameLegal) {
            Toast.makeText(PersonalInfoActivity.this, "姓名格式不正确", Toast.LENGTH_SHORT).show();
            return;
        } else if (personal_sex.getText().equals("请选择性别")) {
            Toast.makeText(PersonalInfoActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        } else if (personal_birth.getText().equals("请选择出生日期")) {
            Toast.makeText(PersonalInfoActivity.this, "请选择出生日期", Toast.LENGTH_SHORT).show();
            return;
        } else if (personal_height.getText().equals("请选择身高")) {
            Toast.makeText(PersonalInfoActivity.this, "请选择身高", Toast.LENGTH_SHORT).show();
            return;
        } else if (personal_weight.getText().equals("请选择体重")) {
            Toast.makeText(PersonalInfoActivity.this, "请选择体重", Toast.LENGTH_SHORT).show();
            return;
        } else {
            sexValue = (String) personal_sex.getText();
            birthValue = (String) personal_birth.getText();
            heightValue = (String) personal_height.getText();
            weightValue = (String) personal_weight.getText();
            nickName = personal_name.getText().toString();

            Log.w(TAG, "saveValue: 测试个人信息是否为空" + personal_name.getText().toString());

            //  PreferenceSettings.getInstance(getApplicationContext()).setBodyWeight(Float.parseFloat(weightValue.substring(0, 2)));


            UserBean userBean = new UserBean();
            userBean.setId(shared.getInt("id"));
            userBean.setAccount(shared.getString("account"));
            userBean.setNickname(personal_name.getText().toString().trim());
            userBean.setSex(personal_sex.getText().toString().trim());
            userBean.setBirth(personal_birth.getText().toString().trim());
            userBean.setHeight(personal_height.getText().toString().trim().split("cm")[0]);
            userBean.setWeight(personal_weight.getText().toString().trim().split("kg")[0]);
            HttpImpl.getInstance().userUpdata(userBean);
//            Toast.makeText(PersonalInfoActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 性别popup
     */
    private void sexPopupWindow() {
        mMySexPopupWindow = new MySexPopupWindow(PersonalInfoActivity.this, itemsOnClick);
      //  mMySexPopupWindow.setBackgroundDrawable(null);
        mMySexPopupWindow.showAtLocation(this.findViewById(R.id.materia), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

    }


    /**
     * 生日popup
     */
    private void birthDialog() {
        // 选择日期
        int[] date = null;
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        if (mYear == 0 && mMonth == 0 && mDay == 0) {
            String curDate = CalendarDateUtils.getCurrentDate();
            date = CalendarDateUtils.getYMDArray(curDate, "-");
            if (date != null) {
                mYear = date[0] - 20;
                mMonth = date[1];
                mDay = date[2];
            }
        }


        dateDialog = new BirthDateDialog(PersonalInfoActivity.this, new BirthDateDialog.PriorityListener() {

            @Override
            public void refreshPriorityUI(String year, String month, String day, BirthDateDialog.CallBack back) {
                mYear = Integer.parseInt(year);
                mMonth = Integer.parseInt(month);
                mDay = Integer.parseInt(day);
                String st = mYear + "年" + mMonth + "月" + mDay + "日0时0分0秒";
                String et = mYear + "年" + mMonth + "月" + mDay + "日23时59分59秒";

                back.execute();
            }

        }, new BirthDateDialog.CallBack() {

            public void execute() {

                personal_birth.setText(mYear + "-" + mMonth + "-" + mDay + " ");
            }
        }, mYear, mMonth, mDay, width, height, "选择日期", 1);

      //  dateDialog.setBackgroundDrawable(null);
        dateDialog.showAtLocation(this.findViewById(R.id.materia), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    /**
     * 身高popup
     */
    private void showHeightPopup() {
        myHeightPopupWindow = new MyHeightPopupWindow(PersonalInfoActivity.this, itemsOnClick);
      //  myHeightPopupWindow.setBackgroundDrawable(null);
        myHeightPopupWindow.showAtLocation(this.findViewById(R.id.materia), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

    }

    /**
     * 体重popup
     */
    private void showWeightPopup() {
        myWeightPopupWindow = new MyWeightPopupWindow(PersonalInfoActivity.this, itemsOnClick);
        //myWeightPopupWindow.setBackgroundDrawable(null);
        myWeightPopupWindow.showAtLocation(this.findViewById(R.id.materia), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {

            System.out.println("mHeight()+++++" + mHeight);
            switch (v.getId()) {
                case R.id.sex_man:
                    personal_sex.setText("男");
                    mMySexPopupWindow.dismiss();
                    personal_sex_check.setBackgroundResource(R.drawable.right);
                    break;
                case R.id.sex_woman:
                    personal_sex.setText("女");
                    mMySexPopupWindow.dismiss();
                    break;
                case R.id.sex_cancel:
                    personal_sex.setText("性别");
                    mMySexPopupWindow.dismiss();
                    break;
                case R.id.height_cancel:
                    myHeightPopupWindow.dismiss();
                    break;
                case R.id.height_sure:
                    if (mHeight == null) {
                        Toast.makeText(PersonalInfoActivity.this, "请选择身高", Toast.LENGTH_SHORT).show();
                    } else {
                        personal_height.setText(mHeight);
                        personal_hight_check.setBackgroundResource(R.drawable.right);
                        myHeightPopupWindow.dismiss();
                    }
                    break;
                case R.id.btn_weight_sure:
                    if (mWeight == null) {
                        Toast.makeText(PersonalInfoActivity.this, "请选择体重", Toast.LENGTH_SHORT).show();
                    } else {
                        personal_weight.setText(mWeight);
                        personal_weight_check.setBackgroundResource(R.drawable.right);
                        myWeightPopupWindow.dismiss();
                    }
                    break;
                case R.id.btn_weight_cancel:
                    myWeightPopupWindow.dismiss();
                    break;
            }
        }
    };

    /**
     * 更换出生日期状态方法
     */
    public void changeBirth() {
        personal_birth_check.setBackgroundResource(R.drawable.right);
    }


    public void setMaterialHeight(String height) {
        this.mHeight = height;
    }

    public void setMaterialWeight(String weight) {
        this.mWeight = weight;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


    @Subscribe
    public void onUpdataResult(CommonUserUpdataResult result) {
        int state = result.getState();
        String msg = result.getMsg();
        Log.d(TAG, "onUpdataResult: 状态state:" + state);
        switch (state) {
            case SUCESS:
                shared.setString("nickname", nickName);
                shared.setString("sex", sexValue);
                shared.setString("birth", birthValue);
                shared.setString("height", heightValue.trim().split("cm")[0]);
                shared.setString("weight", weightValue.trim().split("kg")[0]);
                shared.setString("isExist", "true");
                //提交成功 跳转至首页
                Toast.makeText(PersonalInfoActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                if (isFromRegActivity) {
                    toMainAcitivity();
                }
                finish();
                break;
            default:
                Toast.makeText(PersonalInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void toMainAcitivity() {
        Intent intent = new Intent(PersonalInfoActivity.this, MainActivity.class);
        this.startActivity(intent);
    }


    @Override
    public void afterTextChanged(Editable s) {
        String regEx = "[a-zA-Z\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5]{1,9}";
        //是否正常
        if (!TextUtils.isEmpty(personal_name.getText())) {
            //判断长度
            if (personal_name.getText().toString().matches(regEx)) {
                isNameLegal = true;
                personal_name_check.setBackgroundResource(R.drawable.right);
            } else {
                isNameLegal = false;
                personal_name_check.setBackgroundResource(R.drawable.wrong);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
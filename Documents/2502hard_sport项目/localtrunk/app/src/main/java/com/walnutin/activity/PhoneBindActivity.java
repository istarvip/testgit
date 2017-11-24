package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.walnutin.entity.UserBean;
import com.walnutin.eventbus.CommonUserResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.Config;
import com.walnutin.util.MD5Util;
import com.walnutin.util.Utils;
import com.walnutin.view.TopTitleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

/**
 * 作者：MrJiang on 2016/6/28 18:38
 */
public class PhoneBindActivity extends BaseActivity implements TextWatcher {

    AQuery aQuery;
    EditText edtPwd;
    EditText edtPhone;
    TextView txtGetCode;
    String oldPhone = null;
    boolean sms =false;
    String passValidPhone = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindphone);
        aQuery = new AQuery(this);
        edtPwd = (EditText) findViewById(R.id.phone_eidtPassword);
        edtPhone = (EditText) findViewById(R.id.phone_editPhone);
        edtPhone.addTextChangedListener(this);
        edtPwd.addTextChangedListener(this);
        txtGetCode = (TextView) findViewById(R.id.phone_sendVerification);
        txtGetCode.setClickable(false);
        countTime = new CountTime(60 * 1000, 1000);
        aQuery.find(R.id.phone_binding).clicked(this, "bindPhone");
        aQuery.find(R.id.phone_sendVerification).clicked(this, "sendValidCode");
        aQuery.find(R.id.phone_showPassword).clicked(this, "showPwd");
        initEvent();

        oldPhone = getIntent().getStringExtra("phone");

    }

    private void initEvent() {

        SMSSDK.initSDK(this, Config.SMS_APPKEY, Config.SMS_SECRET, false);
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
        EventBus.getDefault().register(this);
    }

    //  phone_editPhone
    // phone_eidtPassword
    //phone_editVerification
    //phone_sendVerification
    // phone_showPassword
    public void bindPhone(View view) {
        if (!Utils.isMobileNO(aQuery.id(R.id.phone_editPhone).getText().toString().trim()) || edtPwd.getText().length() < 6) {
            Toast.makeText(getApplicationContext(), "手机号码或者密码不规范", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aQuery.id(R.id.phone_editVerification).getText().toString().trim().length() != 4) {
            Toast.makeText(getApplicationContext(), "请输入4位验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(sms ==false) {
            SMSSDK.submitVerificationCode("86", phone, aQuery.id(R.id.phone_editVerification).getText().toString().trim());
        }
        else{
            submit();
        }

    }

    String phone = "";
    CountTime countTime;

    public void sendValidCode(View view) {
        SMSSDK.getVerificationCode("86", edtPhone.getText().toString().trim());
        phone = edtPhone.getText().toString();
        view.setClickable(false);
        countTime.start();
    }

    int type = 0;
    public static final int HIDE_PWD = 0;
    public static final int SHOW_PWD = 1;

    public void showPwd(View view) {
        if (type == HIDE_PWD) {
            edtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edtPwd.setText(edtPwd.getText());
            type = SHOW_PWD;
            //    aQuery.id(R.id.login_showPwd).image(R.drawable.hidepassward);
            aQuery.id(R.id.phone_showPassword).background(R.drawable.showpassward);
        } else {
            edtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edtPwd.setText(edtPwd.getText());
            //      aQuery.id(R.id.login_showPwd).image(R.drawable.showpassward);
            aQuery.id(R.id.phone_showPassword).background(R.drawable.hidepassward);
            type = HIDE_PWD;
        }
    }

    private void submit() {
//        HttpImpl.getInstance().userRegister(aQuery.id(R.id.phone_eidtPassword).getText().toString().trim(),
//                MD5Util.MD5(MD5Util.MD5(edtPwd.getText().toString())));
        //新增绑定

        sms = true;
        passValidPhone = edtPhone.getText().toString().trim();

        if (oldPhone == null) {
            Gson gson = new Gson();
            UserBean userBean = new UserBean();
            userBean.setType(Config.Phone);
            userBean.setOpenid(edtPhone.getText().toString().trim());
            userBean.setAccount(MyApplication.account);
            HttpImpl.getInstance().addPhoneBind(MD5Util.MD5(MD5Util.MD5(edtPwd.getText().toString().trim())), gson.toJson(userBean));
        } else {      // 改变绑定手机
            HttpImpl.getInstance().changeBindedPhone(oldPhone, edtPhone.getText().toString().trim(),
                    MD5Util.MD5(MD5Util.MD5(edtPwd.getText().toString().trim())));
        }
    }


    @Subscribe
    public void onResultChangeBind(CommonUserResult.CommonChangeBindPhoneResult result) {
        if (result.state == 0) {
            Intent intent = new Intent();
            intent.putExtra("newPhone", edtPhone.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), result.msg, Toast.LENGTH_SHORT).show();
        }
    }


    @Subscribe
    public void onResultAddBind(CommonUserResult.CommonChangeBindPhoneResult result) {
        if (result.state == 0) {
            Intent intent = new Intent();
            intent.putExtra("newPhone", edtPhone.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), result.msg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
        EventBus.getDefault().unregister(this);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result" + event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    //     Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    submit();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    //     Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PhoneBindActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        //      Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        }
    };

    int timeInit = 59;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (Utils.isMobileNO(aQuery.id(R.id.phone_editPhone).getText().toString().trim()) && edtPwd.getText().length() >= 6) {
            txtGetCode.setTextColor(getResources().getColor(R.color.red_background_notselected));
            if (timeInit == 59)
                txtGetCode.setClickable(true);
            //   System.out.println("text  true");
        } else {
            txtGetCode.setTextColor(getResources().getColor(R.color.half_gray));
            txtGetCode.setClickable(false);
        }
        sms =false;

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (sms == true) {
            if (!passValidPhone.equals(edtPhone.getText().toString().trim())) {
                sms = false;
            }
        }
    }

    class CountTime extends CountDownTimer {

        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            aQuery.id(R.id.phone_sendVerification).text(String.valueOf(timeInit));
            timeInit--;
        }

        @Override
        public void onFinish() {
            timeInit = 59;
            aQuery.id(R.id.phone_sendVerification).text(R.string.click_get_code);
            aQuery.id(R.id.phone_sendVerification).clickable(true);
        }
    }
}

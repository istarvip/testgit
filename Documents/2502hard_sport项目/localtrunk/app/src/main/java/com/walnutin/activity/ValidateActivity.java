package com.walnutin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.walnutin.eventbus.CommonResetPwdResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.Config;
import com.walnutin.util.MD5Util;
import com.walnutin.util.MySharedPf;
import com.walnutin.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class ValidateActivity extends Activity implements TextWatcher{
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    EditText edtPhone;
    MySharedPf preferenceSettings;
    CountTime countTime;
    EditText edtFirstPwd;
    EditText edtSecondPwd;
    TextView txtGetCode;
    boolean sms = false;
    String passValidPhone = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        aQuery = new AQuery(this);
        countTime = new CountTime(60 * 1000, 1000);
        initview();
        initEvent();
    }

    void initview() {
        aQuery.id(R.id.validate_ok).clicked(this, "submit");
        aQuery.id(R.id.validate_getValidateCode).clicked(this, "getCode");
        aQuery.id(R.id.validate_back).clicked(this, "back");
        txtGetCode = (TextView) findViewById(R.id.validate_getValidateCode);
        edtPhone = (EditText) findViewById(R.id.validate_phoneNum);
        edtFirstPwd = (EditText) findViewById(R.id.validate_firstPwd);
        edtSecondPwd = (EditText) findViewById(R.id.validate_secondPwd);
        edtPhone.addTextChangedListener(this);
        txtGetCode.setClickable(false);
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

    public void back(View v) {
        finish();
    }

    public void submit(View v){
        if(edtFirstPwd.getText().toString().equals(edtSecondPwd.getText().toString()) &&edtFirstPwd.getText().length()>=6){
            SMSSDK.submitVerificationCode("86", phone, aQuery.id(R.id.validate_getValidateCode).getText().toString());

        }else{
            Toast.makeText(getApplicationContext(),"密码输入不合法",Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void valideSubmit() {
        sms = true;
        passValidPhone = edtPhone.getText().toString().trim();
        HttpImpl.getInstance().userResetPwd(edtPhone.getText().toString(), MD5Util.MD5(edtFirstPwd.getText().toString()));
    }

    String phone = "";
    public void getCode(View v){
        SMSSDK.getVerificationCode("86", aQuery.id(R.id.validate_phoneNum).getText().toString());
        phone = aQuery.id(R.id.validate_phoneNum).getText().toString();
        v.setClickable(false);
        countTime.start();
    }



    @Subscribe(threadMode = ThreadMode.POSTING, sticky = false, priority = 0)
    public void onResultValide(CommonResetPwdResult result) {

        int state = result.getState();
        String msg = result.getMsg();
        switch (state){
            case 0:
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
                preferenceSettings.setString("password",null);
                finish();
                break;
            case 1:
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(this,"连接服务器失败",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(Utils.isMobileNO(edtPhone.getText().toString())){
            txtGetCode.setTextColor(getResources().getColor(R.color.font_defaults));
            if(timeInit == 59 )
               txtGetCode.setClickable(true);
        }else{
            txtGetCode.setTextColor(getResources().getColor(R.color.group_line_e5));
            txtGetCode.setClickable(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (sms == true) {
            if (!passValidPhone.equals(edtPhone.getText().toString().trim())) {
                sms = false;
            }
        }
    }

    int timeInit = 59;

    class CountTime extends CountDownTimer {

        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            aQuery.id(R.id.validate_getValidateCode).text(String.valueOf(timeInit));
            timeInit--;
        }

        @Override
        public void onFinish() {
            timeInit = 59;
            aQuery.id(R.id.validate_getValidateCode).text(R.string.click_get_code);
            aQuery.id(R.id.validate_getValidateCode).clickable(true);
        }
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
              //      Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    valideSubmit();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                //    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ValidateActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                     //   Toast.makeText(ValidateActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
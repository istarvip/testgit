package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.walnutin.eventbus.CommonRegisterResult;
import com.walnutin.eventbus.CommonloginResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.entity.UserBean;
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

public class RegisterActivity extends Activity implements TextWatcher {
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    public static final int REGISTER_RESULT_CODE = 101;
    CountTime countTime;
    EditText edtPhone;
    EditText edtPwd;
    TextView txtGetCode;
    private boolean isHidePwd;
    private MySharedPf preferenceSettings;
    private final static String TAG = "RegisterActivity";
    boolean sms = false;
    String passValidPhone = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        aQuery = new AQuery(this);
        countTime = new CountTime(60 * 1000, 1000);
        initview();

        initEvent();
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

    @Subscribe(priority = 0, sticky = false, threadMode = ThreadMode.POSTING)
    public void onRegisterResult(CommonRegisterResult result) {
        String msg = result.getMsg();
        int state = result.getState();
        //        暂时屏蔽测试
        Toast.makeText(this, "注册： state: " + state + "msg:" + msg, Toast.LENGTH_LONG).show();
        switch (state) {
            case 0:
                Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
                //自动登陆并跳转到首页
                preferenceSettings = MySharedPf.getInstance(this);
                if (edtPhone.getText() != null && edtPwd.getText() != null) {
                    Log.w(TAG, "register: 都不空");
                    preferenceSettings.setString("account", edtPhone.getText().toString().trim());
                    preferenceSettings.setString("password", MD5Util.MD5(MD5Util.MD5(edtPwd.getText().toString().trim())));
                    setResult(REGISTER_RESULT_CODE);
                }
                login();
                break;
            case 1:
                Toast.makeText(this, "手机号已被注册", Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(this, "参数错误", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(this, "服务器异常", Toast.LENGTH_LONG).show();
                break;
            case -1:
                Toast.makeText(this, "连接服务器失败", Toast.LENGTH_SHORT).show();
        }
    }

    void initview() {
        //      Toast.makeText(getApplicationContext(),"-------------",Toast.LENGTH_SHORT).show();
        //  aQuery.id(R.id.register_register).clicked(this,"login");
        aQuery.id(R.id.register_register).clicked(this, "register");
        aQuery.id(R.id.register_back).clicked(this, "back");
        aQuery.id(R.id.register_getValidataCode).clicked(this, "getCode");
        aQuery.id(R.id.registe_showPwd).clicked(this, "changeShowPwd");
        edtPhone = (EditText) findViewById(R.id.registe_phonenum);
        edtPwd = (EditText) findViewById(R.id.register_pwd);
        txtGetCode = (TextView) findViewById(R.id.register_getValidataCode);
        txtGetCode.setClickable(false);
        edtPhone.addTextChangedListener(this);
        edtPwd.addTextChangedListener(this);
        changeShowPwd();
        //  HttpUtils http = new HttpUtils();
    }

    private void submitRegister() {
        sms = true;
        passValidPhone = edtPhone.getText().toString().trim();
        HttpImpl.getInstance().userRegister(passValidPhone, MD5Util.MD5(MD5Util.MD5(edtPwd.getText().toString())));
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

                    submitRegister();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    //     Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
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

    public void register(View v) {
//        if(aQuery.id(R.id.register_inputvalidateCode).getText().length()!=4){
//            Toast.makeText(this,"验证码错误",Toast.LENGTH_SHORT).show();
//            return;
//        }
        //  HttpImpl.getInstance().userRegister(edtPhone.getText().toString(),ed);


        //修改
        if (sms == false) {
            SMSSDK.submitVerificationCode("86", phone, aQuery.id(R.id.register_inputvalidateCode).getText().toString());
        } else {
            submitRegister();
        }

        //跳转至设置个人资料页面
//        Intent intent = new Intent();
//        intent.setClass(getApplicationContext(), PersonalInfoActivity.class);
//        intent.putExtra("isFromReg", true);
//        startActivity(intent);

    }

    public void back(View v) {
        finish();
    }

    String phone = "";

    public void getCode(View v) {
        SMSSDK.getVerificationCode("86", aQuery.id(R.id.registe_phonenum).getText().toString());
        phone = aQuery.id(R.id.registe_phonenum).getText().toString();
        v.setClickable(false);
        countTime.start();
    }

    int timeInit = 59;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (Utils.isMobileNO(edtPhone.getText().toString()) && edtPwd.getText().length() >= 6) {
            txtGetCode.setTextColor(getResources().getColor(R.color.red_background_notselected));
            if (timeInit == 59)
                txtGetCode.setClickable(true);
            //   System.out.println("text  true");
        } else {
            txtGetCode.setTextColor(getResources().getColor(R.color.half_gray));
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


    /**
     * 点击眼睛是否显示密码
     */
    public void changeShowPwd() {
        if (isHidePwd) {
            //明文显示
            edtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            edtPwd.setSelection(edtPwd.length());//设置光标显示
            aQuery.id(R.id.registe_showPwd).background(R.drawable.showpassward);
            isHidePwd = false;
        } else {
            edtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edtPwd.setSelection(edtPwd.length());//设置光标显示
            aQuery.id(R.id.registe_showPwd).background(R.drawable.hidepassward);
            isHidePwd = true;
        }

    }


    public void login() {
        String phone;
        String pwd;
        phone = preferenceSettings.getString("account");
        pwd = preferenceSettings.getString("password");

        if (Utils.isMobileNO(phone) && pwd.length() >= 6) {
            HttpImpl.getInstance().userLogin(phone, pwd);

        } else {
            Log.w(TAG, "login: 用户名密码错误");
        }

    }


    @Subscribe(priority = 0, sticky = false, threadMode = ThreadMode.POSTING)
    public void onLoginResult(CommonloginResult result) {
        int state = result.getState();
        String msg = result.getMsg();
        String sessionid = result.getSessionids();
        UserBean user = result.getUser();
        System.out.println("loginstate: " + state);
        Log.w(TAG, "onLoginResult:登录返回状态 state " + state);
        switch (state) {
            case 0:
                preferenceSettings.setInt("id", user.getId());
                preferenceSettings.setString("account", user.getAccount());
                preferenceSettings.setString("sex", user.getSex());
                preferenceSettings.setString("applytime", user.getApplytime());
                preferenceSettings.setString("birth", user.getBirth());
                preferenceSettings.setString("headimage", user.getHeadimage());
                preferenceSettings.setString("height", user.getHeight());
                preferenceSettings.setString("mobile", user.getMobile());
                preferenceSettings.setString("nickname", user.getNickname());
                preferenceSettings.setString("property", user.getProperty());
                preferenceSettings.setString("password", MD5Util.MD5(MD5Util.MD5(edtPwd.getText().toString().trim())));
                preferenceSettings.setString("weight", user.getWeight());
                preferenceSettings.setSessionID(sessionid);

                //TODO:跳转至设置个人资料页面
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), PersonalInfoActivity.class);
                intent.putExtra("isFromReg", true);
                intent.putExtra("userId", user.getId());
                startActivity(intent);

                finish();
                break;
            case 1:
            case 2:
            case 3:
            case -1:
            default:
                Toast.makeText(this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    class CountTime extends CountDownTimer {

        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            aQuery.id(R.id.register_getValidataCode).text(String.valueOf(timeInit));
            timeInit--;
        }

        @Override
        public void onFinish() {
            timeInit = 59;
            aQuery.id(R.id.register_getValidataCode).text(R.string.acquire_verification);
            aQuery.id(R.id.register_getValidataCode).clickable(true);
        }
    }
}
package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidquery.AQuery;

import com.umeng.analytics.MobclickAgent;
import com.walnutin.eventbus.CommonloginResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.entity.UserBean;
import com.walnutin.Presenter.LoginPresenter;
import com.walnutin.ViewInf.LoginView;
import com.walnutin.util.Config;
import com.walnutin.util.LoadDataDialog;
import com.walnutin.util.MD5Util;
import com.walnutin.util.MySharedPf;
import com.walnutin.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends Activity implements PlatformActionListener, LoginView {
    public static final int REGISTER_RESULT_CODE = 101;
    public static final int HIDE_PWD = 0;
    public static final int SHOW_PWD = 1;
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    EditText edtPhone;
    EditText edtPwd;
    ImageButton showPwd;
    MySharedPf preferenceSettings;
    int type = 0;
    private final static String TAG = "LoginActivity";
    private boolean isAutoLogin;
    String openId;
    String token;
    LoginPresenter loginPresenter;
    LoadDataDialog loadDataDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Login loginActivity: " + this + " " + System.currentTimeMillis());
        setContentView(R.layout.activity_login);
        loginPresenter = new LoginPresenter(this);
        aQuery = new AQuery(this);
        preferenceSettings = MySharedPf.getInstance(this);


        //临时
//        preferenceSettings.setToken(null);
//        preferenceSettings.setString("password", null);

        initview();
        initEvent();
        autoLogin();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);       //统计时长
    }

    private void autoLogin() {
        if (!TextUtils.isEmpty(loginPresenter.getLocalAccount()) && !TextUtils.isEmpty(loginPresenter.getLocalPwd())) {

            isAutoLogin = true;
            aQuery.id(R.id.login_phonenum).text(loginPresenter.getLocalAccount());
            //     login(null);
        } else if (!TextUtils.isEmpty(loginPresenter.getLocalAccount()) &&
                TextUtils.isEmpty(loginPresenter.getLocalPwd())) {
            aQuery.id(R.id.login_phonenum).text(loginPresenter.getLocalAccount());
        }


    }

    private void initEvent() {
        EventBus.getDefault().register(this);
    }

    void initview() {
        aQuery.id(R.id.login_login).clicked(this, "login");
        aQuery.id(R.id.login_register).clicked(this, "register");
        aQuery.id(R.id.third_qq).clicked(this, "qq_login");
        aQuery.id(R.id.third_wechat).clicked(this, "weChat_login");
        aQuery.id(R.id.third_weibo).clicked(this, "weibo_login");
        aQuery.id(R.id.login_forgetPwd).clicked(this, "forget_pwd");
        aQuery.id(R.id.login_showPwd).clicked(this, "show_pwd");
        edtPhone = (EditText) findViewById(R.id.login_phonenum);
        edtPwd = (EditText) findViewById(R.id.login_pwd);
        showPwd = (ImageButton) findViewById(R.id.login_showPwd);
    }

    public void login(View paramView) {
        //  Toast.makeText(getApplicationContext(),"queding",Toast.LENGTH_SHORT).show();
        Log.w(TAG, "login: 次数");
        String phone;
        String pwd;
        if (isAutoLogin) {
            phone = loginPresenter.getAccount();
            pwd = loginPresenter.getLocalPwd();
        } else {
            phone = aQuery.id(R.id.login_phonenum).getText().toString();
            pwd = edtPwd.getText().toString().trim();
        }
        if (pwd != null && pwd.length() <= 30) {
            pwd = MD5Util.MD5(MD5Util.MD5(pwd));
        }
        if (Utils.isMobileNO(phone) && pwd.length() >= 6) {
            showDialog();
            HttpImpl.getInstance().userLogin(phone, pwd);

        } else {
            Toast.makeText(this, "用户名或者密码输入不合法", Toast.LENGTH_SHORT).show();
        }

    }

    public void register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_RESULT_CODE);
    }

    public void show_pwd(View paramView) {
        changeShowPwd();
    }

    private void changeShowPwd() {
        if (type == HIDE_PWD) {
            edtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edtPwd.setText(edtPwd.getText());
            type = SHOW_PWD;
            //    aQuery.id(R.id.login_showPwd).image(R.drawable.hidepassward);
            showPwd.setBackgroundResource(R.drawable.showpassward);
        } else {
            edtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edtPwd.setText(edtPwd.getText());
            //      aQuery.id(R.id.login_showPwd).image(R.drawable.showpassward);
            showPwd.setBackgroundResource(R.drawable.hidepassward);
            type = HIDE_PWD;
        }
    }

    public void forget_pwd(View paramView) {
        Intent intent = new Intent(LoginActivity.this, ValidateActivity.class);
        startActivity(intent);
    }

    void showDialog() {
        loadDataDialog = new LoadDataDialog(LoginActivity.this, "login");
        loadDataDialog.show();
        loadDataDialog.setCanceledOnTouchOutside(false);
    }

    void disMissDialog() {
        if (loadDataDialog.isShowing()) {
            loadDataDialog.dismiss();
        }
    }

    @Subscribe(priority = 0, sticky = false, threadMode = ThreadMode.POSTING)
    public void onLoginResult(CommonloginResult result) {
        int state = result.getState();
        String msg = result.getMsg();
        disMissDialog();
        switch (state) {
            case 0:
                token = result.getToken();
                Log.w("login  测试登", "onLoginResult: this:"+this+" token:"+token);
                UserBean user = result.getUser();
                MobclickAgent.onProfileSignIn(user.getAccount());
                MyApplication.token = token.trim();
                loginPresenter.setLoginType();
                loginPresenter.setOpenId();
                loginPresenter.setToken();
                loginPresenter.saveLoginInfo(user);
                if (!isAutoLogin) {
                    preferenceSettings.setString("password", MD5Util.MD5(MD5Util.MD5(edtPwd.getText().toString().trim())));
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                this.startActivity(intent);
                finish();
                break;
            case 1:
            case 2:
                isAutoLogin = false;
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case 3:
                isAutoLogin = false;
                Toast.makeText(this, "服务器异常", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                isAutoLogin = false;
                Toast.makeText(this, "连接超时", Toast.LENGTH_SHORT).show();
                break;
            default:
                isAutoLogin = false;
                Toast.makeText(this, "连接超时", Toast.LENGTH_SHORT).show();
                break;
        }
        //   System.out.println(user.getUserid()+" username: "+user.getNickname());
    }

    Platform platform;

    public void qq_login(View v) {
        System.out.println("time start:" + System.currentTimeMillis());
        platform = ShareSDK.getPlatform(QQ.NAME);
        Config.CurrentPlat = 1;
        authorize(platform);
    }

    public void weChat_login(View v) {
        platform = ShareSDK.getPlatform(Wechat.NAME);
        Config.CurrentPlat = 2;
        authorize(platform);
    }

    public void weibo_login(View v) {
        platform = ShareSDK.getPlatform(SinaWeibo.NAME);
        Config.CurrentPlat = 3;
        authorize(platform);
    }

    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }
        platform.removeAccount();
        if (plat.isAuthValid()) {
            thirdLogin(plat);
            return;
        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    public boolean thirdLogin(Platform plat) {
        openId = plat.getDb().getUserId();
        if (openId != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog();
                }
            });
            //    showDialog();
            loginPresenter.thirdLogin(plat, Config.CurrentPlat);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: REGISTER_RESULT_CODE:" + REGISTER_RESULT_CODE);


    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        if (platform.getName().equals(QQ.NAME)) {
            Config.CurrentPlat = 1;
        } else if (platform.getName().equals(Wechat.NAME)) {
            Config.CurrentPlat = 2;
        } else if (platform.getName().equals(SinaWeibo.NAME)) {
            Config.CurrentPlat = 3;
        }
        thirdLogin(platform);

        return;

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        //   Toast.makeText(getApplicationContext(),platform.getDb().getUserIcon(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(getApplicationContext(), "授权取消", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getLoginPlatType() {
        return Config.CurrentPlat;
    }

    @Override
    public String getOpenId() {
        return openId;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getAccount() {
        return edtPhone.getText().toString();
    }

    @Override
    public String getPassWord() {
        return edtPwd.getText().toString();
    }

    @Override
    public String getThirdInfo() {
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
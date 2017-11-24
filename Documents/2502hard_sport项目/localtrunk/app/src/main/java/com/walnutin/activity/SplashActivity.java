package com.walnutin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.androidquery.AQuery;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.eventbus.CommonloginResult;
import com.walnutin.hard.R;
import com.walnutin.entity.UserBean;
import com.walnutin.Presenter.LoginPresenter;
import com.walnutin.ViewInf.LoginView;
import com.walnutin.util.Config;
import com.walnutin.util.LoadDataDialog;
import com.walnutin.util.MySharedPf;
import com.walnutin.view.TopTitleLableView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class SplashActivity extends Activity implements LoginView {
    /**
     * Called when the activity is first created.
     */
    AQuery aQuery;
    Button btnLogin;
    String openId;
    String token;
    String thirdInfo;
    MySharedPf preferenceSettings;
    int type = -1;
    private long mBeginTime;
    private static final String TAG = "SplashActivity";
    LoadDataDialog loadDataDialog;

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            //     System.out.println("Login Handle:" + " msg: " + msg.what + "  time" + System.currentTimeMillis());
            if (msg.what == 1) {
                //自动登录
                autoLogin();
                // toLonginActivity();
            }
        }
    };
    private long mStartTime;


    private void autoLogin() {
        if (loginPresenter.getLocalType() != -1 && loginPresenter.getLocalOpenId() != null) {
            Platform platform = null;
            type = loginPresenter.getLocalType();
            switch (type) {
                case Config.QQ:
                    platform = ShareSDK.getPlatform(QQ.NAME);
                    thidlogin(platform, type);
                    break;
                case Config.WeChat:
                    platform = ShareSDK.getPlatform(Wechat.NAME);
                    thidlogin(platform, type);
                    break;
                case Config.WeiBo:
                    platform = ShareSDK.getPlatform(SinaWeibo.NAME);
                    thidlogin(platform, type);
                    break;
            }
        } else if (!TextUtils.isEmpty(loginPresenter.getLocalAccount()) &&
                !loginPresenter.getLocalAccount().equals("null") &&
                !TextUtils.isEmpty(loginPresenter.getLocalPwd()) &&
                !loginPresenter.getLocalPwd().equals("null")) {
            String account = loginPresenter.getLocalAccount();
            String password = loginPresenter.getLocalPwd();

            //  showDialog();
            loginPresenter.Login(account, password);
            //login(account, password);
        } else {
            //跳转到登录页面
            toLonginActivity();
        }
    }

    void showDialog() {
        loadDataDialog = new LoadDataDialog(this, "login");
        loadDataDialog.show();
        loadDataDialog.setCanceledOnTouchOutside(false);
    }

    void disMissDialog() {
        if (loadDataDialog.isShowing()) {
            loadDataDialog.dismiss();
        }
    }

    public boolean thidlogin(Platform plat, int type) {
        if (plat.isAuthValid()) {
            //     showDialog();
            openId = plat.getDb().getUserId();
            if (openId != null) {
                loginPresenter.thirdLogin(plat, type);
                return true;
            }
        }
        toLonginActivity();
        return false;
    }


    TopTitleLableView topTitleLableView;
    LoginPresenter loginPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);
        EventBus.getDefault().register(this);
        preferenceSettings = MySharedPf.getInstance(this);

        //临时
//        preferenceSettings.setToken(null);
//        preferenceSettings.setString("password", null);


        loginPresenter = new LoginPresenter(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 1000);

//        initView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Subscribe(priority = 100, sticky = false, threadMode = ThreadMode.POSTING)
    public void onLoginResult(CommonloginResult result) {
        int state = result.getState();
        String msg = result.getMsg();
        //    disMissDialog();
        switch (state) {
            case 0:
                token = result.getToken();
                Log.w("SplashActivity 测试登", "onLoginResult: this:"+this+" token:"+token);
                UserBean user = result.getUser();
                MobclickAgent.onProfileSignIn(user.getAccount());
                MyApplication.token = token.trim();
                loginPresenter.setOpenId();
                loginPresenter.setToken();
                loginPresenter.setLoginType();
                loginPresenter.saveLoginInfo(user);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                this.startActivity(intent);
                finish();
                break;
            default:
                //跳登录页
                //根据返回值决定跳首页还是跳登录页面
                toLonginActivity();
                //TODO:
                Log.w(TAG, "onLoginResult: 调用了default中的方法");
                break;
        }
        //   System.out.println(user.getUserid()+" username: "+user.getNickname());
    }

    private void toLonginActivity() {
        Intent intent2 = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent2);
        SplashActivity.this.finish();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.finish();
            }
        }, 1000);
    }

    @Override
    public int getLoginPlatType() {
        return type;
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
        return null;
    }

    @Override
    public String getPassWord() {
        return null;
    }

    @Override
    public String getThirdInfo() {
        return thirdInfo;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
    }
}
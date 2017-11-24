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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.walnutin.entity.ThirdInfo;
import com.walnutin.entity.UserBean;
import com.walnutin.eventbus.CommonBindThirdInfoResult;
import com.walnutin.eventbus.CommonResetPwdResult;
import com.walnutin.eventbus.CommonUserResult;
import com.walnutin.eventbus.CommonUserUpdataResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.Config;
import com.walnutin.util.LoadDataDialog;
import com.walnutin.util.MD5Util;
import com.walnutin.util.MySharedPf;
import com.walnutin.util.Utils;
import com.walnutin.view.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class BindThridActivity extends BaseActivity implements PlatformActionListener {

    AQuery aQuery;
    String QQ_OpenId = null;
    String WeChat_OpenId = null;
    String SinaWeiBo_OpenId = null;
    String phone = null;
    LoadDataDialog loadDataDialog;
    MySharedPf mySharedPf;
    int whichOper = -1;
    Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindinginfo);
        mySharedPf = MySharedPf.getInstance(this);
        aQuery = new AQuery(this);
        gson = new Gson();
        EventBus.getDefault().register(this);
        //     initview();
        initEvent();
        HttpImpl.getInstance().queryThirdBindInfo();
    }

    void initview() {

    }

    private void initEvent() {
        aQuery.id(R.id.third_qq).clicked(this, "bindQQ");
        aQuery.id(R.id.third_wechat).clicked(this, "bindWeChat");
        aQuery.id(R.id.third_weibo).clicked(this, "bindWeiBo");
        aQuery.id(R.id.bind_phoneNumber).clicked(this, "bindPhone");
    }

    private final static int REQUEST_CODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            phone = data.getStringExtra("newPhone");
            updateView();
        }
    }

    @Subscribe
    public void onResultBind(CommonBindThirdInfoResult result) {
        if (result.getState() == 0) {
            List<ThirdInfo> thirdInfo = result.getUserOpen();
            for (ThirdInfo info : thirdInfo) {
                if (info.type == Config.QQ) {
                    aQuery.id(R.id.third_qq).text("已绑定");
                    QQ_OpenId = info.getOpenid();
                } else if (info.type == Config.WeChat) {
                    aQuery.id(R.id.third_wechat).text("已绑定");
                    WeChat_OpenId = info.getOpenid();
                } else if (info.type == Config.WeiBo) {
                    aQuery.id(R.id.third_weibo).text("已绑定");
                    SinaWeiBo_OpenId = info.getOpenid();
                } else if (info.type == Config.Phone) {
                    phone = info.getOpenid();
                    aQuery.id(R.id.bind_phoneNumber).text(info.getOpenid());
                }
            }
        }

    }

    @Subscribe
    public void onBindOperResult(CommonUserResult.CommonAddBindThirdResult result) {
        if (result.state == 0) {
            updateView();
        } else {
            Toast.makeText(getApplicationContext(), result.msg, Toast.LENGTH_SHORT).show();
            switch (whichOper) {
                case Config.QQ:
                    QQ_OpenId = null;
                    break;
                case Config.WeChat:
                    WeChat_OpenId = null;
                    break;
                case Config.WeiBo:
                    SinaWeiBo_OpenId = null;
                    break;
            }
        }
        disMissDialog();

    }

    @Subscribe
    public void onUnBindOperResult(CommonUserResult.CommonUnBindThirdResult result) {
        if (result.state == 0) {
            Toast.makeText(getApplicationContext(), "解除成功", Toast.LENGTH_SHORT).show();
            switch (whichOper) {
                case Config.QQ:
                    QQ_OpenId = null;
                    aQuery.id(R.id.third_qq).text("未绑定");
                    //        ShareSDK.getPlatform(QQ.NAME).removeAccount();
                    break;
                case Config.WeChat:
                    WeChat_OpenId = null;
                    aQuery.id(R.id.third_wechat).text("未绑定");
                    //        ShareSDK.getPlatform(Wechat.NAME).removeAccount();
                    break;
                case Config.WeiBo:
                    SinaWeiBo_OpenId = null;
                    aQuery.id(R.id.third_weibo).text("未绑定");
                    //       ShareSDK.getPlatform(SinaWeibo.NAME).removeAccount();
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), result.msg, Toast.LENGTH_SHORT).show();
        }
        disMissDialog();

    }


    private void updateView() {
        switch (whichOper) {
            case Config.QQ:
                aQuery.id(R.id.third_qq).text("已绑定");
                break;
            case Config.WeChat:
                aQuery.id(R.id.third_wechat).text("已绑定");
                break;
            case Config.WeiBo:
                aQuery.id(R.id.third_weibo).text("已绑定");
                break;
            case Config.Phone:
                if (phone != null) {
                    aQuery.id(R.id.bind_phoneNumber).text(phone);
                }
                break;
        }
    }


    public void bindPhone(View view) {
        if (mySharedPf.getInt("type") == Config.Phone) {
            return;
        }
        whichOper = Config.Phone;
        if (phone != null) {
            final ConfirmDialog confirmDialog = new ConfirmDialog(this, "您已绑定,是否更改绑定手机号", "", "取消", "确定");
            confirmDialog.show();
            confirmDialog.setCanceledOnTouchOutside(true);
            confirmDialog.setOkListener(new ConfirmDialog.IOkListener() {
                @Override
                public void doOk() {
                    startBindPhone(phone);
                    confirmDialog.dismiss();
                }
            });
            confirmDialog.setCancellistener(new ConfirmDialog.ICancelListener() {
                @Override
                public void doCancel() {
                    confirmDialog.dismiss();
                }
            });
        } else {
            startBindPhone(null);
        }

    }

    void startBindPhone(String ph) {
        Intent intent = new Intent(BindThridActivity.this, PhoneBindActivity.class);
        intent.putExtra("phone", ph);
        startActivityForResult(intent, REQUEST_CODE);       // 修改绑定手机号码
    }

    public void bindQQ(View view) {
        if (mySharedPf.getInt("type") == Config.QQ) {
            return;
        }
        whichOper = Config.QQ;
        if (QQ_OpenId == null) {
            Platform platform = ShareSDK.getPlatform(QQ.NAME);
            authorize(platform);
        } else {
            final ConfirmDialog confirmDialog = new ConfirmDialog(this, "解除绑定", "", "取消", "确定");
            confirmDialog.show();
            confirmDialog.setCanceledOnTouchOutside(true);
            confirmDialog.setOkListener(new ConfirmDialog.IOkListener() {
                @Override
                public void doOk() {
                    UserBean userBean = new UserBean();
                    userBean.setAccount(MyApplication.account);
                    userBean.setType(Config.QQ);
                    userBean.setOpenid(QQ_OpenId);
                    HttpImpl.getInstance().unBindThird(gson.toJson(userBean));
                    confirmDialog.dismiss();
                    //       showDialog();

                }
            });
            confirmDialog.setCancellistener(new ConfirmDialog.ICancelListener() {
                @Override
                public void doCancel() {
                    confirmDialog.dismiss();
                }
            });
        }
    }

    private void showDialog() {
        if (loadDataDialog != null && loadDataDialog.isShowing()) {
            loadDataDialog.dismiss();
        }
        loadDataDialog = new LoadDataDialog(getApplicationContext(), "deal");
        loadDataDialog.setOwnerActivity(BindThridActivity.this);
        loadDataDialog.show();
        loadDataDialog.setCancelable(true);
    }

    private void disMissDialog() {
        if (loadDataDialog != null && loadDataDialog.isShowing()) {
            loadDataDialog.dismiss();
        }
    }

    public void bindWeChat(View view) {
        if (mySharedPf.getInt("type") == Config.WeChat) {
            return;
        }

        whichOper = Config.WeChat;

        if (WeChat_OpenId == null) {
            Platform platform = ShareSDK.getPlatform(Wechat.NAME);
            authorize(platform);
        } else {
            final ConfirmDialog confirmDialog = new ConfirmDialog(this, "解除绑定", "", "取消", "确定");
            confirmDialog.show();
            confirmDialog.setCanceledOnTouchOutside(true);
            confirmDialog.setOkListener(new ConfirmDialog.IOkListener() {
                @Override
                public void doOk() {
                    UserBean userBean = new UserBean();
                    userBean.setAccount(MyApplication.account);
                    userBean.setType(Config.WeChat);
                    userBean.setOpenid(WeChat_OpenId);
                    HttpImpl.getInstance().unBindThird(gson.toJson(userBean));
                    confirmDialog.dismiss();
                    //          showDialog();

                }
            });
            confirmDialog.setCancellistener(new ConfirmDialog.ICancelListener() {
                @Override
                public void doCancel() {
                    confirmDialog.dismiss();
                }
            });

        }
    }

    public void bindWeiBo(View view) {
        if (mySharedPf.getInt("type") == Config.WeiBo) {
            return;
        }
        whichOper = Config.WeiBo;
        if (SinaWeiBo_OpenId == null) {
            Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
            authorize(platform);
        } else {
            final ConfirmDialog confirmDialog = new ConfirmDialog(this, "解除绑定", "", "取消", "确定");
            confirmDialog.show();
            confirmDialog.setCanceledOnTouchOutside(true);
            confirmDialog.setOkListener(new ConfirmDialog.IOkListener() {
                @Override
                public void doOk() {
                    UserBean userBean = new UserBean();
                    userBean.setAccount(MyApplication.account);
                    userBean.setType(Config.WeiBo);
                    userBean.setOpenid(SinaWeiBo_OpenId);
                    HttpImpl.getInstance().unBindThird(gson.toJson(userBean));
                    confirmDialog.dismiss();
                    //     showDialog();

                }
            });
            confirmDialog.setCancellistener(new ConfirmDialog.ICancelListener() {
                @Override
                public void doCancel() {
                    confirmDialog.dismiss();
                }
            });

        }
    }

    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }
        plat.removeAccount();
        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        UserBean userBean = new UserBean();
        if (platform.getName().equals(QQ.NAME)) {
            QQ_OpenId = platform.getDb().getUserId();
            whichOper = Config.QQ;
            userBean.setOpenid(QQ_OpenId);
        } else if (platform.getName().equals(Wechat.NAME)) {
            WeChat_OpenId = platform.getDb().getUserId();
            whichOper = Config.WeChat;
            userBean.setOpenid(WeChat_OpenId);
        } else if (platform.getName().equals(SinaWeibo.NAME)) {
            SinaWeiBo_OpenId = platform.getDb().getUserId();
            whichOper = Config.WeiBo;
            userBean.setOpenid(SinaWeiBo_OpenId);
        }
        userBean.setType(whichOper);
        userBean.setAccount(MyApplication.account);
        //       showDialog();
        HttpImpl.getInstance().addThidBind(gson.toJson(userBean));  //添加绑定信息
        //    updateView();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
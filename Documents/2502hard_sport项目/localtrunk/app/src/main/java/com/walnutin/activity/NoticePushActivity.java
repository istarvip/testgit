package com.walnutin.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.walnutin.manager.NoticeInfoManager;
import com.walnutin.hard.R;
import com.walnutin.view.TopTitleLableView;

/**
 * Created by assa on 2016/5/24.
 */
public class NoticePushActivity extends BaseActivity implements View.OnClickListener {

    ImageView QQ_remind;
    ImageView Wechat_remind;
    ImageView Call_remind;
    ImageView Msg_remind;
    TextView more;
    NoticeInfoManager noticeInfoManager;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushnotice);
        noticeInfoManager = NoticeInfoManager.getInstance(getApplicationContext());
        initView();
        loadLocalData();
    }

    private void loadLocalData() {
        noticeInfoManager.getLocalNoticeInfo();
        phoneFlag = noticeInfoManager.isEnablePhone();
        msgFlag = noticeInfoManager.isEnableMsg();
        QQFlag = noticeInfoManager.isEnableQQ();
        WechatFlag = noticeInfoManager.isEnableWeChat();

        if (QQFlag == true) {
            QQ_remind.setBackgroundResource(R.drawable.openblue);
        }
        if (WechatFlag == true) {
            Wechat_remind.setBackgroundResource(R.drawable.openblue);
        }
        if (msgFlag == true) {
            Msg_remind.setBackgroundResource(R.drawable.openblue);
        }
        if (phoneFlag == true) {
            Call_remind.setBackgroundResource(R.drawable.openblue);
        }
    }

    private void initView() {

        QQ_remind = (ImageView) findViewById(R.id.qq_remind);
        Wechat_remind = (ImageView) findViewById(R.id.wechat_remind);
        Call_remind = (ImageView) findViewById(R.id.call_remind);
        Msg_remind = (ImageView) findViewById(R.id.msg_remind);
        more = (TextView) findViewById(R.id.more);

        QQ_remind.setOnClickListener(this);
        Wechat_remind.setOnClickListener(this);
        Call_remind.setOnClickListener(this);
        Msg_remind.setOnClickListener(this);
        more.setOnClickListener(this);

        topTitleLableView.setOnBackListener(new TopTitleLableView.OnBackListener() {
            @Override
            public void onClick() {
                noticeInfoManager.saveNoticeInfo();
                finish();
            }
        });
    }

    boolean phoneFlag = false;
    boolean msgFlag = false;
    boolean QQFlag = false;
    boolean WechatFlag = false;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qq_remind:
                if (!isEnabled()) {
                    startNoticeService();
                } else {
                    QQFlag = !QQFlag;
                    if (QQFlag == true) {
                        QQ_remind.setBackgroundResource(R.drawable.openblue);
                    } else {
                        QQ_remind.setBackgroundResource(R.drawable.closeblue);
                    }
                    noticeInfoManager.setIsEnableQQRemind(QQFlag);
                }
                break;
            case R.id.wechat_remind:
                if (!isEnabled()) {
                    startNoticeService();
                } else {
                    WechatFlag = !WechatFlag;
                    if (WechatFlag == true) {
                        Wechat_remind.setBackgroundResource(R.drawable.openblue);
                    } else {
                        Wechat_remind.setBackgroundResource(R.drawable.closeblue);

                    }

                    noticeInfoManager.setIsEnableWeChatRemind(WechatFlag);
                }
                break;
            case R.id.call_remind:
                phoneFlag = !phoneFlag;
                if (phoneFlag == true) {
                    Call_remind.setBackgroundResource(R.drawable.openblue);
                } else {
                    Call_remind.setBackgroundResource(R.drawable.closeblue);

                }
                noticeInfoManager.setIsEnablePhoneRemind(phoneFlag);
                break;
            case R.id.msg_remind:
                msgFlag = !msgFlag;
                if (msgFlag == true) {
                    Msg_remind.setBackgroundResource(R.drawable.openblue);
                } else {
                    Msg_remind.setBackgroundResource(R.drawable.closeblue);

                }
                noticeInfoManager.setIsEnableMsgRemind(msgFlag);
                break;
            case R.id.more:
                if (!isEnabled()) {
                    startNoticeService();
                } else {
                    Intent intent = new Intent(NoticePushActivity.this, PushAppListActivity.class);
                    startActivity(intent);
                }
                break;

        }
    }

    void setRemindKeyInfo(String remind) {

    }

    void startNoticeService() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }


    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        noticeInfoManager.saveNoticeInfo();
    }
}
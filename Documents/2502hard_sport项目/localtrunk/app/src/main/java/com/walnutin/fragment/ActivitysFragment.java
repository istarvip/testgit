package com.walnutin.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.walnutin.Presenter.StorePresenter;
import com.walnutin.entity.ServerUser;
import com.walnutin.entity.UserBean;
import com.walnutin.eventbus.CommonUserResult;
import com.walnutin.hard.R;
import com.walnutin.http.HttpImpl;
import com.walnutin.util.Conversion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ActivitysFragment extends BaseFragment implements View.OnClickListener {

    View view;
    WebView webView;
    final String APP_CACAHE_DIRNAME = "webView";
    String account;
    String pwd;
    long expireTime = 25 * 60 * 1000;  //25分钟
    boolean isExpire = false;
    StorePresenter storePresenter;
    CountTime countDownTimer;
    TextView reloadPage;
    ImageView order;
    ImageView shopCar;
    boolean isLogined = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        countDownTimer = new CountTime(expireTime, 60 * 1000); //设置超时时间
        storePresenter = new StorePresenter(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activity, container, false);
        reloadPage = (TextView) view.findViewById(R.id.store_update);
        order = (ImageView) view.findViewById(R.id.order);
        shopCar = (ImageView) view.findViewById(R.id.shop_car);
        order.setOnClickListener(this);
        shopCar.setOnClickListener(this);
        reloadPage.setOnClickListener(this);

        initWebView();
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.KEYCODE_BACK == keyCode && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                countDownTimer.cancel();
                countDownTimer.start();
                return false;
            }
        });
        return view;

    }

    int type = -1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.store_update:
                webView.reload();
                break;
            case R.id.order:
                if (isLogined) {
                    webView.loadUrl("http://www.walnutin.com/user.php?act=order_list");
                } else {
                    type = 1;
                    HttpImpl.getInstance().queryAccountAndPwd();
                }
                break;
            case R.id.shop_car:
                if (isLogined) {
                    webView.loadUrl("http://www.walnutin.com/flow.php?step=cart");
                } else {
                    type = 2;
                    HttpImpl.getInstance().queryAccountAndPwd();
                }
                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("activityFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("activityFragment");
    }

    private void initWebView() {
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        webView.getSettings().setAppCachePath(cacheDirPath);
//        //开启 Application Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
        HttpImpl.getInstance().queryAccountAndPwd();

        //  webView.loadUrl("http://www.walnutin.com/");
    }

    @Subscribe
    public void onResultGetAccountInfo(CommonUserResult.CommomGetAccountAndPwdResult result) {

        if (result.state == 0) {
            if (type == 1) {
                storePresenter.setbase64TokenByObject(result.third_User, "user.php?act=order_list");
            } else if (type == 2) {
                storePresenter.setbase64TokenByObject(result.third_User, "flow.php?step=cart");

            } else {
                storePresenter.setServerInfo(result.third_User);
                storePresenter.setBase64Token();
            }
            String afterBase64 = storePresenter.getbase64TokeInfo();
            String suffix = "category.php?id=1&token=" + afterBase64;
            webView.loadUrl("http://www.walnutin.com/" + suffix);
            isLogined = true;
        } else {
            webView.loadUrl("http://www.walnutin.com/");
        }
        isExpire = false;
        type = -1;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isExpire) {
            HttpImpl.getInstance().queryAccountAndPwd();
        }
    }

    public class CountTime extends CountDownTimer {
        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            isExpire = true;
            isLogined = false;
            if (isVisible()) {
                webView.reload();
            }
            //     webView.reload();
        }
    }
}


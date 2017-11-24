package com.walnutin.activity;

import android.app.Activity;

import com.walnutin.hard.R;
import com.walnutin.view.TopTitleLableView;

/**
 * 作者：MrJiang on 2016/6/28 18:38
 */
public class BaseActivity extends Activity {
   public TopTitleLableView topTitleLableView;

//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        topTitleLableView = (TopTitleLableView) findViewById(R.id.topTitle);
        topTitleLableView.setOnBackListener(new TopTitleLableView.OnBackListener() {
            @Override
            public void onClick() {
                finish();
            }
        });


    }
}

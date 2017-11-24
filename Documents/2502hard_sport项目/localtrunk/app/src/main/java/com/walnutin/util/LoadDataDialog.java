package com.walnutin.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.walnutin.hard.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoadDataDialog extends Dialog {
    TextView txt_tips;
    private Context context;
    private String flag;

    public LoadDataDialog(Context context, String flag) {
        super(context, R.style.myDialog);
        this.context = context;
        this.flag = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }


    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loaddata_dialog, null);
        txt_tips = (TextView) view.findViewById(R.id.tips);
        setContentView(view);
        if (flag.equals("loadDialog")) {
            txt_tips.setText(R.string.initing);
        } else if(flag.equals("login")){
            txt_tips.setText("登录中...");

        }else if (flag.equals("upDialog")) {
            txt_tips.setText(R.string.uping);
        } else if (flag.equals("authorEngineer")) {
            txt_tips.setText(R.string.authoring);
        } else if (flag.equals("lookForProj")) {
            txt_tips.setText(R.string.loading);
        } else if (flag.equals("cleanCacheDialog")) {
            txt_tips.setText(R.string.cleaning);
        }else if(flag.equals("deal")){
            txt_tips.setText(R.string.dealing);
        }else if(flag.equals("link")){
            txt_tips.setText("连接中...");
        }
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        lp.height = (int) (d.heightPixels * 0.2);
        dialogWindow.setAttributes(lp);
    }
}

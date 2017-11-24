package com.walnutin.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.walnutin.hard.R;

public class CorformPopupWindow extends PopupWindow {


    private TextView txtTakiPhoto, cancel;
    private View mMenuView;

    public CorformPopupWindow(Activity context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.conform_pop, null);
        txtTakiPhoto = (TextView) mMenuView.findViewById(R.id.sure);
        cancel = (TextView) mMenuView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        txtTakiPhoto.setOnClickListener(itemsOnClick);
        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

}

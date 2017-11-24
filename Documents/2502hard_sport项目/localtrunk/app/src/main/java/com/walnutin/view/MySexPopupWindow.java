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

public class MySexPopupWindow extends PopupWindow {


	private TextView sex_man,sex_woman, sex_cancel;
	private View mMenuView;

	public MySexPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.mysex_pop, null);
		sex_man = (TextView) mMenuView.findViewById(R.id.sex_man);
		sex_woman = (TextView) mMenuView.findViewById(R.id.sex_woman);
		sex_cancel = (TextView) mMenuView.findViewById(R.id.sex_cancel);
		sex_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		sex_man.setOnClickListener(itemsOnClick);
		sex_woman.setOnClickListener(itemsOnClick);
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
	//	ColorDrawable dw = new ColorDrawable(0xb0000000);
	//	this.setBackgroundDrawable(dw);

	}

}

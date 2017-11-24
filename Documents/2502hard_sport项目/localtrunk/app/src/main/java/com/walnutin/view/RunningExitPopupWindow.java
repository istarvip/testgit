package com.walnutin.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.walnutin.hard.R;

public class RunningExitPopupWindow extends PopupWindow {


	private TextView sure, btn_cancel;
	private View mMenuView;

	public RunningExitPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.running_end_pop, null);
		sure = (TextView) mMenuView.findViewById(R.id.sure);
		btn_cancel = (TextView) mMenuView.findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		sure.setOnClickListener(itemsOnClick);
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
	//	ColorDrawable dw = new ColorDrawable(0xb0000000);
	//	this.setBackgroundDrawable(dw);

	}

}

package com.walnutin.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.walnutin.Jinterface.OnItemSelectedListener;
import com.walnutin.activity.PersonalInfoActivity;
import com.walnutin.hard.R;

import java.util.Arrays;

public class MyHeightPopupWindow extends PopupWindow{


	private Button height_cancel,height_sure;
	private View mMenuView;
	private final LoopView loopView_height;
	private String mheight;


	private static final String[] HEIGHT = new String[]{"请选择身高","100cm", "110cm", "120cm", "130cm", "135cm", "140cm", "145cm", "150cm"
			, "155cm", "160cm", "165cm", "170cm", "175cm", "180cm", "185cm", "190cm", "195cm", "200cm", "205cm", "210cm"};



	public MyHeightPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.myheight_pop, null);
		height_sure = (Button) mMenuView.findViewById(R.id.height_sure);
		height_cancel = (Button) mMenuView.findViewById(R.id.height_cancel);
		loopView_height = (LoopView) mMenuView.findViewById(R.id.LoopView_height);
		loopView_height.setListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				mheight=HEIGHT[index];
				setMaterialHeight(mheight);
			}
		});
		loopView_height.setItems(Arrays.asList(HEIGHT));
		//设置初始位置
		loopView_height.setInitPosition(0);

		//设置字体大小
		loopView_height.setTextSize(15);
		loopView_height.setViewPadding(80, 0, 0, 0);

		height_cancel.setOnClickListener(itemsOnClick);
		height_sure.setOnClickListener(itemsOnClick);

		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		materialActivity = (PersonalInfoActivity) context;
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
	//	ColorDrawable dw = new ColorDrawable(0xb0000000);
	//	this.setBackgroundDrawable(dw);

	}
	PersonalInfoActivity materialActivity = null;
	private void setMaterialHeight(String mheight) {
		materialActivity.setMaterialHeight(mheight);

	}

}

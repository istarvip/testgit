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

public class MyWeightPopupWindow extends PopupWindow{


	private Button weight_cancel,weight_sure;
	private View mMenuView;
	private final LoopView loopView_weight;
	private String mWeight;

	private static final String[] WEIGHT = new String[]{"请选择体重","40kg", "45kg", "50kg", "55kg", "60kg", "65kg", "70kg", "75kg"
			, "80kg", "85kg", "90kg", "95kg", "100kg"};



	public MyWeightPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.myweight_pop, null);
		weight_sure = (Button) mMenuView.findViewById(R.id.btn_weight_sure);
		weight_cancel = (Button) mMenuView.findViewById(R.id.btn_weight_cancel);
		loopView_weight = (LoopView) mMenuView.findViewById(R.id.LoopView_weight);
		loopView_weight.setListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				mWeight = WEIGHT[index];
				setMaterialWeight(mWeight);
			}
		});

		loopView_weight.setItems(Arrays.asList(WEIGHT));
		//设置初始位置
		loopView_weight.setInitPosition(0);



		//设置字体大小
		loopView_weight.setTextSize(15);
		loopView_weight.setViewPadding(80, 0, 0, 0);

		weight_cancel.setOnClickListener(itemsOnClick);
		weight_sure.setOnClickListener(itemsOnClick);

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

	private void setMaterialWeight(String mWeight) {
		materialActivity.setMaterialWeight(mWeight);

	}

}

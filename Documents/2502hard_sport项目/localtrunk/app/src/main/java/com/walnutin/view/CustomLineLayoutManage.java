package com.walnutin.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 作者：MrJiang on 2016/6/8 15:02
 */
public class CustomLineLayoutManage extends LinearLayoutManager {

    public CustomLineLayoutManage(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        System.out.println("width:" + View.MeasureSpec.getSize(widthSpec) + " height: " + View.MeasureSpec.getSize(heightSpec));
    }

}

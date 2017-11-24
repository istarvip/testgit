package com.walnutin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.hard.R;

/**
 * Created by caro on 16/6/7.
 */
public class SimpleLableView extends RelativeLayout{
    private View mRootView;
    private TextView lable;
    private TextView lablevalue;

    String labelString;
    String labelValueString;


    public SimpleLableView(Context context) {
        super(context);
    }

    public SimpleLableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = View.inflate(context, R.layout.item_simple, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.simpleLabel);
        initView(typedArray);
        typedArray.recycle();
    }

    private void initView(TypedArray typedArray){
        labelString = typedArray.getString(R.styleable.simpleLabel_label);
        labelValueString = typedArray.getString(R.styleable.simpleLabel_labelValue);

        lable = (TextView)mRootView.findViewById(R.id.lable);
        lablevalue = (TextView)mRootView.findViewById(R.id.lableview);

        if (labelString!=null){
            lable.setText(labelString);
        }

        if (labelValueString!=null){
            lablevalue.setText(labelString);
        }
    }

    /**
     *
     * @param labelStr
     * @return
     */
    public SimpleLableView setLabel(String labelStr){

        if (labelStr!=null){
            lable.setText(labelStr);
        }


        return this;
    }


    /**
     *
     * @param labelValue
     * @return
     */
    public SimpleLableView setLabelValue(String labelValue){

        if (labelValue!=null){
            lablevalue.setText(labelValue);
        }


        return this;
    }
}

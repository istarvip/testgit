package com.walnutin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.hard.R;

/**
 * Created by MrJ on 16/6/7.
 */
public class RelativeLineGroupView extends RelativeLayout{
    private View mRootView;
    private TextView leftContent;
    private TextView centTerContent;

    String leftString;
    String centerString;


    public RelativeLineGroupView(Context context) {
        super(context);
    }

    public RelativeLineGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = View.inflate(context, R.layout.group_relbase_content, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.groupTopTitle);
        initView(typedArray);
        typedArray.recycle();
    }

    private void initView(TypedArray typedArray){
        leftString = typedArray.getString(R.styleable.groupTopTitle_leftContent);
        centerString = typedArray.getString(R.styleable.groupTopTitle_centerContent);

        leftContent = (TextView)mRootView.findViewById(R.id.left_content);
        centTerContent = (TextView)mRootView.findViewById(R.id.center_content);

        if (leftString!=null){
            leftContent.setText(leftString);
        }

        if (centerString!=null){
            centTerContent.setText(centerString);
        }
    }

    /**
     *
     * @param labelStr
     * @return
     */
    public RelativeLineGroupView setLeftContent(String labelStr){

        if (labelStr!=null){
            leftContent.setText(labelStr);
        }


        return this;
    }


    /**
     *
     * @param labelValue
     * @return
     */
    public RelativeLineGroupView setCenterContent(String labelValue){

        if (labelValue!=null){
            centTerContent.setText(labelValue);
        }


        return this;
    }
}

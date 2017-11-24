package com.walnutin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walnutin.hard.R;

/**
 * Created by MrJ on 16/6/7.
 */
public class RelativeEditGroupView extends RelativeLayout {
    private View mRootView;
    private TextView leftContent;
    private EditText centTerContent;
    private TextView rightContent;

    String leftString;
    String centerString;
    String rightString;

    public RelativeEditGroupView(Context context) {
        super(context);
    }

    public RelativeEditGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = View.inflate(context, R.layout.group_setting_content, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.groupItemContent);
        initView(typedArray);
        typedArray.recycle();
    }

    private void initView(TypedArray typedArray) {
        leftString = typedArray.getString(R.styleable.groupItemContent_leftItemContent);
        centerString = typedArray.getString(R.styleable.groupItemContent_centerItemContent);
        String right = typedArray.getString(R.styleable.groupItemContent_rightItemContent);

        leftContent = (TextView) mRootView.findViewById(R.id.left_content);
        centTerContent = (EditText) mRootView.findViewById(R.id.center_content);

        rightContent = (TextView) mRootView.findViewById(R.id.suffix_type);

        if (leftString != null) {
            leftContent.setText(leftString);
        }

        if (centerString != null) {
            centTerContent.setText(centerString);
        }

        if(right!=null){
            rightContent.setText(right);
        }
    }

    /**
     * @param labelStr
     * @return
     */
    public RelativeEditGroupView setLeftContent(String labelStr) {

        if (labelStr != null) {
            leftContent.setText(labelStr);
        }


        return this;
    }


    /**
     * @param labelValue
     * @return
     */
    public RelativeEditGroupView setCenterContent(String labelValue) {

        if (labelValue != null) {
            centTerContent.setText(labelValue);
        }
        return this;
    }

    public RelativeEditGroupView setRightContent(String labelValue) {

        if (labelValue != null) {
            rightContent.setText(labelValue);
        }
        return this;
    }

    public EditText getCenterEditText(){
        return centTerContent;
    }


}

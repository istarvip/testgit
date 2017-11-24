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
public class TopTitleLableView extends RelativeLayout {
    private View mRootView;
    private TextView txtBack;
    private TextView txtLable;
    TextView txtRightOper;

    String backString;
    String labelValueString;

    private OnBackListener back;
    private OnRightClick rightClick;

    public interface OnBackListener {
         void onClick();
    }

    public interface OnRightClick {
         void onClick();
    }


    public void setOnBackListener(OnBackListener onClickListener) {
        back = onClickListener;

    }

    public void setOnRightClickListener(OnRightClick right) {
        this.rightClick = right;
    }


    public TopTitleLableView(Context context) {
        super(context);
    }

    public TopTitleLableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = View.inflate(context, R.layout.title_top, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.topTitle);
        initView(typedArray);
        typedArray.recycle();
    }

    private void initView(TypedArray typedArray) {
        backString = typedArray.getString(R.styleable.topTitle_back);
        labelValueString = typedArray.getString(R.styleable.topTitle_lableText);
        int resouceid = typedArray.getResourceId(R.styleable.topTitle_right_oper_img, -1);
        String txtRight = typedArray.getString(R.styleable.topTitle_right_oper_txt);

        txtBack = (TextView) mRootView.findViewById(R.id.back);
        txtLable = (TextView) mRootView.findViewById(R.id.title_name);
        txtRightOper = (TextView) mRootView.findViewById(R.id.right_oper);
        if (resouceid != -1) {
            txtRightOper.setBackgroundResource(resouceid);
        } else if (txtRight != null) {
            txtRightOper.setText(txtRight);
        }

        if (backString != null) {
            txtBack.setText(backString);
        }

        if (labelValueString != null) {
            txtLable.setText(labelValueString);
        }

        txtRightOper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightClick != null) {
                    rightClick.onClick();
                }
            }
        });

        txtBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (back != null) {
                    back.onClick();
                }
            }
        });
    }

    /**
     * @param labelStr
     * @return
     */
    public TopTitleLableView setLeftLable(String labelStr) {

        if (labelStr != null) {
            txtBack.setText(labelStr);
        }

        return this;
    }


    /**
     * @param labelValue
     * @return
     */

    public TopTitleLableView setLabelTitleValue(String labelValue) {

        if (labelValue != null) {
            txtLable.setText(labelValue);
        }

        return this;
    }

    public TopTitleLableView setTopRightImgValue(int labelValue) {

        txtRightOper.setBackgroundResource(labelValue);

        return this;
    }

    public TopTitleLableView setTopRightTxtalue(String labelValue) {

        if (labelValue != null) {
            txtRightOper.setText(labelValue);
        }

        return this;
    }

    public TextView getBackView() {
        return txtBack;
    }

    public TextView getTitleView() {
        return txtLable;
    }

    public TextView getRightView() {
        return txtRightOper;
    }

}

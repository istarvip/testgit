/*
 *  Copyright (C) 2015 Ingenic Semiconductor
 *
 *  ShiGuanghua(kenny) <guanghua.shi@ingenic.com>
 *
 *  elf/AmazingLauncher project
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation; either version 2 of the License, or (at your
 *  option) any later version.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */
package com.walnutin.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * 自定义控件：环形进度控件
 *
 * @author likairui & MingDan
 */
public class HealthProgressView extends View {
    private Paint mRingPaint;
    private Paint mDefaultPaint;
    private float mRingRadius;
    private int mXCenter;
    private int mYCenter;
    private int mTotalProgress = 360;
    private int mProgress;
    private int mStrokeWidth;
    private int mCircleRadius; // 圆环半径

    private Context context;
    //   PaintFlagsDrawFilter paintFlagsDrawFilter;

    public HealthProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义的属性
        initAttrs(context, attrs);
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        this.context = context;
        mCircleRadius = dip2px(context, 65);
        mStrokeWidth = dip2px(context, 2);
        // 圆环半径
        mRingRadius = mCircleRadius;

    }

    private void initVariable() {

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStyle(Paint.Style.STROKE);

        mDefaultPaint = new Paint();
        mDefaultPaint.setStrokeWidth(4);
        mDefaultPaint.setStyle(Paint.Style.STROKE);
        mDefaultPaint.setAntiAlias(true);
      //  mDefaultPaint.setColor(Color.RED);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;

        mRingPaint.clearShadowLayer();
      //  canvas.drawCircle(mXCenter,mYCenter,mRingRadius,mDefaultPaint);


        int[] colors = {0x00FFFFFF, 0xFFFFFFFF};
        float[] pos = {0, (float) (mProgress % 360) / 360};
        SweepGradient sweepGradient = new SweepGradient(mXCenter, mYCenter, colors, pos);
        Matrix matrix = new Matrix();
        matrix.setRotate(-92, mXCenter, mYCenter);
        sweepGradient.setLocalMatrix(matrix);
        mRingPaint.setShader(sweepGradient);
        RectF oval = new RectF();
        oval.left = mXCenter - mRingRadius;
        oval.top = mYCenter - mRingRadius;
        oval.right = mXCenter + mRingRadius;
        oval.bottom = mYCenter + mRingRadius;
        canvas.drawArc(oval, -90, mProgress, false,
                mRingPaint);



    }


    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public void setRingColor(int mRingColor) {
        mRingPaint.setColor(mRingColor);
        postInvalidate();
    }

}

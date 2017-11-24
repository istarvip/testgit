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
public class RingProgressView extends View {
    // 画外圈的画笔
    private Paint mOuterRingPaint;
    // 画圆环的画笔
    private Paint mRingPaint;
    // 圆形颜色
    private int mCircleColor;
    // 圆环颜色
    private int mRingColor;
    // 半径
    private float mCircleRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆与圆环间隔宽度
    private float mSpaceWidth;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 总进度
    private int mTotalProgress = 360;
    // 当前进度
    private int mProgress;
    // 圆环的画笔的宽度
    private int mStrokeWidth;
    private int mRingEndColor;

    private int ringStart0Color;
    private int ringStart90Color;
    private int ringStart180Color;
    private int ringStart270Color;

    private Context context;
 //   PaintFlagsDrawFilter paintFlagsDrawFilter;

    public RingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义的属性
        initAttrs(context, attrs);
        initVariable();
    }

    /**
     * 获取自定义属性值
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        this.context = context;
        mCircleRadius = dip2px(context, 65);
        mStrokeWidth = dip2px(context, 15);
        // 圆与圆环间隔宽度
        mSpaceWidth = dip2px(context, 10);
        // 圆环半径
        mRingRadius = mCircleRadius + mSpaceWidth;
        mCircleColor = Color.rgb(0xff, 0xff, 0xff);
//        mRingColor = Color.rgb(0x00, 0x45,0x54);
        mRingColor = Color.rgb(0x00, 0x00, 0xff);
        mRingEndColor = Color.rgb(0xff, 0xff, 0x00);

        ringStart0Color = Color.rgb(0x7f, 0x3c, 0xd9);
        ringStart90Color = Color.rgb(0xdf, 0x28, 0x3c);
        ringStart180Color = Color.rgb(0xe3, 0x77, 0x35);
        ringStart270Color = Color.rgb(0xe9, 0xcb, 0x26);
    }

    /**
     * 初始化
     */
    private void initVariable() {
        mOuterRingPaint = new Paint();
        mOuterRingPaint.setAntiAlias(true);
        mOuterRingPaint.setColor(mCircleColor);
        mOuterRingPaint.setStyle(Paint.Style.STROKE);
        mOuterRingPaint.setStrokeWidth(mStrokeWidth);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        //   mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);
        mRingPaint.setStrokeCap(Paint.Cap.ROUND);
        //  mRingPaint.setSrokeJoin(Paint.Join join);

    }
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;
        canvas.drawCircle(mXCenter, mYCenter, mRingRadius, mOuterRingPaint);
//        mRingColor = Color.rgb(0x00, 0x00,0xff);
//        LinearGradient lg=new LinearGradient(dip2px(context,100),0,dip2px(context,100),dip2px(context,100),ringStart0Color,ringStart90Color,Shader.TileMode.CLAMP);
//        mRingPaint.setShader(lg);
//
        System.out.println("progess............."+mProgress);
        if (mProgress >= 0) {
            RectF oval = new RectF();
            oval.left = mXCenter - mRingRadius;
            oval.top = mYCenter - mRingRadius;
            oval.right = mXCenter + mRingRadius;
            oval.bottom = mYCenter + mRingRadius;
            if (mProgress <= 90) {
                draw0section(canvas, oval);
            } else if (mProgress > 90 && mProgress <= 180) {
                draw0section(canvas, oval);
                draw90section(canvas, oval);
            } else if (mProgress > 180 && mProgress <= 270) {
                draw0section(canvas, oval);
                draw90section(canvas, oval);
                draw180section(canvas, oval);
            } else {
                draw0section(canvas, oval);
                draw90section(canvas, oval);
                draw180section(canvas, oval);
                draw270section(canvas, oval);
            }
//            canvas.drawArc(oval,-90,
//                    ((float) 90 / mTotalProgress) * 360, false,
//                    mRingPaint);
        }
    }

    void draw0section(Canvas canvas, RectF oval) {
        LinearGradient lg = new LinearGradient(dip2px(context, 100), dip2px(context, 0), dip2px(context, 100), dip2px(context, 100), ringStart0Color, ringStart90Color, Shader.TileMode.CLAMP);
        mRingPaint.setShader(lg);

        if (mProgress < 90) {
            canvas.drawArc(oval, -90,
                    ((float) mProgress / mTotalProgress) * 360, false,
                    mRingPaint);
        } else {
            canvas.drawArc(oval, -90,
                    ((float) 90 / mTotalProgress) * 360, false,
                    mRingPaint);
        }
    }

    void draw90section(Canvas canvas, RectF oval) {
        LinearGradient lg = new LinearGradient(dip2px(context, 200), dip2px(context, 200), dip2px(context, 100), dip2px(context, 200), ringStart90Color, ringStart180Color, Shader.TileMode.CLAMP);
        mRingPaint.setShader(lg);

        if (mProgress < 180) {
            canvas.drawArc(oval, 0,
                    ((float) mProgress - 90 / mTotalProgress) * 360, false,
                    mRingPaint);
        } else {
            canvas.drawArc(oval, 0,
                    ((float) 90 / mTotalProgress) * 360, false,
                    mRingPaint);
        }
    }

    void draw180section(Canvas canvas, RectF oval) {
        LinearGradient lg = new LinearGradient(dip2px(context, 100), dip2px(context, 200), dip2px(context, 0), dip2px(context, 100), ringStart180Color, ringStart270Color, Shader.TileMode.CLAMP);
        mRingPaint.setShader(lg);
        if (mProgress < 270) {
            canvas.drawArc(oval, 90,
                    ((float) mProgress - 180 / mTotalProgress) * 360, false,
                    mRingPaint);
        } else {
            canvas.drawArc(oval, 90,
                    ((float) 90 / mTotalProgress) * 360, false,
                    mRingPaint);
        }
    }

    void draw270section(Canvas canvas, RectF oval) {
        LinearGradient lg = new LinearGradient(dip2px(context, 0), dip2px(context, 100), dip2px(context, 0), dip2px(context, 0), ringStart270Color, ringStart0Color, Shader.TileMode.CLAMP);
        mRingPaint.setShader(lg);
        if (mProgress < 360) {
            canvas.drawArc(oval, 180,
                    ((float) mProgress - 270 / mTotalProgress) * 360, false,
                    mRingPaint);
        } else {
            canvas.drawArc(oval, 180,
                    ((float) 90 / mTotalProgress) * 360, false,
                    mRingPaint);
        }
    }

    public void startBatteryAnimation(float startPosition, float stopPosition) {
        ValueAnimator mValue = ValueAnimator.ofFloat(startPosition,
                stopPosition);
        mValue.setDuration(1000);
//        mValue.setInterpolator(new AnticipateOvershootInterpolator ());
        mValue.setInterpolator(new DecelerateInterpolator());
//        mValue.setInterpolator(new LinearInterpolator());
        mValue.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float valueInt = ((Float) animation.getAnimatedValue())
                        .intValue();
                setProgress((int) valueInt);
            }
        });
        mValue.start();
    }

    public void setProgress(int progress) {
        mProgress = progress;
        postInvalidate();
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
   //     System.out.println("dip2px" + scale);
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置圆环颜色
     *
     * @param mRingColor
     */
    public void setRingColor(int mRingColor) {
        mRingPaint.setColor(mRingColor);
        postInvalidate();
    }

}

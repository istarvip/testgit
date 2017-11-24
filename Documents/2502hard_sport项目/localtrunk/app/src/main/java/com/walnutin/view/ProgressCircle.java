/*
 * @author Jerry
 * @time 2015-12-29 下午3:57:33
 */
package com.walnutin.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 弧形刻度进度条.
 *
 * MrJiang
 *
 */
public class ProgressCircle extends View {

    private Paint mPaint;

    private int mColor = Color.rgb(0xe5,0xe5,0xe5);
    private int fronColor = Color.rgb(0xdf,0x0b,0x08);
    private int circleColor = Color.WHITE;
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标
    private ObjectAnimator mAnimator;

    private float progressWidth = dipToPx(4);
    private float longdegree = dipToPx(10);
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(5);

    public ProgressCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircle(Context context) {
        this(context, null);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    int ragle = 5;

    float gap = dipToPx(0.5f);

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(circleColor);
        //canvas.drawCircle(centerX,centerY,getHeight()/2,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        RectF oval = new RectF();
        oval.left = 0;
        oval.top = 0;
        oval.right = getWidth();
        oval.bottom = getHeight();
        canvas.drawArc(oval, 0, 360, false, mPaint);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        super.onDraw(canvas);
        canvas.save();
        //   System.out.println("centerX: " + centerX + " -------- " + centerY);
        for (int i = 0; i < 72; i++) {
            canvas.drawLine(centerX, 0 + gap, centerX, longdegree + gap, mPaint);
            canvas.rotate(ragle, centerX, centerY);

        }
        canvas.restore();
        canvas.save();
        mPaint.setColor(fronColor);

        for (int i = 0; i < progress / 5; i++) {
            canvas.drawLine(centerX, gap, centerX, longdegree+gap, mPaint);
            canvas.rotate(ragle, centerX, centerY);
        }
        canvas.restore();

    }

    int progress = 0;

    public void setProgress(int progress) {
        //  this.progress = progress < 0 ? 0 : progress % 360;
        this.progress = progress;
        if(progress <0){
            progress =0;
        }
        if(progress >=360){
            this.progress =360;
        }
        this.invalidate();
    }

    public int getProgress() {
        return progress;
    }

    private int lastToadayProgress;


    public void setAnimProgress(int progress) {
        if (mAnimator == null || !mAnimator.isRunning()) {
            mAnimator = ObjectAnimator.ofInt(this, "progress", 0, progress);
            mAnimator.setDuration(2000);
            mAnimator.start();
        }
    }

   public void setTodayAniProgress(int progress) {
        if (mAnimator == null || !mAnimator.isRunning()) {
            mAnimator = ObjectAnimator.ofInt(this, "progress", lastToadayProgress, progress);
            if (progress>90){
                mAnimator.setDuration(2000);
            }else {
                mAnimator.setDuration(500);
            }

            mAnimator.start();
        }
       lastToadayProgress = progress;
    }


}

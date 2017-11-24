package com.walnutin.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import com.walnutin.hard.R;

/**
 * Created by Administrator on 2016/7/27.
 */
public class StepStraightLine extends View {

    private Paint paint;
    private int defautColors;
    private int dealedColors;
    float xWidth;
    float yHeight;
    int progress = 0;
    int currentStep =0;
    //final int TOTAL_PROGRESS = 100;
    int rectHeight = dip2px(getContext(),9);
    private  int MAXVALUE =10000;
    private ObjectAnimator mAnimator;


    public StepStraightLine(Context context) {
        super(context);
        init();
    }

    public StepStraightLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        dealedColors = getResources().getColor(R.color.red_background_notselected);
        defautColors = getResources().getColor(R.color.text_color);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        xWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        yHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        System.out.println("xWidth: "+xWidth +" yHeight: "+yHeight);

        float startXPos = getPaddingLeft();
        float startYPos = yHeight -rectHeight;
        System.out.println("xWidth startXPos: "+startXPos);

        RectF rect = new RectF();
        rect.left = startXPos;
        rect.top = startYPos;
        rect.bottom = (rectHeight+startYPos);
        rect.right =  (xWidth+startXPos );

        paint.setColor(defautColors);
        canvas.drawRoundRect(rect, 15, 15, paint);

        paint.setColor(dealedColors);
        RectF newRect = new RectF();
        newRect.left = startXPos;
        newRect.top = startYPos;
        newRect.bottom = (rectHeight+startYPos );
        System.out.println("xWidth progress: "+progress);
        newRect.right =  (((float)currentStep / MAXVALUE) * xWidth +startXPos);

        canvas.drawRoundRect(newRect, 15, 15, paint);

    }


    public void setProgress(int prog) {
        this.progress = prog;
        invalidate();
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        //     System.out.println("dip2px" + scale);
        return (int) (dpValue * scale + 0.5f);
    }

    public void setMAXVALUE(int maxvalue){
        MAXVALUE = maxvalue;
        invalidate();
    }

    public void setCurrentStep(int currentSteps){
        currentStep = currentSteps;
        invalidate();
    }

    private int lastStep;

    public void setAniStep(int currentStep) {
        if (mAnimator == null || !mAnimator.isRunning()) {
            mAnimator = ObjectAnimator.ofInt(this, "currentStep", lastStep, currentStep);
            if (currentStep>MAXVALUE/4){
                mAnimator.setDuration(2000);
            }else {
                mAnimator.setDuration(500);
            }

            mAnimator.start();
        }
        lastStep = currentStep;
    }
}

package com.walnutin.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import com.walnutin.hard.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class HeartRateStraightLine extends View {

    private Paint paint;
    private int defautColors;
    private int dealedColors;
    float xWidth;
    float yHeight;
    int progress = 0;
    final int TOTAL_PROGRESS = 100;
    int rectHeight = dip2px(getContext(), 9);
    private Rect mStartTextBound;
    private Rect mEndTextBound;
    private Rect mCenterTextBound;
    private String startHeareRate = "60";
    private String endHeartRate = "110";
    private String centerHeart = "80";
    private int startRate = 10000;
    int endRate = 0;
    int centerRate = 0;
    List<Integer> recentRateList;

    public HeartRateStraightLine(Context context) {
        super(context);
        init();
    }

    public HeartRateStraightLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setTextSize(dip2px(getContext(), 10));
        dealedColors = getResources().getColor(R.color.red_background_notselected);
        defautColors = getResources().getColor(R.color.text_color);
        mStartTextBound = new Rect();
        mEndTextBound = new Rect();
        mCenterTextBound = new Rect();
        paint.getTextBounds(startHeareRate, 0, startHeareRate.length(), mStartTextBound);
        paint.getTextBounds(endHeartRate, 0, endHeartRate.length(), mEndTextBound);
        paint.getTextBounds(centerHeart, 0, centerHeart.length(), mCenterTextBound);


    }

    float textGapPic = dip2px(getContext(), 5);
    float startXPos;
    float startYPos;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float mTextHeight = mStartTextBound.height();

        xWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        yHeight = getHeight() - getPaddingBottom() - getPaddingTop() - mTextHeight - textGapPic;
        System.out.println("xWidth: " + xWidth + " yHeight: " + yHeight);

        startXPos = getPaddingLeft();
        startYPos = yHeight - rectHeight;
        System.out.println("xWidth startXPos: " + startXPos);

        RectF rect = new RectF();
        rect.left = startXPos;
        rect.top = startYPos;
        rect.bottom = (yHeight);
        rect.right = (xWidth);

        paint.setColor(defautColors);
        canvas.drawRoundRect(rect, 15, 15, paint);


        if (startRate > endRate || centerRate == 0 || recentRateList == null) {
            return;
        }


              /*
        *
        * ???? ???????
        * */
        drawText(canvas);


        /*
        *
        * ??????????????????????
        *
        * */

        drawRectList(canvas);


    }

    // ?????? ?????? 255 ??ν???

    private void drawRectList(Canvas canvas) {
        paint.setColor(dealedColors);

        int alpha = 255;
        paint.setAlpha(alpha);
        int len = recentRateList.size() > 10 ? 10 : recentRateList.size();
        for (int i = 0; i < len; i++) {
            float centerTextPos = (float) (recentRateList.get(i) - startRate) / (endRate - startRate) * xWidth + startXPos;
            RectF newRect = new RectF();
            newRect.left = centerTextPos;
            newRect.top = startYPos;
            newRect.bottom = (yHeight);
            newRect.right = (centerTextPos + textGapPic);
            canvas.drawRect(newRect, paint);
            canvas.drawText(String.valueOf(recentRateList.get(i)), centerTextPos - mCenterTextBound.width() / 4, yHeight + textGapPic + mCenterTextBound.height(), paint);
            alpha -= 25;
            paint.setAlpha(alpha);
        }
//
//        float centerTextPos = (float) (centerRate - startRate) / (endRate - startRate) * xWidth + startXPos;
//        RectF newRect = new RectF();
//        newRect.left = centerTextPos;
//        newRect.top = startYPos;
//        newRect.bottom = (yHeight);
//        newRect.right = (centerTextPos + textGapPic);
//        canvas.drawRect(newRect, paint);
//        canvas.drawText(centerHeart, centerTextPos - mCenterTextBound.width() / 2, yHeight + textGapPic + mCenterTextBound.height(), paint);

        paint.setAlpha(255);
    }

    void drawText(Canvas canvas) {
        paint.setColor(Color.BLACK);
        canvas.drawText(startHeareRate, startXPos, yHeight + textGapPic + mStartTextBound.height(), paint);
        canvas.drawText(endHeartRate, xWidth - mEndTextBound.width(), yHeight + textGapPic + mEndTextBound.height(), paint);
        //     canvas.drawText(centerHeart, xWidth - mCenterTextBound.width(), yHeight + textGapPic + mCenterTextBound.height(), paint);

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

    public void setLowHeartRate(int lowHeartRate) {

        if (lowHeartRate < startRate) {
            startHeareRate = String.valueOf(lowHeartRate);
            startRate = lowHeartRate;
            paint.getTextBounds(startHeareRate, 0, startHeareRate.length(), mStartTextBound);

        }

    }

    public void setHighHeartRate(int HighHeartRate) {

        if (HighHeartRate > endRate) {
            endHeartRate = String.valueOf(HighHeartRate);
            endRate = HighHeartRate;
            paint.getTextBounds(endHeartRate, 0, endHeartRate.length(), mEndTextBound);
        }
    }

    public void setCenterHeart(int centerHeart1) {
        centerHeart = String.valueOf(centerHeart1);
        centerRate = centerHeart1;
        paint.getTextBounds(centerHeart, 0, centerHeart.length(), mCenterTextBound);

    }

    public void setHeartRate(int lowHeartRate, int centerHeart1, int HighHeartRate) {
        if (lowHeartRate < startRate) {
            startHeareRate = String.valueOf(lowHeartRate);
            startRate = lowHeartRate;
            paint.getTextBounds(startHeareRate, 0, startHeareRate.length(), mStartTextBound);

        }

        if (HighHeartRate > endRate) {
            endHeartRate = String.valueOf(HighHeartRate);
            endRate = HighHeartRate;
            paint.getTextBounds(endHeartRate, 0, endHeartRate.length(), mEndTextBound);
        }
        centerHeart = String.valueOf(centerHeart1);
        centerRate = centerHeart1;
        paint.getTextBounds(centerHeart, 0, centerHeart.length(), mCenterTextBound);

        invalidate();
    }

    public void setRecentRateList(List list) {
        recentRateList = list;

    }

}

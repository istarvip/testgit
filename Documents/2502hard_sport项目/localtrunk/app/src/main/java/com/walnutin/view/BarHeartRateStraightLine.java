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
public class BarHeartRateStraightLine extends View {

    private Paint paint;
    private int defautColors;
    private int dealedColors;
    float xWidth;
    float yHeight;
    int progress = 0;
    final int TOTAL_PROGRESS = 100;
    int rectWidth = dip2px(getContext(), 12);
    private Rect mStartTextBound;
    private Rect mEndTextBound;
    private Rect mCenterTextBound;
    private String startHeareRate = "60";
    private String endHeartRate = "110";
    private String centerHeart = "80";
    private int startRate = 60;
    int endRate = 100;
    int centerRate = 100;
    List<Integer> recentRateList;
    float defaultFontSize = dip2px(getContext(), 13);
    float selectedFontSize = dip2px(getContext(), 16);

    public BarHeartRateStraightLine(Context context) {
        super(context);
        init();
    }

    public BarHeartRateStraightLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setTextSize(defaultFontSize);
        dealedColors = getResources().getColor(R.color.white);
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
        yHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        System.out.println("xWidth: " + xWidth + " yHeight: " + yHeight);

        startXPos = getPaddingLeft();
        startYPos = yHeight + getPaddingTop();


        RectF rect = new RectF();
        rect.left = startXPos;
        rect.top = getPaddingTop();
        rect.bottom = yHeight + getPaddingTop();
        rect.right = startXPos + rectWidth;

        paint.setColor(defautColors);
        canvas.drawRect(rect, paint);


        if (startRate > endRate || centerRate == 0 || recentRateList == null) {
            return;
        }


              /*
        *
        * ���� ��ʼ����
        * */
        //    drawText(canvas);


        /*
        *
        * �������״����ͼ����ʾ����
        *
        * */

        drawRectList(canvas);


    }

    // ��ʾ��� ��¼ʮ�� 255 һ�ν���

    float outRectWidth = dip2px(getContext(), 2);  //��β�ĳ���
    float rectHeight = dip2px(getContext(), 3);  //ÿ�ξ��εĸ߶�

    private void drawRectList(Canvas canvas) {
        paint.setColor(dealedColors);

        int alpha = 255;
        paint.setAlpha(alpha);
        int len = recentRateList.size() > 10 ? 10 : recentRateList.size();

        if (len < 1) {
            return;
        }

        for (int i = 0; i < len; i++) {
            // float centerTextPos = (float) (recentRateList.get(i) - startRate) / (endRate - startRate) * xWidth + startXPos;

            RectF newRect = new RectF();
            newRect.left = startXPos - outRectWidth;
            newRect.right = startXPos + rectWidth + outRectWidth;
            newRect.bottom = startYPos - ((float) (recentRateList.get(i) - startRate) / (endRate - startRate)) * yHeight;
            newRect.top = newRect.bottom - rectHeight;

            if (centerRate == recentRateList.get(i)) {
                alpha = 255;
                paint.setAlpha(alpha);
                paint.setTextSize(selectedFontSize);
                canvas.drawText(String.valueOf(recentRateList.get(i)), newRect.right + textGapPic, newRect.bottom - rectHeight / 2 + mCenterTextBound.height() / 2, paint);
            } else {
                alpha = 125;
                paint.setAlpha(alpha);
            }
            canvas.drawRect(newRect, paint);

        }

        paint.setAlpha(255);
    }

    void drawText(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(defaultFontSize);
        canvas.drawText(startHeareRate, startXPos + rectWidth + textGapPic, yHeight + getPaddingTop(), paint); // �������ֵ
        canvas.drawText(endHeartRate, startXPos + rectWidth + textGapPic, getPaddingTop() + mEndTextBound.height(), paint); // �������ֵ
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
        invalidate();

    }

}

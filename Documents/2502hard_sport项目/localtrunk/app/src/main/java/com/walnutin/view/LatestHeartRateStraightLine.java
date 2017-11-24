package com.walnutin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.walnutin.hard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class LatestHeartRateStraightLine extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint;
    private int defautColors;
    private int dealedColors;
    float xWidth;
    float yHeight;
    private int startRate = 40;
    int endRate = 180;
    int centerRate = 0;
    List<Integer> recentRateList = new ArrayList<>();
    private boolean isSurfaceCreated;
    private SurfaceHolder mSurfaceHolder;
    boolean isMeasuring = false;


    public LatestHeartRateStraightLine(Context context) {
        super(context);

    }

    public LatestHeartRateStraightLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    DashPathEffect dashPathEffect;

    void init() {
        paint = new Paint();
        paint.setStrokeWidth(dip2px(getContext(), 1.2f));
        paint.setAntiAlias(true);
        paint.setTextSize(dip2px(getContext(), 10));
        dealedColors = getResources().getColor(R.color.white);
        defautColors = getResources().getColor(R.color.text_color);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        dashPathEffect = new DashPathEffect(new float[]{3, 2}, 0);
    }

    float textGapPic = dip2px(getContext(), 5);
    float startXPos;
    float startYPos;

    Canvas canvas;

    public void drawRate() {
        if (isSurfaceCreated) {
            //  canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    paint.setColor(Color.RED);
                    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
                    drawRectList(canvas);
                }
            } finally {
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

    }

    // ��ʾ��� ��¼ʮ�� 255 һ�ν���

    private void drawRectList(Canvas canvas) {
        xWidth = getWidth();
        yHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        startXPos = 0;
        startYPos = yHeight + getPaddingTop();
        paint.setColor(dealedColors);
        //
        int perWidth = 0;
        int len = recentRateList.size();

        if (len < 2) {
            return;
        }
        if (isMeasuring) {
            perWidth = 50;     //分成 50份长度
        } else {
            perWidth = recentRateList.size() > 50 ? recentRateList.size() : 50;
        }

        float x2 = startXPos;
        float y2 = 0;
        float x1 = startXPos;
        float y1 = y2;
        paint.setPathEffect(dashPathEffect);
        float H1 =startYPos - (((float) (90 - startRate)) / (endRate - startRate)) * yHeight;
        float H2 =startYPos - (((float) (120 - startRate)) / (endRate - startRate)) * yHeight;
        canvas.drawLine(startXPos, startYPos, getWidth(), startYPos, paint);
        canvas.drawLine(startXPos, H1, getWidth(), H1, paint);
        canvas.drawLine(startXPos, H2, getWidth(), H2, paint);
        canvas.drawLine(startXPos, getPaddingTop(), getWidth(), getPaddingTop(), paint);

        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < len - 1; i++) {
            x2 = x1 + xWidth / perWidth;
            if (recentRateList.get(i + 1) >= endRate) {
                y2 = getPaddingTop();
            } else {
                y2 = startYPos - (((float) (recentRateList.get(i + 1) - startRate)) / (endRate - startRate)) * yHeight;

            }
            if (recentRateList.get(i) >= endRate) {
                y1 = getPaddingTop();
            } else {
                y1 = startYPos - (((float) (recentRateList.get(i) - startRate)) / (endRate - startRate)) * yHeight;
                //       System.out.println("startYPos: "+startYPos+"  y1: "+y1+" value: "+recentRateList.get(i)+" v: "+(((float) (recentRateList.get(i) - startRate)) / (endRate - startRate)));
                //         System.out.println("startYPos: startRate: "+startRate+" endRate: "+endRate);

            }
            canvas.drawLine(x1, y1, x2, y2, paint);
            x1 = x2;
        }

    }

    void drawText(Canvas canvas) {
        paint.setColor(Color.BLACK);
        //   canvas.drawText(startHeareRate, startXPos, yHeight + textGapPic + mStartTextBound.height(), paint);
        //   canvas.drawText(endHeartRate, xWidth - mEndTextBound.width(), yHeight + textGapPic + mEndTextBound.height(), paint);
        //     canvas.drawText(centerHeart, xWidth - mCenterTextBound.width(), yHeight + textGapPic + mCenterTextBound.height(), paint);

    }

    public void setMeasuringStatue(boolean isMeasuring) {
        this.isMeasuring = isMeasuring;
        drawRate();
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        //     System.out.println("dip2px" + scale);
        return (int) (dpValue * scale + 0.5f);
    }

    public void setLowHeartRate(int lowHeartRate) {
        if (lowHeartRate < startRate) {
            //   startHeareRate = String.valueOf(lowHeartRate);
            startRate = lowHeartRate;
            //   paint.getTextBounds(startHeareRate, 0, startHeareRate.length(), mStartTextBound);

        }

    }

    public void setHighHeartRate(int HighHeartRate) {

        if (HighHeartRate > endRate) {
            //   endHeartRate = String.valueOf(HighHeartRate);
            endRate = HighHeartRate;
            //    paint.getTextBounds(endHeartRate, 0, endHeartRate.length(), mEndTextBound);
        }
    }

    public void setCenterHeart(int centerHeart1) {
        //   centerHeart = String.valueOf(centerHeart1);
        centerRate = centerHeart1;
        //    paint.getTextBounds(centerHeart, 0, centerHeart.length(), mCenterTextBound);

    }

    public void setHeartRate(int lowHeartRate, int centerHeart1, int HighHeartRate) {
        if (lowHeartRate < startRate) {
            //   startHeareRate = String.valueOf(lowHeartRate);
            startRate = lowHeartRate;
            //  paint.getTextBounds(startHeareRate, 0, startHeareRate.length(), mStartTextBound);
        }

        if (HighHeartRate > endRate) {
            //   endHeartRate = String.valueOf(HighHeartRate);
            endRate = HighHeartRate;
            //     paint.getTextBounds(endHeartRate, 0, endHeartRate.length(), mEndTextBound);
        }
        //   centerHeart = String.valueOf(centerHeart1);
        centerRate = centerHeart1;
        //    paint.getTextBounds(centerHeart, 0, centerHeart.length(), mCenterTextBound);
        invalidate();
    }

    public void setRecentRateList(List list) {
        recentRateList = list;
        drawRate();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isSurfaceCreated = true;
        drawRate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        xWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        yHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        startXPos = 0;
        startYPos = yHeight - getPaddingBottom();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isSurfaceCreated = false;

    }

}

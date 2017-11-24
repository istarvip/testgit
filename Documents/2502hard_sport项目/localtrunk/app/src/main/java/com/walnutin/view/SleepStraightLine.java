package com.walnutin.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.walnutin.hard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class SleepStraightLine extends View {

    private Paint paint;
    private int defautColors;
    private int dealedColors;
    float xWidth;
    float yHeight;
    int progress = 0;
    final int TOTAL_PROGRESS = 100;
    private String startSleetTime = "11:30";
    private String endSleepTime = "07:30";
    private Rect mStartSleepTextBound;
    private Rect mEndSleepTextBound;
    Bitmap txtTipBitMap;
    private int MaxLen;        // ?????
    private int allDurationTime; // ???????????
    private int[] perDurationTime;  //???γ??????????
    private List<Integer> durationStartPos; // ???ο??λ??
    private int[] durationStatus; // ??????????
    private String tipSleepTime = "2:30 - 4:40";
    Rect tipTextBound;
    DisplayMetrics outMetrics;

    public SleepStraightLine(Context context) {
        super(context);
        init();
    }

    public SleepStraightLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setTextSize(dip2px(getContext(), 14));
        dealedColors = getResources().getColor(R.color.red_background_notselected);
        defautColors = getResources().getColor(R.color.text_color);
        mStartSleepTextBound = new Rect();
        mEndSleepTextBound = new Rect();
        tipTextBound = new Rect();
        paint.getTextBounds(startSleetTime, 0, startSleetTime.length(), mStartSleepTextBound);
        paint.getTextBounds(endSleepTime, 0, endSleepTime.length(), mEndSleepTextBound);
        paint.getTextBounds(tipSleepTime, 0, tipSleepTime.length(), tipTextBound);

        txtTipBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.tip_week_up);

        WindowManager manager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
    }

    float startXPos;
    float startYPos;

    float textGapPic = dip2px(getContext(), 5);
    int rectHeight = dip2px(getContext(), 9);  // ???ε???
    int deepRectHeight = dip2px(getContext(), 120);  // ???ε???
    int lightRectHeight = dip2px(getContext(), 70);  // ???ε???
    int soberRectHeight = dip2px(getContext(), 40);  // ???ε???

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float mTextHeight = mStartSleepTextBound.height();

        xWidth = outMetrics.widthPixels - getPaddingLeft() - getPaddingRight();
        yHeight = getHeight() - getPaddingBottom() - getPaddingTop() - mTextHeight - textGapPic;
        System.out.println("xWidth: " + xWidth + " yHeight: " + yHeight);

        startXPos = getPaddingLeft();
//        startYPos = yHeight - rectHeight;
//        System.out.println("xWidth startXPos: " + startXPos);
//        RectF rect = new RectF();
//        rect.left = startXPos;
//        rect.top = startYPos;
//        rect.bottom = yHeight;
//        rect.right = (xWidth);
//        paint.setColor(defautColors);
//        canvas.drawRect(rect, paint);   // ????????

        if (perDurationTime == null || durationStatus == null) {
            return;
        }
        /*
        *
        * ?????????
        * */
        drawSleepStatusRect(canvas);


        drawText(canvas);
    }

    private void drawSleepStatusRect(Canvas canvas) {

        paint.setColor(dealedColors);

        float initializePos = startXPos;
        for (int i = 0; i < perDurationTime.length; i++) {
            RectF newRect = new RectF();
            newRect.left = initializePos;

            newRect.right = initializePos + (((float) perDurationTime[i] / allDurationTime)) * xWidth;
       //     System.out.println("touchPos DrawStart "+initializePos);
         //   System.out.println("touchPos DrawEnd "+newRect.right);

            switch (durationStatus[i]) {
                case 0:
                    newRect.top = yHeight - deepRectHeight;
                    paint.setColor(Color.GREEN);
                    break;
                case 1:
                    newRect.top = yHeight - lightRectHeight;

                    paint.setColor(Color.BLUE);
                    break;
                case 2:
                    newRect.top = yHeight - soberRectHeight;

                    paint.setColor(Color.RED);
                    break;
            }
            if (touchPos == i) {
                tipXPos = (newRect.right + newRect.left) / 2;
            }

            newRect.bottom = yHeight;
            canvas.drawRect(newRect, paint);  // ???? ????
            initializePos = newRect.right;


        }

    }

    float tipXPos = 0;

    void drawText(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(dip2px(getContext(), 16));

        canvas.drawText(startSleetTime, startXPos - mStartSleepTextBound.width() / 4, yHeight + textGapPic + mStartSleepTextBound.height(), paint);
        canvas.drawText(endSleepTime, xWidth - mEndSleepTextBound.width() / 4, yHeight + textGapPic + mEndSleepTextBound.height(), paint);

        if (touchPos != -1) {
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(dip2px(getContext(), 12));
            canvas.drawBitmap(txtTipBitMap, tipXPos - txtTipBitMap.getWidth() / 2, getStartYPos(touchPos) - tipTextBound.height() / 2 - txtTipBitMap.getHeight(), paint);
            canvas.drawText(tipSleepTime, tipXPos - txtTipBitMap.getWidth() / 2, getStartYPos(touchPos) + tipTextBound.height() / 2 - txtTipBitMap.getHeight(), paint);

        }

    }


    public void setProgress(int prog) {
        this.progress = prog;
        invalidate();
    }

    public void update() {
        invalidate();
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        //     System.out.println("dip2px" + scale);
        return (int) (dpValue * scale + 0.5f);
    }

    public void setStartSleepTime(String startSleetTime) {
        this.startSleetTime = startSleetTime;
    }

    public void setEndSleepTime(String endSleepTime) {
        this.endSleepTime = endSleepTime;
    }

    public void setAllDurationTime(int allDurationTime) {
        this.allDurationTime = allDurationTime;
    }

    public void setPerDurationTime(int[] perDurationTime) {
        this.perDurationTime = perDurationTime;
    }

    public void setDurationStartPos(List<Integer> durationStartPos) {
        this.durationStartPos = durationStartPos;
    }

    public void setDurationStatus(int[] durationStatus) {
        this.durationStatus = durationStatus;
    }

    float getStartYPos(int touchPos) {
        if (touchPos == -1) {
            return -1;
        }

        switch (durationStatus[touchPos]) {
            case 0:
                return yHeight - deepRectHeight;
            case 1:
                return yHeight - lightRectHeight;
            case 2:
                return yHeight - soberRectHeight;
        }

        return 0;
    }


    boolean isTouchInYHeight(int pos,float mY) {
//        for (int i = 0; i < perDurationTime.length; i++) {
            switch (durationStatus[pos]) {
                case 0:
                    if (mY >= yHeight - deepRectHeight && mY < yHeight) {
                        return true;
                    }
                    break;
                case 1:
                    if (mY >= yHeight - lightRectHeight && mY < yHeight) {
                        return true;
                    }
                    break;
                case 2:
                    if (mY >= yHeight - soberRectHeight && mY < yHeight) {
                        return true;
                    }
                    break;
          //  }
        }
        return false;
    }

    boolean isFligerUp = true;

    List<Float> mXList;
    int touchPos = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPos = -1;
        if (perDurationTime == null || perDurationTime.length < 1) {
            return false;
        }
        mXList = new ArrayList<Float>();
        float initializePos = startXPos;
        int len = perDurationTime.length;

        for (int i = 0; i < len; i++) {
        //    System.out.println("touchPos  "+initializePos);
            mXList.add(initializePos);
            initializePos = initializePos + (((float) perDurationTime[i] / allDurationTime)) * xWidth;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                float mX = event.getX();
                float mY = event.getY();
                isFligerUp = false;
                len = mXList.size();
          //     System.out.println("touchPos  mX "+mX+" mWdith "+xWidth);

                for (int i = 0; i < len; i++) {
                    if (i != len - 1) {
                        if (mX >= mXList.get(i) && mX < mXList.get(i + 1) && isTouchInYHeight(i,mY)) {
                            touchPos = i;
                            tipSleepTime = TimeUtil.MinutiToTime(durationStartPos.get(i)) +
                                    " - " + TimeUtil.MinutiToTime(durationStartPos.get(i) + perDurationTime[i]);
                            break;
                        }
                    } else {
                        if (mX >= mXList.get(i) && mX < xWidth+startXPos && isTouchInYHeight(i,mY)) {
                            touchPos = i;
                            tipSleepTime = TimeUtil.MinutiToTime(durationStartPos.get(i)) +
                                    " - " + TimeUtil.MinutiToTime(durationStartPos.get(i) + perDurationTime[i]);
                            break;
                        }
                    }
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isFligerUp = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        return super.onTouchEvent(event);

    }
}

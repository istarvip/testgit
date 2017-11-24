package com.walnutin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.walnutin.entity.BaseInfo;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.DateType;
import com.walnutin.hard.R;
import com.walnutin.util.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/21.foc
 */
public class LineChart extends View {
    private Context context;
    Paint mPaint;
    int lineColor = Color.rgb(0xdf, 0x0b, 0x0b);
    int lineWidth = dipToPx(0.5f);
    int horizeColor = Color.rgb(0xe5, 0xe5, 0xe5);
    int perLineHight = dipToPx(50);
    float perPotWidth = 0;
    float mWidth = 0;
    float mHeight = 0;
    String mText = "10000步";
    private Rect mBoundText;
    String mDate = "5/19";
    private Rect mDataTextBound;
    private int mTextWidth;
    private int mTextHeight;
    private float MAXVALUE = 20000f;
    //float startPotPadding = dipToPx(14);
    float startPotPadding = 0;
    Bitmap bitmap;
    Bitmap bigBitBitMap;
    Bitmap txtTipBitMap;
    Bitmap txtTipDownBitMap;
    Bitmap txtWeekTipUpBitMap;
    Bitmap txtWeekTipDownBitMap;
    Bitmap txtMonthTipUpBitMap;
    Bitmap txtMonthTipDownBitMap;
    List<Integer> potLists = new ArrayList<Integer>();
    private List<BaseInfo> dailyInfoList = new ArrayList<>();
    DateType dateType = DateType.DATE_TYPE;
    DisplayMetrics outMetrics;
    private OnItemClicked itemClicked;

    public interface OnItemClicked {
        public void onItem(int pos);
    }

    public void setOnItemClicked(OnItemClicked onItemClicked) {
        this.itemClicked = onItemClicked;
    }

    public LineChart(Context context) {
        super(context);
        init();
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int cxScreen = outMetrics.widthPixels;//


        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(dipToPx(12));
        mWidth = getWidth();
        mHeight = getHeight();

        perPotWidth = getWidth() / 7;
        Log.i("result", "perPotWidth == " + perPotWidth + "  startPotPadding == " + startPotPadding);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap_notclick);
        bigBitBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap_clicked);
        txtTipBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.tipdayup);
        txtTipDownBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.tipdaydown);
        txtWeekTipUpBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.tip_week_up);
        txtWeekTipDownBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.tip_week_down);
        txtMonthTipUpBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.tip_month_up);
        txtMonthTipDownBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.tip_month_down);


        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);


    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    int paddingHeight;
    float textWidthPadding = dipToPx(2);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        if (potLists.size() <= 0) {
            return;
        }
        paddingHeight = bigBitBitMap.getHeight() / 2;
        mHeight = getHeight() - paddingHeight * 2;
        int cxScreen = outMetrics.widthPixels;
        switch (dateType) {
            case DATE_TYPE:
                perPotWidth = (cxScreen) / 7;
                startPotPadding = (cxScreen / 7) / 2;
                break;
            case MONTH_TYPE:
                perPotWidth = (cxScreen) / 6;
                startPotPadding = (cxScreen / 6) / 2;
                break;
            case YEAR_TYPE:
                perPotWidth = (cxScreen) / 6;
                startPotPadding = (cxScreen / 6) / 2;
                break;
        }
        mPaint.setColor(horizeColor);
        canvas.drawLine(0, getHeight() - paddingHeight, mWidth, getHeight() - paddingHeight, mPaint); //底部线条
        canvas.drawLine(0, getHeight() / 2, mWidth, getHeight() / 2, mPaint);//中间线条
        mPaint.setColor(lineColor);
        mPaint.setStyle(Paint.Style.FILL);

        // mText = MAXVALUE/2+"步";
        mBoundText = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mBoundText);
        mTextWidth = mBoundText.width();
        mTextHeight = mBoundText.height();

        mDataTextBound = new Rect();
        mPaint.getTextBounds(mDate, 0, mDate.length(), mDataTextBound);


        //mPaint.setStyle(Paint.Style.STROKE);
        canvas.save();

        drawCicleAndLine(canvas);

        canvas.restore();
        canvas.drawText(mText, mWidth - mTextWidth - dipToPx(2), getHeight() / 2 - dipToPx(3), mPaint);
    }

    private void drawCicleAndLine(Canvas canvas) {
        canvas.translate(startPotPadding, 0);
        float y1;
        float y2;
        if (potLists.size() > 1) {
            for (int i = 0; i < potLists.size() - 2; i++) {
                int potValue = potLists.get(i);
                y1 = paddingHeight + mHeight - ((potValue / MAXVALUE) * mHeight);
                y2 = paddingHeight + mHeight - ((potLists.get(i + 1) / MAXVALUE) * mHeight);
                canvas.drawLine(0, y1, perPotWidth, y2, mPaint);
                if (touchPos == i) {
                    drawTextAndTouchedCircle(canvas, y1, potValue);
                } else {
                    canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2, y1 - bitmap.getHeight() / 2, null);
                }
                canvas.translate(perPotWidth, 0);
            }
            y1 = paddingHeight + mHeight - ((potLists.get(potLists.size() - 2) / MAXVALUE) * mHeight);
            y2 = paddingHeight + mHeight - ((potLists.get(potLists.size() - 1) / MAXVALUE) * mHeight);
            canvas.drawLine(0, y1, perPotWidth, y2, mPaint);
            if (touchPos == potLists.size() - 2) {
                int potValue = potLists.get(touchPos);
                drawTextAndTouchedCircle(canvas, y1, potValue);
            } else {
                canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2, y1 - bitmap.getHeight() / 2, null);
            }
            //canvas.translate(perPotWidth + bitmap.getWidth(), 0);
            canvas.translate(perPotWidth, 0);
        }
        y1 = paddingHeight + mHeight - ((potLists.get(potLists.size() - 1) / MAXVALUE) * mHeight);  //画最后一个圆
        if (touchPos == potLists.size() - 1) {
            int potValue = potLists.get(touchPos);
            drawTextAndTouchedCircle(canvas, y1, potValue);
        } else {
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2, y1 - bitmap.getHeight() / 2, null);
        }
    }


    void drawTextAndTouchedCircle(Canvas canvas, float y1, int potValue) {
        canvas.drawBitmap(bigBitBitMap, -bigBitBitMap.getWidth() / 2, y1 - bigBitBitMap.getHeight() / 2, null);
        if (potValue <= MAXVALUE / 2) {
            switch (dateType) {
                case DATE_TYPE:
                    canvas.drawBitmap(txtTipBitMap, -txtTipBitMap.getWidth() / 2, y1 - bigBitBitMap.getHeight() - txtTipBitMap.getHeight(), mPaint);
                    canvas.drawText(mDate, -txtTipBitMap.getWidth() / 2 + textWidthPadding,
                            y1 - bigBitBitMap.getHeight() - txtTipBitMap.getHeight() + mDataTextBound.height() + textWidthPadding, mPaint);
                    break;
                case MONTH_TYPE:
                    canvas.drawBitmap(txtWeekTipUpBitMap, -txtWeekTipUpBitMap.getWidth() / 2, y1 - bigBitBitMap.getHeight() - txtWeekTipUpBitMap.getHeight(), mPaint);
                    canvas.drawText(mDate, -txtWeekTipUpBitMap.getWidth() / 2 + textWidthPadding,
                            y1 - bigBitBitMap.getHeight() - txtWeekTipUpBitMap.getHeight() + mDataTextBound.height() + textWidthPadding, mPaint);

                    break;
                case YEAR_TYPE:
                    canvas.drawBitmap(txtMonthTipUpBitMap, -txtMonthTipUpBitMap.getWidth() / 2, y1 - bigBitBitMap.getHeight() - txtMonthTipUpBitMap.getHeight(), mPaint);
                    canvas.drawText(mDate, -txtMonthTipUpBitMap.getWidth() / 2 + textWidthPadding,
                            y1 - bigBitBitMap.getHeight() - txtMonthTipUpBitMap.getHeight() + mDataTextBound.height() + textWidthPadding, mPaint);

                    break;
            }
        } else {
            switch (dateType) {
                case DATE_TYPE:
                    canvas.drawBitmap(txtTipDownBitMap, -txtTipDownBitMap.getWidth() / 2, y1 + bigBitBitMap.getHeight(), mPaint);
                    canvas.drawText(mDate, -txtTipDownBitMap.getWidth() / 2 + textWidthPadding,
                            y1 + txtTipDownBitMap.getHeight() + mDataTextBound.height() - dipToPx(1), mPaint);
                    break;
                case MONTH_TYPE:
                    canvas.drawBitmap(txtWeekTipDownBitMap, -txtWeekTipDownBitMap.getWidth() / 2, y1 + bigBitBitMap.getHeight(), mPaint);
                    canvas.drawText(mDate, -txtWeekTipDownBitMap.getWidth() / 2 + textWidthPadding,
                            y1 + txtWeekTipDownBitMap.getHeight() + mDataTextBound.height() - dipToPx(1), mPaint);
                    break;
                case YEAR_TYPE:
                    canvas.drawBitmap(txtMonthTipDownBitMap, -txtMonthTipDownBitMap.getWidth() / 2, y1 + bigBitBitMap.getHeight(), mPaint);
                    canvas.drawText(mDate, -txtMonthTipDownBitMap.getWidth() / 2 + textWidthPadding,
                            y1 + txtMonthTipDownBitMap.getHeight() + mDataTextBound.height() - dipToPx(1), mPaint);
                    break;
            }


        }

    }


    public void setPotPosition(List<Integer> potPosition) {
        potLists = potPosition;
        invalidate();
    }

    int touchPos = -1;

    public void setDailyList(List df) {
        dailyInfoList = df;
        potLists.clear();
        for (int i = 0; i < dailyInfoList.size(); i++) {
            potLists.add(dailyInfoList.get(i).getStep());
        }

        for (int i = 0; i < potLists.size(); i++) {
            if (potLists.get(i) > MAXVALUE) {
                potLists.set(i, (int) MAXVALUE);
            }
        }
        invalidate();
    }

    public void setLineChartType(DateType type) {
        dateType = type;
    }

    public void setWeekDay(String day) {
        mDate = DateUtils.formatData(day);
    }

    public void setGoalValue(int goalValue) {
        mText = String.valueOf(goalValue + "步");
        MAXVALUE = goalValue * 2;
    }

    public void setMAXVALUE(int maxvalue) {
        MAXVALUE = maxvalue;
        mText = String.valueOf((int) MAXVALUE / 2 + "步");
    }

    public void setTouchPos(int pos) {
        if (pos >= dailyInfoList.size()) {
            return;
        }
        touchPos = pos;
        switch (dateType) {
            case DATE_TYPE:
                mDate = DateUtils.formatData(dailyInfoList.get(pos).getDates());     //格式化日期
                break;
            case MONTH_TYPE:
                mDate = DateUtils.formatMonthData(dailyInfoList.get(pos).getDates());
                break;
            case YEAR_TYPE:
                mDate = DateUtils.formatYearData(dailyInfoList.get(pos).getDates());
                break;
        }
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //    System.out.println("-------------touch---------------------");
        List<Float> mYList = new ArrayList<Float>();
        List<Float> mXList = new ArrayList<Float>();
        mXList.add(startPotPadding);
        int len = 7;
        switch (dateType) {
            case DATE_TYPE:
                len = 7;
                break;
            case MONTH_TYPE:
                len = 6;
                break;
            case YEAR_TYPE:
                len = 6;
                break;
            default:
                len = 7;
        }

        for (int i = 1; i < len; i++) {
            mXList.add(startPotPadding + (perPotWidth) * i);
        }


        for (int i = 0; i < potLists.size(); i++) {
            mYList.add(paddingHeight + mHeight - ((potLists.get(i) / MAXVALUE) * mHeight));
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float mX = event.getX();
                float mY = event.getY();
                System.out.println("mX: " + mX + " mY: " + mY);
                for (int i = 0; i < mXList.size(); i++) {
                    if (mX >= mXList.get(i) - bigBitBitMap.getWidth() && mX <= mXList.get(i) + bigBitBitMap.getWidth()) {
                        if (i < potLists.size()) {
                            if (mY > mYList.get(i) - bigBitBitMap.getWidth() && mY < mYList.get(i) + bigBitBitMap.getWidth()) {
                                touchPos = i;
                                if (itemClicked != null) {
                                    itemClicked.onItem(touchPos);
                                }
                                break;
                            }

                        }
                    }

                }


                break;
        }
        //   touchPos = -1;
        return super.onTouchEvent(event);
    }
}

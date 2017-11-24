package com.walnutin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.walnutin.hard.R;

import java.util.Calendar;

/**
 * 作者：MrJiang on 2016/6/20 15:14
 */
public class RotateView extends View {
    private Bitmap bitmapBig;//随手指转动的图片
    private float mPointX = 0, mPointY = 0;// 圆心坐标
    // 旋转角度
    private int mAngle = 0;
    private int beginAngle = 0, currentAngle = 0;
    RotateViewListener listener;
    long beginTime, endTime;
    boolean isUp = false, isTouch = false;
    Calendar now;

    public RotateView(Context context) {
        super(context);
        init();
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bitmapBig = BitmapFactory.decodeResource(getResources(), R.drawable.rotate_back);

    }

    @Override
    public void onDraw(Canvas canvas) {
        mPointX = bitmapBig.getWidth() / 2;
        mPointY = bitmapBig.getHeight() / 2;
        if (isUp) {
            mAngle = 0;
        } else {
            mAngle = currentAngle - beginAngle;
        }
        beginAngle = mAngle;
        Bitmap tempBig = adjustPhotoRotation(bitmapBig, mAngle);
        drawInCenter(canvas, tempBig, mPointX, mPointY + 10, "haha");
    }

    private void drawInCenter(Canvas canvas, Bitmap bitmap, float left,
                              float top, String text) {
        canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2,
                top - bitmap.getHeight() / 2, null);
    }

    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        if (orientationDegree == 0) {
            return bm;
        }
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
                (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;

        } catch (OutOfMemoryError ex) {
        }

        return null;

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                now = Calendar.getInstance();
                beginTime = now.getTimeInMillis();
            //    beginAngle = computeCurrentAngle(e.getX(), e.getY());
                System.out.println("currentAngle  beginAngle"+beginAngle);
                isUp = false;
                //如果点击触摸范围在圈外，则不处理
                if (getDistance(e.getX(), e.getY()) > bitmapBig.getWidth() / 2) {
                    isTouch = false;
                } else {
                    isTouch = true;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isTouch) {
                    return true;
                }
                currentAngle = computeCurrentAngle(e.getX(), e.getY());
                System.out.println("currentAngle:" + currentAngle);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
//                isUp = true;
//                if (!isTouch) {
//                    return true;
//                }
//                beginAngle = currentAngle;
                return true;
        }

        return false;
    }

    // 获取距离圆心的距离
    private float getDistance(float x, float y) {
        // 根据圆心坐标计算角度
        float distance = (float) Math
                .sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
                        * (y - mPointY)));
        return distance;
    }

    private int computeCurrentAngle(float x, float y) {
        // 根据圆心坐标计算角度
        float distance = (float) Math
                .sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
                        * (y - mPointY)));
        int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
        if (y < mPointY) {
            degree = -degree;
        }
        if (degree < 0) {
            degree += 360;
        }

        // Log.i("RoundSpinView", "x:" + x + ",y:" + y + ",degree:" + degree);
        return degree;
    }


    public interface RotateViewListener {
        void onModChange(int mode);
    }
}

package com.walnutin.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.walnutin.activity.MyApplication;
import com.walnutin.hard.R;

/**
 * Created by assa on 2016/5/24.
 */
public class MyGoalFragment extends Fragment {
    private View mView;
    /**
     * 转盘背景
     */
    private ImageView turntable_back;
    /**
     * 指针背景
     */
    private ImageView rotate_ba;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.fragment_goal, null);
        turntable_back = (ImageView) mView.findViewById(R.id.turntable_back);
        rotate_ba = (ImageView) mView.findViewById(R.id.rotate_ba);
        init();
        return mView;

    }

    float rotate_xup1;
    float rotate_yup1;
    float rotate_xup2;
    float rotate_yup2;
    int i = 1;

    private void init() {
        rotate_ba.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取当前坐标
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rotate_xup1 = x;
                        rotate_yup1 = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        rotate_xup2 = event.getX();
                        rotate_yup2 = event.getY();
                        System.out.println("滑动参值 x1=" + rotate_xup1 + "; x2=" + rotate_xup2);
                        if (rotate_xup1 != 0 && rotate_yup1 != 0) {
                            System.out.println("shdjksahdkjas");
                            if (rotate_xup1 - rotate_xup2 < 8) {
                                if (i == 1) {
                                    setAnimation(0f,90f);
                                    i++;
                                } else if (i == 2) {
                                    setAnimation(90f, 180f);
                                    i++;
                                } else if (i == 3) {
                                    setAnimation(180f, 270f);
                                    i++;
                                }else if (i==4){
                                    reinstall();
                                }
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private long lastBackPressedTime = 0;

    private void reinstall() {
        if (System.currentTimeMillis() - lastBackPressedTime < 1000) {
            setAnimation(270f,0f);
        } else {
            lastBackPressedTime = System.currentTimeMillis();
            Toast.makeText(MyApplication.instance(), "再按一次重新设置", Toast.LENGTH_LONG).show();
        }
    }

    private void setAnimation(float x, float y) {
        final RotateAnimation animation = new RotateAnimation(x, y, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);// 设置动画持续时间
        animation.setRepeatCount(0);// 设置重复次数
        animation.setFillAfter(true);
        rotate_ba.startAnimation(animation);
        if (i==4){
            i=1;
        }

    }

}

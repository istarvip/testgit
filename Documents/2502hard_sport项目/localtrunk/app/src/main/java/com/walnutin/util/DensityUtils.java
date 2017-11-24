package com.walnutin.util;

import android.content.Context;

import com.walnutin.activity.MyApplication;

/**
 * Created by Administrator on 2016/5/9.
 */
public class DensityUtils {
    public  static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
     //   System.out.println("dip2px" + scale);
        return (int) (dpValue * scale + 0.5f);
    }
    public  static int dip2px( float dpValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
     //   System.out.println("dip2px" + scale);
        return (int) (dpValue * scale + 0.5f);
    }
}

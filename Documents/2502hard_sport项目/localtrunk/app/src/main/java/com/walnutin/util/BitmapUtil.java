package com.walnutin.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by chenliu on 16/5/25.
 */
public class BitmapUtil {


    /**
     * 不需要指定大小,img为wrapcontent
     *
     * @param context
     * @param url
     * @param img
     */
    public static void loadBitmap(Context context, String url, ImageView img) {
        Glide.with(context)
                .load(url)
                .into(img);
    }

    public static void loadSignalBitmap(Context context, String url, ImageView img,String sig) {
        Glide.with(context)
                .load(url).signature(new StringSignature(sig))
                .into(img);
    }


    /**
     * 指定具体大小
     *
     * @param context
     * @param url
     * @param img
     * @param width
     * @param height
     */
    public static void loadBitmap(Context context, String url, ImageView img,int width, int height) {
        Glide.with(context)
                .load("http://nuuneoi.com/uploads/source/playstore/cover.jpg")
                .override(width, height)
                .into(img);
    }


    /**
     * img为非wrapcontent时,需要自动缩放时使用此方法
     *
     * @param context
     * @param url
     * @param img
     */
    public static void loadBitmap(Context context, String url, ImageView img, boolean isAutoFit) {
        if (isAutoFit) {
            Glide.with(context)
                    .load(url)
                    .fitCenter()
                    .centerCrop()
                    .into(img);
        } else {
            loadBitmap(context, url, img);
        }
    }

    /**
     *
     * @param context
     * @param url
     * @param img
     * @param placeholderResId  占位图R.id
     * @param imagenotfoundResId  图片未找到R.id
     */
    public static void loadBitmap(Context context, String url, int placeholderResId,int imagenotfoundResId,ImageView img) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholderResId)
                .error(imagenotfoundResId).dontAnimate()
                .into(img);
    }


    public static void loadBitmap(Context context, String url, int placeholderResId, Drawable imagenotfound, ImageView img) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholderResId)
                .error(imagenotfound)
                .into(img);
    }



    public static void loadBitmap(Context context, String url, Drawable placeholder, Drawable imagenotfound, ImageView img) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .error(imagenotfound)
                .into(img);
    }



    public static void loadBitmap(Context context, String url, Drawable placeholder, int imagenotfoundResId, ImageView img) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .error(imagenotfoundResId)
                .into(img);
    }


    public static void loadBitmap(Context context, Integer resId, ImageView img) {
            Glide.with(context)
                    .load(resId)
                    .fitCenter()
                    .centerCrop()
                    .into(img);
    }


    /**
     * 本地圆形图片
     * @param context
     * @param resId
     * @param img
     */
    public static void loadCircleBitmap(Context context, Integer resId, ImageView img) {
        Glide.with(context)
                .load(resId)
                .bitmapTransform(new CropCircleTransformation(context))
                .fitCenter()
                .centerCrop()
                .into(img);
    }

    /**
     * 网络圆形图片
     * @param context
     * @param url
     * @param img
     */
    public static void loadCircleBitmap(Context context, String url, ImageView img) {
        Glide.with(context)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(context))
                .fitCenter()
                .centerCrop()
                .into(img);
    }


}

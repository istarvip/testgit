package com.walnutin.hardsdkdemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

/**
 * 作者：MrJiang on 2016/8/25 18:30
 */
public class RingtonePlayUtil {

    static Dialog mDialog;
    static MediaPlayer mediaPlayer;

    public static void playRing(Activity context, String pickedUrl, final int time) {

        // System.out.println("mDialog: " + mDialog);
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        Uri pickedUri;
        if (pickedUrl == null) {
            pickedUri = RingtoneManager.getActualDefaultRingtoneUri(context,
                    RingtoneManager.TYPE_RINGTONE);
        } else {
            pickedUri = Uri.parse(pickedUrl);
        }

        try {
            if (mediaPlayer.isPlaying() == false) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, pickedUri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  final Ringtone myRingTone = RingtoneManager.getRingtone(context, pickedUri);
        //  myRingTone.play();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("丢失提示");
        builder.setMessage("手环已超出丢失范围");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        try {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mediaPlayer = null;

                        mDialog.cancel();
                    }
                });

        //   builder.create().show();
        mDialog = builder.create();
        //  mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
        mDialog.show();

//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle("丢失提示");
//        dialog.setIcon(android.R.drawable.ic_dialog_info);
//        dialog.setMessage("手环已超出丢失范围");
//        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (myRingTone.isPlaying()) {
//                    myRingTone.stop();
//                }
//                dialog.cancel();
//            }
//        });
//
//        mDialog = dialog.create();
//        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
//        mDialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    while (i < time / 1000 && mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Thread.sleep(1000);
                        i++;
                    }
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    if (mDialog != null) {
                        mDialog.cancel();
                    }

                }
            }
        });
        thread.start();

    }

    class RingRunable implements Runnable {

        @Override
        public void run() {

        }
    }


    private void showBox(final Context context) {
    }
}

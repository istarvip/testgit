package com.walnutin.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.walnutin.util.TrackUploadUtil;

public class PowerReceiver extends BroadcastReceiver {

	@SuppressLint("Wakelock")
	@Override
	public void onReceive(final Context context, final Intent intent) {
		final String action = intent.getAction();

		if (Intent.ACTION_SCREEN_OFF.equals(action)) {
			System.out.println("screen off,acquire wake lock!");
			if (null != TrackUploadUtil.wakeLock && !(TrackUploadUtil.wakeLock.isHeld())) {
				TrackUploadUtil.wakeLock.acquire();
			}
		} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
			System.out.println("screen on,release wake lock!");
			if (null != TrackUploadUtil.wakeLock && TrackUploadUtil.wakeLock.isHeld()) {
				TrackUploadUtil.wakeLock.release();
			}
		}
	}

}

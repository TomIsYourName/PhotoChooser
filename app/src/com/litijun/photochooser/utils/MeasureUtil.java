package com.litijun.photochooser.utils;

import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

@TargetApi(Build.VERSION_CODES.DONUT)
public class MeasureUtil {
	private static MeasureUtil instance = null;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private DisplayMetrics metrics;

	private MeasureUtil(Context context) {
		metrics = context.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		DebugLog.v(String.format(Locale.getDefault(),"%d * %d,smallestWidth=%d",screenWidth,screenHeight, 
                screenWidth*160/metrics.densityDpi));
	}

	public static MeasureUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (MeasureUtil.class) {
				if (instance == null) {
					instance = new MeasureUtil(context);
				}
			}
		}
		return instance;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public DisplayMetrics getMetrics() {
		return metrics;
	}

}

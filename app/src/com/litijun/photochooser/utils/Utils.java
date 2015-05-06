package com.litijun.photochooser.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;


public class Utils {
    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @param screenSize 屏幕尺寸信息：width = screenSize[0]; heigth = screenSize[1];
     */
    public static void GetScreenSize(Context context, int[] screenSize) {
        if (null == screenSize || screenSize.length < 2) {
            screenSize = new int[2];
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenSize[0] = displayMetrics.widthPixels;
        screenSize[1] = displayMetrics.heightPixels;
    }
    
    public static String getImagePath(Context context, int firstImageId){
    	String firstImagePath = "";
    	Uri uri_temp = Uri.parse("content://media/external/images/media/" + firstImageId);
		Cursor cur = MediaStore.Images.Media.query(context.getContentResolver(), uri_temp, new String[] { MediaStore.Images.Media.DATA });
		if (cur != null && cur.moveToFirst()) {
			firstImagePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
		}
		else {
			firstImagePath = "";
		}
		if(cur != null) cur.close();
		return firstImagePath;
    }
}

package com.litijun;

import android.app.Application;
import android.content.Context;

import com.litijun.photochooser.utils.ImageLoaderUtil;

public class PhotoApp extends Application {

    private static Context context;
    
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		ImageLoaderUtil.getInstance(this);
	}
	
	public static Context getContext(){
	    return context;
	}
}

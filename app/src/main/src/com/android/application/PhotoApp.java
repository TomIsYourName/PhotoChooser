package com.android.application;

import android.app.Application;
import android.content.Context;

import com.android.imageLoaderUtil.ImageLoaderUtil;

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

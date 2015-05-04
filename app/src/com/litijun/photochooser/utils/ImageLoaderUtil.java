package com.litijun.photochooser.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;


public class ImageLoaderUtil {

	private static ImageLoaderUtil instance;
	private float density;
	
	private ImageLoaderUtil(Context context){
		
//		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
//        .memoryCacheExtraOptions(DisplayUtils.getScreenWidth(this), DisplayUtils.getScreenHeight(this)) // default = device screen dimensions
//        .threadPoolSize(4) // default
//        .threadPriority(Thread.NORM_PRIORITY - 2) // default
//        .memoryCache(new LruMemoryCache(15 * 1024 * 1024))
//        .memoryCacheSize(20 * 1024 * 1024)
//        .tasksProcessingOrder(QueueProcessingType.LIFO)
//        .defaultDisplayImageOptions(displayOptions)
//        .writeDebugLogs()
//        .build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	        .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
	        .diskCacheExtraOptions(480, 800, null)
	        .threadPoolSize(4) // default
	        .threadPriority(Thread.NORM_PRIORITY - 2) // default
	        .tasksProcessingOrder(QueueProcessingType.LIFO) // default
	        .denyCacheImageMultipleSizesInMemory()
	        .memoryCache(new LruMemoryCache(15 * 1024 * 1024))
	        .memoryCacheSize(20 * 1024 * 1024)
	        .memoryCacheSizePercentage(13) // default
	        .diskCache(new UnlimitedDiscCache(AppFileManager.getInstance(context).createFileDir("imgCache"))) 
	        .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
	        .imageDownloader(new BaseImageDownloader(context)) // default
	        .writeDebugLogs()
	        .build();
		
		ImageLoader.getInstance().init(config);
		
		density = MeasureUtil.getInstance(context).getMetrics().density;
	}
	
	public static ImageLoaderUtil getInstance(Context context){
		if(instance == null){
			synchronized (ImageLoaderUtil.class) {
				if(instance == null){
					instance = new ImageLoaderUtil(context);
				}
			}
		}
		return instance;
	}
	
	/**
	 * 创建带有圆角的DisplayImageOptions对象
	 * @param dfImg 默认显示图片ID
	 * @param roundDIP 圆角弧度,单位为dip
	 * @return
	 */
	public DisplayImageOptions createRoundedOptions(int dfImg,int roundDIP){
		DisplayImageOptions roundedAvatarOptios = new DisplayImageOptions.Builder()//
			.showImageForEmptyUri(dfImg)//
			.showImageOnLoading(dfImg)//
			.showImageOnFail(dfImg)//
			.resetViewBeforeLoading(true)//
			.cacheOnDisk(true)//
			.cacheInMemory(true)//
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//
			.bitmapConfig(Bitmap.Config.RGB_565)//
			.considerExifParams(true)//
			.displayer(new RoundedBitmapDisplayer((int)(roundDIP * density)))//
			.build();
		
		return roundedAvatarOptios;
	}
	
	/**
	 * 创建不带圆角的DisplayImageOptions
	 * @param dfImg
	 * @return
	 */
	public DisplayImageOptions createNoRoundedOptions(int dfImg){
		DisplayImageOptions options = new DisplayImageOptions.Builder()//
			.showImageForEmptyUri(dfImg)//
			.showImageOnLoading(dfImg)//
			.showImageOnFail(dfImg)//
			.resetViewBeforeLoading(true)//
			.cacheOnDisk(true)//
			.cacheInMemory(true)//
			.imageScaleType(ImageScaleType.EXACTLY)//EXACTLY_STRETCHED
			.bitmapConfig(Bitmap.Config.RGB_565)//
			.considerExifParams(true)//
			.build();
		return options;
	}
}

package com.litijun.photochooser.manager;

import android.content.Context;
import android.widget.ImageView;

import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.vo.ImageItem;
import com.litijun.photochooser.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

import java.util.HashMap;
import java.util.Map;

public class ImageLoaderMgr {
	
	enum ImageLoadType {
        LOADING_TYPE_BIGIMAGE, LOADING_TYPE_THUMBNAILIMAGE
    }
	
    private static ImageLoaderMgr instance;
    private Context context;
    private ImageLoader thumbnailImageLoader;
    private ImageLoader realImageLoader;
    private Map<Integer, ImageItem> imageItemMap;
    private int maxSelectSize;

    private ImageLoaderMgr(Context context) {
        this.context = context;
//        this.thumbnailImageLoader = new ImageLoader(context, ImageLoader.ImageLoadType.LOADING_TYPE_THUMBNAILIMAGE);
//        this.realImageLoader = new ImageLoader(context, ImageLoader.ImageLoadType.LOADING_TYPE_BIGIMAGE);
        this.maxSelectSize = 0;
        this.imageItemMap = new HashMap<Integer, ImageItem>(maxSelectSize);
    }

    public static ImageLoaderMgr getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoaderMgr(context);
        }
        return instance;
    }

    public int getMaxSelectSize() {
        return maxSelectSize;
    }

    public void setMaxSelectSize(int maxSelectSize) {
        this.maxSelectSize = maxSelectSize;
    }

    public int getSelectCount() {
        return imageItemMap.size();
    }

    public boolean addSelect(ImageItem item) {
        if (imageItemMap.containsKey(item.id)) {
            return true;
        } else if (imageItemMap.size() >= maxSelectSize) {
            return false;
        } else {
            imageItemMap.put(item.id, item);
            return true;
        }
    }

    public boolean removeSelect(ImageItem item) {
        if (imageItemMap.containsKey(item.id)) {
            imageItemMap.remove(item.id);
        }
        return true;
    }

    public ImageItem getImageItem(int key) {
        return imageItemMap.get(key);
    }

    public void dispalyThumnailImage(Integer uri, final ImageView imageView) {
        imageView.setImageResource(R.drawable.image_loading_default);
//        thumbnailImageLoader.loadingImage(uri, new ImageLoader.LoadedCallBack() {
//            @Override
//            public void startLoad() {
//
//            }
//
//            @Override
//            public void imageLoaded(Bitmap bigBitmap, Bitmap thumbnailBitmap) {
//                imageView.setImageBitmap(thumbnailBitmap);
//            }
//        });
    }

    public void dispalyRealImage(Integer uri, final ImageView imageView) {
        imageView.setImageResource(R.drawable.image_loading_default);
//        realImageLoader.loadingImage(uri, new ImageLoader.LoadedCallBack() {
//            @Override
//            public void startLoad() {
//
//            }
//
//            @Override
//            public void imageLoaded(Bitmap bigBitmap, Bitmap thumbnailBitmap) {
//                imageView.setImageBitmap(thumbnailBitmap);
//            }
//        });
        
    }
    
    public void dispalyImage(String picPath, final ImageView imageView) {
//    	DebugLog.d(picPath);
    	ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(picPath), imageView,
    			ImageLoaderUtil.getInstance(context).createNoRoundedOptions(R.drawable.image_loading_default));
    }
}

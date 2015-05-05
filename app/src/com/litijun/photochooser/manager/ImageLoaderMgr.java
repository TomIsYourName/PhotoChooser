package com.litijun.photochooser.manager;

import android.content.Context;
import android.widget.ImageView;

import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.vo.ImageItem;
import com.litijun.photochooser.utils.DebugLog;
import com.litijun.photochooser.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageLoaderMgr {

	private static volatile ImageLoaderMgr	instance;
	private Context							context;
	private Map<Integer, ImageItem>			imageItemMap;
	private List<ImageItem>					allImageList;
	private int								maxSelectSize;
	private boolean							isTakePhoto;

	private ImageLoaderMgr(Context context) {
		this.context = context;
		this.maxSelectSize = 0;
		this.imageItemMap = new HashMap<Integer, ImageItem>(maxSelectSize);
		this.allImageList = new ArrayList<ImageItem>();
	}

	public static ImageLoaderMgr getInstance(Context context) {
		synchronized(ImageLoaderMgr.class){
			if (instance == null) {
				instance = new ImageLoaderMgr(context);
			}
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

	public Map<Integer, ImageItem> getImageItemMap() {
		return imageItemMap;
	}

	public List<ImageItem> getAllImageList() {
		return allImageList;
	}

	public List<ImageItem> getSeletectList() {
		List<ImageItem> data = new ArrayList<ImageItem>();
		Map<Integer, ImageItem> imageItemMap = ImageLoaderMgr.getInstance(context).getImageItemMap();
		DebugLog.v("imageItemMap.size() = " + imageItemMap.size());
		for (Map.Entry<Integer, ImageItem> entry : imageItemMap.entrySet()) {
			Integer key = entry.getKey();
			ImageItem value = entry.getValue();
			if (value != null && value instanceof ImageItem) {
				data.add((ImageItem) value);
			}
		}
		return data;
	}

	public boolean addSelect(ImageItem item) {
		if (imageItemMap.containsKey(item.id)) {
			return true;
		}
		else if (imageItemMap.size() >= maxSelectSize) {
			return false;
		}
		else {
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

	public boolean isTakePhoto() {
		return isTakePhoto;
	}

	public void setTakePhoto(boolean isTakePhoto) {
		this.isTakePhoto = isTakePhoto;
	}

	public void dispalyImage(String picPath, final ImageView imageView) {
		ImageLoader.getInstance().displayImage(Scheme.FILE.wrap(picPath), imageView, ImageLoaderUtil.getInstance(context).createNoRoundedOptions(R.drawable.image_loading_default));
	}
}

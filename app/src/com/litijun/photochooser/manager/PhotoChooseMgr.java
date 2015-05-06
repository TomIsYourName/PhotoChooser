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

public class PhotoChooseMgr {

	private static volatile PhotoChooseMgr instance;
	private Context							context;
	private Map<Integer, ImageItem>         selectedItemMap;
	private List<ImageItem>					allImageList;
	private int								maxSelectSize;
	private boolean							isTakePhoto;

	private PhotoChooseMgr(Context context) {
		this.context = context;
		this.maxSelectSize = 0;
		this.selectedItemMap = new HashMap<Integer, ImageItem>(maxSelectSize);
		this.allImageList = new ArrayList<ImageItem>();
	}

	public static PhotoChooseMgr getInstance(Context context) {
		synchronized(PhotoChooseMgr.class){
			if (instance == null) {
				instance = new PhotoChooseMgr(context);
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
		return selectedItemMap.size();
	}

	public Map<Integer, ImageItem> getSelectedItemMap() {
		return selectedItemMap;
	}

	public List<ImageItem> getAllImageList() {
		return allImageList;
	}

	public List<ImageItem> getSeletectList() {
		List<ImageItem> data = new ArrayList<ImageItem>();
		Map<Integer, ImageItem> imageItemMap = PhotoChooseMgr.getInstance(context).getSelectedItemMap();
		DebugLog.v("SeletectList.size() = " + imageItemMap.size());
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
		if (selectedItemMap.containsKey(item.id)) {
			return true;
		}
		else if (selectedItemMap.size() >= maxSelectSize) {
			return false;
		}
		else {
			selectedItemMap.put(item.id, item);
			return true;
		}
	}

	public boolean removeSelect(ImageItem item) {
		if (selectedItemMap.containsKey(item.id)) {
			selectedItemMap.remove(item.id);
		}
		return true;
	}

	public void clearSelect(){
        selectedItemMap.clear();
	}

	public ImageItem getImageItem(int key) {
		return selectedItemMap.get(key);
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

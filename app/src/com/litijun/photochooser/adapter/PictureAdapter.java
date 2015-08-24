package com.litijun.photochooser.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litijun.photochooser.PhotoChooseActivity;
import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.vo.ImageItem;
import com.litijun.photochooser.manager.PhotoChooseMgr;
import com.litijun.photochooser.utils.DebugLog;
import com.litijun.photochooser.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class PictureAdapter extends BaseAdapter {
	private int							itemSize;
	private LayoutInflater				inflater;
	private View.OnClickListener		listener;
	private Context						context;
	private RelativeLayout.LayoutParams	params;
	private PhotoChooseMgr loaderManager;
	private ContentResolver resolver;
	private DisplayImageOptions displayOptions;

	public PictureAdapter(Activity activity) {
		inflater = activity.getLayoutInflater();
		context = activity;
		resolver = context.getContentResolver();
		loaderManager = PhotoChooseMgr.getInstance(context);

		// 计算每个项的高度：高度=宽度
		int[] point = new int[2];
		Utils.GetScreenSize(context, point);
		int spaceSize = context.getResources().getDimensionPixelSize(R.dimen.gridview_space_size) * 2;
		itemSize = (point[0] - spaceSize) / 3;

		listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				if (!holder.checkBox.isChecked()) {
					holder.checkBox.setChecked(false);
					loaderManager.removeSelect(holder.imageItem);
				}
				else {
					if (!loaderManager.addSelect(holder.imageItem)) {
						holder.checkBox.setChecked(false);
						Toast.makeText(context, context.getString(R.string.max_pic, loaderManager.getMaxSelectSize()), Toast.LENGTH_LONG).show();
					}
					else {
						holder.checkBox.setChecked(true);
					}
				}
				((PhotoChooseActivity)context).changeSelectedCount();
			}
		};

		displayOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.image_loading_default)
				.showImageOnFail(R.drawable.image_loading_default)
				.imageScaleType(ImageScaleType.EXACTLY)
				.cacheInMemory(true)
				.cacheOnDisk(false)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

	@Override
	public int getCount() {
		int size = PhotoChooseMgr.getInstance(context).getAllImageList().size();
		if (PhotoChooseMgr.getInstance(context).isTakePhoto()) {
			size += 1;
		}
		return size;
	}

	@Override
	public ImageItem getItem(int position) {

		if (PhotoChooseMgr.getInstance(context).isTakePhoto()) {
			position = position - 1;
		}
		return PhotoChooseMgr.getInstance(context).getAllImageList().get(position);
	}

	@Override
	public long getItemId(int position) {
		ImageItem item = PhotoChooseMgr.getInstance(context).getAllImageList().get(position);
		if (item == null)
			return 0;
		return item.id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_picture, null);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.picture_checkbox);
			holder.imageView = (ImageView) convertView.findViewById(R.id.picture_imageview);
			holder.textView = (TextView) convertView.findViewById(R.id.text);
			holder.checkBox.setOnClickListener(listener);
			params = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
			params.height = itemSize;
			holder.imageView.setLayoutParams(params);

			holder.checkBox.setTag(holder);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (PhotoChooseMgr.getInstance(context).getAllImageList() != null) {
			if (position == 0 && PhotoChooseMgr.getInstance(context).isTakePhoto()) {
				holder.checkBox.setChecked(false);
				holder.checkBox.setVisibility(View.GONE);
				holder.imageView.setImageResource(R.drawable.take_photo);
			}
			else {
				ImageItem item = getItem(position);
				holder.imageItem = item;
				holder.textView.setText(item.name);
				holder.checkBox.setChecked(loaderManager.getImageItem(item.id) != null && loaderManager.getSelectCount() <= loaderManager.getMaxSelectSize());
				holder.checkBox.setVisibility(View.VISIBLE);
//				PhotoChooseMgr.getInstance(context).dispalyImage(item.realPath, holder.imageView);
				ImageAware imageAware = new ImageViewAware(holder.imageView, false);
				ImageLoader.getInstance().displayImage(resolver, ImageDownloader.Scheme.FILE.wrap(item.realPath), item.id, imageAware, displayOptions);

			}
		}
		return convertView;
	}

	public void setLoadCursor(Cursor loadCursor) {
		PhotoChooseMgr.getInstance(context).getAllImageList().clear();
		if (loadCursor != null) {
			DebugLog.d("loadCursor Size = " + loadCursor.getCount());
			for (int i = 0, count = loadCursor.getCount(); i < count; i++) {
				loadCursor.moveToPosition(i);
				ImageItem item = new ImageItem();
				item.id = loadCursor.getInt(loadCursor.getColumnIndex(MediaStore.Images.Media._ID));
				item.name = loadCursor.getString(loadCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				item.realPath = loadCursor.getString(loadCursor.getColumnIndex(MediaStore.Images.Media.DATA));
				item.albumId = loadCursor.getInt(loadCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
				
				PhotoChooseMgr.getInstance(context).getAllImageList().add(item);
			}
			DebugLog.d("AllImageList Size = " + PhotoChooseMgr.getInstance(context).getAllImageList().size());
		}
		notifyDataSetChanged();
	}

	class ViewHolder {
		ImageItem	imageItem;
		ImageView	imageView;
		CheckBox	checkBox;
		TextView	textView;
	}
}

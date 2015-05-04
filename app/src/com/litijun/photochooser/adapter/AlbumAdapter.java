package com.litijun.photochooser.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.vo.AlbumItem;
import com.litijun.photochooser.manager.ImageLoaderMgr;
import com.litijun.photochooser.utils.DebugLog;

public class AlbumAdapter extends BaseAdapter {
	private Context					context;
	private Map<Integer, AlbumItem>	albumMap;
	private Cursor					albumCursor;
	private int						currAlumbId;

	public AlbumAdapter(Context context) {
		this.context = context;
		this.albumMap = new HashMap<Integer, AlbumItem>();
	}

	@Override
	public int getCount() {
		if (albumCursor == null) {
			return 0;
		}
		return albumCursor.getCount();
	}

	@Override
	public AlbumItem getItem(int position) {
		return albumMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		return albumMap.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_album, null);
			holder.currFlagView = (ImageView) convertView.findViewById(R.id.alumb_curr_flag);
			holder.countView = (TextView) convertView.findViewById(R.id.alumb_count);
			holder.nameView = (TextView) convertView.findViewById(R.id.alubm_name);
			holder.imageView = (ImageView) convertView.findViewById(R.id.alumb_picture);

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (albumCursor != null) {
			albumCursor.moveToPosition(position);

			AlbumItem albumItem;
			if (!albumMap.containsKey(position + 1)) {
				// 创建相册对象
				albumItem = new AlbumItem();
				albumItem.firstImageId = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));

				Uri uri_temp = Uri.parse("content://media/external/images/media/" + albumItem.firstImageId);
				Cursor cur = MediaStore.Images.Media.query(context.getContentResolver(), uri_temp, new String[] { MediaStore.Images.Media.DATA });
				if (cur != null && cur.moveToFirst()) {
					albumItem.firstImagePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
				}
				else {
					albumItem.firstImagePath = "";
				}
				albumItem.albumName = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
				albumItem.id = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
				albumItem.imageCount = albumCursor.getInt(albumCursor.getColumnIndex("allbum_count"));
				albumMap.put(position + 1, albumItem);
			}
			else {
				albumItem = albumMap.get(position);
			}

			holder.nameView.setText(albumItem.albumName);
			holder.countView.setText(albumItem.imageCount + "张");
			holder.currFlagView.setVisibility(albumItem.id == this.currAlumbId ? View.VISIBLE : View.GONE);
			// ImageLoaderMgr.getInstance(context).dispalyThumnailImage(albumItem.firstImageId,
			// holder.imageView);

			DebugLog.d(albumItem.firstImagePath);
			ImageLoaderMgr.getInstance(context).dispalyImage(albumItem.firstImagePath, holder.imageView);
		}
		return convertView;
	}

	public void setCurrAlumbId(int currAlumbId) {
		this.currAlumbId = currAlumbId;
		notifyDataSetChanged();
	}

	public Cursor getAlbumCursor() {
		return albumCursor;
	}

	public void setAlbumCursor(Cursor albumCursor) {
		this.albumCursor = albumCursor;
		notifyDataSetChanged();
	}

	public void setItem(int position, AlbumItem item) {
		albumMap.put(position, item);
	}

	class ViewHolder {
		TextView	nameView;
		TextView	countView;
		ImageView	currFlagView;
		ImageView	imageView;
	}
}

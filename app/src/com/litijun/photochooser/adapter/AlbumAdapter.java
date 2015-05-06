package com.litijun.photochooser.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.vo.AlbumItem;
import com.litijun.photochooser.manager.PhotoChooseMgr;
import com.litijun.photochooser.utils.DebugLog;
import com.litijun.photochooser.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class AlbumAdapter extends BaseAdapter {
	private Context					context;
	private Map<Integer, AlbumItem>	albumMap;
	private int						currAlumbId;

	public AlbumAdapter(Context context) {
		this.context = context;
		this.albumMap = new HashMap<Integer, AlbumItem>();
	}

	@Override
	public int getCount() {
		if (albumMap == null) {
			return 0;
		}
		return albumMap.size();
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

		if (albumMap != null) {
			DebugLog.d("position = " + position);
			AlbumItem albumItem = getItem(position);
			holder.nameView.setText(albumItem.albumName);
			holder.countView.setText(albumItem.imageCount + "张");
			holder.currFlagView.setVisibility(albumItem.id == this.currAlumbId ? View.VISIBLE : View.GONE);
			PhotoChooseMgr.getInstance(context).dispalyImage(albumItem.firstImagePath, holder.imageView);
		}
		return convertView;
	}

	public void setCurrAlumbId(int currAlumbId) {
		this.currAlumbId = currAlumbId;
		notifyDataSetChanged();
	}

	public void setAlbumCursor(Cursor albumCursor) {
		albumMap.clear();
		if (albumCursor != null) {
			DebugLog.d("loadCursor Size = " + albumCursor.getCount());
			for (int i = 0, count = albumCursor.getCount(); i < count; i++) {
				albumCursor.moveToPosition(i);
				AlbumItem albumItem = new AlbumItem();
				albumItem.firstImageId = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
				albumItem.firstImagePath = Utils.getImagePath(context, albumItem.firstImageId);
				albumItem.albumName = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
				albumItem.id = albumCursor.getInt(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
				albumItem.imageCount = albumCursor.getInt(albumCursor.getColumnIndex("allbum_count"));
				albumMap.put(i + 1, albumItem);
			}
			DebugLog.d("albumMap Size = " + albumMap.size());
		}
		notifyDataSetChanged();
	}

	public void setItem(int position, AlbumItem item) {
		albumMap.put(position, item);
		setCurrAlumbId(item.id);
	}

	class ViewHolder {
		TextView	nameView;
		TextView	countView;
		ImageView	currFlagView;
		ImageView	imageView;
	}
}

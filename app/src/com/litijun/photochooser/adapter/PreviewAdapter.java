package com.litijun.photochooser.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.vo.ImageItem;
import com.litijun.photochooser.fragment.PreviewFragment;
import com.litijun.photochooser.manager.PhotoChooseMgr;
import com.litijun.photochooser.widgets.photoview.PhotoView;

import java.util.List;

/**
 * Created by zain on 15-5-4.
 */
public class PreviewAdapter extends PagerAdapter {

	private Fragment 		fragment;
	private Context			mContext;
	private List<ImageItem>	data;
	private int				offset	= 0;
	

	public PreviewAdapter(Fragment fragment, List<ImageItem> data, int offset) {
		this.mContext = fragment.getActivity();
		this.fragment = fragment;
		this.data = data;
		this.offset = offset;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.item_preview, null);
		PhotoView iv_preview = (PhotoView) view.findViewById(R.id.iv_preview);

		position = position + offset;
		if (position < 0) {
			position += getCount();
		}
		else {
			position = position % getCount();
		}
		String imgpath = getCount() > 0 ? data.get(position).realPath : "";
		
		iv_preview.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				((PreviewFragment)fragment).handleHearderOrBottom();
				return true;
			}
		});

		PhotoChooseMgr.getInstance(mContext).dispalyImage(imgpath, iv_preview);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
	}
}

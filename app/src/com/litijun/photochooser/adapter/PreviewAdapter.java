package com.litijun.photochooser.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.litijun.photochooser.R;
import com.litijun.photochooser.manager.ImageLoaderMgr;

import java.util.List;

/**
 * Created by zain on 15-5-4.
 */
public class PreviewAdapter extends PagerAdapter{

    private Context mContext;
    private List<String> data;

    public PreviewAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.data = data;
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
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_preview, null);
        ImageView iv_preview = (ImageView) view.findViewById(R.id.iv_preview);
        ImageLoaderMgr.getInstance(mContext).dispalyImage(data.get(position), iv_preview);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}

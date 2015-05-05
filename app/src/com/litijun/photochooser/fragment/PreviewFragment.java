package com.litijun.photochooser.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.litijun.photochooser.ChooseImageActivity;
import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.PreviewAdapter;
import com.litijun.photochooser.adapter.vo.ImageItem;
import com.litijun.photochooser.manager.ImageLoaderMgr;
import com.litijun.photochooser.utils.DebugLog;
import com.litijun.photochooser.widgets.DepthPageTransformer;
import com.litijun.photochooser.widgets.FixedScroller;

import java.lang.reflect.Field;
import java.util.List;

public class PreviewFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager vp_preview;
    private CheckBox preview_image_cb;
    private Button header_right_button;
    private Button header_back;

    private List<ImageItem> data = null;

    public PreviewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        vp_preview = (ViewPager) getView().findViewById(R.id.vp_preview);
        preview_image_cb = (CheckBox) getView().findViewById(R.id.preview_image_cb);
        header_back = (Button) getView().findViewById(R.id.header_back);
        header_right_button = (Button) getView().findViewById(R.id.header_right_button);

        preview_image_cb.setOnClickListener(this);
        header_back.setOnClickListener(this);
        header_right_button.setOnClickListener(this);

        int offset = args == null ? 0 : args.getInt("offset", 0);
        boolean showAll = args == null ? false : args.getBoolean("show_all", false);
        if(showAll){
            data = ImageLoaderMgr.getInstance(getActivity()).getAllImageList();
        }else{
            data = ImageLoaderMgr.getInstance(getActivity()).getSeletectList();
        }
        vp_preview.setAdapter(new PreviewAdapter(getActivity(), data, 0));
        vp_preview.setOnPageChangeListener(this);
        vp_preview.setCurrentItem(offset);
        initVPAnim();
        changeSelectedCount();
        setCheckStatus(offset);

    }


    public void changeSelectedCount(){

        ImageLoaderMgr imageLoaderMgr = ImageLoaderMgr.getInstance(getActivity());
        int selectedCount = imageLoaderMgr.getSelectCount();
        if(selectedCount>0)
            header_right_button.setText(getString(R.string.select_done, selectedCount, imageLoaderMgr.getMaxSelectSize()));
        else
            header_right_button.setText("完成");
    }

    public void setCheckStatus(int position){
        if(data!= null && data.size()>0){
            ImageItem item = data.get(position);
            ImageLoaderMgr imageLoaderMgr = ImageLoaderMgr.getInstance(getActivity());
            preview_image_cb.setTag(item);
            preview_image_cb.setChecked(imageLoaderMgr.getImageItem(item.id) != null && imageLoaderMgr.getSelectCount() <= imageLoaderMgr.getMaxSelectSize());
        }
    }

    public void changeCheckStatus(ImageItem item){
        ImageLoaderMgr imageLoaderMgr = ImageLoaderMgr.getInstance(getActivity());
        if (!preview_image_cb.isChecked()) {
            preview_image_cb.setChecked(false);
            imageLoaderMgr.removeSelect(item);
        }
        else {
            if (!imageLoaderMgr.addSelect(item)) {
                preview_image_cb.setChecked(false);
                Toast.makeText(getActivity(), getString(R.string.max_pic, imageLoaderMgr.getMaxSelectSize()), Toast.LENGTH_LONG).show();
            }
            else {
                preview_image_cb.setChecked(true);
            }
        }
        changeSelectedCount();
    }

    private void initVPAnim() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(vp_preview.getContext(), sInterpolator);
            mScroller.set(vp_preview, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //vp_preview.setPageTransformer(true, new ZoomOutPageTransformer());
        vp_preview.setPageTransformer(true, new DepthPageTransformer());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.header_back:
                ((ChooseImageActivity)getActivity()).onBackPressed();
                break;
            case R.id.preview_image_cb:
                ImageItem item = (ImageItem)v.getTag();
                changeCheckStatus(item);
                break;
            case R.id.header_right_button:
                ((ChooseImageActivity)getActivity()).finish();
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int pState) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int pPosition) {
        DebugLog.d("Position = " + pPosition);
        setCheckStatus(pPosition);
    }
}

package com.litijun.photochooser.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litijun.photochooser.ChooseImageActivity;
import com.litijun.photochooser.R;
import com.litijun.photochooser.adapter.PreviewAdapter;
import com.litijun.photochooser.widgets.LoopViewPager;

import java.util.List;

public class PreviewFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private LoopViewPager vp_preview;

    public PreviewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        int offset = args == null ? 0 : args.getInt("offset", 0);
        boolean showAll = args == null ? false : args.getBoolean("show_all", false);
        vp_preview = (LoopViewPager) getView().findViewById(R.id.vp_preview);
        List<String> data = ((ChooseImageActivity) getActivity()).getSelectedPhotos();
        if(showAll) data = ((ChooseImageActivity) getActivity()).getAllPhotos();
        vp_preview.setAdapter(new PreviewAdapter(getActivity(), data, offset));
        vp_preview.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPageScrollStateChanged(int pState) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int pPosition) {
    }
}

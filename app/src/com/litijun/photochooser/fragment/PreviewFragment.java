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

import java.util.List;

public class PreviewFragment extends Fragment implements View.OnClickListener {

    public PreviewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager vp_preview = (ViewPager) getView().findViewById(R.id.vp_preview);
        List<String> data = ((ChooseImageActivity) getActivity()).getSelectedPhotos();
        vp_preview.setAdapter(new PreviewAdapter(getActivity(), data));
    }

    @Override
    public void onClick(View v) {

    }
}

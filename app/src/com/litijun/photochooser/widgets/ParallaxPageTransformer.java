package com.litijun.photochooser.widgets;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.litijun.photochooser.R;

public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {

        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1);
        } else if (position <= 1) { // [-1,1]
            View dummyView = view.findViewById(R.id.iv_preview);
            dummyView.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }


    }
}

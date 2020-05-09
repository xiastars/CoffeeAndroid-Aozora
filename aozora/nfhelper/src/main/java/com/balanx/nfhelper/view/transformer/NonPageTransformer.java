package com.balanx.nfhelper.view.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author https://github.com/hongyangAndroid/MagicViewPager
 */
public class NonPageTransformer implements ViewPager.PageTransformer
{
    @Override
    public void transformPage(View page, float position)
    {
        page.setScaleX(0.999f);//hack
    }

    public static final ViewPager.PageTransformer INSTANCE = new NonPageTransformer();
}
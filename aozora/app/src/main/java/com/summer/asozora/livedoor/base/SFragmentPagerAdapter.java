package com.summer.asozora.livedoor.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by xiastars on 2017/8/7.
 */

public class SFragmentPagerAdapter extends FragmentPagerAdapter {


    private List<Fragment> fragmentList;
    private String[] titleList;

    public SFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public void setTitleList(String[] titleList){
        this.titleList = titleList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return fragmentList != null ? fragmentList.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList[position];
    }
}

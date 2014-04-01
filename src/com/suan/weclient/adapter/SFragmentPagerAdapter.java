package com.suan.weclient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by lhk on 2/27/14.
 */
public class SFragmentPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> pagerItemList;


    public SFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> pagerItemList) {

        super(fm);
        this.pagerItemList = pagerItemList;

    }

    @Override
    public int getCount() {
        return pagerItemList.size();
    }

    @Override
    public Fragment getItem(int position) {


        Fragment fragment = null;
        if (position < pagerItemList.size())
            fragment = pagerItemList.get(position);
        else
            fragment = pagerItemList.get(0);

        return fragment;

    }


}

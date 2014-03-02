package com.suan.weclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.suan.weclient.R;
import com.suan.weclient.adapter.SFragmentPagerAdapter;
import com.suan.weclient.fragment.intro.IntroFifthFragment;
import com.suan.weclient.fragment.intro.IntroFirstFragment;
import com.suan.weclient.fragment.intro.IntroForthFragment;
import com.suan.weclient.fragment.intro.IntroSecondFragment;
import com.suan.weclient.fragment.intro.IntroThirdFragment;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.view.IndecatorView;

import java.util.ArrayList;

public class IntroActivity extends SherlockFragmentActivity implements ViewPager.OnPageChangeListener {

    /**
     * Called when the activity is first created.
     */
    private ViewPager viewPager;
    private SFragmentPagerAdapter pagerAdapter;

    private IndecatorView indecatorView;
    private ArrayList<Fragment> fragmentList;
    private IntroFirstFragment firstFragment;
    private IntroSecondFragment secondFragment;
    private IntroThirdFragment thirdFragment;
    private IntroForthFragment forthFragment;
    private IntroFifthFragment fifthFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* request no title mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        initWidgets();


    }

    private void initWidgets() {

        viewPager = (ViewPager) findViewById(R.id.intro_pager);
        indecatorView = (IndecatorView) findViewById(R.id.intro_indecator);

        fragmentList = new ArrayList<Fragment>();

        firstFragment = new IntroFirstFragment();
        secondFragment = new IntroSecondFragment();
        thirdFragment = new IntroThirdFragment();
        forthFragment = new IntroForthFragment();
        fifthFragment = new IntroFifthFragment();


        fragmentList.add(firstFragment);
        fragmentList.add(secondFragment);
        fragmentList.add(thirdFragment);
        fragmentList.add(forthFragment);
        fragmentList.add(fifthFragment);

        pagerAdapter = new SFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(IntroActivity.this);

    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {
        indecatorView.setIndexScroll(i, v);

    }

    @Override
    public void onPageSelected(int i) {
/*
        if (i == fragmentList.size() - 1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferenceManager.putUserFirstEnter(IntroActivity.this, false);


                    Intent jumbIntent = new Intent();
                    int userGroupSize = SharedPreferenceManager.getUserGroup(getApplicationContext()).size();
                    if (userGroupSize == 0) {
                        Bundle nowBundle = new Bundle();
                        nowBundle.putInt(SplashActivity.JUMB_KEY_ENTER_STATE,
                                SplashActivity.JUMB_VALUE_INTENT_TO_LOGIN);
                        jumbIntent.putExtras(nowBundle);
                        jumbIntent.setClass(IntroActivity.this, LoginActivity.class);

                    } else {
                        jumbIntent.setClass(IntroActivity.this, MainActivity.class);

                    }

                    startActivity(jumbIntent);
                    finish();

                }
            }, 2000);

        }
*/

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


}

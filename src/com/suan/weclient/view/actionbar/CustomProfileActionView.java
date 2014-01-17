package com.suan.weclient.view.actionbar;

import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.FansHolder;

public class CustomProfileActionView extends LinearLayout {

    private boolean indexPlaceSet = false;
    private Resources resources;
    private DataManager mDataManager;
    private RelativeLayout customLayout;


    public CustomProfileActionView(Context context) {
        this(context, null);
    }

    public CustomProfileActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProfileActionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

    }

    public void init(DataManager dataManager) {
        mDataManager = dataManager;


        initListener();
        initWidgets();

    }

    private void initListener() {

    }


    private void initWidgets() {
        Context context = getContext();

        resources = context.getResources();

        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        customLayout = (RelativeLayout) layoutInflater.inflate(
                R.layout.custom_actionbar_profile, null);

		/*
         * interesting : I declare the match parent attribute in xml but it make
		 * no sense,unless I write it again in java
		 */
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        addView(customLayout, layoutParams);

    }



}

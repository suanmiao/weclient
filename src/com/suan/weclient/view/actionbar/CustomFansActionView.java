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
import com.suan.weclient.view.SFansDropListWindow;

public class CustomFansActionView extends LinearLayout {

    private boolean indexPlaceSet = false;
    private TextView indexTextView;
    private RelativeLayout indexLayout;
    private Resources resources;
    private DataManager mDataManager;
    private RelativeLayout customLayout;


    /*
     * about popupwindow
     */

    private SFansDropListWindow sFansDropListWindow;

    public CustomFansActionView(Context context) {
        this(context, null);
    }

    public CustomFansActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFansActionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

    }

    public void init(DataManager dataManager) {
        mDataManager = dataManager;


        initListener();
        initWidgets();

    }

    private void initListener() {
        mDataManager.addFansListChangeListener(new DataManager.FansListChangeListener() {
            @Override
            public void onFansGet(boolean changed) {
                if (mDataManager.getCurrentFansHolder().getFansGroupBeans().size() > 0) {
                    FansHolder currentFansHolder = mDataManager.getCurrentFansHolder();
                    int currentGroupIndex = currentFansHolder.getCurrentGroupIndex();
                    if(currentGroupIndex==-1){
                        //all

                        indexTextView.setText(getResources().getString(R.string.all_user));
                    }else{

                        indexTextView.setText(currentFansHolder.getFansGroupBeans().get(currentGroupIndex).getGroupName());
                    }

                }

            }
        });

    }


    private void initWidgets() {
        Context context = getContext();

        resources = context.getResources();

        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        customLayout = (RelativeLayout) layoutInflater.inflate(
                R.layout.custom_actionbar_fans, null);
        indexLayout = (RelativeLayout) customLayout.findViewById(R.id.actionbar_fans_left_layout);
        indexTextView = (TextView) customLayout.findViewById(R.id.actionbar_fans_left_text_first);

        indexLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sFansDropListWindow.isShowing()) {
                    dismissDropDownWindow();

                } else {
                    showDropDownWindow(v);

                }
            }
        });

        initDropDownWindow();


		/*
         * interesting : I declare the match parent attribute in xml but it make
		 * no sense,unless I write it again in java
		 */
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        addView(customLayout, layoutParams);

    }

    private void initDropDownWindow() {
        View dropDownView = ((LayoutInflater) getContext().getSystemService(
                Service.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.drop_down_layout, null);
        sFansDropListWindow = new SFansDropListWindow(mDataManager, getContext(), dropDownView,
                (int) Util.dipToPx(100, resources), LayoutParams.WRAP_CONTENT, true);
        sFansDropListWindow.setBackgroundDrawable(resources
                .getDrawable(R.drawable.drop_down_window_bg));
        sFansDropListWindow.setOutsideTouchable(true);
        sFansDropListWindow.setTouchable(true);

    }

    private void showDropDownWindow(View view) {
        sFansDropListWindow.showAsDropDown(view, 0, 0);

    }

    private void dismissDropDownWindow() {
        sFansDropListWindow.dismiss();

    }


}

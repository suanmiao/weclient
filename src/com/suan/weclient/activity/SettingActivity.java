package com.suan.weclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.suan.weclient.R;
import com.suan.weclient.pushService.AlarmReceiver;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.view.actionbar.CustomProfileActionView;

public class SettingActivity extends SherlockActivity {

    private ActionBar actionBar;
    private DataManager mDataManager;
    private RelativeLayout pushEnableLayout, pushFrequentLayout;
    private RelativeLayout pushEnableCheckboxLayout;
    private TextView pushFrequentTextView;

    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.setting_layout);

        initWidgets();
        initData();
        initActionBar();
        initListener();

    }

    private void initWidgets() {
        pushEnableLayout = (RelativeLayout) findViewById(R.id.setting_layout_push);
        pushFrequentLayout = (RelativeLayout) findViewById(R.id.setting_layout_push_frequent);

        pushEnableCheckboxLayout = (RelativeLayout) findViewById(R.id.setting_layout_checkbox);
        pushFrequentTextView = (TextView) findViewById(R.id.setting_text_push_frequent);

        boolean pushEnable = SharedPreferenceManager.getPushEnable(this);
        if (pushEnable) {
            pushFrequentLayout.setSelected(false);
            pushEnableCheckboxLayout.setSelected(true);

        } else {

            pushFrequentLayout.setSelected(true);
            pushEnableCheckboxLayout.setSelected(false);

        }

        pushEnableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pushEnable = SharedPreferenceManager.getPushEnable(SettingActivity.this);
                if (pushEnable) {
                    SharedPreferenceManager.putPustEnable(SettingActivity.this, false);

                    Intent stopPushIntent = new Intent(SettingActivity.this, AlarmReceiver.class);
                    stopPushIntent.setAction(AlarmReceiver.BROADCAST_ACTION_STOP_PUSH);

                    sendBroadcast(stopPushIntent);


                    pushFrequentLayout.setSelected(true);
                    pushEnableCheckboxLayout.setSelected(false);

                } else {
                    SharedPreferenceManager.putPustEnable(SettingActivity.this, true);


                    Intent startPush = new Intent(SettingActivity.this, AlarmReceiver.class);
                    startPush.setAction(AlarmReceiver.BROADCAST_ACTION_START_PUSH);

                    sendBroadcast(startPush);
                    pushFrequentLayout.setSelected(false);
                    pushEnableCheckboxLayout.setSelected(true);

                }


            }
        });

        pushFrequentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected()) {

                }
            }
        });

    }


    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);


        CustomProfileActionView customProfileActionView = new CustomProfileActionView(this);
        customProfileActionView.init(mDataManager);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customProfileActionView, layoutParams);


    }

    private void initData() {

        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();


    }

    private void initListener() {

    }


}

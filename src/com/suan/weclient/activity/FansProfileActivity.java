package com.suan.weclient.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.suan.weclient.R;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.view.actionbar.CustomProfileActionView;

public class FansProfileActivity extends SherlockActivity  {

    private ActionBar actionBar;
	private DataManager mDataManager;
	private FansHandler fansHandler;

	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fans_profile_layout);

        initWidgets();
        initData();
        initActionBar();
		initListener();

	}

	private void initWidgets() {


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

		fansHandler = new FansHandler();


	}

	private void initListener() {

	}

	public class FansHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			super.handleMessage(msg);

		}
	}


}

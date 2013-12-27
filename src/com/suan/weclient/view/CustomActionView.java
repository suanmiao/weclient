package com.suan.weclient.view;

import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity.ShowMenuListener;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.PagerListener;

public class CustomActionView extends LinearLayout {

	private boolean indexPlaceSet = false;
	private LinearLayout indexLayout;
	private ScrollView indexScrollView;
	private Resources resources;
	private DataManager mDataManager;
	private RelativeLayout customLayout;

	private ImageView showMenuImageView;
	private RelativeLayout firstIndecatorLayout, secondIndecatorLayout;
	private RelativeLayout firstContentLayout, secondContentLayout;

	/*
	 * about popupwindow
	 */
	private ShowMenuListener showMenuListener;

	private SPopUpWindow sPopUpWindow;

	public CustomActionView(Context context) {
		this(context, null);
	}

	public CustomActionView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomActionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);

	}

	public void init(DataManager dataManager) {
		mDataManager = dataManager;

		mDataManager.setPagerListener(new PagerListener() {

			@Override
			public void onScroll(int page, double pagePercent) {
				// TODO Auto-generated method stub

				setScrollPercent(page, pagePercent);

			}

			@Override
			public void onPage(int page) {
				// TODO Auto-generated method stub

			}
		});

		initWidgets();

	}

	public void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (!indexPlaceSet) {

			indexScrollView.scrollTo((int) Util.dipToPx(40,resources), 0);
			indexPlaceSet = true;
		}

	}

	private void initWidgets() {
		Context context = getContext();

		resources = context.getResources();

		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		customLayout = (RelativeLayout) layoutInflater.inflate(
				R.layout.custom_actionbar_main, null);

		showMenuImageView = (ImageView) customLayout
				.findViewById(R.id.actionbar_img_show_menu);

		showMenuImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMenuListener.showLeftMenu();

			}
		});

		indexScrollView = (ScrollView) customLayout
				.findViewById(R.id.actionbar_main_scroll_index);

		indexLayout = (LinearLayout) customLayout
				.findViewById(R.id.actionbar_main_layout_index);

		firstContentLayout = (RelativeLayout) customLayout
				.findViewById(R.id.actionbar_left_layout_first);
		secondContentLayout = (RelativeLayout) customLayout
				.findViewById(R.id.actionbar_left_layout_second);

		initDropDownWindow();
		firstContentLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sPopUpWindow.isShowing()) {
					dismissDropDownWindow();

				} else {

					showDropDownWindow(v);
				}

			}
		});

		firstIndecatorLayout = (RelativeLayout) customLayout
				.findViewById(R.id.actionbar_main_layout_first);

		secondIndecatorLayout = (RelativeLayout) customLayout
				.findViewById(R.id.actionbar_main_layout_second);
		firstIndecatorLayout.setSelected(true);
		firstIndecatorLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("width", CustomActionView.this.getWidth() + "|"
						+ customLayout.getWidth());
				mDataManager.getTabListener().onClickTab(0);

			}
		});

		secondIndecatorLayout.setSelected(false);
		secondIndecatorLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDataManager.getTabListener().onClickTab(1);

			}
		});

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
		sPopUpWindow = new SPopUpWindow(mDataManager, dropDownView,
				(int) Util.dipToPx(100,resources), LayoutParams.WRAP_CONTENT, true);
		sPopUpWindow.setBackgroundDrawable(resources
				.getDrawable(R.drawable.drop_down_window_bg));
		sPopUpWindow.setOutsideTouchable(true);
		sPopUpWindow.setTouchable(true);

	}

	private void showDropDownWindow(View view) {
		sPopUpWindow.updateData();
		sPopUpWindow.showAsDropDown(view, 0, 0);

	}

	private void dismissDropDownWindow() {
		sPopUpWindow.dismiss();

	}

	private void setPage(int page) {

		switch (page) {
		case 0:
			firstIndecatorLayout.setSelected(true);
			secondIndecatorLayout.setSelected(false);
			firstContentLayout.setVisibility(View.VISIBLE);
			secondContentLayout.setVisibility(View.GONE);

			break;

		case 1:
			firstIndecatorLayout.setSelected(false);
			secondIndecatorLayout.setSelected(true);
			firstContentLayout.setVisibility(View.GONE);
			secondContentLayout.setVisibility(View.VISIBLE);

			break;

		}
	}

	private void setScrollPercent(int page, double pagePercent) {

		// Log.e("page", page+"|"+pagePercent);
		int index = (page+pagePercent)>0.5?1:0;
		setPage(index);

		double percent = (page + pagePercent);

		float scrollWidth = Util.dipToPx(80, resources);
		float indexWidth = Util.dipToPx(40,resources);
		double scrollDelta = scrollWidth / 2 * percent;
		int scrollX = (int) (indexWidth - scrollDelta);

		indexScrollView.scrollTo(scrollX, 0);

	}

	public void setShowMenuListener(ShowMenuListener showMenuListener) {
		this.showMenuListener = showMenuListener;
	}


}

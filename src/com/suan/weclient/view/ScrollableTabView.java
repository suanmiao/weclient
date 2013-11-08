package com.suan.weclient.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v4.view.ViewPager;

public class ScrollableTabView extends HorizontalScrollView {

	private ViewPager mPager = null;

	private TabAdapter mAdapter = null;
	private final LinearLayout mContainer;
	private	RelativeLayout parentLayout;
	private final ArrayList<View> mTabs = new ArrayList<View>();

	public ScrollableTabView(Context context) {
		this(context, null);
	}

	public ScrollableTabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollableTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		this.setHorizontalScrollBarEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);

		
		parentLayout = new RelativeLayout(context);
		
		mContainer = new LinearLayout(context);
	}
	
	public void onFinishInflate(){
		
		super.onFinishInflate();
	}
	
	

	public void setAdapter(TabAdapter tabAdapter) {
		this.mAdapter = tabAdapter;
		if (mPager != null && mAdapter != null) {
			initTabs();
		}
		
	}

	public void setViewPage(ViewPager pager) {
		this.mPager = pager;
		if (mPager != null && mAdapter != null) {
			initTabs();
		}
	}

	private void initTabs() {
		mContainer.removeAllViews();
		mTabs.clear();

		if (mAdapter == null) {
			return;
		}
		for (int i = 0; i < mPager.getAdapter().getCount(); i++) {
			final int index = i;
			View tab = mAdapter.getView(i);
			mContainer.addView(tab);
			tab.setFocusable(true);
			mTabs.add(tab);

			tab.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (mPager.getCurrentItem() == index) {
						selectTab(index);
					} else {
						mPager.setCurrentItem(index, true);
					}
				}
			});
		}
		selectTab(mPager.getCurrentItem());
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		if(!inited){
			
			init();
			inited = true;
		}
			
		
		if (changed && mPager != null) {
			selectTab(mPager.getCurrentItem());
		}
		
		
		
	}
	
	private boolean inited = false;
	
	private void init(){
		
		
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
				this.getWidth(),
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		parentLayout.setLayoutParams(relativeLayoutParams);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				this.getWidth(),
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		
		mContainer.setLayoutParams(params);
		mContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		RelativeLayout.LayoutParams sonLayoutParams = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		sonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		parentLayout.addView(mContainer, sonLayoutParams);
//		Log.e("width", this.getWidth()+"");
//			
//		Log.e("parent width", parentLayout.getWidth()+"");
//		Log.e("container width", mContainer.getWidth()+"");
		
		
		
		this.addView(parentLayout);
	
	}

	public void selectTab(int position) {
		for (int i = 0, pos = 0; i < mContainer.getChildCount(); i++, pos++) {
			View tab = mContainer.getChildAt(i);
			tab.setSelected(pos == position);
		}
		View selectView = mContainer.getChildAt(position);
		final int w = selectView.getMeasuredWidth();
		final int l = selectView.getLeft();
		final int x = l - this.getWidth() / 2 + w / 2;
		smoothScrollTo(x, this.getScrollY());

	}

}

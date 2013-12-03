/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.suan.weclient.fragment;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity.ShowMenuListener;
import com.suan.weclient.adapter.ScrollingTabsAdapter;
import com.suan.weclient.util.DataManager;
import com.suan.weclient.util.DataManager.ContentFragmentChangeListener;
import com.suan.weclient.util.DataManager.LoginListener;
import com.suan.weclient.util.DataManager.ProfileGetListener;
import com.suan.weclient.util.DataManager.UserGroupListener;
import com.suan.weclient.util.UserBean;
import com.suan.weclient.view.ScrollableTabView;

public class ContentFragment extends Fragment implements
		OnRefreshListener<ListView>, ViewPager.OnPageChangeListener {

	private ShowMenuListener showMenuListener;
	private ImageButton showLeftButton, showRightButton;
	private TextView nowUserTextView;

	private MyAdapter mAdapter;
	private ViewPager mPager;
	private ArrayList<Fragment> pagerItemList = null;

	private ProfileFragment profileFragment;
	private MassFragment massFragment;
	private MessageFragment messageFragment;

	private ScrollableTabView mScrollableTabView;
	private ScrollingTabsAdapter mScrollingTabsAdapter;
	private DataManager mDataChangeListener;

	public ContentFragment(DataManager dataManager) {
		initListener(dataManager);
	}
	
	private void initListener(DataManager dataManager){
		
		mDataChangeListener = dataManager;
		mDataChangeListener.addUserGroupListener(new UserGroupListener() {
			
			@Override
			public void onGroupChangeEnd() {
				// TODO Auto-generated method stub
				if(mDataChangeListener.getUserGroup().size()==0){
					nowUserTextView.setText(getActivity().getResources().getString(R.string.app_name));
					
				}
				
			}
			
			@Override
			public void onAddUser() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void deleteUser(int index) {
				// TODO Auto-generated method stub
				
			}
		});
		mDataChangeListener.addLoginListener(new LoginListener() {
			
			@Override
			public void onLogin(UserBean userBean) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mDataChangeListener.addProfileGetListener(new ProfileGetListener() {
			
			@Override
			public void onGet(UserBean userBean) {
				// TODO Auto-generated method stub
				
				nowUserTextView.setText(userBean.getNickname());
			}
		});
		
		mDataChangeListener.setContentFragmentListener(new ContentFragmentChangeListener() {
			
			@Override
			public void onChange(int index) {
				// TODO Auto-generated method stub
			
				mPager.setCurrentItem(index, true);
			}
		});
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.content_layout, null);

		showLeftButton = (ImageButton) mView
				.findViewById(R.id.head_button_show_left);
		showRightButton = (ImageButton) mView
				.findViewById(R.id.head_button_show_right);

		nowUserTextView = (TextView) mView
				.findViewById(R.id.head_layout_text_now_user);
		nowUserTextView.setText(getActivity().getResources().getString(R.string.app_name));

		mPager = (ViewPager) mView.findViewById(R.id.vp_list);

		pagerItemList = new ArrayList<Fragment>();
		profileFragment = new ProfileFragment(mDataChangeListener);
		messageFragment = new MessageFragment(mDataChangeListener);
		massFragment = new MassFragment(mDataChangeListener);
		pagerItemList.add(profileFragment);
		pagerItemList.add(messageFragment);
		pagerItemList.add(massFragment);
		mAdapter = new MyAdapter(getFragmentManager());
		mPager.setAdapter(mAdapter);

		mPager.setOnPageChangeListener(this);
		initScrollableTabs(mView, mPager);
		return mView;
	}
	

	private void initScrollableTabs(View view, ViewPager mViewPager) {
		mScrollableTabView = (ScrollableTabView) view
				.findViewById(R.id.scrollabletabview);
		mScrollingTabsAdapter = new ScrollingTabsAdapter(getActivity());
		mScrollableTabView.setAdapter(mScrollingTabsAdapter);
		mScrollableTabView.setViewPage(mViewPager);
	}

	public ViewPager getViewPage() {
		return mPager;
	}
	
	public void setShowMenuListener(ShowMenuListener showMenuListener){
		this.showMenuListener = showMenuListener;
	}

	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		showLeftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMenuListener.showLeftMenu();
			}
		});

		showRightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMenuListener.showRightMenu();
			}
		});
	}

	public boolean isFirst() {
		if (mPager.getCurrentItem() == 0)
			return true;
		else
			return false;
	}

	public boolean isEnd() {
		if (mPager.getCurrentItem() == pagerItemList.size() - 1)
			return true;
		else
			return false;
	}

	public class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
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

	private MyPageChangeListener myPageChangeListener;

	public void setMyPageChangeListener(MyPageChangeListener l) {

		myPageChangeListener = l;

	}

	public interface MyPageChangeListener {
		public void onPageSelected(int position);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		new GetDataTask(refreshView).execute();
	}

	private static class GetDataTask extends AsyncTask<Void, Void, Void> {

		PullToRefreshBase<?> mRefreshedView;

		public GetDataTask(PullToRefreshBase<?> refreshedView) {
			mRefreshedView = refreshedView;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mRefreshedView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		if (myPageChangeListener != null) {
			myPageChangeListener.onPageSelected(position);
		}
		if (mScrollableTabView != null) {
			mScrollableTabView.selectTab(position);
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		pagerItemList.clear();
		pagerItemList = null;
		mScrollableTabView = null;
		mScrollingTabsAdapter = null;
	}
	
	

}

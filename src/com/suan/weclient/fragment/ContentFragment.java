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
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import com.suan.weclient.R;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ContentFragmentChangeListener;
import com.suan.weclient.util.data.DataManager.LoginListener;
import com.suan.weclient.util.data.DataManager.ProfileGetListener;
import com.suan.weclient.util.data.DataManager.TabListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.data.UserBean;

public class ContentFragment extends Fragment implements
		OnRefreshListener<ListView>, ViewPager.OnPageChangeListener {

	private View mView;
	

	private MyAdapter mAdapter;
	private ViewPager mPager;
	private ArrayList<Fragment> pagerItemList = null;

	private MassFragment massFragment;
	private MessageFragment messageFragment;

	private DataManager mDataChangeListener;

	public ContentFragment(DataManager dataManager) {
		initListener(dataManager);
	}
	
	private void initListener(DataManager dataManager){
		
		mDataChangeListener = dataManager;
		
		mDataChangeListener.setTabListener(new TabListener() {
			
			@Override
			public void onClickTab(int page) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(page);
				
				
			}
		});
		mDataChangeListener.addUserGroupListener(new UserGroupListener() {
			
			@Override
			public void onGroupChangeEnd() {
				// TODO Auto-generated method stub
				if(mDataChangeListener.getUserGroup().size()==0){
//					nowUserTextView.setText(getActivity().getResources().getString(R.string.app_name));
					
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
				
//				nowUserTextView.setText(userBean.getNickname());
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
        mView = inflater.inflate(R.layout.content_layout, null);
        initWidgets();

		return mView;
	}
	
	private void initWidgets(){
		
		mPager = (ViewPager) mView.findViewById(R.id.vp_list);

		pagerItemList = new ArrayList<Fragment>();
		messageFragment = new MessageFragment(mDataChangeListener);
		massFragment = new MassFragment(mDataChangeListener);
		pagerItemList.add(messageFragment);
		pagerItemList.add(massFragment);
		mAdapter = new MyAdapter(getFragmentManager());
		mPager.setAdapter(mAdapter);
		

		mPager.setOnPageChangeListener(this);
	}
	


	public ViewPager getViewPage() {
		return mPager;
	}
	

	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

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
		
		/*
		 * arg1:percent
		 * arg2:px
		 */
		
		mDataChangeListener.getPagerListener().onScroll(position,arg1);
		

	}

	@Override
	public void onPageSelected(int position) {
		if (myPageChangeListener != null) {
			myPageChangeListener.onPageSelected(position);
		}
		mDataChangeListener.getPagerListener().onPage(position);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		pagerItemList.clear();
		pagerItemList = null;
	}
	
	

}

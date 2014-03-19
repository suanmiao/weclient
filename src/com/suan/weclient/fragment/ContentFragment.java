package com.suan.weclient.fragment;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.adapter.SFragmentPagerAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ContentFragmentChangeListener;
import com.suan.weclient.util.data.DataManager.LoginListener;
import com.suan.weclient.util.data.DataManager.ProfileGetListener;
import com.suan.weclient.util.data.DataManager.TabListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.view.SViewPager;

public class ContentFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private View mView;


    private SFragmentPagerAdapter mAdapter;
    private SViewPager mPager;
    private ArrayList<Fragment> pagerItemList = null;

    private MassFragment massFragment;
    private MessageFragment messageFragment;

    private DataManager mDataChangeListener;

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

        MainActivity mainActivity = (MainActivity)getActivity();
        mDataChangeListener = ((GlobalContext)mainActivity.getApplicationContext()).getDataManager();

        initListener(mDataChangeListener);

        mView = inflater.inflate(R.layout.content_layout, null);
        initWidgets();

        return mView;
    }

    private void initWidgets() {

        mPager = (SViewPager) mView.findViewById(R.id.vp_list);
        mPager.init(mDataChangeListener);

        pagerItemList = new ArrayList<Fragment>();
        messageFragment = new MessageFragment();
        massFragment = new MassFragment();
        pagerItemList.add(messageFragment);
        pagerItemList.add(massFragment);

        MainActivity mainActivity = (MainActivity)getActivity();

        mAdapter = new SFragmentPagerAdapter(mainActivity.getSupportFragmentManager(),pagerItemList);

        mPager.setAdapter(mAdapter);

        mPager.setPageMargin(10);
        mPager.setPageMarginDrawable(R.color.pageDivider);

        mPager.setOnPageChangeListener(this);

    }


    private void initListener(DataManager dataManager) {

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
                if (mDataChangeListener.getUserGroup().size() == 0) {
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


    private MyPageChangeListener myPageChangeListener;

    public void setMyPageChangeListener(MyPageChangeListener l) {

        myPageChangeListener = l;

    }

    public interface MyPageChangeListener {
        public void onPageSelected(int position);
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

        mDataChangeListener.getPagerListener().onScroll(position, arg1);


    }

    @Override
    public void onPageSelected(int position) {
        if (myPageChangeListener != null) {
            myPageChangeListener.onPageSelected(position);
        }
        mDataChangeListener.getPagerListener().onPage(position);

    }

/*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pagerItemList.clear();
        pagerItemList = null;
    }
*/


}

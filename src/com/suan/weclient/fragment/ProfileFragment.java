package com.suan.weclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.activity.FansListActivity;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ProfileGetListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.data.UserBean;

public class ProfileFragment extends Fragment {

	private DataManager mDataManager;
	private TextView newPeopleTextView, newMessageTextView,
			totalPeopleTextView;
	private RelativeLayout firstLayout, secondLayout, thirdLayout;
	private View view;

	public ProfileFragment(DataManager dataManager) {

		mDataManager = dataManager;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.profile_layout, null);
		initWidgets();
		initListener();
		initData();
		return view;
	}

	private void initWidgets() {
		firstLayout = (RelativeLayout) view
				.findViewById(R.id.profile_layout_first);
		secondLayout = (RelativeLayout) view
				.findViewById(R.id.profile_layout_second);
		thirdLayout = (RelativeLayout) view
				.findViewById(R.id.profile_layout_third);
		newPeopleTextView = (TextView) view
				.findViewById(R.id.profile_text_second);
		newMessageTextView = (TextView) view
				.findViewById(R.id.profile_text_third);
		totalPeopleTextView = (TextView) view
				.findViewById(R.id.profile_text_first);

		firstLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent jumbIntent = new Intent();
				jumbIntent.setClass(getActivity(), FansListActivity.class);
				getActivity().startActivity(jumbIntent);

			}
		});

		secondLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


			}
		});

		thirdLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mDataManager.doChangeContentFragment(1);

			}
		});
	}

	private void initListener() {
		mDataManager.addUserGroupListener(new UserGroupListener() {

			@Override
			public void onGroupChangeEnd() {
				// TODO Auto-generated method stub
				if (mDataManager.getUserGroup().size() == 0) {
					newPeopleTextView.setText("");
					newMessageTextView.setText("");
					totalPeopleTextView.setText("");
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
		mDataManager.addProfileGetListener(new ProfileGetListener() {

			@Override
			public void onGet(UserBean userBean) {
				// TODO Auto-generated method stub
				newPeopleTextView.setText(userBean.getNewPeople());
				newMessageTextView.setText(userBean.getNewMessage());
				totalPeopleTextView.setText(userBean.getTotalPeople());

			}
		});

	}

	private void initData() {

		if (mDataManager.getCurrentUser() != null) {

			newPeopleTextView.setText(mDataManager.getCurrentUser()
					.getNewPeople());
			newMessageTextView.setText(mDataManager.getCurrentUser()
					.getNewMessage());
			totalPeopleTextView.setText(mDataManager.getCurrentUser()
					.getTotalPeople());
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

}

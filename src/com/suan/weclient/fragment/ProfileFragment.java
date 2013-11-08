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

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.util.DataManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.DataManager.ProfileGetListener;
import com.suan.weclient.util.UserBean;


public class ProfileFragment extends Fragment {
	
	
	private DataManager mDataManager;
	private TextView newPeopleTextView,newMessageTextView,totalPeopleTextView;
	private RelativeLayout firstLayout,secondLayout,thirdLayout;
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
	
	private void initWidgets(){
		firstLayout = (RelativeLayout)view.findViewById(R.id.profile_layout_first);
		secondLayout = (RelativeLayout)view.findViewById(R.id.profile_layout_second);
		thirdLayout = (RelativeLayout)view.findViewById(R.id.profile_layout_third);
		newPeopleTextView = (TextView)view.findViewById(R.id.profile_text_second);
		newMessageTextView = (TextView)view.findViewById(R.id.profile_text_third);
		totalPeopleTextView = (TextView)view.findViewById(R.id.profile_text_first);
		
		firstLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
	
	private void initListener(){
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
	
	private void initData(){
		
		if(mDataManager.getCurrentUser()!=null){
			
				newPeopleTextView.setText(mDataManager.getCurrentUser().getNewPeople());
				newMessageTextView.setText(mDataManager.getCurrentUser().getNewMessage());
				totalPeopleTextView.setText(mDataManager.getCurrentUser().getTotalPeople());
		}
	}
	

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	
}


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

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.activity.LoginActivity;
import com.suan.weclient.util.DataManager;
import com.suan.weclient.util.DataManager.UserGroupListener;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.UserBean;
import com.suan.weclient.util.WeChatLoader;
import com.suan.weclient.util.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class LeftFragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener {

	public static int START_ACTIVITY_LOGIN = 0;

	private View view;
	private ListView mListView;
	private RelativeLayout addUserButton;

	private MyAdapter myAdapter;
	private FragmentManager mFragmentManager;
	private DataManager mDataManager;
	private ImageView profileImageView;

	private static final String WECHAT_GET_USER_PROFILE_URL = "https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=";

	public LeftFragment(FragmentManager fragmentManager, DataManager dataManager) {
		mFragmentManager = fragmentManager;
		mDataManager = dataManager;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.user_group_layout, null);
		initWidgets();
		initListener();

		return view;
	}

	private void initListener() {
		mDataManager.addUserGroupListener(new UserGroupListener() {

			@Override
			public void onGroupChange() {
				// TODO Auto-generated method stub
				Log.e("user group change", "");
				updateList();

			}
		});

	}

	private void initWidgets() {

		mListView = (ListView) view.findViewById(R.id.left_listview);

		myAdapter = new MyAdapter();
		mListView.setAdapter(myAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		myAdapter.setSelectPosition(0);

		addUserButton = (RelativeLayout) view
				.findViewById(R.id.left_button_add_user);
		addUserButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent jumbIntent = new Intent();
				jumbIntent.setClass(getActivity(), LoginActivity.class);
				getActivity().startActivityForResult(jumbIntent,
						START_ACTIVITY_LOGIN);

			}
		});
	}

	public void updateList() {
		mDataManager.updateUserGroup();
		myAdapter.notifyDataSetChanged();

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private class MyAdapter extends BaseAdapter {

		private int selectPosition;

		@Override
		public int getCount() {
			return mDataManager.getUserGroup().size();
		}

		@Override
		public Object getItem(int position) {
			return mDataManager.getUserGroup().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setSelectPosition(int position) {
			selectPosition = position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = LayoutInflater.from(getActivity()).inflate(
					R.layout.user_group_item, null);
			TextView nowUserNickname = (TextView) row
					.findViewById(R.id.user_group_text_user_name);
			ImageView nowUserImg = (ImageView) row
					.findViewById(R.id.user_group_img_profile);

			Bitmap imgBitmap = mDataManager.getCacheManager().getDiskBitmap(
					ImageCacheManager.CACHE_USER_PROFILE
							+ mDataManager.getUserGroup().get(position)
									.getUserName());
			if (imgBitmap != null) {
				nowUserImg.setImageBitmap(imgBitmap);

			} else {
				Log.e("start get user bitmap ", "position" + position);
				mDataManager.getWechatManager().getUserImgDirectly(false,
						position, nowUserImg, new OnActionFinishListener() {

							@Override
							public void onFinish(Object object) {
								// TODO Auto-generated method stub
								Bitmap nowUserBitmap = (Bitmap) object;
								Log.e("get user bitmap ", "position" + position);
								mDataManager.getCacheManager().putDiskBitmap(
										ImageCacheManager.CACHE_USER_PROFILE
												+ mDataManager.getUserGroup()
														.get(position)
														.getUserName(),
										nowUserBitmap);

							}
						});

			}

			nowUserNickname.setText(mDataManager.getUserGroup().get(position)
					.getNickname()
					+ "");
			if (position == selectPosition) {
				// row.setBackgroundResource(R.drawable.biz_navigation_tab_bg_pressed);
				nowUserNickname.setSelected(true);
			}
			return row;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		profileImageView = (ImageView) view
				.findViewById(R.id.user_group_img_profile);
		mDataManager.setCurrentPosition(position);

		mDataManager.doLoadingStart("登录...");

		mDataManager.getWechatManager().login(position, false,
				new OnActionFinishListener() {

					@Override
					public void onFinish(Object object) {
						// TODO Auto-generated method stub
						mDataManager.getWechatManager().getUserProfile(true,
								position, new OnActionFinishListener() {

									@Override
									public void onFinish(Object object) {
										// TODO Auto-generated method stub
										String referer = (String) object;

										mDataManager
												.getWechatManager()
												.getUserImgWithReferer(
														position,
														false,
														profileImageView,
														new OnActionFinishListener() {

															@Override
															public void onFinish(
																	Object object) {
																// TODO
																// Auto-generated
																// method stub

																mDataManager
																		.getWechatManager()
																		.getMassData(
																				position,
																				new OnActionFinishListener() {

																					@Override
																					public void onFinish(
																							Object object) {
																						// TODO
																						// Auto-generated
																						// method
																						// stub

																						mDataManager
																								.getWechatManager()
																								.getNewMessageList(
																										true,
																										position,
																										new OnActionFinishListener() {

																											@Override
																											public void onFinish(
																													Object object) {
																												// TODO
																												// Auto-generated
																												// method
																												// stub

																											}
																										});
																					}
																				});

															}
														}, referer);

									}
								});

					}
				});

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			final int position, long id) {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle("删除该用户？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated
						// method stub
						SharedPreferenceManager.deleteUser(getActivity(),
								mDataManager.getUserGroup().get(position)
										.getUserName());
						updateList();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		builder.show();

		return false;
	}

}

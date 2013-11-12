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
package com.suan.weclient.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.fragment.ContentFragment;
import com.suan.weclient.fragment.ContentFragment.MyPageChangeListener;
import com.suan.weclient.fragment.LeftFragment;
import com.suan.weclient.fragment.RightFragment;
import com.suan.weclient.util.DataManager;
import com.suan.weclient.util.DataManager.AutoLoginListener;
import com.suan.weclient.util.DataManager.DialogListener;
import com.suan.weclient.util.DataManager.DialogSureClickListener;
import com.suan.weclient.util.DataManager.UserGroupListener;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.WechatManager.OnActionFinishListener;
import com.suan.weclient.view.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainActivity extends FragmentActivity {

	SlidingMenu mSlidingMenu;
	LeftFragment leftFragment;
	RightFragment rightFragment;
	ContentFragment contentFragment;

	private DataManager mDataManager;
	private Dialog popDialog;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		initDataChangeListener();
		initCache();
		initWidgets();
		initListener(contentFragment);
		autoLogin();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initUmeng();

			}
		}, 2000);

	}

	private void initUmeng() {

		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					
					break;
				case 1: // has no update
					break;
				case 2: // none wifi
					break;
				case 3: // time out
					break;
				}
			}
		});
		UmengUpdateAgent.update(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initDataChangeListener() {
		mDataManager = new DataManager(getApplicationContext());
		mDataManager.addAutoLoginListener(new AutoLoginListener() {
			
			@Override
			public void autoLogin() {
				// TODO Auto-generated method stub
				MainActivity.this.autoLogin();
				
				
			}
			
			public void onAutoLoginEnd(){
				
			}
		});
		mDataManager.addUserGroupListener(new UserGroupListener() {
			
			@Override
			public void onGroupChangeEnd() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void deleteUser(int index) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAddUser() {
				// TODO Auto-generated method stub
				
			}
		});
				mDataManager.addLoadingListener(new DialogListener() {

			@Override
			public void onLoad(String loaingText) {
				// TODO Auto-generated method stub

				if (popDialog != null) {
					popDialog.dismiss();

					popDialog = Util.createLoadingDialog(MainActivity.this,
							loaingText, false);
				} else {

					popDialog = Util.createLoadingDialog(MainActivity.this,
							loaingText, false);
				}
				popDialog.show();

			}

			@Override
			public void onFinishLoad() {
				// TODO Auto-generated method stub
				if (popDialog != null) {

					popDialog.dismiss();
					popDialog = null;
				}

			}

			@Override
			public void onPopEnsureDialog(boolean cancelVisible,
					boolean cancelable, String titleText,
					DialogSureClickListener dialogSureClickListener) {
				// TODO Auto-generated method stub

				try {
					if (popDialog != null) {
						popDialog.dismiss();
					} else {
						popDialog = Util.createEnsureDialog(
								dialogSureClickListener, cancelVisible,
								MainActivity.this, titleText, true);

					}
					popDialog.show();

				} catch (Exception exception) {

				}

			}

			@Override
			public void onDismissAllDialog() {
				// TODO Auto-generated method stub

				if (popDialog != null) {
					popDialog.dismiss();
					popDialog = null;
				}
			}
		});
	}

	private long lastBackKeyTouchTime = 0;

	@SuppressLint("ShowToast")
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - lastBackKeyTouchTime < 3000) {
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "再按一次返回键退出应用",
						Toast.LENGTH_SHORT).show();
				lastBackKeyTouchTime = System.currentTimeMillis();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void initCache() {
		mDataManager.createImageCache(getApplicationContext());

	}

	private void initWidgets() {

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);

		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();

		leftFragment = new LeftFragment(this.getSupportFragmentManager(),
				mDataManager);

		t.replace(R.id.left_frame, leftFragment);
		rightFragment = new RightFragment(mDataManager);

		t.replace(R.id.right_frame, rightFragment);
		contentFragment = new ContentFragment(mDataManager);

		t.replace(R.id.center_frame, contentFragment);
		t.commit();

	}

	private void autoLogin() {

		if (mDataManager.getUserGroup().size() <= 0) {
			return;
		}
		mDataManager.setCurrentPosition(0);

		mDataManager.getWechatManager().login(
				mDataManager.getCurrentPosition(), true,
				new OnActionFinishListener() {

					@Override
					public void onFinish(Object object) {
						// TODO Auto-generated method stub
						mDataManager.getWechatManager().getUserProfile(true,
								mDataManager.getCurrentPosition(),
								new OnActionFinishListener() {

									@Override
									public void onFinish(Object object) {
										// TODO Auto-generated method stub
										String referer = (String) object;

										mDataManager
												.getWechatManager()
												.getUserImgWithReferer(
														mDataManager
																.getCurrentPosition(),
														false,
														null,
														new OnActionFinishListener() {

															@Override
															public void onFinish(
																	Object object) {
																// TODO
																// Auto-generated
																// method stub

															}
														}, referer);
										mDataManager
												.getWechatManager()
												.getMassData(
														mDataManager
																.getCurrentPosition(),
														true,
														new OnActionFinishListener() {

															@Override
															public void onFinish(
																	Object object) {
																// TODO
																// Auto-generated
																// method stub

																mDataManager
																		.getWechatManager()
																		.getNewMessageList(
																				true,
																				mDataManager
																						.getCurrentPosition(),
																				new OnActionFinishListener() {

																					@Override
																					public void onFinish(
																							Object object) {
																						// TODO
																						// Auto-generated
																						// method
																						// stub
																						mDataManager.doAutoLoginEnd();

																					}
																				});
															}
														});

									}
								});

					}
				});

	}

	private void initListener(final ContentFragment fragment) {
		fragment.setMyPageChangeListener(new MyPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (fragment.isFirst()) {
					mSlidingMenu.setCanSliding(true, false);
				} else if (fragment.isEnd()) {
					mSlidingMenu.setCanSliding(false, true);
				} else {
					mSlidingMenu.setCanSliding(false, false);
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LeftFragment.START_ACTIVITY_LOGIN) {
			if (resultCode == RESULT_OK) {

				mDataManager.updateUserGroup();
				mDataManager.doAddUser();
				mDataManager.doGroupChangeEnd();
				
			} else if (resultCode == RESULT_CANCELED) {

			}
		}
	}

	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	public void showRight() {
		mSlidingMenu.showRightView();
	}

}

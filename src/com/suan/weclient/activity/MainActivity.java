package com.suan.weclient.activity;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
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
import com.suan.weclient.util.MessageHolder;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Conversation.SyncListener;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainActivity extends SlidingFragmentActivity {

	LeftFragment leftFragment;
	RightFragment rightFragment;
	ContentFragment contentFragment;
	SlidingMenu mSlidingMenu;

	/*
	 * about pop dialog
	 */
	private TextView popContentTextView;
	private TextView popTitleTextView;
	private EditText popContentEditText;
	private TextView popTextAmountTextView;
	private ImageButton popCancelButton, popSureButton;

	private FeedbackAgent agent;
	private Conversation defaultConversation;

	private DataManager mDataManager;
	private Dialog popDialog;
	private Dialog replyDialog;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initDataChangeListener();
		initCache();
		initSlidingMenu();
		initWidgets();
		initListener(contentFragment);
		autoLogin();
		initUmeng();

	}

	private void initSlidingMenu() {

		// set the Behind View
		setBehindContentView(R.layout.left_frame);
		setContentView(R.layout.main);
		setBehindContentView(R.layout.left_frame);

		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);

		mSlidingMenu.setSecondaryMenu(R.layout.right_frame);

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();

		leftFragment = new LeftFragment(this.getSupportFragmentManager(),
				mDataManager);
		t.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment(mDataManager);
		t.replace(R.id.right_frame, rightFragment);

		contentFragment = new ContentFragment(mDataManager);
		contentFragment.setShowMenuListener(new ShowMenuListener() {

			@Override
			public void showLeftMenu() {
				// TODO Auto-generated method stub
				getSlidingMenu().showMenu();

			}

			public void showRightMenu() {
				getSlidingMenu().showSecondaryMenu();

			}
		});
		t.replace(R.id.content_layout, contentFragment);

		t.commit();

	}

	private void initWidgets() {

	}

	private void initUmeng() {

		agent = new FeedbackAgent(this);
		defaultConversation = agent.getDefaultConversation();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				UmengUpdateAgent.setUpdateAutoPopup(true);
				UmengUpdateAgent
						.setDialogListener(new UmengDialogButtonListener() {

							@Override
							public void onClick(int arg0) {
								// TODO Auto-generated method stub

							}
						});
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
				UmengUpdateAgent.update(MainActivity.this);

			}

		}, 1000);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				defaultConversation.sync(new SyncListener() {

					@Override
					public void onSendUserReply(List<Reply> arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onReceiveDevReply(List<DevReply> arg0) {
						// TODO Auto-generated method stub
						String replyString = "";
						/*
						 * fuck umeng the arg0 might be null
						 */
						try {
							for (int i = 0; i < arg0.size(); i++) {
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"MM-dd HH:mm");
								replyString += dateFormat.format(arg0.get(i)
										.getDatetime());
								replyString += ":  ";
								replyString += arg0.get(i).getContent();
								replyString += "\n";

							}
							if (arg0.size() > 0) {
								dialogShowDevReply(replyString);
							}

						} catch (Exception exception) {

						}

					}
				});

			}
		}, 1000);

	}

	public void dialogShowDevReply(String content) {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.dialog_dev_reply_layout,
				null);
		popTitleTextView = (TextView) dialogView
				.findViewById(R.id.dialog_dev_reply_text_title);

		popSureButton = (ImageButton) dialogView
				.findViewById(R.id.dialog_dev_reply_button_reply);
		popCancelButton = (ImageButton) dialogView
				.findViewById(R.id.dialog_dev_reply_button_o);

		popContentTextView = (TextView) dialogView
				.findViewById(R.id.dialog_dev_reply_text_content);
		popContentTextView.setText(content);

		popTitleTextView.setText("开发者回复:");
		popSureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				replyDialog.dismiss();
				popFeedback();
			}
		});
		popCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				replyDialog.dismiss();

			}
		});

		replyDialog = new Dialog(this, R.style.dialog);

		replyDialog.setContentView(dialogView);
		replyDialog.show();
	}

	private void popFeedback() {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.pop_feedback_layout, null);
		popTitleTextView = (TextView) dialogView
				.findViewById(R.id.pop_feedback_text_title);

		popContentEditText = (EditText) dialogView
				.findViewById(R.id.pop_feedback_edit_text);
		popSureButton = (ImageButton) dialogView
				.findViewById(R.id.pop_feedback_button_sure);
		popCancelButton = (ImageButton) dialogView
				.findViewById(R.id.pop_feedback_button_cancel);

		popTextAmountTextView = (TextView) dialogView
				.findViewById(R.id.pop_feedback_text_num);
		popTextAmountTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popContentEditText.setText("");

			}
		});

		popContentEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				popTextAmountTextView.setTextColor(Color.rgb(0, 0, 0));
				popTextAmountTextView.setText(popContentEditText.getText()
						.length() + " x");

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		popTitleTextView.setText("反馈");
		popSureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String content = popContentEditText.getEditableText()
						.toString();
				defaultConversation.addUserReply(content);
				replyDialog.dismiss();

				mDataManager.doLoadingStart("反馈发送中...");

				sync();
			}
		});
		popCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				replyDialog.cancel();

			}
		});

		replyDialog = new Dialog(this, R.style.dialog);

		replyDialog.setContentView(dialogView);
		replyDialog.show();

	}

	void sync() {
		Conversation.SyncListener listener = new Conversation.SyncListener() {

			@Override
			public void onSendUserReply(List<Reply> replyList) {
				popContentEditText.setText("");
				mDataManager.doLoadingEnd();

				Toast.makeText(MainActivity.this, "反馈发送成功!", Toast.LENGTH_SHORT)
						.show();

			}

			@Override
			public void onReceiveDevReply(List<DevReply> replyList) {
			}
		};
		defaultConversation.sync(listener);
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

			public void onAutoLoginEnd() {

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
																						MessageHolder messageHolder = (MessageHolder) object;
																						mDataManager
																								.doMessageGet(messageHolder);
																						mDataManager
																								.doAutoLoginEnd();

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
					// mSlidingMenu.setCanSliding(true, false);
				} else if (fragment.isEnd()) {
					// mSlidingMenu.setCanSliding(false, true);
				} else {
					// mSlidingMenu.setCanSliding(false, false);
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

	public interface ShowMenuListener {
		public void showLeftMenu();

		public void showRightMenu();

	}

}

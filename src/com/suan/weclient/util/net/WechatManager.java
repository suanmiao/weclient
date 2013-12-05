package com.suan.weclient.util.net;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.suan.weclient.util.DataManager;
import com.suan.weclient.util.DataManager.DialogSureClickListener;
import com.suan.weclient.util.UserBean;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.net.DataParser.MessageListParseCallBack;
import com.suan.weclient.util.net.DataParser.MessageResultHolder;
import com.suan.weclient.util.net.DataParser.ParseMassDataCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatExceptionListener;
import com.suan.weclient.util.net.WeChatLoader.WechatGetHeadImgCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatGetMassData;
import com.suan.weclient.util.net.WeChatLoader.WechatGetMessageImgCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatGetUserProfleCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatLoginCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMassCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessageListCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessagePageCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessageReplyCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatMessageStarCallBack;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class WechatManager {

	public interface OnActionFinishListener {

		public void onFinish(Object object);
	}

	private DataManager mDataManager;
	private Context mContext;

	public WechatManager(DataManager dataManager, Context context) {
		mDataManager = dataManager;
		mContext = context;

	}

	public void login(final int userIndex, final boolean popDialog,
			final OnActionFinishListener onActionFinishListener) {

		if (popDialog) {
			mDataManager.doDismissAllDialog();
			mDataManager.doLoadingStart("登录...");

		}

		WeChatLoader
				.wechatLogin(
						new WechatExceptionListener() {

							@Override
							public void onError() {
								// TODO Auto-generated method stub

								mDataManager.doPopEnsureDialog(true, false,
										"登录失败 重试?",
										new DialogSureClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												login(userIndex, popDialog,
														onActionFinishListener);

											}
										});

							}
						},

						new WechatLoginCallBack() {

							@Override
							public void onBack(String strResult,
									String slaveSid, String slaveUser) {
								// TODO Auto-generated method stub
								try {
									UserBean nowBean = mDataManager
											.getUserGroup().get(userIndex);
									int loginResult = DataParser.analyseLogin(
											nowBean, strResult, slaveSid,
											slaveUser, mContext);
									nowBean.setSlaveSid(slaveSid);
									nowBean.setSlaveUser(slaveUser);
									switch (loginResult) {
									case DataParser.LOGIN_SUCCESS:
										mDataManager.doLoginSuccess(nowBean);

										onActionFinishListener.onFinish(null);
										break;

									case DataParser.LOGIN_FAILED:

										mDataManager.doPopEnsureDialog(true,
												false, "登录失败 请检查账户名和密码",
												new DialogSureClickListener() {

													@Override
													public void onClick(View v) {
														// TODO Auto-generated
														// method
														// stub
														mDataManager
																.doDismissAllDialog();

													}
												});
										break;
									}
								} catch (Exception exception) {

								}

							}
						}, mDataManager.getUserGroup().get(userIndex)
								.getUserName(),
						mDataManager.getUserGroup().get(userIndex).getPwd(),
						"", "json");
	}

	public void getUserProfile(final boolean popDialog, final int userIndex,
			final OnActionFinishListener onActionFinishListener) {
		if (popDialog) {
			mDataManager.doLoadingStart("获取用户数据...");

		}
		WeChatLoader.wechatGetUserProfile(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				mDataManager.doPopEnsureDialog(true, false, "获取用户信息失败 重试?",
						new DialogSureClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								getUserProfile(popDialog, userIndex,
										onActionFinishListener);

							}
						});

			}
		},

		new WechatGetUserProfleCallBack() {

			@Override
			public void onBack(String strResult, String referer) {
				// TODO Auto-generated method
				// stub

				try {
					if (popDialog) {

						mDataManager.doLoadingStart("解析用户数据...");

					}
					int getUserProfileState = DataParser.getUserProfile(
							strResult,
							mDataManager.getUserGroup().get(userIndex));

					switch (getUserProfileState) {
					case DataParser.GET_USER_PROFILE_SUCCESS:
						mDataManager.doProfileGet(mDataManager.getUserGroup()
								.get(userIndex));
						onActionFinishListener.onFinish(referer);

						break;

					case DataParser.GET_USER_PROFILE_FAILED:

						break;
					}
				} catch (Exception exception) {

				}
			}
		}, mDataManager.getUserGroup().get(userIndex));

	}

	public void getUserImgDirectly(final boolean popDialog,
			final int userIndex, final ImageView userProfileImageView,
			final OnActionFinishListener onActionFinishListener) {

		WeChatLoader.wechatGetUserProfile(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				mDataManager.doPopEnsureDialog(true, false, "获取用户信息失败 重试?",
						new DialogSureClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								getUserProfile(popDialog, userIndex,
										onActionFinishListener);

							}
						});

			}
		},

		new WechatGetUserProfleCallBack() {

			@Override
			public void onBack(String strResult, String referer) {
				// TODO Auto-generated method
				// stub

				try {
					int getUserProfileState = DataParser.getUserProfile(
							strResult,
							mDataManager.getUserGroup().get(userIndex));

					switch (getUserProfileState) {
					case DataParser.GET_USER_PROFILE_SUCCESS:

						getUserImgWithReferer(userIndex, popDialog,
								userProfileImageView, onActionFinishListener,
								referer);

						break;

					case DataParser.GET_USER_PROFILE_FAILED:

						break;
					}
				} catch (Exception exception) {

				}
			}
		}, mDataManager.getUserGroup().get(userIndex));

	}

	public void getMessageHeadImg(final int userIndex, final String fakeId,
			final String referer, final ImageView imageView,
			final OnActionFinishListener onActionFinishListener) {

		WeChatLoader.wechatGetHeadImg(
				new WechatExceptionListener() {

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				},

				new WechatGetHeadImgCallBack() {

					@Override
					public void onBack(Bitmap bitmap,
							String referer, ImageView imageView) {
						// TODO Auto-generated method stub

						try {
							Bitmap roundBitmap = Util.roundCorner(
									bitmap, 10);

							imageView.setImageBitmap(roundBitmap);
							onActionFinishListener.onFinish(roundBitmap);
							
						} catch (Exception exception) {

						}

					}
				}, mDataManager.getUserGroup().get(userIndex),
				fakeId,
				referer,
				imageView);

		
		
	}

	public void getMessageImg(final int userIndex, final String msgId,
			final String slaveSid, final String slaveUser, final String token,
			final String referer, final ImageView imageView,
			final String imgType,
			final OnActionFinishListener onActionFinishListener) {

		WeChatLoader.wechatGetMessageImg(
				new WechatExceptionListener() {

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				},
				new WechatGetMessageImgCallBack() {

					@Override
					public void onBack(Bitmap bitmap,
							ImageView imageView) {
						// TODO Auto-generated method stub
						try {
							imageView.setImageBitmap(bitmap);
							onActionFinishListener.onFinish(bitmap);

						} catch (Exception exception) {

						}

					}
				}, msgId, slaveSid, slaveUser, token, referer, imageView,
				WeChatLoader.WECHAT_URL_MESSAGE_IMG_LARGE);

	}

	public void getUserImgWithReferer(final int userIndex,
			final boolean popDialog, final ImageView imageView,
			final OnActionFinishListener onActionFinishListener,
			final String referer) {
		if (popDialog) {
			mDataManager.doLoadingStart("获取用户头像...");

		}

		WeChatLoader.wechatGetHeadImg(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO
				// Auto-generated
				// method
				// stub
				mDataManager.doLoadingEnd();

				mDataManager.doPopEnsureDialog(true, false, "获取用户信息失败 重试?",
						new DialogSureClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								getUserImgWithReferer(userIndex, popDialog,
										imageView, onActionFinishListener,
										referer);

							}
						});
			}
		},

		new WechatGetHeadImgCallBack() {

			@Override
			public void onBack(Bitmap bitmap, String referer,
					ImageView imageView) {
				// TODO

				try {
					if (popDialog) {

						mDataManager.doLoadingStart("设置用户头像...");
					}
					mDataManager.getCacheManager().putDiskBitmap(
							ImageCacheManager.CACHE_USER_PROFILE
									+ mDataManager.getUserGroup()
											.get(userIndex).getUserName(),
							bitmap);
					if (imageView != null) {
						imageView.setImageBitmap(bitmap);
					}
					onActionFinishListener.onFinish(bitmap);

				} catch (Exception exception) {

				}
			}
		}, mDataManager.getUserGroup().get(userIndex), mDataManager
				.getUserGroup().get(userIndex).getFakeId(), referer, imageView);

	}

	public void getMassData(final int userIndex, final boolean popDialog,
			final OnActionFinishListener onActionFinishListener) {
		if (popDialog) {
			mDataManager.doLoadingStart("获取用户群发数据...");

		}

		WeChatLoader.wechatGetMassData(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				mDataManager.doPopEnsureDialog(true, false, "获取用户信息失败 重试?",
						new DialogSureClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								getMassData(userIndex, popDialog,
										onActionFinishListener);

							}
						});

			}
		}, new WechatGetMassData() {

			@Override
			public void onBack(String strResult) {
				// TODO Auto-generated method stub

				try {
					if (popDialog) {

						mDataManager.doLoadingStart("解析用户群发数据...");

					}
					DataParser.getMassData(strResult, mDataManager
							.getUserGroup().get(userIndex),
							new ParseMassDataCallBack() {

								@Override
								public void onBack(UserBean userBean) {
									// TODO Auto-generated method stub
									mDataManager.doProfileGet(mDataManager
											.getUserGroup().get(userIndex));
									onActionFinishListener.onFinish(null);

								}
							});

				} catch (Exception exception) {
					Log.e("mass parse error", "" + exception);

				}
			}
		}, mDataManager.getUserGroup().get(userIndex));
	}

	public void getNewMessageList(final boolean popLoadingDialog,
			final int userIndex,
			final OnActionFinishListener onActionFinishListener) {
		if (popLoadingDialog) {

			mDataManager.doLoadingStart("获取消息数据...");

		}

		WeChatLoader.wechatGetMessageList(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				mDataManager.doPopEnsureDialog(true, false, "获取消息失败 重试?",
						new DialogSureClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								getNewMessageList(popLoadingDialog, userIndex,
										onActionFinishListener);

							}
						});

			}
		},

		new WechatMessageListCallBack() {
			@Override
			public void onBack(String strResult, String referer) {
				// TODO Auto-generated method
				// stub

				try {
					if (popLoadingDialog) {

						mDataManager.doDismissAllDialog();
						mDataManager.doLoadingStart("解析消息数据...");

					}

					DataParser.getNewMessage(new MessageListParseCallBack() {

						@Override
						public void onBack(
								MessageResultHolder messageResultHolder) {
							// TODO
							// Auto-generated
							// method
							// stub
							mDataManager.doMessageGet(mDataManager
									.getMessageHolders().get(userIndex));
							mDataManager.doLoadingEnd();

						}
					}, strResult, mDataManager.getUserGroup().get(userIndex),
							mDataManager.getMessageHolders().get(userIndex),
							referer);
					mDataManager.doDismissAllDialog();
					onActionFinishListener.onFinish(null);

				} catch (Exception exception) {

				}
			}
		}, mDataManager.getUserGroup().get(userIndex));
	}

	public void getNextMessageList(final int page, final int userIndex,
			final OnActionFinishListener onActionFinishListener) {

		WeChatLoader.wechatGetMessagePage(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO Auto-generated method stub

				mDataManager.doPopEnsureDialog(true, false,
						"获取消息列表失败 网络错误 重试?", new DialogSureClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								getNextMessageList(page, userIndex,
										onActionFinishListener);

							}
						});

			}
		},

		new WechatMessagePageCallBack() {

			@Override
			public void onBack(String strResult, String referer) {
				// TODO Auto-generated method stub
				try {

					DataParser.getNextMessage(new MessageListParseCallBack() {

						@Override
						public void onBack(
								MessageResultHolder messageResultHolder) {
							// TODO
							// Auto-generated
							// method
							// stub
							// Log.e("get next message",
							// "message size"+messageResultHolder.messageItems.size());
							mDataManager.doMessageGet(mDataManager
									.getMessageHolders().get(userIndex));

						}
					}, strResult, mDataManager.getUserGroup().get(userIndex),
							mDataManager.getMessageHolders().get(userIndex),
							referer);

				} catch (Exception exception) {

				}

				onActionFinishListener.onFinish(null);

			}
		}, mDataManager.getMessageHolders().get(userIndex), page);

	}

	public void reply(final int userIndex, final int position,
			final String replyContent,
			final OnActionFinishListener onActionFinishListener) {
		WeChatLoader.wechatMessageReply(
				new WechatExceptionListener() {

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				},

				new WechatMessageReplyCallBack() {

					@Override
					public void onBack(String strResult) {
						// TODO Auto-generated method stub

						try {

							// String strResult = EntityUtils.toString(response
							// .getEntity());
							Toast.makeText(mContext, "回复成功", Toast.LENGTH_SHORT)
									.show();
							onActionFinishListener.onFinish(null);
						} catch (Exception exception) {

						}
					}
				}, mDataManager.getUserGroup().get(userIndex), mDataManager
						.getMessageHolders().get(userIndex).getMessageList()
						.get(position), replyContent);

	}

	public void star(final int userIndex, final int position,
			final ImageButton v, final boolean star,
			final OnActionFinishListener onActionFinishListener) {

		WeChatLoader.wechatMessageStar(

				new WechatExceptionListener() {

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				},

				new WechatMessageStarCallBack() {

					@Override
					public void onBack(String strResult) {
						// TODO Auto-generated method
						// stub
						try {

							JSONObject resultJsonObject = new JSONObject(
									strResult);
							if (resultJsonObject.get("ret") != null) {
								if (Integer.parseInt(resultJsonObject
										.get("ret") + "") == WeChatLoader.WECHAT_STAR_OK) {
									if (star) {

										Toast.makeText(mContext, "加星标成功",
												Toast.LENGTH_SHORT).show();
									} else {

										Toast.makeText(mContext, "取消星标成功",
												Toast.LENGTH_SHORT).show();
									}
									mDataManager.getMessageHolders()
											.get(userIndex).getMessageList()
											.get(position).setStarred(star);
									onActionFinishListener.onFinish(null);

								}

							}

						} catch (Exception exception) {
							Log.e("star result parse error", "" + exception);

						}

					}
				}, mDataManager.getUserGroup().get(userIndex), mDataManager
						.getMessageHolders().get(userIndex).getMessageList()
						.get(position), star);
	}

	public void mass(final int userIndex, final String massContent,
			final OnActionFinishListener onActionFinishListener) {

		WeChatLoader.wechatMass(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				mDataManager.doLoadingEnd();

			}
		},

		new WechatMassCallBack() {

			@Override
			public void onBack(String strResult) {
				// TODO Auto-generated method stub
				try {
					mDataManager.doLoadingEnd();

					JSONObject resultJsonObject = new JSONObject(strResult);

					if (resultJsonObject.get("ret") != null) {

						if (Integer.parseInt(resultJsonObject.get("ret") + "") == WeChatLoader.WECHAT_MASS_OK) {
							onActionFinishListener.onFinish(null);

						} else if (Integer.parseInt(resultJsonObject.get("ret")
								+ "") == WeChatLoader.WECHAT_MASS_ERROR_ONLY_ONE) {

							Log.e("mass only ", "");

							mDataManager.doPopEnsureDialog(false, true,
									"每天只能群发一条哦～",
									new DialogSureClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											mDataManager.doDismissAllDialog();

										}
									});
						}

					} else {
						mDataManager.doLoadingEnd();
					}
				} catch (Exception exception) {

					Log.e("mass result parse exception", "" + exception);
				}

			}
		}, mDataManager.getCurrentUser(), massContent);
	}
}

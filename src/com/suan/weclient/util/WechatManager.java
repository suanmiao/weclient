package com.suan.weclient.util;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.suan.weclient.util.DataManager.DialogSureClickListener;
import com.suan.weclient.util.DataParser.MessageListParseCallBack;
import com.suan.weclient.util.DataParser.MessageResultHolder;
import com.suan.weclient.util.WeChatLoader.WechatExceptionListener;
import com.suan.weclient.util.WeChatLoader.WechatGetHeadImgCallBack;
import com.suan.weclient.util.WeChatLoader.WechatGetUserProfleCallBack;
import com.suan.weclient.util.WeChatLoader.WechatLoginCallBack;
import com.suan.weclient.util.WeChatLoader.WechatMassCallBack;
import com.suan.weclient.util.WeChatLoader.WechatMessageListCallBack;
import com.suan.weclient.util.WeChatLoader.WechatMessagePageCallBack;
import com.suan.weclient.util.WeChatLoader.WechatMessageReplyCallBack;
import com.suan.weclient.util.WeChatLoader.WechatMessageStarCallBack;
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

	public void login(final int userIndex,final boolean popDialog,
			final OnActionFinishListener onActionFinishListener) {

		if(popDialog){
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
												login(userIndex,popDialog,
														onActionFinishListener);

											}
										});

							}
						},

						new WechatLoginCallBack() {

							@Override
							public void onBack(HttpResponse response) {
								// TODO Auto-generated method stub
								try {
									UserBean nowBean = mDataManager
											.getUserGroup().get(userIndex);
									int loginResult = DataParser.analyseLogin(
											nowBean, response, mContext);
									switch (loginResult) {
									case DataParser.LOGIN_SUCCESS:
										mDataManager.doLoginSuccess(nowBean);

										onActionFinishListener.onFinish(null);
										break;

									case DataParser.LOGIN_FAILED:

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
		if(popDialog){
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
			public void onBack(HttpResponse response, String referer) {
				// TODO Auto-generated method
				// stub

				try {
					String strResult = EntityUtils.toString(response
							.getEntity());
					if (popDialog) {
						mDataManager.doDismissAllDialog();

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
			public void onBack(HttpResponse response, String referer) {
				// TODO Auto-generated method
				// stub

				try {
					String strResult = EntityUtils.toString(response
							.getEntity());
					int getUserProfileState = DataParser.getUserProfile(
							strResult,
							mDataManager.getUserGroup().get(userIndex));

					switch (getUserProfileState) {
					case DataParser.GET_USER_PROFILE_SUCCESS:
						mDataManager.doProfileGet(mDataManager.getUserGroup()
								.get(userIndex));

						getUserImgWithReferer(userIndex,popDialog, userProfileImageView,
								onActionFinishListener, referer);

						break;

					case DataParser.GET_USER_PROFILE_FAILED:

						break;
					}
				} catch (Exception exception) {

				}
			}
		}, mDataManager.getUserGroup().get(userIndex));

	}

	public void getUserImgWithReferer(final int userIndex,final boolean popDialog,
			final ImageView imageView,
			final OnActionFinishListener onActionFinishListener,
			final String referer) {
		if(popDialog){
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
								getUserImgWithReferer(userIndex,popDialog, imageView,
										onActionFinishListener, referer);

							}
						});
			}
		},

		new WechatGetHeadImgCallBack() {

			@Override
			public void onBack(HttpResponse response, String referer,
					ImageView imageView) {
				// TODO

				try {
					mDataManager.doLoadingStart("设置用户头像...");

					Bitmap bitmap = BitmapFactory
							.decodeStream((InputStream) response.getEntity()
									.getContent());
					Log.e(" get user img", "" + bitmap);
					mDataManager.getCacheManager().putDiskBitmap(
							ImageCacheManager.CACHE_USER_PROFILE
									+ mDataManager.getUserGroup()
											.get(userIndex).getUserName(),
							bitmap);
					if (imageView != null) {
						imageView.setImageBitmap(bitmap);
					}
					onActionFinishListener.onFinish(bitmap);

					mDataManager.doLoadingEnd();

				} catch (Exception exception) {

				}
			}
		}, mDataManager.getUserGroup().get(userIndex), mDataManager
				.getUserGroup().get(userIndex).getFakeId(), referer, imageView);

	}

	public void getMassData(final int userIndex,
			final OnActionFinishListener onActionFinishListener) {
		mDataManager.doLoadingStart("获取用户群发数据...");

		WeChatLoader.wechatGetMassData(new WechatExceptionListener() {

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				mDataManager.doPopEnsureDialog(true, false, "获取用户信息失败 重试?",
						new DialogSureClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								getMassData(userIndex, onActionFinishListener);

							}
						});

			}
		}, new WechatMassCallBack() {

			@Override
			public void onBack(HttpResponse response) {
				// TODO Auto-generated method stub

				try {
					mDataManager.doLoadingStart("解析用户群发数据...");

					String strResult = EntityUtils.toString(response
							.getEntity());
					int massDataGetResult = DataParser.getMassData(strResult,
							mDataManager.getUserGroup().get(userIndex));
					mDataManager.doProfileGet(mDataManager.getUserGroup().get(
							userIndex));
					switch (massDataGetResult) {
					case DataParser.GET_MASS_DATA_SUCCESS:
						onActionFinishListener.onFinish(null);

						break;
					case DataParser.GET_MASS_DATA_FAILED:

						break;
					}

				} catch (Exception exception) {
					Log.e("mass parse error", "" + exception);

				}
			}
		}, mDataManager.getUserGroup().get(userIndex));
	}

	public void getNewMessageList(final boolean popLoadingDialog,
			final int userIndex,
			final OnActionFinishListener onActionFinishListener) {

		mDataManager.doLoadingStart("获取消息数据...");

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
			public void onBack(HttpResponse response, String referer) {
				// TODO Auto-generated method
				// stub

				try {
					mDataManager.doLoadingStart("解析消息数据...");

					String strResult = EntityUtils.toString(response
							.getEntity());

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
			public void onBack(HttpResponse response, String referer) {
				// TODO Auto-generated method stub
				try {

					String strResult = EntityUtils.toString(response
							.getEntity());

					DataParser.getNextMessage(new MessageListParseCallBack() {

						@Override
						public void onBack(
								MessageResultHolder messageResultHolder) {
							// TODO
							// Auto-generated
							// method
							// stub
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
					public void onBack(HttpResponse response) {
						// TODO Auto-generated method stub

						try {

							String strResult = EntityUtils.toString(response
									.getEntity());
							Toast.makeText(mContext, "回复成功", Toast.LENGTH_SHORT)
									.show();
							onActionFinishListener.onFinish(null);
						} catch (Exception exception) {

						}
					}
				}, mDataManager.getUserGroup().get(userIndex),
				mDataManager.getMessageHolders().get(userIndex)
						.getMessageList().get(position), replyContent);

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
					public void onBack(HttpResponse response) {
						// TODO Auto-generated method
						// stub
						try {

							String strResult = EntityUtils.toString(response
									.getEntity());

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
				}, mDataManager.getUserGroup().get(userIndex),
				mDataManager.getMessageHolders().get(userIndex)
						.getMessageList().get(position), star);
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
			public void onBack(HttpResponse response) {
				// TODO Auto-generated method stub
				try {
					mDataManager.doLoadingEnd();

					String strResult = EntityUtils.toString(response
							.getEntity());
					Log.e("mass result", strResult);

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

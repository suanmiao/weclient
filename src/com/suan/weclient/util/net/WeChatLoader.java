package com.suan.weclient.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.suan.weclient.util.data.ChatHolder;
import com.suan.weclient.util.data.MessageHolder;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.data.UserBean;

public class WeChatLoader {

	public static final int WECHAT_LOGIN_OK = 302;
	public static final int WECHAT_MASS_OK = 0;
	public static final int WECHAT_MASS_ERROR_ONLY_ONE = 64004;

	public static final int WECHAT_STAR_OK = 0;

	public static final int WECHAT_REPLY_OK = 0;

    public static final int WECHAT_SINGLE_CHAT_OK = 0;

	private static final String WECHAT_URL_LOGIN = "http://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN";


    /*
        all,today,yesterday,day before:
        url1+url2+day+url4+tokdn+url5

        star:
        url1+url3+token+url5
     */

    public static final int GET_MESSAGE_ALL = 0;
    public static final int GET_MESSAGE_TODAY = 1;
    public static final int GET_MESSAGE_YESTERDAY = 2;
    public static final int GET_MESSAGE_DAY_BEFORE = 3;
    public static final int GET_MESSAGE_OLDER = 4;

    public static final int GET_MESSAGE_STAR = 5;

	private static final String WECHAT_URL_GET_MESSAGE_LIST_1 = "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&count=20&";
    private static final String WECHAT_URL_GET_MESSAGE_LIST_2 = "&day=";
    private static final String WECHAT_URL_GET_MESSAGE_LIST_3 = "&action=star&token=";
    private static final String WECHAT_URL_GET_MESSAGE_LIST_4 = "&token=";
    private static final String WECHAT_URL_GET_MESSAGE_LIST_5 = "&lang=zh_CN";

	private static final String WECHAT_URL_MESSAGE_LOAD_PAGE_1 = "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&action=&keyword=&frommsgid=";
	private static final String WECHAT_URL_MESSAGE_LOAD_PAGE_2 = "&offset=";
	private static final String WECHAT_URL_MESSAGE_LOAD_PAGE_3 = "&count=20&day=7&token=";
	private static final String WECHAT_URL_MESSAGE_LOAD_PAGE_4 = "&lang=zh_CN";

	private static final String WECHAT_URL_MESSAGE_REPLY = "https://mp.weixin.qq.com/cgi-bin/singlesend";

	private static final String WECHAT_URL_MESSAGE_STAR = "https://mp.weixin.qq.com/cgi-bin/setstarmessage";

	private static final String WECHAT_URL_MESSAGE_MASS = "https://mp.weixin.qq.com/cgi-bin/masssend";

	private static final String WECHAT_URL_GET_MESSAGE_PROFILE_IMG_1 = "https://mp.weixin.qq.com/cgi-bin/getheadimg?token=";
	private static final String WECHAT_URL_GET_MESSAGE_PROFILE_IMG_2 = "&fakeid=";

	private static final String WECHAT_URL_GET_USER_PROFILE = "https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=";

	private static final String WECHAT_URL_GET_MASS_DATA_1 = "https://mp.weixin.qq.com/cgi-bin/masssendpage?t=mass/send&token=";
	private static final String WECHAT_URL_GET_MASS_DATA_2 = "&lang=zh_CN";

	private static final String WECHAT_URL_GET_MESSAGE_IMG_1 = "https://mp.weixin.qq.com/cgi-bin/getimgdata?token=";
	private static final String WECHAT_URL_GET_MESSAGE_IMG_2 = "&msgid=";
	private static final String WECHAT_URL_GET_MESSAGE_IMG_3 = "&mode=";
	private static final String WECHAT_URL_GET_MESSAGE_IMG_4 = "&source=&fileId=0";

	public static final String WECHAT_URL_MESSAGE_IMG_LARGE = "large";
	public static final String WECHAT_URL_MESSAGE_IMG_SMALL = "small";

	private static final String WECHAT_URL_GET_VOICE_MESSAGE_1 = "https://mp.weixin.qq.com/cgi-bin/getvoicedata?msgid=";

	private static final String WECHAT_URL_GET_VOICE_MESSAGE_2 = "&fileid=&token=";
	private static final String WECHAT_URL_GET_VOICE_MESSAGE_3 = "&lang=zh_CN";

	private static final String WECHAT_URL_GET_VOICE_MESSAGE_REFERER = "https://mp.weixin.qq.com/mpres/zh_CN/htmledition/plprecorder/soundmanager2.swf";

	private static final String WECHAT_URL_GET_FANS_LIST_1 = "https://mp.weixin.qq.com/cgi-bin/contactmanage?t=user/index&pagesize=10&pageidx=";
	/*
	 * if want all the user,use 2,without groupId if want group user ,use 3,with
	 * groupId
	 */
	private static final String WECHAT_URL_GET_FANS_LIST_2 = "&type=0";
	private static final String WECHAT_URL_GET_FANS_LIST_3 = "&type=0&groupid=";
	private static final String WECHAT_URL_GET_FANS_LIST_4 = "&token=";
	private static final String WECHAT_URL_GET_FANS_LIST_5 = "&lang=zh_CN";

	private static final String WECHAT_URL_GET_CHAT_LIST_1 = "https://mp.weixin.qq.com/cgi-bin/singlesendpage?tofakeid=";
	private static final String WECHAT_URL_GET_CHAT_LIST_2 = "&t=message/send&action=index&token=";
	private static final String WECHAT_URL_GET_CHAT_LIST_3 = "&lang=zh_CN";

	private static final String WECHAT_URL_CHAT_SINGLE_SEND = "https://mp.weixin.qq.com/cgi-bin/singlesend";

    private static final String WECHAT_URL_GET_CHAT_NEW_ITEM_1 = "https://mp.weixin.qq.com/cgi-bin/singlesendpage?token=";
    private static final String WECHAT_URL_GET_CHAT_NEW_ITEM_2 = "&lang=zh_CN&random=0.7740735916886479&f=json&ajax=1&tofakeid=";
    private static final String WECHAT_URL_GET_CHAT_NEW_ITEM_3 = "&action=sync&lastmsgfromfakeid=";
    private static final String WECHAT_URL_GET_CHAT_NEW_ITEM_4 = "&lastmsgid=";
    private static final String WECHAT_URL_GET_CHAT_NEW_ITEM_5 = "&createtime=";

	public interface WechatExceptionListener {
		public void onError();
	}

	/**
	 * 回调接口 *
	 */

	public interface WechatLoginCallBack {
		public void onBack(String result, String slaveSid, String slaveUser);
	}

	public static void wechatLogin(
			final WechatExceptionListener wechatExceptionListener,
			final WechatLoginCallBack loginCallBack, final String username,
			final String pwd, final String imgcode, final String f) {
		final Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				loginCallBack.onBack(contentHolder.get("result"),
						contentHolder.get("slaveSid"),
						contentHolder.get("slaveUser"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList
						.add(new BasicNameValuePair("Referer",
								"https://mp.weixin.qq.com/cgi-bin/loginpage?t=wxm-login&lang=zh_CN"));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", username));
				params.add(new BasicNameValuePair("pwd", pwd));
				params.add(new BasicNameValuePair("imgcode", imgcode));
				params.add(new BasicNameValuePair("f", f));

				HttpResponse response = httpPost(WECHAT_URL_LOGIN, headerList,
						params);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						for (int i = 0; i < response.getAllHeaders().length; i++) {
							if (response.getAllHeaders()[i].getName().contains(
									"Set-Cookie")) {
								String nowCookie = response.getAllHeaders()[i]
										.getValue();
								if (nowCookie.contains("slave_user")) {
									String slaveUser = nowCookie
											.substring(
													nowCookie
															.indexOf("slave_user") + 11,
													nowCookie.indexOf(";"));
									contentHolder.put("slaveUser", slaveUser);
								}
								if (nowCookie.contains("slave_sid")) {

									String slaveSid = nowCookie
											.substring(nowCookie
													.indexOf("slave_sid") + 10,
													nowCookie.indexOf(";"));
									contentHolder.put("slaveSid", slaveSid);
								}

							}

						}
						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetUserProfleCallBack {
		public void onBack(String strResult, String referer);
	}

	public static void wechatGetUserProfile(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetUserProfleCallBack userProfileCallBack,
			final UserBean userBean) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				userProfileCallBack.onBack(contentHolder.get("result"),
						contentHolder.get("referer"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				String targetUrl = WECHAT_URL_GET_USER_PROFILE
						+ userBean.getToken();
				HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();

						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						contentHolder.put("referer", targetUrl);

						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();

				}

			}

		}.start();

	}

	public interface WechatMessageListCallBack {
		public void onBack(String strResult, String referer);
	}

	public static void wechatGetMessageList(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMessageListCallBack messageListCallBack,
			final UserBean userBean,final int mode) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;

				messageListCallBack.onBack(contentHolder.get("result"),
						contentHolder.get("referer"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

    /*
        all,today,yesterday,day before:
        url1+url2+day+url4+tokdn+url5

        star:
        url1+url3+token+url5
     */
 				String targetUrl = "";

               switch(mode){
                    case GET_MESSAGE_ALL:
                        targetUrl = WECHAT_URL_GET_MESSAGE_LIST_1+
                                WECHAT_URL_GET_MESSAGE_LIST_2+
                                7+
                                WECHAT_URL_GET_MESSAGE_LIST_4+
                                userBean.getToken()+
                                WECHAT_URL_GET_MESSAGE_LIST_5;


                        break;
                    case GET_MESSAGE_TODAY:

                        targetUrl = WECHAT_URL_GET_MESSAGE_LIST_1+
                                WECHAT_URL_GET_MESSAGE_LIST_2+
                                0+
                                WECHAT_URL_GET_MESSAGE_LIST_4+
                                userBean.getToken()+
                                WECHAT_URL_GET_MESSAGE_LIST_5;
                         break;

                    case GET_MESSAGE_YESTERDAY:

                        targetUrl = WECHAT_URL_GET_MESSAGE_LIST_1+
                                WECHAT_URL_GET_MESSAGE_LIST_2+
                                1+
                                WECHAT_URL_GET_MESSAGE_LIST_4+
                                userBean.getToken()+
                                WECHAT_URL_GET_MESSAGE_LIST_5;
                         break;

                    case GET_MESSAGE_DAY_BEFORE:

                        targetUrl = WECHAT_URL_GET_MESSAGE_LIST_1+
                                WECHAT_URL_GET_MESSAGE_LIST_2+
                                2+
                                WECHAT_URL_GET_MESSAGE_LIST_4+
                                userBean.getToken()+
                                WECHAT_URL_GET_MESSAGE_LIST_5;
                         break;

                   case GET_MESSAGE_OLDER:
                         targetUrl = WECHAT_URL_GET_MESSAGE_LIST_1+
                                WECHAT_URL_GET_MESSAGE_LIST_2+
                                3+
                                WECHAT_URL_GET_MESSAGE_LIST_4+
                                userBean.getToken()+
                                WECHAT_URL_GET_MESSAGE_LIST_5;

                       break;

                    case GET_MESSAGE_STAR:
                        targetUrl = WECHAT_URL_GET_MESSAGE_LIST_1+
                                WECHAT_URL_GET_MESSAGE_LIST_3+
                                userBean.getToken()+
                                WECHAT_URL_GET_MESSAGE_LIST_5;

                        break;
                }
			HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						contentHolder.put("referer", targetUrl);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatMessagePageCallBack {
		public void onBack(String strResult, String referer);
	}

	public static void wechatGetMessagePage(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMessagePageCallBack messagePageCallBack,
			final MessageHolder messageHolder, final int page) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;

				messagePageCallBack.onBack(contentHolder.get("result"),
						contentHolder.get("referer"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ messageHolder.getUserBean().getSlaveSid() + "; "
						+ "slave_user="
						+ messageHolder.getUserBean().getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				int offset = (page - 1) * 20;
				String targetUrl = WECHAT_URL_MESSAGE_LOAD_PAGE_1
						+ messageHolder.getLatestMsgId()
						+ WECHAT_URL_MESSAGE_LOAD_PAGE_2 + offset
						+ WECHAT_URL_MESSAGE_LOAD_PAGE_3
						+ messageHolder.getUserBean().getToken()
						+ WECHAT_URL_MESSAGE_LOAD_PAGE_4;
				HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						contentHolder.put("referer", targetUrl);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatMessageReplyCallBack {
		public void onBack(String strResult);
	}

	public static void wechatMessageReply(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMessageReplyCallBack messagReplyCallBack,
			final UserBean userBean, final MessageBean messageBean,
			final String replyContent) {
		final Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);

				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;

				messagReplyCallBack.onBack(contentHolder.get("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				headerList.add(new BasicNameValuePair("Referer", messageBean
						.getReferer()));

				ArrayList<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
				paramArrayList.add(new BasicNameValuePair("mask", "false"));
				paramArrayList.add(new BasicNameValuePair("tofakeid",
						messageBean.getFakeId()));
				paramArrayList.add(new BasicNameValuePair("imgcode", ""));
				paramArrayList.add(new BasicNameValuePair("type", "1"));
				paramArrayList.add(new BasicNameValuePair("content",
						replyContent));
				paramArrayList.add(new BasicNameValuePair("quickreplyid",
						messageBean.getId()));
				paramArrayList
						.add(new BasicNameValuePair("t", "ajax-response"));
				paramArrayList.add(new BasicNameValuePair("token", userBean
						.getToken()));
				paramArrayList.add(new BasicNameValuePair("lang", "zh_CN"));
				String targetUrl = WECHAT_URL_MESSAGE_REPLY;
				HttpResponse response = httpPost(targetUrl, headerList,
						paramArrayList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatMessageStarCallBack {
		public void onBack(String strResult);
	}

	public static void wechatMessageStar(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMessageStarCallBack messagStarCallBack,
			final UserBean userBean, final MessageBean messageBean,
			final boolean star) {
		final Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);

				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				messagStarCallBack.onBack(contentHolder.get("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				headerList.add(new BasicNameValuePair("Referer", messageBean
						.getReferer()));

				ArrayList<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
				paramArrayList.add(new BasicNameValuePair("msgid", messageBean
						.getId()));
				paramArrayList.add(new BasicNameValuePair("value", star ? "1"
						: "0"));
				paramArrayList.add(new BasicNameValuePair("t",
						"ajax-setstarmessage"));
				paramArrayList.add(new BasicNameValuePair("token", userBean
						.getToken()));
				paramArrayList.add(new BasicNameValuePair("lang", "zh_CN"));
				String targetUrl = WECHAT_URL_MESSAGE_STAR;
				HttpResponse response = httpPost(targetUrl, headerList,
						paramArrayList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatMassCallBack {
		public void onBack(String strResult);
	}

	public static void wechatMass(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMassCallBack massCallBack, final UserBean userBean,
			final String content) {
		final Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);

				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				massCallBack.onBack(contentHolder.get("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				headerList.add(new BasicNameValuePair("Referer",
						"https://mp.weixin.qq.com/cgi-bin/masssendpage?t=mass/send&token="
								+ userBean.getToken() + "&lang=zh_CN"));

				ArrayList<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
				paramArrayList.add(new BasicNameValuePair("type", "1"));
				paramArrayList.add(new BasicNameValuePair("content", content));
				paramArrayList.add(new BasicNameValuePair("sex", "0"));
				paramArrayList.add(new BasicNameValuePair("groupid", "-1"));
				paramArrayList.add(new BasicNameValuePair("synctxweibo", "0"));
				paramArrayList.add(new BasicNameValuePair("synctxnews", "0"));
				paramArrayList.add(new BasicNameValuePair("country", ""));
				paramArrayList.add(new BasicNameValuePair("province", ""));
				paramArrayList.add(new BasicNameValuePair("city", ""));
				paramArrayList.add(new BasicNameValuePair("imgcode", ""));
				paramArrayList.add(new BasicNameValuePair("token", userBean
						.getToken()));
				paramArrayList.add(new BasicNameValuePair("lang", "zh_CN"));
				paramArrayList.add(new BasicNameValuePair("random",
						"0.7117042664902147"));
				paramArrayList.add(new BasicNameValuePair("f", "json"));
				paramArrayList.add(new BasicNameValuePair("ajax", "1"));
				paramArrayList
						.add(new BasicNameValuePair("t", "ajax-response"));
				String targetUrl = WECHAT_URL_MESSAGE_MASS;
				HttpResponse response = httpPost(targetUrl, headerList,
						paramArrayList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetHeadImgCallBack {
		public void onBack(Bitmap bitmap, String referer, ImageView imageView);
	}

	public static void wechatGetHeadImg(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetHeadImgCallBack getHeadImgCallBack,
			final UserBean userBean, final String fakeId, final String referer,
			final ImageView imageView) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				getHeadImgCallBack.onBack(
						(Bitmap) contentHolder.getExtra("result"),
						contentHolder.get("referer"), imageView);

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				headerList.add(new BasicNameValuePair("Referer", referer));

				String targetUrl = WECHAT_URL_GET_MESSAGE_PROFILE_IMG_1
						+ userBean.getToken()
						+ WECHAT_URL_GET_MESSAGE_PROFILE_IMG_2 + fakeId;
				HttpResponse response = httpGet(targetUrl, headerList);
				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						Bitmap bitmap = BitmapFactory
								.decodeStream((InputStream) response
										.getEntity().getContent());
						contentHolder.putExtra("result", bitmap);
						contentHolder.put("referer", targetUrl);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetMessageImgCallBack {
		public void onBack(Bitmap bitmap, ImageView imageView);
	}

	public static void wechatGetMessageImg(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetMessageImgCallBack getMessageImgCallBack,
			final String msgId, final String slaveSid, final String slaveUser,
			final String token, final String referer,
			final ImageView imageView, final String imgType) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				getMessageImgCallBack.onBack(
						(Bitmap) contentHolder.getExtra("result"), imageView);

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ slaveSid + "; " + "slave_user=" + slaveUser));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				headerList.add(new BasicNameValuePair("Referer", referer));

				String targetUrl = WECHAT_URL_GET_MESSAGE_IMG_1 + token
						+ WECHAT_URL_GET_MESSAGE_IMG_2 + msgId
						+ WECHAT_URL_GET_MESSAGE_IMG_3 + imgType
						+ WECHAT_URL_GET_MESSAGE_IMG_4;
				HttpResponse response = httpGet(targetUrl, headerList);
				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						Bitmap bitmap = BitmapFactory
								.decodeStream((InputStream) response
										.getEntity().getContent());
						contentHolder.putExtra("result", bitmap);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetVoiceMsgCallBack {
		public void onBack(byte[] bytes);
	}

	public static void wechatGetVoiceMsg(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetVoiceMsgCallBack getVoiceMsgCallBack,
			final UserBean userBean, final String msgId, final int length) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				getVoiceMsgCallBack.onBack((byte[]) contentHolder
						.getExtra("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				headerList.add(new BasicNameValuePair("Referer",
						WECHAT_URL_GET_VOICE_MESSAGE_REFERER));

				String targetUrl = WECHAT_URL_GET_VOICE_MESSAGE_1 + msgId
						+ WECHAT_URL_GET_VOICE_MESSAGE_2 + userBean.getToken()
						+ WECHAT_URL_GET_VOICE_MESSAGE_3;
				HttpResponse response = httpGet(targetUrl, headerList);
				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						InputStream inputStream = (InputStream) response
								.getEntity().getContent();
						byte[] bytes = new byte[length];
						inputStream.read(bytes);

						contentHolder.putExtra("result", bytes);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetMassData {
		public void onBack(String strResult);
	}

	public static void wechatGetMassData(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetMassData wechatGetMassData, final UserBean userBean) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				wechatGetMassData.onBack(contentHolder.get("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				headerList.add(new BasicNameValuePair("Referer",
						WECHAT_URL_GET_USER_PROFILE + userBean.getToken()));
				String targetUrl = WECHAT_URL_GET_MASS_DATA_1
						+ userBean.getToken() + WECHAT_URL_GET_MASS_DATA_2;

				HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();

						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);

						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetFansList {
		public void onBack(String strResult, String referer);
	}

	public static void wechatGetFansList(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetFansList wechatGetFansList, final UserBean userBean,
			final int groupId, final int page) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				wechatGetFansList.onBack(contentHolder.get("result"),
						contentHolder.get("referer"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				String referer = "";
				if (page == 0) {
					referer = "https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token="
							+ userBean.getToken();
				} else {

					referer = WECHAT_URL_GET_FANS_LIST_1 + (page - 1)
							+ WECHAT_URL_GET_FANS_LIST_2 + groupId
							+ WECHAT_URL_GET_FANS_LIST_3 + userBean.getToken()
							+ WECHAT_URL_GET_FANS_LIST_4;

				}

				headerList.add(new BasicNameValuePair("Referer", referer));
				String targetUrl = "";
				if (groupId == -1) {
					// all user
					targetUrl = WECHAT_URL_GET_FANS_LIST_1 + page
							+ WECHAT_URL_GET_FANS_LIST_2
							+ WECHAT_URL_GET_FANS_LIST_4 + userBean.getToken()
							+ WECHAT_URL_GET_FANS_LIST_5;
				} else {

					targetUrl = WECHAT_URL_GET_FANS_LIST_1 + page
							+ WECHAT_URL_GET_FANS_LIST_3 + groupId
							+ WECHAT_URL_GET_FANS_LIST_4 + userBean.getToken()
							+ WECHAT_URL_GET_FANS_LIST_5;
				}

				HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();

						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						contentHolder.put("referer", targetUrl);

						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetChatList {
		public void onBack(String strResult);
	}

	public static void wechatGetChatList(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetChatList wechatGetChatList, final UserBean userBean,
			final String toFakeId) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				wechatGetChatList.onBack(contentHolder.get("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				String targetUrl = WECHAT_URL_GET_CHAT_LIST_1 + toFakeId
						+ WECHAT_URL_GET_CHAT_LIST_2 + userBean.getToken()
						+ WECHAT_URL_GET_CHAT_LIST_3;

				HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();

						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);

						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	/**
	 * 回调接口 *
	 */

	public interface WechatChatSingleCallBack {
		public void onBack(String result);
	}

	public static void wechatChatSingle(
			final WechatExceptionListener wechatExceptionListener,
			final WechatChatSingleCallBack chatSingleCallBack,
			final UserBean userBean,final MessageBean messageBean) {
		final Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				chatSingleCallBack.onBack(contentHolder.get("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();

				String referer = WECHAT_URL_GET_CHAT_LIST_1
						+ messageBean.getToFakeId() + WECHAT_URL_GET_CHAT_LIST_2
						+ userBean.getToken()
						+ WECHAT_URL_GET_CHAT_LIST_3;
				headerList.add(new BasicNameValuePair("Referer", referer));
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("type", messageBean.getType()+ ""));
				params.add(new BasicNameValuePair("content", messageBean.getContent()));
				params.add(new BasicNameValuePair("tofakeid", ""
						+ messageBean.getToFakeId()));
				params.add(new BasicNameValuePair("imgcode", ""));
				params.add(new BasicNameValuePair("token", ""
						+ userBean.getToken()));
				params.add(new BasicNameValuePair("lang", "zh_CN"));
				params.add(new BasicNameValuePair("random",
						"0.015136290108785033"));
				params.add(new BasicNameValuePair("f", "json"));
				params.add(new BasicNameValuePair("ajax", "1"));
				params.add(new BasicNameValuePair("t", "ajax-response"));


				HttpResponse response = httpPost(WECHAT_URL_CHAT_SINGLE_SEND,
						headerList, params);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();
						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);
						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetChatNewItems {
		public void onBack(String strResult);
	}

	public static void wechatGetChatNewItems(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetChatNewItems wechatGetChatNewItems, final UserBean userBean,
            final MessageBean messageBean,
            final String lastMsgId,
            final String createTime,


			final String toFakeId) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ContentHolder contentHolder = (ContentHolder) msg.obj;
				wechatGetChatNewItems.onBack(contentHolder.get("result"));

			}
		};

		new Thread() {
			public void run() {
				Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));

				String referer = WECHAT_URL_GET_CHAT_LIST_1
						+ messageBean.getToFakeId() + WECHAT_URL_GET_CHAT_LIST_2
						+ userBean.getToken()
						+ WECHAT_URL_GET_CHAT_LIST_3;
				headerList.add(new BasicNameValuePair("Referer", referer));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
                String targetUrl = WECHAT_URL_GET_CHAT_NEW_ITEM_1+userBean.getToken()
                        +WECHAT_URL_GET_CHAT_NEW_ITEM_2+messageBean.getToFakeId()
                        +WECHAT_URL_GET_CHAT_NEW_ITEM_3+userBean.getFakeId()
                        +WECHAT_URL_GET_CHAT_NEW_ITEM_4+lastMsgId
                        +WECHAT_URL_GET_CHAT_NEW_ITEM_5+createTime;

				HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					try {

						Message message = new Message();
						ContentHolder contentHolder = new ContentHolder();

						String strResult = EntityUtils.toString(response
								.getEntity());
						contentHolder.put("result", strResult);

						message.obj = contentHolder;

						loadHandler.sendMessage(message);
					} catch (Exception exception) {

					}

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	private static HttpResponse httpPost(String targetUrl,
			ArrayList<NameValuePair> headerArrayList,
			ArrayList<NameValuePair> paramsArrayList) {
		/* 声明网址字符串 */
		/* 建立HTTP Post联机 */
		HttpPost httpRequest = new HttpPost(targetUrl);
		/*
		 * Post运作传送变量必须用NameValuePair[]数组储存
		 */

		try {
			/* 发出HTTP request */
			httpRequest.setEntity(new UrlEncodedFormEntity(paramsArrayList,
					HTTP.UTF_8));

			for (int i = 0; i < headerArrayList.size(); i++) {

				httpRequest.addHeader(headerArrayList.get(i).getName(),
						headerArrayList.get(i).getValue());

			}

			/* 取得HTTP response */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */

				return httpResponse;
			} else {
				Log.e("errorcode", httpResponse.getStatusLine().toString());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static HttpResponse httpGet(String targetUrl,
			ArrayList<NameValuePair> headerArrayList) {
		/* 声明网址字符串 */
		/* 建立HTTP Post联机 */
		HttpGet httpRequest = new HttpGet(targetUrl);
		/*
		 * Post运作传送变量必须用NameValuePair[]数组储存
		 */

		try {
			/* 发出HTTP request */

			for (int i = 0; i < headerArrayList.size(); i++) {

				httpRequest.addHeader(headerArrayList.get(i).getName(),
						headerArrayList.get(i).getValue());

			}
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 130000);
			HttpConnectionParams.setSoTimeout(httpParams, 150000);
			/* 取得HTTP response */
			HttpClient httpClient = new DefaultHttpClient(httpParams);

			HttpResponse httpResponse = httpClient.execute(httpRequest);

			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */

				return httpResponse;
			} else {
				Log.e("errorcode", httpResponse.getStatusLine().toString());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	public static class ContentHolder {
		private Map<String, String> content = new HashMap<String, String>();
		private Map<String, Object> extra = new HashMap<String, Object>();

		public void put(String key, String value) {
			content.put(key, value);
		}

		public String get(String key) {
			return content.get(key);
		}

		public void putExtra(String key, Object object) {
			extra.put(key, object);
		}

		public Object getExtra(String key) {
			return extra.get(key);
		}

	}

}

package com.suan.weclient.util.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.Header;
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

import com.suan.weclient.R.id;
import com.suan.weclient.util.MessageHolder;
import com.suan.weclient.util.MessageItem;
import com.suan.weclient.util.UserBean;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class WeChatLoader {

	public static final int WECHAT_LOGIN_OK = 302;
	public static final int WECHAT_MASS_OK = 0;
	public static final int WECHAT_MASS_ERROR_ONLY_ONE = 64004;

	public static final int WECHAT_STAR_OK = 0;

	public static final int WECHAT_REPLY_OK = 0;

	private static final String WECHAT_URL_LOGIN = "http://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN";
	private static final String WECHAT_URL_MESSAGE_LIST_1 = "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&count=20&day=7&token=";
	private static final String WECHAT_URL_MESSAGE_LIST_2 = "&lang=zh_CN";

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
	
	public interface WechatExceptionListener {
		public void onError();
	}

	/**
	 * 回调接口 *
	 */

	public interface WechatLoginCallBack {
		public void onBack(HttpResponse response);
	}

	public static void wechatLogin(
			final WechatExceptionListener wechatExceptionListener,
			final WechatLoginCallBack loginCallBack, final String username,
			final String pwd, final String imgcode, final String f) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				HttpResponse getHttpResponse = (HttpResponse) msg.obj;
				loginCallBack.onBack(getHttpResponse);

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
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

					Message message = new Message();
					message.obj = response;

					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}


	public interface WechatMessageListCallBack {
		public void onBack(HttpResponse response, String referer);
	}

	public static void wechatGetMessageList(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMessageListCallBack messageListCallBack,
			final UserBean userBean) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ResponseHolder responseHolder = (ResponseHolder) msg.obj;
				messageListCallBack.onBack(responseHolder.getHttpResponse(),
						responseHolder.getReferer());

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				String targetUrl = WECHAT_URL_MESSAGE_LIST_1
						+ userBean.getToken() + WECHAT_URL_MESSAGE_LIST_2;
				HttpResponse response = httpGet(targetUrl, headerList);

				if (response != null) {

					Message message = new Message();
					ResponseHolder responseHolder = new ResponseHolder(response);
					responseHolder.setReferer(targetUrl);
					message.obj = responseHolder;

					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}


	public interface WechatMessagePageCallBack {
		public void onBack(HttpResponse response, String referer);
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
				ResponseHolder responseHolder = (ResponseHolder) msg.obj;
				messagePageCallBack.onBack(responseHolder.getHttpResponse(),
						responseHolder.getReferer());

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
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

					Message message = new Message();
					ResponseHolder responseHolder = new ResponseHolder(response);
					responseHolder.setReferer(targetUrl);
					message.obj = responseHolder;

					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}


	public interface WechatMessageReplyCallBack {
		public void onBack(HttpResponse response);
	}

	public static void wechatMessageReply(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMessageReplyCallBack messagReplyCallBack,
			final UserBean userBean, final MessageItem messageItem,
			final String replyContent) {
		final Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);

				// 此处可以更新UI
				HttpResponse getHttpResponse = (HttpResponse) msg.obj;
				messagReplyCallBack.onBack(getHttpResponse);

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				headerList.add(new BasicNameValuePair("Referer", messageItem
						.getReferer()));

				ArrayList<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
				paramArrayList.add(new BasicNameValuePair("mask", "false"));
				paramArrayList.add(new BasicNameValuePair("tofakeid",
						messageItem.getFakeId()));
				paramArrayList.add(new BasicNameValuePair("imgcode", ""));
				paramArrayList.add(new BasicNameValuePair("type", "1"));
				paramArrayList.add(new BasicNameValuePair("content",
						replyContent));
				paramArrayList.add(new BasicNameValuePair("quickreplyid",
						messageItem.getId()));
				paramArrayList
						.add(new BasicNameValuePair("t", "ajax-response"));
				paramArrayList.add(new BasicNameValuePair("token", userBean
						.getToken()));
				paramArrayList.add(new BasicNameValuePair("lang", "zh_CN"));
				String targetUrl = WECHAT_URL_MESSAGE_REPLY;
				HttpResponse response = httpPost(targetUrl, headerList,
						paramArrayList);

				if (response != null) {

					Message message = new Message();
					message.obj = response;
					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}


	public interface WechatMessageStarCallBack {
		public void onBack(HttpResponse response);
	}

	public static void wechatMessageStar(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMessageStarCallBack messagStarCallBack,
			final UserBean userBean, final MessageItem messageItem,
			final boolean star) {
		final Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);

				// 此处可以更新UI
				HttpResponse getHttpResponse = (HttpResponse) msg.obj;
				messagStarCallBack.onBack(getHttpResponse);

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ userBean.getSlaveSid() + "; " + "slave_user="
						+ userBean.getSlaveUser()));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));
				headerList.add(new BasicNameValuePair("Referer", messageItem
						.getReferer()));

				ArrayList<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
				paramArrayList.add(new BasicNameValuePair("msgid", messageItem
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

					Message message = new Message();
					message.obj = response;
					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}


	public interface WechatMassCallBack {
		public void onBack(HttpResponse response);
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
				HttpResponse getHttpResponse = (HttpResponse) msg.obj;
				massCallBack.onBack(getHttpResponse);

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
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
				paramArrayList.add(new BasicNameValuePair("random", "0.7117042664902147"));
				paramArrayList.add(new BasicNameValuePair("f", "json"));
				paramArrayList.add(new BasicNameValuePair("ajax", "1"));
				paramArrayList.add(new BasicNameValuePair("t", "ajax-response"));
				String targetUrl = WECHAT_URL_MESSAGE_MASS;
				HttpResponse response = httpPost(targetUrl, headerList,paramArrayList);

				if (response != null) {

					Message message = new Message();
					message.obj = response;
					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetHeadImgCallBack {
		public void onBack(HttpResponse response, String referer,
				ImageView imageView);
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
				ResponseHolder responseHolder = (ResponseHolder) msg.obj;
				getHeadImgCallBack.onBack(responseHolder.getHttpResponse(),
						responseHolder.getReferer(), imageView);

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
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

					Message message = new Message();
					ResponseHolder responseHolder = new ResponseHolder(response);
					responseHolder.setReferer(targetUrl);
					message.obj = responseHolder;

					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

	}

	public interface WechatGetMessageImgCallBack {
		public void onBack(HttpResponse response, ImageView imageView);
	}

	public static void wechatGetMessageImg(
			final WechatExceptionListener wechatExceptionListener,
			final WechatGetMessageImgCallBack getMessageImgCallBack,
			final String msgId,
			final String slaveSid,final String slaveUser, 
			final String token,
			final String referer,
			final ImageView imageView,final String imgType) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				HttpResponse response = (HttpResponse)msg.obj;
				getMessageImgCallBack.onBack(response,
						imageView);

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
				ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
				headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
						+ slaveSid + "; " + "slave_user="
						+ slaveUser));
				headerList.add(new BasicNameValuePair("Content-Type",
						"text/html; charset=utf-8"));

				headerList.add(new BasicNameValuePair("Referer", referer));
				
				String targetUrl = WECHAT_URL_GET_MESSAGE_IMG_1 + token+WECHAT_URL_GET_MESSAGE_IMG_2+msgId+WECHAT_URL_GET_MESSAGE_IMG_3+imgType+WECHAT_URL_GET_MESSAGE_IMG_4;
				HttpResponse response = httpGet(targetUrl, headerList);
				if (response != null) {

					Message message = new Message();
					message.obj = response;
					loadHandler.sendMessage(message);

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


	public interface WechatGetUserProfleCallBack {
		public void onBack(HttpResponse response, String referer);
	}

	public static void wechatGetUserProfile(final WechatExceptionListener wechatExceptionListener,
			final WechatGetUserProfleCallBack userProfileCallBack,
			final UserBean userBean) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				ResponseHolder responseHolder = (ResponseHolder) msg.obj;
				userProfileCallBack.onBack(responseHolder.getHttpResponse(),
						responseHolder.getReferer());

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
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

					Message message = new Message();
					ResponseHolder responseHolder = new ResponseHolder(response);
					responseHolder.setReferer(targetUrl);
					message.obj = responseHolder;

					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();

				}

			}

		}.start();

	}

	public interface WechatGetMassData {
		public void onBack(HttpResponse response);
	}

	public static void wechatGetMassData(
			final WechatExceptionListener wechatExceptionListener,
			final WechatMassCallBack massDataCallBack, final UserBean userBean) {
		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				HttpResponse response = (HttpResponse) msg.obj;
				massDataCallBack.onBack(response);

			}
		};

		new Thread() {
			public void run() { Looper.prepare();
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

					Message message = new Message();
					message.obj = response;

					loadHandler.sendMessage(message);

				} else {
					wechatExceptionListener.onError();
				}

			}

		}.start();

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

	public static class ResponseHolder {
		private HttpResponse httpResponse;

		private String referer = "";

		public ResponseHolder(HttpResponse response) {
			httpResponse = response;
		}

		public void setReferer(String referer) {
			this.referer = referer;

		}

		public HttpResponse getHttpResponse() {
			return httpResponse;
		}

		public String getReferer() {
			return referer;
		}

	}

}

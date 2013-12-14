package com.suan.weclient.util.net;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.data.FansBean;
import com.suan.weclient.util.data.FansGroupBean;
import com.suan.weclient.util.data.FansHolder;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.data.MessageHolder;
import com.suan.weclient.util.data.UserBean;

public class DataParser {

	public static final int LOGIN_SUCCESS = 1;
	public static final int LOGIN_FAILED = 0;
	public static final int GET_MESSAGE_SUCCESS = 1;
	public static final int GET_MESSAGE_FAILED = 0;

	public static final int GET_USER_PROFILE_SUCCESS = 1;
	public static final int GET_USER_PROFILE_FAILED = 0;

	public static final int GET_MASS_DATA_SUCCESS = 1;

	public static final int GET_MASS_DATA_FAILED = 0;

	public static final String today = "今天";

	public static int parseUserProfile(String source, UserBean userBean) {

		Document document = Jsoup.parse(source);
		Elements numElements = document.getElementsByClass("number");
		for (int i = 0; i < numElements.size(); i++) {
			if (numElements.size() == 3) {

				if (i == 0) {
					int newMessage = Integer
							.parseInt(numElements.get(i).html());
					userBean.setNewMessage(newMessage + "");

				}
				if (i == 1) {

					int newPeople = Integer.parseInt(numElements.get(i).html());
					userBean.setNewPeople(newPeople + "");
				}
				if (i == 2) {

					int totalPeople = Integer.parseInt(numElements.get(i)
							.html());
					userBean.setTotalPeople(totalPeople + "");
				}
			}
		}

		Elements avataElements = document.getElementsByClass("avatar");

		for (int i = 0; i < avataElements.size(); i++) {
			String fakeId = getProfileFakeId(avataElements.get(i).attr("src"));
			if (!fakeId.equals("")) {
				userBean.setFakeId(fakeId);

			}

		}

		Elements nickNameElements = document.getElementsByClass("nickname");

		for (int i = 0; i < nickNameElements.size(); i++) {
			String nickNameString = nickNameElements.get(i).html();

			if (nickNameString != "") {

				userBean.setNickname(nickNameString);

				return GET_USER_PROFILE_SUCCESS;
			}
		}

		return GET_USER_PROFILE_FAILED;
	}

	public interface ParseMassDataCallBack {
		public void onBack(UserBean userBean);
	}

	public static void parseMassData(final String source,
			final UserBean userBean,
			final ParseMassDataCallBack parseMassDataCallBack) {

		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				UserBean getBean = (UserBean) msg.obj;
				parseMassDataCallBack.onBack(getBean);

			}
		};

		new Thread() {
			public void run() {

				getUserType();
				getMassLeft();

			}

			private void getUserType() {

				Document document = Jsoup.parse(source);
				Elements typeElements = document
						.getElementsByClass("mass_send_tips");
				if (typeElements.size() > 0) {

					String userType = typeElements.get(0).html();
					if (userType.contains(today)) {
						userBean.setUserType(UserBean.USER_TYPE_SUBSTRICTION);
					} else {
						userBean.setUserType(UserBean.USER_TYPE_SERVICE);

					}
				}
			}

			private void getMassLeft() {

				String result = "";
				Pattern pattern = Pattern
						.compile("can_verify_apply\\s\\?\\s\\'(\\d*)\\'\\*");

				Matcher matcher = pattern.matcher(source);
				while (matcher.find()) {
					result = matcher.group(1);
					userBean.setMassLeft(Integer.parseInt(result));
					Message message = new Message();
					message.obj = userBean;
					loadHandler.sendMessage(message);

				}
			}

		}.start();

	}

	private static String getProfileFakeId(String source) {

		String result = "";
		Pattern pattern = Pattern.compile("fakeid=(\\d*)");

		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			return matcher.group(1);
		}

		return result;
	}

	public static class MessageResultHolder {
		public MessageHolder messageHolder;
		public ArrayList<MessageBean> messageBeans;
		public String lastMsgId = "";

	}

	public interface MessageListParseCallBack {
		public void onBack(MessageResultHolder messageResultHolder,boolean dataChanged);
	}

	public static void parseNewMessage(
			final MessageListParseCallBack messageListParseCallBack,
			final String source, final UserBean userBean,
			final MessageHolder messageHolder, final String referer) {

		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				MessageResultHolder messageResultHolder = (MessageResultHolder) msg.obj;
				boolean dataChanged = false;
				if(msg.arg1==1){
					dataChanged = true;
				}
				messageListParseCallBack.onBack(messageResultHolder,dataChanged);

			}
		};

		new Thread() {
			public void run() {
				Document document = Jsoup.parse(source);
				Elements scriptElements = document.getElementsByTag("script");
				for (Element nowElement : scriptElements) {
					if (nowElement.html().contains("wx.cgiData ")) {
						JSONArray getArray = getMessageArray(nowElement.html());
						ArrayList<MessageBean> getMessageList = getMessageItems(
								getArray, userBean, referer);
						String latestMsgId = getLatestMsgId(nowElement.html());
						boolean dataChanged = false;
						if (listChanged(messageHolder.getMessageList(),
								getMessageList)) {
							// when the message is list changed
							dataChanged = true;

							messageHolder.setMessage(getMessageList);
							messageHolder.setLatestMsgId(latestMsgId);
						}

						Message message = new Message();
						MessageResultHolder messageResultHolder = new MessageResultHolder();
						messageResultHolder.lastMsgId = latestMsgId;
						messageResultHolder.messageHolder = messageHolder;
						messageResultHolder.messageBeans = getMessageList;
						message.obj = messageResultHolder;
						message.arg1 = dataChanged?1:0;

						loadHandler.sendMessage(message);

					}
				}
			}

			private boolean listChanged(ArrayList<MessageBean> oldArrayList,
					ArrayList<MessageBean> nowArrayList) {
				if (oldArrayList.size() == 0 || nowArrayList.size() == 0) {
					return true;
				}
				if (oldArrayList.get(0).getId()
						.equals(nowArrayList.get(0).getId())) {
					return false;
				}

				return true;

			}
		}.start();

	}

	public static void parseNextMessage(
			final MessageListParseCallBack messageListParseCallBack,
			final String source, final UserBean userBean,
			final MessageHolder messageHolder, final String referer) {

		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				MessageResultHolder messageResultHolder = (MessageResultHolder) msg.obj;
				messageListParseCallBack.onBack(messageResultHolder,true);

			}
		};

		new Thread() {
			public void run() {
				Document document = Jsoup.parse(source);
				Elements scriptElements = document.getElementsByTag("script");
				for (Element nowElement : scriptElements) {
					if (nowElement.html().contains("wx.cgiData ")) {
						JSONArray getArray = getMessageArray(nowElement.html());
						ArrayList<MessageBean> getMessageList = getMessageItems(
								getArray, userBean, referer);
						String latestMsgId = getLatestMsgId(nowElement.html());
						messageHolder.addMessage(getMessageList);
						messageHolder.setLatestMsgId(latestMsgId);

						Message message = new Message();
						MessageResultHolder messageResultHolder = new MessageResultHolder();
						messageResultHolder.lastMsgId = latestMsgId;
						messageResultHolder.messageHolder = messageHolder;
						messageResultHolder.messageBeans = getMessageList;
						message.obj = messageResultHolder;

						loadHandler.sendMessage(message);

					}
				}
			}
		}.start();

	}

	public static int parseLogin(UserBean nowBean, String strResult,
			String slaveSid, String slaveUser, Context context) {

		try {

			JSONObject resultJsonObject = new JSONObject(strResult);
			int ret = (Integer) resultJsonObject.get("Ret");
			if (resultJsonObject.get("Ret") != null) {
				if (ret != 302) {
					// progressDialog.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context).setTitle("登录失败，请检查账户名和密码")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated
											// method stub

										}
									});
					builder.show();

					return LOGIN_FAILED;

				}
			}

			int index = strResult.indexOf("token");
			if (index != -1) {
				String tokenString = getToken(resultJsonObject);
				nowBean.setToken(tokenString);
				SharedPreferenceManager.updateUser(context, nowBean);
			}
		} catch (Exception exception) {
			Log.e("login exception", exception + "");
			return LOGIN_FAILED;

		}
		return LOGIN_SUCCESS;

	}

	private static String getToken(JSONObject resultJsonObject) {
		String tokenString = "";
		try {
			String contentString = resultJsonObject.getString("ErrMsg");
			tokenString = contentString.substring(
					contentString.indexOf("token") + 6, contentString.length());
		} catch (Exception e) {

		}

		return tokenString;
	}

	private static JSONArray getMessageArray(String source) {
		try {
			String content = source.substring(source.indexOf("{\"msg_item\":"),
					source.indexOf(").msg_item"));
			JSONObject nowJsonObject = new JSONObject(content);
			return nowJsonObject.getJSONArray("msg_item");

		} catch (Exception exception) {

		}

		return null;

	}

	private static String getLatestMsgId(String source) {
		String result = "";
		Pattern pattern = Pattern.compile("latest_msg_id\\s:\\s'(\\d*)',");

		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			return matcher.group(1);
		}

		return result;
	}

	private static ArrayList<MessageBean> getMessageItems(JSONArray jsonArray,
			UserBean userBean, String referer) {
		ArrayList<MessageBean> messageBeans = new ArrayList<MessageBean>();
		Gson gson = new Gson();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject nowJsonObject = jsonArray.getJSONObject(i);
				nowJsonObject.put("token", userBean.getToken());
				nowJsonObject.put("slave_sid", userBean.getSlaveSid());
				nowJsonObject.put("slave_user", userBean.getSlaveUser());
				nowJsonObject.put("referer", referer);

				MessageBean nowItem = (MessageBean) gson.fromJson(
						nowJsonObject.toString(), MessageBean.class);

				messageBeans.add(nowItem);
			} catch (Exception exception) {
				Log.e("parse errror", exception + "");
			}
		}

		return messageBeans;
	}

	public interface FansListParseCallback {
		public void onBack(FansHolder fansHolder,boolean dataChanged);
	}

	public static void parseFansList(final String source, final String referer,
			final FansHolder fansHolder, final UserBean userBean,
			final boolean refresh,
			final FansListParseCallback fansListParseCallback) {

		final Handler loadHandler = new Handler() {

			// 子类必须重写此方法,接受数据
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				super.handleMessage(msg);
				// 此处可以更新UI
				boolean dataChanged = false;
				if(msg.arg1==1){
					dataChanged = true;
				}
				fansListParseCallback.onBack(fansHolder,dataChanged);

			}
		};

		new Thread() {
			public void run() {

				String contentBodyString = source.substring(
						source.indexOf("wx.cgiData={"),
						source.indexOf("seajs.use(\"user/index\")"));
				String fansTypeString = contentBodyString.substring(
						contentBodyString.indexOf("\"groups\":[") + 9,
						contentBodyString.indexOf("}).groups"));
				String fansContentString = contentBodyString.substring(
						contentBodyString.indexOf("\"contacts\":") + 11,
						contentBodyString.indexOf("}).contacts"));
				try {

					Gson gson = new Gson();
					JSONArray fansTypeArray = new JSONArray(fansTypeString);
					ArrayList<FansGroupBean> fansGroupBeans = new ArrayList<FansGroupBean>();
					for (int i = 0; i < fansTypeArray.length(); i++) {
						JSONObject nowJsonObject = fansTypeArray
								.getJSONObject(i);
						FansGroupBean nowGroupBean = (FansGroupBean) gson
								.fromJson(nowJsonObject.toString(),
										FansGroupBean.class);
						fansGroupBeans.add(nowGroupBean);
					}
					fansHolder.setFansGroup(fansGroupBeans);

					JSONArray fansArray = new JSONArray(fansContentString);

					ArrayList<FansBean> fansBeans = new ArrayList<FansBean>();
					for (int i = 0; i < fansArray.length(); i++) {
						JSONObject nowJsonObject = fansArray.getJSONObject(i);
						FansBean nowFansBean = (FansBean) gson.fromJson(
								nowJsonObject.toString(), FansBean.class);
						nowFansBean.setReferer(referer);
						fansBeans.add(nowFansBean);
					}

					boolean dataChanged = false;
					if (refresh) {
						if (listChange(fansHolder.getFansBeans(), fansBeans)) {
							// when the fans list changed

							dataChanged = true;
							fansHolder.setFans(fansBeans);
						}

					} else {
						dataChanged = true;
						fansHolder.addFans(fansBeans);
					}

					Message nowMessage = new Message();
					nowMessage.arg1 = dataChanged?1:0;
					
					loadHandler.sendMessage(nowMessage);

				} catch (Exception exception) {
					Log.e("fans parse errror", "" + exception);
				}

			}

			private boolean listChange(ArrayList<FansBean> oldArrayList,
					ArrayList<FansBean> nowArrayList) {
				if (oldArrayList.size() == 0 || nowArrayList.size() == 0) {
					return true;
				}
				if (oldArrayList.get(0).getFansId()
						.equals(nowArrayList.get(0).getFansId())) {
					return false;
				}

				return true;
			}
		}.start();

	}

}

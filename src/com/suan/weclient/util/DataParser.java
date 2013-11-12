package com.suan.weclient.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
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

public class DataParser {

	public static final int LOGIN_SUCCESS = 1;
	public static final int LOGIN_FAILED = 0;
	public static final int GET_MESSAGE_SUCCESS = 1;
	public static final int GET_MESSAGE_FAILED = 0;

	public static final int GET_USER_PROFILE_SUCCESS = 1;
	public static final int GET_USER_PROFILE_FAILED = 0;

	public static final int GET_MASS_DATA_SUCCESS = 1;

	public static final int GET_MASS_DATA_FAILED = 0;

	public static int getUserProfile(String source, UserBean userBean) {

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
	
	public interface ParseMassDataCallBack{
		public void onBack(UserBean userBean);
	}

	public static void getMassData(final String source,final UserBean userBean,final ParseMassDataCallBack parseMassDataCallBack) {

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
				String result = "";
				Pattern pattern = Pattern
						.compile("can_verify_apply\\s\\?\\s\\'(\\d*)\\'\\*");

				Matcher matcher = pattern.matcher(source);
				while (matcher.find()) {
					result = matcher.group(1);
					userBean.setMassLeft(Integer.parseInt(result));
					Message message = new Message();
					message.obj =userBean;

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
		public ArrayList<MessageItem> messageItems;
		public String lastMsgId = "";

	}

	public interface MessageListParseCallBack {
		public void onBack(MessageResultHolder messageResultHolder);
	}

	public static void getNewMessage(
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
				messageListParseCallBack.onBack(messageResultHolder);

			}
		};

		new Thread() {
			public void run() {
				Document document = Jsoup.parse(source);
				Elements scriptElements = document.getElementsByTag("script");
				for (Element nowElement : scriptElements) {
					if (nowElement.html().contains("wx.cgiData ")) {
						JSONArray getArray = getMessageArray(nowElement.html());
						ArrayList<MessageItem> getMessageList = getMessageItems(
								getArray, userBean, referer);
						String latestMsgId = getLatestMsgId(nowElement.html());

						messageHolder.setMessage(getMessageList);
						messageHolder.setLatestMsgId(latestMsgId);

						Message message = new Message();
						MessageResultHolder messageResultHolder = new MessageResultHolder();
						messageResultHolder.lastMsgId = latestMsgId;
						messageResultHolder.messageHolder = messageHolder;
						messageResultHolder.messageItems = getMessageList;
						message.obj = messageResultHolder;

						loadHandler.sendMessage(message);

					}
				}
			}
		}.start();

	}

	public static void getNextMessage(
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
				messageListParseCallBack.onBack(messageResultHolder);

			}
		};

		new Thread() {
			public void run() {
				Document document = Jsoup.parse(source);
				Elements scriptElements = document.getElementsByTag("script");
				for (Element nowElement : scriptElements) {
					if (nowElement.html().contains("wx.cgiData ")) {
						Log.e("next parse", "find wx.cgiData");
						Log.e("next parse", "content"+nowElement.html());
						JSONArray getArray = getMessageArray(nowElement.html());
						ArrayList<MessageItem> getMessageList = getMessageItems(
								getArray, userBean, referer);
						String latestMsgId = getLatestMsgId(nowElement.html());
						messageHolder.addMessage(getMessageList);
						messageHolder.setLatestMsgId(latestMsgId);

						Message message = new Message();
						MessageResultHolder messageResultHolder = new MessageResultHolder();
						messageResultHolder.lastMsgId = latestMsgId;
						messageResultHolder.messageHolder = messageHolder;
						messageResultHolder.messageItems = getMessageList;
						Log.e("next parse", "parse end"+getMessageList.size()+"|"+messageHolder.getMessageList().size());
						message.obj = messageResultHolder;

						loadHandler.sendMessage(message);

					}
				}
			}
		}.start();

	}

	public static int analyseLogin(UserBean nowBean, HttpResponse response,
			Context context) {

		try {
			int hitAmount = 0;

			String strResult = EntityUtils.toString(response.getEntity());

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
				hitAmount++;
			}
			for (int i = 0; i < response.getAllHeaders().length; i++) {
				if (response.getAllHeaders()[i].getName()
						.contains("Set-Cookie")) {
					String nowCookie = response.getAllHeaders()[i].getValue();
					if (nowCookie.contains("slave_user")) {
						String slaveUser = nowCookie.substring(
								nowCookie.indexOf("slave_user") + 11,
								nowCookie.indexOf(";"));
						nowBean.setSlaveUser(slaveUser);
						hitAmount++;
					}
					if (nowCookie.contains("slave_sid")) {

						String slaveSid = nowCookie.substring(
								nowCookie.indexOf("slave_sid") + 10,
								nowCookie.indexOf(";"));
						nowBean.setSlaveSid(slaveSid);
						hitAmount++;
					}

				}

			}
			if (hitAmount == 3) {
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

	private static boolean filter = false;

	private static ArrayList<MessageItem> getMessageItems(JSONArray jsonArray,
			UserBean userBean, String referer) {
		ArrayList<MessageItem> messageItems = new ArrayList<MessageItem>();
		Gson gson = new Gson();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject nowJsonObject = jsonArray.getJSONObject(i);
				nowJsonObject.put("token", userBean.getToken());
				nowJsonObject.put("slave_sid", userBean.getSlaveSid());
				nowJsonObject.put("slave_user", userBean.getSlaveUser());
				nowJsonObject.put("referer", referer);

				MessageItem nowItem = (MessageItem) gson.fromJson(
						nowJsonObject.toString(), MessageItem.class);
				if (filter) {
					if (nowItem.getType() == MessageItem.MESSAGE_TYPE_TEXT||nowItem.getType() == MessageItem.MESSAGE_TYPE_IMG) {

						messageItems.add(nowItem);
					}
				} else {

					messageItems.add(nowItem);
				}
			} catch (Exception exception) {
				Log.e("parse errror", exception + "");
			}
		}

		return messageItems;
	}
}

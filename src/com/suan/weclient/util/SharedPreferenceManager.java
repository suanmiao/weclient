package com.suan.weclient.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Debug;
import android.util.Log;

public class SharedPreferenceManager {

	private static final String USER_GROUP_SHAREDPREF = "userGroup";

	private static final String USER_GROUP_CONTENT = "content";


	public static final int ENTER_STATE_FIRST_TIME = -1;
	public static final int ENTER_STATE_OTHER_TIME = 1;
	

//	public static int getEnterState(Context context) {
//		SharedPreferences sharedPreferences = context.getSharedPreferences(
//				ENTER_STATE_SHAREDPREF, 0);
//		return sharedPreferences.getInt(ENTER_STATE_CONTENT, -1);
//	}
//
//	public static boolean putEnterState(Context context, int state) {
//		SharedPreferences sharedPreferences = context.getSharedPreferences(
//				ENTER_STATE_SHAREDPREF, 0);
//		Editor editor = sharedPreferences.edit();
//		editor.putInt(ENTER_STATE_CONTENT, state);
//
//		return editor.commit();
//	}

	private static String getUserGroupString(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				USER_GROUP_SHAREDPREF, 0);
		return sharedPreferences.getString(USER_GROUP_CONTENT, "");
	}

	private static boolean putUserGroupString(Context context, String content) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				USER_GROUP_SHAREDPREF, 0);
		Editor editor = sharedPreferences.edit();
		editor.putString(USER_GROUP_CONTENT, content);

		return editor.commit();
	}

	public static ArrayList<UserBean> getUserGroup(Context context) {
		ArrayList<UserBean> userGroupArrayList = new ArrayList<UserBean>();
		String userContentString = getUserGroupString(context);
		if (userContentString != "") {
			try {

				JSONArray userArray = new JSONArray(userContentString);
				for (int i = 0; i < userArray.length(); i++) {
					JSONObject nowJsonObject = userArray.getJSONObject(i);
					UserBean nowUserBean = new UserBean(nowJsonObject);
					userGroupArrayList.add(nowUserBean);
				}
			} catch (Exception exception) {

			}

		}

		return userGroupArrayList;
	}

	public static void insertUser(Context context, UserBean userBean) {
		ArrayList<UserBean> userGroupArrayList = getUserGroup(context);
		userGroupArrayList.add(userBean);

		JSONArray contentArray = new JSONArray();

		for (int i = 0; i < userGroupArrayList.size(); i++) {
			contentArray.put(userGroupArrayList.get(i).getContentObject());
		}

		putUserGroupString(context, contentArray.toString());

	}
	
	public static boolean containUser(Context context,String userName ){
		
		ArrayList<UserBean> userGroupArrayList = getUserGroup(context);
		for(int i = 0;i<userGroupArrayList.size();i++){
			String nowUserName = userGroupArrayList.get(i).getUserName();
			if(nowUserName.equals(userName)){
				return true;
			}
		}
		
		return false;
	}

	public static void updateUser(Context context, UserBean userBean) {

		ArrayList<UserBean> userGroupArrayList = getUserGroup(context);
		for (int i = 0; i < userGroupArrayList.size(); i++) {
			if (userGroupArrayList.get(i).getUserName()
					.equals(userBean.getUserName())) {
				userGroupArrayList.set(i, userBean);
			}
		}

		JSONArray contentArray = new JSONArray();

		for (int i = 0; i < userGroupArrayList.size(); i++) {
			contentArray.put(userGroupArrayList.get(i).getContentObject());
		}

		putUserGroupString(context, contentArray.toString());

	}

	public static void deleteUser(Context context, String userName) {

		ArrayList<UserBean> userGroupArrayList = getUserGroup(context);
		for (int i = 0; i < userGroupArrayList.size(); i++) {
			if (userGroupArrayList.get(i).getUserName().equals(userName)) {
				userGroupArrayList.remove(i);
			}
		}

		JSONArray contentArray = new JSONArray();

		for (int i = 0; i < userGroupArrayList.size(); i++) {
			contentArray.put(userGroupArrayList.get(i).getContentObject());
		}

		putUserGroupString(context, contentArray.toString());

	}

}

package com.suan.weclient.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.suan.weclient.pushService.PushService;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.UserBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SharedPreferenceManager {

    private static final String USER_GROUP_SHAREDPREF = "userGroup";
    private static final String USER_GROUP_CONTENT = "content";
    private static final String USER_GROUP_CURRENT_INDEX = "currentIndex";
    private static final String USER_HIDE_KEY_WORD_MESSAGE = "hideKeyWordMessage";

    private static final String PUSH_STATE_SHAREDPREF = "activityState";
    private static final String ACIVITY_RUNNING = "running";
    private static final String PUSH_ENABLE = "pushEnable";
    private static final String PUSH_FIRST_BLOOD = "pushFirstUse";
    private static final String PUSH_FREQUENT = "pushFrequent";
    private static final String PUSH_CLOSE_NIGHT = "pushCloseNight";

    private static final String PUSH_NOTIFY_WHOLE_GROUP = "pushNotifyWholeGroup";
    private static final String PUSH_USER_GROUP = "pushUserGroup";





    public static final int ENTER_STATE_FIRST_TIME = -1;
    public static final int ENTER_STATE_OTHER_TIME = 1;

    public static boolean getActivityRunning(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(ACIVITY_RUNNING, false);
    }

    public static boolean putActivityRunning(Context context, boolean running) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(ACIVITY_RUNNING, running);

        return editor.commit();
    }

    public static boolean getPushEnable(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(PUSH_ENABLE, false);
    }

    public static boolean putPushEnable(Context context, boolean running) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(PUSH_ENABLE, running);

        return editor.commit();
    }



    public static boolean getPushFirstUse(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(PUSH_FIRST_BLOOD, true);
    }

    public static boolean putPushFirstUse(Context context, boolean running) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(PUSH_FIRST_BLOOD, running);

        return editor.commit();
    }


    public static boolean getPushCloseNight(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(PUSH_CLOSE_NIGHT, true);
    }

    public static boolean putPustCloseNight(Context context, boolean close) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(PUSH_CLOSE_NIGHT, close);

        return editor.commit();
    }





    public static int getPushFrequent(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getInt(PUSH_FREQUENT, PushService.PUSH_FREQUENT_NORMAL);
    }

    public static boolean putPushFrequent(Context context, int pushFrequent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putInt(PUSH_FREQUENT, pushFrequent);

        return editor.commit();
    }



    public static boolean getPushNotifyWholeGroup(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(PUSH_NOTIFY_WHOLE_GROUP, true);
    }

    public static boolean putPushNotifyWholeGroup(Context context, boolean close) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(PUSH_NOTIFY_WHOLE_GROUP, close);

        return editor.commit();
    }


    public static String getPushUserGroup(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(PUSH_USER_GROUP, "");
    }

    public static boolean putPushUserGroup(Context context, String pushUserGroup) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PUSH_STATE_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putString(PUSH_USER_GROUP, pushUserGroup);

        return editor.commit();
    }



    public static boolean putHideKeyWordMessage(Context context, boolean hideKeyWordMessage) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_GROUP_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_HIDE_KEY_WORD_MESSAGE, hideKeyWordMessage);

        return editor.commit();
    }

    public static boolean getHideKeyWordMessage(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_GROUP_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(USER_HIDE_KEY_WORD_MESSAGE, true);
    }

    public static boolean putCurrentIndex(Context context, int currentIndex) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_GROUP_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putInt(USER_GROUP_CURRENT_INDEX, currentIndex);

        return editor.commit();
    }

    public static int getCurentIndex(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_GROUP_SHAREDPREF, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getInt(USER_GROUP_CURRENT_INDEX, 0);
    }
    private static String getUserGroupString(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_GROUP_SHAREDPREF, context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(USER_GROUP_CONTENT, "");
    }

    private static boolean putUserGroupString(Context context, String content) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                USER_GROUP_SHAREDPREF, context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putString(USER_GROUP_CONTENT, content);

        boolean editResult =  editor.commit();
        return editResult;
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

    public static boolean containUser(Context context, String userName) {

        ArrayList<UserBean> userGroupArrayList = getUserGroup(context);
        for (int i = 0; i < userGroupArrayList.size(); i++) {
            String nowUserName = userGroupArrayList.get(i).getUserName();
            if (nowUserName.equals(userName)) {
                return true;
            }
        }

        return false;
    }

    public static void updateUser(Context context,DataManager dataManager) {

        JSONArray contentArray = new JSONArray();

        for (int i = 0; i < dataManager.getUserGroup().size(); i++) {
            contentArray.put(dataManager.getUserGroup().get(i).getContentObject());
            if(i==dataManager.getCurrentPosition()){

            }

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

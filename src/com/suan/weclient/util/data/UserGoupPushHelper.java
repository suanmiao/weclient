package com.suan.weclient.util.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

/**
 * Created by lhk on 1/29/14.
 */
public class UserGoupPushHelper {

    private int currentIndex;
    private ArrayList<PushUserHolder> userHolders;

    public UserGoupPushHelper(String source) {

        init(source);

    }

    private void init(String source) {
        if (source.length() != 0) {
            try {

                JSONObject contentObject = new JSONObject(source);
                currentIndex = contentObject.getInt("currentIndex");

                userHolders = new ArrayList<PushUserHolder>();
                JSONArray contentArray = contentObject.getJSONArray("contentArray");
                for (int i = 0; i < contentArray.length(); i++) {
                    JSONObject nowObject = contentArray.getJSONObject(i);
                    PushUserHolder nowHolder = new PushUserHolder(nowObject);
                    userHolders.add(nowHolder);
                }

                return;

            } catch (Exception e) {
                Log.e("init error", "" + e);
            }

        } else {

            userHolders = new ArrayList<PushUserHolder>();
            currentIndex = 0;
        }

    }

    public String getString() {
        try {
            JSONObject contentObject = new JSONObject();
            contentObject.put("currentIndex", currentIndex);
            JSONArray contentArray = new JSONArray();
            for (int i = 0; i < userHolders.size(); i++) {
                JSONObject nowObject = userHolders.get(i).getContentObject();
                contentArray.put(nowObject);
            }
            contentObject.put("contentArray", contentArray);
            return contentObject.toString();

        } catch (Exception e) {
            Log.e("get contentObject error", "" + e);

        }


        return null;
    }

    public void updateUserGroup(DataManager dataManager) {

        ArrayList<PushUserHolder> newUserHolders = new ArrayList<PushUserHolder>();

        for (int i = 0; i < dataManager.getUserGroup().size(); i++) {
            PushUserHolder nowBornHolder = new PushUserHolder(dataManager.getUserGroup().get(i));
            for (int j = 0; j < userHolders.size(); j++) {
                PushUserHolder nowSearchHolder = userHolders.get(j);
                //find the same old holder and set data to new holder
                if (nowBornHolder.getUserBean().getUserName().equals(nowSearchHolder.getUserBean().getUserName())) {
                    nowBornHolder.setLastMsgId(nowSearchHolder.getLastMsgId());
                    nowBornHolder.setLastNotifyTime(nowSearchHolder.getLastNotifyTime());
                    nowBornHolder.setLastNewMessageCount(nowSearchHolder.getLastNewMessageCount());

                }

            }
            newUserHolders.add(nowBornHolder);

        }

        userHolders = newUserHolders;


        currentIndex = dataManager.getCurrentPosition();

    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public ArrayList<PushUserHolder> getUserHolders() {
        return userHolders;
    }


    public class PushUserHolder {
        private UserBean userBean;
        private String lastMsgId = "";
        private int lastNewMessageCount = 0;
        private long lastNotifyTime = 0;

        public PushUserHolder(UserBean userBean) {
            this.userBean = userBean;
        }

        public PushUserHolder(JSONObject jsonObject) {
            try {

                this.userBean = new UserBean(jsonObject.getJSONObject("userBean"));
                this.lastMsgId = jsonObject.getString("lastMsgId");
                this.lastNewMessageCount = jsonObject.getInt("lastNewMessageCount");
                this.lastNotifyTime = jsonObject.getLong("lastNotifyTime");


            } catch (Exception e) {
                Log.e("user holder error", "" + e);
            }

        }

        public JSONObject getContentObject() {
            try {
                JSONObject contentObject = new JSONObject();
                JSONObject userObject = userBean.getContentObject();
                contentObject.put("userBean", userObject);
                contentObject.put("lastMsgId", lastMsgId);
                contentObject.put("lastNewMessageCount", lastNewMessageCount);
                contentObject.put("lastNotifyTime", lastNotifyTime);
                return contentObject;

            } catch (Exception e) {

            }

            return null;

        }

        public String getLastMsgId() {
            return lastMsgId;
        }

        public void setLastMsgId(String lastMsgId) {
            this.lastMsgId = lastMsgId;
        }

        public int getLastNewMessageCount() {
            return lastNewMessageCount;
        }

        public void setLastNewMessageCount(int lastNewMessageCount) {
            this.lastNewMessageCount = lastNewMessageCount;
        }

        public long getLastNotifyTime() {
            return lastNotifyTime;
        }

        public void setLastNotifyTime(long lastNotifyTime) {
            this.lastNotifyTime = lastNotifyTime;
        }

        public UserBean getUserBean() {
            return this.userBean;
        }


    }


}

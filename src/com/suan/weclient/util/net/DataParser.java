package com.suan.weclient.util.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.data.ChatHolder;
import com.suan.weclient.util.data.FansBean;
import com.suan.weclient.util.data.FansGroupBean;
import com.suan.weclient.util.data.FansHolder;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.data.MessageHolder;
import com.suan.weclient.util.data.UserBean;

public class DataParser {

    public static final int PARSE_LOGIN_SUCCESS = 1;
    public static final int PARSE_LOGIN_FAILED = 0;
    public static final int RET_LOGIN_SUCCESS = 302;
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
        public void onBack(MessageResultHolder messageResultHolder,
                           boolean dataChanged);
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
                if (msg.arg1 == 1) {
                    dataChanged = true;
                }
                messageListParseCallBack.onBack(messageResultHolder,
                        dataChanged);

            }
        };

        new Thread() {
            public void run() {
                Document document = Jsoup.parse(source);
                Elements scriptElements = document.getElementsByTag("script");
                for (Element nowElement : scriptElements) {

                    if (nowElement.html().contains("wx.cgiData ")) {
                        JSONObject contentObject = getMessageArray(nowElement.html());
                        if (contentObject != null) {
                            try {
                                removeEmptyMessage(messageHolder.getMessageList());
                                JSONArray getArray = contentObject.getJSONArray("messageArray");
                                ArrayList<MessageBean> getMessageList = getMessageItems(
                                        getArray, userBean, referer);
                                String latestMsgId = contentObject.get("lastMsgId").toString();

                                if (!(messageHolder.getLatestMsgId().equals(latestMsgId) && messageHolder.getContentMessageMode() == messageHolder.getNowMessageMode())) {
                                    // when the message is list changed
                                    messageHolder.setMessage(getMessageList);
                                    messageHolder.setLatestMsgId(latestMsgId);
                                    messageHolder.getUserBean().setLastMsgId(latestMsgId);
                                    messageHolder.setContentMessageMode(messageHolder.getNowMessageMode());
                                } else {

                                }


                                if (getMessageList.size() == 0) {
                                    MessageBean emptyMessage = new MessageBean();
                                    emptyMessage.setType(MessageBean.MESSAGE_TYPE_EMPTY);
                                    messageHolder.getMessageList().add(emptyMessage);

                                }

                                Message message = new Message();
                                MessageResultHolder messageResultHolder = new MessageResultHolder();
                                messageResultHolder.lastMsgId = latestMsgId;
                                messageResultHolder.messageHolder = messageHolder;
                                messageResultHolder.messageBeans = getMessageList;
                                message.obj = messageResultHolder;

                                loadHandler.sendMessage(message);

                            } catch (Exception e) {

                            }

                        }

                    }

                }

            }


            private void removeEmptyMessage(ArrayList<MessageBean> messageBeans) {
                for (int i = 0; i < messageBeans.size(); i++) {
                    if (messageBeans.get(i).getType() == MessageBean.MESSAGE_TYPE_EMPTY) {
                        messageBeans.remove(i);
                    }

                }

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
                messageListParseCallBack.onBack(messageResultHolder, true);

            }
        };

        new Thread() {
            public void run() {
                Document document = Jsoup.parse(source);
                Elements scriptElements = document.getElementsByTag("script");
                for (Element nowElement : scriptElements) {
                    if (nowElement.html().contains("wx.cgiData ")) {
                        JSONObject contentObject = getMessageArray(nowElement.html());
                        if (contentObject != null) {
                            try {
                                JSONArray getArray = contentObject.getJSONArray("messageArray");
                                ArrayList<MessageBean> getMessageList = getMessageItems(
                                        getArray, userBean, referer);
                                String latestMsgId = contentObject.get("lastMsgId").toString();
                                messageHolder.addMessage(getMessageList);
                                messageHolder.setLatestMsgId(latestMsgId);

                                Message message = new Message();
                                MessageResultHolder messageResultHolder = new MessageResultHolder();
                                messageResultHolder.lastMsgId = latestMsgId;
                                messageResultHolder.messageHolder = messageHolder;
                                messageResultHolder.messageBeans = getMessageList;
                                message.obj = messageResultHolder;

                                loadHandler.sendMessage(message);

                            } catch (Exception e) {

                            }

                        }

                    }
                }
            }
        }.start();

    }

    public static int parseLogin(UserBean nowBean, String strResult,
                                 String slaveSid, String slaveUser, Context context) {

        JSONObject resultJsonObject = null;

        try {

            resultJsonObject = new JSONObject(strResult);
            int ret = getRet(resultJsonObject);
/*
            Toast.makeText(context, "" + ret, Toast.LENGTH_LONG).show();
            Toast.makeText(context, "" + ret, Toast.LENGTH_LONG).show();
            Toast.makeText(context, "" + ret, Toast.LENGTH_LONG).show();


            try {
                File file = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "weclient_test.txt");
                file.createNewFile();
                //write the bytes in file
                if (file.exists()) {
                    OutputStream fo = new FileOutputStream(file);

                    fo.write(strResult.getBytes());
                    fo.close();
                }

            } catch (Exception e) {

            }

*/

            if (ret != RET_LOGIN_SUCCESS) {
                Log.e("login failed", strResult);

                return PARSE_LOGIN_FAILED;

            }

            if (strResult.contains("token")) {
                String tokenString = getToken(resultJsonObject);
                nowBean.setToken(tokenString);
            }
        } catch (Exception exception) {
            Log.e("login exception fuck", exception + "");
            return PARSE_LOGIN_FAILED;

        }
        return PARSE_LOGIN_SUCCESS;

    }

    private static String getToken(JSONObject resultJsonObject) {
        String tokenString = "";
        try {
            String contentString = resultJsonObject.getString("ErrMsg");
            String regex = "token=(\\d*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(contentString);
            while (matcher.find()) {
                String getToken = matcher.group(1);
                if (getToken != null) {
                    tokenString = getToken;
                }
            }
        } catch (Exception e) {

        }

        return tokenString;
    }

    private static JSONObject getMessageArray(String source) {
        try {
            String regx = "latest_msg_id\\s:\\s'(\\d*)',[^\\(]*\\(\\{\"msg_item\":(\\[[^\\]]*\\])";

            Pattern pattern = Pattern.compile(regx);
            Matcher matcher = pattern.matcher(source);
            while (matcher.find()) {
                String lastMsgId = matcher.group(1);
                String messageArray = matcher.group(2);
                if (lastMsgId != null && messageArray != null) {
                    JSONObject contentObject = new JSONObject();
                    contentObject.put("lastMsgId", lastMsgId);
                    contentObject.put("messageArray", new JSONArray(messageArray));


                    return contentObject;
                }

            }


        } catch (Exception exception) {

        }

        return null;

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
        public void onBack(FansHolder fansHolder, boolean dataChanged);
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
                if (msg.arg1 == 1) {
                    dataChanged = true;
                }
                fansListParseCallback.onBack(fansHolder, dataChanged);

            }
        };

        new Thread() {

            public void run() {


                JSONObject fansContentObject = getFansContentObject(source);
                if (fansContentObject != null) {

                    try {

                        String fansTypeString = fansContentObject.get("fansType").toString();
                        String fansContentString = fansContentObject.get("fansContent").toString();
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

                            dataChanged = true;
                            fansHolder.setFans(fansBeans);

                        } else {
                            dataChanged = true;
                            fansHolder.addFans(fansBeans);
                        }

                        Message nowMessage = new Message();
                        nowMessage.arg1 = dataChanged ? 1 : 0;

                        loadHandler.sendMessage(nowMessage);

                    } catch (Exception exception) {
                        Log.e("fans parse errror", "" + exception);
                    }

                }


            }

            private JSONObject getFansContentObject(String source) {

                String regex = "groupsList\\s*:\\s*\\(\\{\"groups\":(\\[[^\\]]*\\])[^\\[]*(\\[[^\\]]*])";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(source);
                while (matcher.find()) {
                    String fansType = matcher.group(1);
                    String fansContent = matcher.group(2);
                    JSONObject fansContentObject = new JSONObject();
                    try {
                        fansContentObject.put("fansType", fansType);
                        fansContentObject.put("fansContent", fansContent);

                        return fansContentObject;

                    } catch (Exception e) {

                    }


                }


                return null;
            }


        }.start();

    }

    public interface ChatListParseCallback {
        public void onBack(ChatHolder chatHolder, boolean dataChanged);
    }

    public static void parseChatList(final String source,
                                     final ChatHolder chatHolder,
                                     final ChatListParseCallback chatListParseCallback) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                boolean dataChanged = false;
                if (msg.arg1 == 1) {
                    dataChanged = true;
                }
                chatListParseCallback.onBack(chatHolder, dataChanged);

            }
        };

        new Thread() {

            public void run() {

                JSONObject chatContentObject = getChatContentObject(source);
                if (chatContentObject != null) {

                    try {

                        String messageContent = chatContentObject.get("messageContent").toString();
                        Gson gson = new Gson();
                        JSONArray messageArray = new JSONArray(messageContent);
                        ArrayList<MessageBean> messageBeans = new ArrayList<MessageBean>();
                        for (int i = 0; i < messageArray.length(); i++) {
                            JSONObject nowJsonObject = messageArray
                                    .getJSONObject(i);

                            MessageBean nowMessageBean = (MessageBean) gson
                                    .fromJson(nowJsonObject.toString(),
                                            MessageBean.class);
                            if (nowMessageBean.getFakeId().equals(chatHolder.getToFakeId())) {
                                nowMessageBean.setOwner(MessageBean.MESSAGE_OWNER_HER);
                            } else {
                                nowMessageBean.setOwner(MessageBean.MESSAGE_OWNER_ME);
                            }
                            //reverse
                            messageBeans.add(0, nowMessageBean);
                        }

                        chatHolder.setMessage(messageBeans);


                        Message nowMessage = new Message();
                        nowMessage.arg1 = 1;

                        loadHandler.sendMessage(nowMessage);

                    } catch (Exception exception) {
                        Log.e("chat parse errror", "" + exception);
                    }
                }

            }


            private JSONObject getChatContentObject(String source) {

                String regex = "msg_item\":(\\[[^\\]]*\\])[^\\d]*(\\d*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(source);
                while (matcher.find()) {
                    String messageContent = matcher.group(1);
                    JSONObject chatContentObject = new JSONObject();
                    try {
                        chatContentObject.put("messageContent", messageContent);

                        return chatContentObject;

                    } catch (Exception e) {

                    }


                }


                return null;
            }
        }.start();

    }


    public static int getRet(JSONObject resultObject) {
        try {
            if (resultObject.get("Ret") != null) {
                return Integer.parseInt("" + resultObject.get("Ret"));

            }

        } catch (Exception e) {

        }
        try {

            if (resultObject.get("ret") != null) {
                return Integer.parseInt("" + resultObject.get("ret"));
            }
        } catch (Exception e) {

        }


        return GET_RET_NONE;
    }

    public static final int GET_RET_NONE = -1;
}

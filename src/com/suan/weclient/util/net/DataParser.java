package com.suan.weclient.util.net;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.AppItemBean;
import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.holder.AppItemHolder;
import com.suan.weclient.util.data.holder.ChatHolder;
import com.suan.weclient.util.data.bean.FansBean;
import com.suan.weclient.util.data.bean.FansGroupBean;
import com.suan.weclient.util.data.holder.FansHolder;
import com.suan.weclient.util.data.bean.MessageBean;
import com.suan.weclient.util.data.holder.MaterialHolder;
import com.suan.weclient.util.data.holder.MessageHolder;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.holder.resultHolder.FansResultHolder;
import com.suan.weclient.util.data.holder.resultHolder.MaterialResultHolder;
import com.suan.weclient.util.data.holder.resultHolder.MessageResultHolder;

public class DataParser {

    public static final int PARSE_SUCCESS = 1;
    public static final int PARSE_FAILED = 2;
    public static final int PARSE_SPECIFIC_ERROR = 3;
    public static final int PARSE_SPECIFIC_STUATION = 4;

    private static final String login_timeout = "登录超时";
    private static final String invalid_bizpay = "invalid bizpay url";

    public static final int RET_LOGIN_SUCCESS = 302;
    public static final String LOGIN_NEEDS_VERIFY = "/cgi-bin/readtemplate?t=user/validate_phone_tmpl";

    public static final int GET_USER_PROFILE_SUCCESS = 1;
    public static final int GET_USER_PROFILE_FAILED = 0;


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
        public void onBack(int code, UserBean userBean);
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
                switch (msg.arg1) {
                    case PARSE_SUCCESS:
                        UserBean getBean = (UserBean) msg.obj;
                        parseMassDataCallBack.onBack(msg.arg1, getBean);

                        break;
                    case PARSE_FAILED:

                        parseMassDataCallBack.onBack(msg.arg1, null);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                boolean first = getUserType();
                boolean second = getMassLeft();


                Message message = new Message();
                if (first && second) {
                    message.arg1 = PARSE_SUCCESS;
                    message.obj = userBean;

                } else {

                    if (source.contains(login_timeout)) {
                        message.arg1 = PARSE_SPECIFIC_ERROR;
                    } else {
                        message.arg1 = PARSE_FAILED;
                    }

                }
                loadHandler.sendMessage(message);
            }

            private boolean getUserType() {

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
                    return true;
                }

                return false;
            }

            private boolean getMassLeft() {

                String result = "";
                Pattern pattern = Pattern
                        .compile("can_verify_apply\\s\\?\\s\\'(\\d*)\\'\\*");

                Matcher matcher = pattern.matcher(source);
                while (matcher.find()) {
                    result = matcher.group(1);
                    userBean.setMassLeft(Integer.parseInt(result));
                    return true;

                }

                return false;
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

    public interface MessageListParseCallBack {
        public void onBack(MessageResultHolder messageResultHolder,
                           int code);
    }

    public static void parseNewMessage(
            final MessageListParseCallBack messageListParseCallBack,
            final String source, final UserBean userBean,
            final int messageMode,
            final String referer) {


        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                switch (msg.arg1) {
                    case PARSE_SUCCESS:
                        MessageResultHolder messageResultHolder = (MessageResultHolder) msg.obj;

                        messageListParseCallBack.onBack(messageResultHolder,
                                msg.arg1);

                        break;
                    default:

                        messageListParseCallBack.onBack(null,
                                msg.arg1);

                        break;
                }

            }
        };


        new Thread() {
            public void run() {

                Message message = new Message();
                message.arg1 = PARSE_FAILED;
                Document document = Jsoup.parse(source);
                Elements scriptElements = document.getElementsByTag("script");
                for (Element nowElement : scriptElements) {

                    if (nowElement.html().contains("wx.cgiData ")) {
                        JSONObject contentObject = getMessageArray(nowElement.html());
                        if (contentObject != null) {
                            try {

                                JSONArray getArray = contentObject.getJSONArray("messageArray");
                                ArrayList<MessageBean> resultMessageList = getMessageItems(
                                        getArray, userBean, referer);
                                String latestMsgId = contentObject.get("lastMsgId").toString();


                                if (resultMessageList.size() == 0) {
                                    MessageBean emptyMessage = new MessageBean();
                                    emptyMessage.setType(MessageBean.MESSAGE_TYPE_EMPTY);
                                    resultMessageList.add(emptyMessage);

                                } else {
                                    MessageBean dataMessage = new MessageBean();
                                    dataMessage.setType(MessageBean.MESSAGE_TYPE_DATA);
                                    resultMessageList.add(0, dataMessage);

                                }

                                MessageResultHolder messageResultHolder = new MessageResultHolder(resultMessageList,
                                        latestMsgId, messageMode, MessageResultHolder.RESULT_MODE_REFRESH);
                                message.obj = messageResultHolder;

                                message.arg1 = PARSE_SUCCESS;

                            } catch (Exception e) {
                                Log.e("parse excepti9on", "" + e);

                            }

                        }

                    }

                }
                if (message.arg1 == PARSE_FAILED && source.contains(login_timeout)) {
                    message.arg1 = PARSE_SPECIFIC_ERROR;
                }

                loadHandler.sendMessage(message);

            }

        }.start();

    }

    public static void parseNextMessage(
            final MessageListParseCallBack messageListParseCallBack,
            final String source, final UserBean userBean,
            final int messageMode, final String referer) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI

                switch (msg.arg1) {
                    case PARSE_SUCCESS:
                        MessageResultHolder messageResultHolder = (MessageResultHolder) msg.obj;
                        messageListParseCallBack.onBack(messageResultHolder, msg.arg1);

                        break;
                    default:

                        messageListParseCallBack.onBack(null, msg.arg1);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                Message message = new Message();
                message.arg1 = PARSE_FAILED;

                Document document = Jsoup.parse(source);
                Elements scriptElements = document.getElementsByTag("script");
                for (Element nowElement : scriptElements) {
                    if (nowElement.html().contains("wx.cgiData ")) {
                        JSONObject contentObject = getMessageArray(nowElement.html());
                        if (contentObject != null) {
                            try {
                                JSONArray getArray = contentObject.getJSONArray("messageArray");
                                ArrayList<MessageBean> resultMessageList = getMessageItems(
                                        getArray, userBean, referer);
                                String latestMsgId = contentObject.get("lastMsgId").toString();

                                MessageResultHolder messageResultHolder = new MessageResultHolder(resultMessageList, latestMsgId, messageMode, MessageResultHolder.RESULT_MODE_ADD);
                                message.obj = messageResultHolder;
                                message.arg1 = PARSE_SUCCESS;


                            } catch (Exception e) {

                            }

                        }

                    }
                }
                if (message.arg1 == PARSE_FAILED && source.contains(login_timeout)) {
                    message.arg1 = PARSE_SPECIFIC_ERROR;
                }

                loadHandler.sendMessage(message);
            }
        }.start();

    }


    public interface MaterialListParseCallBack {
        public void onBack(int code, MaterialResultHolder materialResultHolder);
    }

    public static void parseMaterialList(
            final MaterialListParseCallBack materialListParseCallBack,
            final int begin,
            final String source, final UserBean userBean,
            final String referer) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                switch (msg.arg1) {
                    case PARSE_SUCCESS:

                        materialListParseCallBack.onBack(msg.arg1, (MaterialResultHolder) msg.obj);
                        break;
                    default:

                        materialListParseCallBack.onBack(msg.arg1, null);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                Message message = new Message();
                message.arg1 = PARSE_FAILED;
                try {
                    JSONObject contentObject = new JSONObject(source);
                    JSONObject resultObject = contentObject.getJSONObject("base_resp");
                    if (getRet(resultObject) == 0) {
                        JSONObject fileContentObject = contentObject.getJSONObject("page_info");
                        JSONArray materialArray = fileContentObject.getJSONArray("file_item");
                        Gson gson = new Gson();

                        ArrayList<MaterialBean> getList = new ArrayList<MaterialBean>();
                        for (int i = 0; i < materialArray.length(); i++) {
                            JSONObject nowItemObject = materialArray.getJSONObject(i);

                            MaterialBean nowItemBean = gson.fromJson(nowItemObject.toString(), MaterialBean.class);
                            getList.add(nowItemBean);

                        }
                        MaterialResultHolder materialResultHolder = new MaterialResultHolder(getList, (begin == 0) ? MaterialResultHolder.RESULT_MODE_REFRESH : MaterialResultHolder.RESULT_MODE_ADD);
                        message.arg1 = PARSE_SUCCESS;
                        message.obj = materialResultHolder;

                    }


                } catch (Exception e) {
                    Log.e("material list parse error", "" + e);

                }
                if (message.arg1 == PARSE_FAILED && source.contains(login_timeout)) {
                    message.arg1 = PARSE_SPECIFIC_ERROR;

                }

                loadHandler.sendMessage(message);


            }


        }.start();

    }


    public interface AppMsgListParseCallBack {
        public void onBack(int code, MaterialResultHolder materialResultHolder);
    }

    public static void parseAppMsgList(
            final AppMsgListParseCallBack messageListParseCallBack,
            final int begin,
            final String source, final UserBean userBean,
            final String referer) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                switch (msg.arg1) {
                    case PARSE_SUCCESS:

                        messageListParseCallBack.onBack(msg.arg1, (MaterialResultHolder) msg.obj);
                        break;
                    default:

                        messageListParseCallBack.onBack(msg.arg1, null);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                Message message = new Message();

                message.arg1 = PARSE_FAILED;
                try {
                    JSONObject contentObject = new JSONObject(source);
                    JSONObject appContentObject = contentObject.getJSONObject("app_msg_info");
                    JSONArray appItemArray = appContentObject.getJSONArray("item");
                    JSONObject appInfoObject = appContentObject.getJSONObject("file_cnt");

                    Gson gson = new Gson();
/*
                    AppItemHolder appItemHolder = new AppItemHolder();

                    appItemHolder = (AppItemHolder) gson.fromJson(appInfoObject.toString(), AppItemHolder.class);
                    */

                    ArrayList<MaterialBean> materialBeans = new ArrayList<MaterialBean>();
                    for (int i = 0; i < appItemArray.length(); i++) {
                        JSONObject nowItemObject = appItemArray.getJSONObject(i);

                        AppItemBean nowItemBean = gson.fromJson(nowItemObject.toString(), AppItemBean.class);
                        materialBeans.add(new MaterialBean(nowItemBean));

                    }
                    MaterialResultHolder materialResultHolder = new MaterialResultHolder(materialBeans,
                            begin==0?MaterialResultHolder.RESULT_MODE_REFRESH:MaterialResultHolder.RESULT_MODE_ADD);

                    message.arg1 = PARSE_SUCCESS;
                    message.obj = materialResultHolder;


                } catch (Exception e) {
                    Log.e("app list parse error", "" + e);

                }
                if (message.arg1 == PARSE_FAILED && source.contains(login_timeout)) {
                    message.arg1 = PARSE_SPECIFIC_ERROR;
                }
                loadHandler.sendMessage(message);


            }


        }.start();

    }


    public interface UploadInfoParseCallBack {
        public void onBack(int code);
    }

    public static void parseUploadInfo(
            final UploadInfoParseCallBack uploadInfoParseCallBack,
            final String source, final UploadHelper uploadHelper) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                switch (msg.arg1) {
                    case PARSE_SUCCESS:

                        uploadInfoParseCallBack.onBack(msg.arg1);
                        break;
                    default:

                        uploadInfoParseCallBack.onBack(msg.arg1);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                Message message = new Message();

                message.arg1 = PARSE_FAILED;
                try {

                    String ticket = getTickets(source);
                    Log.e("get ticket", "" + ticket);
                    if (ticket != null) {
                        uploadHelper.setTicket(ticket);

                        message.arg1 = PARSE_SUCCESS;
                    }
                } catch (Exception e) {
                    Log.e("upload info parse error", "" + e);

                }
                loadHandler.sendMessage(message);


            }

            private String getTickets(String source) {
                String result = null;
                String regx = "data:(\\{[^\\}]*)";

                Pattern pattern = Pattern.compile(regx);
                Matcher matcher = pattern.matcher(source);
                while (matcher.find()) {

                    String dataString = matcher.group(1);
                    Log.e("get data", "" + dataString);
                    if (dataString != null) {
                        regx = "ticket:\"([^\"]*)\"";
                        pattern = Pattern.compile(regx);
                        matcher = pattern.matcher(dataString);
                        while (matcher.find()) {
                            String ticket = matcher.group(1);
                            return ticket;

                        }

                    }


                }


                return result;
            }

        }.start();

    }


    public interface LoginParseCallBack {
        public void onBack(int code, UserBean userBean);
    }

    public static void parseLogin(
            final LoginParseCallBack loginParseCallBack,
            final String source, final Header[] headers, final UserBean userBean) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                switch (msg.arg1) {
                    case PARSE_SUCCESS:

                        loginParseCallBack.onBack(msg.arg1, (UserBean) msg.obj);
                        break;
                    default:

                        loginParseCallBack.onBack(msg.arg1, null);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                Message message = new Message();

                message.arg1 = PARSE_FAILED;
                try {


                    JSONObject resultJsonObject = null;

                    try {

                        resultJsonObject = new JSONObject(source);
                        int ret = getRet(resultJsonObject);


                        if (ret == RET_LOGIN_SUCCESS) {

                            if (source.contains("token")) {
                                String tokenString = getToken(resultJsonObject);
                                userBean.setToken(tokenString);
                                initNormalLogin();

                                message.arg1 = PARSE_SUCCESS;
                                message.obj = userBean;

                            } else {
                                if (source.contains(LOGIN_NEEDS_VERIFY)) {

                                    initVerify();
                                    message.arg1 = PARSE_SPECIFIC_STUATION;
                                    message.obj = userBean;
                                }

                            }
                        }

                    } catch (Exception exception) {
                        Log.e("login exception fuck", exception + "");

                    }


                } catch (Exception e) {
                    Log.e("app list parse error", "" + e);

                }
                if (message.arg1 == PARSE_FAILED && source.contains(login_timeout)) {
                    message.arg1 = PARSE_SPECIFIC_ERROR;
                }
                loadHandler.sendMessage(message);


            }

            private void initNormalLogin() {

                for (int i = 0; i < headers.length; i++) {


                    if (headers[i].getName().contains(
                            "Set-Cookie")) {
                        String nowCookie = headers[i]
                                .getValue();
                        if (nowCookie.contains("slave_user")) {

                            String slaveUser = nowCookie
                                    .substring(
                                            nowCookie
                                                    .indexOf("slave_user") + 11,
                                            nowCookie.indexOf(";"));
                            userBean.setSlaveUser(slaveUser);
                        }
                        if (nowCookie.contains("slave_sid")) {

                            String slaveSid = nowCookie
                                    .substring(nowCookie
                                            .indexOf("slave_sid") + 10,
                                            nowCookie.indexOf(";"));
                            userBean.setSlaveSid(slaveSid);
                        }

                    }

                }
            }

            private void initVerify() {

                try {
                    JSONObject resultObject = new JSONObject(source);
                    String errorMsg = resultObject.get("ErrMsg").toString();
                    String regex = "phone=(.*)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(errorMsg);
                    while (matcher.find()) {
                        String phone = matcher.group(1);
                        if (phone != null) {
                            userBean.setPhone(phone);
                        }

                    }


                } catch (Exception e) {

                }


            }


        }.start();

    }


    public interface VerifyPageParseCallBack {
        public void onBack(int code, UserBean userBean);
    }

    public static void parseVerifyPage(
            final VerifyPageParseCallBack verifyPageParseCallBack,
            final String source, final Header[] headers, final UserBean userBean) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                switch (msg.arg1) {
                    case PARSE_SUCCESS:

                        verifyPageParseCallBack.onBack(msg.arg1, (UserBean) msg.obj);
                        break;
                    default:

                        verifyPageParseCallBack.onBack(msg.arg1, null);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                Message message = new Message();

                message.arg1 = PARSE_FAILED;
                try {
                    Log.e("result", source);
                    initNormalLogin();
                    message.arg1 = PARSE_SUCCESS;
                    message.obj = userBean;

                } catch (Exception e) {
                    Log.e("app list parse error", "" + e);

                }
                if (message.arg1 == PARSE_FAILED && source.contains(login_timeout)) {
                    message.arg1 = PARSE_SPECIFIC_ERROR;
                }
                loadHandler.sendMessage(message);


            }

            private void initNormalLogin() {

                for (int i = 0; i < headers.length; i++) {

                    Log.e("get verify page", "" + headers[i].getValue());
                    if (headers[i].getName().contains(
                            "Set-Cookie")) {

                        String nowCookie = headers[i]
                                .getValue();
                        if (nowCookie.contains("ticket_id")) {
                            Log.e("get ticket id", "" + headers[i].getValue());
                        }
                        if (nowCookie.contains("ticket")) {

                            Log.e("get ticket", "" + headers[i].getValue());

                        }

                    }

                }
            }

            private void initVerify() {


                Log.e("start get verify", "start");
                for (int i = 0; i < headers.length; i++) {

                    if (headers[i].getName().contains(
                            "Set-Cookie")) {

                        Log.e("start get verify", "get cookie\n" + headers[i].getValue());
                        String nowCookie = headers[i]
                                .getValue();
                        if (nowCookie.contains("verifysession")) {

                            Log.e("start get verify", "get session");

                            String regex = "verifysession=([^;]*);";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(nowCookie);
                            while (matcher.find()) {
                                String verifySession = matcher.group(1);
                                Log.e("get verify session", verifySession + "");
                                userBean.setVerifySession(verifySession);
                            }


                        }

                    }

                }
            }


        }.start();

    }


    public static String getVerifyUrl(String source) {
        try {


        } catch (Exception e) {

        }

        return "";

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

            String reg = "latest_msg_id\\s:\\s'(\\d*)',[^\\(]*\\(\\{\"msg_item\":(\\[.*(?=\\}\\).msg_item))";
            Pattern pattern = Pattern.compile(reg);
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
            Log.e("get message array exception", "" + exception);

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
        public void onBack(FansResultHolder fansResultHolder, int code);
    }

    public static void parseFansList(final String source, final String referer, final int currentGroupIndex,
                                     final UserBean userBean,
                                     final boolean refresh,
                                     final FansListParseCallback fansListParseCallback) {


        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                switch (msg.arg1) {
                    case PARSE_SUCCESS:

                        fansListParseCallback.onBack((FansResultHolder) msg.obj, msg.arg1);

                        break;
                    default:

                        fansListParseCallback.onBack(null, msg.arg1);
                        break;
                }


            }
        };

        new Thread() {

            public void run() {


                Message nowMessage = new Message();
                nowMessage.arg1 = PARSE_FAILED;
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


                        JSONArray fansArray = new JSONArray(fansContentString);

                        ArrayList<FansBean> fansBeans = new ArrayList<FansBean>();
                        for (int i = 0; i < fansArray.length(); i++) {
                            JSONObject nowJsonObject = fansArray.getJSONObject(i);
                            FansBean nowFansBean = (FansBean) gson.fromJson(
                                    nowJsonObject.toString(), FansBean.class);
                            nowFansBean.setReferer(referer);
                            fansBeans.add(nowFansBean);
                        }

                        if (refresh) {
                            //add fans data
                            FansBean dataBean = new FansBean();
                            dataBean.setBeanType(FansBean.BEAN_TYPE_DATA);
                            fansBeans.add(0, dataBean);

                        }

                        FansResultHolder fansResultHolder = new FansResultHolder(fansBeans, fansGroupBeans, currentGroupIndex,
                                refresh ? FansResultHolder.RESULT_MODE_REFRESH : FansResultHolder.RESULT_MODE_ADD);

                        nowMessage.arg1 = PARSE_SUCCESS;
                        nowMessage.obj = fansResultHolder;


                    } catch (Exception exception) {
                        Log.e("fans parse errror", "" + exception);

                    }

                    loadHandler.sendMessage(nowMessage);
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


    public interface ParseFansProfileCallBack {
        public void onBack(int code, FansProfileHolder fansProfileHolder);
    }

    public static class FansProfileHolder {
        public FansBean fansBean;
        public ArrayList<FansGroupBean> fansGroupBeans;

        public FansProfileHolder(FansBean fansBean, ArrayList<FansGroupBean> fansGroupBeans) {
            this.fansBean = fansBean;
            this.fansGroupBeans = fansGroupBeans;
        }
    }

    public static void parseFansProfile(final String source,
                                        final UserBean userBean,
                                        final ParseFansProfileCallBack parseFansProfileCallBack) {

        final Handler loadHandler = new Handler() {

            // 子类必须重写此方法,接受数据
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                switch (msg.arg1) {
                    case PARSE_SUCCESS:
                        FansProfileHolder fansProfileHolder = (FansProfileHolder) msg.obj;
                        parseFansProfileCallBack.onBack(msg.arg1, fansProfileHolder);

                        break;
                    default:

                        parseFansProfileCallBack.onBack(msg.arg1, null);
                        break;
                }

            }
        };

        new Thread() {
            public void run() {

                FansProfileHolder fansProfileHolder = getResult();


                Message message = new Message();
                if (fansProfileHolder != null) {
                    message.arg1 = PARSE_SUCCESS;
                    message.obj = fansProfileHolder;

                } else {

                    if (source.contains(login_timeout) || source.contains(invalid_bizpay)) {
                        message.arg1 = PARSE_SPECIFIC_ERROR;
                    } else {
                        message.arg1 = PARSE_FAILED;
                    }

                }
                loadHandler.sendMessage(message);
            }

            private FansProfileHolder getResult() {

                try {
                    JSONObject resultObject = new JSONObject(source);
                    JSONObject stateObject = resultObject.getJSONObject("base_resp");
                    if (getRet(stateObject) == 0) {
                        JSONObject contactInfoObject = resultObject.getJSONObject("contact_info");
                        JSONObject groupObject = resultObject.getJSONObject("groups");

                        Gson gson = new Gson();

                        FansBean fansBean = (FansBean) gson.fromJson(contactInfoObject.toString(), FansBean.class);

                        JSONArray fansTypeArray = groupObject.getJSONArray("groups");
                        ArrayList<FansGroupBean> fansGroupBeans = new ArrayList<FansGroupBean>();
                        for (int i = 0; i < fansTypeArray.length(); i++) {
                            JSONObject nowJsonObject = fansTypeArray
                                    .getJSONObject(i);
                            FansGroupBean nowGroupBean = (FansGroupBean) gson
                                    .fromJson(nowJsonObject.toString(),
                                            FansGroupBean.class);
                            fansGroupBeans.add(nowGroupBean);
                        }

                        FansProfileHolder fansProfileHolder = new FansProfileHolder(fansBean, fansGroupBeans);
                        return fansProfileHolder;

                    }


                } catch (Exception e) {
                    Log.e("fans profile parse exception", "" + e);

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
                        setTimeTagShow(messageBeans);

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

                String regex = "msg_item\":(\\[.*\\](?=\\}\\}))";
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


            long lastTime = 0;

            private void setTimeTagShow(ArrayList<MessageBean> messageBeans) {

                for (int i = 0; i < messageBeans.size(); i++) {
                    MessageBean nowBean = messageBeans.get(i);
                    long nowTime = Long.parseLong(nowBean.getDateTime());
                    if (nowTime - lastTime > 100000) {
                        nowBean.setTimeTagShow(true);

                    }
                    lastTime = nowTime;

                }

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


    private static void removeEmptyMessage(ArrayList<MessageBean> messageBeans) {
        for (int i = 0; i < messageBeans.size(); i++) {
            if (messageBeans.get(i).getType() == MessageBean.MESSAGE_TYPE_EMPTY) {
                messageBeans.remove(i);
            }

        }

    }

    public static final int GET_RET_NONE = -1;
}

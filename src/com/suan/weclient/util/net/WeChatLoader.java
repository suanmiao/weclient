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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.suan.weclient.util.data.MessageHolder;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.data.UserBean;

public class WeChatLoader {

    public static final int WECHAT_LOGIN_OK = 302;
    public static final int WECHAT_MASS_OK = 0;
    public static final int WECHAT_MASS_ERROR_ONLY_ONE = 64004;

    public static final int WECHAT_STAR_OK = 0;
    public static final int WECHAT_EDIT_GROUP_OK = 0;

    public static final int WECHAT_REPLY_OK = 0;

    public static final int WECHAT_SINGLE_CHAT_OK = 0;
    public static final int WECHAT_SINGLE_CHAT_OUT_OF_DATE = 10706;

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

    private static final String WECHAT_URL_GET_MESSAGE_LIST = "https://mp.weixin.qq.com/cgi-bin/message";

    private static final String WECHAT_URL_GET_NEW_MESSAGE_COUNT = "https://mp.weixin.qq.com/cgi-bin/getnewmsgnum";

    private static final String WECHAT_URL_MESSAGE_LOAD_PAGE = "https://mp.weixin.qq.com/cgi-bin/message";

    private static final String WECHAT_URL_MESSAGE_REPLY = "https://mp.weixin.qq.com/cgi-bin/singlesend";

    private static final String WECHAT_URL_MESSAGE_STAR = "https://mp.weixin.qq.com/cgi-bin/setstarmessage";

    private static final String WECHAT_URL_MESSAGE_MASS = "https://mp.weixin.qq.com/cgi-bin/masssend";

    private static final String WECHAT_URL_GET_MESSAGE_PROFILE_IMG = "https://mp.weixin.qq.com/cgi-bin/getheadimg";

    private static final String WECHAT_URL_GET_USER_PROFILE = "https://mp.weixin.qq.com/cgi-bin/home";

    private static final String WECHAT_URL_GET_MASS_DATA = "https://mp.weixin.qq.com/cgi-bin/masssendpage";

    private static final String WECHAT_URL_GET_MESSAGE_IMG = "https://mp.weixin.qq.com/cgi-bin/getimgdata";

    public static final String WECHAT_URL_MESSAGE_IMG_LARGE = "large";
    public static final String WECHAT_URL_MESSAGE_IMG_SMALL = "small";

    private static final String WECHAT_URL_GET_VOICE_MESSAGE = "https://mp.weixin.qq.com/cgi-bin/getvoicedata";

    private static final String WECHAT_URL_GET_VOICE_MESSAGE_REFERER = "https://mp.weixin.qq.com/mpres/zh_CN/htmledition/plprecorder/soundmanager2.swf";

    private static final String WECHAT_URL_GET_FANS_LIST = "https://mp.weixin.qq.com/cgi-bin/contactmanage";

    private static final String WECHAT_URL_MODIFY_CONTACTS = "https://mp.weixin.qq.com/cgi-bin/modifycontacts";

    public static final int MODIFY_CONTACTS_ACTION_MODIFY = 1;
    public static final int MODIFY_CONTACTS_ACTION_REMARK = 2;

    private static final String WECHAT_URL_GET_CONTACT_INFO = "https://mp.weixin.qq.com/cgi-bin/getcontactinfo";


    private static final String WECHAT_URL_GET_CHAT_LIST = "https://mp.weixin.qq.com/cgi-bin/singlesendpage";

    private static final String WECHAT_URL_CHAT_SINGLE_SEND = "https://mp.weixin.qq.com/cgi-bin/singlesend";

    private static final String WECHAT_URL_GET_CHAT_NEW_ITEM = "https://mp.weixin.qq.com/cgi-bin/singlesendpage";

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
                String targetUrl = WECHAT_URL_GET_USER_PROFILE;

               /* PROFILE = "https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=";
                */

                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("t","home/index"));
                paramList.add(new BasicNameValuePair("token",userBean.getToken()));
                paramList.add(new BasicNameValuePair("lang","zh_CN"));




                HttpResponse response = httpGet(targetUrl, paramList, headerList);

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
            final UserBean userBean, final int mode, final boolean hideKeyWordMessage) {
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

/*
    1 = "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&count=20";
    2 = "&day=";
    3 = "&action=star&token=";
    4 = "&token=";
    5 = "&lang=zh_CN";
    6 = "&filterivrmsg=";*/

                int hideKeyWord = hideKeyWordMessage ? 1 : 0;

                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("t","message/list"));
                paramList.add(new BasicNameValuePair("count","20"));

                paramList.add(new BasicNameValuePair("filterivrmsg",hideKeyWord+""));





                String targetUrl = WECHAT_URL_GET_MESSAGE_LIST;

                switch (mode) {
                    case GET_MESSAGE_ALL:

                        paramList.add(new BasicNameValuePair("day","7"));
                       ;


                        break;
                    case GET_MESSAGE_TODAY:

                        paramList.add(new BasicNameValuePair("day","0"));


                        break;

                    case GET_MESSAGE_YESTERDAY:

                        paramList.add(new BasicNameValuePair("day","1"));
                        break;

                    case GET_MESSAGE_DAY_BEFORE:

                        paramList.add(new BasicNameValuePair("day","2"));

                        break;

                    case GET_MESSAGE_OLDER:

                        paramList.add(new BasicNameValuePair("day","3"));

                        break;

                    case GET_MESSAGE_STAR:

                        paramList.add(new BasicNameValuePair("action","star"));

                        break;
                }


                paramList.add(new BasicNameValuePair("token",userBean.getToken()));
                paramList.add(new BasicNameValuePair("lang","zh_CN"));

                HttpResponse response = httpGet(targetUrl,paramList, headerList);

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
            final MessageHolder messageHolder, final int page,final int mode, final boolean hideKeyWordMessage) {
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
                String targetUrl = WECHAT_URL_MESSAGE_LOAD_PAGE
;

               /*
    1 = "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&action=&keyword=&frommsgid=";
    2 = "&offset=";
    3 = "&count=20&day=7&token=";
    4 = "&lang=zh_CN";
*/


                int hideKeyWord = hideKeyWordMessage ? 1 : 0;

                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();

                paramList.add(new BasicNameValuePair("t","message/list"));
                paramList.add(new BasicNameValuePair("action",""));
                paramList.add(new BasicNameValuePair("keyword",""));

                paramList.add(new BasicNameValuePair("filterivrmsg",hideKeyWord+""));

                paramList.add(new BasicNameValuePair("frommsgid",messageHolder.getLatestMsgId()));
                paramList.add(new BasicNameValuePair("offset",offset+""));
                paramList.add(new BasicNameValuePair("count","20"));


                switch (mode) {
                    case GET_MESSAGE_ALL:

                        paramList.add(new BasicNameValuePair("day","7"));
                       ;


                        break;
                    case GET_MESSAGE_TODAY:

                        paramList.add(new BasicNameValuePair("day","0"));


                        break;

                    case GET_MESSAGE_YESTERDAY:

                        paramList.add(new BasicNameValuePair("day","1"));
                        break;

                    case GET_MESSAGE_DAY_BEFORE:

                        paramList.add(new BasicNameValuePair("day","2"));

                        break;

                    case GET_MESSAGE_OLDER:

                        paramList.add(new BasicNameValuePair("day","3"));

                        break;

                    case GET_MESSAGE_STAR:

                        paramList.add(new BasicNameValuePair("action","star"));

                        break;
                }


                paramList.add(new BasicNameValuePair("token",messageHolder.getUserBean().getToken()));
                paramList.add(new BasicNameValuePair("lang","zh_CN"));


                HttpResponse response = httpGet(targetUrl,paramList, headerList);

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

                String targetUrl = WECHAT_URL_GET_MESSAGE_PROFILE_IMG
;

/*
    1 = "https://mp.weixin.qq.com/cgi-bin/getheadimg?token=";
    2 = "&fakeid=";
               */
                 ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("token",userBean.getToken()));
                paramList.add(new BasicNameValuePair("fakeid",fakeId));



                HttpResponse response = httpGet(targetUrl,paramList, headerList);


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
            final UserBean userBean,final MessageBean messageBean,
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
                        + userBean.getSlaveSid() + "; " + "slave_user=" + userBean.getSlaveUser()));
                headerList.add(new BasicNameValuePair("Content-Type",
                        "text/html; charset=utf-8"));

                headerList.add(new BasicNameValuePair("Referer", messageBean.getReferer()));

                String targetUrl = WECHAT_URL_GET_MESSAGE_IMG ;
/*
    1 = "https://mp.weixin.qq.com/cgi-bin/getimgdata?token=";
    2 = "&msgid=";
    3 = "&mode=";
    4 = "&source=&fileId=0";
               */



                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("token",userBean.getToken()));
                paramList.add(new BasicNameValuePair("msgid",messageBean.getId()));
                paramList.add(new BasicNameValuePair("mode",imgType));
                paramList.add(new BasicNameValuePair("source",messageBean.getSource()));
                paramList.add(new BasicNameValuePair("fileId",messageBean.getFileId()));
                paramList.add(new BasicNameValuePair("lang","zh_CN"));


                HttpResponse response = httpGet(targetUrl,paramList, headerList);
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

                String targetUrl = WECHAT_URL_GET_VOICE_MESSAGE ;
/*
    1 = "https://mp.weixin.qq.com/cgi-bin/getvoicedata?msgid=";
    2 = "&fileid=&token=";
    3 = "&lang=zh_CN";
               */
                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("msgid",msgId));
                paramList.add(new BasicNameValuePair("fileid",""));
                paramList.add(new BasicNameValuePair("token",userBean.getToken()));
                paramList.add(new BasicNameValuePair("lang","zh_CN"));



                HttpResponse response = httpGet(targetUrl,paramList, headerList);
                if (response != null) {

                    try {

                        Message message = new Message();
                        ContentHolder contentHolder = new ContentHolder();
                        InputStream inputStream = (InputStream) response
                                .getEntity().getContent();
                        byte[] bytes = new byte[length * 2];
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





                String targetUrl = WECHAT_URL_GET_MASS_DATA
                        ;
   /*
   1 = "https://mp.weixin.qq.com/cgi-bin/masssendpage?t=mass/send&token=";
   2 = "&lang=zh_CN";
*/
                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("t","mass/send"));
                paramList.add(new BasicNameValuePair("token",userBean.getToken()));
                paramList.add(new BasicNameValuePair("lang","zh_CN"));


                HttpResponse response = httpGet(targetUrl,paramList, headerList);

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
            final String groupId, final int page) {
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
                    if (groupId.equals("-1")) {
                        referer = "https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token="
                                + userBean.getToken();
                    } else {
                        referer = "https://mp.weixin.qq.com/cgi-bin/contactmanage?t=user/index&pagesize=10&pageidx=0&type=0&token=" + userBean.getToken() + "&lang=zh_CN";
                    }

                } else {

                    referer = "https://mp.weixin.qq.com/cgi-bin/contactmanage?t=user/index&pagesize=10&pageidx=" + (page - 1)
                            + "&type=0&groupid=" + groupId
                            + "&token=" + userBean.getToken()
                            + "&lang=zh_CN";

                }
                headerList.add(new BasicNameValuePair("Referer", referer));


                String targetUrl = WECHAT_URL_GET_FANS_LIST;

/*
    1 = "https://mp.weixin.qq.com/cgi-bin/contactmanage?t=user/index&pagesize=10&pageidx=";
    */
/*
     * if want all the user,use 2,without groupId if want group user ,use 3,with
     * groupId
     *//*

    2 = "&type=0";
    3 = "&type=0&groupid=";
    4 = "&token=";
    5 = "&lang=zh_CN";
*/
                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();

                paramList.add(new BasicNameValuePair("t", "user/index"));
                paramList.add(new BasicNameValuePair("pagesize", "10"));
                paramList.add(new BasicNameValuePair("pageidx", page + ""));
                paramList.add(new BasicNameValuePair("type", "0"));
                if (groupId.equals("-1")) {
                    // all user

                } else {

                    paramList.add(new BasicNameValuePair("groupid", groupId));
                }

                paramList.add(new BasicNameValuePair("token", userBean.getToken()));
                paramList.add(new BasicNameValuePair("lang", "zh_CN"));

                HttpResponse response = httpGet(targetUrl, paramList, headerList);

                if (response != null) {

                    try {

                        Message message = new Message();
                        ContentHolder contentHolder = new ContentHolder();

                        String strResult = EntityUtils.toString(response
                                .getEntity());
                        contentHolder.put("result", strResult);
                        Log.e("fans result",""+strResult);
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


    public interface WechatModifyContactsCallBack {
        public void onBack(String strResult);
    }

    public static void wechatModifyContacts(
            final WechatExceptionListener wechatExceptionListener,
            final WechatModifyContactsCallBack editFansGroupCallBack,
            final UserBean userBean, final int action,
            final String groupId, final String toFakeIdList,
            final String remark
    ) {
        final Handler loadHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);

                // 此处可以更新UI
                ContentHolder contentHolder = (ContentHolder) msg.obj;
                editFansGroupCallBack.onBack(contentHolder.get("result"));

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
                String referer = "https://mp.weixin.qq.com/cgi-bin/contactmanage?t=user/index&pagesize=10&pageidx=0&type=0&groupid=0&token=" + userBean.getToken() + "&lang=zh_CN";

                headerList.add(new BasicNameValuePair("Referer",
                        referer));
                ArrayList<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
                paramArrayList.add(new BasicNameValuePair("ajax", "1"));
                paramArrayList.add(new BasicNameValuePair("f", "json"));
                paramArrayList.add(new BasicNameValuePair("lang", "zh_CN"));
                String random = "0.5573073124532";
                random += (int) (Math.random() * 1000);
                paramArrayList.add(new BasicNameValuePair("random", random));

                switch (action) {
                    case MODIFY_CONTACTS_ACTION_MODIFY:
                        paramArrayList.add(new BasicNameValuePair("action", "modifycontacts"));

                        paramArrayList.add(new BasicNameValuePair("t", "ajax-putinto-group"));
                        paramArrayList.add(new BasicNameValuePair("contacttype", groupId));
                        paramArrayList.add(new BasicNameValuePair("tofakeidlist", toFakeIdList));

                        break;

                    case MODIFY_CONTACTS_ACTION_REMARK:

                        paramArrayList.add(new BasicNameValuePair("action", "setremark"));

                        paramArrayList.add(new BasicNameValuePair("t", "ajax-response"));
                        paramArrayList.add(new BasicNameValuePair("remark", remark));
                        paramArrayList.add(new BasicNameValuePair("tofakeuin", toFakeIdList));
                        break;
                }
                paramArrayList.add(new BasicNameValuePair("token", userBean
                        .getToken()));


                String targetUrl = WECHAT_URL_MODIFY_CONTACTS;
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


    public interface WechatGetContactInfoCallBack {
        public void onBack(String strResult);
    }

    public static void wechatGetContactInfo(
            final WechatExceptionListener wechatExceptionListener,
            final WechatGetContactInfoCallBack getContactInfoCallBack,
            final UserBean userBean,
            final String fakeId
    ) {
        final Handler loadHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);

                // 此处可以更新UI
                ContentHolder contentHolder = (ContentHolder) msg.obj;
                getContactInfoCallBack.onBack(contentHolder.get("result"));

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
                String referer = "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&count=20&day=7&token=" + userBean.getToken() + "&lang=zh_CN";

                headerList.add(new BasicNameValuePair("Referer",
                        referer));

                ArrayList<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
                paramArrayList.add(new BasicNameValuePair("ajax", "1"));
                paramArrayList.add(new BasicNameValuePair("f", "json"));

                paramArrayList.add(new BasicNameValuePair("fakeid", fakeId));
                paramArrayList.add(new BasicNameValuePair("lang", "zh_CN"));
                String random = "0.5573073124532";
                random += (int) (Math.random() * 1000);
                paramArrayList.add(new BasicNameValuePair("random", random));

                paramArrayList.add(new BasicNameValuePair("t", "ajax-getcontactinfo"));

                paramArrayList.add(new BasicNameValuePair("token", userBean
                        .getToken()));


                String targetUrl = WECHAT_URL_GET_CONTACT_INFO;
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

/*
    1 = "https://mp.weixin.qq.com/cgi-bin/singlesendpage?tofakeid=";
    2 = "&t=message/send&action=index&token=";
    3 = "&lang=zh_CN";
                */
                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("tofakeid", toFakeId));
                paramList.add(new BasicNameValuePair("t", "message/send"));
                paramList.add(new BasicNameValuePair("action", "index"));
                paramList.add(new BasicNameValuePair("token", userBean.getToken()));
                paramList.add(new BasicNameValuePair("lang", "zh_CN"));


                String targetUrl = WECHAT_URL_GET_CHAT_LIST;

                HttpResponse response = httpGet(targetUrl, paramList, headerList);

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
            final UserBean userBean, final MessageBean messageBean) {
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

                String referer = "https://mp.weixin.qq.com/cgi-bin/singlesendpage?tofakeid="
                        + messageBean.getToFakeId() + "&t=message/send&action=index&token="
                        + userBean.getToken()
                        + "&lang=zh_CN";
                headerList.add(new BasicNameValuePair("Referer", referer));
                headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
                        + userBean.getSlaveSid() + "; " + "slave_user="
                        + userBean.getSlaveUser()));
                headerList.add(new BasicNameValuePair("Content-Type",
                        "text/html; charset=utf-8"));

                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", messageBean.getType() + ""));
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

                String referer = "https://mp.weixin.qq.com/cgi-bin/singlesendpage?tofakeid="
                        + messageBean.getToFakeId() + "&t=message/send&action=index&token="
                        + userBean.getToken()
                        + "&lang=zh_CN";
                headerList.add(new BasicNameValuePair("Referer", referer));
                headerList.add(new BasicNameValuePair("Content-Type",
                        "text/html; charset=utf-8"));


/*
    1 = "https://mp.weixin.qq.com/cgi-bin/singlesendpage?token=";
   2 = "&lang=zh_CN&random=0.7740735916886479&f=json&ajax=1&tofakeid=";
    3 = "&action=sync&lastmsgfromfakeid=";
    4 = "&lastmsgid=";
    5 = "&createtime=";

     */


                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("token", userBean.getToken()));
                paramList.add(new BasicNameValuePair("lang", "zh_CN"));
                paramList.add(new BasicNameValuePair("random", "0.7740735916886479"));
                paramList.add(new BasicNameValuePair("f", "json"));
                paramList.add(new BasicNameValuePair("ajax", "1"));
                paramList.add(new BasicNameValuePair("tofakeid", toFakeId));
                paramList.add(new BasicNameValuePair("action", "sync"));
                paramList.add(new BasicNameValuePair("lastmsgfromfakeid", userBean.getFakeId()));
                paramList.add(new BasicNameValuePair("lastmsgid", lastMsgId));
                paramList.add(new BasicNameValuePair("createtime", createTime));
                String targetUrl = WECHAT_URL_GET_CHAT_NEW_ITEM;

                HttpResponse response = httpGet(targetUrl, paramList, headerList);

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


    public interface WechatGetNewMessageCountCallBack {
        public void onBack(String result);
    }

    public static void wechatGetNewMessageCount(
            final WechatExceptionListener wechatExceptionListener,
            final WechatGetNewMessageCountCallBack newMessageCountCallBack, final UserBean userBean, final String lastMsgId) {
        final Handler loadHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                super.handleMessage(msg);
                // 此处可以更新UI
                ContentHolder contentHolder = (ContentHolder) msg.obj;
                newMessageCountCallBack.onBack(contentHolder.get("result")
                );

            }
        };

        new Thread() {
            public void run() {
                Looper.prepare();
                ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();

                String referer = "https://mp.weixin.qq.com/cgi-bin/message?t=message/list&count=20&day=7&token=" + userBean.getToken() + "&lang=zh_CN";
                headerList
                        .add(new BasicNameValuePair("Referer",
                                referer));
                headerList.add(new BasicNameValuePair("Content-Type",
                        "text/html; charset=utf-8"));

                headerList.add(new BasicNameValuePair("Cookie", "slave_sid="
                        + userBean.getSlaveSid() + "; " + "slave_user="
                        + userBean.getSlaveUser()));

                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("ajax", "1"));
                params.add(new BasicNameValuePair("f", "json"));
                params.add(new BasicNameValuePair("lastmsgid", lastMsgId));
                params.add(new BasicNameValuePair("lang", "zh_CN"));
                params.add(new BasicNameValuePair("random", "0.6920196032466058"));
                params.add(new BasicNameValuePair("t", "ajax-getmsgnum"));
                params.add(new BasicNameValuePair("token", userBean.getToken()));

                HttpResponse response = httpPost(WECHAT_URL_GET_NEW_MESSAGE_COUNT, headerList,
                        params);

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

    private static HttpResponse httpGet(String targetUrl, ArrayList<NameValuePair> paramList,
                                        ArrayList<NameValuePair> headerArrayList) {

        for (int i = 0; i < paramList.size(); i++) {
            NameValuePair nowPair = paramList.get(i);
            if (i == 0) {
                targetUrl += ("?" + nowPair.getName() + "=" + nowPair.getValue());
            } else {
                targetUrl += ("&" + nowPair.getName() + "=" + nowPair.getValue());
            }
        }


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
            HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
            HttpConnectionParams.setSoTimeout(httpParams, 50000);
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

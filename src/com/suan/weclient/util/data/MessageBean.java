package com.suan.weclient.util.data;


import android.util.Log;

import com.suan.weclient.adapter.ChatListAdapter;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;

public class MessageBean {

    public static final int MESSAGE_TYPE_TEXT = 1;
    public static final int MESSAGE_TYPE_IMG = 2;
    public static final int MESSAGE_TYPE_VOICE = 3;

    private String id = "";
    private int type = -1;
    private String fakeid = "";
    private String fileid = "";

    private String nick_name = "";
    private String date_time = "";
    private String content = "";
    private String source = "";
    private String msg_status = "";
    private String has_reply = "";
    private String refuse_reason = "";
    private String is_starred_msg = "0";

    /*
     * about audio
     */
    private String play_length = "0";
    private String length = "0";
    /**
     * ****************************************************************
     */
    private String token = "";
    private String slave_sid = "";
    private String slave_user = "";
    private String referer = "";

    /*
     * about chat
     */
    private String toFakeId = "";
    private int owner = -1;
    private int sendState = -1;

    private MessageSendListener messageSendListener;
    private DataManager mDataManager;
    private UserBean userBean;
    private String lastMsgId;
    private String createTime;
    private ChatListAdapter.ItemViewHolder holder;
    public static final int MESSAGE_SEND_NONE = -1;
    //indecate that the message is in send mode
    public static final int MESSAGE_SEND_PREPARE = 0;
    public static final int MESSAGE_SEND_ING = 1;
    public static final int MESSAGE_SEND_OK = 2;
    public static final int MESSAGE_SEND_FAILED = 3;

    public static final int MESSAGE_OWNER_ME = 0;
    public static final int MESSAGE_OWNER_HER = 1;


    public String getToken() {
        return token;
    }

    public void setToken(String data) {
        this.token = data;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String data) {
        this.referer = data;
    }

    public String getSlaveSid() {
        return slave_sid;
    }

    public void setSlaveSid(String data) {
        this.slave_sid = data;
    }

    public String getSlaveUser() {
        return slave_user;
    }

    public void setSlaveUser(String data) {
        this.slave_user = data;
    }

    public boolean getStarred() {
        return is_starred_msg.equals("1");
    }

    public void setStarred(boolean star) {

        is_starred_msg = star ? "1" : "0";
    }

    public String getId() {
        return id;
    }

    public void setId(String data) {
        this.id = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int data) {
        this.type = data;
    }

    public String getFakeId() {
        return fakeid;
    }

    public void setFakeId(String data) {
        this.fakeid = data;
    }

    public String getNickName() {
        return nick_name;
    }

    public void setNickName(String data) {
        this.nick_name = data;
    }

    public String getDateTime() {
        return date_time;
    }

    public void setDateTime(String data) {
        this.date_time = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String data) {
        this.content = data;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String data) {
        this.source = data;
    }


    public String getPlayLength() {
        return play_length;
    }

    public void setPlayLength(String playLength) {
        this.play_length = playLength;
    }


    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }


    public String getFileId() {
        return fileid;
    }

    public void setFileId(String data) {
        this.fileid = data;
    }


    public String getMsgStatus() {
        return msg_status;
    }

    public void setMsgStatus(String data) {
        this.msg_status = data;
    }


    public String getHasReply() {
        return has_reply;
    }

    public void setHasReply(String data) {
        this.has_reply = data;
    }


    public String getRefuseReason() {
        return refuse_reason;
    }

    public void setRefuseReason(String data) {
        this.refuse_reason = data;
    }


    public String getToFakeId() {
        return toFakeId;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getOwner() {
        return owner;
    }


    public void sendMessage(DataManager dataManager, String lastMsgId, UserBean userBean, String toFakeId) {
        this.toFakeId = toFakeId;
        this.owner = MESSAGE_OWNER_ME;
        this.lastMsgId = lastMsgId;
        this.userBean = userBean;
        this.mDataManager = dataManager;
        this.createTime = (System.currentTimeMillis() + "").substring(0, 10);


        dataManager.getWechatManager().singleChat(userBean, this, new WechatManager.OnActionFinishListener() {
            @Override
            public void onFinish(int code,Object object) {
                Boolean result = (Boolean) object;
                if (result) {
                    getChatNewItem();

                }

            }
        });

    }

    public void resend(){

        mDataManager.getWechatManager().singleChat(userBean, this, new WechatManager.OnActionFinishListener() {
            @Override
            public void onFinish(int code,Object object) {
                Boolean result = (Boolean) object;
                if (result) {
                    getChatNewItem();

                }

            }
        });
    }


    private void getChatNewItem() {

        Log.e("get new item","start");
        WeChatLoader.wechatGetChatNewItems(new WeChatLoader.WechatExceptionListener() {
                                               @Override
                                               public void onError() {

                                               }
                                      }, new WeChatLoader.WechatGetChatNewItems() {
                                               @Override
                                               public void onBack(String strResult) {
                                                   Log.e("get new item",strResult);

                                               }
                                           }, userBean, this, lastMsgId, createTime, toFakeId
        );


    }


    public void setMessageSendListener(ChatListAdapter.ItemViewHolder holder, MessageSendListener messageSendListener) {
        this.holder = holder;
        this.messageSendListener = messageSendListener;
    }

    public MessageSendListener getMessageSendListener() {
        return this.messageSendListener;
    }

    public void setSendState(int sendState) {
        if (this.messageSendListener != null) {
            this.messageSendListener.onSendFinish(sendState, holder);
        }

        this.sendState = sendState;
    }

    public int getSendState() {

        return sendState;
    }


    public interface MessageSendListener {
        public void onSendFinish(int state, ChatListAdapter.ItemViewHolder holder);
    }

}

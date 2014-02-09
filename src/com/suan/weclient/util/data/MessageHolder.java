package com.suan.weclient.util.data;

import com.suan.weclient.util.net.UploadHelper;
import com.suan.weclient.util.net.WeChatLoader;

import java.util.ArrayList;

public class MessageHolder {

    private String latestMsgId = "";
    private ArrayList<MessageBean> messageBeans;
    private UserBean nowBean;
    private int nowMessageMode = WeChatLoader.GET_MESSAGE_ALL;
    private int contentMessageMode = WeChatLoader.GET_MESSAGE_ALL;

    private boolean holderEmpty = true;
    private int index = -1;

    /*
    about file upload
     */
    private UploadHelper uploadHelper;


    public MessageHolder(UserBean userBean, int index) {
        this.index = index;
        nowBean = userBean;
        messageBeans = new ArrayList<MessageBean>();
        addEmptyMessage();
        uploadHelper = new UploadHelper();

    }


    private void addEmptyMessage() {
        MessageBean emptyMessage = new MessageBean();
        emptyMessage.setType(MessageBean.MESSAGE_TYPE_EMPTY);
        messageBeans.add(emptyMessage);  }

    public void setNowMessageMode(int mode) {
        nowMessageMode = mode;

    }

    public int getNowMessageMode() {
        return nowMessageMode;
    }


    public void setContentMessageMode(int mode) {
        contentMessageMode = mode;

    }

    public int getContentMessageMode() {
        return contentMessageMode;
    }


    public ArrayList<MessageBean> getMessageList() {
        return messageBeans;
    }

    public void addMessage(ArrayList<MessageBean> nowArrayList) {
        for (int i = 0; i < nowArrayList.size(); i++) {
            messageBeans.add(nowArrayList.get(i));
        }

    }

    public void setMessage(ArrayList<MessageBean> nowArrayList) {
        this.messageBeans = nowArrayList;

    }

    public void setLatestMsgId(String id) {
        latestMsgId = id;
    }

    public String getLatestMsgId() {
        return latestMsgId;
    }

    public UserBean getUserBean() {

        return nowBean;
    }

    public boolean getHolderEmpty() {
        return holderEmpty;
    }

    public void setHolderEmpty(boolean holderEmpty) {
        this.holderEmpty = holderEmpty;
    }


    public UploadHelper getUploadHelper(){
        return uploadHelper;
    }

}
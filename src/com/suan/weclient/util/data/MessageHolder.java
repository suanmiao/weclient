package com.suan.weclient.util.data;

import com.suan.weclient.util.net.WeChatLoader;

import java.util.ArrayList;

public class MessageHolder{
	
	private String latestMsgId = "";
	private ArrayList<MessageBean> messageBeans;
	private UserBean nowBean;
    private int nowMessageMode = WeChatLoader.GET_MESSAGE_ALL;
	public MessageHolder(UserBean userBean){
		nowBean = userBean;
		messageBeans = new ArrayList<MessageBean>();
		
	}

    public void setNowMessageMode(int mode){
        nowMessageMode = mode;

    }
    public int getNowMessageMode(){
        return nowMessageMode;
    }


	
	public ArrayList<MessageBean> getMessageList(){
		return messageBeans;
	}
	
	public void addMessage(ArrayList<MessageBean> nowArrayList){
		for(int i = 0;i<nowArrayList.size();i++){
			messageBeans.add(nowArrayList.get(i));
		}
	}
	
	public void setMessage(ArrayList<MessageBean> nowArrayList){
		this.messageBeans = nowArrayList;
	}
	
	public void setLatestMsgId(String id){
		latestMsgId = id;
	}
	
	public String getLatestMsgId(){
		return latestMsgId;
	}
	
	public UserBean getUserBean(){
		return nowBean;
	}
}
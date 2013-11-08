package com.suan.weclient.util;

import java.util.ArrayList;

public class MessageHolder{
	
	private String latestMsgId = "";
	private ArrayList<MessageItem> messageItems;
	private UserBean nowBean;
	public MessageHolder(UserBean userBean){
		nowBean = userBean;
		messageItems = new ArrayList<MessageItem>();
		
	}
	
	public ArrayList<MessageItem> getMessageList(){
		return messageItems;
	}
	
	public void addMessage(ArrayList<MessageItem> nowArrayList){
		for(int i = 0;i<nowArrayList.size();i++){
			messageItems.add(nowArrayList.get(i));
		}
	}
	
	public void setMessage(ArrayList<MessageItem> nowArrayList){
		this.messageItems = nowArrayList;
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
package com.suan.weclient.util.data;

import java.util.ArrayList;

public class MessageHolder{
	
	private String latestMsgId = "";
	private ArrayList<MessageBean> messageBeans;
	private UserBean nowBean;
	public MessageHolder(UserBean userBean){
		nowBean = userBean;
		messageBeans = new ArrayList<MessageBean>();
		
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
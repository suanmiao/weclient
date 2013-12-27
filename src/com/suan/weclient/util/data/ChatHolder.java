package com.suan.weclient.util.data;

import java.util.ArrayList;

public class ChatHolder{
	
	private ArrayList<MessageBean> messageBeans;
	private UserBean nowBean;
	private String toFakeId = "";
	public ChatHolder(UserBean userBean,String toFakeId){
		nowBean = userBean;
		messageBeans = new ArrayList<MessageBean>();
		this.toFakeId = toFakeId;
		
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
	
	
	public UserBean getUserBean(){
		return nowBean;
	}
	
	public String getToFakeId(){
		return toFakeId;
	}
}
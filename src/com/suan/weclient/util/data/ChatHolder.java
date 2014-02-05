package com.suan.weclient.util.data;

import java.util.ArrayList;

public class ChatHolder{
	
	private ArrayList<MessageBean> messageBeans;
	private UserBean nowBean;
	private String toFakeId = "";

    private String toNickname = "";
	public ChatHolder(UserBean userBean,String toFakeId,String toNickname){
		nowBean = userBean;
		messageBeans = new ArrayList<MessageBean>();
		this.toFakeId = toFakeId;
        this.toNickname = toNickname;
		
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

    public String getToNickname(){
        return toNickname;
    }
}
package com.suan.weclient.util.data;

import java.util.ArrayList;

public class FansHolder {
	
	private ArrayList<FansBean> fansBeans;
	private ArrayList<FansGroupBean> fansGroupBeans;
	private int totalAmount = 0;
	private UserBean userBean;
	
	
	
	public FansHolder(UserBean userBean){
		fansBeans = new ArrayList<FansBean>();
		fansGroupBeans = new ArrayList<FansGroupBean>();
		this.userBean = userBean;
	}
	

	public void addFans(ArrayList<FansBean> nowArrayList){
		for(int i = 0;i<nowArrayList.size();i++){
			fansBeans.add(nowArrayList.get(i));
		}
	}
	
	public void setFans(ArrayList<FansBean> nowArrayList){
		this.fansBeans = nowArrayList;
	}
	
	public void setFansGroup(ArrayList<FansGroupBean> nowArrayList){
		this.fansGroupBeans = nowArrayList;
	}
	
	
	public ArrayList<FansBean> getFansBeans(){
		return fansBeans;
	}
	
	public ArrayList<FansGroupBean> getFansGroupBeans(){
		return fansGroupBeans;
	}
	
	public void setTotalAmount(int totalAmount){
		this.totalAmount = totalAmount;
	}
	
	public int getTotalAmount(){
		return totalAmount;
	}
	
	public UserBean getUserBean(){
		return userBean;
	}

}

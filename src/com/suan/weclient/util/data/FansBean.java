package com.suan.weclient.util.data;

public class FansBean {
	
	private String id = "";
	private String nick_name = "";
	private String remark_name = "";
	private String group_id = "";
	
	private String referer = "";
	
	
	public String getFansId(){
		return id;
	}
	
	public String getNickname (){
		return nick_name;
	}
	
	public String getRemarkName(){
		return remark_name;
	}
		
	public String getGoupId(){
		return group_id;
	}
	
	public void setReferer(String referer){
		this.referer = referer;
	}
	
	public String getReferer(){
		return referer;
	}

}

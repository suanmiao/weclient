package com.suan.weclient.util;



public class MessageItem {
	
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
/********************************************************************/	
	private String token = "";
	private String slave_sid = "";
	private String slave_user = "";
	private String referer = "";
	

	public String getToken(){
		return token;
	}
	
	public void setToken(String data){
		this.token = data;
	}
	
	public String getReferer(){
		return referer;
	}
	
	public void setReferer(String data){
		this.referer = data;
	}
	
	public String getSlaveSid(){
		return slave_sid;
	}
	
	public void setSlaveSid(String data){
		this.slave_sid = data;
	}

	public String getSlaveUser(){
		return slave_user;
	}
	
	public void setSlaveUser(String data){
		this.slave_user = data;
	}

	public boolean getStarred (){
		return is_starred_msg.equals("1");
	}
	public void setStarred (boolean star){

		is_starred_msg = star ? "1":"0";
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String data){
		this.id = data;
	}

	public int getType(){
		return type;
	}
	
	public void setType(int data){
		this.type = data;
	}
	
	public String getFakeId(){
		return fakeid;
	}
	
	public void setFakeId(String data){
		this.fakeid = data;
	}
	public String getNickName(){
		return nick_name;
	}
	
	public void setNickName(String data){
		this.nick_name = data;
	}
	public String getDateTime(){
		return date_time;
	}
	
	public void setDateTime(String data){
		this.date_time = data;
	}
	
	public String getContent(){
		return content;
	}
	
	public void setContent(String data){
		this.content = data;
	}


	public String getSource(){
		return source;
	}
	
	public void setSource(String data){
		this.source = data;
	}

	

	public String getPlayLength(){
		return play_length;
	}
	
	public void setPlayLength(String playLength){
		this.play_length = playLength;
	}


	public String getLength(){
		return length;
	}
	
	public void setLength(String length){
		this.length =  length;
	}


	public String getFileId(){
		return fileid;
	}
	
	public void setFileId(String data){
		this.fileid = data;
	}

	
	public String getMsgStatus(){
		return msg_status;
	}
	
	public void setMsgStatus(String data){
		this.msg_status = data;
	}

	
	
	public String getHasReply(){
		return has_reply;
	}
	
	public void setHasReply(String data){
		this.has_reply= data;
	}

	
	
	
	public String getRefuseReason(){
		return refuse_reason;
	}
	
	public void setRefuseReason(String data){
		this.refuse_reason = data;
	}


}

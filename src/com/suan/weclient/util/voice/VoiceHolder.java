package com.suan.weclient.util.voice;


public class VoiceHolder {
	
	private int playLength ;
	private int length ;
	private byte[] bytes;
	public VoiceHolder(byte[] bytes,String playLength ,String length ){
		this.playLength = Integer.parseInt(playLength);
		this.length = Integer.parseInt(length);
		this.bytes = bytes;
		
	}
	
	public int getPlayLength (){
		return playLength;
	}
	
	public int getLength(){
		return length;
	}
	
	public byte[] getBytes(){
		return bytes;
	}

}

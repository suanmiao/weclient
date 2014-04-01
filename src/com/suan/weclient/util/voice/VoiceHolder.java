package com.suan.weclient.util.voice;


public class VoiceHolder {
	
	private int playLength ;
	private int length ;
	private byte[] bytes;
	private boolean isPlaying = false;
	
	
	
	public VoiceHolder(byte[] bytes,String playLength ,String length ){
		this.playLength = Integer.parseInt(playLength);
		this.length = Integer.parseInt(length);
		this.bytes = bytes;
		
	}
	
	public void setPlaying(boolean playing){
		isPlaying = playing;
	}
	
	public boolean getPlaying(){
		return isPlaying;
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

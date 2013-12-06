package com.suan.weclient.util;

import java.util.HashMap;

import android.view.View;

public class ListCacheManager {
	
	/*
	 * why I write it
	 * 
	 * 1.the reusing of convertView always cause repeat of content
	 * 
	 * 2.set tag for view is not so elegant
	 * 
	 * 3.ram got bigger and bigger
	 * 
	 */
	
	private HashMap<String, View> contentHashMap ;
	
	public ListCacheManager(){
		contentHashMap = new HashMap<String, View>();
		
	}
	
	
	public boolean containView(String key){
		return contentHashMap.containsKey(key);
	}
	
	public View getView(String key){
		if(containView(key)){
			return contentHashMap.get(key);
		}else{
			return null;
		}
	}
	
	public void clearData(){
		contentHashMap.clear();
	}
	
	
	public void putView(View view,String key){
		contentHashMap.put(key, view);
		
	}
	
	
	

}

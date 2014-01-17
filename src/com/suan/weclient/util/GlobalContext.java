package com.suan.weclient.util;

import java.io.File;

import android.app.Application;
import android.os.Environment;

import com.suan.weclient.util.data.DataManager;

public class GlobalContext extends Application {


	private final static String APP_FILE_NAME = "/.WeClient/";

	private final static String APP_NAME = "WeClient";

	private DataManager mDataManager;


	public void onCreate() {
		super.onCreate();
		
		initData();

	}
	
	private void initData(){
		
		mDataManager = new DataManager(getApplicationContext());
		
	}
	
	public DataManager getDataManager(){
		
		return mDataManager;
		
	}
	

	public static String getSDPath() {

		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
		} else {

		}
		return sdDir.toString();
	}

	public static String getAppFilePath() {
		return getSDPath() + APP_FILE_NAME;
	}

	public static String getAppName() {
		return APP_NAME;
	}

	




}
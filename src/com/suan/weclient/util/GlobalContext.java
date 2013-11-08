package com.suan.weclient.util;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.view.Display;

import com.suan.weclient.util.net.images.ImageCacheManager;

public class GlobalContext extends Application {

	private static Context mContext;

	private final static String APP_FILE_NAME = "/.WeClient/";

	private final static String APP_NAME = "WeClient";

	

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

	public static Context getContext() {
		return mContext;
	}

	public static void setContext(Context context) {
		mContext = context;
	}

	public void onCreate() {
		super.onCreate();

	}




}
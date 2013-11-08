package com.suan.weclient.activity;

import java.io.InputStream;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.suan.weclient.R;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.WeChatLoader;
import com.suan.weclient.util.WeChatLoader.WechatExceptionListener;
import com.suan.weclient.util.WeChatLoader.WechatGetMessageImgCallBack;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.umeng.analytics.MobclickAgent;

public class ShowImgActivity extends Activity {

	Dialog loadingDialog;
	private LinearLayout mainLayout;
	private RelativeLayout bgLayout;
	private ImageView contentImageView;
	private String slaveSid, slaveUser, token, referer, msgId;


	private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	private static int DISK_IMAGECACHE_QUALITY = 100; // PNG is lossless so
														// quality is ignored
														// but must be provided
	private ImageCacheManager mImageCacheManager;
	/*
	 * 
	 * 12:too much time
	 */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		/* request no title mode */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Transparent);
		setContentView(R.layout.show_img_layout);
		initImgCache();
		initWidget();
		initIntent();
		
	}
	
	public void initImgCache(){
		
		mImageCacheManager = ImageCacheManager.getInstance();
		mImageCacheManager.init(this, this.getPackageCodePath(),
				DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
				DISK_IMAGECACHE_QUALITY);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initIntent() {

		Intent getIntent = getIntent();
		if (getIntent != null) {
			Bundle getBundle = getIntent.getExtras();
			if (getBundle != null) {

				try {

					msgId = getBundle.getString("msgId");
					slaveSid = getBundle.getString("slaveSid");
					slaveUser = getBundle.getString("slaveUser");
					token = getBundle.getString("token");
					referer = getBundle.getString("referer");

					loadImg();


				} catch (Exception exception) {
					Log.e("message parse error", "" + exception);

				}
			}

		}

	}

	private void loadImg() {
		loadingDialog.show();
		Bitmap imgBitmap = mImageCacheManager.getDiskBitmap(ImageCacheManager.CACHE_MESSAGE_CONTENT+msgId);
		if(imgBitmap == null){
			WeChatLoader.wechatGetMessageImg(
					new WechatExceptionListener() {

						@Override
						public void onError() {
							// TODO Auto-generated method stub
							loadingDialog.dismiss();

						}
					},
					new WechatGetMessageImgCallBack() {

						@Override
						public void onBack(HttpResponse response,
								ImageView imageView) {
							// TODO Auto-generated method stub
							try {
								Bitmap bitmap = BitmapFactory
										.decodeStream((InputStream) response
												.getEntity().getContent());
								mImageCacheManager.putDiskBitmap(ImageCacheManager.CACHE_MESSAGE_CONTENT+msgId, bitmap);
								imageView.setImageBitmap(bitmap);
								loadingDialog.dismiss();

							} catch (Exception exception) {

							}

						}
					}, msgId, slaveSid, slaveUser, token, referer,
					contentImageView,
					WeChatLoader.WECHAT_MESSAGE_IMG_LARGE);
			
		}else{
			
			contentImageView.setImageBitmap(imgBitmap);
			
		}

	}

	private void initWidget() {
		loadingDialog = Util.createLoadingDialog(this, "图片加载中", false);
		mainLayout = (LinearLayout)findViewById(R.id.show_img_layout);
		mainLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				
			}
		});
		bgLayout = (RelativeLayout)findViewById(R.id.show_img_bg_layout);
		bgLayout.setBackgroundColor(Color.argb(220, 0, 0, 0));
	
		contentImageView = (ImageView) findViewById(R.id.show_img_img_content);

	}

}

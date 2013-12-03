package com.suan.weclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.suan.weclient.R;
import com.suan.weclient.util.SharedPreferenceManager;

public class SplashActivity extends Activity {

	/** Called when the activity is first created. */

	public static final String JUMB_KEY_ENTER_STATE = "enterState";
	public static final int JUMB_VALUE_NONE = 0;
	public static final int JUMB_VALUE_INTENT_TO_LOGIN = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/* request no title mode */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
//		ImageView planeImageView = (ImageView)findViewById(R.id.id_plane);
//		ImageView shadowImageView = (ImageView)findViewById(R.id.id_shadow);
//		TextView splashTextView = (TextView)findViewById(R.id.id_splash_text);
//		Animation planeAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash_plane);
//		Animation shadowAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash_shadow);
//		Animation splashtextAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash_text);
//		planeImageView.startAnimation(planeAnimation);
//		shadowImageView.startAnimation(shadowAnimation);
//		splashTextView.startAnimation(splashtextAnimation);


		Handler handler = new Handler();

		handler.postDelayed(new SplashHandler(), 0);

	}

	/*
	 * user state
	 * 
	 * -1:first time enter
	 * 
	 * 0:normal
	 */

	class SplashHandler implements Runnable {

		public void run() {

			Intent jumbIntent = new Intent();
			
			int userGroupSize = SharedPreferenceManager.getUserGroup(getApplicationContext()).size();
			if(userGroupSize == 0){
				Bundle nowBundle = new Bundle();
				nowBundle.putInt(JUMB_KEY_ENTER_STATE,
						JUMB_VALUE_INTENT_TO_LOGIN);
				jumbIntent.putExtras(nowBundle);
				jumbIntent.setClass(SplashActivity.this, LoginActivity.class);
				
			}else{
				jumbIntent.setClass(SplashActivity.this, MainActivity.class);
				
			}

			startActivity(jumbIntent);
			SplashActivity.this.finish();

		}

	}

}

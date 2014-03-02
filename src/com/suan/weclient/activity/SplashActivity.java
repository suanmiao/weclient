package com.suan.weclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.suan.weclient.R;
import com.suan.weclient.util.SharedPreferenceManager;

public class SplashActivity extends Activity {

    /**
     * Called when the activity is first created.
     */

    public static final String JUMB_KEY_ENTER_STATE = "enterState";
    public static final int JUMB_VALUE_NONE = 0;
    public static final int JUMB_VALUE_INTENT_TO_LOGIN = 1;

    private int enterTime = 0;
    private ImageView wandoujiaImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* request no title mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        wandoujiaImg = (ImageView)findViewById(R.id.splash_img_wandoujia);

        Handler handler = new Handler();

        enterTime = SharedPreferenceManager.getUserEnterTime(SplashActivity.this);
        int delay = 0;
        if(enterTime<5){
            delay = 2000;
            wandoujiaImg.setVisibility(View.VISIBLE);
        }
        handler.postDelayed(new SplashHandler(), delay);

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

            if (enterTime==0) {

                jumbIntent.setClass(SplashActivity.this, IntroActivity.class);

            } else {

                int userGroupSize = SharedPreferenceManager.getUserGroup(getApplicationContext()).size();
                if (userGroupSize == 0) {
                    Bundle nowBundle = new Bundle();
                    nowBundle.putInt(JUMB_KEY_ENTER_STATE,
                            JUMB_VALUE_INTENT_TO_LOGIN);
                    jumbIntent.putExtras(nowBundle);
                    jumbIntent.setClass(SplashActivity.this, LoginActivity.class);

                } else {
                    jumbIntent.setClass(SplashActivity.this, MainActivity.class);

                }

            }

            SharedPreferenceManager.putUserEnterTime(SplashActivity.this,enterTime+1);

            startActivity(jumbIntent);
            SplashActivity.this.finish();

        }

    }

}

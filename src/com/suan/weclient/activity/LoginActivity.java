package com.suan.weclient.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.util.NetworkUtil;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.UserBean;
import com.suan.weclient.util.net.DataParser;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WeChatLoader.WechatExceptionListener;
import com.suan.weclient.util.net.WeChatLoader.WechatGetUserProfleCallBack;
import com.suan.weclient.util.net.WeChatLoader.WechatLoginCallBack;
import com.suan.weclient.util.net.WechatManager;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity {

    private Button loginButton;
    private EditText userNameEditText, passWordEditText;
    private static final int INPUT_OK = 0;
    private static final int INPUT_USER_NAME_PROBLEM = 1;
    private static final int INPUT_PASSWORD_PROBLEM = 2;
    private UserBean nowBean;

    private int jumbState = 0;

    Dialog loginDialog;

	/*
     *
	 * 12:too much time
	 */

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* request no title mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initIntent();
        initWidget();
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

        jumbState = SplashActivity.JUMB_VALUE_NONE;
        Intent getIntent = getIntent();
        if (getIntent != null) {
            Bundle getBundle = getIntent.getExtras();
            if (getBundle != null) {

                jumbState = getBundle
                        .getInt(SplashActivity.JUMB_KEY_ENTER_STATE);
            }

        }

    }

    private void initWidget() {
        loginDialog = Util.createLoadingDialog(this, "登录", WechatManager.DIALOG_POP_NO);
        userNameEditText = (EditText) findViewById(R.id.login_edit_text_user_id);
        passWordEditText = (EditText) findViewById(R.id.login_edit_text_pass_word);
        loginButton = (Button) findViewById(R.id.login_button_login);
        loginButton.setOnClickListener(new loginClickListener());

    }

    private class loginClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (NetworkUtil.getNetworkType(getApplicationContext()) != NetworkUtil.NOCONNECTION) {

                int checkResult = checkInput();
                switch (checkResult) {
                    case INPUT_OK:

                        break;
                    case INPUT_USER_NAME_PROBLEM:
                        Toast.makeText(getApplicationContext(), "请输入正确的用户名",
                                Toast.LENGTH_LONG).show();
                        return;

                    case INPUT_PASSWORD_PROBLEM:

                        Toast.makeText(getApplicationContext(), "请输入密码",
                                Toast.LENGTH_LONG).show();
                        return;

                }
                if (SharedPreferenceManager.containUser(
                        getApplicationContext(), userNameEditText.getText()
                        .toString())) {
                    Toast.makeText(LoginActivity.this,
                            "此账户已经添加，若想要重新添加请先删除该账户", Toast.LENGTH_LONG).show();
                } else {
                    if (!Util.isNetConnected(LoginActivity.this)) {
                        loginDialog.dismiss();

                        loginDialog = Util.createEnsureDialog(
                                new DataManager.DialogSureClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(android.provider.Settings.ACTION_SOUND_SETTINGS);
                                        startActivity(intent);
                                        finish();

                                    }
                                }, true, LoginActivity.this, "网络","无网络连接，进入设置网络？", true);
                        loginDialog.show();

                    } else {

                        login();
                    }

                }

            }
        }
    }

    private void login() {
        loginDialog = Util.createLoadingDialog(this, "登录", WechatManager.DIALOG_POP_NO);
        loginDialog.show();

        WeChatLoader.wechatLogin(new WechatExceptionListener() {

                                     @Override
                                     public void onError() {
                                         // TODO Auto-generated method stub
                                         loginDialog.dismiss();

                                         loginDialog = Util.createEnsureDialog(
                                                 new DataManager.DialogSureClickListener() {

                                                     @Override
                                                     public void onClick(View v) {
                                                         // TODO Auto-generated method stub
                                                         login();

                                                     }
                                                 }, false, LoginActivity.this, "网络","网络错误，重试？", true);
                                         loginDialog.show();

                                     }
                                 }, new WechatLoginCallBack() {

                                     @Override
                                     public void onBack(String strResult, String slaveSid,
                                                        String slaveUser) {
                                         // TODO Auto-generated method stub
                                         try {

                                             nowBean = new UserBean(userNameEditText.getText()
                                                     .toString(), WeChatLoader
                                                     .getMD5Str(passWordEditText.getText().toString()));
                                             nowBean.setSlaveSid(slaveSid);
                                             nowBean.setSlaveUser(slaveUser);
                                             int loginResult = DataParser.parseLogin(nowBean,
                                                     strResult, slaveSid, slaveUser,
                                                     getApplicationContext());
                                             switch (loginResult) {
                                                 case DataParser.PARSE_LOGIN_SUCCESS:

                                                     WeChatLoader.wechatGetUserProfile(
                                                             new WechatExceptionListener() {

                                                                 @Override
                                                                 public void onError() {
                                                                     // TODO Auto-generated method stub

                                                                 }
                                                             }, new WechatGetUserProfleCallBack() {

                                                                 @Override
                                                                 public void onBack(String strResult,
                                                                                    String referer) {
                                                                     // TODO Auto-generated method stub

                                                                     try {

                                                                         int getUserProfileState = DataParser
                                                                                 .parseUserProfile(strResult,
                                                                                         nowBean);

                                                                         switch (getUserProfileState) {
                                                                             case DataParser.GET_USER_PROFILE_SUCCESS:
                                                                                 // Log.e("fuck userbean",
                                                                                 // nowBean.getNickname()+"|"+nowBean.getNewMessage()+"|"+nowBean.getNewPeople());
                                                                                 // Log.e("userbean conternt",
                                                                                 // nowBean.toString());

                                                                                 SharedPreferenceManager
                                                                                         .insertUser(
                                                                                                 getApplicationContext(),
                                                                                                 nowBean);
                                                                                 loginDialog.dismiss();
                                                                                 switch (jumbState) {
                                                                                     case SplashActivity.JUMB_VALUE_INTENT_TO_LOGIN:
                                                                                         Intent jumbIntent = new Intent();
                                                                                         jumbIntent.setClass(
                                                                                                 LoginActivity.this,
                                                                                                 MainActivity.class);
                                                                                         startActivity(jumbIntent);

                                                                                         break;

                                                                                     case SplashActivity.JUMB_VALUE_NONE:

                                                                                         LoginActivity.this
                                                                                                 .setResult(RESULT_OK);
                                                                                         break;
                                                                                 }
                                                                                 finish();

                                                                                 break;

                                                                             case DataParser.GET_USER_PROFILE_FAILED:
                                                                                 loginDialog.dismiss();

                                                                                 loginDialog = Util
                                                                                         .createEnsureDialog(
                                                                                                 new DataManager.DialogSureClickListener() {

                                                                                                     @Override
                                                                                                     public void onClick(
                                                                                                             View v) {
                                                                                                         // TODO
                                                                                                         // Auto-generated
                                                                                                         // method
                                                                                                         // stub
                                                                                                         login();

                                                                                                     }
                                                                                                 },
                                                                                                 false,
                                                                                                 LoginActivity.this,
                                                                                                 "错误","登录失败，重试？",
                                                                                                 true);
                                                                                 loginDialog.show();

                                                                                 break;
                                                                         }

                                                                     } catch (Exception exception) {

                                                                         Log.e("get user profile result error",
                                                                                 "" + exception);
                                                                     }

                                                                 }
                                                             }, nowBean
                                                     );

                                                     break;

                                                 case DataParser.PARSE_LOGIN_FAILED:

                                                     loginDialog.dismiss();
//                        Log.e("dialog login","dismiss");

                                                     loginDialog = Util.createEnsureDialog(
                                                             new DataManager.DialogSureClickListener() {

                                                                 @Override
                                                                 public void onClick(View v) {
                                                                     // TODO Auto-generated method stub
                                                                     loginDialog.dismiss();

                                                                 }
                                                             }, false, LoginActivity.this,"错误", "登录失败，请检查账户名和密码",
                                                             true);

                                                     loginDialog.show();
                                                     break;
                                             }

                                         } catch (Exception exception) {

                                         }
                                     }
                                 }, userNameEditText.getText().toString(), WeChatLoader
                .getMD5Str(passWordEditText.getText().toString()), "", "json"
        );
    }

    private int checkInput() {
        int result = INPUT_OK;

        String userName = userNameEditText.getText().toString();
        if (userName.length() < 1) {
            return INPUT_USER_NAME_PROBLEM;
        }
        String passWord = passWordEditText.getText().toString();
        if (passWord.length() < 1) {
            return INPUT_PASSWORD_PROBLEM;
        }

        return result;

    }

}

package com.suan.weclient.activity;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.suan.weclient.R;
import com.suan.weclient.fragment.ContentFragment;
import com.suan.weclient.fragment.ContentFragment.MyPageChangeListener;
import com.suan.weclient.fragment.LeftFragment;
import com.suan.weclient.fragment.UserListFragment;
import com.suan.weclient.pushService.AlarmReceiver;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.AutoLoginListener;
import com.suan.weclient.util.data.DataManager.DialogListener;
import com.suan.weclient.util.data.DataManager.DialogSureClickListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.data.UserGoupPushHelper;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.view.actionbar.CustomMainActionView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Conversation.SyncListener;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainActivity extends SlidingFragmentActivity {

    public static final String BROADCAST_ACTION_REFRESH_MESSAGE = "cn.com.action.suan.refreshMessage";

    private GlobalContext mGlobalContext;
    LeftFragment leftFragment;
    ContentFragment contentFragment;
    SlidingMenu mSlidingMenu;
    private ActionBar actionBar;


    /*
    about broadcast
     */

    private BroadcastReceiver mReceiver;

    /*
     * about pop dialog
     */
    private TextView popContentTextView;
    private TextView popTitleTextView;
    private EditText popContentEditText;
    private TextView popTextAmountTextView;
    private Button popCancelButton, popSureButton;

    private FeedbackAgent agent;
    private Conversation defaultConversation;

    private DataManager mDataManager;
    private Dialog popDialog;
    private Dialog replyDialog;


    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        initReceiver();
        initDataChangeListener();
        initCache();
        initSlidingMenu();
        initWidgets();

        initActionBar();

        initListener(contentFragment);

        initUmeng();
        boolean networkConnected = Util.isNetConnected(MainActivity.this);
        if (networkConnected) {
            startLoad();
        }


    }

    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                /*
                receive the broadcast ,
                refresh the profile to show new message count
                 */
                mDataManager.getWechatManager().getUserProfile(WechatManager.DIALOG_POP_NO, false, mDataManager.getCurrentPosition(), new OnActionFinishListener() {
                    @Override
                    public void onFinish(int code, Object object) {

                    }
                });

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_REFRESH_MESSAGE);
        registerReceiver(mReceiver, filter);
    }


    public void onNewIntent(Intent intent) {

        //analyse intent
        int getCurrentIndex = intent.getIntExtra("currentIndex", -1);
        if (getCurrentIndex != -1) {
            //get the current value
            if (getCurrentIndex != mDataManager.getCurrentPosition()) {
                if (mDataManager.setCurrentPosition(getCurrentIndex)) {
                    autoLogin();
                }
            }

        }
        super.onNewIntent(intent);
    }


    private void initService() {
        if (SharedPreferenceManager.getPushEnable(this)) {
            String alarmServicePath = "com.suan.weclient.pushService.AlarmSysService";
            boolean alarmServiceRunning = Util.isServiceRunning(this, alarmServicePath);
            if (!alarmServiceRunning) {
                Intent startServiceIntent = new Intent();
                startServiceIntent.setAction(AlarmReceiver.BROADCAST_ACTION_START_PUSH);
                sendBroadcast(startServiceIntent);

            }

        }
    }

    private void initSlidingMenu() {

        // set the Behind View
        setBehindContentView(R.layout.left_frame);
        setContentView(R.layout.main);
        setBehindContentView(R.layout.left_frame);

        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setSecondaryMenu(R.layout.right_frame);

        FragmentTransaction t = this.getSupportFragmentManager()
                .beginTransaction();

        leftFragment = new LeftFragment(this.getSupportFragmentManager(),
                mDataManager);

        t.replace(R.id.left_frame, leftFragment);

        contentFragment = new ContentFragment(mDataManager);
        t.replace(R.id.content_layout, contentFragment);

        t.commit();

    }

    private void initWidgets() {

    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        CustomMainActionView customMainActionView = new CustomMainActionView(this);
        customMainActionView.init(mDataManager);

        customMainActionView.setShowMenuListener(new ShowMenuListener() {

            @Override
            public void showLeftMenu() {
                // TODO Auto-generated method stub
                getSlidingMenu().showMenu();

            }

            public void showRightMenu() {
                getSlidingMenu().showSecondaryMenu();

            }
        });

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customMainActionView, layoutParams);


    }

    private void initUmeng() {

        agent = new FeedbackAgent(this);
        defaultConversation = agent.getDefaultConversation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                UmengUpdateAgent.setUpdateAutoPopup(true);
                UmengUpdateAgent
                        .setDialogListener(new UmengDialogButtonListener() {

                            @Override
                            public void onClick(int arg0) {
                                // TODO Auto-generated method stub

                            }
                        });
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus,
                                                 UpdateResponse updateInfo) {
                        switch (updateStatus) {
                            case 0: // has update

                                break;
                            case 1: // has no update
                                break;
                            case 2: // none wifi
                                break;
                            case 3: // time out
                                break;
                        }
                    }
                });
                UmengUpdateAgent.update(MainActivity.this);

            }

        }, 1000);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                defaultConversation.sync(new SyncListener() {

                    @Override
                    public void onSendUserReply(List<Reply> arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onReceiveDevReply(List<DevReply> arg0) {
                        // TODO Auto-generated method stub
                        String replyString = "";
                        /*
                         * fuck umeng the arg0 might be null
						 */
                        try {
                            for (int i = 0; i < arg0.size(); i++) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat(
                                        "MM-dd HH:mm");
                                replyString += dateFormat.format(arg0.get(i)
                                        .getDatetime());
                                replyString += ":  ";
                                replyString += arg0.get(i).getContent();
                                replyString += "\n";

                            }
                            if (arg0.size() > 0) {
                                dialogShowDevReply(replyString);
                            }

                        } catch (Exception exception) {

                        }

                    }
                });

            }
        }, 1000);

    }

    public void dialogShowDevReply(String content) {

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_dev_reply_layout,
                null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_dev_reply_text_title);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_dev_reply_button_reply);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_dev_reply_button_o);

        popContentTextView = (TextView) dialogView
                .findViewById(R.id.dialog_dev_reply_text_content);
        popContentTextView.setText(content);

        popTitleTextView.setText("开发者回复:");
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                replyDialog.dismiss();
                popFeedback();
            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                replyDialog.dismiss();

            }
        });

        replyDialog = new Dialog(this, R.style.dialog);

        replyDialog.setContentView(dialogView);
        replyDialog.show();
    }

    private void popFeedback() {

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_feedback_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_feedback_text_title);

        popContentEditText = (EditText) dialogView
                .findViewById(R.id.dialog_feedback_edit_text);
        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_feedback_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_feedback_button_cancel);

        popTextAmountTextView = (TextView) dialogView
                .findViewById(R.id.dialog_feedback_text_num);
        popTextAmountTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popContentEditText.setText("");

            }
        });

        popContentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                popTextAmountTextView.setTextColor(Color.rgb(0, 0, 0));
                popTextAmountTextView.setText(popContentEditText.getText()
                        .length() + " x");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        popTitleTextView.setText("反馈");
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String content = popContentEditText.getEditableText()
                        .toString();
                defaultConversation.addUserReply(content);
                replyDialog.dismiss();

                mDataManager.doLoadingStart("反馈发送中...", WechatManager.DIALOG_POP_CANCELABLE);

                sync();
            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                replyDialog.cancel();

            }
        });

        replyDialog = new Dialog(this, R.style.dialog);

        replyDialog.setContentView(dialogView);
        replyDialog.show();

    }

    private void sync() {
        Conversation.SyncListener listener = new Conversation.SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {
                popContentEditText.setText("");
                mDataManager.doLoadingEnd();

                Toast.makeText(MainActivity.this, "反馈发送成功!", Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onReceiveDevReply(List<DevReply> replyList) {
            }
        };
        defaultConversation.sync(listener);
    }

    public void onStart() {

        initService();
        boolean networkConnected = Util.isNetConnected(MainActivity.this);
        if (networkConnected) {

        } else {
            gotoNetworkSetting();

        }

        super.onStart();

    }

    private void gotoNetworkSetting() {

        Handler popHandler = new Handler();
        popHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mDataManager.doPopEnsureDialog(true, false, "网络", "无网络连接，进入设置开启网络？", new DialogSureClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }, 500);


    }

    private void startLoad() {

        autoLogin();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onResume() {

        SharedPreferenceManager.putActivityRunning(this, false);
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {

        SharedPreferenceManager.putActivityRunning(this, false);
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void onDestroy() {

        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void initListener(final ContentFragment fragment) {

        fragment.setMyPageChangeListener(new MyPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (fragment.isFirst()) {
                    mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                } else if (fragment.isEnd()) {
                    mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                } else {
                    // mSlidingMenu.setCanSliding(false, false);
                }
            }
        });
    }

    private void initDataChangeListener() {

        mGlobalContext = ((GlobalContext) getApplicationContext());
        mDataManager = mGlobalContext.getDataManager();
        mDataManager.updateUserGroup();

        mDataManager.addAutoLoginListener(new AutoLoginListener() {

            @Override
            public void autoLogin() {
                // TODO Auto-generated method stub
                MainActivity.this.autoLogin();

            }

            public void onAutoLoginEnd() {

            }
        });
        mDataManager.addUserGroupListener(new UserGroupListener() {

            @Override
            public void onGroupChangeEnd() {
                // TODO Auto-generated method stub
                mDataManager.getUserListControlListener().onUserListDismiss();
                int userAmount = mDataManager.getUserGroup().size();
                if (userAmount == 0) {

                    popLoginEnsure();
                }

            }

            @Override
            public void deleteUser(int index) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAddUser() {
                // TODO Auto-generated method stub

            }
        });
        mDataManager.addMessageChangeListener(new DataManager.MessageChangeListener() {
            @Override
            public void onMessageGet() {

                UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(MainActivity.this));
                userGoupPushHelper.updateUserGroup(mDataManager);

                mDataManager.saveUserGroup(MainActivity.this);

                userGoupPushHelper.getUserHolders().get(mDataManager.getCurrentPosition()).setLastNewMessageCount(0);

                userGoupPushHelper.getUserHolders().get(mDataManager.getCurrentPosition()).setLastMsgId(mDataManager.getCurrentUser().getLastMsgId());

                SharedPreferenceManager.putPushUserGroup(MainActivity.this, userGoupPushHelper.getString());

            }
        });
        mDataManager.setLoadingListener(new DialogListener() {

            @Override
            public void onLoad(String loaingText, int dialogCancelType) {
                // TODO Auto-generated method stub

                if (MainActivity.this != null
                        && !MainActivity.this.isFinishing()) {

                    if (popDialog != null) {
                        popDialog.dismiss();

                    }

                    popDialog = Util.createLoadingDialog(MainActivity.this,
                            loaingText, dialogCancelType);
                    popDialog.show();

                }

            }

            @Override
            public void onFinishLoad() {
                // TODO Auto-generated method stub
                if (popDialog != null) {

                    popDialog.dismiss();
                    popDialog = null;
                }

            }

            @Override
            public void onPopEnsureDialog(boolean cancelVisible,
                                          boolean cancelable, String titleText, String contentText,
                                          DialogSureClickListener dialogSureClickListener) {
                // TODO Auto-generated method stub

                try {
                    if (popDialog != null) {
                        popDialog.dismiss();
                    }
                    popDialog = Util.createEnsureDialog(
                            dialogSureClickListener, cancelVisible,
                            MainActivity.this, titleText, contentText, true);

                    popDialog.show();

                } catch (Exception exception) {
                    Log.e("pop ensure error", "" + exception);

                }

            }

            @Override
            public void onDismissAllDialog() {
                // TODO Auto-generated method stub

                if (popDialog != null) {
                    popDialog.dismiss();
                    popDialog = null;
                }
            }
        });
    }

    private void popLoginEnsure() {

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_ensure_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_ensure_text_title);
        popContentTextView = (TextView) dialogView.findViewById(R.id.dialog_ensure_text_content);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_ensure_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_ensure_button_cancel);

        popTitleTextView.setText("跳转到登录");
        popContentTextView.setText("你的账户列表目前为空，跳转到登录页面？");
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                popDialog.cancel();
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(MainActivity.this, LoginActivity.class);
                startActivityForResult(jumbIntent,
                        UserListFragment.START_ACTIVITY_LOGIN);


            }
        });
        popCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popDialog.cancel();

            }
        });

        popDialog = new Dialog(MainActivity.this, R.style.dialog);

        popDialog.setContentView(dialogView);
        popDialog.show();

    }


    private long lastBackKeyTouchTime = 0;

    @SuppressLint("ShowToast")
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - lastBackKeyTouchTime < 3000) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "再按一次返回键退出应用",
                        Toast.LENGTH_SHORT).show();
                lastBackKeyTouchTime = System.currentTimeMillis();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initCache() {
        mDataManager.createImageCache(getApplicationContext());

    }

    private void autoLogin() {

        if (mDataManager.getUserGroup().size() <= 0) {
            return;
        }

        mDataManager.getWechatManager().login(
                mDataManager.getCurrentPosition(), WechatManager.DIALOG_POP_NOT_CANCELABLE, true,
                new OnActionFinishListener() {


                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub
                        mDataManager.getWechatManager().getUserProfile(WechatManager.DIALOG_POP_CANCELABLE, true,
                                mDataManager.getCurrentPosition(),
                                new OnActionFinishListener() {

                                    @Override
                                    public void onFinish(int code, Object object) {
                                        // TODO Auto-generated method stub
                                        String referer = (String) object;

                                        mDataManager
                                                .getWechatManager()
                                                .getUserImgWithReferer(
                                                        mDataManager
                                                                .getCurrentPosition(),
                                                        WechatManager.DIALOG_POP_CANCELABLE,
                                                        null,
                                                        new OnActionFinishListener() {


                                                            @Override
                                                            public void onFinish(
                                                                    int code, Object object) {
                                                                // TODO
                                                                // Auto-generated
                                                                // method stub

                                                            }
                                                        }, referer);
                                        mDataManager
                                                .getWechatManager()
                                                .getMassData(
                                                        mDataManager
                                                                .getCurrentPosition(),
                                                        WechatManager.DIALOG_POP_CANCELABLE,
                                                        new OnActionFinishListener() {


                                                            @Override
                                                            public void onFinish(
                                                                    int code, Object object) {
                                                                // TODO
                                                                // Auto-generated
                                                                // method stub

                                                                mDataManager.doMassDataGet(mDataManager.getCurrentUser());


                                                                mDataManager
                                                                        .getWechatManager()
                                                                        .getNewMessageList(
                                                                                WechatManager.DIALOG_POP_CANCELABLE,
                                                                                mDataManager
                                                                                        .getCurrentPosition(),
                                                                                new OnActionFinishListener() {


                                                                                    @Override
                                                                                    public void onFinish(
                                                                                            int code, Object object) {
                                                                                        // TODO
                                                                                        // Auto-generated
                                                                                        // method
                                                                                        // stub
                                                                                        mDataManager
                                                                                                .doMessageGet();
                                                                                        mDataManager
                                                                                                .doAutoLoginEnd();

                                                                                    }
                                                                                });
                                                            }
                                                        });

                                    }
                                });

                    }
                });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserListFragment.START_ACTIVITY_LOGIN) {
            if (resultCode == RESULT_OK) {
                mDataManager.updateUserGroup();
                mDataManager.doAddUser();
                mDataManager.doGroupChangeEnd();

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public interface ShowMenuListener {
        public void showLeftMenu();

        public void showRightMenu();

    }

}

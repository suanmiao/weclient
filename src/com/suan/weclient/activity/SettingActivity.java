package com.suan.weclient.activity;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.suan.weclient.R;
import com.suan.weclient.pushService.AlarmReceiver;
import com.suan.weclient.pushService.PushService;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.data.Constants;
import com.suan.weclient.util.data.DataManager;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class SettingActivity extends SherlockActivity {

    private ActionBar actionBar;
    private TextView titleTextView;

    private ImageView backButton;

    private DataManager mDataManager;
    private RelativeLayout pushEnableLayout, pushNewMessageLayout, pushNewPeopleLayout, pushFrequentLayout, shareLayout, aboutUsLayout, feedbackLayout;
    private RelativeLayout pushEnableCheckboxLayout, pushNewMessageCheckboxLayout, pushNewPeopleCheckboxLayout;
    private RelativeLayout pushFreFastCheckboxLayout, pushFreNormalCheckboxLayout, pushFreSlowCheckboxLayout;
    private TextView pushFrequentTextView;


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

    private Dialog replyDialog;


    /*
    about wechat
     */
    private IWXAPI api;

    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.setting_layout);

        initWidgets();
        initData();
        initUmeng();
        initWechat();
        initActionBar();
        initListener();

    }

    private void initWidgets() {
        pushEnableLayout = (RelativeLayout) findViewById(R.id.setting_layout_push);
        pushFrequentLayout = (RelativeLayout) findViewById(R.id.setting_layout_push_frequent);

        pushEnableCheckboxLayout = (RelativeLayout) findViewById(R.id.setting_layout_checkbox_push);

        pushNewMessageLayout = (RelativeLayout) findViewById(R.id.setting_layout_push_new_message);
        pushNewMessageCheckboxLayout = (RelativeLayout) findViewById(R.id.setting_layout_new_message_checkbox);

        pushNewMessageCheckboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean pushNewMessageEnable = SharedPreferenceManager.getPushNewMessageEnable(SettingActivity.this);

                boolean pushNewPeopleEnable = SharedPreferenceManager.getPushNewPeopleEnable(SettingActivity.this);

                if (pushNewMessageEnable) {
                    pushNewMessageCheckboxLayout.setSelected(false);
                    SharedPreferenceManager.putPustNewMessageEnable(SettingActivity.this,false);
                    if(!pushNewPeopleEnable){
                        SharedPreferenceManager.putPustEnable(SettingActivity.this,false);
                        setPushLayout();
                    }

                } else {

                    pushNewMessageCheckboxLayout.setSelected(true);
                    SharedPreferenceManager.putPustNewMessageEnable(SettingActivity.this,true);
                }

            }
        });

        pushNewPeopleLayout = (RelativeLayout) findViewById(R.id.setting_layout_push_new_people);
        pushNewPeopleCheckboxLayout = (RelativeLayout) findViewById(R.id.setting_layout_new_people_checkbox);

        pushNewPeopleCheckboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean pushNewMessageEnable = SharedPreferenceManager.getPushNewMessageEnable(SettingActivity.this);

                boolean pushNewPeopleEnable = SharedPreferenceManager.getPushNewPeopleEnable(SettingActivity.this);

                if (pushNewPeopleEnable) {
                    pushNewPeopleCheckboxLayout.setSelected(false);
                    SharedPreferenceManager.putPushNewPeopleEnable(SettingActivity.this, false);
                    if(!pushNewMessageEnable){
                        SharedPreferenceManager.putPustEnable(SettingActivity.this,false);
                        setPushLayout();
                    }

                } else {

                    pushNewPeopleCheckboxLayout.setSelected(true);
                    SharedPreferenceManager.putPushNewPeopleEnable(SettingActivity.this, true);
                }

            }
        });
        pushFrequentTextView = (TextView) findViewById(R.id.setting_text_push_frequent);


        pushFreFastCheckboxLayout = (RelativeLayout) findViewById(R.id.setting_layout_checkbox_fast);

        pushFreNormalCheckboxLayout = (RelativeLayout) findViewById(R.id.setting_layout_checkbox_normal);

        pushFreSlowCheckboxLayout = (RelativeLayout) findViewById(R.id.setting_layout_checkbox_slow);
         setPushLayout();

        pushEnableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pushEnable = SharedPreferenceManager.getPushEnable(SettingActivity.this);
                if (pushEnable) {
                    SharedPreferenceManager.putPustEnable(SettingActivity.this, false);

                    Intent stopPushIntent = new Intent(SettingActivity.this, AlarmReceiver.class);
                    stopPushIntent.setAction(AlarmReceiver.BROADCAST_ACTION_STOP_PUSH);

                    sendBroadcast(stopPushIntent);

                    setPushLayout();

                } else {
                    SharedPreferenceManager.putPustEnable(SettingActivity.this, true);

                    Intent startPush = new Intent(SettingActivity.this, AlarmReceiver.class);
                    startPush.setAction(AlarmReceiver.BROADCAST_ACTION_START_PUSH);

                    sendBroadcast(startPush);
                    setPushLayout();

                }


            }
        });
       pushFreFastCheckboxLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!SharedPreferenceManager.getPushEnable(SettingActivity.this)) {
                    return;

                }
                SharedPreferenceManager.putPushFrequent(SettingActivity.this, PushService.PUSH_FREQUENT_FAST);
                pushFreFastCheckboxLayout.setSelected(true);
                pushFreNormalCheckboxLayout.setSelected(false);
                pushFreSlowCheckboxLayout.setSelected(false);

            }
        });


        pushFreNormalCheckboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferenceManager.getPushEnable(SettingActivity.this)) {
                    return;

                }

                SharedPreferenceManager.putPushFrequent(SettingActivity.this, PushService.PUSH_FREQUENT_NORMAL);
                pushFreFastCheckboxLayout.setSelected(false);
                pushFreNormalCheckboxLayout.setSelected(true);
                pushFreSlowCheckboxLayout.setSelected(false);

            }
        });


        pushFreSlowCheckboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferenceManager.getPushEnable(SettingActivity.this)) {
                    return;

                }

                SharedPreferenceManager.putPushFrequent(SettingActivity.this, PushService.PUSH_FREQUENT_SLOW);
                pushFreFastCheckboxLayout.setSelected(false);
                pushFreNormalCheckboxLayout.setSelected(false);
                pushFreSlowCheckboxLayout.setSelected(true);

            }
        });
        setPushLayout();


        aboutUsLayout = (RelativeLayout) findViewById(R.id.setting_layout_about_us);

        aboutUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(SettingActivity.this, AboutActivity.class);
                startActivity(jumbIntent);
            }
        });

        feedbackLayout = (RelativeLayout) findViewById(R.id.setting_layout_feedback);
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFeedback();
            }
        });


        shareLayout = (RelativeLayout) findViewById(R.id.setting_layout_share);
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareToFriends();

            }
        });

    }


    private void initWechat() {

        //reg to wx
        api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_APPID, true);
        api.registerApp(Constants.WECHAT_APPID);
        mDataManager.setWechatShareApi(api);


    }

    private void setPushLayout() {

        int pushFrequent = SharedPreferenceManager.getPushFrequent(SettingActivity.this);
        if (!SharedPreferenceManager.getPushEnable(SettingActivity.this)) {

            pushNewMessageLayout.setSelected(true);
            pushNewMessageCheckboxLayout.setSelected(false);
            pushNewPeopleLayout.setSelected(true);
            pushNewPeopleCheckboxLayout.setSelected(false);
            pushFreFastCheckboxLayout.setSelected(false);
            pushFreNormalCheckboxLayout.setSelected(false);
            pushFreSlowCheckboxLayout.setSelected(false);
            pushFrequentLayout.setSelected(true);
            pushEnableCheckboxLayout.setSelected(false);


            return;

        }

        pushFrequentLayout.setSelected(false);
        pushEnableCheckboxLayout.setSelected(true);
        pushNewPeopleLayout.setSelected(false);
        pushNewMessageLayout.setSelected(false);

        boolean pushNewMessageEnable = SharedPreferenceManager.getPushNewMessageEnable(SettingActivity.this);
        if (pushNewMessageEnable) {
            pushNewMessageCheckboxLayout.setSelected(true);

        } else {

            pushNewMessageCheckboxLayout.setSelected(false);
        }
        boolean pushNewPeopleEnable = SharedPreferenceManager.getPushNewPeopleEnable(SettingActivity.this);

        if (pushNewPeopleEnable) {
            pushNewPeopleCheckboxLayout.setSelected(true);

        } else {

            pushNewPeopleCheckboxLayout.setSelected(false);
        }
        switch (pushFrequent) {
            case PushService.PUSH_FREQUENT_FAST:
                pushFreFastCheckboxLayout.setSelected(true);
                pushFreNormalCheckboxLayout.setSelected(false);
                pushFreSlowCheckboxLayout.setSelected(false);

                break;

            case PushService.PUSH_FREQUENT_NORMAL:
                pushFreFastCheckboxLayout.setSelected(false);
                pushFreNormalCheckboxLayout.setSelected(true);
                pushFreSlowCheckboxLayout.setSelected(false);

                break;
            case PushService.PUSH_FREQUENT_SLOW:
                pushFreFastCheckboxLayout.setSelected(false);
                pushFreNormalCheckboxLayout.setSelected(false);
                pushFreSlowCheckboxLayout.setSelected(true);

                break;
        }

    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View customActionBarView = layoutInflater.inflate(R.layout.custom_actionbar_back_with_title, null);

        backButton = (ImageView) customActionBarView.findViewById(R.id.actionbar_back_with_title_img_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        titleTextView = (TextView) customActionBarView.findViewById(R.id.actionbar_back_with_title_text_title);
        titleTextView.setText(getResources().getString(R.string.setting));

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customActionBarView, layoutParams);

    }

    private void initData() {

        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();


    }

    private void initListener() {

    }

    private void initUmeng() {

        agent = new FeedbackAgent(this);
        defaultConversation = agent.getDefaultConversation();

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
        popTextAmountTextView.setOnClickListener(new View.OnClickListener() {

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
        popSureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String content = popContentEditText.getEditableText()
                        .toString();
                defaultConversation.addUserReply(content);
                replyDialog.dismiss();


                sync();
            }
        });
        popCancelButton.setOnClickListener(new View.OnClickListener() {

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

                Toast.makeText(SettingActivity.this, "反馈发送成功!", Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onReceiveDevReply(List<DevReply> replyList) {
            }
        };
        defaultConversation.sync(listener);
    }

    private void shareToFriends() {
        String description = "分享小助手到您的朋友圈 ^_^";
        String title = "从此在手机上就能管理公众平台";
        String url = "http://www.wandoujia.com/apps/com.suan.weclient";

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        msg.thumbData = bmpToByteArray(thumb, false);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis() + "weclient");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        boolean result = mDataManager.getWechatShareApi().sendReq(req);
        Log.e("share result", "" + result);


    }


    private byte[] bmpToByteArray(final Bitmap bmp, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

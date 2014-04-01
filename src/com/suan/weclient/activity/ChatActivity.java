package com.suan.weclient.activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.suan.weclient.R;
import com.suan.weclient.adapter.ChatListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.holder.ChatHolder;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ChatItemChangeListener;
import com.suan.weclient.util.data.bean.MessageBean;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.view.ptr.PTRListview;

public class ChatActivity extends SherlockActivity {

    private ActionBar actionBar;
    private ImageView backButton;
    private TextView titleTextView;
    private PTRListview ptrListview;
    private ChatListAdapter chatListAdapter;
    private EditText contentEditText;
    private ImageButton expressionButton;
    private Button sendButton;
    private DataManager mDataManager;
    private ChatHandler chatHandler;
    private static final int INPUT_OK = 0;
    private static final int INPUT_NONE = 1;
    private static final int INPUT_TOO_LONG = 2;
    private static final int MAX_INPUT_LENGTH = 600;

    private boolean updateThreadRun = true;
    private RefreshThread refreshThread;

    private static final int HANDLER_MSG_REFRESH_LIST = 3;
    private static final int HANDLER_MSG_LOAD_LIST = 4;
    private static final int HANDLER_MSG_UPDATE_CHAT = 5;
    private long lastSendTime = 0;


    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.chat_layout);
        initWidgets();
        initData();
        //actionbar should be inited after data ,cause nickname appear on actionbar
        initActionBar();

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
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ChatActivity.this.finish();
                ChatActivity.this.overridePendingTransition(R.anim.activity_movein_from_left_anim,R.anim.activity_moveout_to_right_anim);
            }
        });

        titleTextView = (TextView) customActionBarView.findViewById(R.id.actionbar_back_with_title_text_title);
        String targetUserName = mDataManager.getChatHolder().getToNickname();
        titleTextView.setText(Util.getShortString(targetUserName, 10, 3));

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);

        actionBar.setCustomView(customActionBarView, layoutParams);


    }

    private void initWidgets() {

        ptrListview = (PTRListview) findViewById(R.id.chat_layout_list);
        ptrListview.setLoadEnable(false);
        ptrListview.setRefreshEnable(false);

        contentEditText = (EditText) findViewById(R.id.chat_edit_edit);
        expressionButton = (ImageButton) findViewById(R.id.chat_button_expression);
        sendButton = (Button) findViewById(R.id.chat_button_send);
        sendButton.setOnClickListener(new SendClickListener());

    }


    private void initData() {

        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();
        initListener();
        chatHandler = new ChatHandler();

        mDataManager.doListLoadStart();
        mDataManager.getWechatManager().getChatList(
                mDataManager.getCurrentPosition(),
                new OnActionFinishListener() {

                    @Override
                    public void onFinish(int code, Object object) {
                        // TODO Auto-generated method stub

                        Message message = new Message();
                        message.arg1 = HANDLER_MSG_REFRESH_LIST;
                        message.obj = object;
                        chatHandler.sendMessage(message);
                        mDataManager.doListLoadEnd();
                    }
                });

        chatListAdapter = new ChatListAdapter(this, mDataManager);
        ptrListview.setAdapter(chatListAdapter);
        ptrListview.setSelection(chatListAdapter.getCount() - 1);


    }

    public class ChatHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            switch (msg.arg1) {
                case HANDLER_MSG_REFRESH_LIST:

                    chatListAdapter.notifyDataSetChanged();
                    //scroll to bottom
                    ptrListview.setSelection(chatListAdapter.getCount() - 1);

                    break;

                case HANDLER_MSG_LOAD_LIST:

                    chatListAdapter.notifyDataSetChanged();

                    break;

                case HANDLER_MSG_UPDATE_CHAT:

                    mDataManager.getWechatManager().getChatList(
                            mDataManager.getCurrentPosition(),
                            new OnActionFinishListener() {

                                @Override
                                public void onFinish(int code, Object object) {
                                    // TODO Auto-generated method stub

                                    Message message = new Message();
                                    message.arg1 = HANDLER_MSG_LOAD_LIST;
                                    message.obj = object;
                                    chatHandler.sendMessage(message);

                                }
                            });
                    break;

            }

        }
    }

    private void initListener() {
        mDataManager.addChatItemChangeListenr(new ChatItemChangeListener() {

            @Override
            public void onItemGet() {
                // TODO Auto-generated method stub
            }
        });
        mDataManager.setListLoadingListener(new DataManager.ListLoadingListener() {
            @Override
            public void onLoadStart() {
                ptrListview.onRefreshStart();

            }

            @Override
            public void onLoadFinish() {
                ptrListview.onRefreshComplete();

            }
        });

    }

    public class SendClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int checkResult = checkInput();
            switch (checkResult) {
                case INPUT_NONE:
                    Toast.makeText(ChatActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();

                    break;

                case INPUT_OK:
                    lastSendTime = System.currentTimeMillis();
                    insertMessage();

                    break;
                case INPUT_TOO_LONG:

                    Toast.makeText(ChatActivity.this, "输入内容超过字数限制", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

    }


    private int checkInput() {
        int result = INPUT_OK;
        String inputString = contentEditText.getText().toString();
        if (inputString.length() == 0) {
            return INPUT_NONE;
        } else if (inputString.length() >= MAX_INPUT_LENGTH) {
            return INPUT_TOO_LONG;
        }


        return result;

    }

    private void insertMessage() {

        MessageBean sendMessage = createMessage();
        sendMessage.setSendState(MessageBean.MESSAGE_SEND_PREPARE);

        ChatHolder chatHolder = mDataManager.getChatHolder();
        //add is to the bottom
        chatHolder.getMessageList().add(sendMessage);
        chatListAdapter.notifyDataSetChanged();
        sendMessage.setSendState(MessageBean.MESSAGE_SEND_ING);
        contentEditText.setText("");
        String lastMsgId = getLastMsgId(chatHolder);


        ptrListview.setSelection(chatListAdapter.getCount() - 1);

        sendMessage.sendMessage(mDataManager, lastMsgId, mDataManager.getCurrentUser(),
                chatHolder.getToFakeId(), ChatActivity.this);


    }

    private String getLastMsgId(ChatHolder chatHolder) {
        for (int i = 0; i < chatHolder.getMessageList().size(); i++) {
            MessageBean nowMessage = chatHolder.getMessageList().get(chatHolder.getMessageList().size() - 1 - i);
            if (nowMessage.getId().length() > 1) {
                return nowMessage.getId();
            }
        }

        return "";

    }

    private MessageBean createMessage() {
        MessageBean sendMessage = new MessageBean();
        sendMessage.setType(MessageBean.MESSAGE_TYPE_TEXT);
        sendMessage.setContent(contentEditText.getText().toString());
        sendMessage.setFakeId(mDataManager.getCurrentUser().getFakeId());
        sendMessage.setNickName(mDataManager.getCurrentUser().getNickname());
        String nowTime = (System.currentTimeMillis() + "").substring(0, 10);
        sendMessage.setDateTime(nowTime);
        return sendMessage;

    }


    @Override
    public void onResume() {
        super.onResume();
        updateThreadRun = true;
        refreshThread = new RefreshThread();
        refreshThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateThreadRun = false;
    }


    public class RefreshThread extends Thread {
        public void run() {
            Looper.prepare();
            while (updateThreadRun) {
                try {
                    sleep(4000);
                } catch (Exception e) {

                }
                if (System.currentTimeMillis() - lastSendTime < 100) {
                    try {
                        sleep(1000);
                    } catch (Exception e) {

                    }

                }
                Message message = new Message();
                message.arg1 = HANDLER_MSG_UPDATE_CHAT;
                chatHandler.sendMessage(message);
            }

        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_movein_from_left_anim,R.anim.activity_moveout_to_right_anim);
    }


}

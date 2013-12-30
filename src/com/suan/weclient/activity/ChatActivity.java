package com.suan.weclient.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.adapter.ChatListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.ChatHolder;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ChatItemChangeListener;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;

public class ChatActivity extends Activity {

    private ListView mListView;
    private RelativeLayout backButton;
    private ChatListAdapter chatListAdapter;
    private EditText contentEditText;
    private ImageButton expressionButton, sendButton;
    private DataManager mDataManager;
    private ChatHandler chatHandler;
    private static final int INPUT_OK = 0;
    private static final int INPUT_NONE = 1;
    private static final int INPUT_TOO_LONG = 2;
    private static final int MAX_INPUT_LENGTH = 600;


    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat_layout);
        initWidgets();
        initData();
        initListener();

    }

    private void initWidgets() {

        mListView = (ListView) findViewById(R.id.chat_layout_list);
        backButton = (RelativeLayout)findViewById(R.id.chat_layout_back);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chatListAdapter.notifyDataSetChanged();
            }
        });
        contentEditText = (EditText) findViewById(R.id.chat_edit_edit);
        expressionButton = (ImageButton) findViewById(R.id.chat_button_expression);
        sendButton = (ImageButton) findViewById(R.id.chat_button_send);
        sendButton.setOnClickListener(new SendClickListener());

    }


    private void initData() {

        GlobalContext globalContext = (GlobalContext) getApplicationContext();
        mDataManager = globalContext.getDataManager();
        chatHandler = new ChatHandler();

        mDataManager.getWechatManager().getChatList(
                mDataManager.getCurrentPosition(),
                new OnActionFinishListener() {

                    @Override
                    public void onFinish(Object object) {
                        // TODO Auto-generated method stub

                        Message message = new Message();
                        message.obj = object;
                        chatHandler.sendMessage(message);
                    }
                });

        chatListAdapter = new ChatListAdapter(this, mDataManager);
        mListView.setAdapter(chatListAdapter);
    }

    public class ChatHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            Boolean changed = (Boolean) msg.obj;
            mDataManager.doChatItemGet(changed);

        }
    }

    private void initListener() {
        mDataManager.addChatItemChangeListenr(new ChatItemChangeListener() {

            @Override
            public void onItemGet(boolean changed) {
                // TODO Auto-generated method stub
                chatListAdapter.notifyDataSetChanged();

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
        chatHolder.getMessageList().add(sendMessage);
        chatListAdapter.notifyDataSetChanged();
        sendMessage.setSendState(MessageBean.MESSAGE_SEND_ING);
        contentEditText.setText("");
       String lastMsgId = getLastMsgId(chatHolder);
        Log.e("lastMsgId","id:"+lastMsgId);

        sendMessage.sendMessage(mDataManager,lastMsgId , mDataManager.getCurrentUser(), chatHolder.getToFakeId());


    }

    private String getLastMsgId(ChatHolder chatHolder){
         for(int i = 0;i<chatHolder.getMessageList().size();i++){
             MessageBean nowMessage = chatHolder.getMessageList().get(chatHolder.getMessageList().size()-1-i);
             if(nowMessage.getId().length()>1){
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

}

package com.suan.weclient.activity;

import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuView;
import com.suan.weclient.R;
import com.suan.weclient.adapter.ChatListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.ChatHolder;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ChatItemChangeListener;
import com.suan.weclient.util.data.MessageBean;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;

public class ChatActivity extends SherlockActivity {

    private ActionBar actionBar;
    private ImageView backButton;
    private TextView titleTextView;
    private ListView mListView;
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


    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.chat_layout);
        initActionBar();
        initWidgets();
        initData();
        initListener();

    }


    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);


        View customActionBarView = layoutInflater.inflate(R.layout.custom_actionbar_back_with_title, null);

        backButton = (ImageView)customActionBarView. findViewById(R.id.actionbar_back_with_title_img_back);
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ChatActivity.this.finish();
            }
        });

        titleTextView = (TextView)customActionBarView.findViewById(R.id.actionbar_back_with_title_text_title);
        titleTextView.setText(getResources().getString(R.string.chat));


         ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT,
                ActionMenuView.LayoutParams.MATCH_PARENT);

        actionBar.setCustomView(customActionBarView, layoutParams);


    }

    private void initWidgets() {

        mListView = (ListView) findViewById(R.id.chat_layout_list);
       contentEditText = (EditText) findViewById(R.id.chat_edit_edit);
        expressionButton = (ImageButton) findViewById(R.id.chat_button_expression);
        sendButton = (Button) findViewById(R.id.chat_button_send);
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
                    public void onFinish(int code, Object object) {
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
        Log.e("lastMsgId", "id:" + lastMsgId);

        sendMessage.sendMessage(mDataManager, lastMsgId, mDataManager.getCurrentUser(), chatHolder.getToFakeId());


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

}

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
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.adapter.ChatListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.ChatItemChangeListener;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WeChatLoader.WechatExceptionListener;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;

public class ChatActivity extends Activity {

	private ListView mListView;
	private ChatListAdapter chatListAdapter;
	private EditText contentEditText;
	private ImageButton expressionButton,sendButton;
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
		contentEditText = (EditText)findViewById(R.id.chat_edit_edit);
		expressionButton = (ImageButton)findViewById(R.id.chat_button_expression);
		sendButton = (ImageButton)findViewById(R.id.chat_button_send);
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
	
	public class SendClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int checkResult = checkInput();
			switch(checkResult){
			case INPUT_NONE:
				Toast.makeText(ChatActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
				
				break;
				
			case INPUT_OK:
				sendContent();
				
				break;
			case INPUT_TOO_LONG:
				
				Toast.makeText(ChatActivity.this, "输入内容超过字数限制", Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
		
	}
	
	private void sendContent(){
		
		mDataManager.getWechatManager().singleChat(mDataManager.getCurrentPosition(), 1, contentEditText.getText().toString(), mDataManager.getChatHolder(), new OnActionFinishListener() {
			
			@Override
			public void onFinish(Object object) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	private int checkInput(){
		int result = INPUT_OK;
		String inputString = contentEditText.getText().toString();
		if(inputString.length()==0){
			return INPUT_NONE;
		}else if(inputString.length()>=MAX_INPUT_LENGTH){
			return INPUT_TOO_LONG;
		}
		
		
		return result;
		
	}

}

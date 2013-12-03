/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.suan.weclient.fragment;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.AboutActivity;
import com.suan.weclient.util.DataManager;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class RightFragment extends Fragment {
	private RelativeLayout aboutLayout;
	private RelativeLayout feedbackLayout;
	private RelativeLayout checkUpdateLayout;
	View view;
	
	private Dialog popDialog;

	private EditText popContentEditText;
	private TextView popTitleTextView;
	private TextView popTextAmountTextView;
	private ImageButton popCancelButton, popSureButton;

	private FeedbackAgent agent;
	private Conversation defaultConversation;
	private DataManager mDataManager;
	
	public RightFragment(DataManager dataManager){
		mDataManager = dataManager;
		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.right_layout, null);
		initAgent();
		initWidgets();
		return view;
	}
	
	private void initAgent(){
		
		agent = new FeedbackAgent(getActivity());
		defaultConversation = agent.getDefaultConversation();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initWidgets() {
		aboutLayout = (RelativeLayout) view
				.findViewById(R.id.right_button_about);
		aboutLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent jumbIntent = new Intent();
				 jumbIntent.setClass(getActivity(), AboutActivity.class);
				 getActivity().startActivity(jumbIntent);

			}
		});
		
		feedbackLayout = (RelativeLayout)view.findViewById(R.id.right_button_feedback);
		feedbackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popFeedback();
				
			}
		});


		checkUpdateLayout = (RelativeLayout)view.findViewById(R.id.right_button_check_update);
		checkUpdateLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkUpdate();
				
			}
		});
	}
	
	private void checkUpdate(){
		

		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					
					break;
				case 1: // has no update
					Toast.makeText(getActivity(), "没有新版本", Toast.LENGTH_LONG).show();
					break;
				case 2: // none wifi
					break;
				case 3: // time out
					Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_LONG).show();
					break;
				}
			}
		});
		UmengUpdateAgent.update(getActivity());
	}
	
	

	private void popFeedback() {

		LayoutInflater inflater = (LayoutInflater) 
				getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.pop_feedback_layout, null);
		popTitleTextView = (TextView) dialogView
				.findViewById(R.id.pop_feedback_text_title);

		popContentEditText = (EditText) dialogView
				.findViewById(R.id.pop_feedback_edit_text);
		popSureButton = (ImageButton) dialogView
				.findViewById(R.id.pop_feedback_button_sure);
		popCancelButton = (ImageButton) dialogView
				.findViewById(R.id.pop_feedback_button_cancel);

		popTextAmountTextView = (TextView) dialogView
				.findViewById(R.id.pop_feedback_text_num);
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
				popTextAmountTextView.setText(popContentEditText.getText().length() + " x");

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
                
                mDataManager.doLoadingStart("反馈发送中...");
                
                sync();
			}
		});
		popCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popDialog.cancel();

			}
		});

		popDialog = new Dialog(getActivity(), R.style.dialog);

		popDialog.setContentView(dialogView);
		popDialog.show();

	}
	
	void sync() {
        Conversation.SyncListener listener = new Conversation.SyncListener() {

                @Override
                public void onSendUserReply(List<Reply> replyList) {
                	popContentEditText.setText("");
                	mDataManager.doLoadingEnd();
                	popDialog.dismiss();
                	Toast.makeText(getActivity(), "反馈发送成功!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onReceiveDevReply(List<DevReply> replyList) {
                }
        };
        defaultConversation.sync(listener);
	}

}

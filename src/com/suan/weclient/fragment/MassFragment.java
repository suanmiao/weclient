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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.util.DataManager;
import com.suan.weclient.util.DataManager.DialogSureClickListener;
import com.suan.weclient.util.DataManager.LoginListener;
import com.suan.weclient.util.UserBean;
import com.suan.weclient.util.WechatManager.OnActionFinishListener;

public class MassFragment extends Fragment {

	private DataManager mDataManager;
	private EditText contentEditText;
	private TextView textAmountTextView;
	private TextView popContentTextView;
	private TextView popTitleTextView;
	private TextView popTextAmountTextView;
	private TextView massLeftNumTextView;
	private ImageButton popCancelButton, popSureButton;
	private Dialog dialog;

	private ImageButton sendButton;
	private View view;

	public MassFragment(DataManager dataManager) {

		mDataManager = dataManager;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.mass_layout, null);
		initWidgets();
		initListener();
		return view;
	}

	private void initListener() {
		mDataManager.addLoginListener(new LoginListener() {

			@Override
			public void onLogin(final UserBean userBean) {
				// TODO Auto-generated method stub
				Log.e("start get mass data", "");
				if (mDataManager.getUserGroup().size() == 0) {
					sendButton
							.setBackgroundResource(R.drawable.send_selected_state);

				} else {
					if (mDataManager.getCurrentUser().getMassLeft() <= 0) {
						sendButton
								.setBackgroundResource(R.drawable.send_selected_state);
					} else {
						sendButton.setSelected(false);
					}

				}

			}
		});

	}

	private void initWidgets() {
		contentEditText = (EditText) view.findViewById(R.id.mass_edit_mass);

		massLeftNumTextView = (TextView) view
				.findViewById(R.id.mass_text_left_num);
		if (mDataManager.getUserGroup().size() == 0) {
			massLeftNumTextView.setText("你今天还能群发 " + 0 + " 条消息");

		} else {
			massLeftNumTextView.setText("你今天还能群发 "
					+ mDataManager.getCurrentUser().getMassLeft() + " 条消息");

		}
		textAmountTextView = (TextView) view.findViewById(R.id.mass_text_num);
		textAmountTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contentEditText.setText("");

			}
		});

		contentEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				textAmountTextView.setTextColor(Color.rgb(0, 0, 0));
				textAmountTextView.setText(s.length() + " x");

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

		sendButton = (ImageButton) view.findViewById(R.id.mass_button_send);

		if (mDataManager.getUserGroup().size() == 0) {
			sendButton.setBackgroundResource(R.drawable.send_selected_state);

		} else {
			if (mDataManager.getCurrentUser().getMassLeft() <= 0) {
				sendButton
						.setBackgroundResource(R.drawable.send_selected_state);
			} else {

			}

		}
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (mDataManager.getUserGroup().size() == 0) {
					sendButton
							.setBackgroundResource(R.drawable.send_selected_state);

				} else {
					if (mDataManager.getCurrentUser().getMassLeft() <= 0) {
						sendButton
								.setBackgroundResource(R.drawable.send_selected_state);
					} else {

						dialogSure();
					}

				}

			};
		});
	}

	private void dialogSure() {
		String content = contentEditText.getText().toString();
		if (content.length() == 0) {
			Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater
				.inflate(R.layout.dialog_preview_layout, null);
		popTitleTextView = (TextView) dialogView
				.findViewById(R.id.dialog_preview_text_title);

		popSureButton = (ImageButton) dialogView
				.findViewById(R.id.dialog_preview_button_sure);
		popCancelButton = (ImageButton) dialogView
				.findViewById(R.id.dialog_preview_button_cancel);

		popTextAmountTextView = (TextView) dialogView
				.findViewById(R.id.dialog_preview_text_num);

		popContentTextView = (TextView) dialogView
				.findViewById(R.id.dialog_preview_text_content);
		popContentTextView.setText(content);

		popTextAmountTextView.setText(" " + content.length() + " ");
		popTitleTextView.setText("确认发送 帐号:"
				+ mDataManager.getCurrentUser().getUserName());
		popSureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mass();

			}
		});
		popCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();

			}
		});

		dialog = new Dialog(getActivity(), R.style.dialog);

		dialog.setContentView(dialogView);
		dialog.show();

	}

	private void mass() {
		String massContent = contentEditText.getText().toString();
		mDataManager.doLoadingStart("发送中");
		mDataManager.getWechatManager().mass(mDataManager.getCurrentPosition(),
				massContent, new OnActionFinishListener() {

					@Override
					public void onFinish(Object object) {
						// TODO Auto-generated method stub

						contentEditText.setText("");

						mDataManager.doPopEnsureDialog(false, true, "群发成功",
								new DialogSureClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										mDataManager.doDismissAllDialog();

									}
								});
						
						mDataManager.getCurrentUser().setMassLeft(mDataManager.getCurrentUser().getMassLeft()-1);
						refreshMassLeft();

					}
				});
	}
	
	public void refreshMassLeft(){
		
		if (mDataManager.getUserGroup().size() == 0) {
			massLeftNumTextView.setText("你今天还能群发 " + 0 + " 条消息");

		} else {
			massLeftNumTextView.setText("你今天还能群发 "
					+ mDataManager.getCurrentUser().getMassLeft() + " 条消息");

		}
		if (mDataManager.getUserGroup().size() == 0) {
			sendButton.setBackgroundResource(R.drawable.send_selected_state);

		} else {
			if (mDataManager.getCurrentUser().getMassLeft() <= 0) {
				sendButton
						.setBackgroundResource(R.drawable.send_selected_state);
			} else {

			}

		}
	}
	

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}

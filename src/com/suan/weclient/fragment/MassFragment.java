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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.UserBean;
import com.suan.weclient.util.data.DataManager.DialogSureClickListener;
import com.suan.weclient.util.data.DataManager.LoginListener;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.text.EmotionHandler;
import com.suan.weclient.view.Face.FaceHolderView;

public class MassFragment extends Fragment {

    private DataManager mDataManager;
    private EditText contentEditText;
    private TextView textAmountTextView;
    private TextView popContentTextView;
    private TextView popTitleTextView;
    private TextView popTextAmountTextView;
    private TextView massLeftNumTextView;
    private Button popCancelButton, popSureButton;
    private Dialog dialog;


    private FaceHolderView faceHolderView;
    private ImageView faceShowButton;
    private Button sendButton;
    private View view;
    private EmotionHandler emotionHandler;

    public MassFragment(DataManager dataManager) {

        mDataManager = dataManager;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mass_layout, null);
        initWidgets();
        initWidgetsEvent();
        initListener();
        return view;
    }

    private void initListener() {
        mDataManager.addLoginListener(new LoginListener() {

            @Override
            public void onLogin(final UserBean userBean) {
                // TODO Auto-generated method stub
                setMassLeft();

            }
        });
        mDataManager.addMassDataGetListener(new DataManager.MassDataGetListener() {
            @Override
            public void onGet(UserBean userBean) {
                setMassLeft();

            }
        });

    }

    private void initWidgets() {
        contentEditText = (EditText) view.findViewById(R.id.mass_edit_mass);
        contentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    faceHolderView.setVisibility(View.GONE);
                    faceShowButton.setSelected(false);
                }

            }
        });

        emotionHandler = new EmotionHandler(contentEditText);

        massLeftNumTextView = (TextView) view
                .findViewById(R.id.mass_text_left_num);
        textAmountTextView = (TextView) view.findViewById(R.id.mass_text_num);


        sendButton = (Button) view.findViewById(R.id.mass_button_send);
        faceShowButton = (ImageView) view.findViewById(R.id.mass_button_face_show);
        faceShowButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (faceHolderView.getVisibility() != View.VISIBLE) {
                    faceHolderView.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(contentEditText.getWindowToken(), 0);
                    faceShowButton.setSelected(true);

                } else {

                    faceHolderView.setVisibility(View.GONE);

                    faceShowButton.setSelected(false);
                }

            }
        });

        faceHolderView = (FaceHolderView) view.findViewById(R.id.face_holder_view);
        faceHolderView.init(mDataManager);
        faceHolderView.setInputFaceListener(new FaceHolderView.InputFaceListener() {
            @Override
            public void onInput(String key) {
                emotionHandler.insert(key);

            }
        });

    }

    private void initWidgetsEvent() {

        setMassLeft();
        textAmountTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (contentEditText.getText().length() > 0) {

                    popClearEnsure();
                } else {

                }

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
        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
/*
                emotionHandler.insert("/::~");
                String unspanned = SpanUtil.getUnspannedContentString(contentEditText);
                Log.e("content", "" + unspanned);
*/


                if (mDataManager.getUserGroup().size() == 0) {
                    sendButton.setSelected(true);

                } else {
                    if (mDataManager.getCurrentUser().getMassLeft() <= 0) {
                        sendButton.setSelected(true);
                    } else {
                        sendButton.setSelected(false);

                        dialogEnsureMass();
                    }

                }

            }

            ;
        });
    }

    private void popClearEnsure() {

        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_ensure_layout, null);
        popTitleTextView = (TextView) dialogView
                .findViewById(R.id.dialog_ensure_text_title);
        popContentTextView = (TextView) dialogView.findViewById(R.id.dialog_ensure_text_content);

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_ensure_button_sure);
        popCancelButton = (Button) dialogView
                .findViewById(R.id.dialog_ensure_button_cancel);

        popTitleTextView.setText("删除内容");
        popContentTextView.setText("删除当前编辑的内容？");
        popSureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                contentEditText.setText("");

                dialog.cancel();

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

    private void dialogEnsureMass() {
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

        popSureButton = (Button) dialogView
                .findViewById(R.id.dialog_preview_button_sure);
        popCancelButton = (Button) dialogView
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
            public void onFinish(int code, Object object) {
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

                mDataManager.getCurrentUser().setMassLeft(mDataManager.getCurrentUser().getMassLeft() - 1);
                setMassLeft();

            }
        });
    }

    public void setMassLeft() {
        String typeString = "";
        switch (mDataManager.getCurrentUser().getUserType()) {
            case UserBean.USER_TYPE_SUBSTRICTION:
                typeString = "今天";

                break;

            case UserBean.USER_TYPE_SERVICE:

                typeString = "本月";
                break;
        }

        if (mDataManager.getUserGroup().size() == 0) {

            massLeftNumTextView.setText("你" + typeString + "还能群发 " + 0 + " 条消息");

        } else {
            massLeftNumTextView.setText("你" + typeString + "还能群发 "
                    + mDataManager.getCurrentUser().getMassLeft() + " 条消息");

        }
        if (mDataManager.getUserGroup().size() == 0) {
            sendButton.setSelected(true);

        } else {
            if (mDataManager.getCurrentUser().getMassLeft() <= 0) {
                sendButton.setSelected(true);
            } else {

                sendButton.setSelected(false);
            }

        }
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

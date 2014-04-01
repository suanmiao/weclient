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
package com.suan.weclient.fragment.mass;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.adapter.FaceGridAdapter;
import com.suan.weclient.fragment.BaseFragment;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.text.EmotionHandler;
import com.suan.weclient.util.text.FaceItem;
import com.suan.weclient.util.text.SpanUtil;
import com.suan.weclient.view.Face.FaceGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MassTextFragment extends BaseFragment {

    private DataManager mDataManager;
    private EditText contentEditText;
    private TextView textAmountTextView;
    private Dialog dialog;


    private ImageView faceShowButton;
    private View view;
    private EmotionHandler emotionHandler;
    private FaceGridView faceGridView;
    private LinearLayout faceLayout;

    /*
    total
     */
    private TextView massLeftNumTextView;
    private Button sendButton;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mass_text_layout, null);


        /*
        init data
         */
        MainActivity mainActivity = (MainActivity) getActivity();

        mDataManager = ((GlobalContext) mainActivity.getApplicationContext()).getDataManager();

        initWidgets();
        initWidgetsEvent();
        initListener();
        return view;
    }

    private void onKeyboardOpen() {
        faceShowButton.setSelected(false);
        faceLayout.setVisibility(View.GONE);

    }

    private void onKeyboardClose() {

    }

    private void initListener() {

        mDataManager.addLoginListener(new DataManager.LoginListener() {

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

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean wasOpened;

            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {

                view.getWindowVisibleDisplayFrame(r);

                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);

                boolean isOpen = heightDiff > 100;
                if (isOpen == wasOpened) {
                    return;
                }
                wasOpened = isOpen;

                if (heightDiff > 100) {
                    onKeyboardOpen();

                } else {

                    onKeyboardClose();
                }


            }
        });

        contentEditText = (EditText) view.findViewById(R.id.mass_text_edit_mass);
        contentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    faceLayout.setVisibility(View.GONE);
                    faceShowButton.setSelected(false);
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(contentEditText.getWindowToken(), 0);

                }

            }
        });

        faceLayout = (LinearLayout) view.findViewById(R.id.mass_text_layout_face);

        HashMap<String, String> faceMap = SpanUtil.getFaceMap();
        Set<String> keySet = faceMap.keySet();
        ArrayList<FaceItem> pageFaceItems = new ArrayList<FaceItem>();
        for (String nowKey : keySet) {
            pageFaceItems.add(new FaceItem(nowKey, faceMap.get(nowKey)));
        }

        FaceGridAdapter faceGridAdapter = new FaceGridAdapter(getActivity(), pageFaceItems, new FaceGridView.InputFaceListener() {
            @Override
            public void onInput(String key) {
                emotionHandler.insert(key);

            }
        });
        faceGridView = (FaceGridView) view.findViewById(R.id.mass_text_face_grid_view);

        faceGridView.setAdapter(faceGridAdapter);

        emotionHandler = new EmotionHandler(contentEditText);

        textAmountTextView = (TextView) view.findViewById(R.id.mass_text_text_num);

        faceShowButton = (ImageView) view.findViewById(R.id.mass_text_button_face_show);
        faceShowButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (faceLayout.getVisibility() != View.VISIBLE) {
                    faceLayout.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(contentEditText.getWindowToken(), 0);
                    faceShowButton.setSelected(true);

                } else {

                    faceLayout.setVisibility(View.GONE);

                    faceShowButton.setSelected(false);
                }

            }
        });

        massLeftNumTextView = (TextView) view
                .findViewById(R.id.mass_text_left_num);

        sendButton = (Button) view.findViewById(R.id.mass_button_send);

        setMassLeft();
        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

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

    @Override
    public void onResume() {
        super.onResume();
        faceShowButton.setSelected(false);
        faceLayout.setVisibility(View.GONE);


    }


    private void dialogEnsureMass() {

        String content = contentEditText.getText().toString();
        if (content.length() == 0) {
            Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog = Util.createEnsureDialog(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mass();
                dialog.dismiss();
            }
        }, true, getActivity(), "群发确认", "确认群发此消息", true);
        dialog.show();

    }

    private void mass() {
        String massContent = contentEditText.getText().toString();
        mDataManager.doLoadingStart("发送中", WechatManager.DIALOG_POP_CANCELABLE);
        MaterialBean textBean = new MaterialBean(massContent);
        textBean.setContent(massContent);
        mDataManager.getWechatManager().mass(mDataManager.getCurrentPosition(),
                textBean, new WechatManager.OnActionFinishListener() {

            @Override
            public void onFinish(int code, Object object) {
                // TODO Auto-generated method stub

                contentEditText.setText("");

                mDataManager.doPopEnsureDialog(false, true, "恭喜", "群发成功",
                        new DataManager.DialogSureClickListener() {

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

    private void initWidgetsEvent() {

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
    }

    private void popClearEnsure() {

        dialog = Util.createEnsureDialog(new OnClickListener() {
            @Override
            public void onClick(View v) {

                contentEditText.setText("");

                dialog.cancel();
            }
        }, true, getActivity(), "删除内容", "删除当前编辑的内容？", true);

        dialog.show();

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

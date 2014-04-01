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
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MassVideoFragment extends BaseFragment {

    private DataManager mDataManager;
    private TextView showTextView;
    private View view;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mass_video_layout, null);

        /*
        init data
         */
        MainActivity mainActivity = (MainActivity) getActivity();

        mDataManager = ((GlobalContext) mainActivity.getApplicationContext()).getDataManager();

        initWidgets();
        initListener();
        return view;
    }

    private void initListener() {


    }

    private void initWidgets() {

        showTextView = (TextView)view.findViewById(R.id.mass_video_text_show);
        showTextView.setText("\nSomeone said flower can fly in spring\nI said so can I\nYou may say I am a dreamer\nI am not the only one");

    }



    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

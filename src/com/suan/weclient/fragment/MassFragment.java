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
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.fragment.mass.MassAPPFragment;
import com.suan.weclient.fragment.mass.MassImgFragment;
import com.suan.weclient.fragment.mass.MassTextFragment;
import com.suan.weclient.fragment.mass.MassVoiceFragment;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.DataManager.DialogSureClickListener;
import com.suan.weclient.util.data.DataManager.LoginListener;

public class MassFragment extends BaseFragment {

    private DataManager mDataManager;

    /*
    about pop dialog
     */
    private TextView popContentTextView;
    private TextView popTitleTextView;
    private TextView popTextAmountTextView;
    private Button popCancelButton, popSureButton;
    private Dialog dialog;


    private LinearLayout contentLayout;


   private View view;

    private RelativeLayout[] indexLayout = new RelativeLayout[5];

    /*
    about fragments
     */
    MassTextFragment massTextFragment;
    MassImgFragment massImgFragment;
    MassVoiceFragment massVoiceFragment;
    MassAPPFragment massAPPFragment;

    private FragmentManager fragmentManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mass_layout, null);
        /*
        init data
         */
        MainActivity mainActivity = (MainActivity) getActivity();

        mDataManager = ((GlobalContext) mainActivity.getApplicationContext()).getDataManager();

        initWidgets();
        initFragments();
        initWidgetsEvent();
        initListener();
        return view;
    }

    private void initListener() {

    }

    private void initWidgets() {

        indexLayout[0] = (RelativeLayout) view.findViewById(R.id.mass_layout_index_first);
        indexLayout[1] = (RelativeLayout) view.findViewById(R.id.mass_layout_index_second);
        indexLayout[2] = (RelativeLayout) view.findViewById(R.id.mass_layout_index_third);
        indexLayout[3] = (RelativeLayout) view.findViewById(R.id.mass_layout_index_forth);
        indexLayout[4] = (RelativeLayout) view.findViewById(R.id.mass_layout_index_fifth);
        for (int i = 0; i < 5; i++) {
            indexLayout[i].setOnClickListener(new IndexClickListener(i));
        }
        indexLayout[0].setSelected(true);

        contentLayout = (LinearLayout) view.findViewById(R.id.mass_layout_content);

    }

    public class IndexClickListener implements OnClickListener {
        private int index;

        public IndexClickListener(int index) {
            this.index = index;

        }

        @Override
        public void onClick(View v) {

            setIndex(index);

        }
    }

    private void setIndex(int index) {
        for (int i = 0; i < 5; i++) {
            indexLayout[i].setSelected(false);
        }
        indexLayout[index].setSelected(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (index) {
            case 0:
                fragmentTransaction.replace(R.id.mass_layout_content, massTextFragment);
                break;
            case 1:
                fragmentTransaction.replace(R.id.mass_layout_content, massImgFragment);

                break;
            case 2:
                fragmentTransaction.replace(R.id.mass_layout_content, massVoiceFragment);
                break;
            case 3:
                fragmentTransaction.replace(R.id.mass_layout_content, massAPPFragment);
                break;

            case 4:
                fragmentTransaction.replace(R.id.mass_layout_content, massTextFragment);
                break;
        }
        fragmentTransaction.commit();
    }

    private void initFragments() {

        massTextFragment = new MassTextFragment();
        massImgFragment = new MassImgFragment();
        massVoiceFragment = new MassVoiceFragment();
        massAPPFragment = new MassAPPFragment();
        MainActivity mainActivity = (MainActivity) getActivity();
        fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mass_layout_content, massTextFragment);
        fragmentTransaction.commit();

    }

    private void initWidgetsEvent() {

   }




    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

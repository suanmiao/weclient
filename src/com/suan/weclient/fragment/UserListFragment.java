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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suan.weclient.R;
import com.suan.weclient.activity.LoginActivity;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.adapter.UserListAdapter;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.AutoLoginListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;
import com.suan.weclient.util.data.UserGoupPushHelper;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class UserListFragment extends Fragment {

    public static int START_ACTIVITY_LOGIN = 0;

    private View view;
    private ListView mListView;
    private RelativeLayout addUserButton;

    private UserListAdapter userListAdapter;
    private DataManager mDataManager;


    /*
    about dialog
     */

    private Dialog popDialog;

    private TextView popContentTextView;
    private TextView popTitleTextView;
    private TextView popTextAmountTextView;
    private Button popCancelButton, popSureButton;

    public UserListFragment() {

    }

    public UserListFragment(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.user_group_layout, null);
        initWidgets();
        initListener();

        return view;
    }

    private void initListener() {
        mDataManager.addAutoLoginListener(new AutoLoginListener() {

            @Override
            public void onAutoLoginEnd() {
                // TODO Auto-generated method stub
                for (int i = 0; i < mListView.getChildCount(); i++) {
                    mListView.setItemChecked(i, false);
                }
                mListView.setItemChecked(mDataManager.getCurrentPosition(), true);

            }

            @Override
            public void autoLogin() {
                // TODO Auto-generated method stub

            }
        });
        mDataManager.addUserGroupListener(new UserGroupListener() {

            @Override
            public void onGroupChangeEnd() {
                // TODO Auto-generated method stub

                userListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAddUser() {
                // TODO Auto-generated method stub

            }

            @Override
            public void deleteUser(int index) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void initWidgets() {

        mListView = (ListView) view.findViewById(R.id.left_listview);

        userListAdapter = new UserListAdapter(getActivity(),mDataManager);
        mListView.setAdapter(userListAdapter);
        mListView.setOnItemClickListener(userListAdapter);
        mListView.setOnItemLongClickListener(userListAdapter);


        addUserButton = (RelativeLayout) view
                .findViewById(R.id.left_button_add_user);
        addUserButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                mDataManager.getUserListControlListener().onUserListDismiss();
                Intent jumbIntent = new Intent();
                jumbIntent.setClass(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(jumbIntent,
                        START_ACTIVITY_LOGIN);
            }
        });
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.adapter.UserListAdapter;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.DataManager.AutoLoginListener;
import com.suan.weclient.util.data.DataManager.UserGroupListener;

public class UserListFragment extends BaseFragment {

    public static int START_ACTIVITY_LOGIN = 10;

    private View view;
    private ListView mListView;

    private UserListAdapter userListAdapter;
    private DataManager mDataManager;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.user_group_layout, null);
                /*
        init data
         */
        MainActivity mainActivity = (MainActivity)getActivity();

        mDataManager =((GlobalContext)mainActivity.getApplicationContext()).getDataManager();

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

   }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

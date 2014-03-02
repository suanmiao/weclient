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
package com.suan.weclient.fragment.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.suan.weclient.R;
import com.suan.weclient.activity.LoginActivity;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.activity.SplashActivity;
import com.suan.weclient.util.SharedPreferenceManager;

public class IntroFifthFragment extends Fragment {

    private View view;

    private Button enterButton;

    public IntroFifthFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.intro_fifth_layout, null);

        initWidgets();
        return view;
    }


    private void initWidgets() {

        enterButton = (Button) view.findViewById(R.id.intro_but_enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent jumbIntent = new Intent();
                int userGroupSize = SharedPreferenceManager.getUserGroup(getActivity()).size();
                if (userGroupSize == 0) {
                    Bundle nowBundle = new Bundle();
                    nowBundle.putInt(SplashActivity.JUMB_KEY_ENTER_STATE,
                            SplashActivity.JUMB_VALUE_INTENT_TO_LOGIN);
                    jumbIntent.putExtras(nowBundle);
                    jumbIntent.setClass(getActivity(), LoginActivity.class);

                } else {
                    jumbIntent.setClass(getActivity(), MainActivity.class);

                }

                getActivity().startActivity(jumbIntent);
                getActivity().finish();


            }
        });
    }


}

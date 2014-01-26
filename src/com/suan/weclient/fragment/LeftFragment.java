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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.suan.weclient.R;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.UserBean;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;

public class LeftFragment extends Fragment {

    private FragmentManager mFragmentManager;
    private ProfileFragment profileFragment;
    private UserListFragment userListFragment;
    private View view;
    private RelativeLayout headLayout;
    private ImageView headImageView;
    private TextView headTextView;
    private LinearLayout userListLayout, profileLayout;

    private ImageView showListView;
    private DataManager mDataManager;

    public LeftFragment(FragmentManager fragmentManager, DataManager dataManager) {
        mFragmentManager = fragmentManager;
        mDataManager = dataManager;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.left_layout, null);
        initFragments();
        initWidgets();
        initListener();

        return view;
    }

    private void initFragments() {
        profileFragment = new ProfileFragment(mDataManager);
        userListFragment = new UserListFragment(mDataManager);


        FragmentTransaction t = mFragmentManager
                .beginTransaction();

        t.replace(R.id.left_layout_profile, profileFragment);
        t.replace(R.id.left_layout_user_list, userListFragment);


        t.commit();
    }

    private void initListener() {


        mDataManager.setUserListControlListener(new ProfileFragment.UserListControlListener() {
            @Override
            public void onUserListShow() {

                ValueHolder yValueHolder = new ValueHolder();
                ObjectAnimator yObjectAnimator = ObjectAnimator.ofFloat(yValueHolder, "y", -userListLayout.getHeight(), 0).setDuration(600);
                yObjectAnimator.addUpdateListener(new YUpdateListener());
                yObjectAnimator.start();


                ValueHolder degreeValueHolder = new ValueHolder();
                ObjectAnimator degreeObjectAnimator = ObjectAnimator.ofFloat(degreeValueHolder, "degree", 0, 180).setDuration(600);
                degreeObjectAnimator.addUpdateListener(new DegreeUpdateListener());
                degreeObjectAnimator.start();

            }

            @Override
            public void onUserListDismiss() {


                ValueHolder yValueHolder = new ValueHolder();
                ObjectAnimator yObjectAnimation = ObjectAnimator.ofFloat(yValueHolder, "y", 0, -userListLayout.getHeight()).setDuration(600);
                yObjectAnimation.addUpdateListener(new YUpdateListener());
                yObjectAnimation.start();
                ValueHolder degreeValueHolder = new ValueHolder();
                ObjectAnimator degreeObjectAnimator = ObjectAnimator.ofFloat(degreeValueHolder, "degree", 180, 0).setDuration(600);
                degreeObjectAnimator.addUpdateListener(new DegreeUpdateListener());
                degreeObjectAnimator.start();

            }
        });

        mDataManager.addProfileGetListener(new DataManager.ProfileGetListener() {
            @Override
            public void onGet(UserBean userBean) {

                headTextView.setText(userBean.getNickname());


                Bitmap imgBitmap = mDataManager.getCacheManager().getBitmap(
                        ImageCacheManager.CACHE_USER_PROFILE
                                + mDataManager.getUserGroup().get(mDataManager.getCurrentPosition())
                                .getUserName());
                if (imgBitmap != null) {
                    headImageView.setImageBitmap(imgBitmap);

                } else {
                    mDataManager.getWechatManager().getUserImgDirectly(false, false,
                            mDataManager.getCurrentPosition(), headImageView, new OnActionFinishListener() {

                        @Override
                        public void onFinish(int code, Object object) {
                            // TODO Auto-generated method stub
                            Bitmap nowUserBitmap = (Bitmap) object;
                            mDataManager.getCacheManager().putBitmap(
                                    ImageCacheManager.CACHE_USER_PROFILE
                                            + mDataManager.getUserGroup()
                                            .get(mDataManager.getCurrentPosition())
                                            .getUserName(),
                                    nowUserBitmap, true);

                        }
                    });

                }

            }
        });
    }

    private void initWidgets() {


        headLayout = (RelativeLayout) view.findViewById(R.id.left_layout_head);

        headLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                int userListTop = userListLayout.getTop();
                if (userListTop!=0) {
                    showUserList();
                } else {
                    dismissUserlist();
                }
            }
        });


        headImageView = (ImageView) view.findViewById(R.id.left_img_profile);
        headTextView = (TextView) view.findViewById(R.id.left_text_profile);

        userListLayout = (LinearLayout) view.findViewById(R.id.left_layout_user_list);


        profileLayout = (LinearLayout) view.findViewById(R.id.left_layout_profile);

        showListView = (ImageView) view.findViewById(R.id.left_button_show_list);


    }

    public class ValueHolder {
        private float x = 0;
        private float y = 0;
        private float degree = 0;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public void setDegree(float degree) {
            this.degree = degree;
        }

        public float getDegree() {
            return this.degree;
        }
    }


    public class DegreeUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Float value = (Float) animation.getAnimatedValue();
            float degree = (float) value;
            int toDegree = (int) degree;

            showListView.setRotationX(showListView.getWidth() / 2 - Util.dipToPx(15, getResources()));
            showListView.setRotationY(showListView.getHeight() / 2);
            showListView.setRotation(toDegree);
        }
    }

    public class YUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Float value = (Float) animation.getAnimatedValue();
            float top = (float) value;
            int toTop = (int) top;
            userListLayout.setTop(toTop);
        }
    }

    private void showUserList() {

        mDataManager.getUserListControlListener().onUserListShow();
    }

    private void dismissUserlist() {

        mDataManager.getUserListControlListener().onUserListDismiss();
    }

}

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

import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.fragment.mass.MassAPPFragment;
import com.suan.weclient.fragment.mass.MassImgFragment;
import com.suan.weclient.fragment.mass.MassTextFragment;
import com.suan.weclient.fragment.mass.MassVideoFragment;
import com.suan.weclient.fragment.mass.MassVoiceFragment;
import com.suan.weclient.fragment.mass.VoiceFragment;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.DataManager.DialogSureClickListener;
import com.suan.weclient.util.data.DataManager.LoginListener;
import com.tencent.mm.sdk.openapi.SendMessageToWX;

import org.jsoup.examples.HtmlToPlainText;

import java.io.File;

public class MassFragment extends BaseFragment {

    private DataManager mDataManager;

    /*
    about pop dialog
     */
    private Dialog dialog;


    public static final int REQUEST_CODE_SELECT_PHOTO = 5;
    public static final int REQUEST_CODE_TAKE_PHOTO = 6;

    private LinearLayout contentLayout;

    private LinearLayout recordLayout;

    private View view;

    private RelativeLayout[] indexLayout = new RelativeLayout[5];

    /*
    about fragments
     */
    MassTextFragment massTextFragment;
    MassImgFragment massImgFragment;
    MassVoiceFragment massVoiceFragment;
    MassAPPFragment massAPPFragment;
    MassVideoFragment massVideoFragment;

    VoiceFragment voiceFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private int nowIndex = 0;

    /*
    about select img
     */

    public static final String UPLOAD_IMG_NAME = "ImgToUpload.jpg";

    private String capturedImageName = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mass_layout, null);
        /*
        init data
         */
        MainActivity mainActivity = (MainActivity) getActivity();

        mDataManager = ((GlobalContext) mainActivity.getApplicationContext()).getDataManager();
        initFragments();
        initWidgets();
        initWidgetsEvent();
        initListener();
        return view;
    }

    private void initListener() {
        mDataManager.setRecordControlListener(new RecordLayoutControlListener() {
            @Override
            public void onLayoutShow() {

                ValueHolder yValueHolder = new ValueHolder();
                ObjectAnimator yObjectAnimator = ObjectAnimator.ofFloat(yValueHolder, "y",
                        view.getBottom() + recordLayout.getHeight(), view.getBottom()).setDuration(500);
                yObjectAnimator.addUpdateListener(new YUpdateListener());
                yObjectAnimator.start();

            }

            @Override
            public void onLayoutDismiss() {


                ValueHolder yValueHolder = new ValueHolder();
                ObjectAnimator yObjectAnimator = ObjectAnimator.ofFloat(yValueHolder, "y",
                        view.getBottom(), view.getBottom() + recordLayout.getHeight()).setDuration(300);
                yObjectAnimator.addUpdateListener(new YUpdateListener());
                yObjectAnimator.start();
            }
        });

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
        recordLayout = (LinearLayout) view.findViewById(R.id.mass_voice_record_layout);

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

    @Override
    public void onStart() {

        super.onStart();

    }

    @Override
    public void onResume() {

        super.onResume();

        setIndex(nowIndex);

    }


    private void setIndex(int index) {
        nowIndex = index;

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
                massImgFragment.setSelectPhotoListener(new SelectPhotoListener() {
                    @Override
                    public void onSelectFromAlbum() {


                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, REQUEST_CODE_SELECT_PHOTO);
                    }

                    @Override
                    public void onTakePhoto() {


                        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        String uploadImgPath = Util.getFilePath(UPLOAD_IMG_NAME);
                        File out = new File(uploadImgPath);
                        capturedImageName = out.getAbsolutePath();
                        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
                        startActivityForResult(i, REQUEST_CODE_TAKE_PHOTO);

                    }
                });

                break;
            case 2:
                fragmentTransaction.replace(R.id.mass_layout_content, massVoiceFragment);
                break;
            case 3:
                fragmentTransaction.replace(R.id.mass_layout_content, massAPPFragment);

                break;

            case 4:
                fragmentTransaction.replace(R.id.mass_layout_content, massVideoFragment);
                break;
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQUEST_CODE_SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {

                        Uri selectedImage = imageReturnedIntent.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        capturedImageName = cursor.getString(columnIndex);
                        cursor.close();
                        massImgFragment.setCapturedImageName(capturedImageName);

                    } catch (Exception e) {
                        Log.e("parse selected photo error", "" + e);

                    }


                }
                break;
            case REQUEST_CODE_TAKE_PHOTO:

                if (resultCode == Activity.RESULT_OK) {

                    massImgFragment.setCapturedImageName(capturedImageName);

                }

                break;
        }
    }

    @Override
    public void onPause() {

        super.onPause();

    }

    private void initFragments() {

        massTextFragment = new MassTextFragment();
        massImgFragment = new MassImgFragment();
        massVoiceFragment = new MassVoiceFragment();
        massAPPFragment = new MassAPPFragment();
        massVideoFragment = new MassVideoFragment();

        voiceFragment = new VoiceFragment();

        fragmentManager = getChildFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.disallowAddToBackStack();

        fragmentTransaction.add(R.id.mass_voice_record_layout, voiceFragment);
        fragmentTransaction.commit();


    }


    public class ValueHolder {
        private float y = 0;
        private float degree = 0;

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

    }

    public class YUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Float value = (Float) animation.getAnimatedValue();
            float top = (float) value;
            int toTop = (int) top;

            recordLayout.setTop(toTop - recordLayout.getHeight());
            recordLayout.setBottom(toTop);

        }
    }

    private void initWidgetsEvent() {

    }


    public interface SelectPhotoListener {
        public void onSelectFromAlbum();

        public void onTakePhoto();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public interface RecordLayoutControlListener {
        public void onLayoutShow();

        public void onLayoutDismiss();
    }

}

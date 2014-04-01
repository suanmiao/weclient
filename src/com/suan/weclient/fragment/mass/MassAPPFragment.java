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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.adapter.MaterialListAdapter;
import com.suan.weclient.fragment.BaseFragment;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.AppItemBean;
import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.bean.MultiItemBean;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.holder.resultHolder.MaterialResultHolder;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.view.ptr.PTRListview;

import java.io.File;

public class MassAPPFragment extends BaseFragment implements PTRListview.OnRefreshListener, PTRListview.OnLoadListener {

    private DataManager mDataManager;
    private Dialog popDialog;


    private View view;

    private LinearLayout editLayout;
    /*
   about app msg
    */
    private RelativeLayout coverLayout;
    private RelativeLayout[] itemLayout;


    private ImageView coverImageView;
    private ImageView[] itemImageView;

    private TextView timeTextView;
    private TextView titleTextView;
    private TextView coverTextView;
    private TextView[] itemTextView;




    /*
    total
     */

    private TextView massLeftNumTextView;
    private Button sendButton;

    private PTRListview ptrListview;
    private MaterialListAdapter materialListAdapter;

    private MaterialListAdapter.ItemViewHolder selectedHolder;
    private MaterialBean selectedBean;

    private MaterialHandler mHandler;


    public static final int REQUEST_CODE_SELECT_PHOTO = 5;
    public static final int REQUEST_CODE_TAKE_PHOTO = 6;


    private static final int PAGE_MESSAGE_AMOUNT = 10;

    /*
    about adapter list change
     */
    public static final int MESSAGE_NOTIFY_TYPE_ONLY_REFRESH = 2;
    public static final int MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA = 3;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mass_app_layout, null);
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

    private void initListener() {

        mHandler = new MaterialHandler();

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

        mDataManager.addMaterialChangeListener(new DataManager.MaterialGetListener() {
            @Override
            public void onMaterialGet(int type, MaterialResultHolder materialResultHolder) {
                if (type == MaterialBean.MATERIAL_TYPE_APP) {

                    Message notifyMessage = new Message();
                    notifyMessage.arg1 = MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA;
                    notifyMessage.obj = materialResultHolder;
                    mHandler.sendMessage(notifyMessage);
                }

            }
        });

    }

    private void initWidgets() {


        titleTextView = (TextView) view.findViewById(R.id.mass_app_text_title);
        timeTextView = (TextView) view.findViewById(R.id.material_item_text_time);
        coverLayout = (RelativeLayout) view.findViewById(R.id.mass_app_layout_cover);
        itemLayout = new RelativeLayout[3];
        itemImageView = new ImageView[3];
        itemTextView = new TextView[3];
        itemLayout[0] = (RelativeLayout) view.findViewById(R.id.mass_app_layout_first_item);
        itemLayout[1] = (RelativeLayout) view.findViewById(R.id.mass_app_layout_second_item);
        itemLayout[2] = (RelativeLayout) view.findViewById(R.id.mass_app_layout_third_item);

        coverImageView = (ImageView) view.findViewById(R.id.mass_app_img_cover);
        itemImageView[0] = (ImageView) view.findViewById(R.id.mass_app_img_first);
        itemImageView[1] = (ImageView) view.findViewById(R.id.mass_app_img_second);
        itemImageView[2] = (ImageView) view.findViewById(R.id.mass_app_img_third);

        coverTextView = (TextView) view.findViewById(R.id.mass_app_text_cover_title);
        itemTextView[0] = (TextView) view.findViewById(R.id.mass_app_text_first_title);
        itemTextView[1] = (TextView) view.findViewById(R.id.mass_app_text_second_title);
        itemTextView[2] = (TextView) view.findViewById(R.id.mass_app_text_third_title);


        editLayout = (LinearLayout) view.findViewById(R.id.mass_app_layout_edit);
        editLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popMaterialList();

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

    private void dialogEnsureMass() {

        if (selectedBean == null) {
            Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        popDialog = Util.createEnsureDialog(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mass();
                popDialog.dismiss();
            }
        }, true, getActivity(), "群发确认", "确认群发此消息", true);
        popDialog.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQUEST_CODE_SELECT_PHOTO:
                break;
            case REQUEST_CODE_TAKE_PHOTO:


                break;
        }
    }


    private void mass() {
        mDataManager.doLoadingStart("发送中", WechatManager.DIALOG_POP_CANCELABLE);
        mDataManager.getWechatManager().mass(mDataManager.getCurrentPosition(),
                selectedBean, new WechatManager.OnActionFinishListener() {

            @Override
            public void onFinish(int code, Object object) {
                // TODO Auto-generated method stub

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

    }

    private void popMaterialList() {
        mDataManager.createMaterialHolder(mDataManager.getCurrentUser());
        popDialog = Util.createMaterialListDialog(getActivity(), "图文素材", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialListAdapter.ItemViewHolder holder = materialListAdapter.getSelectedHolder();
                        if (holder != null) {

                            selectedHolder = holder;

                            selectedBean = holder.getMaterialBean();
                            setAPPMessageContent(selectedHolder);
                            popDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请选择内容", Toast.LENGTH_LONG).show();

                        }

                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popDialog.dismiss();

                    }
                }
        );

        ptrListview = (PTRListview) popDialog.findViewById(R.id.dialog_material_list_content);
        materialListAdapter = new MaterialListAdapter(getActivity(), mDataManager);
        ptrListview.setonRefreshListener(this);
        ptrListview.setOnLoadListener(this);
        ptrListview.setAdapter(materialListAdapter);
        ptrListview.setOnScrollListener(materialListAdapter);

        ptrListview.onRefreshStart();
        new GetDataTask(ptrListview, PTRListview.PTR_MODE_REFRESH).execute();

        popDialog.show();


    }


    public void setAPPMessageContent(MaterialListAdapter.ItemViewHolder holder) {
        AppItemBean appItemBean = holder.getMaterialBean().getAppItemBean();
        int itemCount = appItemBean.getMulti_item().size();
        coverTextView.setText(appItemBean.getDigest());
        titleTextView.setText(appItemBean.getTitle());

        Bitmap contentBitmap = mDataManager.getCacheManager().getBitmap(
                ImageCacheManager.CACHE_MESSAGE_CONTENT
                        + holder.getMaterialBean().getAppItemBean().getFile_id());
        if (contentBitmap == null) {
            mDataManager.getWechatManager().getNormalImg(mDataManager.getCurrentPosition(),
                    Util.checkImgUrl(holder.getMaterialBean().getAppItemBean().getImg_url()), coverImageView, new WechatManager.OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    if (code == WechatManager.ACTION_SUCCESS) {
                        if (object != null) {
                            Bitmap bitmap = (Bitmap) object;

                            coverImageView.setImageBitmap(bitmap);

                        }

                    }

                }
            });


        } else {
            coverImageView.setImageBitmap(contentBitmap);
        }


        for (int i = 0; i < 3; i++) {
            if (i < itemCount - 1) {
                itemLayout[i].setVisibility(View.VISIBLE);
                itemTextView[i].setText(appItemBean.getMulti_item().get(i + 1).getDigest());
                setAppItemImg(i, holder);

            } else {
                itemLayout[i].setVisibility(View.GONE);
            }

        }


    }

    private void setAppItemImg(final int index, MaterialListAdapter.ItemViewHolder holder) {
        MultiItemBean multiItemBean = holder.getMaterialBean().getAppItemBean().getMulti_item().get(index);

        Bitmap contentBitmap = mDataManager.getCacheManager().getBitmap(
                ImageCacheManager.CACHE_MESSAGE_CONTENT
                        + multiItemBean.getFile_id());
        if (contentBitmap == null) {
            mDataManager.getWechatManager().getNormalImg(mDataManager.getCurrentPosition(),
                    Util.checkImgUrl(multiItemBean.getCover()), itemImageView[index], new WechatManager.OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    if (code == WechatManager.ACTION_SUCCESS) {
                        if (object != null) {
                            Bitmap bitmap = (Bitmap) object;
                            itemImageView[index].setImageBitmap(bitmap);

                        }

                    }

                }
            });


        } else {
            itemImageView[index].setImageBitmap(contentBitmap);
        }


    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class MaterialHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);


            switch (msg.arg1) {
                case MESSAGE_NOTIFY_TYPE_ONLY_REFRESH:

                    break;
                case MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA:

                    MaterialResultHolder materialResultHolder = (MaterialResultHolder) msg.obj;
                    mDataManager.getMaterialHolder().mergeMaterialResult(materialResultHolder);
                    if (materialResultHolder != null) {
                        switch (materialResultHolder.getResultMode()) {
                            case MaterialResultHolder.RESULT_MODE_ADD:

                                try {

                                    ptrListview.onLoadComplete();
                                } catch (Exception e) {

                                }
                                break;
                            case MaterialResultHolder.RESULT_MODE_REFRESH:

                                try {
                                    materialListAdapter.updateCache();
                                    ptrListview.onRefreshComplete();
                                    ptrListview.setSelection(1);

                                } catch (Exception e) {

                                }

                                break;
                        }

                    }

                    break;
            }


            try {


                int size = mDataManager.getMaterialHolder().getMaterialCount();
                ptrListview.setLoadEnable(size % PAGE_MESSAGE_AMOUNT == 0);

                ptrListview.requestLayout();
                materialListAdapter.notifyDataSetChanged();
            } catch (Exception e) {

            }

        }
    }


    @Override
    public void onRefresh() {

        new GetDataTask(ptrListview, PTRListview.PTR_MODE_REFRESH).execute();

    }

    @Override
    public void onLoad() {

        new GetDataTask(ptrListview, PTRListview.PTR_MODE_LOAD).execute();

    }


    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        private PTRListview mRefreshedView;
        private boolean end = false;
        private int mode;

        public GetDataTask(PTRListview refreshedView, int mode) {
            mRefreshedView = refreshedView;

            this.mode = mode;
            end = false;
            if (mDataManager.getCurrentMessageHolder() == null) {
                end = true;
                mRefreshedView.onLoadComplete();
                return;
            }
            try {
                if (mode == PTRListview.PTR_MODE_LOAD) {

                    int size = mDataManager.getMaterialHolder().getMaterialCount();

                    // must be fuul amount of page

                    if (size % PAGE_MESSAGE_AMOUNT == 0) {

                        mDataManager.getWechatManager().getAppMsgList(mDataManager.getCurrentPosition(), size, new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code, Object object) {
                                switch (code) {
                                    case WechatManager.ACTION_SUCCESS:

                                        mDataManager.doMaterialGet(MaterialBean.MATERIAL_TYPE_APP, (MaterialResultHolder) object);

                                        break;
                                    case WechatManager.ACTION_TIME_OUT:

                                        break;
                                    case WechatManager.ACTION_OTHER:

                                        break;
                                    case WechatManager.ACTION_SPECIFICED_ERROR:

                                        mDataManager.doPopEnsureDialog(true, true, "登录超时", "登录超时", new DataManager.DialogSureClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                mDataManager.doAutoLogin();

                                            }
                                        });

                                        break;
                                }
                                try {

                                    ptrListview.onLoadComplete();
                                } catch (Exception e) {

                                }


                                end = true;

                            }
                        });

                    } else {

                        try {

                            ptrListview.onLoadComplete();
                        } catch (Exception e) {

                        }
                        end = true;
                    }

                } else if (mode == PTRListview.PTR_MODE_REFRESH) {


                    mDataManager.getWechatManager().getAppMsgList(mDataManager.getCurrentPosition(), 0, new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:

                                    mDataManager.doMaterialGet(MaterialBean.MATERIAL_TYPE_APP, (MaterialResultHolder) object);

                                    break;
                                case WechatManager.ACTION_TIME_OUT:

                                    break;
                                case WechatManager.ACTION_OTHER:

                                    break;
                                case WechatManager.ACTION_SPECIFICED_ERROR:

                                    mDataManager.doPopEnsureDialog(true, true, "登录超时", "登录超时", new DataManager.DialogSureClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            mDataManager.doAutoLogin();

                                        }
                                    });

                                    break;
                            }
                            try {

                                ptrListview.onRefreshComplete();
                            } catch (Exception e) {

                            }

                            end = true;

                        }
                    });


                }
            } catch (Exception e) {

            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Simulates a background job.
            try {

                while (!end) {
                    Thread.sleep(50);
                }

            } catch (Exception exception) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            switch (mode) {
                case PTRListview.PTR_MODE_LOAD:
                    try {

                        mRefreshedView.onLoadComplete();
                    } catch (Exception e) {

                    }

                    break;
                case PTRListview.PTR_MODE_REFRESH:

                    try {

                        mRefreshedView.onRefreshComplete();
                    } catch (Exception e) {

                    }
                    break;

            }
        }
    }
}

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
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.suan.weclient.util.data.bean.MaterialBean;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.data.holder.resultHolder.MaterialResultHolder;
import com.suan.weclient.util.net.UploadHelper;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.voice.RecorderThread;
import com.suan.weclient.util.voice.VoiceHolder;
import com.suan.weclient.util.voice.VoiceManager;
import com.suan.weclient.view.ptr.PTRListview;

import java.io.File;

public class MassVoiceFragment extends BaseFragment implements PTRListview.OnRefreshListener, PTRListview.OnLoadListener {

    private DataManager mDataManager;
    private Dialog popDialog;


    private View view;

    private RelativeLayout voicePlayLayout;
    private ImageView playImageView;
    private TextView voiceInfoTextView;


    private LinearLayout recordLayout;
    private LinearLayout selectLayout;


    private Button uploadButton;


    /*

     */
    // 音频获取源
    private int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private static int sampleRateInHz = 16000;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;
    private AudioRecord audioRecord;
    private boolean isRecord = false;// 设置正在录制的状态
    //AudioName裸音频数据文件
    private String rawAudioPath = "";
    //NewAudioName可播放的音频文件
    private String wavAudioPath = "";

    private RecorderThread recorderThread;


    /*
    common
     */
    private TextView massLeftNumTextView;
    private Button sendButton;

    private PTRListview ptrListview;
    private MaterialListAdapter materialListAdapter;

    private MaterialListAdapter.ItemViewHolder selectedHolder;
    private MaterialBean selectedBean;


    private VoiceHolder selectedVoiceHolder;

    private MaterialHandler mHandler;


    public static final int REQUEST_CODE_SELECT_PHOTO = 5;
    public static final int REQUEST_CODE_TAKE_PHOTO = 6;
    public static final int REQUEST_CODE_SELECT_VOICE = 7;



    private static final int PAGE_MESSAGE_AMOUNT = 10;

    /*
    about adapter list change
     */
    public static final int MESSAGE_NOTIFY_TYPE_ONLY_REFRESH = 2;
    public static final int MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA = 3;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mass_voice_layout, null);
        /*
        init data
         */
        MainActivity mainActivity = (MainActivity) getActivity();

        mDataManager = ((GlobalContext) mainActivity.getApplicationContext()).getDataManager();

        initWidgets();
        initListener();
        initRecorder();
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
                if (type == MaterialBean.MATERIAL_TYPE_VOICE) {

                    Message notifyMessage = new Message();
                    notifyMessage.arg1 = MESSAGE_NOTIFY_TYPE_REFRESH_WITH_NEW_DATA;
                    notifyMessage.obj = materialResultHolder;
                    mHandler.sendMessage(notifyMessage);
                }

            }
        });

    }

    private void initRecorder() {


        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        // 创建AudioRecord对象
        audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);

    }


    private void startRecord() {
        if(audioRecord==null){
            initRecorder();
        }
        audioRecord.startRecording();
        Log.e("record", "start");
        // 让录制状态为true
        isRecord = true;
        // 开启音频文件写入线程
        rawAudioPath = Util.getFilePath("record.raw");

        wavAudioPath = Util.getFilePath("record.wav");

        recorderThread = new RecorderThread(audioRecord, bufferSizeInBytes, sampleRateInHz, rawAudioPath, wavAudioPath);
        recorderThread.setRecording(true);
        new Thread(recorderThread).start();

    }

    private void stopRecord() {
        if (audioRecord != null) {
            Log.e("record", "stop");
            isRecord = false;
            try {
                recorderThread.setRecording(false);

            } catch (Exception e) {
                e.printStackTrace();

            }
            audioRecord.stop();
            audioRecord.release();//释放资源
            audioRecord = null;
        }
    }


    private void initWidgets() {

        uploadButton = (Button) view.findViewById(R.id.mass_voice_but_upload);

        uploadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wavAudioPath.length() > 1) {
                    mDataManager.getWechatManager().getUploadInfo(mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {

                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:
                                    Log.e("upload info ", "ok");
                                    Toast.makeText(getActivity(), "准备成功", Toast.LENGTH_SHORT).show();

                                    mDataManager.getWechatManager().uploadImg(mDataManager.getCurrentPosition(), wavAudioPath, new WechatManager.OnActionFinishListener() {
                                        @Override
                                        public void onFinish(int code, Object object) {


                                            switch (code) {
                                                case WechatManager.ACTION_SUCCESS:
                                                    Log.e("upload  ", "ok");
                                                    Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                                                    UploadHelper.NowUploadBean nowUploadBean = (UploadHelper.NowUploadBean)object;
                                                    selectedBean = new MaterialBean(nowUploadBean.getContent(),MaterialBean.MATERIAL_TYPE_VOICE);
                                                    break;
                                                case WechatManager.ACTION_TIME_OUT:

                                                    break;
                                                case WechatManager.ACTION_OTHER:

                                                    break;
                                                case WechatManager.ACTION_SPECIFICED_ERROR:


                                                    break;
                                            }
                                        }
                                    });


                                    break;
                                case WechatManager.ACTION_TIME_OUT:

                                    break;
                                case WechatManager.ACTION_OTHER:

                                    break;
                                case WechatManager.ACTION_SPECIFICED_ERROR:


                                    break;
                            }

                        }
                    });

                }

            }
        });

        voicePlayLayout = (RelativeLayout) view.findViewById(R.id.mass_voice_layout_play);
        playImageView = (ImageView) view.findViewById(R.id.mass_voice_button_play);
        voiceInfoTextView = (TextView) view.findViewById(R.id.mass_voice_text_info);

        recordLayout = (LinearLayout) view.findViewById(R.id.mass_voice_layout_record);

        recordLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        recordLayout.setBackgroundResource(R.drawable.icon);
                        if (!isRecord) {
                            startRecord();

                        }
                        break;
                    case MotionEvent.ACTION_MOVE:

                        recordLayout.setBackgroundResource(R.drawable.icon);
                        if (!isRecord) {
                            startRecord();

                        }

                        break;
                    default:

                        recordLayout.setBackgroundResource(R.drawable.remark_edit);
                        if(isRecord){
                            stopRecord();
                        }

                        break;
                }

                return true;
            }
        });

        selectLayout = (LinearLayout) view.findViewById(R.id.mass_voice_layout_edit);
        selectLayout.setOnClickListener(new OnClickListener() {
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

    private void setContentVoice() {
        if (selectedHolder.getDataLoaded()) {
            selectedVoiceHolder = (VoiceHolder) selectedHolder.getData();

            int playLength = Integer.parseInt(selectedHolder.getMaterialBean().getPlay_length());
            int seconds = playLength / 1000;
            int minutes = seconds / 60;
            int leaveSecond = seconds % 60;
            String info = "";
            if (minutes != 0) {
                info += minutes + "'";
            }

            info += " " + leaveSecond + "'";
            voiceInfoTextView.setText(info);


            if (selectedVoiceHolder.getPlaying()) {
                mDataManager.getVoiceManager().stopMusic();

            } else {

                mDataManager.getVoiceManager().playVoice(
                        selectedVoiceHolder.getBytes(),
                        selectedVoiceHolder.getPlayLength(),
                        selectedVoiceHolder.getLength(),
                        new VoiceManager.AudioPlayListener() {

                            @Override
                            public void onAudioStop() {
                                // TODO Auto-generated method stub

                                selectedVoiceHolder.setPlaying(false);

                                playImageView.setSelected(false);

                            }

                            @Override
                            public void onAudioStart() {
                                // TODO Auto-generated method stub
                                selectedVoiceHolder.setPlaying(true);
                                playImageView.setSelected(true);

                            }

                            @Override
                            public void onAudioError() {
                                // TODO Auto-generated method stub

                            }
                        });
            }

        } else {

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

                        mDataManager.getWechatManager().getMaterialList(MaterialBean.MATERIAL_TYPE_VOICE, size, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code, Object object) {
                                switch (code) {
                                    case WechatManager.ACTION_SUCCESS:

                                        mDataManager.doMaterialGet(MaterialBean.MATERIAL_TYPE_VOICE, (MaterialResultHolder) object);

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


                    mDataManager.getWechatManager().getMaterialList(MaterialBean.MATERIAL_TYPE_VOICE, 0, mDataManager.getCurrentPosition(), new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            switch (code) {
                                case WechatManager.ACTION_SUCCESS:

                                    mDataManager.doMaterialGet(MaterialBean.MATERIAL_TYPE_VOICE, (MaterialResultHolder) object);

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


    private void popMaterialList() {
        mDataManager.createMaterialHolder(mDataManager.getCurrentUser());
        popDialog = Util.createMaterialListDialog(getActivity(), "Img", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialListAdapter.ItemViewHolder holder = materialListAdapter.getSelectedHolder();
                        if (holder != null) {

                            selectedHolder = holder;
                            selectedBean = holder.getMaterialBean();
                            setContentVoice();
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

    }

}

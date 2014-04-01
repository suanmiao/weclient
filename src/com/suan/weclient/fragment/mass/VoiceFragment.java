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

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.suan.weclient.R;
import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.fragment.BaseFragment;
import com.suan.weclient.fragment.ProfileFragment;
import com.suan.weclient.fragment.UserListFragment;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.bean.UserBean;
import com.suan.weclient.util.net.WechatManager;
import com.suan.weclient.util.net.WechatManager.OnActionFinishListener;
import com.suan.weclient.util.net.images.ImageCacheManager;
import com.suan.weclient.util.voice.RecorderThread;

import org.apache.http.impl.client.TunnelRefusedException;

public class VoiceFragment extends BaseFragment {

    private DataManager mDataManager;
    private FragmentManager mFragmentManager;
    private View view;

    private RelativeLayout recordLayout;

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
    //NewAudioName可播放的音频文件
    private String wavAudioPath = "";

    private RecorderThread recorderThread;

    private Vibrator vibrator;
    private VoiceHandler voiceHandler;

    private static final int RECORD_MSG_START = 3;
    private static final int RECORD_MSG_FINISH = 4;
    private static final int RECORD_MSG_ERROR = 5;

    public class RecordResultHolder {
        private String filePath;
        private int type;
        private long playLength;

        public RecordResultHolder(String filePath, int type, long playLength) {
            this.type = type;
            this.filePath = filePath;
            this.playLength = playLength;

        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        init the data
         */
        MainActivity mainActivity = (MainActivity) getActivity();
        mDataManager = ((GlobalContext) mainActivity.getApplicationContext()).getDataManager();
        mFragmentManager = mainActivity.getSupportFragmentManager();

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        voiceHandler = new VoiceHandler();

        view = inflater.inflate(R.layout.voice_record_layout, null);
        /*
        fuck android ,you must add the layout params
         */
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        initWidgets();
        initListener();

        return view;
    }


    private void initListener() {


    }

    private void initWidgets() {

        recordLayout = (RelativeLayout) view.findViewById(R.id.voice_record_start);


        recordLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recordLayout.setSelected(true);

                        if (!isRecord) {
                            startRecord();

                        }

                        break;
                    case MotionEvent.ACTION_MOVE:

                        recordLayout.setSelected(true);

                        if (!isRecord) {
                            startRecord();

                        }


                        break;
                    default:

                        recordLayout.setSelected(false);

                        if (isRecord) {
                            stopRecord();
                        }

                        break;
                }

                return true;
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


    public class VoiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            super.handleMessage(msg);
            switch (msg.arg1) {
                case RECORD_MSG_START:

                    Toast.makeText(getActivity(), "录音开始", Toast.LENGTH_SHORT).show();
                    break;
                case RECORD_MSG_FINISH:

                    RecordResultHolder recordResultHolder = (RecordResultHolder) msg.obj;
                    if (recordResultHolder != null) {

                        mDataManager.doRecordFinish(recordResultHolder.type, recordResultHolder.filePath, recordResultHolder.playLength);
                    }
                    Toast.makeText(getActivity(), "录音结束", Toast.LENGTH_SHORT).show();
                    break;
                case RECORD_MSG_ERROR:
                    switch (msg.arg2) {
                        case RecorderThread.RECORD_ERROR_TOO_LONG:

                            Toast.makeText(getActivity(), "录音超长，请从新录制", Toast.LENGTH_SHORT).show();
                            break;
                        case RecorderThread.RECORD_ERROR_TOO_SHORT:

                            Toast.makeText(getActivity(), "录音时间小于1秒，请重新录制", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;

            }

        }

    }


    private void startRecord() {
        if (audioRecord == null) {
            initRecorder();
        }
        audioRecord.startRecording();
        // 让录制状态为true
        isRecord = true;
        // 开启音频文件写入线程
        wavAudioPath = Util.getFilePath("record.wav");

        recorderThread = new RecorderThread(audioRecord, bufferSizeInBytes, sampleRateInHz, wavAudioPath, new RecorderThread.RecordListener() {
            @Override
            public void onRecordStart(int type) {

                vibrator.vibrate(100);
                Message message = new Message();
                message.arg1 = RECORD_MSG_START;
                message.arg2 = type;
                voiceHandler.sendMessage(message);

            }

            @Override
            public void onRecordFinish(int type, String filePath, long playLength) {

                vibrator.vibrate(100);
                Message message = new Message();
                message.arg1 = RECORD_MSG_FINISH;
                message.arg2 = type;
                RecordResultHolder recordResultHolder = new RecordResultHolder(filePath, type, playLength);
                message.obj = recordResultHolder;

                voiceHandler.sendMessage(message);
            }

            @Override
            public void onRecordError(int type) {

                Message message = new Message();
                message.arg1 = RECORD_MSG_ERROR;
                message.arg2 = type;
                voiceHandler.sendMessage(message);
                vibrator.vibrate(100);
            }
        });
        recorderThread.setRecording(true);
        new Thread(recorderThread).start();

    }

    private void stopRecord() {
        if (audioRecord != null) {
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

    public static final int RECORD_TYPE_MASS = 3;
    public static final int RECORD_TYPE_CHAT = 4;


}

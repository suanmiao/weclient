package com.suan.weclient.pushService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.suan.weclient.activity.MainActivity;
import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.UserBean;
import com.suan.weclient.util.net.WeChatLoader;
import com.suan.weclient.util.net.WechatManager;

import java.util.Date;

/**
 * Created by lhk on 1/2/14.
 */
public class PushService extends Service {
    private GlobalContext globalContext;
    private DataManager mDatamanager;
    private MessageNotification messageNotification;
    private String lastMsgId;

    public static final int PUSH_FREQUENT_FAST = 2;
    public static final int PUSH_FREQUENT_NORMAL = 1;
    public static final int PUSH_FREQUENT_SLOW = 0;


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messageNotification = MessageNotification.getInstance(this);
        //��һִ��
        checkServiceStatus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean pushEnable = SharedPreferenceManager.getPushEnable(this);
        boolean timeAppropriate = true;
        boolean closeNight = SharedPreferenceManager.getPushCloseNight(this);
        if (closeNight) {
            Date date = new Date(System.currentTimeMillis());

            int hour = date.getHours();
            if (hour > 23 || hour < 7) {
                timeAppropriate = false;
            }

        }


        if (pushEnable && timeAppropriate) {
            doTask();
        } else {

        }

        return super.onStartCommand(intent, flags, startId);

    }

/*
    private void cancelAlarm() {
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(PushService.this, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.BROADCAST_ACTION_START_PUSH_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(PushService.this, 0, intent, 0);
        alarm.cancel(sender);

        Intent stopAlarmService = new Intent(PushService.this, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.BROADCAST_ACTION_STOP_PUSH);
        sendBroadcast(stopAlarmService);


    }
*/


    private void doTask() {
        try {

            globalContext = (GlobalContext) getApplicationContext();
            mDatamanager = globalContext.getDataManager();
            //I found this parameter is not so meaningful

            boolean networkConnected = Util.isNetConnected(PushService.this);
            if (networkConnected) {

                UserBean nowBean = mDatamanager.getCurrentUser();
                lastMsgId = SharedPreferenceManager.getLastMsgId(PushService.this);
                if (nowBean.getSlaveSid() != null && nowBean.getSlaveSid().length() > 1) {
                    //slave sid is not null
                    //try it,just get profile

                    justGetNewMessage(mDatamanager, lastMsgId, new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            if (code == WechatManager.ACTION_SUCCESS) {
                                int newMessageCount = (Integer) object;
                                logic(newMessageCount);

                            } else {

                                getNewMessageAfterLogin(mDatamanager, new WechatManager.OnActionFinishListener() {
                                    @Override
                                    public void onFinish(int code, Object object) {
                                        if (code == WechatManager.ACTION_SUCCESS) {
                                            justGetNewMessage(mDatamanager, lastMsgId, new WechatManager.OnActionFinishListener() {
                                                @Override
                                                public void onFinish(int code, Object object) {

                                                    int newMessageCount = (Integer) object;
                                                    logic(newMessageCount);

                                                }
                                            });

                                        }

                                    }
                                });

                            }

                        }
                    });


                }

            } else {
                //activity running
                //clear all notification
                messageNotification.clearAllNotification();
            }


        } catch (Exception exception) {

        }
    }

    private void justGetNewMessage(DataManager mDatamanager, String lastMsgId, WechatManager.OnActionFinishListener onActionFinishListener) {
        mDatamanager.getWechatManager().getNewMessageCount(mDatamanager.getCurrentPosition(), lastMsgId, false, onActionFinishListener);

    }

    private void getNewMessageAfterLogin(DataManager mDatamanager, WechatManager.OnActionFinishListener onActionFinishListener) {
        mDatamanager.getWechatManager().login(mDatamanager.getCurrentPosition(), WechatManager.DIALOG_POP_NO, false, onActionFinishListener);

    }


    private void logic(int newMessage) {

        int lastNewMessage = SharedPreferenceManager.getLastNewMessage(this);

        boolean pushNewMessageEnable = SharedPreferenceManager.getPushNewMessageEnable(this);
        if (pushNewMessageEnable) {

            if (newMessage > lastNewMessage) {
                //new message added


                showNotification(MessageNotification.NOTIFI_TYPE_NEW_MESSAGE, newMessage, mDatamanager.getCurrentUser().getNickname());
                SharedPreferenceManager.putLastNewMessage(this, newMessage);


            } else {
                if (newMessage != 0) {
                    long lastUpdateTime = SharedPreferenceManager.getLastMessageNotifyTime(this);
                    if (System.currentTimeMillis() - lastUpdateTime > 10 * 60 * 1000) {
                        //more than 10minutes
                        showNotification(MessageNotification.NOTIFI_TYPE_NEW_MESSAGE, newMessage, mDatamanager.getCurrentUser().getNickname());

                    }

                } else {
                    SharedPreferenceManager.putLastNewMessage(this, 0);
                }

            }

        }


    }

    private void showNotification(int type, int amount, String accountName) {

        boolean activityRunning = SharedPreferenceManager.getActivityRunning(this);
        if (!activityRunning) {

            messageNotification.createNotification(type, amount, accountName);
            switch (type) {
                case MessageNotification.NOTIFI_TYPE_NEW_PEOPLE:
                    SharedPreferenceManager.putLastPeopleNotifyTime(this, System.currentTimeMillis());

                    break;

                case MessageNotification.NOTIFI_TYPE_NEW_MESSAGE:

                    SharedPreferenceManager.putLastMessageNotifyTime(this, System.currentTimeMillis());
                    break;
            }

        }

        sendRefreshBroadcast();


    }


    private void sendRefreshBroadcast() {
        Intent startServiceIntent = new Intent();
        startServiceIntent.setAction(MainActivity.BROADCAST_ACTION_REFRESH_MESSAGE);
        sendBroadcast(startServiceIntent);
        Log.e(" send broadcase", "send broadcase");

    }

    private void checkServiceStatus() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    String _filePath = "com.suan.weclient.pushService.PushService";
                    boolean _flag = Util.isServiceRunning(PushService.this, _filePath);
                    if (_flag == false) {
                        PushService.this.startService(new Intent(PushService.this, PushService.class));
                    }
                } catch (Exception e) {

                }
            }
        }.start();

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

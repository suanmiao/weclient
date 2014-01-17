package com.suan.weclient.pushService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.suan.weclient.util.GlobalContext;
import com.suan.weclient.util.SharedPreferenceManager;
import com.suan.weclient.util.Util;
import com.suan.weclient.util.data.DataManager;
import com.suan.weclient.util.data.UserBean;
import com.suan.weclient.util.net.WechatManager;

/**
 * Created by lhk on 1/2/14.
 */
public class PushService extends Service {
    private GlobalContext globalContext;
    private DataManager mDatamanager;
    private MessageNotification messageNotification;

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
        Log.e("push service", "oncreate");
        messageNotification = MessageNotification.getInstance(this);
        //��һִ��
        checkServiceStatus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean pushEnable = SharedPreferenceManager.getPushEnable(this);
        Log.e("push start commant", "" + pushEnable);

        if (pushEnable) {
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
            boolean activityRunning = SharedPreferenceManager.getActivityRunning(this);
            if (!activityRunning) {

                UserBean nowBean = mDatamanager.getCurrentUser();
                if (nowBean.getSlaveSid() != null && nowBean.getSlaveSid().length() > 1) {
                    //slave sid is not null
                    //try it,just get profile
                    justGetProfile(mDatamanager, new WechatManager.OnActionFinishListener() {
                        @Override
                        public void onFinish(int code, Object object) {
                            if (code == WechatManager.ACTION_SUCCESS) {
                                checkUserProfile();

                            } else {
                                Toast.makeText(PushService.this, "just get failed,login", Toast.LENGTH_LONG).show();

                                getProfileAfterLogin(mDatamanager, new WechatManager.OnActionFinishListener() {
                                    @Override
                                    public void onFinish(int code, Object object) {
                                        if (code == WechatManager.ACTION_SUCCESS) {
                                            justGetProfile(mDatamanager, new WechatManager.OnActionFinishListener() {
                                                @Override
                                                public void onFinish(int code, Object object) {
                                                    if (code == WechatManager.ACTION_SUCCESS) {

                                                        checkUserProfile();
                                                    }

                                                }
                                            });

                                        } else {

                                            Toast.makeText(PushService.this, "login in get failed", Toast.LENGTH_LONG).show();
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
            Log.e("service error", exception + "");

        }
    }

    private void checkUserProfile() {
        UserBean nowBean = mDatamanager.getCurrentUser();
        int newPeople = Integer.parseInt(nowBean.getNewPeople());
        int newMessage = Integer.parseInt(nowBean.getNewMessage());

        logic(newPeople, newMessage);

    }

    private void justGetProfile(DataManager mDatamanager, WechatManager.OnActionFinishListener onActionFinishListener) {
        mDatamanager.getWechatManager().getUserProfile(false,false, mDatamanager.getCurrentPosition(), onActionFinishListener);

    }

    private void getProfileAfterLogin(DataManager mDatamanager, WechatManager.OnActionFinishListener onActionFinishListener) {
        mDatamanager.getWechatManager().login(mDatamanager.getCurrentPosition(), false,false, onActionFinishListener);

    }


    private void logic(int newPeople, int newMessage) {

        int lastNewPeople = SharedPreferenceManager.getLastNewPeople(this);
        int lastNewMessage = SharedPreferenceManager.getLastNewMessage(this);


        if (newPeople > lastNewPeople) {
            //new people added
            showNotification(MessageNotification.NOTIFI_TYPE_NEW_PEOPLE, newPeople, mDatamanager.getCurrentUser().getNickname());
            SharedPreferenceManager.putLastNewPeople(this, newPeople);


        } else {
            if (newPeople != 0) {
                long lastUpdateTime = SharedPreferenceManager.getLastPeopleNotifyTime(this);
                if (System.currentTimeMillis() - lastUpdateTime > 10 * 60 * 1000) {
                    //more than 10minutes
                    showNotification(MessageNotification.NOTIFI_TYPE_NEW_PEOPLE, newPeople, mDatamanager.getCurrentUser().getNickname());

                }

            } else {
                SharedPreferenceManager.putLastNewPeople(this, 0);
            }

        }


        if (newMessage > lastNewMessage) {
            Log.e("message", "new>last");
            //new message added
            showNotification(MessageNotification.NOTIFI_TYPE_NEW_MESSAGE, newMessage, mDatamanager.getCurrentUser().getNickname());
            SharedPreferenceManager.putLastNewMessage(this, newMessage);


        } else {
            if (newMessage != 0) {
                long lastUpdateTime = SharedPreferenceManager.getLastMessageNotifyTime(this);
                if (System.currentTimeMillis() - lastUpdateTime > 10 * 60 * 1000) {
                    //more than 10minutes
                    Log.e("message", "more time");
                    showNotification(MessageNotification.NOTIFI_TYPE_NEW_MESSAGE, newMessage, mDatamanager.getCurrentUser().getNickname());

                }

            } else {
                SharedPreferenceManager.putLastNewMessage(this, 0);
            }

        }


    }

    private void showNotification(int type, int amount, String accountName) {

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

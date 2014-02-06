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
import com.suan.weclient.util.data.UserGoupPushHelper;
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
                UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(PushService.this));
                boolean notifyWholeGroup = SharedPreferenceManager.getPushNotifyWholeGroup(PushService.this);

                if (notifyWholeGroup) {

                    for (int i = 0; i < userGoupPushHelper.getUserHolders().size(); i++) {
                        pushUser(userGoupPushHelper.getUserHolders().get(i), mDatamanager, i);

                    }
                } else {
                    int currentIndex = userGoupPushHelper.getCurrentIndex();

                    pushUser(userGoupPushHelper.getUserHolders().get(currentIndex), mDatamanager, currentIndex);

                }


            } else {
                //activity running
                //clear all notification
            }


        } catch (Exception exception) {

        }
    }

    private void pushUser(final UserGoupPushHelper.PushUserHolder userHolder, final DataManager mDatamanager, final int index) {
        UserBean nowBean = userHolder.getUserBean();
        lastMsgId = userHolder.getLastMsgId();
        if (nowBean.getSlaveSid() != null && nowBean.getSlaveSid().length() > 1 && lastMsgId.length() > 1) {
            //slave sid is not null
            //try it,just get profile

            justGetNewMessage(mDatamanager, index, lastMsgId, new WechatManager.OnActionFinishListener() {
                @Override
                public void onFinish(int code, Object object) {
                    if (code == WechatManager.ACTION_SUCCESS) {
                        int newMessageCount = (Integer) object;
                        logic(newMessageCount, userHolder, index);

                    } else {

                        getNewMessageAfterLogin(mDatamanager, index, new WechatManager.OnActionFinishListener() {
                            @Override
                            public void onFinish(int code, Object object) {
                                if (code == WechatManager.ACTION_SUCCESS) {
                                    //refresh the token
                                    UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(PushService.this));
                                    userGoupPushHelper.updateUserGroup(mDatamanager);
                                    mDatamanager.saveUserGroup(PushService.this);

                                    SharedPreferenceManager.putPushUserGroup(PushService.this, userGoupPushHelper.getString());

                                    justGetNewMessage(mDatamanager, index, lastMsgId, new WechatManager.OnActionFinishListener() {
                                        @Override
                                        public void onFinish(int code, Object object) {
                                            if (code == WechatManager.ACTION_SUCCESS) {

                                                int newMessageCount = (Integer) object;
                                                logic(newMessageCount, userHolder, index);

                                            }

                                        }
                                    });

                                }

                            }
                        });

                    }

                }
            });


        }

    }

    private void justGetNewMessage(DataManager mDatamanager, int userIndex, String lastMsgId, WechatManager.OnActionFinishListener onActionFinishListener) {
        mDatamanager.getWechatManager().getNewMessageCount(userIndex, lastMsgId, onActionFinishListener);

    }

    private void getNewMessageAfterLogin(DataManager mDatamanager, int userIndex, WechatManager.OnActionFinishListener onActionFinishListener) {
        mDatamanager.getWechatManager().login(userIndex, WechatManager.DIALOG_POP_NO, false, onActionFinishListener);

    }


    private void logic(int newMessage, UserGoupPushHelper.PushUserHolder userHolder, final int index) {

        int lastNewMessage = userHolder.getLastNewMessageCount();


        if (newMessage > lastNewMessage) {
            //new message added

            showNotification(newMessage, userHolder.getUserBean().getNickname(), index);

            UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(PushService.this));
            userGoupPushHelper.getUserHolders().get(index).setLastNewMessageCount(newMessage);
            SharedPreferenceManager.putPushUserGroup(PushService.this, userGoupPushHelper.getString());


        } else {
            if (newMessage != 0) {

                long lastUpdateTime = userHolder.getLastNotifyTime();
                if (System.currentTimeMillis() - lastUpdateTime > 10 * 60 * 1000) {
                    //more than 10minutes
                    showNotification(newMessage, userHolder.getUserBean().getNickname(), index);

                }

            } else {

                try {
                    UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(PushService.this));
                    userGoupPushHelper.getUserHolders().get(index).setLastNewMessageCount(0);
                    SharedPreferenceManager.putPushUserGroup(PushService.this, userGoupPushHelper.getString());

                } catch (Exception e) {

                }

            }

        }


    }

    private void showNotification(int amount, String accountName, int index) {

        boolean activityRunning = SharedPreferenceManager.getActivityRunning(this);
        if (!activityRunning) {

            messageNotification.createNotification(amount, accountName,index);

            //update notify time

            try {
                UserGoupPushHelper userGoupPushHelper = new UserGoupPushHelper(SharedPreferenceManager.getPushUserGroup(PushService.this));
                userGoupPushHelper.getUserHolders().get(index).setLastNotifyTime(System.currentTimeMillis());
                SharedPreferenceManager.putPushUserGroup(PushService.this, userGoupPushHelper.getString());

            } catch (Exception e) {

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

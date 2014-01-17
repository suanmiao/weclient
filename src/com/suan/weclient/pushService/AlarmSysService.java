package com.suan.weclient.pushService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.suan.weclient.util.SharedPreferenceManager;

/**
 * Created by lhk on 1/2/14.
 */
public class AlarmSysService extends Service {
    private AlarmManager alarm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //startTimer();
        doAlarmTask();

    }

    public void doAlarmTask() {
        if (null == alarm) {
            alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(AlarmSysService.this, AlarmReceiver.class);
            intent.setAction(AlarmReceiver.BROADCAST_ACTION_START_PUSH_SERVICE);
            PendingIntent sender = PendingIntent.getBroadcast(AlarmSysService.this, 0, intent, 0);
            Log.e("alarm service", "start alarm");
            boolean pushEnable = SharedPreferenceManager.getPushEnable(this);
            if (pushEnable) {
                int pushFrequent = SharedPreferenceManager.getPustFrequent(this);
                int delay = 20 * 1000;
                switch (pushFrequent) {
                    case PushService.PUSH_FREQUENT_FAST:
                        delay = 10 * 1000;

                        break;

                    case PushService.PUSH_FREQUENT_NORMAL:
                        delay = 30 * 1000;

                        break;
                    case PushService.PUSH_FREQUENT_SLOW:

                        delay = 2 * 60 * 1000;

                        break;
                }

                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), delay, sender);

            } else {
                this.stopSelf();
            }

        }
    }


    @Override
    public void onDestroy() {
        Log.e("alarmservice","destroy");
        super.onDestroy();
    }

}

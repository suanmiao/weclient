package com.suan.weclient.pushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by lhk on 1/2/14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ACTION_START_PUSH_SERVICE = "cn.com.action.suan.startPushService";
    public static final String BROADCAST_ACTION_START_PUSH = "cn.com.action.suan.startPush";
    public static final String BROADCAST_ACTION_STOP_PUSH = "cn.com.action.suan.stopPush";

    private final static String TAG = "AlarmReceiver";
    private final static String SYSTEM_BROADCAST_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BROADCAST_ACTION_START_PUSH_SERVICE)) {
            Intent mintent = new Intent(context, PushService.class);
            context.startService(mintent);
        }

        if (intent.getAction().equals(BROADCAST_ACTION_START_PUSH)) {
            Intent alarmIntent = new Intent(context, AlarmSysService.class);
            context.startService(alarmIntent);
        }

        if (intent.getAction().equals(BROADCAST_ACTION_STOP_PUSH)) {
            Intent alarmIntent = new Intent(context, AlarmSysService.class);
            Intent pushIntent = new Intent(context, PushService.class);
            context.stopService(alarmIntent);
            context.stopService(pushIntent);
        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent alarmIntent = new Intent(context, AlarmSysService.class);
            context.startService(alarmIntent);

        }

        if (intent.getAction().equals(SYSTEM_BROADCAST_ACTION)) {
            context.startService(new Intent(context, AlarmSysService.class));
        }
    }
}

package com.space.gymday;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "gym_alarm_channel_v2";

    @Override public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 1);
        int hour = intent.getIntExtra("hour", 8);
        int minute = intent.getIntExtra("minute", 0);
        boolean snooze = intent.getBooleanExtra("snooze", false);
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String type = intent.getStringExtra("type");

        Intent alarm = new Intent(context, AlarmActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("id", id).putExtra("title", title).putExtra("body", body).putExtra("type", type);
        PendingIntent fullScreen = PendingIntent.getActivity(context, id, alarm, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "GYM alarms", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("منبهات الوجبات والجيم والنوم");
            ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            ch.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
            ch.enableVibration(true);
            nm.createNotificationChannel(ch);
        }

        Notification.Builder builder = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, CHANNEL_ID) : new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(title).setContentText(body).setStyle(new Notification.BigTextStyle().bigText(body))
                .setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC).setAutoCancel(true)
                .setFullScreenIntent(fullScreen, true).setContentIntent(fullScreen);
        nm.notify(id, builder.build());

        try { context.startActivity(alarm); } catch (Exception ignored) {}
        if (!snooze && hour >= 0) AlarmScheduler.schedule(context, id, hour, minute, title, body, type);
    }
}

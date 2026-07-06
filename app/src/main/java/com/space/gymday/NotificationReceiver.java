package com.space.gymday;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL = "gym_motivation_v2";

    @Override public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 7001);
        int hour = intent.getIntExtra("hour", 10);
        int minute = intent.getIntExtra("minute", 15);
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "GYM motivation", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("رسائل تشجيع ذكية"); nm.createNotificationChannel(ch);
        }
        Intent open = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, id, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, CHANNEL) : new Notification.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body)
                .setStyle(new Notification.BigTextStyle().bigText(body)).setContentIntent(pi).setAutoCancel(true);
        nm.notify(id, b.build());

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent next = new Intent(context, NotificationReceiver.class).putExtras(intent);
        PendingIntent nextPi = PendingIntent.getBroadcast(context, id, next, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, AlarmScheduler.nextTime(hour, minute), nextPi);
    }
}

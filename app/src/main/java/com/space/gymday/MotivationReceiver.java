package com.space.gymday;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MotivationReceiver extends BroadcastReceiver {
    private static final String CHANNEL = "gym_motivation";
    private static final String[] MESSAGES = {
            "كل وجبة ملتزم بها تقربك من هدفك 💪",
            "الانضباط اليوم هو شكل جسمك بعد شهور 🔥",
            "اشرب مياه الآن — الأداء يبدأ من الترطيب 💧",
            "مش لازم تكون متحمس، لازم تبدأ فقط 🏋️",
            "النوم الجيد جزء من التمرين، مش وقت ضائع 😴"
    };

    @Override public void onReceive(Context context, Intent intent) {
        if (!context.getSharedPreferences("gym", Context.MODE_PRIVATE).getBoolean("motivation_on", true)) { AlarmScheduler.scheduleMotivation(context); return; }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "تحفيز GYM", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("رسائل تحفيز ونصائح يومية");
            nm.createNotificationChannel(ch);
        }
        Intent open = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 7001, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        String msg = MESSAGES[(int)(System.currentTimeMillis() / 86400000L) % MESSAGES.length];
        android.app.Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new android.app.Notification.Builder(context, CHANNEL) : new android.app.Notification.Builder(context);
        b.setSmallIcon(android.R.drawable.star_big_on)
                .setContentTitle("GYM — دفعة صغيرة ليوم أقوى")
                .setContentText(msg)
                .setStyle(new android.app.Notification.BigTextStyle().bigText(msg))
                .setAutoCancel(true)
                .setContentIntent(pi);
        nm.notify(7002, b.build());
        AlarmScheduler.scheduleMotivation(context);
    }
}

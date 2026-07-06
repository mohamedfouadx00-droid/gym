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

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean motivationEnabled =
                context
                        .getSharedPreferences(
                                "gym_app_v2",
                                Context.MODE_PRIVATE
                        )
                        .getBoolean(
                                "motivation_enabled",
                                true
                        );

        if (!motivationEnabled) {
            return;
        }

        NotificationManager nm =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL,
                            "تحفيز GYM",
                            NotificationManager.IMPORTANCE_DEFAULT
                    );

            channel.setDescription(
                    "رسائل تحفيز ونصائح يومية"
            );

            nm.createNotificationChannel(channel);
        }

        Intent open =
                new Intent(
                        context,
                        MainActivity.class
                );

        open.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        7001,
                        open,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        int messageIndex =
                (int) (
                        System.currentTimeMillis()
                                / 86400000L
                ) % MESSAGES.length;

        String message =
                MESSAGES[messageIndex];

        android.app.Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder =
                    new android.app.Notification.Builder(
                            context,
                            CHANNEL
                    );

        } else {

            builder =
                    new android.app.Notification.Builder(
                            context
                    );
        }

        builder
                .setSmallIcon(
                        android.R.drawable.star_big_on
                )
                .setContentTitle(
                        "GYM — دفعة صغيرة ليوم أقوى"
                )
                .setContentText(
                        message
                )
                .setStyle(
                        new android.app.Notification
                                .BigTextStyle()
                                .bigText(message)
                )
                .setAutoCancel(true)
                .setContentIntent(
                        pendingIntent
                );

        nm.notify(
                7002,
                builder.build()
        );
    }
}

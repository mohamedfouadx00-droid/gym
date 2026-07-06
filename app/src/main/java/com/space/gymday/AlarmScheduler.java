package com.space.gymday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.util.Calendar;

public final class AlarmScheduler {
    private AlarmScheduler() {}

    public static void scheduleAll(Context context) {
        for (String[] item : AppData.SCHEDULE) {
            schedule(context, Integer.parseInt(item[0]), Integer.parseInt(item[1]), Integer.parseInt(item[2]), item[3], item[4], item[5]);
        }
        scheduleMotivation(context, 7001, 10, 15, "ركز على يوم واحد فقط 💪", "كل وجبة وكل تمرين خطوة صغيرة ناحية جسم أقوى.");
        scheduleMotivation(context, 7002, 17, 0, "أنت قادر تكمل 🔥", "الاستمرار أهم من الكمال. حافظ على خطتك اليوم.");
    }

    public static void schedule(Context context, int id, int hour, int minute, String title, String body, String type) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return;
        long when = nextTime(hour, minute);
        Intent intent = new Intent(context, AlarmReceiver.class)
                .putExtra("id", id).putExtra("hour", hour).putExtra("minute", minute)
                .putExtra("title", title).putExtra("body", body).putExtra("type", type);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.setAlarmClock(new AlarmManager.AlarmClockInfo(when, pi), pi);
    }

    public static void scheduleSnooze(Context context, int id, int minutes, String title, String body, String type) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long when = System.currentTimeMillis() + minutes * 60_000L;
        Intent intent = new Intent(context, AlarmReceiver.class)
                .putExtra("id", id).putExtra("hour", -1).putExtra("minute", -1)
                .putExtra("title", title).putExtra("body", body).putExtra("type", type).putExtra("snooze", true);
        PendingIntent pi = PendingIntent.getBroadcast(context, id + 90000, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return;
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
    }

    private static void scheduleMotivation(Context context, int id, int hour, int minute, String title, String body) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class).putExtra("id", id).putExtra("hour", hour).putExtra("minute", minute).putExtra("title", title).putExtra("body", body);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long when = nextTime(hour, minute);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
    }

    public static long nextTime(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour); c.set(Calendar.MINUTE, minute); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1);
        return c.getTimeInMillis();
    }

    public static Intent exactAlarmSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setData(android.net.Uri.parse("package:" + context.getPackageName()));
        }
        return null;
    }
}

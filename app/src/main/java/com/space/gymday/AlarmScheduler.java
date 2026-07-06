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

    public static final String[][] DEFAULTS = new String[][]{
            {"800", "8", "0", "اصحى يا بطل 💪", "وقت ما تبدأ يومك", "wake"},
            {"830", "8", "30", "وقت الفطار 🍳", "3 بيضات + فول + عيش + موزة", "meal"},
            {"1100", "11", "0", "وجبة قبل التمرين 🥣", "شوفان + لبن + موزة", "meal"},
            {"1230", "12", "30", "استعد للجيم 💧", "اشرب مياه وجهز حاجتك", "gym"},
            {"1300", "13", "0", "وقت التمرين 🏋️", "ابدأ التمرين الآن", "gym"},
            {"1415", "14", "15", "كرياتين بعد التمرين", "خد 5 جرام كرياتين واشرب مياه", "supplement"},
            {"1500", "15", "0", "وقت الغدا 🍗", "أرز أو مكرونة + بروتين + سلطة", "meal"},
            {"1800", "18", "0", "سناك اقتصادي 🥛", "لبن + موزة + فول سوداني", "meal"},
            {"2100", "21", "0", "وقت العشا 🍳", "بيض + فول أو جبنة + عيش", "meal"},
            {"2330", "23", "30", "وجبة خفيفة قبل النوم", "لبن أو زبادي", "meal"},
            {"0", "0", "0", "وقت النوم 😴", "نام 7 إلى 9 ساعات عشان تبني عضل", "sleep"}
    };

    public static void scheduleAll(Context context) {
        for (String[] item : DEFAULTS) {
            schedule(context, Integer.parseInt(item[0]), Integer.parseInt(item[1]), Integer.parseInt(item[2]), item[3], item[4], item[5]);
        }
    }

    public static void schedule(Context context, int id, int hour, int minute, String title, String body, String type) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1);

        Intent intent = new Intent(context, AlarmReceiver.class)
                .putExtra("id", id)
                .putExtra("hour", hour)
                .putExtra("minute", minute)
                .putExtra("title", title)
                .putExtra("body", body)
                .putExtra("type", type);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.setAlarmClock(new AlarmManager.AlarmClockInfo(c.getTimeInMillis(), pi), pi);
    }

    public static Intent exactAlarmSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setData(android.net.Uri.parse("package:" + context.getPackageName()));
        }
        return null;
    }
}

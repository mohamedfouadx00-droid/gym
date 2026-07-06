package com.space.gymday;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private final String[] foods = {"بيض", "فول", "عيش بلدي", "موز", "شوفان", "لبن", "عسل", "أرز", "مكرونة", "فراخ", "جبنة قريش", "فول سوداني", "زبادي", "بطاطس", "خضار / سلطة", "كرياتين"};
    private final Map<String, CheckBox> checks = new LinkedHashMap<>();
    private SharedPreferences prefs;
    private TextView suggestion;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("gym_day", MODE_PRIVATE);
        setContentView(buildUi());
        requestPermissionsAndSchedule();
        refreshSuggestion();
    }

    private View buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(getColor(R.color.bg));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(30), dp(18), dp(30));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(root);

        TextView title = text("يومي الرياضي 💪", 30, true);
        TextView subtitle = text("خطة زيادة الوزن وبناء العضلات — تمرين الساعة 1 ظهرًا", 16, false);
        subtitle.setTextColor(getColor(R.color.muted));
        subtitle.setPadding(0, dp(4), 0, dp(18));
        root.addView(title); root.addView(subtitle);

        root.addView(sectionTitle("وجبتك التالية"));
        LinearLayout suggestCard = card();
        suggestion = text("", 19, true);
        suggestion.setTextColor(getColor(R.color.text));
        suggestCard.addView(suggestion);
        root.addView(suggestCard);

        root.addView(sectionTitle("الحاجات الموجودة عندك"));
        LinearLayout inv = card();
        for (String food : foods) {
            CheckBox cb = new CheckBox(this);
            cb.setText(food);
            cb.setTextSize(18);
            cb.setChecked(prefs.getBoolean("food_" + food, true));
            cb.setPadding(0, dp(5), 0, dp(5));
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("food_" + food, isChecked).apply();
                refreshSuggestion();
            });
            checks.put(food, cb);
            inv.addView(cb);
        }
        root.addView(inv);

        root.addView(sectionTitle("جدول اليوم"));
        LinearLayout schedule = card();
        for (String[] item : AlarmScheduler.DEFAULTS) {
            String time = String.format("%02d:%02d", Integer.parseInt(item[1]), Integer.parseInt(item[2]));
            TextView row = text(time + "  —  " + item[3] + "\n" + item[4], 17, false);
            row.setPadding(0, dp(10), 0, dp(10));
            schedule.addView(row);
        }
        root.addView(schedule);

        Button alarmButton = new Button(this);
        alarmButton.setText("تفعيل كل المنبهات الآن");
        alarmButton.setTextSize(18);
        alarmButton.setTextColor(0xFFFFFFFF);
        alarmButton.setBackgroundResource(R.drawable.bg_button);
        alarmButton.setPadding(dp(12), dp(14), dp(12), dp(14));
        alarmButton.setOnClickListener(v -> enableAlarms());
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bp.setMargins(0, dp(18), 0, dp(12));
        root.addView(alarmButton, bp);

        TextView note = text("المنبه الصباحي يستخدم أعلى صوت Alarm في الهاتف. باقي المواعيد تستخدم نغمة المنبه، وليس نغمة إشعار قصيرة.", 15, false);
        note.setTextColor(getColor(R.color.muted));
        root.addView(note);
        return scroll;
    }

    private void requestPermissionsAndSchedule() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 44);
        }
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
            Toast.makeText(this, "فعّل السماح بالمنبهات الدقيقة عشان المواعيد تشتغل في وقتها", Toast.LENGTH_LONG).show();
        } else {
            AlarmScheduler.scheduleAll(this);
        }
    }

    private void enableAlarms() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
            Intent settings = AlarmScheduler.exactAlarmSettings(this);
            if (settings != null) startActivity(settings);
            return;
        }
        AlarmScheduler.scheduleAll(this);
        Toast.makeText(this, "تم تفعيل كل المنبهات يوميًا ✅", Toast.LENGTH_LONG).show();
    }

    @Override protected void onResume() {
        super.onResume();
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()) AlarmScheduler.scheduleAll(this);
    }

    private void refreshSuggestion() {
        if (suggestion == null) return;
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String meal;
        List<String> needed = new ArrayList<>();
        if (hour < 10) {
            meal = "الفطار: 3 بيضات + فول + عيش بلدي + موزة";
            addMissing(needed, "بيض", "فول", "عيش بلدي", "موز");
        } else if (hour < 13) {
            meal = "قبل التمرين: شوفان + لبن + موزة + عسل";
            addMissing(needed, "شوفان", "لبن", "موز", "عسل");
        } else if (hour < 16) {
            meal = "بعد التمرين: 5 جم كرياتين، ثم غدا أرز/مكرونة + فراخ + سلطة";
            addMissing(needed, "كرياتين", "أرز", "فراخ", "خضار / سلطة");
        } else if (hour < 20) {
            meal = "السناك: لبن + موزة + فول سوداني";
            addMissing(needed, "لبن", "موز", "فول سوداني");
        } else if (hour < 23) {
            meal = "العشا: بيض + فول أو جبنة قريش + عيش";
            addMissing(needed, "بيض", "عيش بلدي");
            if (!has("فول") && !has("جبنة قريش")) needed.add("فول أو جبنة قريش");
        } else {
            meal = "قبل النوم: لبن أو زبادي، وبعدها نوم";
            if (!has("لبن") && !has("زبادي")) needed.add("لبن أو زبادي");
        }
        if (needed.isEmpty()) suggestion.setText(meal + "\n\n✅ كل المكونات موجودة عندك");
        else suggestion.setText(meal + "\n\n🛒 ناقصك: " + String.join("، ", needed));
    }

    private void addMissing(List<String> list, String... names) { for (String n : names) if (!has(n)) list.add(n); }
    private boolean has(String name) { CheckBox cb = checks.get(name); return cb == null ? prefs.getBoolean("food_" + name, true) : cb.isChecked(); }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setBackgroundResource(R.drawable.bg_card); box.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); p.setMargins(0, 0, 0, dp(12)); box.setLayoutParams(p); return box;
    }
    private TextView sectionTitle(String s) { TextView v = text(s, 21, true); v.setPadding(0, dp(14), 0, dp(10)); return v; }
    private TextView text(String s, int sp, boolean bold) { TextView v = new TextView(this); v.setText(s); v.setTextSize(sp); v.setTextColor(getColor(R.color.text)); v.setGravity(Gravity.RIGHT); if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD); return v; }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density); }
}

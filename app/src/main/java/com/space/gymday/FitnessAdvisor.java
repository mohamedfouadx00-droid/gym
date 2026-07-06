package com.space.gymday;

import android.content.SharedPreferences;
import java.util.Calendar;

public final class FitnessAdvisor {
    private FitnessAdvisor() {}

    public static class Advice {
        public final boolean restDay;
        public final String title;
        public final String reason;
        public final String workout;
        Advice(boolean restDay, String title, String reason, String workout) {
            this.restDay = restDay; this.title = title; this.reason = reason; this.workout = workout;
        }
    }

    public static Advice today(SharedPreferences p) {
        Calendar c = Calendar.getInstance();
        int dow = c.get(Calendar.DAY_OF_WEEK);
        if (dow == Calendar.FRIDAY) {
            return new Advice(true, "الجمعة راحة من الجيم", "الجيم مغلق اليوم. الراحة تساعد العضلات على الاستشفاء والنمو.", "مشي خفيف 20–30 دقيقة + إطالات بسيطة");
        }

        boolean wentToday = p.getBoolean(keyForToday(), false);
        if (wentToday) return new Advice(true, "أنت تمرنت اليوم ✅", "لا تحتاج تمرين حديد ثاني اليوم. ركّز على الأكل والمياه والنوم.", "مسموح مشي خفيف أو إطالات فقط");

        int streak = p.getInt("training_streak", 0);
        int lastDay = p.getInt("last_training_day", -99);
        int today = c.get(Calendar.DAY_OF_YEAR);
        boolean trainedYesterday = lastDay == today - 1 || (today == 1 && lastDay >= 364);
        if (trainedYesterday && streak >= 3) {
            return new Advice(true, "الأفضل ترتاح اليوم", "أكملت 3 أيام تدريب متتالية. يوم راحة الآن غالبًا أفضل لجودة التمرين والاستشفاء.", "مشي 20 دقيقة + إطالات + نوم 7–9 ساعات");
        }

        String split = splitForDay(dow);
        return new Advice(false, "اليوم مناسب للتمرين 💪", "بناءً على سجل التمرين الحالي لا يوجد سبب واضح للراحة اليوم.", split);
    }

    public static String splitForDay(int dow) {
        switch (dow) {
            case Calendar.SATURDAY: return "Full Body A — صدر + ظهر + رجل";
            case Calendar.SUNDAY: return "راحة أو مشي خفيف";
            case Calendar.MONDAY: return "Full Body B — رجل + كتف + ظهر";
            case Calendar.TUESDAY: return "راحة واستشفاء";
            case Calendar.WEDNESDAY: return "Full Body A — صدر + ظهر + رجل";
            case Calendar.THURSDAY: return "تمرين اختياري خفيف أو تعويض تمرين فائت";
            default: return "راحة";
        }
    }

    public static void setWentToday(SharedPreferences p, boolean went) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_YEAR);
        SharedPreferences.Editor e = p.edit().putBoolean(keyForToday(), went);
        if (went) {
            int last = p.getInt("last_training_day", -99);
            int streak = p.getInt("training_streak", 0);
            boolean consecutive = last == day - 1 || (day == 1 && last >= 364);
            e.putInt("training_streak", consecutive ? streak + 1 : 1);
            e.putInt("last_training_day", day);
        }
        e.apply();
    }

    public static String keyForToday() {
        Calendar c = Calendar.getInstance();
        return "went_" + c.get(Calendar.YEAR) + "_" + c.get(Calendar.DAY_OF_YEAR);
    }

    public static int suggestedProtein(int weightKg) { return Math.max(80, Math.round(weightKg * 1.8f)); }
    public static int suggestedWaterMl(int weightKg) { return Math.max(2000, weightKg * 35); }
    public static int targetWeight(int heightCm, int currentKg) { return Math.max(currentKg + 5, Math.round((heightCm - 100) * 0.92f)); }
}

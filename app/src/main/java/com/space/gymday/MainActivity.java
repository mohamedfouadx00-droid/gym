package com.space.gymday;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private SharedPreferences prefs;
    private LinearLayout content;
    private LinearLayout nav;
    private final Map<String, CheckBox> foodChecks = new LinkedHashMap<>();
    private int activeTab = 0;

    private final String[] tabs = {"🏠\nالرئيسية", "📅\nاليوم", "🏋️\nالتمرين", "🍳\nمطبخي", "☰\nالمزيد"};

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("gym", MODE_PRIVATE);
        if (!prefs.getBoolean("profile_ready", false)) seedDefaults();
        setContentView(buildShell());
        showTab(0);
        requestPermissionsAndSchedule();
        if (!prefs.getBoolean("profile_ready", false)) showProfileDialog(true);
    }

    private void seedDefaults() {
        prefs.edit()
                .putString("name", "محمد")
                .putInt("age", 24)
                .putInt("height", 174)
                .putInt("weight", 60)
                .putInt("target_weight", 68)
                .putInt("water_ml", 0)
                .putInt("creatine_streak", 0)
                .putBoolean("motivation_on", true)
                .apply();
        for (String[] arr : FoodCatalog.categories().values()) for (String f : arr) prefs.edit().putBoolean("food_" + f, defaultFood(f)).apply();
    }

    private boolean defaultFood(String f) {
        String yes = "بيض،فول،عيش مصري بلدي،موز،شوفان،لبن،عسل،أرز أبيض،مكرونة،أوراك فراخ،جبنة قريش،فول سوداني،زبادي،بطاطس،طماطم،خيار،مياه،كرياتين";
        return yes.contains(f);
    }

    private View buildShell() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColor(R.color.bg));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        root.addView(content, cp);

        nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(dp(6), dp(8), dp(6), dp(8));
        nav.setBackgroundColor(0xFF111111);
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            TextView b = text(tabs[i], 12, true);
            b.setGravity(Gravity.CENTER);
            b.setPadding(dp(4), dp(4), dp(4), dp(4));
            b.setOnClickListener(v -> showTab(index));
            nav.addView(b, new LinearLayout.LayoutParams(0, dp(62), 1f));
        }
        root.addView(nav);
        return root;
    }

    private void showTab(int index) {
        activeTab = index;
        content.removeAllViews();
        updateNav();
        View page;
        if (index == 0) page = homePage();
        else if (index == 1) page = todayPage();
        else if (index == 2) page = workoutPage();
        else if (index == 3) page = kitchenPage();
        else page = morePage();
        content.addView(page);
        page.setAlpha(0f); page.setTranslationY(dp(18));
        page.animate().alpha(1f).translationY(0).setDuration(280).start();
    }

    private void updateNav() {
        for (int i = 0; i < nav.getChildCount(); i++) {
            TextView t = (TextView) nav.getChildAt(i);
            t.setTextColor(i == activeTab ? getColor(R.color.primary) : getColor(R.color.muted));
        }
    }

    private ScrollView page() {
        ScrollView s = new ScrollView(this);
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.VERTICAL);
        r.setPadding(dp(16), dp(20), dp(16), dp(30));
        r.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        s.addView(r);
        s.setTag(r);
        s.setBackgroundColor(getColor(R.color.bg));
        return s;
    }

    private LinearLayout rootOf(ScrollView s) { return (LinearLayout) s.getTag(); }

    private View homePage() {
        ScrollView s = page(); LinearLayout root = rootOf(s);
        String name = prefs.getString("name", "محمد");
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "صباح الخير" : hour < 18 ? "مساء النشاط" : "مساء الخير";
        root.addView(text(greeting + " يا " + name + " 💪", 28, true));
        TextView sub = text("كل يوم ملتزم فيه هو خطوة لنسخة أقوى منك", 15, false); sub.setTextColor(getColor(R.color.muted)); sub.setPadding(0, dp(4), 0, dp(14)); root.addView(sub);

        FitnessAdvisor.Advice a = FitnessAdvisor.today(prefs);
        LinearLayout hero = card();
        TextView heroTitle = text(a.title, 23, true); heroTitle.setTextColor(a.restDay ? getColor(R.color.warning) : getColor(R.color.success)); hero.addView(heroTitle);
        TextView heroBody = text(a.reason, 16, false); heroBody.setPadding(0, dp(8), 0, dp(8)); hero.addView(heroBody);
        TextView heroWorkout = text("الخطة: " + a.workout, 16, true); hero.addView(heroWorkout);
        root.addView(hero);

        root.addView(sectionTitle("هل ذهبت للجيم اليوم؟"));
        LinearLayout attendance = card();
        TextView status = text(prefs.getBoolean(FitnessAdvisor.keyForToday(), false) ? "✅ تم تسجيل تمرين اليوم" : "لم تسجل تمرين اليوم بعد", 18, true); attendance.addView(status);
        LinearLayout buttons = row();
        Button yes = primaryButton("نعم، ذهبت");
        Button no = secondaryButton("لا، لم أذهب");
        yes.setOnClickListener(v -> { FitnessAdvisor.setWentToday(prefs, true); toast("تم تسجيل التمرين 🔥"); showTab(0); });
        no.setOnClickListener(v -> { FitnessAdvisor.setWentToday(prefs, false); toast("تمام، سأعدل توصية اليوم"); showTab(0); });
        buttons.addView(yes, new LinearLayout.LayoutParams(0, dp(54), 1f));
        buttons.addView(no, new LinearLayout.LayoutParams(0, dp(54), 1f));
        attendance.addView(buttons); root.addView(attendance);

        root.addView(sectionTitle("حالتك الآن"));
        LinearLayout metrics = row();
        metrics.addView(metricCard("⚖️", prefs.getInt("weight", 60) + " كجم", "وزنك"), new LinearLayout.LayoutParams(0, dp(126), 1f));
        metrics.addView(metricCard("💧", prefs.getInt("water_ml", 0) + " مل", "المياه"), new LinearLayout.LayoutParams(0, dp(126), 1f));
        metrics.addView(metricCard("🔥", prefs.getInt("training_streak", 0) + " يوم", "التزام"), new LinearLayout.LayoutParams(0, dp(126), 1f));
        root.addView(metrics);

        root.addView(sectionTitle("الوجبة القادمة"));
        LinearLayout meal = card();
        meal.addView(text(nextMealText(), 18, true));
        TextView missing = text(missingForNextMeal(), 15, false); missing.setTextColor(getColor(R.color.muted)); missing.setPadding(0, dp(8), 0, 0); meal.addView(missing);
        root.addView(meal);

        root.addView(sectionTitle("مدربوك"));
        root.addView(coachesRow());

        root.addView(sectionTitle("تقدمك"));
        LinearLayout progress = card();
        int complete = dailyCompletion();
        TextView percent = text(complete + "% من اليوم مكتمل", 20, true); progress.addView(percent);
        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); bar.setMax(100); bar.setProgress(complete); bar.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.primary))); LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(14)); pp.setMargins(0, dp(12), 0, dp(6)); progress.addView(bar, pp);
        progress.addView(text("مياه • وجبات • تمرين • كرياتين • نوم", 14, false));
        root.addView(progress);
        return s;
    }

    private View todayPage() {
        ScrollView s = page(); LinearLayout root = rootOf(s);
        root.addView(titleBlock("📅 يومك", new SimpleDateFormat("EEEE، d MMMM", new Locale("ar")).format(new Date())));
        int i = 0;
        for (String[] item : AlarmScheduler.DEFAULTS) {
            LinearLayout c = card();
            String time = String.format(Locale.US, "%02d:%02d", Integer.parseInt(item[1]), Integer.parseInt(item[2]));
            TextView t = text(time + "  •  " + item[3], 18, true); c.addView(t);
            TextView body = text(item[4], 15, false); body.setTextColor(getColor(R.color.muted)); body.setPadding(0, dp(4), 0, 0); c.addView(body);
            final String key = "done_schedule_" + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "_" + i;
            CheckBox done = new CheckBox(this); done.setText("تم ✅"); done.setTextColor(getColor(R.color.text)); done.setChecked(prefs.getBoolean(key, false)); done.setOnCheckedChangeListener((b, checked) -> { prefs.edit().putBoolean(key, checked).apply(); }); c.addView(done);
            root.addView(c); i++;
        }
        Button alarms = primaryButton("تفعيل كل المنبهات"); alarms.setOnClickListener(v -> enableAlarms()); root.addView(alarms, fullButtonParams());
        return s;
    }

    private View workoutPage() {
        ScrollView s = page(); LinearLayout root = rootOf(s);
        root.addView(titleBlock("🏋️ التمرين", "الجيم يوم الجمعة إجازة — التطبيق يقترح الراحة حسب سجل حضورك"));
        FitnessAdvisor.Advice a = FitnessAdvisor.today(prefs);
        LinearLayout recommendation = card();
        recommendation.addView(text(a.title, 22, true));
        recommendation.addView(text(a.reason, 15, false));
        TextView plan = text(a.workout, 17, true); plan.setTextColor(getColor(R.color.primary)); plan.setPadding(0, dp(10), 0, 0); recommendation.addView(plan);
        root.addView(recommendation);

        root.addView(sectionTitle("تمرين الجيم المقترح"));
        String[][] gym = {
                {"Chest Press", "3 مجموعات × 10 تكرارات", "صدر"},
                {"Lat Pulldown", "3 مجموعات × 10 تكرارات", "ظهر"},
                {"Leg Press", "3 مجموعات × 12 تكرار", "رجل"},
                {"Shoulder Press", "2 مجموعات × 10 تكرارات", "كتف"},
                {"Cable Row", "3 مجموعات × 10 تكرارات", "ظهر"},
                {"Plank", "3 × 30–45 ثانية", "بطن"}
        };
        for (String[] x : gym) root.addView(exerciseCard(x[0], x[1], x[2], false));

        root.addView(sectionTitle("لو هتتمرن في البيت"));
        String[][] home = {
                {"Push Up", "4 مجموعات × 8–12 تكرار", "صدر وترايسبس"},
                {"Bodyweight Squat", "4 مجموعات × 15 تكرار", "رجل"},
                {"Reverse Lunge", "3 مجموعات × 10 لكل رجل", "رجل وتوازن"},
                {"Pike Push Up", "3 مجموعات × 6–10", "كتف"},
                {"Glute Bridge", "4 مجموعات × 15", "مؤخرة وخلفية"},
                {"Plank", "3 × 30–60 ثانية", "كور"}
        };
        for (String[] x : home) root.addView(exerciseCard(x[0], x[1], x[2], true));

        Button extra = secondaryButton("أريد تمرين يوم إضافي — هل هو مناسب؟");
        extra.setOnClickListener(v -> showExtraDayAdvice());
        root.addView(extra, fullButtonParams());
        return s;
    }

    private View kitchenPage() {
        ScrollView s = page(); LinearLayout root = rootOf(s);
        root.addView(titleBlock("🍳 مطبخي", "حدد الموجود عندك، وسيقترح التطبيق وجبات من المتاح"));
        foodChecks.clear();
        for (Map.Entry<String, String[]> e : FoodCatalog.categories().entrySet()) {
            root.addView(sectionTitle(e.getKey()));
            LinearLayout c = card();
            for (String f : e.getValue()) {
                CheckBox cb = new CheckBox(this); cb.setText(f); cb.setTextColor(getColor(R.color.text)); cb.setTextSize(16); cb.setChecked(prefs.getBoolean("food_" + f, false)); cb.setPadding(0, dp(5), 0, dp(5));
                cb.setOnCheckedChangeListener((b, checked) -> prefs.edit().putBoolean("food_" + f, checked).apply());
                foodChecks.put(f, cb); c.addView(cb);
            }
            root.addView(c);
        }
        root.addView(sectionTitle("اقتراح ذكي من الموجود"));
        LinearLayout idea = card(); idea.addView(text(smartMealFromInventory(), 18, true)); root.addView(idea);
        return s;
    }

    private View morePage() {
        ScrollView s = page(); LinearLayout root = rootOf(s);
        root.addView(titleBlock("⚙️ المزيد", "ملفك الشخصي، القياسات، المياه، الكرياتين والتنبيهات"));

        LinearLayout profile = card();
        int h = prefs.getInt("height", 174), w = prefs.getInt("weight", 60), target = prefs.getInt("target_weight", 68);
        profile.addView(text("👤 " + prefs.getString("name", "محمد"), 22, true));
        profile.addView(text("الطول: " + h + " سم   •   الوزن: " + w + " كجم   •   الهدف: " + target + " كجم", 16, false));
        Button edit = primaryButton("تعديل بياناتي"); edit.setOnClickListener(v -> showProfileDialog(false)); profile.addView(edit, fullButtonParams()); root.addView(profile);

        LinearLayout nutrition = card();
        nutrition.addView(text("🎯 أهداف يومية مقترحة", 20, true));
        nutrition.addView(text("بروتين تقريبي: " + FitnessAdvisor.suggestedProtein(w) + " جم\nمياه: " + FitnessAdvisor.suggestedWaterMl(w) + " مل\nالهدف الوزني المبدئي: " + FitnessAdvisor.targetWeight(h, w) + " كجم", 17, false));
        root.addView(nutrition);

        LinearLayout water = card();
        TextView waterText = text("💧 المياه اليوم: " + prefs.getInt("water_ml", 0) + " مل", 20, true); water.addView(waterText);
        LinearLayout wb = row();
        Button add250 = secondaryButton("+250 مل"); Button reset = secondaryButton("تصفير");
        add250.setOnClickListener(v -> { prefs.edit().putInt("water_ml", prefs.getInt("water_ml", 0) + 250).apply(); showTab(4); });
        reset.setOnClickListener(v -> { prefs.edit().putInt("water_ml", 0).apply(); showTab(4); });
        wb.addView(add250, new LinearLayout.LayoutParams(0, dp(52), 1f)); wb.addView(reset, new LinearLayout.LayoutParams(0, dp(52), 1f)); water.addView(wb); root.addView(water);

        LinearLayout creatine = card();
        creatine.addView(text("🧪 الكرياتين", 20, true));
        boolean today = prefs.getBoolean("creatine_" + Calendar.getInstance().get(Calendar.DAY_OF_YEAR), false);
        TextView ct = text(today ? "✅ أخذت 5 جم اليوم" : "لم تسجل جرعة اليوم", 17, false); creatine.addView(ct);
        Button dose = primaryButton("سجل 5 جم الآن"); dose.setOnClickListener(v -> { if (!today) prefs.edit().putBoolean("creatine_" + Calendar.getInstance().get(Calendar.DAY_OF_YEAR), true).putInt("creatine_streak", prefs.getInt("creatine_streak", 0) + 1).apply(); showTab(4); }); creatine.addView(dose, fullButtonParams()); root.addView(creatine);

        LinearLayout alerts = card(); alerts.addView(text("🔔 التنبيهات", 20, true));
        CheckBox motivation = new CheckBox(this); motivation.setText("إشعارات تشجيع يومية"); motivation.setTextColor(getColor(R.color.text)); motivation.setChecked(prefs.getBoolean("motivation_on", true)); motivation.setOnCheckedChangeListener((b, checked) -> prefs.edit().putBoolean("motivation_on", checked).apply()); alerts.addView(motivation);
        Button alarm = primaryButton("تفعيل المنبهات القوية"); alarm.setOnClickListener(v -> enableAlarms()); alerts.addView(alarm, fullButtonParams()); root.addView(alerts);

        root.addView(sectionTitle("ملاحظة صحية"));
        TextView note = text("التطبيق يقدم إرشادات عامة للمبتدئين، وليس تشخيصًا طبيًا. الألم الحاد، الدوخة، إصابة مستمرة أو مرض مزمن يحتاج تقييم مختص.", 14, false); note.setTextColor(getColor(R.color.muted)); root.addView(note);
        return s;
    }

    private View coachesRow() {
        HorizontalScrollView hsv = new HorizontalScrollView(this); hsv.setHorizontalScrollBarEnabled(false);
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); row.setPadding(0, 0, 0, dp(8));
        int[] imgs = {R.drawable.coach_1, R.drawable.coach_2, R.drawable.coach_3};
        String[] names = {"كابتن أحمد", "كابتن محمود", "كابتن عمر"};
        String[] roles = {"بناء عضلات", "تغذية وتمارين", "لياقة وقوة"};
        for (int i = 0; i < imgs.length; i++) {
            LinearLayout c = card(); LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(170), ViewGroup.LayoutParams.WRAP_CONTENT); lp.setMargins(dp(6), 0, dp(6), 0); c.setLayoutParams(lp);
            ImageView im = new ImageView(this); im.setImageResource(imgs[i]); im.setScaleType(ImageView.ScaleType.CENTER_CROP); c.addView(im, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(150)));
            c.addView(text(names[i], 17, true)); TextView r = text(roles[i], 14, false); r.setTextColor(getColor(R.color.muted)); c.addView(r); row.addView(c);
        }
        hsv.addView(row); return hsv;
    }

    private View exerciseCard(String name, String prescription, String target, boolean home) {
        LinearLayout c = card();
        TextView n = text((home ? "🏠 " : "🏋️ ") + name, 19, true); c.addView(n);
        TextView p = text(prescription, 16, true); p.setTextColor(getColor(R.color.primary)); p.setPadding(0, dp(6), 0, dp(4)); c.addView(p);
        c.addView(text("العضلات المستهدفة: " + target, 14, false));
        TextView hint = text(home ? "ابدأ ببطء، وحافظ على الحركة الكاملة. توقف إذا ظهر ألم حاد." : "استخدم وزنًا يسمح لك بإنهاء التكرارات بتكنيك ثابت.", 13, false); hint.setTextColor(getColor(R.color.muted)); hint.setPadding(0, dp(6), 0, 0); c.addView(hint);
        return c;
    }

    private View metricCard(String icon, String value, String label) {
        LinearLayout c = card(); c.setGravity(Gravity.CENTER); c.setPadding(dp(8), dp(12), dp(8), dp(12));
        TextView i = text(icon, 25, false); i.setGravity(Gravity.CENTER); c.addView(i);
        TextView v = text(value, 18, true); v.setGravity(Gravity.CENTER); c.addView(v);
        TextView l = text(label, 13, false); l.setGravity(Gravity.CENTER); l.setTextColor(getColor(R.color.muted)); c.addView(l); return c;
    }

    private void showExtraDayAdvice() {
        FitnessAdvisor.Advice a = FitnessAdvisor.today(prefs);
        String msg;
        if (a.restDay) msg = "اليوم أنصحك بالراحة. تمرين إضافي ليس أفضل لمجرد أنه أكثر. التعافي مهم لبناء العضلات.";
        else if (prefs.getInt("training_streak", 0) >= 2) msg = "يمكنك التدريب اليوم حسب الخطة، لكن لا تضف حصة ثانية. لو أنت مبتدئ، 3 أيام مقاومة أسبوعيًا كافية كبداية.";
        else msg = "يوم إضافي خفيف ممكن لو جسمك مرتاح، نومك جيد، ولا يوجد ألم عضلي شديد. اجعله خفيفًا أو تمرين تقنية.";
        new AlertDialog.Builder(this).setTitle("هل التمرين الإضافي مناسب؟").setMessage(msg).setPositiveButton("فهمت", null).show();
    }

    private void showProfileDialog(boolean first) {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(20), dp(6), dp(20), 0); box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        EditText name = field("الاسم", prefs.getString("name", "محمد"), false);
        EditText age = field("العمر", String.valueOf(prefs.getInt("age", 24)), true);
        EditText height = field("الطول بالسم", String.valueOf(prefs.getInt("height", 174)), true);
        EditText weight = field("الوزن بالكجم", String.valueOf(prefs.getInt("weight", 60)), true);
        EditText target = field("الوزن المستهدف", String.valueOf(prefs.getInt("target_weight", 68)), true);
        box.addView(name); box.addView(age); box.addView(height); box.addView(weight); box.addView(target);
        AlertDialog d = new AlertDialog.Builder(this).setTitle(first ? "جهز ملفك الشخصي" : "تعديل بياناتك").setView(box).setCancelable(!first).setPositiveButton("حفظ", null).create();
        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                int a = Integer.parseInt(age.getText().toString().trim()); int h = Integer.parseInt(height.getText().toString().trim()); int w = Integer.parseInt(weight.getText().toString().trim()); int t = Integer.parseInt(target.getText().toString().trim());
                if (a < 14 || a > 90 || h < 120 || h > 230 || w < 30 || w > 250 || t < 30 || t > 250) throw new IllegalArgumentException();
                prefs.edit().putString("name", name.getText().toString().trim()).putInt("age", a).putInt("height", h).putInt("weight", w).putInt("target_weight", t).putBoolean("profile_ready", true).apply();
                d.dismiss(); showTab(activeTab);
            } catch (Exception e) { toast("راجع العمر والطول والوزن"); }
        }));
        d.show();
    }

    private EditText field(String hint, String value, boolean number) {
        EditText e = new EditText(this); e.setHint(hint); e.setText(value); e.setTextColor(getColor(R.color.text)); e.setHintTextColor(getColor(R.color.muted)); e.setSingleLine(true); if (number) e.setInputType(InputType.TYPE_CLASS_NUMBER); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(58)); p.setMargins(0, dp(4), 0, dp(4)); e.setLayoutParams(p); return e;
    }

    private String nextMealText() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h < 9) return "🍳 الفطار 8:30 — 3 بيضات + فول + عيش مصري بلدي + موزة";
        if (h < 12) return "🥣 قبل التمرين 11:00 — شوفان + لبن + موزة + عسل";
        if (h < 15) return "🧪 بعد التمرين — 5 جم كرياتين ثم الغداء";
        if (h < 19) return "🥛 السناك 18:00 — لبن + موز + فول سوداني";
        if (h < 22) return "🍳 العشاء 21:00 — بيض + فول أو جبنة قريش + عيش مصري";
        return "🥛 قبل النوم — لبن أو زبادي";
    }

    private String missingForNextMeal() {
        String[] req;
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h < 9) req = new String[]{"بيض","فول","عيش مصري بلدي","موز"};
        else if (h < 12) req = new String[]{"شوفان","لبن","موز","عسل"};
        else if (h < 15) req = new String[]{"كرياتين","أرز أبيض","أوراك فراخ"};
        else if (h < 19) req = new String[]{"لبن","موز","فول سوداني"};
        else req = new String[]{"بيض","عيش مصري بلدي"};
        List<String> miss = new ArrayList<>(); for (String x : req) if (!prefs.getBoolean("food_" + x, false)) miss.add(x);
        return miss.isEmpty() ? "✅ كل المكونات الأساسية موجودة" : "🛒 ناقصك: " + join(miss);
    }

    private String smartMealFromInventory() {
        if (has("بيض") && has("عيش مصري بلدي") && has("فول")) return "فطار اقتصادي قوي: 3 بيضات + فول + 2 رغيف عيش مصري بلدي";
        if (has("أرز أبيض") && has("أوراك فراخ")) return "غداء مناسب للزيادة: أرز + أوراك فراخ + خضار متاح";
        if (has("شوفان") && has("لبن") && has("موز")) return "سناك عالي السعرات: شوفان + لبن + موز في الخلاط";
        if (has("عدس") && has("أرز أبيض")) return "وجبة اقتصادية: عدس + أرز، وأضف بيض لو متاح";
        return "حدد المزيد من الأطعمة الموجودة عندك وسأكوّن وجبة مناسبة تلقائيًا.";
    }

    private boolean has(String f) { return prefs.getBoolean("food_" + f, false); }

    private int dailyCompletion() {
        int done = 0, total = 5;
        if (prefs.getBoolean(FitnessAdvisor.keyForToday(), false)) done++;
        if (prefs.getInt("water_ml", 0) >= 1500) done++;
        if (prefs.getBoolean("creatine_" + Calendar.getInstance().get(Calendar.DAY_OF_YEAR), false)) done++;
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 15) done++;
        int scheduleDone = 0; for (int i = 0; i < AlarmScheduler.DEFAULTS.length; i++) if (prefs.getBoolean("done_schedule_" + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "_" + i, false)) scheduleDone++;
        if (scheduleDone >= 4) done++;
        return done * 100 / total;
    }

    private void requestPermissionsAndSchedule() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 44);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) toast("فعّل المنبهات الدقيقة لضمان المواعيد"); else AlarmScheduler.scheduleAll(this);
    }

    private void enableAlarms() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) { Intent settings = AlarmScheduler.exactAlarmSettings(this); if (settings != null) startActivity(settings); return; }
        AlarmScheduler.scheduleAll(this); toast("تم تفعيل كل المنبهات ✅");
    }

    @Override protected void onResume() { super.onResume(); if (content != null && activeTab == 0) showTab(0); }

    private LinearLayout titleBlock(String title, String subtitle) { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.addView(text(title, 28, true)); TextView s = text(subtitle, 15, false); s.setTextColor(getColor(R.color.muted)); s.setPadding(0, dp(4), 0, dp(14)); l.addView(s); return l; }
    private LinearLayout card() { LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setBackgroundResource(R.drawable.bg_card); c.setPadding(dp(16), dp(16), dp(16), dp(16)); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); p.setMargins(0, 0, 0, dp(12)); c.setLayoutParams(p); return c; }
    private LinearLayout row() { LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); r.setGravity(Gravity.CENTER_VERTICAL); return r; }
    private TextView sectionTitle(String s) { TextView t = text(s, 20, true); t.setPadding(0, dp(12), 0, dp(8)); return t; }
    private TextView text(String s, int sp, boolean bold) { TextView t = new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(getColor(R.color.text)); t.setGravity(Gravity.RIGHT); t.setLineSpacing(0, 1.12f); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t; }
    private Button primaryButton(String s) { Button b = new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(16); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD); b.setBackgroundResource(R.drawable.bg_button); return b; }
    private Button secondaryButton(String s) { Button b = new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(15); b.setBackgroundResource(R.drawable.bg_soft); return b; }
    private LinearLayout.LayoutParams fullButtonParams() { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(56)); p.setMargins(0, dp(10), 0, dp(4)); return p; }
    private String join(List<String> xs) { StringBuilder b = new StringBuilder(); for (int i = 0; i < xs.size(); i++) { if (i > 0) b.append("، "); b.append(xs.get(i)); } return b.toString(); }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density); }
}

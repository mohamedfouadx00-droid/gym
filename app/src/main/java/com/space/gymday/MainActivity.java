package com.space.gymday;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private static final int BG = Color.rgb(10, 7, 8);
    private static final int CARD = Color.rgb(30, 16, 18);
    private static final int CARD_2 = Color.rgb(48, 22, 25);
    private static final int ACCENT = Color.rgb(232, 25, 35);
    private static final int TEXT = Color.WHITE;
    private static final int MUTED = Color.rgb(174, 190, 179);
    private static final int DANGER = Color.rgb(255, 100, 100);

    private SharedPreferences prefs;
    private LinearLayout rootContent;
    private LinearLayout navBar;
    private int currentTab = 0;

    private final DecimalFormat qtyFormat = new DecimalFormat("0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("gym_app_v2", MODE_PRIVATE);

        seedDefaults();

        setContentView(buildShell());

        requestPermissionsAndSchedule();

        showTab(0);
    }

    private View buildShell() {
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.VERTICAL);
        shell.setBackgroundColor(BG);
        shell.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        rootContent = new LinearLayout(this);
        rootContent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                );

        shell.addView(rootContent, cp);

        navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setGravity(Gravity.CENTER);
        navBar.setPadding(dp(6), dp(7), dp(6), dp(8));
        navBar.setBackgroundColor(Color.rgb(18, 8, 10));

        String[] titles = {
                "الرئيسية",
                "اليوم",
                "التمرين",
                "مطبخي",
                "المزيد"
        };

        String[] icons = {
                "⌂",
                "◷",
                "🏋",
                "🍳",
                "•••"
        };

        for (int i = 0; i < titles.length; i++) {
            final int tab = i;

            TextView item = txt(
                    icons[i] + "\n" + titles[i],
                    12,
                    false
            );

            item.setTag(i);
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(6), dp(5), dp(6), dp(5));

            item.setOnClickListener(v -> showTab(tab));

            navBar.addView(
                    item,
                    new LinearLayout.LayoutParams(
                            0,
                            dp(58),
                            1f
                    )
            );
        }

        shell.addView(
                navBar,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(66)
                )
        );

        return shell;
    }

    private void showTab(int index) {
        currentTab = index;

        rootContent.removeAllViews();

        View page;

        if (index == 0) {
            page = homePage();
        } else if (index == 1) {
            page = dayPage();
        } else if (index == 2) {
            page = workoutPage();
        } else if (index == 3) {
            page = kitchenPage();
        } else {
            page = morePage();
        }

        rootContent.addView(
                page,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        updateNav();

        page.setAlpha(0f);
        page.setTranslationY(dp(12));

        page.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(280)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void updateNav() {
        for (int i = 0; i < navBar.getChildCount(); i++) {
            TextView v = (TextView) navBar.getChildAt(i);

            boolean active = i == currentTab;

            v.setTextColor(active ? ACCENT : MUTED);

            v.setTypeface(
                    null,
                    active ? Typeface.BOLD : Typeface.NORMAL
            );

            v.setBackgroundColor(
                    active
                            ? Color.rgb(58, 18, 22)
                            : Color.TRANSPARENT
            );
        }
    }

    private ScrollView pageScroll() {
        ScrollView scroll = new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        return scroll;
    }

    private LinearLayout pageRoot(ScrollView scroll) {
        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(22), dp(18), dp(28));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        scroll.addView(root);

        return root;
    }

    private View homePage() {
        ScrollView scroll = pageScroll();

        LinearLayout root = pageRoot(scroll);

        String name = prefs.getString("name", "محمد");

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        String greeting =
                hour < 12
                        ? "صباح الخير"
                        : hour < 18
                        ? "مساء النشاط"
                        : "مساء الخير";

        TextView brand = txt("GYM", 17, true);
        brand.setTextColor(ACCENT);
        brand.setLetterSpacing(.18f);

        root.addView(brand);

        root.addView(
                txt(
                        greeting + " يا " + name + " 💪",
                        28,
                        true
                )
        );

        TextView sub = txt(
                "هدفك: "
                        + prefs.getString(
                        "goal",
                        "زيادة الوزن وبناء العضلات"
                ),
                15,
                false
        );

        sub.setTextColor(MUTED);
        sub.setPadding(0, dp(4), 0, dp(18));

        root.addView(sub);

        ImageView hero = new ImageView(this);

        hero.setImageResource(R.drawable.gym_hero);
        hero.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout.LayoutParams hp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(190)
                );

        hp.setMargins(0, 0, 0, dp(14));

        hero.setLayoutParams(hp);

        hero.setOnClickListener(v -> showTab(2));

        root.addView(hero);

        hero.setAlpha(0.86f);

        hero.animate()
                .alpha(1f)
                .scaleX(1.02f)
                .scaleY(1.02f)
                .setDuration(1300)
                .withEndAction(
                        () -> hero.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(900)
                                .start()
                )
                .start();

        root.addView(weightHero());

        root.addView(section("قرار جسمك النهارده"));
        root.addView(recoveryDecisionCard());

        root.addView(section("مدربوك"));
        root.addView(coachesRow());

        root.addView(section("تقدم اليوم"));
        root.addView(todayProgressCard());

        root.addView(section("الوجبة القادمة"));
        root.addView(nextMealCard());

        root.addView(section("المهام السريعة"));
        root.addView(quickActions());

        root.addView(section("رسالة اليوم"));

        LinearLayout motivation = card();

        TextView quote = txt(
                motivationForDay(),
                19,
                true
        );

        quote.setTextColor(ACCENT);
        quote.setGravity(Gravity.CENTER);
        quote.setPadding(0, dp(8), 0, dp(8));

        motivation.addView(quote);

        root.addView(motivation);

        return scroll;
    }

    private View weightHero() {
        LinearLayout box = card();

        double current = getWeight();

        double goal = prefs.getFloat(
                "goal_weight",
                68f
        );

        LinearLayout row = hRow();

        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);

        TextView l1 = txt(
                "وزنك الحالي",
                14,
                false
        );

        l1.setTextColor(MUTED);

        TextView l2 = txt(
                qtyFormat.format(current) + " كجم",
                29,
                true
        );

        l2.setTextColor(TEXT);

        left.addView(l1);
        left.addView(l2);

        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(Gravity.LEFT);

        TextView r1 = txt(
                "الهدف",
                14,
                false
        );

        r1.setTextColor(MUTED);
        r1.setGravity(Gravity.LEFT);

        TextView r2 = txt(
                qtyFormat.format(goal) + " كجم",
                24,
                true
        );

        r2.setTextColor(ACCENT);
        r2.setGravity(Gravity.LEFT);

        right.addView(r1);
        right.addView(r2);

        row.addView(
                left,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        row.addView(
                right,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        box.addView(row);

        ProgressBar progress =
                new ProgressBar(
                        this,
                        null,
                        android.R.attr.progressBarStyleHorizontal
                );

        int pct = (int) Math.max(
                0,
                Math.min(
                        100,
                        ((current - 60.0)
                                / Math.max(1, goal - 60.0))
                                * 100
                )
        );

        progress.setMax(100);
        progress.setProgress(pct);

        progress.setProgressTintList(
                android.content.res.ColorStateList.valueOf(
                        ACCENT
                )
        );

        LinearLayout.LayoutParams pp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(9)
                );

        pp.setMargins(
                0,
                dp(16),
                0,
                dp(8)
        );

        box.addView(progress, pp);

        TextView caption = txt(
                "التقدم نحو الهدف: " + pct + "%",
                13,
                false
        );

        caption.setTextColor(MUTED);

        box.addView(caption);

        return box;
    }

    private View todayProgressCard() {
        LinearLayout box = card();

        int meals =
                prefs.getInt(
                        dayKey("meals"),
                        0
                );

        int water =
                prefs.getInt(
                        dayKey("water"),
                        0
                );

        boolean creatine =
                prefs.getBoolean(
                        dayKey("creatine"),
                        false
                );

        boolean workout =
                prefs.getBoolean(
                        dayKey("workout"),
                        false
                );

        int done =
                Math.min(7, meals)
                        + (water >= 3000 ? 1 : 0)
                        + (creatine ? 1 : 0)
                        + (workout ? 1 : 0);

        int pct =
                (int) (done / 10.0 * 100);

        TextView pctView =
                txt(
                        pct + "%",
                        38,
                        true
                );

        pctView.setTextColor(ACCENT);
        pctView.setGravity(Gravity.CENTER);

        box.addView(pctView);

        TextView cap =
                txt(
                        "من خطتك اليومية مكتمل",
                        14,
                        false
                );

        cap.setTextColor(MUTED);
        cap.setGravity(Gravity.CENTER);

        box.addView(cap);

        LinearLayout stats = hRow();

        stats.setPadding(
                0,
                dp(18),
                0,
                0
        );

        stats.addView(
                statMini(
                        "🍽",
                        meals + "/7",
                        "وجبات"
                ),
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        stats.addView(
                statMini(
                        "💧",
                        water + " مل",
                        "مياه"
                ),
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        stats.addView(
                statMini(
                        "🧪",
                        creatine ? "تم" : "لسه",
                        "كرياتين"
                ),
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        box.addView(stats);

        return box;
    }

    private View nextMealCard() {
        AppData.Meal meal = nextMeal();

        LinearLayout box = card();

        LinearLayout row = hRow();

        TextView icon = txt(
                meal.emoji,
                35,
                false
        );

        icon.setGravity(Gravity.CENTER);

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        TextView n = txt(
                meal.name + " • " + meal.time,
                20,
                true
        );

        TextView note = txt(
                meal.note,
                14,
                false
        );

        note.setTextColor(MUTED);

        note.setPadding(
                0,
                dp(4),
                0,
                0
        );

        info.addView(n);
        info.addView(note);

        row.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(56),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        row.addView(
                info,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        box.addView(row);

        List<String> missing =
                missingIngredients(meal);

        TextView status = txt(
                missing.isEmpty()
                        ? "✅ كل المكونات موجودة عندك"
                        : "🛒 ناقصك: " + join(missing),
                14,
                true
        );

        status.setTextColor(
                missing.isEmpty()
                        ? ACCENT
                        : DANGER
        );

        status.setPadding(
                0,
                dp(14),
                0,
                dp(8)
        );

        box.addView(status);

        Button done =
                button(
                        "أكلت الوجبة ✅",
                        ACCENT,
                        BG
                );

        done.setOnClickListener(v -> {
            int c =
                    prefs.getInt(
                            dayKey("meals"),
                            0
                    );

            prefs.edit()
                    .putInt(
                            dayKey("meals"),
                            Math.min(7, c + 1)
                    )
                    .apply();

            consumeMeal(meal);

            toast(
                    "عاش! سجلنا الوجبة 💪"
            );

            showTab(0);
        });

        box.addView(done);

        return box;
    }

    private View quickActions() {
        HorizontalScrollView hsv =
                new HorizontalScrollView(this);

        hsv.setHorizontalScrollBarEnabled(false);

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        row.addView(
                actionChip(
                        "💧 +300 مل",
                        v -> addWater(300)
                )
        );

        row.addView(
                actionChip(
                        "🧪 كرياتين",
                        v -> {
                            prefs.edit()
                                    .putBoolean(
                                            dayKey("creatine"),
                                            true
                                    )
                                    .apply();

                            changeQty(
                                    "كرياتين",
                                    -1
                            );

                            toast(
                                    "تم تسجيل الكرياتين 🔥"
                            );

                            showTab(0);
                        }
                )
        );

        row.addView(
                actionChip(
                        "⚖️ سجل وزن",
                        v -> weightDialog()
                )
        );

        row.addView(
                actionChip(
                        "✅ رحت الجيم",
                        v -> markGymAttendance(true)
                )
        );

        row.addView(
                actionChip(
                        "❌ مرحتش",
                        v -> markGymAttendance(false)
                )
        );

        hsv.addView(row);

        return hsv;
    }

    private View dayPage() {
        ScrollView scroll = pageScroll();

        LinearLayout root = pageRoot(scroll);

        root.addView(
                pageTitle(
                        "يومي",
                        "كل خطوة صغيرة بتجمع نتيجة كبيرة"
                )
        );

        for (String[] item : AppData.SCHEDULE) {
            LinearLayout box = card();

            LinearLayout row = hRow();

            TextView time = txt(
                    String.format(
                            "%02d:%02d",
                            Integer.parseInt(item[1]),
                            Integer.parseInt(item[2])
                    ),
                    19,
                    true
            );

            time.setTextColor(ACCENT);

            LinearLayout info =
                    new LinearLayout(this);

            info.setOrientation(
                    LinearLayout.VERTICAL
            );

            info.addView(
                    txt(
                            item[3],
                            18,
                            true
                    )
            );

            TextView note =
                    txt(
                            item[4],
                            14,
                            false
                    );

            note.setTextColor(MUTED);

            info.addView(note);

            row.addView(
                    time,
                    new LinearLayout.LayoutParams(
                            dp(82),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            row.addView(
                    info,
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1f
                    )
            );

            box.addView(row);

            root.addView(box);
        }

        Button alarms =
                button(
                        "تفعيل كل المنبهات",
                        ACCENT,
                        BG
                );

        alarms.setOnClickListener(
                v -> enableAlarms()
        );

        root.addView(alarms);

        return scroll;
    }

    private View workoutPage() {
        ScrollView scroll = pageScroll();

        LinearLayout root = pageRoot(scroll);

        root.addView(
                pageTitle(
                        "التمرين",
                        "GYM Coach يقرر معاك: تمرين، راحة، أو تمرين بيت"
                )
        );

        root.addView(
                recoveryDecisionCard()
        );

        root.addView(
                section(
                        "هل رحت الجيم النهارده؟"
                )
        );

        LinearLayout attendance = hRow();

        Button yes =
                button(
                        "أيوه ✅",
                        ACCENT,
                        Color.WHITE
                );

        yes.setOnClickListener(
                v -> markGymAttendance(true)
        );

        Button no =
                button(
                        "لا ❌",
                        CARD_2,
                        Color.WHITE
                );

        no.setOnClickListener(
                v -> markGymAttendance(false)
        );

        attendance.addView(
                yes,
                new LinearLayout.LayoutParams(
                        0,
                        dp(58),
                        1f
                )
        );

        attendance.addView(
                no,
                new LinearLayout.LayoutParams(
                        0,
                        dp(58),
                        1f
                )
        );

        root.addView(attendance);

        root.addView(
                section(
                        "خطة الجيم المقترحة"
                )
        );

        root.addView(
                workoutSummary()
        );

        String[][] exercises =
                personalizedGymPlan();

        for (int i = 0; i < exercises.length; i++) {
            root.addView(
                    exerciseCard(
                            i + 1,
                            exercises[i][0],
                            exercises[i][1],
                            exercises[i][2]
                    )
            );
        }

        Button complete =
                button(
                        "إنهاء تمرين اليوم 🔥",
                        ACCENT,
                        Color.WHITE
                );

        complete.setOnClickListener(
                v -> completeWorkout()
        );

        root.addView(complete);

        root.addView(
                section(
                        "عايز تتمرن يوم زيادة؟"
                )
        );

        LinearLayout extra = card();

        TextView exText = txt(
                "اضغط هنا وGYM هيقيم هل اليوم الإضافي مناسب لجسمك بناءً على آخر تمارينك والنوم والإجهاد.",
                15,
                false
        );

        exText.setTextColor(MUTED);

        extra.addView(exText);

        Button assess =
                button(
                        "قيّم يوم التمرين الإضافي",
                        CARD_2,
                        Color.WHITE
                );

        assess.setOnClickListener(
                v -> toast(
                        extraDayAssessment()
                )
        );

        extra.addView(assess);

        root.addView(extra);

        root.addView(
                section(
                        "لو هتتمرن في البيت"
                )
        );

        String[][] home =
                AppData.HOME_WORKOUTS;

        for (int i = 0; i < home.length; i++) {
            root.addView(
                    homeExerciseCard(
                            home[i][0],
                            home[i][1],
                            home[i][2],
                            home[i][3]
                    )
            );
        }

        return scroll;
    }

    private View workoutSummary() {
        LinearLayout box = card();

        LinearLayout row = hRow();

        row.addView(
                statMini(
                        "🔥",
                        String.valueOf(
                                prefs.getInt(
                                        "workout_streak",
                                        0
                                )
                        ),
                        "Streak"
                ),
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        row.addView(
                statMini(
                        "🏋️",
                        String.valueOf(
                                prefs.getInt(
                                        "workout_count",
                                        0
                                )
                        ),
                        "تمرين"
                ),
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        row.addView(
                statMini(
                        "⏱",
                        "45–60",
                        "دقيقة"
                ),
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        box.addView(row);

        return box;
    }

    private View kitchenPage() {
        ScrollView scroll = pageScroll();

        LinearLayout root = pageRoot(scroll);

        root.addView(
                pageTitle(
                        "مطبخي",
                        "المخزون الذكي — الكميات تتخصم مع الوجبات"
                )
        );

        LinearLayout shopping = card();

        List<String> low =
                lowStockNames();

        TextView lowTitle =
                txt(
                        "🛒 قائمة المشتريات الذكية",
                        19,
                        true
                );

        shopping.addView(lowTitle);

        TextView lowText = txt(
                low.isEmpty()
                        ? "مخزونك كويس حاليًا ✅"
                        : join(low),
                14,
                false
        );

        lowText.setTextColor(
                low.isEmpty()
                        ? ACCENT
                        : MUTED
        );

        lowText.setPadding(
                0,
                dp(8),
                0,
                0
        );

        shopping.addView(lowText);

        root.addView(shopping);

        for (
                Map.Entry<
                        String,
                        List<AppData.FoodItem>
                        > entry
                : AppData.foodsByCategory().entrySet()
        ) {
            root.addView(
                    section(
                            entry.getKey()
                    )
            );

            LinearLayout group = card();

            for (
                    AppData.FoodItem item
                    : entry.getValue()
            ) {
                group.addView(
                        inventoryRow(item)
                );
            }

            root.addView(group);
        }

        return scroll;
    }

    private View inventoryRow(
            AppData.FoodItem item
    ) {
        LinearLayout row = hRow();

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                0,
                dp(7),
                0,
                dp(7)
        );

        TextView icon =
                txt(
                        item.emoji,
                        25,
                        false
                );

        icon.setGravity(
                Gravity.CENTER
        );

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        TextView name =
                txt(
                        item.name,
                        16,
                        true
                );

        TextView qty =
                txt(
                        qtyFormat.format(
                                getQty(
                                        item.name,
                                        item.defaultQty
                                )
                        )
                                + " "
                                + item.unit,
                        13,
                        false
                );

        qty.setTextColor(MUTED);

        qty.setTag(
                "qty_" + item.name
        );

        info.addView(name);
        info.addView(qty);

        Button minus =
                miniButton("−");

        Button plus =
                miniButton("+");

        minus.setOnClickListener(v -> {
            changeQty(
                    item.name,
                    -stepFor(item)
            );

            qty.setText(
                    qtyFormat.format(
                            getQty(
                                    item.name,
                                    item.defaultQty
                            )
                    )
                            + " "
                            + item.unit
            );
        });

        plus.setOnClickListener(v -> {
            changeQty(
                    item.name,
                    stepFor(item)
            );

            qty.setText(
                    qtyFormat.format(
                            getQty(
                                    item.name,
                                    item.defaultQty
                            )
                    )
                            + " "
                            + item.unit
            );
        });

        row.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(45)
                )
        );

        row.addView(
                info,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        row.addView(minus);
        row.addView(plus);

        return row;
    }

    private View morePage() {
        ScrollView scroll =
                pageScroll();

        LinearLayout root =
                pageRoot(scroll);

        root.addView(
                pageTitle(
                        "المزيد",
                        "بياناتك هي اللي بتخلي النصائح والتمرين مناسبين ليك"
                )
        );

        root.addView(
                section(
                        "ملفك الشخصي"
                )
        );

        LinearLayout profile = card();

        EditText name =
                edit(
                        "الاسم",
                        prefs.getString(
                                "name",
                                "محمد"
                        ),
                        false
                );

        profile.addView(name);

        EditText age =
                edit(
                        "السن",
                        String.valueOf(
                                prefs.getInt(
                                        "age",
                                        22
                                )
                        ),
                        true
                );

        profile.addView(age);

        EditText height =
                edit(
                        "الطول بالسنتيمتر",
                        String.valueOf(
                                prefs.getInt(
                                        "height_cm",
                                        174
                                )
                        ),
                        true
                );

        profile.addView(height);

        EditText weight =
                edit(
                        "الوزن الحالي",
                        qtyFormat.format(
                                getWeight()
                        ),
                        true
                );

        profile.addView(weight);

        EditText goal =
                edit(
                        "الوزن المستهدف",
                        String.valueOf(
                                prefs.getFloat(
                                        "goal_weight",
                                        68f
                                )
                        ),
                        true
                );

        profile.addView(goal);

        EditText sleep =
                edit(
                        "ساعات نومك آخر ليلة",
                        String.valueOf(
                                prefs.getFloat(
                                        "sleep_hours",
                                        7.5f
                                )
                        ),
                        true
                );

        profile.addView(sleep);

        EditText soreness =
                edit(
                        "الإجهاد العضلي من 0 إلى 10",
                        String.valueOf(
                                prefs.getInt(
                                        "soreness",
                                        3
                                )
                        ),
                        true
                );

        profile.addView(soreness);

        Button save =
                button(
                        "حفظ وتحديث الخطة",
                        ACCENT,
                        Color.WHITE
                );

        save.setOnClickListener(v -> {
            try {
                int ageV =
                        Integer.parseInt(
                                age.getText()
                                        .toString()
                        );

                int heightV =
                        Integer.parseInt(
                                height.getText()
                                        .toString()
                        );

                double weightV =
                        Double.parseDouble(
                                weight.getText()
                                        .toString()
                        );

                float goalV =
                        Float.parseFloat(
                                goal.getText()
                                        .toString()
                        );

                float sleepV =
                        Float.parseFloat(
                                sleep.getText()
                                        .toString()
                        );

                int soreV =
                        Math.max(
                                0,
                                Math.min(
                                        10,
                                        Integer.parseInt(
                                                soreness
                                                        .getText()
                                                        .toString()
                                        )
                                )
                        );

                prefs.edit()
                        .putString(
                                "name",
                                name.getText()
                                        .toString()
                                        .trim()
                        )
                        .putInt(
                                "age",
                                ageV
                        )
                        .putInt(
                                "height_cm",
                                heightV
                        )
                        .putLong(
                                "current_weight",
                                Double.doubleToRawLongBits(
                                        weightV
                                )
                        )
                        .putFloat(
                                "goal_weight",
                                goalV
                        )
                        .putFloat(
                                "sleep_hours",
                                sleepV
                        )
                        .putInt(
                                "soreness",
                                soreV
                        )
                        .apply();

                toast(
                        "تم تحديث خطتك بناءً على بياناتك ✅"
                );

                showTab(4);

            } catch (Exception e) {
                toast(
                        "راجع الأرقام المكتوبة"
                );
            }
        });

        profile.addView(save);

        root.addView(profile);

        root.addView(
                section(
                        "ملخصك الذكي"
                )
        );

        LinearLayout smart = card();

        smart.addView(
                infoRow(
                        "📏 الطول",
                        prefs.getInt(
                                "height_cm",
                                174
                        ) + " سم"
                )
        );

        smart.addView(
                infoRow(
                        "⚖️ الوزن",
                        qtyFormat.format(
                                getWeight()
                        ) + " كجم"
                )
        );

        smart.addView(
                infoRow(
                        "🍗 هدف البروتين التقريبي",
                        proteinTarget()
                                + " جم/يوم"
                )
        );

        smart.addView(
                infoRow(
                        "🔥 السعرات كبداية",
                        calorieTarget()
                                + " سعرة/يوم"
                )
        );

        TextView advice =
                txt(
                        personalizedAdvice(),
                        15,
                        false
                );

        advice.setTextColor(MUTED);

        advice.setPadding(
                0,
                dp(12),
                0,
                0
        );

        smart.addView(advice);

        root.addView(smart);

        root.addView(
                section(
                        "المدربين"
                )
        );

        LinearLayout coaches = card();

        TextView ct =
                txt(
                        "اختار مدربك المفضل وافتح ملفه ونصائحه",
                        16,
                        true
                );

        coaches.addView(ct);

        Button coachBtn =
                button(
                        "فتح صفحة المدربين",
                        CARD_2,
                        Color.WHITE
                );

        coachBtn.setOnClickListener(
                v -> openCoach(
                        prefs.getInt(
                                "selected_coach",
                                0
                        )
                )
        );

        coaches.addView(coachBtn);

        root.addView(coaches);

        root.addView(
                section(
                        "التتبع"
                )
        );

        LinearLayout tracking = card();

        tracking.addView(
                infoRow(
                        "🏋️ إجمالي التمارين",
                        String.valueOf(
                                prefs.getInt(
                                        "workout_count",
                                        0
                                )
                        )
                )
        );

        tracking.addView(
                infoRow(
                        "🔥 أيام متتالية",
                        String.valueOf(
                                prefs.getInt(
                                        "consecutive_workouts",
                                        0
                                )
                        )
                )
        );

        tracking.addView(
                infoRow(
                        "🧪 أيام الكرياتين",
                        String.valueOf(
                                prefs.getInt(
                                        "creatine_days",
                                        0
                                )
                        )
                )
        );

        root.addView(tracking);

        root.addView(
                section(
                        "الإعدادات"
                )
        );

        LinearLayout settings = card();

        Switch motivation =
                new Switch(this);

        motivation.setText(
                "إشعارات التشجيع"
        );

        motivation.setTextColor(TEXT);
        motivation.setTextSize(17);

        motivation.setChecked(
                prefs.getBoolean(
                        "motivation_enabled",
                        true
                )
        );

        motivation.setOnCheckedChangeListener(
                (b, checked) ->
                        prefs.edit()
                                .putBoolean(
                                        "motivation_enabled",
                                        checked
                                )
                                .apply()
        );

        settings.addView(motivation);

        Switch alarms =
                new Switch(this);

        alarms.setText(
                "منبهات اليوم بصوت Alarm"
        );

        alarms.setTextColor(TEXT);
        alarms.setTextSize(17);
        alarms.setChecked(true);

        alarms.setOnCheckedChangeListener(
                (b, checked) -> {
                    if (checked) {
                        enableAlarms();
                    }
                }
        );

        settings.addView(alarms);

        root.addView(settings);

        LinearLayout about = card();

        TextView a = txt(
                "GYM • Red 3D Edition V3\n"
                        + "مساعد شخصي للتدريب، الراحة، الأكل، المخزون والتحفيز",
                15,
                false
        );

        a.setTextColor(MUTED);
        a.setGravity(Gravity.CENTER);

        about.addView(a);

        root.addView(about);

        return scroll;
    }

    private View coachesRow() {
        HorizontalScrollView hsv =
                new HorizontalScrollView(this);

        hsv.setHorizontalScrollBarEnabled(false);

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        row.addView(
                coachCard(
                        0,
                        R.drawable.coach_1,
                        "كابتن آدم",
                        "زيادة الكتلة",
                        "الأوزان والتدرج"
                )
        );

        row.addView(
                coachCard(
                        1,
                        R.drawable.coach_2,
                        "كابتن سيف",
                        "التعافي",
                        "تمرين أو راحة"
                )
        );

        row.addView(
                coachCard(
                        2,
                        R.drawable.coach_3,
                        "كابتن كريم",
                        "التكنيك",
                        "شرح بالصور"
                )
        );

        hsv.addView(row);

        return hsv;
    }

    private View coachCard(
            int index,
            int imageRes,
            String name,
            String specialty,
            String desc
    ) {
        LinearLayout box = card();

        box.setMinimumWidth(
                dp(230)
        );

        ImageView avatar =
                new ImageView(this);

        avatar.setImageResource(
                imageRes
        );

        avatar.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        box.addView(
                avatar,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(170)
                )
        );

        TextView n =
                txt(
                        name,
                        20,
                        true
                );

        n.setGravity(Gravity.CENTER);

        n.setPadding(
                0,
                dp(10),
                0,
                0
        );

        box.addView(n);

        TextView sp =
                txt(
                        specialty,
                        14,
                        true
                );

        sp.setGravity(Gravity.CENTER);
        sp.setTextColor(ACCENT);

        box.addView(sp);

        TextView d =
                txt(
                        desc,
                        13,
                        false
                );

        d.setGravity(Gravity.CENTER);
        d.setTextColor(MUTED);

        d.setPadding(
                0,
                dp(5),
                0,
                0
        );

        box.addView(d);

        TextView open =
                txt(
                        "عرض الملف ←",
                        14,
                        true
                );

        open.setTextColor(ACCENT);
        open.setGravity(Gravity.CENTER);

        open.setPadding(
                0,
                dp(12),
                0,
                0
        );

        box.addView(open);

        box.setOnClickListener(
                v -> openCoach(index)
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        dp(245),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        p.setMargins(
                0,
                0,
                dp(12),
                0
        );

        box.setLayoutParams(p);

        return box;
    }

    private void openCoach(int index) {
        Intent i =
                new Intent(
                        this,
                        DetailActivity.class
                );

        i.putExtra(
                "type",
                "coach"
        );

        i.putExtra(
                "index",
                index
        );

        startActivity(i);
    }

    private void openExercise(
            String name,
            boolean home
    ) {
        Intent i =
                new Intent(
                        this,
                        DetailActivity.class
                );

        i.putExtra(
                "type",
                "exercise"
        );

        i.putExtra(
                "name",
                name
        );

        i.putExtra(
                "home",
                home
        );

        startActivity(i);
    }

    private View recoveryDecisionCard() {
        LinearLayout box = card();

        String[] decision =
                trainingDecision();

        TextView icon =
                txt(
                        decision[0],
                        42,
                        false
                );

        icon.setGravity(
                Gravity.CENTER
        );

        box.addView(icon);

        TextView title =
                txt(
                        decision[1],
                        23,
                        true
                );

        title.setGravity(
                Gravity.CENTER
        );

        title.setTextColor(
                ACCENT
        );

        box.addView(title);

        TextView body =
                txt(
                        decision[2],
                        15,
                        false
                );

        body.setGravity(
                Gravity.CENTER
        );

        body.setTextColor(
                MUTED
        );

        body.setPadding(
                0,
                dp(8),
                0,
                dp(8)
        );

        box.addView(body);

        TextView status =
                txt(
                        "الجمعة إجازة الجيم • نومك: "
                                + prefs.getFloat(
                                "sleep_hours",
                                7.5f
                        )
                                + " س • إجهادك: "
                                + prefs.getInt(
                                "soreness",
                                3
                        )
                                + "/10",
                        12,
                        false
                );

        status.setGravity(
                Gravity.CENTER
        );

        status.setTextColor(
                MUTED
        );

        box.addView(status);

        return box;
    }

    private String[] trainingDecision() {
        Calendar c =
                Calendar.getInstance();

        int day =
                c.get(
                        Calendar.DAY_OF_WEEK
                );

        boolean trained =
                prefs.getBoolean(
                        dayKey("workout"),
                        false
                );

        int consecutive =
                prefs.getInt(
                        "consecutive_workouts",
                        0
                );

        float sleep =
                prefs.getFloat(
                        "sleep_hours",
                        7.5f
                );

        int soreness =
                prefs.getInt(
                        "soreness",
                        3
                );

        if (day == Calendar.FRIDAY) {
            return new String[]{
                    "🛌",
                    "راحة اليوم",
                    "الجيم إجازة الجمعة. اعمل مشي خفيف 20–30 دقيقة وتمدد بسيط لو حابب."
            };
        }

        if (trained) {
            return new String[]{
                    "✅",
                    "أنت تمرنت بالفعل",
                    "ركز دلوقتي على الأكل، المياه والنوم. مفيش داعي لتمرين حديد تاني النهارده."
            };
        }

        if (
                sleep < 6f
                        || soreness >= 7
                        || consecutive >= 3
        ) {
            return new String[]{
                    "🧘",
                    "الأفضل راحة",
                    "علامات التعافي مش كاملة. خليك على حركة خفيفة وتمدد وارجع أقوى في التمرين القادم."
            };
        }

        return new String[]{
                "🔥",
                "يوم تمرين مناسب",
                "جسمك جاهز غالبًا لتمرين Full Body لمدة 45–60 دقيقة مع أوزان تتحكم فيها كويس."
        };
    }

    private void markGymAttendance(
            boolean went
    ) {
        prefs.edit()
                .putBoolean(
                        dayKey("gym_answered"),
                        true
                )
                .putBoolean(
                        dayKey("workout"),
                        went
                )
                .apply();

        toast(
                went
                        ? "عاش! سجلنا إنك رحت الجيم ✅"
                        : "تمام، سجلنا إنك مرحتش. الخطة هتتكيف معاك."
        );

        showTab(currentTab);
    }

    private void completeWorkout() {
        int count =
                prefs.getInt(
                        "workout_count",
                        0
                ) + 1;

        int consecutive =
                prefs.getInt(
                        "consecutive_workouts",
                        0
                ) + 1;

        prefs.edit()
                .putBoolean(
                        dayKey("workout"),
                        true
                )
                .putBoolean(
                        dayKey("gym_answered"),
                        true
                )
                .putInt(
                        "workout_count",
                        count
                )
                .putInt(
                        "consecutive_workouts",
                        consecutive
                )
                .putLong(
                        "last_workout_day",
                        System.currentTimeMillis()
                )
                .apply();

        toast(
                "عاش يا بطل! تمرين جديد اتسجل 🏆"
        );

        showTab(2);
    }

    private String extraDayAssessment() {
        String[] d =
                trainingDecision();

        if (
                d[1].contains(
                        "مناسب"
                )
        ) {
            return "✅ يوم إضافي ممكن يكون مناسب، بشرط تخفف الشدة وما تكرر نفس العضلات بإجهاد عالي.";
        }

        return "⚠️ الأفضل بلاش تمرين إضافي النهارده. جسمك محتاج تعافي أكتر عشان تستفيد من التمرين.";
    }

    private String[][] personalizedGymPlan() {
        double w = getWeight();

        String base =
                w < 70
                        ? "ابدأ خفيف وزوّد تدريجيًا"
                        : "اختار وزن تقدر تتحكم فيه";

        return new String[][]{
                {
                        "Leg Press",
                        "3 × 10",
                        base
                },
                {
                        "Chest Press",
                        "3 × 10",
                        "آخر عدتين يكونوا صعبين لكن بتكنيك سليم"
                },
                {
                        "Lat Pulldown",
                        "3 × 10",
                        "اسحب بالكوع واثبت ظهرك"
                },
                {
                        "Seated Row",
                        "3 × 10",
                        "ما ترجّعش جسمك للخلف"
                },
                {
                        "Shoulder Press",
                        "3 × 10",
                        "متقفلش الكوع بعنف"
                },
                {
                        "Leg Curl",
                        "3 × 12",
                        "حركة بطيئة وتحكم"
                },
                {
                        "Biceps Curl",
                        "2 × 12",
                        "من غير مرجحة"
                },
                {
                        "Triceps Pushdown",
                        "2 × 12",
                        "ثبت الكوع جنب جسمك"
                }
        };
    }

    private View exerciseCard(
            int number,
            String name,
            String reps,
            String note
    ) {
        LinearLayout box = card();

        LinearLayout row = hRow();

        TextView num =
                txt(
                        String.valueOf(number),
                        20,
                        true
                );

        num.setGravity(
                Gravity.CENTER
        );

        num.setTextColor(
                Color.WHITE
        );

        num.setBackgroundTintList(
                android.content.res.ColorStateList
                        .valueOf(ACCENT)
        );

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        info.addView(
                txt(
                        name,
                        18,
                        true
                )
        );

        TextView rp =
                txt(
                        reps
                                + " • راحة 90 ثانية",
                        14,
                        false
                );

        rp.setTextColor(ACCENT);

        info.addView(rp);

        TextView nt =
                txt(
                        note,
                        13,
                        false
                );

        nt.setTextColor(MUTED);

        info.addView(nt);

        row.addView(
                num,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                )
        );

        row.addView(
                info,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        box.addView(row);

        TextView more =
                txt(
                        "اضغط للشرح بالصور والخطوات ←",
                        13,
                        true
                );

        more.setTextColor(ACCENT);

        more.setPadding(
                0,
                dp(10),
                0,
                0
        );

        box.addView(more);

        box.setOnClickListener(
                v -> openExercise(
                        name,
                        false
                )
        );

        return box;
    }

    private View homeExerciseCard(
            String icon,
            String name,
            String reps,
            String how
    ) {
        LinearLayout box = card();

        LinearLayout row = hRow();

        TextView art =
                txt(
                        icon,
                        48,
                        false
                );

        art.setGravity(
                Gravity.CENTER
        );

        art.setBackgroundTintList(
                android.content.res.ColorStateList
                        .valueOf(CARD_2)
        );

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        info.addView(
                txt(
                        name,
                        19,
                        true
                )
        );

        TextView rp =
                txt(
                        reps,
                        15,
                        true
                );

        rp.setTextColor(ACCENT);

        info.addView(rp);

        TextView h =
                txt(
                        how,
                        13,
                        false
                );

        h.setTextColor(MUTED);

        info.addView(h);

        row.addView(
                art,
                new LinearLayout.LayoutParams(
                        dp(70),
                        dp(70)
                )
        );

        row.addView(
                info,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        box.addView(row);

        TextView more =
                txt(
                        "عرض طريقة الأداء بالصور ←",
                        13,
                        true
                );

        more.setTextColor(ACCENT);

        more.setPadding(
                0,
                dp(10),
                0,
                0
        );

        box.addView(more);

        box.setOnClickListener(
                v -> openExercise(
                        name,
                        true
                )
        );

        return box;
    }

    private int proteinTarget() {
        return (int) Math.round(
                getWeight() * 1.7
        );
    }

    private int calorieTarget() {
        return (int) Math.round(
                getWeight() * 35 + 300
        );
    }

    private String personalizedAdvice() {
        double bmi =
                getWeight()
                        / Math.pow(
                        prefs.getInt(
                                "height_cm",
                                174
                        ) / 100.0,
                        2
                );

        if (bmi < 20) {
            return "وزنك خفيف نسبيًا لطولك، فركز على زيادة تدريجية: وجبات ثابتة، بروتين موزع على اليوم، وتمرين مقاومة منتظم.";
        }

        return "ركز على زيادة هادئة في الوزن، ثبت التمرين، وزوّد الأكل تدريجيًا حسب حركة الميزان كل أسبوعين.";
    }

    private View pageTitle(
            String title,
            String subtitle
    ) {
        LinearLayout wrap =
                new LinearLayout(this);

        wrap.setOrientation(
                LinearLayout.VERTICAL
        );

        wrap.setPadding(
                0,
                0,
                0,
                dp(18)
        );

        TextView brand =
                txt(
                        "GYM",
                        15,
                        true
                );

        brand.setTextColor(ACCENT);
        brand.setLetterSpacing(.16f);

        wrap.addView(brand);

        wrap.addView(
                txt(
                        title,
                        29,
                        true
                )
        );

        TextView s =
                txt(
                        subtitle,
                        14,
                        false
                );

        s.setTextColor(MUTED);

        s.setPadding(
                0,
                dp(4),
                0,
                0
        );

        wrap.addView(s);

        return wrap;
    }

    private LinearLayout card() {
        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        box.setBackgroundResource(
                R.drawable.bg_card_dark
        );

        box.setElevation(dp(8));
        box.setTranslationZ(dp(2));

        box.setCameraDistance(
                8000
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        p.setMargins(
                0,
                0,
                0,
                dp(14)
        );

        box.setLayoutParams(p);

        return box;
    }

    private TextView section(String s) {
        TextView v =
                txt(
                        s,
                        20,
                        true
                );

        v.setPadding(
                0,
                dp(12),
                0,
                dp(10)
        );

        return v;
    }

    private LinearLayout hRow() {
        LinearLayout r =
                new LinearLayout(this);

        r.setOrientation(
                LinearLayout.HORIZONTAL
        );

        r.setGravity(
                Gravity.CENTER_VERTICAL
        );

        return r;
    }

    private TextView txt(
            String s,
            int sp,
            boolean bold
    ) {
        TextView v =
                new TextView(this);

        v.setText(s);
        v.setTextSize(sp);
        v.setTextColor(TEXT);
        v.setGravity(Gravity.RIGHT);

        if (bold) {
            v.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }

        return v;
    }

    private Button button(
            String s,
            int bg,
            int fg
    ) {
        Button b =
                new Button(this);

        b.setText(s);
        b.setTextSize(17);
        b.setTextColor(fg);
        b.setAllCaps(false);

        b.setBackgroundTintList(
                android.content.res.ColorStateList
                        .valueOf(bg)
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        p.setMargins(
                0,
                dp(8),
                0,
                0
        );

        b.setLayoutParams(p);

        return b;
    }

    private Button miniButton(String s) {
        Button b =
                new Button(this);

        b.setText(s);
        b.setTextSize(19);
        b.setTextColor(TEXT);
        b.setAllCaps(false);

        b.setBackgroundTintList(
                android.content.res.ColorStateList
                        .valueOf(CARD_2)
        );

        b.setMinWidth(0);

        b.setPadding(
                0,
                0,
                0,
                0
        );

        b.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                )
        );

        return b;
    }

    private TextView actionChip(
            String s,
            View.OnClickListener l
    ) {
        TextView v =
                txt(
                        s,
                        15,
                        true
                );

        v.setGravity(
                Gravity.CENTER
        );

        v.setBackgroundResource(
                R.drawable.bg_chip
        );

        v.setPadding(
                dp(16),
                dp(12),
                dp(16),
                dp(12)
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        p.setMargins(
                0,
                0,
                dp(9),
                dp(6)
        );

        v.setLayoutParams(p);

        v.setOnClickListener(l);

        return v;
    }

    private View statMini(
            String icon,
            String value,
            String label
    ) {
        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setGravity(
                Gravity.CENTER
        );

        TextView i =
                txt(
                        icon,
                        22,
                        false
                );

        i.setGravity(
                Gravity.CENTER
        );

        TextView v =
                txt(
                        value,
                        17,
                        true
                );

        v.setGravity(
                Gravity.CENTER
        );

        v.setTextColor(
                ACCENT
        );

        TextView l =
                txt(
                        label,
                        12,
                        false
                );

        l.setGravity(
                Gravity.CENTER
        );

        l.setTextColor(
                MUTED
        );

        box.addView(i);
        box.addView(v);
        box.addView(l);

        return box;
    }

    private View infoRow(
            String left,
            String right
    ) {
        LinearLayout row = hRow();

        row.setPadding(
                0,
                dp(9),
                0,
                dp(9)
        );

        TextView l =
                txt(
                        left,
                        16,
                        true
                );

        TextView r =
                txt(
                        right,
                        16,
                        true
                );

        r.setTextColor(ACCENT);
        r.setGravity(Gravity.LEFT);

        row.addView(
                l,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        row.addView(
                r,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        return row;
    }

    private EditText edit(
            String hint,
            String value,
            boolean numeric
    ) {
        EditText e =
                new EditText(this);

        e.setHint(hint);
        e.setHintTextColor(MUTED);
        e.setTextColor(TEXT);
        e.setText(value);
        e.setTextSize(17);
        e.setSingleLine(true);

        e.setBackgroundTintList(
                android.content.res.ColorStateList
                        .valueOf(ACCENT)
        );

        if (numeric) {
            e.setInputType(
                    InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL
            );
        }

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        p.setMargins(
                0,
                0,
                0,
                dp(10)
        );

        e.setLayoutParams(p);

        return e;
    }

    private AppData.Meal nextMeal() {
        int now =
                Calendar.getInstance()
                        .get(
                                Calendar.HOUR_OF_DAY
                        )
                        * 60
                        + Calendar.getInstance()
                        .get(
                                Calendar.MINUTE
                        );

        for (
                AppData.Meal m
                : AppData.MEALS
        ) {
            String[] p =
                    m.time.split(":");

            int t =
                    Integer.parseInt(
                            p[0]
                    )
                            * 60
                            + Integer.parseInt(
                            p[1]
                    );

            if (t >= now) {
                return m;
            }
        }

        return AppData.MEALS.get(0);
    }

    private List<String> missingIngredients(
            AppData.Meal meal
    ) {
        List<String> out =
                new ArrayList<>();

        for (
                String n
                : meal.ingredients
        ) {
            AppData.FoodItem f =
                    findFood(n);

            if (
                    f != null
                            && getQty(
                            f.name,
                            f.defaultQty
                    ) <= 0
            ) {
                out.add(n);
            }
        }

        return out;
    }

    private void consumeMeal(
            AppData.Meal meal
    ) {
        for (
                String n
                : meal.ingredients
        ) {
            if (n.equals("بيض")) {
                changeQty(n, -3);

            } else if (
                    n.equals(
                            "عيش مصري بلدي"
                    )
            ) {
                changeQty(n, -2);

            } else if (
                    n.equals("موز")
            ) {
                changeQty(n, -1);

            } else if (
                    n.equals("لبن")
            ) {
                changeQty(n, -250);

            } else if (
                    n.equals("شوفان")
            ) {
                changeQty(n, -80);

            } else if (
                    n.equals("كرياتين")
            ) {
                changeQty(n, -1);

            } else if (
                    n.equals("مياه")
            ) {
                changeQty(n, -300);

            } else {
                changeQty(n, -1);
            }
        }
    }

    private AppData.FoodItem findFood(
            String n
    ) {
        for (
                AppData.FoodItem f
                : AppData.FOODS
        ) {
            if (
                    f.name.equals(n)
            ) {
                return f;
            }
        }

        return null;
    }

    private double getQty(
            String name,
            double def
    ) {
        return Double.longBitsToDouble(
                prefs.getLong(
                        "qty_" + name,
                        Double.doubleToRawLongBits(
                                def
                        )
                )
        );
    }

    private void setQty(
            String name,
            double value
    ) {
        prefs.edit()
                .putLong(
                        "qty_" + name,
                        Double.doubleToRawLongBits(
                                Math.max(
                                        0,
                                        value
                                )
                        )
                )
                .apply();
    }

    private void changeQty(
            String name,
            double delta
    ) {
        AppData.FoodItem f =
                findFood(name);

        double def =
                f == null
                        ? 0
                        : f.defaultQty;

        setQty(
                name,
                getQty(
                        name,
                        def
                ) + delta
        );
    }

    private double stepFor(
            AppData.FoodItem item
    ) {
        if (
                item.unit.equals("جم")
                        || item.unit.equals("مل")
        ) {
            return 100;
        }

        return 1;
    }

    private List<String> lowStockNames() {
        List<String> out =
                new ArrayList<>();

        for (
                AppData.FoodItem f
                : AppData.FOODS
        ) {
            double q =
                    getQty(
                            f.name,
                            f.defaultQty
                    );

            if (
                    q > 0
                            && q <= f.lowQty
            ) {
                out.add(
                        f.emoji
                                + " "
                                + f.name
                );
            }
        }

        return out;
    }

    private void addWater(int amount) {
        int w =
                prefs.getInt(
                        dayKey("water"),
                        0
                ) + amount;

        prefs.edit()
                .putInt(
                        dayKey("water"),
                        Math.min(
                                5000,
                                w
                        )
                )
                .apply();

        changeQty(
                "مياه",
                -amount
        );

        toast(
                "المياه: "
                        + Math.min(
                        5000,
                        w
                )
                        + " مل 💧"
        );

        showTab(0);
    }

    private double getWeight() {
        return Double.longBitsToDouble(
                prefs.getLong(
                        "current_weight",
                        Double.doubleToRawLongBits(
                                60.0
                        )
                )
        );
    }

    private void weightDialog() {
        final EditText input =
                edit(
                        "وزنك الحالي",
                        qtyFormat.format(
                                getWeight()
                        ),
                        true
                );

        LinearLayout box =
                new LinearLayout(this);

        box.setPadding(
                dp(24),
                dp(10),
                dp(24),
                0
        );

        box.addView(input);

        new android.app.AlertDialog.Builder(this)
                .setTitle(
                        "سجل وزنك ⚖️"
                )
                .setView(box)
                .setPositiveButton(
                        "حفظ",
                        (d, w) -> {
                            try {
                                double val =
                                        Double.parseDouble(
                                                input.getText()
                                                        .toString()
                                        );

                                prefs.edit()
                                        .putLong(
                                                "current_weight",
                                                Double.doubleToRawLongBits(
                                                        val
                                                )
                                        )
                                        .apply();

                                toast(
                                        "تم تسجيل "
                                                + val
                                                + " كجم ✅"
                                );

                                showTab(
                                        currentTab
                                );

                            } catch (
                                    Exception e
                            ) {
                                toast(
                                        "اكتب رقم صحيح"
                                );
                            }
                        }
                )
                .setNegativeButton(
                        "إلغاء",
                        null
                )
                .show();
    }

    private String motivationForDay() {
        String[] q = {
                "الاستمرار أهم من الكمال.",
                "مش لازم تكون متحمس. لازم تبدأ فقط.",
                "كل وجبة كويسة بتبني جسمك الجديد.",
                "النهارده فرصة جديدة تبقى أقوى من امبارح.",
                "النتيجة الكبيرة هي مجموع أيام صغيرة ملتزم فيها.",
                "جسمك بيتغير لما عاداتك تتغير.",
                "أنت لا تحتاج السرعة. أنت تحتاج الاستمرار."
        };

        return q[
                Calendar.getInstance()
                        .get(
                                Calendar.DAY_OF_YEAR
                        )
                        % q.length
                ];
    }

    private String dayKey(
            String key
    ) {
        Calendar c =
                Calendar.getInstance();

        return key
                + "_"
                + c.get(
                Calendar.YEAR
        )
                + "_"
                + c.get(
                Calendar.DAY_OF_YEAR
        );
    }

    private String join(
            List<String> list
    ) {
        StringBuilder b =
                new StringBuilder();

        for (
                int i = 0;
                i < list.size();
                i++
        ) {
            if (i > 0) {
                b.append("، ");
            }

            b.append(
                    list.get(i)
            );
        }

        return b.toString();
    }

    private void toast(
            String s
    ) {
        Toast.makeText(
                this,
                s,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void seedDefaults() {
        if (
                !prefs.contains("seeded")
        ) {
            SharedPreferences.Editor e =
                    prefs.edit()
                            .putBoolean(
                                    "seeded",
                                    true
                            )
                            .putString(
                                    "name",
                                    "محمد"
                            )
                            .putFloat(
                                    "goal_weight",
                                    68f
                            )
                            .putLong(
                                    "current_weight",
                                    Double.doubleToRawLongBits(
                                            60.0
                                    )
                            )
                            .putBoolean(
                                    "motivation_enabled",
                                    true
                            )
                            .putInt(
                                    "streak",
                                    1
                            )
                            .putInt(
                                    "age",
                                    22
                            )
                            .putInt(
                                    "height_cm",
                                    174
                            )
                            .putFloat(
                                    "sleep_hours",
                                    7.5f
                            )
                            .putInt(
                                    "soreness",
                                    3
                            )
                            .putString(
                                    "goal",
                                    "زيادة الوزن وبناء العضلات"
                            )
                            .putInt(
                                    "consecutive_workouts",
                                    0
                            );

            for (
                    AppData.FoodItem f
                    : AppData.FOODS
            ) {
                e.putLong(
                        "qty_" + f.name,
                        Double.doubleToRawLongBits(
                                f.defaultQty
                        )
                );
            }

            e.apply();
        }
    }

    private void requestPermissionsAndSchedule() {
        if (
                Build.VERSION.SDK_INT >= 33
                        && checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                )
                        != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    44
            );
        }

        AlarmManager am =
                (AlarmManager)
                        getSystemService(
                                Context.ALARM_SERVICE
                        );

        if (
                Build.VERSION.SDK_INT < 31
                        || am.canScheduleExactAlarms()
        ) {
            AlarmScheduler.scheduleAll(this);
        }
    }

    private void enableAlarms() {
        AlarmManager am =
                (AlarmManager)
                        getSystemService(
                                Context.ALARM_SERVICE
                        );

        if (
                Build.VERSION.SDK_INT >= 31
                        && !am.canScheduleExactAlarms()
        ) {
            Intent i =
                    AlarmScheduler
                            .exactAlarmSettings(this);

            if (i != null) {
                startActivity(i);
            }

            return;
        }

        AlarmScheduler.scheduleAll(this);

        toast(
                "تم تفعيل كل المنبهات يوميًا ✅"
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlarmManager am =
                (AlarmManager)
                        getSystemService(
                                Context.ALARM_SERVICE
                        );

        if (
                Build.VERSION.SDK_INT < 31
                        || am.canScheduleExactAlarms()
        ) {
            AlarmScheduler.scheduleAll(this);
        }
    }

    private int dp(int v) {
        return (int) (
                v
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}

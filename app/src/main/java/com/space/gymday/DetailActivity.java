package com.space.gymday;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DetailActivity extends Activity {
    private static final int BG = Color.rgb(8, 6, 7);
    private static final int CARD = Color.rgb(28, 13, 16);
    private static final int ACCENT = Color.rgb(230, 22, 34);
    private static final int TEXT = Color.WHITE;
    private static final int MUTED = Color.rgb(185, 178, 181);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getIntent().getStringExtra("type");
        if ("coach".equals(type)) setContentView(coachPage());
        else setContentView(exercisePage());
    }

    private View coachPage() {
        ScrollView scroll = baseScroll();
        LinearLayout root = baseRoot(scroll);
        int index = getIntent().getIntExtra("index", 0);
        String[] names = {"كابتن آدم", "كابتن سيف", "كابتن كريم"};
        String[] specialties = {"زيادة الكتلة والتدرج", "التعافي والحركة", "تمارين المنزل والتكنيك"};
        String[] bios = {
                "يركز على بناء العضلات للمبتدئين، اختيار أوزان آمنة، وزيادة الحمل تدريجيًا بدون تضييع التكنيك.",
                "يساعدك تعرف إمتى تتمرن وإمتى ترتاح، ويظبط معاك النوم والإجهاد وأيام الاستشفاء.",
                "متخصص في تعليم الحركة بالصور، بدائل التمرين في البيت، وتصحيح وضع الجسم خطوة بخطوة."
        };
        int[] images = {R.drawable.coach_1, R.drawable.coach_2, R.drawable.coach_3};
        root.addView(topBar("المدرب", "ملف احترافي ونصائح عملية"));
        ImageView hero = image(images[Math.max(0, Math.min(2, index))], 300); root.addView(hero);
        TextView name = text(names[index], 30, true); name.setGravity(Gravity.CENTER); root.addView(name);
        TextView sp = text(specialties[index], 17, true); sp.setTextColor(ACCENT); sp.setGravity(Gravity.CENTER); sp.setPadding(0, 6, 0, 18); root.addView(sp);
        root.addView(cardText("نبذة", bios[index]));
        root.addView(cardText("إزاي هيساعدك؟", index == 0 ?
                "• يظبط ترتيب التمارين\n• يقترح أوزان بداية\n• يتابع التقدم أسبوعيًا\n• يمنع الزيادة السريعة في الحمل"
                : index == 1 ? "• يقيّم نومك وإجهادك\n• يحدد تمرين أو راحة\n• يقترح تمارين حركة خفيفة\n• يتابع أيام التمرين المتتالية"
                : "• شرح كل تمرين خطوة بخطوة\n• صور وضع البداية والنهاية\n• بدائل منزلية\n• أخطاء شائعة وتصحيحها"));
        Button choose = button("اختيار هذا المدرب كمساعدي");
        choose.setOnClickListener(v -> {
            getSharedPreferences("gym_app_v2", MODE_PRIVATE).edit().putInt("selected_coach", index).apply();
            choose.setText("تم الاختيار ✅"); choose.setEnabled(false);
        });
        root.addView(choose);
        return scroll;
    }

    private View exercisePage() {
        ScrollView scroll = baseScroll();
        LinearLayout root = baseRoot(scroll);
        String name = safe(getIntent().getStringExtra("name"), "تمرين");
        boolean home = getIntent().getBooleanExtra("home", false);
        ExerciseInfo info = ExerciseInfo.forName(name, home);
        root.addView(topBar("شرح التمرين", "صور + خطوات + أخطاء شائعة"));
        TextView title = text(info.title, 30, true); title.setPadding(0, 8, 0, 4); root.addView(title);
        TextView meta = text(info.sets + "  •  " + info.target, 16, true); meta.setTextColor(ACCENT); meta.setPadding(0, 0, 0, 14); root.addView(meta);
        root.addView(image(info.imageRes, 280));
        root.addView(cardText("الوضع الصحيح", info.setup));
        root.addView(cardText("خطوات الحركة", info.steps));
        root.addView(cardText("إزاي تشيل صح؟", info.lifting));
        root.addView(cardText("أخطاء شائعة لازم تتجنبها", info.mistakes));
        root.addView(cardText("الراحة والعدة", info.rest));
        Button start = button("بدأت التمرين 🔥");
        start.setOnClickListener(v -> { start.setText("تم تسجيل البداية ✅"); start.setEnabled(false); });
        root.addView(start);
        return scroll;
    }

    private ScrollView baseScroll() { ScrollView s = new ScrollView(this); s.setFillViewport(true); s.setBackgroundColor(BG); return s; }
    private LinearLayout baseRoot(ScrollView s) { LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.VERTICAL); r.setPadding(dp(18), dp(18), dp(18), dp(28)); r.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); s.addView(r); return r; }
    private View topBar(String title, String subtitle) { LinearLayout b = new LinearLayout(this); b.setOrientation(LinearLayout.VERTICAL); TextView t=text("GYM • "+title,16,true); t.setTextColor(ACCENT); b.addView(t); TextView s=text(subtitle,14,false); s.setTextColor(MUTED); s.setPadding(0,4,0,16); b.addView(s); return b; }
    private TextView text(String s, int sp, boolean bold) { TextView v = new TextView(this); v.setText(s); v.setTextColor(TEXT); v.setTextSize(sp); v.setGravity(Gravity.RIGHT); if (bold) v.setTypeface(null, Typeface.BOLD); return v; }
    private View cardText(String heading, String body) { LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(16),dp(16),dp(16),dp(16)); c.setBackgroundResource(R.drawable.bg_card_dark); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT); p.setMargins(0,0,0,dp(12)); c.setLayoutParams(p); TextView h=text(heading,19,true); h.setTextColor(ACCENT); c.addView(h); TextView b=text(body,15,false); b.setTextColor(MUTED); b.setLineSpacing(0,1.25f); b.setPadding(0,dp(8),0,0); c.addView(b); return c; }
    private ImageView image(int res, int heightDp) { ImageView v=new ImageView(this); v.setImageResource(res); v.setScaleType(ImageView.ScaleType.CENTER_CROP); v.setBackgroundColor(CARD); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(heightDp)); p.setMargins(0,0,0,dp(14)); v.setLayoutParams(p); v.setClipToOutline(true); return v; }
    private Button button(String s) { Button b=new Button(this); b.setText(s); b.setTextSize(17); b.setTextColor(Color.WHITE); b.setAllCaps(false); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ACCENT)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(58)); p.setMargins(0,dp(6),0,0); b.setLayoutParams(p); return b; }
    private String safe(String v,String d){ return v==null||v.trim().isEmpty()?d:v; }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density); }

    private static final class ExerciseInfo {
        final String title, sets, target, setup, steps, lifting, mistakes, rest; final int imageRes;
        ExerciseInfo(String title,String sets,String target,String setup,String steps,String lifting,String mistakes,String rest,int imageRes){this.title=title;this.sets=sets;this.target=target;this.setup=setup;this.steps=steps;this.lifting=lifting;this.mistakes=mistakes;this.rest=rest;this.imageRes=imageRes;}
        static ExerciseInfo forName(String n, boolean home){
            if(home) return homeInfo(n);
            if(n.contains("Leg Press")) return new ExerciseInfo("Leg Press","3 مجموعات × 10","أمام الفخذ • المؤخرة","اضبط المقعد بحيث أسفل ظهرك ثابت. القدمين بعرض الكتفين وفي منتصف اللوح.","1) فك الأمان.\n2) انزل ببطء حتى زاوية مريحة للركبة.\n3) ادفع بالقدم كلها.\n4) لا تقفل الركبتين بعنف.","ابدأ بوزن خفيف جدًا. لو تقدر تعمل 12 عدة بسهولة مرتين متتاليتين، زوّد أقل زيادة متاحة.","• ضم الركب للداخل\n• رفع الحوض من المقعد\n• نزول عميق يسبب ألم\n• قفل الركبة بعنف","راحة 90 ثانية. وقف فورًا لو ظهر ألم حاد في الركبة أو الظهر.",R.drawable.exercise_gym_tutorial);
            if(n.contains("Chest Press")) return new ExerciseInfo("Chest Press","3 مجموعات × 10","الصدر • الترايسبس","المقبض في مستوى منتصف الصدر. الكتف للخلف ولأسفل والقدم ثابتة.","1) خذ نفس.\n2) ادفع للأمام بدون رفع الكتف.\n3) توقف قبل قفل الكوع.\n4) ارجع ببطء 2–3 ثوان.","اختار وزن يخليك تحافظ على نفس المسار في كل عدة. آخر عدتين صعبين لكن بدون هز الجسم.","• رفع الكتف\n• سرعة الرجوع\n• تقويس الظهر زيادة\n• وزن أكبر من التحكم","راحة 90 ثانية.",R.drawable.exercise_bench_steps);
            if(n.contains("Lat Pulldown")) return new ExerciseInfo("Lat Pulldown","3 مجموعات × 10","الظهر العريض • البايسبس","امسك البار أوسع قليلًا من الكتف. ثبت الفخذين واجلس بصدر مرفوع.","1) اسحب الكتف لأسفل.\n2) اسحب البار لأعلى الصدر.\n3) فكر إن الكوع ينزل للجيب.\n4) ارجع الذراع كاملة بتحكم.","وزن يسمح لك تثبت الجذع. لو بدأت ترجع بجسمك للخلف، الوزن غالبًا ثقيل.","• سحب خلف الرقبة\n• هز الجذع\n• قبضة واسعة جدًا\n• تقصير مدى الحركة","راحة 75–90 ثانية.",R.drawable.exercise_gym_tutorial);
            if(n.contains("Seated Row")) return new ExerciseInfo("Seated Row","3 مجموعات × 10","منتصف الظهر • البايسبس","الصدر مرفوع، الظهر محايد، الركبة مثنية قليلًا.","1) ابدأ والذراع ممتدة.\n2) اسحب الكوع للخلف.\n3) قرب لوح الكتف بدون رفع الكتف.\n4) ارجع ببطء.","ما تستخدمش رجلك وظهرك لعمل زخم. لو بتتهز، خفف الوزن.","• رمي الجسم للخلف\n• رفع الكتف\n• شد باليد فقط\n• سرعة عالية","راحة 75–90 ثانية.",R.drawable.exercise_gym_tutorial);
            if(n.contains("Shoulder Press")) return new ExerciseInfo("Shoulder Press","3 مجموعات × 10","الكتف • الترايسبس","المقعد يدعم الظهر. المقبض يبدأ قرب مستوى الأذن.","1) شد البطن.\n2) ادفع لأعلى.\n3) لا تقفل الكوع بعنف.\n4) انزل حتى مدى مريح.","ابدأ أخف مما تتوقع؛ مفصل الكتف محتاج تحكم أكثر من الأرقام.","• تقويس الظهر\n• نزول منخفض جدًا\n• وزن ثقيل\n• اصطدام الأوزان","راحة 90 ثانية.",R.drawable.exercise_gym_tutorial);
            if(n.contains("Leg Curl")) return new ExerciseInfo("Leg Curl","3 مجموعات × 12","خلف الفخذ","اضبط الوسادة فوق الكاحل وثبت الحوض في المقعد.","1) اثن الركبة.\n2) توقف لحظة.\n3) ارجع ببطء كامل.\n4) حافظ على الحوض ثابت.","اختار وزن يمنعك من ضرب الجهاز أو رفع الحوض.","• رفع الحوض\n• نصف مدى\n• سرعة الرجوع","راحة 60–75 ثانية.",R.drawable.exercise_gym_tutorial);
            if(n.contains("Biceps")) return new ExerciseInfo("Biceps Curl","2 مجموعات × 12","البايسبس","قف ثابت والكوع بجانب الجسم.","1) ارفع الوزن بثني الكوع.\n2) لا تحرك الكتف.\n3) انزل ببطء حتى الذراع شبه مستقيمة.","لو تحتاج ترجح جسمك، الوزن ثقيل.","• مرجحة الظهر\n• حركة الكوع للأمام\n• نزول سريع","راحة 60 ثانية.",R.drawable.exercise_gym_tutorial);
            if(n.contains("Triceps")) return new ExerciseInfo("Triceps Pushdown","2 مجموعات × 12","الترايسبس","ثبت الكوع بجانب الجسم والكتف لأسفل.","1) ادفع المقبض لأسفل.\n2) افرد الكوع بدون تحريك الكتف.\n3) ارجع ببطء حتى 90 درجة.","ثبت جسمك؛ لو بتميل على المقبض خفف الوزن.","• فتح الكوع للخارج\n• تحريك الكتف\n• زخم الجسم","راحة 60 ثانية.",R.drawable.exercise_gym_tutorial);
            return new ExerciseInfo(n,"3 مجموعات","تمرين مقاومة","ابدأ بوضع ثابت ومريح.","نفذ الحركة ببطء وتحكم.","ابدأ خفيف وزوّد تدريجيًا.","تجنب الألم الحاد والزخم.","راحة 60–90 ثانية.",R.drawable.exercise_gym_tutorial);
        }
        static ExerciseInfo homeInfo(String n){
            if(n.contains("Push")) return new ExerciseInfo("Push Up","4 مجموعات × 8–15","الصدر • الكتف • الترايسبس","اليد تحت أو أوسع قليلًا من الكتف. الجسم خط واحد من الرأس للكعب.","1) شد البطن والمؤخرة.\n2) انزل والصدر يقرب من الأرض.\n3) الكوع بزاوية تقريبية 30–45°.\n4) ادفع الأرض بعيدًا.","لو صعب: ارفع اليد على ترابيزة ثابتة. لو سهل: بطّئ النزول.","• سقوط الحوض\n• خروج الكوع 90°\n• نصف مدى\n• دفع الرأس للأمام","راحة 60–90 ثانية.",R.drawable.exercise_home_tutorial);
            if(n.contains("Squat")) return new ExerciseInfo("Bodyweight Squat","4 مجموعات × 15","الرجل • المؤخرة","القدم بعرض الكتف وأصابع القدم للخارج قليلًا.","1) ارجع بالحوض للخلف.\n2) انزل والركبة في اتجاه القدم.\n3) حافظ على القدم كاملة على الأرض.\n4) اصعد بدفع الأرض.","ابدأ بدون وزن. تحكم في النزول أهم من السرعة.","• دخول الركب للداخل\n• رفع الكعب\n• تقويس الظهر","راحة 60 ثانية.",R.drawable.exercise_home_tutorial);
            if(n.contains("Lunge")) return new ExerciseInfo("Reverse Lunge","3 مجموعات × 10 لكل رجل","الرجل • المؤخرة","قف مستقيم وخطوة للخلف بمسافة مريحة.","1) خطوة للخلف.\n2) انزل الركبة الخلفية نحو الأرض.\n3) الركبة الأمامية تتبع اتجاه القدم.\n4) ادفع بالقدم الأمامية وارجع.","استخدم حائط للتوازن أول مرة.","• خطوة قصيرة جدًا\n• ميل شديد للأمام\n• خبطة الركبة بالأرض","راحة 60–75 ثانية.",R.drawable.exercise_home_tutorial);
            if(n.contains("Plank")) return new ExerciseInfo("Plank","3 مجموعات × 30–60 ثانية","البطن • الجذع","الكوع تحت الكتف والجسم خط واحد.","1) شد البطن.\n2) شد المؤخرة.\n3) تنفس طبيعي.\n4) توقف قبل انهيار الوضع.","زود الوقت تدريجيًا، مش لازم تكمل دقيقة من أول يوم.","• سقوط الحوض\n• رفع المؤخرة زيادة\n• حبس النفس","راحة 45–60 ثانية.",R.drawable.exercise_home_tutorial);
            return new ExerciseInfo(n,"3 مجموعات","تمرين منزلي","جهز مساحة آمنة وثابتة.","نفذ الحركة ببطء ومدى مريح.","زود العدات قبل ما تضيف وزن.","تجنب السرعة والوضع المؤلم.","راحة 60 ثانية.",R.drawable.exercise_home_tutorial);
        }
    }
}

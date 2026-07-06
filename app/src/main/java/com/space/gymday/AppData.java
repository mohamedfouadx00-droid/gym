package com.space.gymday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AppData {
    private AppData() {}

    public static final class FoodItem {
        public final String category;
        public final String emoji;
        public final String name;
        public final String unit;
        public final double defaultQty;
        public final double lowQty;

        public FoodItem(String category, String emoji, String name, String unit, double defaultQty, double lowQty) {
            this.category = category;
            this.emoji = emoji;
            this.name = name;
            this.unit = unit;
            this.defaultQty = defaultQty;
            this.lowQty = lowQty;
        }
    }

    public static final class Meal {
        public final String name;
        public final String emoji;
        public final String time;
        public final String[] ingredients;
        public final String note;

        public Meal(String name, String emoji, String time, String[] ingredients, String note) {
            this.name = name;
            this.emoji = emoji;
            this.time = time;
            this.ingredients = ingredients;
            this.note = note;
        }
    }

    public static final String[][] SCHEDULE = new String[][]{
            {"800", "8", "0", "اصحى يا بطل 💪", "ابدأ يومك واشرب كوبين مياه", "wake"},
            {"830", "8", "30", "وقت الفطار 🍳", "3 بيضات + فول + عيش مصري بلدي + موزة", "meal"},
            {"1100", "11", "0", "وجبة قبل التمرين 🥣", "شوفان + لبن + موزة + عسل", "meal"},
            {"1230", "12", "30", "استعد للجيم 🎒", "اشرب مياه وجهز حاجتك", "gym"},
            {"1300", "13", "0", "وقت التمرين 🏋️", "ابدأ التمرين الآن — أنت أقوى من أمس", "gym"},
            {"1415", "14", "15", "وقت الكرياتين 🧪", "خد 5 جرام كرياتين واشرب مياه", "supplement"},
            {"1500", "15", "0", "وقت الغدا 🍗", "أرز أو مكرونة + بروتين + سلطة", "meal"},
            {"1800", "18", "0", "سناك اقتصادي 🥛", "لبن + موزة + فول سوداني", "meal"},
            {"2100", "21", "0", "وقت العشا 🍳", "بيض + فول أو جبنة قريش + عيش", "meal"},
            {"2330", "23", "30", "وجبة خفيفة قبل النوم 🌙", "لبن أو زبادي", "meal"},
            {"0", "0", "0", "وقت النوم 😴", "نام 7 إلى 9 ساعات عشان جسمك يتعافى", "sleep"}
    };

    public static final List<FoodItem> FOODS = Arrays.asList(
            // Bread & carbs
            new FoodItem("الخبز والنشويات", "🫓", "عيش مصري بلدي", "رغيف", 10, 4),
            new FoodItem("الخبز والنشويات", "🍞", "عيش توست", "شريحة", 12, 4),
            new FoodItem("الخبز والنشويات", "🥖", "عيش فينو", "قطعة", 6, 2),
            new FoodItem("الخبز والنشويات", "🥣", "شوفان", "جم", 500, 150),
            new FoodItem("الخبز والنشويات", "🍚", "أرز أبيض", "جم", 1500, 400),
            new FoodItem("الخبز والنشويات", "🍚", "أرز بسمتي", "جم", 1000, 300),
            new FoodItem("الخبز والنشويات", "🍝", "مكرونة", "جم", 1000, 300),
            new FoodItem("الخبز والنشويات", "🥔", "بطاطس", "جم", 1500, 500),
            new FoodItem("الخبز والنشويات", "🍠", "بطاطا", "جم", 1000, 300),
            new FoodItem("الخبز والنشويات", "🍯", "عسل", "جم", 250, 70),
            new FoodItem("الخبز والنشويات", "🍓", "مربى", "جم", 300, 80),
            new FoodItem("الخبز والنشويات", "🌽", "ذرة", "جم", 500, 150),
            new FoodItem("الخبز والنشويات", "🥣", "كورن فليكس", "جم", 500, 150),
            new FoodItem("الخبز والنشويات", "🥞", "دقيق", "جم", 1000, 250),

            // Economic protein
            new FoodItem("البروتين الاقتصادي", "🥚", "بيض", "بيضة", 30, 8),
            new FoodItem("البروتين الاقتصادي", "🥣", "فول", "علبة", 5, 2),
            new FoodItem("البروتين الاقتصادي", "🫘", "عدس", "جم", 1000, 250),
            new FoodItem("البروتين الاقتصادي", "🫘", "فاصوليا", "جم", 800, 200),
            new FoodItem("البروتين الاقتصادي", "🫘", "لوبيا", "جم", 800, 200),
            new FoodItem("البروتين الاقتصادي", "🫘", "حمص", "جم", 500, 150),
            new FoodItem("البروتين الاقتصادي", "🧀", "جبنة قريش", "جم", 500, 150),
            new FoodItem("البروتين الاقتصادي", "🥛", "لبن", "مل", 2000, 500),
            new FoodItem("البروتين الاقتصادي", "🥛", "زبادي", "علبة", 6, 2),
            new FoodItem("البروتين الاقتصادي", "🥜", "فول سوداني", "جم", 500, 120),
            new FoodItem("البروتين الاقتصادي", "🥜", "زبدة فول سوداني", "جم", 350, 100),

            // Animal protein
            new FoodItem("البروتين الحيواني", "🍗", "صدور فراخ", "جم", 1000, 300),
            new FoodItem("البروتين الحيواني", "🍗", "أوراك فراخ", "جم", 1500, 400),
            new FoodItem("البروتين الحيواني", "🍗", "دبابيس فراخ", "جم", 1000, 300),
            new FoodItem("البروتين الحيواني", "🥩", "لحمة", "جم", 700, 200),
            new FoodItem("البروتين الحيواني", "🥩", "لحم مفروم", "جم", 700, 200),
            new FoodItem("البروتين الحيواني", "🥩", "كبدة", "جم", 500, 150),
            new FoodItem("البروتين الحيواني", "🐟", "تونة", "علبة", 5, 2),
            new FoodItem("البروتين الحيواني", "🐟", "سردين", "علبة", 4, 1),
            new FoodItem("البروتين الحيواني", "🐟", "سمك", "جم", 1000, 300),

            // Fruits
            new FoodItem("الفواكه", "🍌", "موز", "ثمرة", 10, 4),
            new FoodItem("الفواكه", "🍎", "تفاح", "ثمرة", 6, 2),
            new FoodItem("الفواكه", "🍊", "برتقال", "ثمرة", 6, 2),
            new FoodItem("الفواكه", "🌴", "تمر", "ثمرة", 30, 10),
            new FoodItem("الفواكه", "🥭", "مانجو", "ثمرة", 4, 1),
            new FoodItem("الفواكه", "🍓", "فراولة", "جم", 500, 150),
            new FoodItem("الفواكه", "🍇", "عنب", "جم", 700, 200),
            new FoodItem("الفواكه", "🍉", "بطيخ", "جم", 1500, 400),
            new FoodItem("الفواكه", "🥝", "كيوي", "ثمرة", 4, 1),

            // Vegetables
            new FoodItem("الخضروات", "🍅", "طماطم", "ثمرة", 8, 3),
            new FoodItem("الخضروات", "🥒", "خيار", "ثمرة", 8, 3),
            new FoodItem("الخضروات", "🥬", "خس", "واحدة", 2, 1),
            new FoodItem("الخضروات", "🥕", "جزر", "ثمرة", 6, 2),
            new FoodItem("الخضروات", "🫑", "فلفل", "ثمرة", 6, 2),
            new FoodItem("الخضروات", "🧅", "بصل", "ثمرة", 10, 3),
            new FoodItem("الخضروات", "🥗", "خضار مشكل", "جم", 1000, 300),
            new FoodItem("الخضروات", "🥦", "بروكلي", "جم", 500, 150),
            new FoodItem("الخضروات", "🥬", "سبانخ", "جم", 500, 150),

            // Healthy fats
            new FoodItem("الدهون الصحية", "🥄", "طحينة", "جم", 300, 80),
            new FoodItem("الدهون الصحية", "🫒", "زيت زيتون", "مل", 500, 150),
            new FoodItem("الدهون الصحية", "🌰", "مكسرات", "جم", 300, 80),

            // Drinks
            new FoodItem("المشروبات", "💧", "مياه", "مل", 6000, 1500),
            new FoodItem("المشروبات", "☕", "قهوة", "كوب", 10, 2),
            new FoodItem("المشروبات", "🍵", "شاي", "كوب", 10, 2),
            new FoodItem("المشروبات", "🥤", "عصير طبيعي", "مل", 1000, 250),

            // Supplements
            new FoodItem("المكملات", "🧪", "كرياتين", "جرعة", 75, 10),
            new FoodItem("المكملات", "🥤", "Whey Protein", "جرعة", 0, 5),
            new FoodItem("المكملات", "🐟", "Omega 3", "كبسولة", 0, 10),
            new FoodItem("المكملات", "☀️", "Vitamin D", "جرعة", 0, 5)
    );

    public static final String[][] HOME_WORKOUTS = new String[][]{
            {"🤸", "Bodyweight Squat", "4 × 15 عدة", "انزل ببطء، ركبتك في اتجاه مشط القدم وظهرك ثابت"},
            {"🫸", "Push-ups", "4 × 8–15 عدة", "جسمك خط واحد. لو صعب ابدأ على الركبتين"},
            {"🦵", "Reverse Lunges", "3 × 10 لكل رجل", "خطوة للخلف وانزل بتحكم مع ثبات الركبة الأمامية"},
            {"🧱", "Plank", "3 × 30–60 ثانية", "شد البطن والمؤخرة وخلي ظهرك مستقيم"},
            {"🪑", "Chair Dips", "3 × 8–12 عدة", "كرسي ثابت، انزل ببطء وخلي كتفك بعيد عن ودنك"},
            {"🐦", "Bird Dog", "3 × 10 لكل ناحية", "مد ذراع ورجل عكس بعض من غير لف الحوض"}
    };

    public static final List<Meal> MEALS = Arrays.asList(
            new Meal("الفطار", "🍳", "08:30", new String[]{"بيض", "فول", "عيش مصري بلدي", "موز"}, "3 بيضات + فول + 2 رغيف عيش بلدي + موزة"),
            new Meal("قبل التمرين", "🥣", "11:00", new String[]{"شوفان", "لبن", "موز", "عسل"}, "شوفان + لبن كامل الدسم + موزة + عسل"),
            new Meal("بعد التمرين", "🧪", "14:15", new String[]{"كرياتين", "مياه"}, "5 جم كرياتين + مياه"),
            new Meal("الغداء", "🍗", "15:00", new String[]{"أرز أبيض", "أوراك فراخ", "خضار مشكل"}, "طبق أرز كبير + بروتين + سلطة أو خضار"),
            new Meal("السناك", "🥛", "18:00", new String[]{"لبن", "موز", "فول سوداني"}, "لبن + موزة + فول سوداني"),
            new Meal("العشاء", "🍳", "21:00", new String[]{"بيض", "عيش مصري بلدي"}, "3 بيضات + فول أو جبنة قريش + عيش"),
            new Meal("قبل النوم", "🌙", "23:30", new String[]{"لبن"}, "كوب لبن أو زبادي")
    );

    public static Map<String, List<FoodItem>> foodsByCategory() {
        LinkedHashMap<String, List<FoodItem>> map = new LinkedHashMap<>();
        for (FoodItem item : FOODS) {
            if (!map.containsKey(item.category)) map.put(item.category, new ArrayList<>());
            map.get(item.category).add(item);
        }
        return map;
    }
}

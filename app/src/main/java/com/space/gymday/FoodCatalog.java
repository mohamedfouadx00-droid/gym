package com.space.gymday;

import java.util.LinkedHashMap;
import java.util.Map;

public final class FoodCatalog {
    private FoodCatalog() {}
    public static Map<String, String[]> categories() {
        Map<String, String[]> m = new LinkedHashMap<>();
        m.put("🍞 الخبز والنشويات", new String[]{"عيش مصري بلدي","عيش فينو","عيش توست","شوفان","أرز أبيض","أرز بسمتي","مكرونة","بطاطس","بطاطا","كورن فليكس","عسل","مربى"});
        m.put("🥚 بروتين اقتصادي", new String[]{"بيض","فول","عدس","فاصوليا","لوبيا","حمص","جبنة قريش","لبن","زبادي","فول سوداني","زبدة فول سوداني"});
        m.put("🍗 بروتين حيواني", new String[]{"صدور فراخ","أوراك فراخ","دبابيس فراخ","فرخة كاملة","لحمة","لحم مفروم","كبدة","تونة","سردين","سمك"});
        m.put("🍌 فواكه", new String[]{"موز","تفاح","برتقال","تمر","مانجو","فراولة","عنب","بطيخ"});
        m.put("🥦 خضروات", new String[]{"طماطم","خيار","خس","جزر","فلفل","بصل","سبانخ","خضار مشكل"});
        m.put("🥜 دهون صحية", new String[]{"طحينة","زيت زيتون","مكسرات"});
        m.put("🥤 مشروبات", new String[]{"مياه","قهوة","شاي","عصير طبيعي"});
        m.put("🧪 مكملات", new String[]{"كرياتين","Whey Protein","Multivitamin","Omega 3","Vitamin D"});
        return m;
    }
}

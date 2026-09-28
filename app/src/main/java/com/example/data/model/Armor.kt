package com.example.data.model

data class Armor(
    val id: Int,
    val name: String,
    val englishName: String,
    val description: String,
    val damageReductionPercent: Int, // نسبة امتصاص الضرر (مثال 15%)
    val bonusHp: Int,               // نقاط حياة إضافية تضاف للاعب
    val costGold: Int,
    val costGems: Int = 0
)

object ArmorsCatalog {
    val allArmors: List<Armor> = listOf(
        Armor(
            id = 1,
            name = "درع المحارب الجلدي المصفح",
            englishName = "Reinforced Leather Tunic",
            description = "درع خفيف مرن يوفر حماية أساسية دون إعاقة سرعة الحركة في القتال.",
            damageReductionPercent = 5,
            bonusHp = 50,
            costGold = 0
        ),
        Armor(
            id = 2,
            name = "درع الحديد الصلب المعزز",
            englishName = "Hardened Iron Plate",
            description = "صفائح حديدية مدعمة بحلقات صلبة تمتص صدمات الضربات المباشرة بكفاءة.",
            damageReductionPercent = 10,
            bonusHp = 100,
            costGold = 450
        ),
        Armor(
            id = 3,
            name = "درع الفولاذ السماوي",
            englishName = "Celestial Steel Aegis",
            description = "فولاذ نقي مسبوك تحت تأثير إشعاع النجوم، يمنح صلابة استثنائية وصد أضرار عالي.",
            damageReductionPercent = 16,
            bonusHp = 180,
            costGold = 900
        ),
        Armor(
            id = 4,
            name = "حراشف التنين الأحمر الملتهب",
            englishName = "Red Dragon Scale Mail",
            description = "حراشف مأخوذة من تنين بركاني عتيق؛ تمنح مقاومة هائلة للحروق والهجمات النارية.",
            damageReductionPercent = 22,
            bonusHp = 260,
            costGold = 1500,
            costGems = 20
        ),
        Armor(
            id = 5,
            name = "رداء الظلال الأثيري",
            englishName = "Aetherial Shadow Robe",
            description = "نسيج غامض من طاقة الفراغ يجعل حامل الدرع شبه شبحي ومحصن ضد الضربات الغادرة.",
            damageReductionPercent = 26,
            bonusHp = 320,
            costGold = 2200,
            costGems = 35
        ),
        Armor(
            id = 6,
            name = "درع الصقيع الأبدي",
            englishName = "Glacial Frost Bulwark",
            description = "بلورات جليدية سميكة مأخوذة من جبال يومير تبطئ قوة هجمات الخصوم عند ملامستها.",
            damageReductionPercent = 30,
            bonusHp = 400,
            costGold = 3000,
            costGems = 50
        ),
        Armor(
            id = 7,
            name = "درع النيازك الكونية المضغوطة",
            englishName = "Cosmic Meteor Carapace",
            description = "سبائك نيزكية هبطت من أعماق الفضاء، تصمد في وجه أقوى الانفجارات الطاقوية.",
            damageReductionPercent = 35,
            bonusHp = 500,
            costGold = 4000,
            costGems = 75
        ),
        Armor(
            id = 8,
            name = "درع المادة المظلمة الحصين",
            englishName = "Dark Matter Citadel",
            description = "حقل دفاعي جاذبي يحرف مسار نصف الضربات القادمة ويحمي نقاط الضعف الحيوية.",
            damageReductionPercent = 40,
            bonusHp = 650,
            costGold = 5500,
            costGems = 100
        ),
        Armor(
            id = 9,
            name = "هيكل التيتانيوم النانوي التكتيكي",
            englishName = "Nanite Titanium Exoskeleton",
            description = "هيكل خارجي ذكي يعيد ترميم نفسه ذاتياً ويزيد قدرة التحمل البدني إلى مستويات قياسية.",
            damageReductionPercent = 45,
            bonusHp = 800,
            costGold = 7500,
            costGems = 150
        ),
        Armor(
            id = 10,
            name = "درع الخلود الأوميغا الأعظم",
            englishName = "Omega Eternity Plate",
            description = "الدرع الأسطوري لحكام الأكوان؛ لا يخترق إلا بالمعجزات ويمنح حامله قوة صمود خارقة.",
            damageReductionPercent = 52,
            bonusHp = 1000,
            costGold = 10000,
            costGems = 250
        )
    )

    fun getArmorById(id: Int): Armor {
        return allArmors.find { it.id == id } ?: allArmors.first()
    }
}

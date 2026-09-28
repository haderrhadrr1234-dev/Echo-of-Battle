package com.example.data.model

data class TitanBoss(
    val id: Int,
    val name: String,
    val title: String,
    val description: String,
    val maxHp: Int,
    val baseDamage: Int,
    val defensePercent: Int,
    val specialAttackName: String,
    val specialAttackDamage: Int,
    val rewardGold: Int,
    val rewardGems: Int,
    val rewardWeaponId: Int?, // سلاح أسطوري يفتحه اللاعب فور هزيمة الزعيم لأول مرة
    val difficultyRank: String // S, SS, SSS, MYTHIC, GODLY
)

object TitansCatalog {
    val allTitans: List<TitanBoss> = listOf(
        TitanBoss(
            id = 1,
            name = "باهاموت تنين الفوضى الأبدي",
            title = "ملك التنانين البدائية",
            description = "تنين أسطوري يلف جسده لهب كوني أسود، تزأر أنفاسه بصوت الرعب وتهز أركان ساحة المعركة.",
            maxHp = 850,
            baseDamage = 85,
            defensePercent = 15,
            specialAttackName = "أنفاس الجحيم الكونية",
            specialAttackDamage = 160,
            rewardGold = 1200,
            rewardGems = 25,
            rewardWeaponId = 31,
            difficultyRank = "S"
        ),
        TitanBoss(
            id = 2,
            name = "يومير عملاق الصقيع الكوني",
            title = "حاكم جبال الجليد الأزلي",
            description = "عملاق صخري مغطى ببلورات جليد الصفر المطلق، يمتلك درعاً صلداً ويطلق عواصف ثلجية تجمد الخصوم.",
            maxHp = 1100,
            baseDamage = 95,
            defensePercent = 25,
            specialAttackName = "عاصفة السحق الجليدية",
            specialAttackDamage = 180,
            rewardGold = 1800,
            rewardGems = 40,
            rewardWeaponId = 34,
            difficultyRank = "SS"
        ),
        TitanBoss(
            id = 3,
            name = "أركوس سيد الصواعق الفلكية",
            title = "إمبراطور العواصف الرعدية",
            description = "كيان من الطاقة الكهربائية النقية، يتحرك بسرعة الضوء ويضرب بصواعق رعدية ثلاثية متتالية.",
            maxHp = 1000,
            baseDamage = 110,
            defensePercent = 12,
            specialAttackName = "طوفان البرق المزلزل",
            specialAttackDamage = 210,
            rewardGold = 2400,
            rewardGems = 55,
            rewardWeaponId = 32,
            difficultyRank = "SS"
        ),
        TitanBoss(
            id = 4,
            name = "فولكان حارس قلب البراكين",
            title = "عملاق الحمم المنصهرة",
            description = "كتلة هائلة من الصهارة البركانية والحديد السائل، ينفجر غضبه بحمم مشتعلة تحرق كل ما يحيط به.",
            maxHp = 1350,
            baseDamage = 105,
            defensePercent = 28,
            specialAttackName = "انفجار البراكين العظمى",
            specialAttackDamage = 220,
            rewardGold = 3200,
            rewardGems = 70,
            rewardWeaponId = 39,
            difficultyRank = "SSS"
        ),
        TitanBoss(
            id = 5,
            name = "إيريبوس ظل الهاوية المطلقة",
            title = "سيد عوالم الفراغ",
            description = "شبح أسطوري عملاق يبتلع النور والأصوات، يسرق طاقة الخصم ويشن هجمات مجهولة الاتجاه.",
            maxHp = 1500,
            baseDamage = 125,
            defensePercent = 30,
            specialAttackName = "ابتلاع الفراغ المظلم",
            specialAttackDamage = 250,
            rewardGold = 4500,
            rewardGems = 100,
            rewardWeaponId = 35,
            difficultyRank = "MYTHIC"
        ),
        TitanBoss(
            id = 6,
            name = "كرونوس حاكم الزمان والأبعاد الأعظم",
            title = "سيد الأكوان والخلود",
            description = "الزعيم الأسطوري المطلق؛ يمزق نسيج الزمان والمكان، ويوجه ضربات لا يمكن صدها إلا بأعظم الدروع والأسلحة.",
            maxHp = 2200,
            baseDamage = 150,
            defensePercent = 35,
            specialAttackName = "الانهيار الزمني الشامل",
            specialAttackDamage = 320,
            rewardGold = 8000,
            rewardGems = 200,
            rewardWeaponId = 50,
            difficultyRank = "GODLY"
        )
    )

    fun getTitanById(id: Int): TitanBoss {
        return allTitans.find { it.id == id } ?: allTitans.first()
    }
}

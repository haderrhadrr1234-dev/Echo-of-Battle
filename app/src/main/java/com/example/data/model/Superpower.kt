package com.example.data.model

data class Superpower(
    val id: Int,
    val name: String,
    val englishName: String,
    val description: String,
    val damage: Int,
    val energyCost: Int,
    val soundProfile: SuperpowerSoundProfile,
    val costGold: Int = 0
)

enum class SuperpowerSoundProfile {
    CHRONO_FREEZE,
    DIMENSIONAL_MIRROR,
    SUPERNOVA_BURST,
    VOID_WRAITH,
    QUANTUM_SHIFT,
    ORBITAL_LIGHTNING,
    EMP_SHOCKWAVE,
    DRAGON_ROAR,
    STARLIGHT_SIPHON,
    INVERTED_GRAVITY,
    HYDROGEN_WARD,
    SPEED_BURST,
    POCKET_DIMENSION,
    TOXIC_NEBULA,
    INFRASOUND_BLAST,
    METEOR_STRIKE,
    QUANTUM_BARRAGE,
    SUBATOMIC_BLINK,
    VACUUM_BOMB,
    MIND_FRACTURE,
    MAGNETIC_CITADEL,
    PHOENIX_REBIRTH,
    HARMONIC_CRUSHER,
    GLACIAL_SILENCE,
    PHOTON_WEB,
    PLASMA_FUSION,
    ANTIMATTER_ECHO,
    AURORA_BEAM,
    SENSORY_EYE,
    OMEGA_CATACLYSM
}

object SuperpowersCatalog {
    val allSuperpowers: List<Superpower> = listOf(
        Superpower(
            id = 1,
            name = "نبض إيقاف الزمن التكتيكي",
            englishName = "Chrono Freeze",
            description = "تجميد تدفق الوقت لثوانٍ معدودة؛ يتيح توجيه ضربات لا يمكن تفاديها.",
            damage = 110,
            energyCost = 50,
            soundProfile = SuperpowerSoundProfile.CHRONO_FREEZE
        ),
        Superpower(
            id = 2,
            name = "درع الانعكاس المرآتي البعدي",
            englishName = "Dimensional Mirror Aegis",
            description = "درع يعكس هجوم الخصم كاملاً مع مضاعفة الصدمة الارتدادية في وجهه.",
            damage = 130,
            energyCost = 60,
            soundProfile = SuperpowerSoundProfile.DIMENSIONAL_MIRROR,
            costGold = 100
        ),
        Superpower(
            id = 3,
            name = "انفجار السوبرنوفا الداخلي",
            englishName = "Supernova Burst",
            description = "تفجير طاقة شمسية ساحقة تصدر وميضاً حرارياً هائلاً وموجة ارتجاجية خارقة.",
            damage = 160,
            energyCost = 75,
            soundProfile = SuperpowerSoundProfile.SUPERNOVA_BURST,
            costGold = 130
        ),
        Superpower(
            id = 4,
            name = "استدعاء شبح الفراغ المظلم",
            englishName = "Void Wraith Summon",
            description = "كائن طيفي من أبعاد العدم يظهر خلف الخصم ويشن هجوماً مرعباً على حواسه.",
            damage = 120,
            energyCost = 55,
            soundProfile = SuperpowerSoundProfile.VOID_WRAITH,
            costGold = 150
        ),
        Superpower(
            id = 5,
            name = "تبديل الواقع الكمومي",
            englishName = "Quantum Reality Shift",
            description = "إعادة ترتيب احتمالات المعركة لإلغاء ضرر الخصم وتوجيه صدمة احتمالية مضادة.",
            damage = 125,
            energyCost = 60,
            soundProfile = SuperpowerSoundProfile.QUANTUM_SHIFT,
            costGold = 180
        ),
        Superpower(
            id = 6,
            name = "العاصفة الرعدية المدارية",
            englishName = "Orbital Lightning Storm",
            description = "استدعاء صواعق أيونية من الغلاف الجوي تضرب ساحة المعركة بتتابع مرعد.",
            damage = 145,
            energyCost = 70,
            soundProfile = SuperpowerSoundProfile.ORBITAL_LIGHTNING,
            costGold = 220
        ),
        Superpower(
            id = 7,
            name = "النبض الكهرومغناطيسي المعمي",
            englishName = "Blinding EMP Shockwave",
            description = "موجة ترددية تعطل جميع الأسلحة والأجهزة وتصعق الجهاز العصبي للخصم.",
            damage = 115,
            energyCost = 50,
            soundProfile = SuperpowerSoundProfile.EMP_SHOCKWAVE,
            costGold = 250
        ),
        Superpower(
            id = 8,
            name = "زئير التنين الأثيري",
            englishName = "Aetherial Dragon Roar",
            description = "تردد صوتي هائل تحت-صوتي يخلخل توازن الخصم ويسحق إرادته القتالية.",
            damage = 150,
            energyCost = 75,
            soundProfile = SuperpowerSoundProfile.DRAGON_ROAR,
            costGold = 280
        ),
        Superpower(
            id = 9,
            name = "امتصاص طاقة النجوم",
            englishName = "Starlight Energy Siphon",
            description = "سحب طاقة حيوية من الخصم وتحويلها إلى نقاط حياة وطاقة للمقاتل.",
            damage = 100,
            energyCost = 45,
            soundProfile = SuperpowerSoundProfile.STARLIGHT_SIPHON,
            costGold = 300
        ),
        Superpower(
            id = 10,
            name = "حقل الجاذبية المعكوس",
            englishName = "Inverted Gravity Field",
            description = "رفع الخصم عالياً في الهواء ثم إسقاطه بسرعة خارقة ليرتطم بقوة مدوية.",
            damage = 135,
            energyCost = 65,
            soundProfile = SuperpowerSoundProfile.INVERTED_GRAVITY,
            costGold = 330
        ),
        Superpower(
            id = 11,
            name = "درع الهيدروجين المشتعل",
            englishName = "Blazing Hydrogen Ward",
            description = "هالة نارية حارقة تحرق الخصم عند الاقتراب وتمتص جزءاً كبيراً من هجماته.",
            damage = 110,
            energyCost = 50,
            soundProfile = SuperpowerSoundProfile.HYDROGEN_WARD,
            costGold = 360
        ),
        Superpower(
            id = 12,
            name = "تسارع النبض الحركي الفائق",
            englishName = "Hyperkinetic Speed Burst",
            description = "التحرك بسرعة الصوت وتسديد سلسلة ضربات متتابعة خاطفة.",
            damage = 140,
            energyCost = 65,
            soundProfile = SuperpowerSoundProfile.SPEED_BURST,
            costGold = 400
        ),
        Superpower(
            id = 13,
            name = "تمزيق الأبعاد الجيبية",
            englishName = "Pocket Dimension Rend",
            description = "فتح شق في الفضاء يسحب الخصم لثانية واحدة ويعيده مصدوماً وممزقاً.",
            damage = 155,
            energyCost = 75,
            soundProfile = SuperpowerSoundProfile.POCKET_DIMENSION,
            costGold = 430
        ),
        Superpower(
            id = 14,
            name = "سديم الغبار السام الكوني",
            englishName = "Cosmic Nebula Toxic Miasma",
            description = "سحابة فضائية سامة تخنق دفاعات الخصم وتلحق به ضرراً كيميائياً فتاكاً.",
            damage = 125,
            energyCost = 55,
            soundProfile = SuperpowerSoundProfile.TOXIC_NEBULA,
            costGold = 460
        ),
        Superpower(
            id = 15,
            name = "صدمة التردد الطيفي",
            englishName = "Spectral Infrasound Blast",
            description = "ترددات اهتزازية منخفضة تخترق أعماق الدروع وتحدث صدمة مدوية.",
            damage = 130,
            energyCost = 60,
            soundProfile = SuperpowerSoundProfile.INFRASOUND_BLAST,
            costGold = 490
        ),
        Superpower(
            id = 16,
            name = "استدعاء نيزك الجحيم",
            englishName = "Oblivion Meteor Strike",
            description = "سقوط نيزك ناري ملتهب من أعالي الفضاء يسحق ساحة المعركة برمتها.",
            damage = 175,
            energyCost = 85,
            soundProfile = SuperpowerSoundProfile.METEOR_STRIKE,
            costGold = 530
        ),
        Superpower(
            id = 17,
            name = "طفرة الليزر الكوانتي",
            englishName = "Quantum Laser Barrage",
            description = "مئات الأشعة الليزرية المتقاطعة تنهال من كل زاوية مسببة دماراً شاملاً.",
            damage = 148,
            energyCost = 70,
            soundProfile = SuperpowerSoundProfile.QUANTUM_BARRAGE,
            costGold = 560
        ),
        Superpower(
            id = 18,
            name = "انتقال الإزاحة الفورية",
            englishName = "Subatomic Blink",
            description = "اختفاء آني من أمام هجوم العدو والظهور المباشر لتوجيه ضربة قاصمة.",
            damage = 132,
            energyCost = 60,
            soundProfile = SuperpowerSoundProfile.SUBATOMIC_BLINK,
            costGold = 600
        ),
        Superpower(
            id = 19,
            name = "قنبلة الانشطار الفراغي",
            englishName = "Vacuum Collapse Bomb",
            description = "تفريغ مفاجئ للضغط الجوي يليه انفجار ضغط هائل يدمر توازن الخصم.",
            damage = 142,
            energyCost = 68,
            soundProfile = SuperpowerSoundProfile.VACUUM_BOMB,
            costGold = 640
        ),
        Superpower(
            id = 20,
            name = "صرخة الصدمة النفسية الحيوية",
            englishName = "Psionic Mind Fracture",
            description = "موجة فكرية حيوية تشل إدراك العدو وتشعره بأصوات رنين قاهر يفقده توازنه.",
            damage = 138,
            energyCost = 62,
            soundProfile = SuperpowerSoundProfile.MIND_FRACTURE,
            costGold = 680
        ),
        Superpower(
            id = 21,
            name = "درع العواصف المغناطيسية",
            englishName = "Magnetic Storm Citadel",
            description = "حصن مغناطيسي يمتص الهجمات ويطلق شحنة كهربائية مضادة تصعق المهاجم.",
            damage = 118,
            energyCost = 55,
            soundProfile = SuperpowerSoundProfile.MAGNETIC_CITADEL,
            costGold = 720
        ),
        Superpower(
            id = 22,
            name = "ولادة العنقاء المتوهجة",
            englishName = "Phoenix Rebirth Surge",
            description = "طفرة نارية تجدد طاقة المقاتل وتطلق نيران البعث المدمرة حوله.",
            damage = 152,
            energyCost = 75,
            soundProfile = SuperpowerSoundProfile.PHOENIX_REBIRTH,
            costGold = 760
        ),
        Superpower(
            id = 23,
            name = "سحق الترددات التناغمية",
            englishName = "Harmonic Crusher",
            description = "موجة صوتية متوافقة مع تردد المادة تذيب درع العدو وتفقده مناعته الدفاعية.",
            damage = 146,
            energyCost = 70,
            soundProfile = SuperpowerSoundProfile.HARMONIC_CRUSHER,
            costGold = 800
        ),
        Superpower(
            id = 24,
            name = "قبة الجليد الصامت",
            englishName = "Glacial Silence Dome",
            description = "قبة جليدية تخفض درجات الحرارة فوراً وتجمد الخصم وسط صمت مطبق.",
            damage = 135,
            energyCost = 65,
            soundProfile = SuperpowerSoundProfile.GLACIAL_SILENCE,
            costGold = 840
        ),
        Superpower(
            id = 25,
            name = "حزام الفوتونات المتشابكة",
            englishName = "Entangled Photon Web",
            description = "شبكة ضوئية خفية تكبل أطراف الخصم وتفرغ فيه شحنات فوتونية متفجرة.",
            damage = 144,
            energyCost = 68,
            soundProfile = SuperpowerSoundProfile.PHOTON_WEB,
            costGold = 880
        ),
        Superpower(
            id = 26,
            name = "اندماج البلازما التدميري",
            englishName = "Plasma Fusion Overload",
            description = "تحميل زائد لطاقة البلازما ينفجر بلهب أزرق خارق يدمر كل ما أمامه.",
            damage = 168,
            energyCost = 82,
            soundProfile = SuperpowerSoundProfile.PLASMA_FUSION,
            costGold = 920
        ),
        Superpower(
            id = 27,
            name = "موجة الصدى المضادة للمادة",
            englishName = "Antimatter Echo Wave",
            description = "موجة عدم تمسح قسماً من واقع الخصم وتخلف صفيراً طويلاً مدمراً.",
            damage = 172,
            energyCost = 85,
            soundProfile = SuperpowerSoundProfile.ANTIMATTER_ECHO,
            costGold = 960
        ),
        Superpower(
            id = 28,
            name = "انفجار الشفق القطبي المكهرب",
            englishName = "Electrified Aurora Beam",
            description = "شعاع ساطع متعدد الألوان مشحون بمليارات الفولتات يصيب الهدف بدقة مطلقة.",
            damage = 162,
            energyCost = 78,
            soundProfile = SuperpowerSoundProfile.AURORA_BEAM,
            costGold = 1000
        ),
        Superpower(
            id = 29,
            name = "عين الإدراك الحسي المطلق",
            englishName = "All-Seeing Sensory Eye",
            description = "بصيرة سمعية وفكرية خارقة تكشف نقطة ضعف الخصم القاتلة وتضربها بقسوة.",
            damage = 180,
            energyCost = 90,
            soundProfile = SuperpowerSoundProfile.SENSORY_EYE,
            costGold = 1100
        ),
        Superpower(
            id = 30,
            name = "دمار الأوميغا الكوني الشامل",
            englishName = "Omega Cataclysm Apocalypse",
            description = "القوة المطلقة العظمى؛ انفجار كوني يسحق أبعاد ساحة المعركة بصوت يزلزل الأكوان.",
            damage = 220,
            energyCost = 100,
            soundProfile = SuperpowerSoundProfile.OMEGA_CATACLYSM,
            costGold = 1500
        )
    )

    fun getSuperpowerById(id: Int): Superpower {
        return allSuperpowers.find { it.id == id } ?: allSuperpowers.first()
    }
}

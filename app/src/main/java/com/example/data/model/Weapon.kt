package com.example.data.model

data class Weapon(
    val id: Int,
    val name: String,
    val englishName: String,
    val description: String,
    val damage: Int,
    val speed: Float, // 1.0 = normal, higher = faster
    val soundProfile: SoundProfile,
    val costGold: Int = 0
)

enum class SoundProfile {
    TEMPORAL_ECHO,
    BLACK_HOLE,
    PLASMA_WHIP,
    AETHER_BOW,
    ECLIPSE_HAMMER,
    SILENCE_DAGGER,
    SEISMIC_BLASTER,
    METEOR_MACE,
    VOID_FROST,
    MATTER_DISSOLVER,
    DEMONIC_CLAWS,
    MAGNETIC_SCYTHE,
    GRAVITY_PISTOL,
    MULTIVERSE_CLEAVER,
    ANTIMATTER_CANNON,
    THUNDER_AXE,
    BLOOD_BLADE,
    NANITE_CLUSTER,
    SPATIAL_RUPTURE,
    SUPERSONIC_SCEPTER,
    SOLAR_FLARE,
    LIVING_SHADOW_KUNAI,
    PROTON_SABRE,
    MAGMA_MORTAR,
    CHAOS_KATANA,
    SPECTRAL_RECOIL,
    HYPERSTRING_TETHER,
    IONIC_RAPIER,
    CRYO_BLASTER,
    OMEGA_SINGULARITY
}

object WeaponsCatalog {
    val allWeapons: List<Weapon> = listOf(
        Weapon(
            id = 1,
            name = "سيف الصدى الزمني",
            englishName = "Temporal Echo Blade",
            description = "نصل بلوري غريب يضرب في الماضي والمستقبل في لحظة واحدة، محدثاً ارتداداً زمنياً مضاعفاً.",
            damage = 75,
            speed = 1.3f,
            soundProfile = SoundProfile.TEMPORAL_ECHO
        ),
        Weapon(
            id = 2,
            name = "مدفع الثقب الأسود المصغر",
            englishName = "Micro Black Hole Cannon",
            description = "سلاح مدمج يطلق ثقوباً دودية متناهية الصغر تبتلع ذرات الخصم وتمزق جاذبيته.",
            damage = 95,
            speed = 0.8f,
            soundProfile = SoundProfile.BLACK_HOLE,
            costGold = 100
        ),
        Weapon(
            id = 3,
            name = "سوط البلازما الكمومية",
            englishName = "Quantum Plasma Whip",
            description = "سلسلة من الأيونات المشحونة تتلوى كالأفعى وتخترق الأبعاد لتلدغ الخصم بصوت مفرقع.",
            damage = 70,
            speed = 1.5f,
            soundProfile = SoundProfile.PLASMA_WHIP,
            costGold = 120
        ),
        Weapon(
            id = 4,
            name = "قوس البرق الأثيري",
            englishName = "Aether Lightning Bow",
            description = "وتر من الطاقة النقية يطلق سهاماً كهرومغناطيسية تتتبع ذبذبات قلب الخصم.",
            damage = 80,
            speed = 1.2f,
            soundProfile = SoundProfile.AETHER_BOW,
            costGold = 150
        ),
        Weapon(
            id = 5,
            name = "مطرقة الكسوف النجمي",
            englishName = "Solar Eclipse Warhammer",
            description = "كتلة من مادة النجوم المنهارة، تسحق الأرض وتحدث موجات جاذبية تفقد الخصم توازنه.",
            damage = 110,
            speed = 0.7f,
            soundProfile = SoundProfile.ECLIPSE_HAMMER,
            costGold = 200
        ),
        Weapon(
            id = 6,
            name = "خنجر الصمت المطلق",
            englishName = "Absolute Silence Dagger",
            description = "نصل مظلم يمتص الصوت والحرارة المحيطة؛ طعنته تخلق فراغاً صوتياً يربك الحواس.",
            damage = 65,
            speed = 1.7f,
            soundProfile = SoundProfile.SILENCE_DAGGER,
            costGold = 220
        ),
        Weapon(
            id = 7,
            name = "قاذف الموجات التدميرية الزلزالية",
            englishName = "Seismic Pulse Blaster",
            description = "جهاز صوتي ثقيل يرسل هزات تحت صوتية تمزق الدروع من الداخل إلى الخارج.",
            damage = 85,
            speed = 1.0f,
            soundProfile = SoundProfile.SEISMIC_BLASTER,
            costGold = 250
        ),
        Weapon(
            id = 8,
            name = "هراوة النيازك المشعة",
            englishName = "Radiant Meteor Mace",
            description = "محفورة من قلب كويكب ملتهب، تتوهج بإشعاعات كونية تحرق الخصم عند كل ارتطام.",
            damage = 90,
            speed = 0.9f,
            soundProfile = SoundProfile.METEOR_MACE,
            costGold = 280
        ),
        Weapon(
            id = 9,
            name = "شفرة الجليد الحارق المظلم",
            englishName = "Dark Void Frostblade",
            description = "نصل يتحدى الفيزياء؛ جليده يحرق الخلايا وصوته يشبه تكسر زجاج الأبعاد المجهولة.",
            damage = 82,
            speed = 1.2f,
            soundProfile = SoundProfile.VOID_FROST,
            costGold = 300
        ),
        Weapon(
            id = 10,
            name = "رمح تفتيت المادة الكونية",
            englishName = "Cosmic Matter Dissolver",
            description = "رمح نانوي يفكك الروابط بين الذرات، محولاً صلب دفاعات العدو إلى هباء متطاير.",
            damage = 100,
            speed = 1.0f,
            soundProfile = SoundProfile.MATTER_DISSOLVER,
            costGold = 350
        ),
        Weapon(
            id = 11,
            name = "مخالب التردد الشيطاني",
            englishName = "Demonic Frequency Claws",
            description = "مخالب تهتز بملايين الذبذبات في الثانية مسببة رنيناً تمزقياً للأنسجة والدروع.",
            damage = 78,
            speed = 1.6f,
            soundProfile = SoundProfile.DEMONIC_CLAWS,
            costGold = 380
        ),
        Weapon(
            id = 12,
            name = "قيثارة الموت المغناطيسية",
            englishName = "Magnetic Harvester Scythe",
            description = "منجل عملاق مغناطيسي يولد حقلاً كهرومغناطيسياً يسحب طاقة الخصم الحركية.",
            damage = 92,
            speed = 0.9f,
            soundProfile = SoundProfile.MAGNETIC_SCYTHE,
            costGold = 400
        ),
        Weapon(
            id = 13,
            name = "مسدس الانشطار الجاذبي",
            englishName = "Gravitational Fission Pistol",
            description = "سلاح خفيف يطلق كريات طاقة مجهرية تنفجر وتدفع الخصم في عدة اتجاهات في وقت واحد.",
            damage = 72,
            speed = 1.4f,
            soundProfile = SoundProfile.GRAVITY_PISTOL,
            costGold = 420
        ),
        Weapon(
            id = 14,
            name = "شفرة الأبعاد المتوازية",
            englishName = "Multiverse Rift Cleaver",
            description = "ساطور مهول يقطع عبر واقع موازٍ، فلا يستطيع الخصم رؤية مسار الضربة أو التنبؤ بها.",
            damage = 105,
            speed = 0.8f,
            soundProfile = SoundProfile.MULTIVERSE_CLEAVER,
            costGold = 450
        ),
        Weapon(
            id = 15,
            name = "مدفع البوزيترون المضاد",
            englishName = "Antimatter Positron Cannon",
            description = "يطلق حزم مضادات المادة التي تصطدم بإلكترونات الدرع وتسبب انفجاراً فوتونياً صاعقاً.",
            damage = 115,
            speed = 0.7f,
            soundProfile = SoundProfile.ANTIMATTER_CANNON,
            costGold = 500
        ),
        Weapon(
            id = 16,
            name = "فأس العواصف الرعدية الميكانيكية",
            englishName = "Mecha Storm Thunder Axe",
            description = "فأس ثقيل بمحرك نفاث داخلي يولد صواعق بقوة مليون فولت عند كل ارتطام.",
            damage = 98,
            speed = 0.85f,
            soundProfile = SoundProfile.THUNDER_AXE,
            costGold = 530
        ),
        Weapon(
            id = 17,
            name = "شفرة الدم البلوري",
            englishName = "Crystalline Bloodblade",
            description = "سيف عضوي بلوري يمتص الصدمات الصوتية ويتحول لون رنينه بحسب قوة الإصابة.",
            damage = 84,
            speed = 1.3f,
            soundProfile = SoundProfile.BLOOD_BLADE,
            costGold = 550
        ),
        Weapon(
            id = 18,
            name = "قاذفة النوى النانوية المتفجرة",
            englishName = "Nanite Swarm Cluster",
            description = "مجموعة من الروبوتات النانوية تطلق كغيمة سوداء تلتصق بالدرع وتنفجر بتناغم مروع.",
            damage = 88,
            speed = 1.1f,
            soundProfile = SoundProfile.NANITE_CLUSTER,
            costGold = 580
        ),
        Weapon(
            id = 19,
            name = "منجل تمزق الفضاء",
            englishName = "Spatial Rupture Scythe",
            description = "يقطع الفراغ الفيزيائي نفسه؛ يخلف وراءه صفيراً كونياً مرعباً يشق جدار الصوت.",
            damage = 102,
            speed = 0.9f,
            soundProfile = SoundProfile.SPATIAL_RUPTURE,
            costGold = 600
        ),
        Weapon(
            id = 20,
            name = "صولجان النبض الفوق صوتي",
            englishName = "Supersonic Resonator",
            description = "صولجان ذهبي يصدر موجات عالية التردد تسبب عمى مؤقتاً وشللاً حركياً فورياً للخصم.",
            damage = 76,
            speed = 1.4f,
            soundProfile = SoundProfile.SUPERSONIC_SCEPTER,
            costGold = 620
        ),
        Weapon(
            id = 21,
            name = "قاذف الرياح الشمسية",
            englishName = "Solar Flare Projector",
            description = "يطلق تياراً حارقاً من الجسيمات الشمسية المشحونة، محولاً الهواء إلى فرن حارق.",
            damage = 94,
            speed = 1.0f,
            soundProfile = SoundProfile.SOLAR_FLARE,
            costGold = 650
        ),
        Weapon(
            id = 22,
            name = "خناجر الظلال الحية",
            englishName = "Living Shadow Kunai",
            description = "خناجر مصنوعة من مادة الظلام الكثيفة؛ تتحرك ذاتياً وتنقض من الزوايا الميتة للخصم.",
            damage = 68,
            speed = 1.8f,
            soundProfile = SoundProfile.LIVING_SHADOW_KUNAI,
            costGold = 680
        ),
        Weapon(
            id = 23,
            name = "قاطعة البروتونات المتسارعة",
            englishName = "Accelerated Proton Sabre",
            description = "نصل ليزري فائق التماسك يقطع أي مادة معروفة كما يقطع السكين الساخن الزبدة.",
            damage = 89,
            speed = 1.25f,
            soundProfile = SoundProfile.PROTON_SABRE,
            costGold = 700
        ),
        Weapon(
            id = 24,
            name = "هاون الصهارة النووية",
            englishName = "Nuclear Magma Mortar",
            description = "سلاح ثقيل يقذف قذائف مصهورة تنفجر بموجات حرارية خانقة وارتجاج عنيف.",
            damage = 112,
            speed = 0.75f,
            soundProfile = SoundProfile.MAGMA_MORTAR,
            costGold = 750
        ),
        Weapon(
            id = 25,
            name = "سيف الفوضى الهيدروجينية",
            englishName = "Hydrogen Chaos Katana",
            description = "كاتانا يابانية مطعمة بطاقة الاندماج الهيدروجيني، ضرباتها تشعل وميضاً شمسياً خاطفاً.",
            damage = 86,
            speed = 1.35f,
            soundProfile = SoundProfile.CHAOS_KATANA,
            costGold = 780
        ),
        Weapon(
            id = 26,
            name = "مطرقة ارتداد الصوت الطيفي",
            englishName = "Spectral Sonic Hammer",
            description = "مطرقة عريضة تحدث انفجاراً صوتياً يرتد بين الجدران مضاعفاً أثر الضربة.",
            damage = 96,
            speed = 0.85f,
            soundProfile = SoundProfile.SPECTRAL_RECOIL,
            costGold = 800
        ),
        Weapon(
            id = 27,
            name = "قاذف أوتار الجاذبية الفائقة",
            englishName = "Hyperstring Gravity Tether",
            description = "خيوط طاقوية غير مرئية تلتف حول الخصم وتسحقه تحت وطأة ضغط الجاذبية الفضائية.",
            damage = 91,
            speed = 1.1f,
            soundProfile = SoundProfile.HYPERSTRING_TETHER,
            costGold = 840
        ),
        Weapon(
            id = 28,
            name = "شفرة الأثير الأيونية",
            englishName = "Ionic Aether Rapier",
            description = "سيف مبارزة نحيف يخترق الحواجز بسرعة الضوء مع طنين أيوني عالي التردد.",
            damage = 74,
            speed = 1.75f,
            soundProfile = SoundProfile.IONIC_RAPIER,
            costGold = 880
        ),
        Weapon(
            id = 29,
            name = "مدفع التجميد المطلق",
            englishName = "Zero-Kelvin Cryo-Blaster",
            description = "شعاع تجميد ينزل بدرجة حرارة الهدف إلى الصفر المطلق فتتوقف حركة جزيئاته نهائياً.",
            damage = 108,
            speed = 0.8f,
            soundProfile = SoundProfile.CRYO_BLASTER,
            costGold = 920
        ),
        Weapon(
            id = 30,
            name = "سلاح نهاية العوالم الأوميغا",
            englishName = "Omega Singularity Annihilator",
            description = "السلاح الأسطوري المحرم؛ تفجير أحادي البعد يسحق الزمان والمكان ويدك الخصم بقوة كونية.",
            damage = 140,
            speed = 0.65f,
            soundProfile = SoundProfile.OMEGA_SINGULARITY,
            costGold = 1200
        )
    )

    fun getWeaponById(id: Int): Weapon {
        return allWeapons.find { it.id == id } ?: allWeapons.first()
    }
}

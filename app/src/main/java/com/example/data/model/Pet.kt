package com.example.data.model

data class Pet(
    val id: Int,
    val name: String,
    val englishName: String,
    val description: String,
    val attackDamage: Int,        // ضرر هجوم المرافق في كل دور
    val healAmountPerTurn: Int,   // نقاط الشفاء التي يمنحها للمحارب في كل دور
    val specialEffect: String,    // وصف التأثير الخاص للمرافق
    val costGold: Int,
    val costGems: Int = 0
)

object PetsCatalog {
    val allPets: List<Pet> = listOf(
        Pet(
            id = 1,
            name = "دراكو التنين الصغير الملتهب",
            englishName = "Drako Fire Whelp",
            description = "تنين صغير وفي يطلق كرات نارية حارقة تدعم هجماتك في المعركة.",
            attackDamage = 25,
            healAmountPerTurn = 0,
            specialEffect = "هجوم ناري إضافي كل دور",
            costGold = 0
        ),
        Pet(
            id = 2,
            name = "فنرير ذئب الصواعق الفلكية",
            englishName = "Fenrir Thunder Wolf",
            description = "ذئب سريع مشحون بالبرق ينقض على الخصوم ويشتت انتباههم.",
            attackDamage = 35,
            healAmountPerTurn = 5,
            specialEffect = "صعق كهربائي وسرعة إضافية",
            costGold = 600,
            costGems = 10
        ),
        Pet(
            id = 3,
            name = "سيلف طائر الفينيق الشافي",
            englishName = "Sylph Celestial Phoenix",
            description = "طائر نوراني يرفرف فوقك في المعركة ويمطر عليك قطرات من الشفاء الروحي كل دور.",
            attackDamage = 15,
            healAmountPerTurn = 30,
            specialEffect = "ترميم دائم لصحة اللاعب كل دور",
            costGold = 1200,
            costGems = 25
        ),
        Pet(
            id = 4,
            name = "غورغون الغولم الحجري الحارس",
            englishName = "Gorgon Stone Guardian",
            description = "مخلوق صخري عملاق مصغر يمتص الضربات الغادرة ويحمي سيده بجسده الصلب.",
            attackDamage = 20,
            healAmountPerTurn = 15,
            specialEffect = "تقليص ضرر الوحوش بنسبة 10%",
            costGold = 2000,
            costGems = 40
        ),
        Pet(
            id = 5,
            name = "ويكس شبح الفراغ الكوني",
            englishName = "Vex Cosmic Void Phantom",
            description = "كائن طيفي يتسلل خلف دروع الخصم ويسرق جزءاً من طاقته ليشحن قواك الخارقة.",
            attackDamage = 45,
            healAmountPerTurn = 10,
            specialEffect = "استنزاف طاقة العدو وشحن القوة",
            costGold = 3200,
            costGems = 60
        ),
        Pet(
            id = 6,
            name = "تيتان سلحفاة الكريستال الأزلية",
            englishName = "Titan Crystal Tortoise",
            description = "سلحفاة أسطورية تحمل درعاً بلورياً يصد ضربات الزعماء الساحقة.",
            attackDamage = 28,
            healAmountPerTurn = 40,
            specialEffect = "درع كريستالي مضاعف في اللحظات الحرجة",
            costGold = 4500,
            costGems = 80
        ),
        Pet(
            id = 7,
            name = "شادو الفهد الطيفي الليلي",
            englishName = "Shadow Spectral Panther",
            description = "مفترس سريع يوجه ضربات حرجة مباغتة تمزق دفاعات الأعداء وتزيد فرص الضربة القاضية.",
            attackDamage = 60,
            healAmountPerTurn = 0,
            specialEffect = "زيادة نسبة الضربة الحرجة (Critical Hit) بنسبة 20%",
            costGold = 6000,
            costGems = 120
        ),
        Pet(
            id = 8,
            name = "أوريون التنين الأسطوري الأعظم",
            englishName = "Orion Galactic Dragon Lord",
            description = "أعظم الكائنات المروّضة في الكون؛ يطلق زئيراً كونياً يزلزل ساحة المعركة ويهاجم بضرر مرعب.",
            attackDamage = 85,
            healAmountPerTurn = 50,
            specialEffect = "هجوم كوني ساحق وترميم هائل للصحة",
            costGold = 10000,
            costGems = 250
        )
    )

    fun getPetById(id: Int): Pet {
        return allPets.find { it.id == id } ?: allPets.first()
    }
}

package com.example

import com.example.data.model.SuperpowersCatalog
import com.example.data.model.WeaponsCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun verifyWeaponsCountAndIntegrity() {
        val weapons = WeaponsCatalog.allWeapons
        assertEquals("يجب أن تحتوي اللعبة على 30 سلاحاً غريباً بالضبط", 30, weapons.size)

        val uniqueIds = weapons.map { it.id }.toSet()
        assertEquals(30, uniqueIds.size)

        weapons.forEach { weapon ->
            assertTrue("ضرر السلاح يجب أن يكون أكبر من الصفر", weapon.damage > 0)
            assertTrue("سرعة السلاح يجب أن تكون أكبر من الصفر", weapon.speed > 0f)
            assertTrue("اسم السلاح لا يجب أن يكون فارغاً", weapon.name.isNotBlank())
            assertTrue("وصف السلاح لا يجب أن يكون فارغاً", weapon.description.isNotBlank())
        }
    }

    @Test
    fun verifySuperpowersCountAndIntegrity() {
        val powers = SuperpowersCatalog.allSuperpowers
        assertEquals("يجب أن تحتوي اللعبة على 30 قوة خارقة بالضبط", 30, powers.size)

        val uniqueIds = powers.map { it.id }.toSet()
        assertEquals(30, uniqueIds.size)

        powers.forEach { power ->
            assertTrue("ضرر القوة الخارقة يجب أن يكون أكبر من الصفر", power.damage > 0)
            assertTrue("تكلفة الطاقة يجب أن تكون بين 1 و 100", power.energyCost in 1..100)
            assertTrue("اسم القوة لا يجب أن يكون فارغاً", power.name.isNotBlank())
            assertTrue("وصف القوة لا يجب أن يكون فارغاً", power.description.isNotBlank())
        }
    }
}

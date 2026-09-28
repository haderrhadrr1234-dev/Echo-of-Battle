package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Weapon
import com.example.data.model.WeaponsCatalog
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun WeaponSelectionScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val equippedWeaponId = currentUser?.equippedWeaponId ?: 1
    val unlockedWeaponIds = (currentUser?.unlockedWeaponIds ?: "1").split(",")
    val userGold = currentUser?.gold ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D121B))
    ) {
        // شريط العنوان العلوي
        Surface(
            color = Color(0xFF182232),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.semantics { contentDescription = "العودة إلى الشاشة الرئيسية" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ترسانة الأسلحة (30 سلاح)",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFB300).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
                ) {
                    Text(
                        text = "💰 $userGold",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // قائمة الـ 30 سلاح
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(WeaponsCatalog.allWeapons, key = { it.id }) { weapon ->
                val isEquipped = weapon.id == equippedWeaponId
                val isUnlocked = unlockedWeaponIds.contains(weapon.id.toString())

                WeaponItemCard(
                    weapon = weapon,
                    isEquipped = isEquipped,
                    isUnlocked = isUnlocked,
                    userGold = userGold,
                    onPreviewSound = { viewModel.previewWeapon(weapon) },
                    onEquipOrBuy = { viewModel.equipWeapon(weapon) }
                )
            }
        }
    }
}

@Composable
private fun WeaponItemCard(
    weapon: Weapon,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    userGold: Int,
    onPreviewSound: () -> Unit,
    onEquipOrBuy: () -> Unit
) {
    val borderColor = when {
        isEquipped -> Color(0xFF00E676)
        isUnlocked -> Color(0xFF42A5F5)
        else -> Color(0xFF37474F)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) Color(0xFF152A20) else Color(0xFF161E2C)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(if (isEquipped) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weapon_item_${weapon.id}")
            .semantics {
                contentDescription = "سلاح رقم ${weapon.id}: ${weapon.name}. ${weapon.description}. الضرر: ${weapon.damage}. السرعة: ${weapon.speed}. الحالة: " +
                        if (isEquipped) "مجهز حالياً" else if (isUnlocked) "مملوك، جاهز للتجهيز" else "مغلق، يتطلب ${weapon.costGold} ذهب للشراء."
            }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${weapon.id}. ${weapon.name}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEquipped) Color(0xFFB9F6CA) else Color.White
                        )
                        if (isEquipped) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "مجهز",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = weapon.englishName,
                        fontSize = 12.sp,
                        color = Color(0xFF78909C)
                    )
                }

                // زر تشغيل المؤثر الصوتي للسلاح والوصف
                Button(
                    onClick = onPreviewSound,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.semantics { contentDescription = "استماع للمؤثر الصوتي لوصف ${weapon.name}" }
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "صوت السلاح", fontSize = 12.sp, color = Color(0xFFFFD54F))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = weapon.description,
                fontSize = 13.sp,
                color = Color(0xFFCFD8DC),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFF5252).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "💥 الضرر: ${weapon.damage}",
                            color = Color(0xFFFF8A80),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF448AFF).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "⚡ السرعة: ${weapon.speed}x",
                            color = Color(0xFF82B1FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // زر التجهيز أو الشراء
                if (isEquipped) {
                    Button(
                        onClick = {},
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(0xFF1B5E20),
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("مجهز حالياً", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (isUnlocked) {
                    Button(
                        onClick = onEquipOrBuy,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.semantics { contentDescription = "تجهيز ${weapon.name} في المعركة" }
                    ) {
                        Text("تجهيز", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    val canAfford = userGold >= weapon.costGold
                    Button(
                        onClick = onEquipOrBuy,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF8F00),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.semantics {
                            contentDescription = "شراء وتجهيز ${weapon.name} مقابل ${weapon.costGold} ذهب"
                        }
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "شراء ${weapon.costGold} 💰",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

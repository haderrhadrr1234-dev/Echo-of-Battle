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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.Superpower
import com.example.data.model.SuperpowersCatalog
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun SuperpowerSelectionScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val equippedPowerId = currentUser?.equippedSuperpowerId ?: 1
    val unlockedPowerIds = (currentUser?.unlockedSuperpowerIds ?: "1").split(",")
    val userGold = currentUser?.gold ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E18))
    ) {
        // شريط العنوان العلوي
        Surface(
            color = Color(0xFF201A33),
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
                        modifier = Modifier.semantics { contentDescription = "الرجوع إلى الشاشة الرئيسية" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "خزينة القوى الخارقة (30 قوة)",
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

        // قائمة الـ 30 قوة خارقة
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(SuperpowersCatalog.allSuperpowers, key = { it.id }) { power ->
                val isEquipped = power.id == equippedPowerId
                val isUnlocked = unlockedPowerIds.contains(power.id.toString())

                SuperpowerItemCard(
                    power = power,
                    isEquipped = isEquipped,
                    isUnlocked = isUnlocked,
                    userGold = userGold,
                    onPreviewSound = { viewModel.previewSuperpower(power) },
                    onEquipOrBuy = { viewModel.equipSuperpower(power) }
                )
            }
        }
    }
}

@Composable
private fun SuperpowerItemCard(
    power: Superpower,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    userGold: Int,
    onPreviewSound: () -> Unit,
    onEquipOrBuy: () -> Unit
) {
    val borderColor = when {
        isEquipped -> Color(0xFF00E5FF)
        isUnlocked -> Color(0xFFBA68C8)
        else -> Color(0xFF3E2D54)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) Color(0xFF162534) else Color(0xFF1E172E)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(if (isEquipped) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("superpower_item_${power.id}")
            .semantics {
                contentDescription = "قوة خارقة رقم ${power.id}: ${power.name}. ${power.description}. الضرر: ${power.damage}. استهلاك الطاقة: ${power.energyCost} بالمئة. الحالة: " +
                        if (isEquipped) "مجهزة حالياً" else if (isUnlocked) "مملوكة، جاهزة للتجهيز" else "مغلقة، تتطلب ${power.costGold} ذهب للشراء."
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
                            text = "${power.id}. ${power.name}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEquipped) Color(0xFF80DEEA) else Color.White
                        )
                        if (isEquipped) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "مجهزة",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = power.englishName,
                        fontSize = 12.sp,
                        color = Color(0xFFB39DDB)
                    )
                }

                // زر استماع للمؤثر الصوتي والوصف
                Button(
                    onClick = onPreviewSound,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF311B92)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.semantics { contentDescription = "استماع للمؤثر الصوتي الخاص بـ ${power.name}" }
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF80DEEA), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "صوت القوة", fontSize = 12.sp, color = Color(0xFF80DEEA))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = power.description,
                fontSize = 13.sp,
                color = Color(0xFFE1BEE7),
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
                            text = "💥 الضرر: ${power.damage}",
                            color = Color(0xFFFF8A80),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "⚡ الطاقة: ${power.energyCost}%",
                            color = Color(0xFF80DEEA),
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
                            disabledContainerColor = Color(0xFF006064),
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("مجهزة حالياً", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (isUnlocked) {
                    Button(
                        onClick = onEquipOrBuy,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.semantics { contentDescription = "تجهيز ${power.name}" }
                    ) {
                        Text("تجهيز", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    val canAfford = userGold >= power.costGold
                    Button(
                        onClick = onEquipOrBuy,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF8F00),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.semantics {
                            contentDescription = "شراء وتجهيز ${power.name} مقابل ${power.costGold} ذهب"
                        }
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "شراء ${power.costGold} 💰",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

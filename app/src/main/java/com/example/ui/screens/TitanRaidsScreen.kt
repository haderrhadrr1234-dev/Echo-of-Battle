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
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsKabaddi
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
import com.example.data.model.TitanBoss
import com.example.data.model.TitansCatalog
import com.example.data.model.WeaponsCatalog
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun TitanRaidsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val user = currentUser
    val highestDefeated = user?.highestRaidDefeated ?: 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C1017))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // شريط العنوان
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("raids_back_button")
                            .semantics { contentDescription = "العودة إلى الشاشة الرئيسية" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "غارات الزعماء الأسطوريين",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFD50000).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252))
                ) {
                    Text(
                        text = "💀 نمط الجبابرة",
                        color = Color(0xFFFF8A80),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(TitansCatalog.allTitans) { titan ->
                    val isDefeated = highestDefeated >= titan.id
                    val isAvailable = titan.id == 1 || highestDefeated >= (titan.id - 1)

                    TitanItemCard(
                        titan = titan,
                        isDefeated = isDefeated,
                        isAvailable = isAvailable,
                        onStartRaid = { viewModel.startTitanRaid(titan) }
                    )
                }
            }
        }
    }
}

@Composable
fun TitanItemCard(
    titan: TitanBoss,
    isDefeated: Boolean,
    isAvailable: Boolean,
    onStartRaid: () -> Unit
) {
    val rewardWeapon = titan.rewardWeaponId?.let { WeaponsCatalog.getWeaponById(it) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDefeated -> Color(0xFF1E281F)
                isAvailable -> Color(0xFF261517)
                else -> Color(0xFF14161E)
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            when {
                isDefeated -> Color(0xFF4CAF50)
                isAvailable -> Color(0xFFFF5252)
                else -> Color(0xFF37474F)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("titan_card_${titan.id}")
            .semantics {
                val statusText = if (isDefeated) "تم سحقه سابقاً" else if (isAvailable) "جاهز للقتال" else "مقفل، اهزم الزعيم السابق أولاً"
                contentDescription = "${titan.name}. اللقب: ${titan.title}. الرتبة: ${titan.difficultyRank}. نقاط الصحة: ${titan.maxHp}. الهجوم الخاص: ${titan.specialAttackName}. المكافأة: ${titan.rewardGold} ذهبة و ${titan.rewardGems} جوهرة. الحالة: $statusText. ${titan.description}"
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = titan.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDefeated) Color(0xFFA5D6A7) else Color.White
                        )
                        if (isDefeated) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = titan.title,
                        fontSize = 13.sp,
                        color = Color(0xFFFFAB91),
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (titan.difficultyRank) {
                        "GODLY" -> Color(0xFFFFD700)
                        "MYTHIC" -> Color(0xFFBA68C8)
                        "SSS" -> Color(0xFFFF5252)
                        else -> Color(0xFFFF9800)
                    }.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "رتبة: ${titan.difficultyRank}",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titan.description,
                fontSize = 13.sp,
                color = Color(0xFFCFD8DC),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // إحصائيات الزعيم
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "❤️ صحة الزعيم: ${titan.maxHp}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A80)
                )
                Text(
                    text = "⚡ هجوم خاص: ${titan.specialAttackDamage}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // مكافأة الإسقاط
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF263238),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎁 الجائزة: +${titan.rewardGold} 💰  +${titan.rewardGems} 💎",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81D4FA)
                    )
                    if (rewardWeapon != null) {
                        Text(
                            text = "🗡️ سلاح أسطوري",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // زر التحدي أو القفل
            if (isAvailable) {
                Button(
                    onClick = onStartRaid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDefeated) Color(0xFF388E3C) else Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_raid_button_${titan.id}")
                        .semantics {
                            contentDescription = "بدء غارة قتال الزعيم ${titan.name}"
                        }
                ) {
                    Icon(Icons.Default.SportsKabaddi, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDefeated) "إعادة قتال الزعيم (غارة)" else "⚔️ بدء الغارة الأسطورية الآن",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF212121),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF757575), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مقفل - اهزم الزعيم السابق أولاً لفتح هذه الغارة",
                            color = Color(0xFF9E9E9E),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

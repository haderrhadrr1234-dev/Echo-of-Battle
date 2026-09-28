package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WeaponsCatalog
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ForgeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val user = currentUser
    val weapon = WeaponsCatalog.getWeaponById(user?.equippedWeaponId ?: 1)
    val forgeLevel = user?.weaponUpgradeLevel ?: 0
    val isMaxLevel = forgeLevel >= 10

    val nextCostGold = (forgeLevel + 1) * 600
    val nextCostGems = (forgeLevel + 1) * 8
    val canAfford = (user?.gold ?: 0) >= nextCostGold && (user?.gems ?: 0) >= nextCostGems && !isMaxLevel

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C1017))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // شريط العنوان العلوي
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("forge_back_button")
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
                        text = "ورشة الحدادة والترقية",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFB300).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
                    ) {
                        Text(
                            text = "💰 ${user?.gold ?: 0}",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                    ) {
                        Text(
                            text = "💎 ${user?.gems ?: 0}",
                            color = Color(0xFF80D8FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // بطاقة السندان وسلاح اللاعب
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2230)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF9800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "السلاح المجهز على السندان: ${weapon.name}. مستوى الترقية الحالي: زائد $forgeLevel من 10. مضاعف الضرر: زائد ${forgeLevel * 10}%."
                    }
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "🗡️ ${weapon.name}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE082),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = weapon.englishName,
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE65100).copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                    ) {
                        Text(
                            text = if (isMaxLevel) "⭐ الحد الأقصى (+10) ⭐" else "مستوى الترقية الحالي: +$forgeLevel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFB74D),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (forgeLevel / 10f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Color(0xFFFF9800),
                        trackColor = Color(0xFF37474F)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "الضرر الأساسي", fontSize = 12.sp, color = Color(0xFF90A4AE))
                            Text(text = "${weapon.damage}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "زيادة الحدادة", fontSize = 12.sp, color = Color(0xFF90A4AE))
                            Text(text = "+${forgeLevel * 10}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "الضرر الفعلي الآن", fontSize = 12.sp, color = Color(0xFF90A4AE))
                            val actual = (weapon.damage * (1.0f + (forgeLevel * 0.10f))).toInt()
                            Text(text = "$actual", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A65))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // تفاصيل الترقية القادمة وزر التطوير
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141924)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "تفاصيل الترقية التالية (+${forgeLevel + 1}):",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• زيادة الضرر الإضافي بنسبة 10% لجميع أسلحتك في المعارك.\n" +
                                "• صوت سندان حدادة مخصص واهتزازات تفاعلية قوية.\n" +
                                "• زيادة فرصة إصابة الأعداء بضربات نقدية مدمرة (Critical Hits).",
                        fontSize = 13.sp,
                        color = Color(0xFFCFD8DC),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isMaxLevel) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تكلفة الترقية:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB0BEC5)
                            )
                            Row {
                                Text(
                                    text = "$nextCostGold 💰",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$nextCostGems 💎",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF80D8FF)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.upgradeWeaponForge() },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE65100),
                                disabledContainerColor = Color(0xFF37474F)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("upgrade_forge_button")
                                .semantics {
                                    contentDescription = "ترقية في الحدادة إلى زائد ${forgeLevel + 1} مقابل $nextCostGold ذهبة و $nextCostGems جوهرة"
                                }
                        ) {
                            Icon(Icons.Default.Upgrade, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "طرق وترقية السلاح الآن (+${forgeLevel + 1})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.25f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎉 تهانينا! لقد بلغت ذروة الحدادة الأسطورية (+10)!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

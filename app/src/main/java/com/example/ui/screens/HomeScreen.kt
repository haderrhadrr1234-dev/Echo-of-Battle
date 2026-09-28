package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.SuperpowersCatalog
import com.example.data.model.WeaponsCatalog
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val cloudStatus by viewModel.cloudStatus.collectAsStateWithLifecycle()
    val isVoiceEnabled by viewModel.narratorEngine.isVoiceEnabled.collectAsStateWithLifecycle()
    val speechRate by viewModel.narratorEngine.speechRate.collectAsStateWithLifecycle()

    var showVoiceSettings by remember { mutableStateOf(false) }

    val user = currentUser
    val equippedWeapon = WeaponsCatalog.getWeaponById(user?.equippedWeaponId ?: 1)
    val equippedPower = SuperpowersCatalog.getSuperpowerById(user?.equippedSuperpowerId ?: 1)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C1017))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // شريط حالة قاعدة بيانات جوجل فايربيز والمؤثرات الصوتية الاحترافية
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF2E1C0A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics {
                        contentDescription = "حالة اللعبة: متصل بقاعدة بيانات جوجل فايربيز (Firebase Firestore). سرعة الاستجابة ${cloudStatus.pingLatencyMs} مللي ثانية. مكتبة الصوت: أصوات ألعاب ستوديو 2026 الاحترافية للأندرويد."
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🔥 قاعدة بيانات جوجل فايربيز (Firestore)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFE082)
                        )
                    }

                    Text(
                        text = "${cloudStatus.pingLatencyMs}ms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFB74D)
                    )
                }
            }

            // شريط التحديثات التلقائية المباشرة والسلسة
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF141F33),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF28406E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics {
                        contentDescription = "التحديثات التلقائية: الإصدار الحالي v${viewModel.updateManager.currentVersionName}."
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.SystemUpdate,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "التحديث التلقائي للعبة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBBDEFB)
                            )
                            Text(
                                text = "الإصدار الحالي: v${viewModel.updateManager.currentVersionName} • تحديث مباشر بنقرة واحدة",
                                fontSize = 10.sp,
                                color = Color(0xFF90CAF9)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.checkForAppUpdates(manual = true) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        modifier = Modifier
                            .height(34.dp)
                            .semantics { contentDescription = "فحص تحديثات اللعبة الآن" }
                            .testTag("check_update_button")
                    ) {
                        Text("فحص التحديثات", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // بطاقة بيانات اللاعب العلوية
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF192233)),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_profile_card")
                    .semantics {
                        contentDescription = "ملف اللاعب: ${user?.username ?: "محارب"}. المستوى: ${user?.level ?: 1}. الرصيد: ${user?.gold ?: 0} ذهبة. الانتصارات: ${user?.wins ?: 0}."
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "⚔️ ${user?.username ?: "المحارب"}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "المستوى ${user?.level ?: 1} | خبرة: ${user?.xp ?: 0}",
                                fontSize = 14.sp,
                                color = Color(0xFF90CAF9)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFB300).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💰 ${user?.gold ?: 0}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = "🏆 الانتصارات: ${user?.wins ?: 0}",
                            color = Color(0xFF81C784),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "💀 الهزائم: ${user?.losses ?: 0}",
                            color = Color(0xFFE57373),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // بطاقة العتاد المجهز حالياً
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C2B)),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "العتاد المجهز: السلاح: ${equippedWeapon.name}. القوة الخارقة: ${equippedPower.name}."
                    }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "العتاد المجهز للمعركة:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0BEC5)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🗡️ ${equippedWeapon.name}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC80)
                            )
                            Text(
                                text = "ضرر السلاح: ${equippedWeapon.damage} | سرعة: ${equippedWeapon.speed}",
                                fontSize = 12.sp,
                                color = Color(0xFFB0BEC5)
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.previewWeapon(equippedWeapon)
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .semantics { contentDescription = "استمع لصوت سلاح ${equippedWeapon.name}" }
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFFFFB74D))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚡ ${equippedPower.name}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF80DEEA)
                            )
                            Text(
                                text = "ضرر القوة: ${equippedPower.damage} | طاقة: ${equippedPower.energyCost}%",
                                fontSize = 12.sp,
                                color = Color(0xFFB0BEC5)
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.previewSuperpower(equippedPower)
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .semantics { contentDescription = "استمع لصوت قوة ${equippedPower.name}" }
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF80DEEA))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // زر بدء المعركة الكبير (Accessible & Eye-catching)
            Button(
                onClick = { viewModel.navigateTo(Screen.BATTLE) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE65100),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .testTag("start_battle_button")
                    .semantics { contentDescription = "بدء معركة قتالية صوتية جديدة ضد خصم عشوائي" }
            ) {
                Icon(Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "بدء المعركة الصوتية",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // قائمة الأسلحة (30 سلاحاً)
            Button(
                onClick = { viewModel.navigateTo(Screen.WEAPONS) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF283593),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("weapons_menu_button")
                    .semantics { contentDescription = "ترسانة الأسلحة، تحتوي على ثلاثين سلاحاً غريباً ومذهلاً" }
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ترسانة الأسلحة (30 سلاح)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "استمع لأصوات الأسلحة وجهز سلاحك",
                        fontSize = 12.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // قائمة القوى الخارقة (30 قوة)
            Button(
                onClick = { viewModel.navigateTo(Screen.SUPERPOWERS) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A148C),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("superpowers_menu_button")
                    .semantics { contentDescription = "خزينة القوى الخارقة، تحتوي على ثلاثين قوة خارقة وغريبة" }
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "خزينة القوى الخارقة (30 قوة)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "استمع لأصوات القوى الساحقة وجهز قدرتك",
                        fontSize = 12.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // قائمة المتصدرين أونلاين (Global Cloud Leaderboard)
            Button(
                onClick = { viewModel.navigateTo(Screen.LEADERBOARD) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00695C),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("leaderboard_menu_button")
                    .semantics { contentDescription = "قائمة المتصدرين العالمية أونلاين، استمع لترتيب الأبطال حول العالم" }
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD54F))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "🏆 قائمة المتصدرين أونلاين",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ترتيب الأبطال ومنافسي فايربيز مباشرة 🔥",
                        fontSize = 12.sp,
                        color = Color(0xFFB2DFDB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // سجل المعارك
            OutlinedButton(
                onClick = { viewModel.navigateTo(Screen.HISTORY) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF80CBC4)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("history_menu_button")
                    .semantics { contentDescription = "عرض سجل المعارك السابقة والانتصارات" }
            ) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "سجل المعارك السابقة", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // بطاقة إعدادات الناطق الصوتي للمكفوفين
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151D2A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المساعد الصوتي المدمج: ${if (isVoiceEnabled) "مفعل" else "معطل"}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Button(
                            onClick = { viewModel.narratorEngine.toggleVoice() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isVoiceEnabled) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        ) {
                            Text(if (isVoiceEnabled) "إيقاف الصوت" else "تشغيل الصوت")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "سرعة النطق: ${(speechRate * 100).toInt()}%",
                        color = Color(0xFFB0BEC5),
                        fontSize = 13.sp
                    )
                    Slider(
                        value = speechRate,
                        onValueChange = { viewModel.narratorEngine.setSpeechRate(it) },
                        valueRange = 0.75f..1.75f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFD54F),
                            activeTrackColor = Color(0xFFFFD54F)
                        ),
                        modifier = Modifier.semantics {
                            contentDescription = "تعديل سرعة نطق المساعد الصوتي"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // زر تسجيل الخروج
            OutlinedButton(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8A80)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button")
                    .semantics { contentDescription = "تسجيل الخروج من الحساب" }
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل الخروج", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

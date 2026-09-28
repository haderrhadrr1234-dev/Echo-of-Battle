package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import com.example.ui.viewmodel.BattleState
import com.example.ui.viewmodel.BattleTurn
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

/**
 * شاشة القتال بنظام الأدوار التكتيكي (Turn-Based Battle Screen)
 *
 * في دورك: تستخدم سلاحك الخاص الذي اخترته، وقوتك الخارقة التي اخترتها، أو الصد والدفاع بالدرع.
 * في دور الوحش: يستخدم الوحش سلاحه الخاص وقوته الخارقة ضدك.
 */
@Composable
fun BattleScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val battleState by viewModel.battleState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val equippedWeapon = WeaponsCatalog.getWeaponById(currentUser?.equippedWeaponId ?: 1)
    val equippedPower = SuperpowersCatalog.getSuperpowerById(currentUser?.equippedSuperpowerId ?: 1)

    val monsterWeapon = WeaponsCatalog.getWeaponById(battleState.opponentWeaponId)
    val monsterPower = SuperpowersCatalog.getSuperpowerById(battleState.opponentSuperpowerId)

    val isMyTurn = battleState.currentTurn == BattleTurn.PLAYER_TURN && !battleState.isActionInProgress && !battleState.isBattleOver

    // وميض حركي لتنبيه اللاعب عندما يحين دور الوحش أو دوره
    val infiniteTransition = rememberInfiniteTransition(label = "turn_pulse")
    val turnScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // 1. شريط إشعار الدور الحالي بنظام الأدوار (Turn Indicator)
            // ==========================================
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isMyTurn) Color(0xFF0D2818) else Color(0xFF2E1116),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isMyTurn) Color(0xFF00E676) else Color(0xFFFF5252)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(if (!isMyTurn) turnScale else 1.0f)
                        .semantics {
                            contentDescription = if (isMyTurn) {
                                "الجولة رقم ${battleState.turnNumber}: دورك الحالي! اختر الهجوم بسلاحك ${equippedWeapon.name} أو إطلاق قوتك الخارقة أو الصد بالدرع."
                            } else {
                                "الجولة رقم ${battleState.turnNumber}: دور الوحش ${battleState.opponentName} الآن! الوحش يفكر ويجهز هجومه بسلاحه أو قوته الخارقة."
                            }
                        }
                        .testTag("battle_turn_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMyTurn) Icons.Default.SportsMma else Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = if (isMyTurn) Color(0xFF00E676) else Color(0xFFFF5252),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isMyTurn) "⚔️ دورك الآن! (الجولة ${battleState.turnNumber})" else "⏳ دور الوحش (${battleState.opponentName})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isMyTurn) Color(0xFFB9F6CA) else Color(0xFFFF8A80)
                                )
                                Text(
                                    text = if (isMyTurn) "اختر هجوم سلاحك، قوتك الخارقة، أو الدرع" else "الوحش يجهز هجومه بسلاحه أو قوته...",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = if (isMyTurn) Color(0xFF00E676) else Color(0xFFFF5252),
                            modifier = Modifier.size(14.dp)
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ==========================================
                // 2. بطاقة الوحش / الخصم (Monster Card)
                // ==========================================
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1318)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD32F2F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "الوحش: ${battleState.opponentName}. صحته: ${battleState.opponentHp} من ${battleState.opponentMaxHp}. " +
                                    "طاقته الخارقة: ${battleState.opponentEnergy}%. سلاح الوحش: ${monsterWeapon.name}. قوة الوحش: ${monsterPower.name}."
                        }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👹 ${battleState.opponentName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF8A80)
                            )
                            Text(
                                text = "${battleState.opponentHp} / ${battleState.opponentMaxHp} HP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = {
                                (battleState.opponentHp.toFloat() / battleState.opponentMaxHp).coerceIn(0f, 1f)
                            },
                            color = Color(0xFFFF1744),
                            trackColor = Color(0xFF37474F),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // عتاد وسلاح وقوة الوحش
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🗡️ سلاح الوحش: ${monsterWeapon.name}",
                                fontSize = 11.sp,
                                color = Color(0xFFFFCDD2)
                            )
                            Text(
                                text = "⚡ طاقة الوحش: ${battleState.opponentEnergy}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFFAB91)
                            )
                        }
                        Text(
                            text = "💥 قوته الخارقة: ${monsterPower.name} (تكلفة: ${monsterPower.energyCost}%)",
                            fontSize = 11.sp,
                            color = Color(0xFFFFCCBC)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ==========================================
                // 3. بطاقة اللاعب (Player Card)
                // ==========================================
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF102018)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00C853)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "صحتك: ${battleState.playerHp} من ${battleState.playerMaxHp}. " +
                                    "طاقتك الخارقة: ${battleState.playerEnergy}%. سلاحك المختار: ${equippedWeapon.name}. قوتك الخارقة المختارة: ${equippedPower.name}." +
                                    if (battleState.isPlayerDefending) " درع الصد والدفاع مرفوع لتقليص 70% من الضرر." else ""
                        }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🛡️ ${currentUser?.username ?: "المقاتل"} (أنت)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB9F6CA)
                            )
                            Text(
                                text = "${battleState.playerHp} / ${battleState.playerMaxHp} HP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = {
                                (battleState.playerHp.toFloat() / battleState.playerMaxHp).coerceIn(0f, 1f)
                            },
                            color = Color(0xFF00E676),
                            trackColor = Color(0xFF37474F),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // شريط الطاقة الخارقة للاعب
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🗡️ سلاحك: ${equippedWeapon.name}",
                                fontSize = 11.sp,
                                color = Color(0xFFC8E6C9)
                            )
                            Text(
                                text = "⚡ طاقتك: ${battleState.playerEnergy}% (المطلوب: ${equippedPower.energyCost}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF80DEEA)
                            )
                        }

                        Text(
                            text = "💥 قوتك الخارقة: ${equippedPower.name}",
                            fontSize = 11.sp,
                            color = Color(0xFFB2EBF2)
                        )

                        if (battleState.isPlayerDefending) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🛡️ وضعية الصد والدفاع نشطة (-70% ضرر)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64FFDA)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 4. مساحة سجل الأحداث الأخيرة (Battle Turn Log)
            // ==========================================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF141A28),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263248)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    val lastEvent = battleState.battleLog.firstOrNull() ?: "تبدأ معركة الأدوار التكتيكية... اختر حركتك الأولى في دورك!"
                    Text(
                        text = lastEvent,
                        fontSize = 13.sp,
                        color = Color(0xFFECEFF1),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ==========================================
            // 5. أزرار التحكم القتالية بنظام الأدوار (Turn Action Buttons)
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // الصف الأول: هجوم بالسلاح الخاص + صد ودفاع بالدرع
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. زر الهجوم بالسلاح المختار
                    Button(
                        onClick = { viewModel.onPlayerAttack() },
                        enabled = isMyTurn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD84315),
                            disabledContainerColor = Color(0xFF3E2723).copy(alpha = 0.4f),
                            contentColor = Color.White,
                            disabledContentColor = Color(0xFF8D6E63)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                            .testTag("battle_attack_button")
                            .semantics {
                                contentDescription = if (isMyTurn) {
                                    "زر دورك: الهجوم بسلاحك المختار ${equippedWeapon.name}. اضغط لتسديد ضربة السلاح في دورك."
                                } else {
                                    "زر الهجوم بالسلاح معطل حالياً لأن الدور للوحش."
                                }
                            }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SportsMma, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("الهجوم بسلاحي", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text(
                                text = equippedWeapon.name,
                                fontSize = 11.sp,
                                color = Color(0xFFFFCCBC),
                                maxLines = 1
                            )
                        }
                    }

                    // 2. زر الدفاع والصد بالدرع
                    Button(
                        onClick = { viewModel.onPlayerDefend() },
                        enabled = isMyTurn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00838F),
                            disabledContainerColor = Color(0xFF004D40).copy(alpha = 0.4f),
                            contentColor = Color.White,
                            disabledContentColor = Color(0xFF80CBC4)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                            .testTag("battle_defend_button")
                            .semantics {
                                contentDescription = if (isMyTurn) {
                                    "زر دورك: صد ودفاع بالدرع. يقلص ضرر ضربة الوحش القادمة بنسبة 70 بالمئة ويشحن 20 بالمئة طاقة."
                                } else {
                                    "زر الصد معطل حالياً لأن الدور للوحش."
                                }
                            }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("صد ودفاع بالدرع", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text(
                                text = "-70% ضرر + 20 طاقة 🛡️",
                                fontSize = 11.sp,
                                color = Color(0xFFB2EBF2)
                            )
                        }
                    }
                }

                // الصف الثاني: القوة الخارقة المختارة + استماع لحالة المعركة
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 3. زر القوة الخارقة المختارة
                    val canUsePower = battleState.playerEnergy >= equippedPower.energyCost
                    Button(
                        onClick = { viewModel.onPlayerSuperpower() },
                        enabled = isMyTurn && canUsePower,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A1B9A),
                            disabledContainerColor = Color(0xFF311B92).copy(alpha = 0.35f),
                            contentColor = Color.White,
                            disabledContentColor = Color(0xFF78909C)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                            .testTag("battle_superpower_button")
                            .semantics {
                                contentDescription = if (!isMyTurn) {
                                    "زر القوة الخارقة معطل حالياً لأن الدور للوحش."
                                } else if (canUsePower) {
                                    "زر دورك: إطلاق قوتك الخارقة المختارة ${equippedPower.name}. جاهزة للإطلاق وتستهلك ${equippedPower.energyCost} بالمئة."
                                } else {
                                    "زر القوة الخارقة: طاقتك الحالية ${battleState.playerEnergy} بالمئة وتحتاج إلى ${equippedPower.energyCost} بالمئة."
                                }
                            }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("قوتي الخارقة", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text(
                                text = if (canUsePower) "⚡ ${equippedPower.name}" else "طاقة غير كافية (${battleState.playerEnergy}%)",
                                fontSize = 11.sp,
                                color = if (canUsePower) Color(0xFFE1BEE7) else Color(0xFF90A4AE),
                                maxLines = 1
                            )
                        }
                    }

                    // 4. زر استماع لحالة المعركة بنظام الأدوار للمكفوفين
                    Button(
                        onClick = { viewModel.speakBattleStatus() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                            .testTag("battle_status_audio_button")
                            .semantics {
                                contentDescription = "استماع لحالة المعركة الكاملة ومن صاحب الدور الحالي بالصوت"
                            }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Hearing, contentDescription = null, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حالة المعركة", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text(
                                text = "استماع بالصوت 🎧",
                                fontSize = 11.sp,
                                color = Color(0xFFC8E6C9)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 6. نافذة نهاية المعركة المنبثقة (Victory or Defeat)
        // ==========================================
        if (battleState.isBattleOver) {
            AlertDialog(
                onDismissRequest = {},
                containerColor = if (battleState.isVictory) Color(0xFF1B2A1E) else Color(0xFF2B1619),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (battleState.isVictory) Icons.Default.EmojiEvents else Icons.Default.Dangerous,
                            contentDescription = null,
                            tint = if (battleState.isVictory) Color(0xFFFFD54F) else Color(0xFFFF5252),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (battleState.isVictory) "🏆 نصر أسطوري ساحق!" else "💀 سقطت في المعركة",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (battleState.isVictory) Color(0xFFFFD54F) else Color(0xFFFF5252)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = if (battleState.isVictory)
                                "لقد تغلبت بجدارة على ${battleState.opponentName} في معركة الأدوار وسحقت أسلحته وقواه الخارقة!"
                            else
                                "هزمك ${battleState.opponentName} باستخدام سلاحه وقوته الخارقة. تدرب جيداً واستخدم الدرع في دورك القادم!",
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    text = "💰 +${battleState.goldEarned} ذهب",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "⭐ +${battleState.xpEarned} خبرة",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF81D4FA),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.startNewBattle() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.testTag("battle_replay_button")
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("معركة أدوار جديدة", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                        modifier = Modifier.testTag("battle_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("الرئيسية", color = Color.White)
                    }
                }
            )
        }
    }
}

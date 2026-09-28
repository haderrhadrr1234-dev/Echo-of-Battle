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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val leaderboard by viewModel.onlineLeaderboard.collectAsStateWithLifecycle()
    val cloudStatus by viewModel.cloudStatus.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🏆 المتصدرون أونلاين 🏆",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "قاعدة بيانات جوجل فايربيز الحية (${cloudStatus.pingLatencyMs}ms) 🔥",
                            fontSize = 12.sp,
                            color = Color(0xFFFFB74D)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .testTag("leaderboard_back_button")
                            .semantics { contentDescription = "الرجوع إلى القائمة الرئيسية" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF131A26)
                )
            )
        },
        containerColor = Color(0xFF0A0E17),
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // شريط إشعار السحابة أونلاين
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1B2838),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFFFFB74D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ترتيب الأبطال يتم تحديثه ومزامنته مباشرة عبر قاعدة بيانات جوجل فايربيز (Firebase) 🔥",
                        fontSize = 13.sp,
                        color = Color(0xFFFFE082)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(leaderboard) { player ->
                    val rankColor = when (player.rank) {
                        1 -> Color(0xFFFFD700) // ذهبي
                        2 -> Color(0xFFC0C0C0) // فضي
                        3 -> Color(0xFFCD7F32) // برونزي
                        else -> Color(0xFF90A4AE)
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (player.isCurrentPlayer) Color(0xFF1E3A5F) else Color(0xFF161F2E)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "المركز ${player.rank}: ${player.username}. المستوى: ${player.level}. الانتصارات: ${player.wins}."
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // دائرة الترتيب
                            Surface(
                                shape = CircleShape,
                                color = rankColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(2.dp, rankColor),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${player.rank}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = rankColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = player.username,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (player.isCurrentPlayer) Color(0xFFFFD54F) else Color.White
                                    )
                                    if (player.isCurrentPlayer) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(أنت)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD54F)
                                        )
                                    }
                                }

                                Text(
                                    text = "المستوى ${player.level} • ${player.onlineStatus}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF81C784)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "🏆 ${player.wins} فوز",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB300)
                                )
                                Text(
                                    text = "نسبة الفوز: ${player.winRatePercent}%",
                                    fontSize = 12.sp,
                                    color = Color(0xFF90CAF9)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

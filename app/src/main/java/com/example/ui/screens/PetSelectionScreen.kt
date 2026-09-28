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
import androidx.compose.material.icons.filled.Pets
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
import com.example.data.model.Pet
import com.example.data.model.PetsCatalog
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun PetSelectionScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val user = currentUser
    val equippedPetId = user?.equippedPetId ?: 1
    val unlockedPetIds = user?.unlockedPetIds?.split(",")?.filter { it.isNotBlank() } ?: listOf("1")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C1017))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                            .testTag("pets_back_button")
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
                        text = "ملاذ المرافقين (8 مرافقين)",
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(PetsCatalog.allPets) { pet ->
                    val isEquipped = pet.id == equippedPetId
                    val isUnlocked = unlockedPetIds.contains(pet.id.toString())

                    PetItemCard(
                        pet = pet,
                        isEquipped = isEquipped,
                        isUnlocked = isUnlocked,
                        userGold = user?.gold ?: 0,
                        userGems = user?.gems ?: 0,
                        onPreview = { viewModel.previewPet(pet) },
                        onEquipOrBuy = { viewModel.equipPet(pet) }
                    )
                }
            }
        }
    }
}

@Composable
fun PetItemCard(
    pet: Pet,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    userGold: Int,
    userGems: Int,
    onPreview: () -> Unit,
    onEquipOrBuy: () -> Unit
) {
    val canAfford = userGold >= pet.costGold && userGems >= pet.costGems

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isEquipped -> Color(0xFF281C38)
                isUnlocked -> Color(0xFF1B1D2A)
                else -> Color(0xFF141720)
            }
        ),
        border = if (isEquipped) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFCE93D8)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pet_card_${pet.id}")
            .semantics {
                val stateText = if (isEquipped) "مجهز حالياً يقاتل معك" else if (isUnlocked) "مروّض ومملوك" else "مغلق، يتطلب ${pet.costGold} ذهبة و ${pet.costGems} جوهرة"
                contentDescription = "${pet.name}. هجوم: ${pet.attackDamage}. شفاء لكل دور: ${pet.healAmountPerTurn}. التأثير: ${pet.specialEffect}. الحالة: $stateText. ${pet.description}"
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
                            text = pet.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEquipped) Color(0xFFE1BEE7) else Color.White
                        )
                        if (isEquipped) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFFCE93D8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = pet.englishName,
                        fontSize = 12.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }

                IconButton(
                    onClick = onPreview,
                    modifier = Modifier
                        .size(44.dp)
                        .semantics { contentDescription = "استمع لصوت مرافق ${pet.name}" }
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFFBA68C8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pet.description,
                fontSize = 13.sp,
                color = Color(0xFFCFD8DC),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF4A148C).copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "✨ الميزة الخاصة: ${pet.specialEffect}",
                    fontSize = 12.sp,
                    color = Color(0xFFE1BEE7),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "⚔️ ضرر: +${pet.attackDamage}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFAB91)
                    )
                    Text(
                        text = "💚 شفاء: +${pet.healAmountPerTurn}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5D6A7)
                    )
                }

                if (isEquipped) {
                    Text(
                        text = "المرافق النشط",
                        color = Color(0xFFCE93D8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isUnlocked) {
                    Button(
                        onClick = onEquipOrBuy,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .testTag("equip_pet_button_${pet.id}")
                            .semantics { contentDescription = "تجهيز ${pet.name}" }
                    ) {
                        Text("تجهيز", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onEquipOrBuy,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE65100),
                            disabledContainerColor = Color(0xFF37474F)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .testTag("buy_pet_button_${pet.id}")
                            .semantics { contentDescription = "ترويض ${pet.name} مقابل ${pet.costGold} ذهبة و ${pet.costGems} جوهرة" }
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        val gemsPart = if (pet.costGems > 0) " + ${pet.costGems}💎" else ""
                        Text("ترويض (${pet.costGold}💰$gemsPart)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.GameViewModel

@Composable
fun AuthScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val showAccountChooser by viewModel.showAccountChooser.collectAsStateWithLifecycle()
    val availableAccounts by viewModel.availableAccounts.collectAsStateWithLifecycle()

    // نافذة اختيار الحساب السحابي (Account Chooser Dialog)
    if (showAccountChooser) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.dismissAccountChooser() },
            containerColor = Color(0xFF162030),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF4285F4))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "اختر حسابك للدخول",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "قام خادم Google Firebase بجلب قائمة الحسابات المتاحة. اختر حسابك الحقيقي لبدء اللعبة ومزامنة الذهب والعتاد:",
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    availableAccounts.forEach { account ->
                        Card(
                            onClick = { viewModel.selectAccount(account) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF222F46)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .semantics {
                                    contentDescription = "حساب ${account.provider}: ${account.realUsername}. البريد: ${account.email}. اضغط للاختيار."
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF4285F4).copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF64B5F6))
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = account.realUsername,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = account.email,
                                        fontSize = 12.sp,
                                        color = Color(0xFF90CAF9)
                                    )
                                    Text(
                                        text = "${account.provider} • المستوى ${account.level}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF81C784)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissAccountChooser() }
                ) {
                    Text("إلغاء", color = Color(0xFFFFCC80))
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // الشعار والعنوان
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638)),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚔️ صدى المعركة ⚔️",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics {
                            contentDescription = "صدى المعركة، لعبة القتال الصوتية للمكفوفين"
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "لعبة قتالية صوتية مصممة خصيصاً للمكفوفين وضعاف البصر مع مؤثرات صوتية ثلاثية الأبعاد وناطق باللغة العربية",
                        fontSize = 14.sp,
                        color = Color(0xFFB0BEC5),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE65100).copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥 قاعدة بيانات جوجل فايربيز السحابية | Firebase Cloud DB",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC80)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // تبويب تسجيل الدخول أو حساب جديد
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF18202F),
                contentColor = Color(0xFFFFD54F),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        viewModel.audioEngine.playTabSwitch()
                        viewModel.narratorEngine.speak("تم اختيار تبويب تسجيل الدخول")
                    },
                    text = {
                        Text(
                            text = "تسجيل الدخول",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) Color(0xFFFFD54F) else Color.White
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        viewModel.audioEngine.playTabSwitch()
                        viewModel.narratorEngine.speak("تم اختيار تبويب إنشاء حساب جديد")
                    },
                    text = {
                        Text(
                            text = "حساب جديد",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) Color(0xFFFFD54F) else Color.White
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // الحقول
            if (selectedTab == 1) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("اسم المقاتل / المحارب", color = Color(0xFF90CAF9)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF90CAF9)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD54F),
                        unfocusedBorderColor = Color(0xFF455A64)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input")
                        .semantics { contentDescription = "أدخل اسم المقاتل" }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("البريد الإلكتروني", color = Color(0xFF90CAF9)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF90CAF9)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD54F),
                    unfocusedBorderColor = Color(0xFF455A64)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input")
                    .semantics { contentDescription = "أدخل البريد الإلكتروني" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("كلمة المرور", color = Color(0xFF90CAF9)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF90CAF9)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD54F),
                    unfocusedBorderColor = Color(0xFF455A64)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input")
                    .semantics { contentDescription = "أدخل كلمة المرور" }
            )

            // رسالة الخطأ
            authError?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = err,
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // زر التنفيذ الأساسي (تسجيل الدخول / إنشاء حساب)
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        viewModel.loginWithEmail(email, password)
                    } else {
                        viewModel.registerWithEmail(username, email, password)
                    }
                },
                enabled = !authLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("auth_submit_button")
                    .semantics {
                        contentDescription = if (selectedTab == 0) "زر تسجيل الدخول بالبريد" else "زر إنشاء الحساب الجديد"
                    }
            ) {
                if (authLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (selectedTab == 0) "تسجيل الدخول" else "إنشاء الحساب وبدء القتال",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // خيار جلب واختيار حساب من السحابة وجوجل
            Button(
                onClick = {
                    viewModel.openAccountChooser()
                },
                enabled = !authLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("google_login_button")
                    .semantics { contentDescription = "جلب واختيار حسابك الحقيقي للدخول فوراً" }
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "اختيار حساب Google / حساب سحابي",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // خيار الدخول كضيف فوري
            OutlinedButton(
                onClick = {
                    viewModel.loginAsGuest()
                },
                enabled = !authLoading,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF80CBC4)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("guest_login_button")
                    .semantics { contentDescription = "الدخول السريع كضيف للمقاتلين" }
            ) {
                Text(
                    text = "دخول سريع كضيف (بدون تسجيل)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // زر المساعد الصوتي
            IconButton(
                onClick = {
                    viewModel.narratorEngine.speak("مرحباً بك في شاشة الدخول إلى صدى المعركة. يمكنك كتابة البريد وكلمة المرور، أو الضغط على زر تسجيل الدخول عبر جوجل، أو دخول سريع كضيف.")
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("help_audio_button")
                    .semantics { contentDescription = "استمع للتعليمات الصوتية لشاشة تسجيل الدخول" }
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "استماع للتعليمات الصوتية", tint = Color(0xFFFFD54F))
            }
        }
    }
}

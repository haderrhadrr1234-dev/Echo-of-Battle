package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.update.DownloadState
import com.example.update.UpdateInfo

/**
 * نافذة منبثقة تفاعلية لتحديث التطبيق التلقائي (In-App Auto Update Dialog)
 * متوافقة تماماً مع معايير الوصول للمكفوفين وتصميم Material 3 العصري.
 */
@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    downloadState: DownloadState,
    onConfirmUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDownloading = downloadState is DownloadState.Downloading

    AlertDialog(
        onDismissRequest = {
            if (!isDownloading) onDismiss()
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = when (downloadState) {
                        is DownloadState.Error -> Icons.Default.ErrorOutline
                        is DownloadState.Downloading -> Icons.Default.Download
                        else -> Icons.Default.SystemUpdate
                    },
                    contentDescription = null,
                    tint = when (downloadState) {
                        is DownloadState.Error -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = if (updateInfo.isUpdateAvailable) "يتوفر تحديث جديد!" else "أحدث إصدار متوفر (${updateInfo.latestVersionName})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "عنوان: إصدار ${updateInfo.latestVersionName}"
                    }
                    .testTag("update_dialog_title")
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (updateInfo.isUpdateAvailable) {
                        "يتوفر الإصدار الأحدث ${updateInfo.latestVersionName} في المستودع. هل تريد التحديث والتثبيت الآن؟"
                    } else {
                        "أحدث ملف APK للعبة متاح وجاهز للتثبيت المباشر (${updateInfo.latestVersionName}). هل تريد تنزيله وتثبيته الآن؟"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "رسالة التحديث للإصدار ${updateInfo.latestVersionName}"
                        }
                        .testTag("update_dialog_message")
                )

                if (updateInfo.latestVersionName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الإصدار: ${updateInfo.latestVersionName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // عرض تفاصيل وحالة التنزيل
                AnimatedVisibility(visible = downloadState is DownloadState.Downloading) {
                    if (downloadState is DownloadState.Downloading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val percent = (downloadState.progress * 100).toInt().coerceIn(0, 100)
                            val downloadedMb = downloadState.downloadedBytes / (1024f * 1024f)
                            val totalMb = downloadState.totalBytes / (1024f * 1024f)

                            LinearProgressIndicator(
                                progress = { downloadState.progress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .testTag("update_progress_bar"),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (totalMb > 0) {
                                    "جاري التنزيل: $percent% (%.1f / %.1f ميجابايت)".format(downloadedMb, totalMb)
                                } else {
                                    "جاري تنزيل ملف التحديث... $percent%"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.semantics {
                                    contentDescription = "نسبة اكتمال تنزيل التحديث $percent بالمائة"
                                }
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = downloadState is DownloadState.Downloaded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "اكتمل التنزيل بنجاح! يتم الآن فتح نافذة التثبيت...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                AnimatedVisibility(visible = downloadState is DownloadState.Error) {
                    if (downloadState is DownloadState.Error) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = downloadState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmUpdate,
                enabled = !isDownloading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .semantics {
                        contentDescription = "تنزيل وتثبيت ملف APK للعبة"
                    }
                    .testTag("update_confirm_button")
            ) {
                Text(
                    text = when {
                        downloadState is DownloadState.Error -> "إعادة المحاولة"
                        updateInfo.isUpdateAvailable -> "تحديث وتثبيت الآن"
                        else -> "تنزيل وتثبيت الآن"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isDownloading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .semantics {
                        contentDescription = "إلغاء وإغلاق نافذة التحديث"
                    }
                    .testTag("update_cancel_button")
            ) {
                Text(
                    text = "إلغاء",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

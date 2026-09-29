package com.example

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.UpdateDialog
import com.example.ui.screens.ArmorSelectionScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.ForgeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.PetSelectionScreen
import com.example.ui.screens.SuperpowerSelectionScreen
import com.example.ui.screens.TitanRaidsScreen
import com.example.ui.screens.WeaponSelectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen
import java.io.File

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    // مُسجّل طلب إذن تثبيت التطبيقات غير المعروفة المرتبط بنشاط اللعبة مباشرة
    private val installPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.updateInstallPermissionStatus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (packageManager.canRequestPackageInstalls()) {
                val apk = viewModel.updateManager.lastDownloadedApk
                if (apk != null && apk.exists()) {
                    launchPackageInstaller(apk)
                }
            }
        }
    }

    fun launchInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:$packageName")
                }
                installPermissionLauncher.launch(intent)
            } catch (_: Exception) {
                try {
                    val fallback = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    installPermissionLauncher.launch(fallback)
                } catch (_: Exception) {}
            }
        }
    }

    fun launchPackageInstaller(file: File) {
        try {
            file.setReadable(true, false)
            file.setWritable(true, false)
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            }

            val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resolveInfos) {
                grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(intent)
        } catch (_: Exception) {
            try {
                val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
                val altIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                    data = uri
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                }
                startActivity(altIntent)
            } catch (_: Exception) {
                try {
                    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
                    val chooser = Intent.createChooser(Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/vnd.android.package-archive")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }, "تثبيت تحديث صدى المعركة")
                    startActivity(chooser)
                } catch (_: Exception) {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

                        val screenModifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)

                        when (currentScreen) {
                            Screen.AUTH -> AuthScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.HOME -> HomeScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.WEAPONS -> WeaponSelectionScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.SUPERPOWERS -> SuperpowerSelectionScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.ARMOR -> ArmorSelectionScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.PETS -> PetSelectionScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.FORGE -> ForgeScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.TITAN_RAIDS -> TitanRaidsScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.BATTLE -> BattleScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.HISTORY -> HistoryScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.LEADERBOARD -> LeaderboardScreen(viewModel = viewModel, modifier = screenModifier)
                        }

                        // نافذة التحديث التلقائي المنبثقة من مستودع GitHub
                        val showUpdateDialog by viewModel.showUpdateDialog.collectAsStateWithLifecycle()
                        val updateInfo by viewModel.updateInfo.collectAsStateWithLifecycle()
                        val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
                        val hasInstallPermission by viewModel.hasInstallPermission.collectAsStateWithLifecycle()

                        if (showUpdateDialog && updateInfo != null) {
                            UpdateDialog(
                                updateInfo = updateInfo!!,
                                downloadState = downloadState,
                                hasInstallPermission = hasInstallPermission,
                                onConfirmUpdate = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
                                        launchInstallPermission()
                                    } else {
                                        viewModel.startAppUpdate { apkFile ->
                                            launchPackageInstaller(apkFile)
                                        }
                                    }
                                },
                                onRequestPermission = { launchInstallPermission() },
                                onRetryInstall = {
                                    val apk = viewModel.updateManager.lastDownloadedApk
                                    if (apk != null && apk.exists()) {
                                        launchPackageInstaller(apk)
                                    } else {
                                        viewModel.startAppUpdate { apkFile ->
                                            launchPackageInstaller(apkFile)
                                        }
                                    }
                                },
                                onDismiss = { viewModel.dismissUpdateDialog() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateInstallPermissionStatus()
        // استئناف تثبيت التحديث تلقائياً فور منح الإذن من الإعدادات والعودة إلى اللعبة
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.canRequestPackageInstalls()) {
            val apk = viewModel.updateManager.lastDownloadedApk
            if (apk != null && apk.exists()) {
                launchPackageInstaller(apk)
            }
        }
    }
}

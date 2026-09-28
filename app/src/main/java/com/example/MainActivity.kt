package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.UpdateDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.SuperpowerSelectionScreen
import com.example.ui.screens.WeaponSelectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

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
                            Screen.BATTLE -> BattleScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.HISTORY -> HistoryScreen(viewModel = viewModel, modifier = screenModifier)
                            Screen.LEADERBOARD -> LeaderboardScreen(viewModel = viewModel, modifier = screenModifier)
                        }

                        // نافذة التحديث التلقائي المنبثقة من مستودع GitHub
                        val showUpdateDialog by viewModel.showUpdateDialog.collectAsStateWithLifecycle()
                        val updateInfo by viewModel.updateInfo.collectAsStateWithLifecycle()
                        val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()

                        if (showUpdateDialog && updateInfo != null) {
                            UpdateDialog(
                                updateInfo = updateInfo!!,
                                downloadState = downloadState,
                                onConfirmUpdate = { viewModel.startAppUpdate() },
                                onDismiss = { viewModel.dismissUpdateDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}

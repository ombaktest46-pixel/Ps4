package com.example.ui.screens

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameItem
import com.example.ui.components.AddGameDialog
import com.example.ui.components.ConfigureEmulatorDialog
import com.example.ui.components.GameLaunchSimulatorDialog
import com.example.ui.components.HandheldSettingsDialog
import com.example.ui.components.Ps4ControllerBottomBar
import com.example.ui.components.Ps4DynamicBackground
import com.example.ui.components.Ps4GameDetailMenu
import com.example.ui.components.Ps4GameRow
import com.example.ui.components.Ps4StatusBar
import com.example.ui.components.UserProfileDialog
import com.example.viewmodel.Ps4LauncherViewModel

@Composable
fun Ps4LauncherScreen(
    viewModel: Ps4LauncherViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val games by viewModel.games.collectAsState()
    val selectedIndex by viewModel.selectedIndex.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val activeGameSession by viewModel.activeGameSession.collectAsState()
    val configuringGame by viewModel.configuringGame.collectAsState()
    val isAddGameOpen by viewModel.isAddGameOpen.collectAsState()
    val isProfileOpen by viewModel.isProfileOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()

    val selectedGame: GameItem? = games.getOrNull(selectedIndex)
    val focusRequester = remember { FocusRequester() }

    // Auto-focus so hardware Gamepad events immediately navigate
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    // Hardware Back / B button handler
    BackHandler(enabled = true) {
        when {
            isSettingsOpen -> viewModel.closeSettings()
            activeGameSession != null -> viewModel.exitGameSession()
            isAddGameOpen -> viewModel.closeAddGame()
            configuringGame != null -> viewModel.closeEmulatorConfig()
            isProfileOpen -> viewModel.closeProfile()
            selectedIndex != 0 -> viewModel.selectGame(0)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                viewModel.handleGamepadKeyEvent(
                    keyCode = keyEvent.nativeKeyEvent.keyCode,
                    isActionDown = keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                )
            }
    ) {
        // 1. Dynamic Animated Handheld Console Canvas with Glowing Ribbons & Particles
        Ps4DynamicBackground(
            theme = settings.themeColor,
            waveStyle = settings.waveStyle,
            customBackgroundUri = settings.customBackgroundUri,
            wallpaperPreset = settings.customWallpaperPreset,
            backdropIconUri = if (settings.useGameBackdrop) selectedGame?.customIconUri else null,
            backdropIconRes = if (settings.useGameBackdrop && selectedGame?.isAddButton != true) selectedGame?.iconRes ?: 0 else 0
        )

        // 2. Main Console Layout: Clean Minimalist Handheld Home (No bottom buttons, clean background)
        Scaffold(
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top Custom Handheld Status Bar
                Ps4StatusBar(
                    theme = settings.themeColor,
                    isBgmPlaying = settings.isBgmPlaying,
                    onToggleBgm = { viewModel.toggleBgm() },
                    onAvatarClick = { viewModel.openProfile() },
                    onOpenSettings = { viewModel.openSettings() },
                    modifier = Modifier.statusBarsPadding()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Kotak-Kotak Game Carousel
                Ps4GameRow(
                    games = games,
                    selectedIndex = selectedIndex,
                    onSelectGame = { viewModel.selectGame(it) },
                    onExecuteAction = { viewModel.executeTileAction(it) },
                    theme = settings.themeColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kotak Game Bawahnya Tulisan Nama Gamenya Udh
                selectedGame?.let { game ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = game.title,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Dibawah game kosong cuma background doang
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // ==========================================
        // DEDICATED DIALOGS & OVERLAYS
        // ==========================================

        // 1. Separate Dedicated Handheld Settings & Audio Dialog
        if (isSettingsOpen) {
            HandheldSettingsDialog(
                settings = settings,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onPickCustomAudioUri = { viewModel.setCustomAudioFromUri(it) },
                onRemoveCustomAudio = { viewModel.removeCustomAudio() },
                onPlaySfxTick = { viewModel.playSfxTick() },
                onPlaySfxSelect = { viewModel.playSfxSelect() },
                onDismiss = { viewModel.closeSettings() }
            )
        }

        // 2. Active Game Session
        activeGameSession?.let { game ->
            GameLaunchSimulatorDialog(
                game = game,
                theme = settings.themeColor,
                onExitGame = { viewModel.exitGameSession() },
                onPlaySfxTick = { viewModel.playSfxTick() }
            )
        }

        // 3. Configure Emulator Dialog
        configuringGame?.let { game ->
            ConfigureEmulatorDialog(
                game = game,
                theme = settings.themeColor,
                onDismiss = { viewModel.closeEmulatorConfig() },
                onSaveConfig = { item, config ->
                    viewModel.saveEmulatorConfig(item, config)
                }
            )
        }

        // 4. Add Game / Import Winlator Shortcut Dialog
        if (isAddGameOpen) {
            AddGameDialog(
                theme = settings.themeColor,
                onDismiss = { viewModel.closeAddGame() },
                onGameAdded = { viewModel.addGame(it) }
            )
        }

        // 5. User Profile Dialog
        if (isProfileOpen) {
            UserProfileDialog(
                theme = settings.themeColor,
                onDismiss = { viewModel.closeProfile() }
            )
        }
    }
}

package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.audio.Ps4AudioEngine
import com.example.data.AppDatabase
import com.example.data.entity.Game
import com.example.data.repository.GameRepository
import com.example.model.BackgroundWaveStyle
import com.example.model.BgmPreset
import com.example.model.EmulatorConfig
import com.example.model.GameItem
import com.example.model.LauncherSettings
import com.example.model.ThemeColorPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class Ps4LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val audioEngine = Ps4AudioEngine(application)
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val gameRepository = GameRepository(database.gameDao())

    // Initial games set synchronously so UI has zero flicker/delay on start
    private val _games = MutableStateFlow<List<GameItem>>(initialDefaultGames)
    val games: StateFlow<List<GameItem>> = _games.asStateFlow()

    // Default selected index: starts at first game (PES 2017)
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    private val _settings = MutableStateFlow(loadSettingsFromPrefs(application))
    val settings: StateFlow<LauncherSettings> = _settings.asStateFlow()

    private val _activeGameSession = MutableStateFlow<GameItem?>(null)
    val activeGameSession: StateFlow<GameItem?> = _activeGameSession.asStateFlow()

    private val _configuringGame = MutableStateFlow<GameItem?>(null)
    val configuringGame: StateFlow<GameItem?> = _configuringGame.asStateFlow()

    private val _isAddGameOpen = MutableStateFlow(false)
    val isAddGameOpen: StateFlow<Boolean> = _isAddGameOpen.asStateFlow()

    private val _isProfileOpen = MutableStateFlow(false)
    val isProfileOpen: StateFlow<Boolean> = _isProfileOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    init {
        // Sync loaded settings to audio engine
        val current = _settings.value
        audioEngine.updateSettings(
            preset = current.bgmPreset,
            customUri = current.customAudioUri,
            playing = current.isBgmPlaying,
            volume = current.bgmVolume,
            sfxOn = current.sfxEnabled,
            sfxVol = current.sfxVolume
        )

        // Initialize and listen to Room Database games
        viewModelScope.launch {
            seedDatabaseIfEmpty()
            observeGameLibrary()
        }
    }

    private suspend fun seedDatabaseIfEmpty(force: Boolean = false) {
                try {
            // Bersihkan data lama yang memuat kata "Winlator" atau "apk" agar bersih seperti aplikasi baru
            val existing = gameRepository.getAllGamesList()
            if (existing.any { 
                (it.platform?.contains("Winlator", ignoreCase = true) ?: false) || 
                (it.packageName?.contains("winlator", ignoreCase = true) ?: false) 
            }) {
                gameRepository.deleteAllGames()
            }
        } catch (e: Exception) {
            Log.e("Ps4LauncherViewModel", "Seed cleanup error: ${e.message}")
                }
    
    }

    private suspend fun observeGameLibrary() {
        gameRepository.allGames.collectLatest { entities ->
            val items = entities.map { it.toGameItem() }.toMutableList()
            // Selalu sediakan kotak Tambah Game bersih (tanpa winlator/app/apk)
            items.add(cleanAddGameTile)
            _games.value = items

            if (_selectedIndex.value >= items.size) {
                _selectedIndex.value = (items.size - 1).coerceAtLeast(0)
            }
        }
    }

    private var lastJoystickMotionTime = 0L

    fun selectGame(index: Int) {
        if (index in 0 until _games.value.size && index != _selectedIndex.value) {
            _selectedIndex.value = index
            val item = _games.value.getOrNull(index)
            // Bunyikan audio navigasi khas PS dan sesuaikan backsound tersendiri untuk game ini
            audioEngine.onGameSelected(index, isAddButton = item?.isAddButton == true)
        }
    }

    fun selectNextGame() {
        val total = _games.value.size
        if (total > 0) {
            val next = (_selectedIndex.value + 1).coerceAtMost(total - 1)
            selectGame(next)
        }
    }

    fun selectPreviousGame() {
        val total = _games.value.size
        if (total > 0) {
            val prev = (_selectedIndex.value - 1).coerceAtLeast(0)
            selectGame(prev)
        }
    }

    fun skipGames(delta: Int) {
        val total = _games.value.size
        if (total > 0) {
            val target = (_selectedIndex.value + delta).coerceIn(0, total - 1)
            selectGame(target)
        }
    }

    fun openSettings() {
        audioEngine.playSelectChime()
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        audioEngine.playBackChime()
        _isSettingsOpen.value = false
    }

    fun toggleSettings() {
        if (_isSettingsOpen.value) closeSettings() else openSettings()
    }

    fun handleGenericMotionEvent(event: android.view.MotionEvent): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastJoystickMotionTime < 220) return false

        val x = event.getAxisValue(android.view.MotionEvent.AXIS_X).takeIf { it != 0f }
            ?: event.getAxisValue(android.view.MotionEvent.AXIS_HAT_X)

        if (x > 0.5f) {
            lastJoystickMotionTime = now
            selectNextGame()
            return true
        } else if (x < -0.5f) {
            lastJoystickMotionTime = now
            selectPreviousGame()
            return true
        }
        return false
    }

    fun handleGamepadKeyEvent(keyCode: Int, isActionDown: Boolean): Boolean {
        if (!isActionDown) return false

        when (keyCode) {
            android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                selectNextGame()
                return true
            }
            android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                selectPreviousGame()
                return true
            }
            android.view.KeyEvent.KEYCODE_BUTTON_R1 -> {
                skipGames(3)
                return true
            }
            android.view.KeyEvent.KEYCODE_BUTTON_L1 -> {
                skipGames(-3)
                return true
            }
            android.view.KeyEvent.KEYCODE_BUTTON_R2 -> {
                skipGames(10)
                return true
            }
            android.view.KeyEvent.KEYCODE_BUTTON_L2 -> {
                skipGames(-10)
                return true
            }
            android.view.KeyEvent.KEYCODE_BUTTON_A,
            android.view.KeyEvent.KEYCODE_DPAD_CENTER,
            android.view.KeyEvent.KEYCODE_ENTER -> {
                if (_isSettingsOpen.value) {
                    return false
                }
                val current = _games.value.getOrNull(_selectedIndex.value)
                if (current != null) {
                    executeTileAction(current)
                    return true
                }
            }
            android.view.KeyEvent.KEYCODE_BUTTON_B,
            android.view.KeyEvent.KEYCODE_BACK,
            android.view.KeyEvent.KEYCODE_ESCAPE -> {
                if (_isSettingsOpen.value) {
                    closeSettings()
                    return true
                }
                if (_activeGameSession.value != null) {
                    exitGameSession()
                    return true
                }
                if (_isAddGameOpen.value) {
                    closeAddGame()
                    return true
                }
                if (_configuringGame.value != null) {
                    closeEmulatorConfig()
                    return true
                }
                if (_selectedIndex.value != 0) {
                    selectGame(0)
                    return true
                }
            }
            android.view.KeyEvent.KEYCODE_BUTTON_X -> {
                val current = _games.value.getOrNull(_selectedIndex.value)
                if (current != null && !current.isAddButton) {
                    openEmulatorConfig(current)
                    return true
                }
            }
            android.view.KeyEvent.KEYCODE_BUTTON_Y -> {
                openAddGame()
                return true
            }
            android.view.KeyEvent.KEYCODE_BUTTON_START,
            android.view.KeyEvent.KEYCODE_MENU -> {
                toggleSettings()
                return true
            }
            android.view.KeyEvent.KEYCODE_BUTTON_SELECT -> {
                openProfile()
                return true
            }
        }
        return false
    }

    fun executeTileAction(item: GameItem) {
        audioEngine.playSelectChime()
        when {
            item.isAddButton -> {
                _isAddGameOpen.value = true
            }
            item.isPersonalization -> {
                openSettings()
            }
            else -> {
                launchGame(item)
            }
        }
    }

    fun launchGame(item: GameItem) {
        audioEngine.playStartGameSweep()
        if (!item.packageName.isNullOrBlank()) {
            try {
                val intent = getApplication<Application>().packageManager.getLaunchIntentForPackage(item.packageName)
                if (intent != null) {
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    getApplication<Application>().startActivity(intent)
                    return
                }
            } catch (_: Exception) {}
        }
        _activeGameSession.value = item
    }

    fun exitGameSession() {
        audioEngine.playBackChime()
        _activeGameSession.value = null
    }

    fun openEmulatorConfig(item: GameItem) {
        audioEngine.playSelectChime()
        _configuringGame.value = item
    }

    fun closeEmulatorConfig() {
        audioEngine.playBackChime()
        _configuringGame.value = null
    }

    fun saveEmulatorConfig(item: GameItem, config: EmulatorConfig) {
        audioEngine.playSelectChime()
        _configuringGame.value = null
    }

    fun openAddGame() {
        audioEngine.playSelectChime()
        _isAddGameOpen.value = true
    }

    fun closeAddGame() {
        audioEngine.playBackChime()
        _isAddGameOpen.value = false
    }

    fun addGame(game: GameItem) {
        audioEngine.playSelectChime()
        viewModelScope.launch {
            val entity = Game.fromGameItem(game)
            gameRepository.insertGame(entity)
        }
        _isAddGameOpen.value = false
    }

    fun updateCustomIcon(gameId: String, customIconUri: String) {
        // Updated icon
    }

    fun deleteGame(gameId: String) {
        audioEngine.playBackChime()
        viewModelScope.launch {
            val idLong = gameId.toLongOrNull() ?: 0L
            gameRepository.deleteGameById(idLong)
        }
    }

    fun clearAllGames() {
        resetToEmptyLibrary()
    }

    fun resetToEmptyLibrary() {
        audioEngine.playBackChime()
        viewModelScope.launch {
            gameRepository.deleteAllGames()
            _selectedIndex.value = 0
            _games.value = listOf(cleanAddGameTile)
        }
    }

    fun restoreDefaultGames() {
        loadSampleHandheldGames()
    }

    fun loadSampleHandheldGames() {
        audioEngine.playSelectChime()
        viewModelScope.launch {
            gameRepository.deleteAllGames()
            val sampleGames = listOf(
                Game(
                    id = 1L,
                    title = "PES 2017",
                    platform = "Console",
                    iconPath = R.drawable.ic_pes2017_cover.toString(),
                    packageName = "com.konami.pes2017"
                ),
                Game(
                    id = 2L,
                    title = "The Witcher 3®: Wild Hunt",
                    platform = "Console",
                    iconPath = R.drawable.ic_witcher_cover.toString(),
                    packageName = "com.cdprojekt.witcher3"
                ),
                Game(
                    id = 3L,
                    title = "God of War® II",
                    platform = "PlayStation",
                    iconPath = R.drawable.ic_godofwar_ps2.toString(),
                    packageName = "com.playstation.gow2"
                ),
                Game(
                    id = 4L,
                    title = "Zelda: Breath of the Wild",
                    platform = "Nintendo",
                    iconPath = R.drawable.ic_zelda_cover.toString(),
                    packageName = "com.nintendo.zelda"
                ),
                Game(
                    id = 5L,
                    title = "GameHub Multi-Nexus",
                    platform = "Handheld",
                    iconPath = R.drawable.ic_gamehub_cover.toString(),
                    packageName = "com.gamehub"
                ),
                Game(
                    id = 6L,
                    title = "Mobile Legends: Bang Bang",
                    platform = "Mobile",
                    iconPath = R.drawable.ic_mobile_legends_cover.toString(),
                    packageName = "com.mobile.legends"
                ),
                Game(
                    id = 7L,
                    title = "Assassin's Creed® IV Black Flag",
                    platform = "Console",
                    iconPath = R.drawable.ic_assassins_cover.toString(),
                    packageName = "com.ubisoft.ac4"
                )
            )
            gameRepository.insertGames(sampleGames)
            _selectedIndex.value = 0
        }
    }

    fun setWallpaperPreset(presetId: String) {
        audioEngine.playSelectChime()
        val updated = _settings.value.copy(
            customWallpaperPreset = presetId,
            customBackgroundUri = null
        )
        updateSettings(updated)
    }

    fun setCustomWallpaperFromUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val app = getApplication<Application>()
                val wallpaperFile = File(app.filesDir, "custom_wallpaper.jpg")
                app.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(wallpaperFile).use { output ->
                        input.copyTo(output)
                    }
                }
                withContext(Dispatchers.Main) {
                    val updated = _settings.value.copy(customBackgroundUri = wallpaperFile.absolutePath)
                    updateSettings(updated)
                }
            } catch (e: Exception) {
                Log.e("Ps4LauncherViewModel", "Failed to save custom wallpaper: ${e.message}")
            }
        }
    }

    fun removeCustomWallpaper() {
        val updated = _settings.value.copy(customBackgroundUri = null)
        updateSettings(updated)
    }

    fun openProfile() {
        audioEngine.playSelectChime()
        _isProfileOpen.value = true
    }

    fun closeProfile() {
        audioEngine.playBackChime()
        _isProfileOpen.value = false
    }

    fun updateSettings(newSettings: LauncherSettings) {
        _settings.value = newSettings
        saveSettingsToPrefs(getApplication(), newSettings)
        audioEngine.updateSettings(
            preset = newSettings.bgmPreset,
            customUri = newSettings.customAudioUri,
            playing = newSettings.isBgmPlaying,
            volume = newSettings.bgmVolume,
            sfxOn = newSettings.sfxEnabled,
            sfxVol = newSettings.sfxVolume
        )
    }

    /**
     * Import user's own audio file into persistent app internal storage
     * so read permissions never expire and audio plays seamlessly.
     */
    fun setCustomAudioFromUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val app = getApplication<Application>()
                val cr = app.contentResolver

                var fileName = "user_audio_custom"
                try {
                    cr.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            fileName = cursor.getString(nameIndex)
                        }
                    }
                } catch (_: Exception) {}

                if (fileName == "user_audio_custom") {
                    fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "Audio_Pribadi.mp3"
                }

                val audioDir = File(app.filesDir, "custom_audio")
                if (!audioDir.exists()) audioDir.mkdirs()
                val targetFile = File(audioDir, "user_bgm_track.mp3")

                cr.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val absolutePath = targetFile.absolutePath
                withContext(Dispatchers.Main) {
                    audioEngine.playSelectChime()
                    val updated = _settings.value.copy(
                        bgmPreset = BgmPreset.CUSTOM_AUDIO,
                        customAudioUri = absolutePath,
                        customAudioTitle = fileName,
                        isBgmPlaying = true
                    )
                    updateSettings(updated)
                }
            } catch (e: Exception) {
                Log.e("Ps4Launcher", "Failed copying custom audio: ${e.message}")
            }
        }
    }

    fun removeCustomAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val app = getApplication<Application>()
                val targetFile = File(app.filesDir, "custom_audio/user_bgm_track.mp3")
                if (targetFile.exists()) targetFile.delete()
            } catch (_: Exception) {}
            withContext(Dispatchers.Main) {
                audioEngine.playBackChime()
                val updated = _settings.value.copy(
                    bgmPreset = BgmPreset.CUSTOM_AUDIO,
                    customAudioUri = null,
                    customAudioTitle = "Pilih Berkas Audio Anda",
                    isBgmPlaying = false
                )
                updateSettings(updated)
            }
        }
    }

    fun setCustomAudio(uri: String, title: String) {
        val current = _settings.value
        updateSettings(
            current.copy(
                bgmPreset = BgmPreset.CUSTOM_AUDIO,
                customAudioUri = uri,
                customAudioTitle = title,
                isBgmPlaying = true
            )
        )
    }

    fun toggleBgm() {
        val current = _settings.value
        val newState = !current.isBgmPlaying
        updateSettings(current.copy(isBgmPlaying = newState))
        audioEngine.playNavTick()
    }

    fun playSfxTick() {
        audioEngine.playNavTick()
    }

    fun playSfxSelect() {
        audioEngine.playSelectChime()
    }

    fun playSfxBack() {
        audioEngine.playBackChime()
    }

    fun selectPersonalizationTile() {
        val pIndex = _games.value.indexOfFirst { it.isPersonalization }
        if (pIndex >= 0) {
            selectGame(pIndex)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }

    companion object {
        fun loadSettingsFromPrefs(context: Context): LauncherSettings {
            val prefs = context.getSharedPreferences("ps4_launcher_settings", Context.MODE_PRIVATE)
            val themeStr = prefs.getString("theme_color", ThemeColorPreset.BLUE_PS4.name) ?: ThemeColorPreset.BLUE_PS4.name
            val theme = try { ThemeColorPreset.valueOf(themeStr) } catch (_: Exception) { ThemeColorPreset.BLUE_PS4 }

            val waveStr = prefs.getString("wave_style", BackgroundWaveStyle.DYNAMIC_PS4.name) ?: BackgroundWaveStyle.DYNAMIC_PS4.name
            val wave = try { BackgroundWaveStyle.valueOf(waveStr) } catch (_: Exception) { BackgroundWaveStyle.DYNAMIC_PS4 }

            val presetStr = prefs.getString("bgm_preset", BgmPreset.PS4_AMBIENT.name) ?: BgmPreset.PS4_AMBIENT.name
            val preset = try { BgmPreset.valueOf(presetStr) } catch (_: Exception) { BgmPreset.PS4_AMBIENT }

            val customUri = prefs.getString("custom_audio_uri", null)
            val customTitle = prefs.getString("custom_audio_title", "Pilih Berkas Audio Anda") ?: "Pilih Berkas Audio Anda"
            val customWallpaper = prefs.getString("custom_wallpaper_uri", null)
            val wallpaperPreset = prefs.getString("wallpaper_preset", "default") ?: "default"
            val useBackdrop = prefs.getBoolean("use_game_backdrop", true)
            val bgmVolume = prefs.getFloat("bgm_volume", 0.70f)
            val sfxVolume = prefs.getFloat("sfx_volume", 0.85f)
            val isPlaying = prefs.getBoolean("bgm_playing", true)
            val sfxEnabled = prefs.getBoolean("sfx_enabled", true)
            val swipeSfx = prefs.getBoolean("swipe_sfx_enabled", true) // default true agar pas pindah game ada audio kek ps

            val guideStr = prefs.getString("button_guide_style", com.example.model.ButtonGuideStyle.NINTENDO_SWITCH.name)
                ?: com.example.model.ButtonGuideStyle.NINTENDO_SWITCH.name
            val guideStyle = try {
                com.example.model.ButtonGuideStyle.valueOf(guideStr)
            } catch (_: Exception) {
                com.example.model.ButtonGuideStyle.NINTENDO_SWITCH
            }

            return LauncherSettings(
                themeColor = theme,
                waveStyle = wave,
                bgmPreset = preset,
                customBackgroundUri = customWallpaper,
                customWallpaperPreset = wallpaperPreset,
                useGameBackdrop = useBackdrop,
                customAudioUri = customUri,
                customAudioTitle = customTitle,
                bgmVolume = bgmVolume,
                sfxVolume = sfxVolume,
                isBgmPlaying = isPlaying,
                sfxEnabled = sfxEnabled,
                swipeSfxEnabled = swipeSfx,
                buttonGuideStyle = guideStyle
            )
        }

        fun saveSettingsToPrefs(context: Context, settings: LauncherSettings) {
            val prefs = context.getSharedPreferences("ps4_launcher_settings", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("theme_color", settings.themeColor.name)
                .putString("wave_style", settings.waveStyle.name)
                .putString("bgm_preset", settings.bgmPreset.name)
                .putString("custom_wallpaper_uri", settings.customBackgroundUri)
                .putString("wallpaper_preset", settings.customWallpaperPreset)
                .putBoolean("use_game_backdrop", settings.useGameBackdrop)
                .putString("custom_audio_uri", settings.customAudioUri)
                .putString("custom_audio_title", settings.customAudioTitle)
                .putFloat("bgm_volume", settings.bgmVolume)
                .putFloat("sfx_volume", settings.sfxVolume)
                .putBoolean("bgm_playing", settings.isBgmPlaying)
                .putBoolean("sfx_enabled", settings.sfxEnabled)
                .putBoolean("swipe_sfx_enabled", settings.swipeSfxEnabled)
                .putString("button_guide_style", settings.buttonGuideStyle.name)
                .apply()
        }

        val cleanAddGameTile = GameItem(
            id = "add_game",
            title = "Tambah Game",
            subtitle = "Koleksi Game Handheld",
            platform = "Koleksi Game",
            iconRes = R.drawable.ic_add_game,
            isAddButton = true,
            genre = "Game",
            size = "+",
            trophiesPercent = 0,
            playTimeHours = 0,
            lastPlayed = "-",
            description = "Pilih untuk menambahkan game baru ke perpustakaan konsol handheld Anda."
        )

        // Bersih seperti aplikasi baru (tanpa winlator / app / apk)
        val initialDefaultGames = listOf(cleanAddGameTile)
    }
}

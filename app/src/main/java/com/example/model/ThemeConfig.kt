package com.example.model

import androidx.compose.ui.graphics.Color

enum class ThemeColorPreset(
    val displayName: String,
    val primaryColor: Color,
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val ribbonColorPrimary: Color,
    val ribbonColorSecondary: Color,
    val glowColor: Color
) {
    BLUE_PS4(
        displayName = "Biru PS4",
        primaryColor = Color(0xFF0058C6),
        backgroundTop = Color(0xFF091F4D),
        backgroundBottom = Color(0xFF010A1C),
        ribbonColorPrimary = Color(0xFF00A2FF),
        ribbonColorSecondary = Color(0xFF4DE2FF),
        glowColor = Color(0xFF00C3FF)
    ),
    CRIMSON(
        displayName = "Merah Crimson",
        primaryColor = Color(0xFFC2185B),
        backgroundTop = Color(0xFF380815),
        backgroundBottom = Color(0xFF100105),
        ribbonColorPrimary = Color(0xFFFF2A55),
        ribbonColorSecondary = Color(0xFFFF7A90),
        glowColor = Color(0xFFFF3366)
    ),
    CYBERPUNK(
        displayName = "Ungu Cyberpunk",
        primaryColor = Color(0xFF8E24AA),
        backgroundTop = Color(0xFF2C0A3E),
        backgroundBottom = Color(0xFF0B0212),
        ribbonColorPrimary = Color(0xFFD500F9),
        ribbonColorSecondary = Color(0xFF00E5FF),
        glowColor = Color(0xFFE040FB)
    ),
    EMERALD(
        displayName = "Hijau Emerald",
        primaryColor = Color(0xFF00897B),
        backgroundTop = Color(0xFF042922),
        backgroundBottom = Color(0xFF000E0B),
        ribbonColorPrimary = Color(0xFF00E676),
        ribbonColorSecondary = Color(0xFF69F0AE),
        glowColor = Color(0xFF1DE9B6)
    ),
    MIDNIGHT(
        displayName = "Midnight Dark",
        primaryColor = Color(0xFF546E7A),
        backgroundTop = Color(0xFF1E2631),
        backgroundBottom = Color(0xFF0A0D12),
        ribbonColorPrimary = Color(0xFFB0BEC5),
        ribbonColorSecondary = Color(0xFFECEFF1),
        glowColor = Color(0xFF90A4AE)
    )
}

enum class BackgroundWaveStyle(val displayName: String, val waveCount: Int, val speedFactor: Float) {
    DYNAMIC_PS4("PS4 Dynamic", waveCount = 5, speedFactor = 1.0f),
    RIBBON_FLOW("Aliran Pita Lembut", waveCount = 7, speedFactor = 1.3f),
    NEBULA_GLOW("Nebula Angkasa", waveCount = 3, speedFactor = 0.6f),
    MINIMAL_STREAM("Garis Minimal", waveCount = 2, speedFactor = 0.8f)
}

enum class BgmPreset(val displayName: String, val subtitle: String) {
    CUSTOM_AUDIO("Audio Musik Kustom Sendiri", "Pilih berkas MP3/WAV/audio Anda"),
    PS4_AMBIENT("PS4 Dynamic Ambient", "Alunan nada hangat & tenang khas konsol"),
    SYNTH_CHILL("Synthwave Neon Lounge", "Melodi retro masa depan yang santai"),
    LOFI_BEATS("Lo-Fi Chill Hop", "Dentang piano hangat dan ritme santai"),
    MUTED("Hening (Mute)", "Hanya efek suara tombol tanpa musik latar")
}

enum class ButtonGuideStyle(val displayName: String, val description: String) {
    NINTENDO_SWITCH("Nintendo Handheld", "Tombol bersih A/B/X/Y gaya Nintendo Switch"),
    MINIMAL_PILL("Minimalis Handheld", "Pill ringkas tanpa tombol penuh"),
    PS_MINIMAL("PlayStation Ringkas", "Ikon konsol PS bersih")
}

enum class CustomWallpaperPreset(val id: String, val displayName: String, val description: String) {
    DYNAMIC_CONSOLE("default", "Animasi Dinamis PS", "Gelombang pita dinamis khas konsol"),
    MIDNIGHT_OLED("midnight", "Midnight Dark OLED", "Latar gelap gulita hemat daya & elegan"),
    DEEP_BLUE("deep_blue", "PlayStation Deep Blue", "Gradasi biru tua ikonik konsol"),
    CYBER_NEON("cyber_neon", "Cyber Neon Horizon", "Nuansa ungu neon retro modern"),
    CRIMSON_NEBULA("crimson_nebula", "Crimson Nebula", "Galaksi kosmik merah membara"),
    CARBON_TITANIUM("carbon", "Carbon Matrix", "Tekstur titanium carbon gelap")
}

data class LauncherSettings(
    val themeColor: ThemeColorPreset = ThemeColorPreset.BLUE_PS4,
    val waveStyle: BackgroundWaveStyle = BackgroundWaveStyle.DYNAMIC_PS4,
    val bgmPreset: BgmPreset = BgmPreset.PS4_AMBIENT,
    val customBackgroundUri: String? = null, // Wallpaper background kustom pengguna dari galeri
    val customWallpaperPreset: String = "default", // Pilihan wallpaper preset konsol
    val useGameBackdrop: Boolean = true, // Latar belakang beradaptasi dengan gambar game terpilih
    val customAudioUri: String? = null,
    val customAudioTitle: String = "Pilih Berkas Audio Anda",
    val bgmVolume: Float = 0.70f,
    val sfxVolume: Float = 0.85f,
    val isBgmPlaying: Boolean = true,
    val sfxEnabled: Boolean = true,
    val swipeSfxEnabled: Boolean = true, // Audio khas navigasi konsol saat pindah game
    val buttonGuideStyle: ButtonGuideStyle = ButtonGuideStyle.NINTENDO_SWITCH,
    val particleDensity: Int = 35,
    val dynamicWaveSpeed: Float = 1.0f
)

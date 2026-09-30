package com.example.model

data class EmulatorConfig(
    val resolution: String = "1920x1080 (1080p)",
    val fpsLimit: Int = 60,
    val graphicsBackend: String = "Vulkan (DXVK 2.3)",
    val cpuAffinity: String = "Semua Core (8-Core)",
    val controllerPreset: String = "DualShock 4 Wireless"
)

data class GameItem(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val platform: String, // "Winlator PC", "Switch Yuzu", "Android Native", "System"
    val iconRes: Int = 0,
    val customIconUri: String? = null,
    val isPersonalization: Boolean = false,
    val isAddButton: Boolean = false,
    val genre: String = "Action / Adventure",
    val size: String = "15 GB",
    val trophiesPercent: Int = 50,
    val playTimeHours: Int = 12,
    val lastPlayed: String = "Hari ini",
    val description: String = "",
    val packageName: String? = null,
    val emulatorConfig: EmulatorConfig = EmulatorConfig()
)

package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BackgroundWaveStyle
import com.example.model.BgmPreset
import com.example.model.LauncherSettings
import com.example.model.ThemeColorPreset

/**
 * Dedicated Handheld Settings Dialog.
 * Separates audio & personalization from the main game carousel, preventing clutter.
 */
@Composable
fun HandheldSettingsDialog(
    settings: LauncherSettings,
    onUpdateSettings: (LauncherSettings) -> Unit,
    onPickCustomAudioUri: (Uri) -> Unit,
    onRemoveCustomAudio: () -> Unit,
    onPickCustomWallpaperUri: ((Uri) -> Unit)? = null,
    onRemoveCustomWallpaper: (() -> Unit)? = null,
    onClearAllGames: (() -> Unit)? = null,
    onRestoreDefaultGames: (() -> Unit)? = null,
    onPlaySfxTick: () -> Unit,
    onPlaySfxSelect: () -> Unit,
    onDismiss: () -> Unit
) {
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickCustomAudioUri(uri)
            onPlaySfxSelect()
        }
    }

    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickCustomWallpaperUri?.invoke(uri)
            onPlaySfxSelect()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.5.dp, settings.themeColor.glowColor, RoundedCornerShape(18.dp))
                .testTag("dialog_handheld_settings"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A101D)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(settings.themeColor.primaryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "Pengaturan Konsol Handheld",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Atur audio kustom, tema tampilan, dan navigasi gamepad",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settings_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                }

                // ==========================================
                // SECTION 1: AUDIO BACKGROUND (PISAH DARI GAME)
                // ==========================================
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = settings.themeColor.glowColor, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Audio Musik Latar (Isi Sendiri)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Play/Pause & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Status Audio:",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = if (settings.bgmPreset == BgmPreset.CUSTOM_AUDIO && !settings.customAudioUri.isNullOrBlank()) {
                                        "Lagu: ${settings.customAudioTitle.take(30)}"
                                    } else if (settings.isBgmPlaying) {
                                        "Preset: ${settings.bgmPreset.displayName}"
                                    } else {
                                        "Musik Hening / Mati"
                                    },
                                    color = settings.themeColor.glowColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = {
                                    onPlaySfxSelect()
                                    onUpdateSettings(settings.copy(isBgmPlaying = !settings.isBgmPlaying))
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(settings.themeColor.primaryColor)
                            ) {
                                Icon(
                                    imageVector = if (settings.isBgmPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // File Picker Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onPlaySfxSelect()
                                    audioPickerLauncher.launch("audio/*")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = settings.themeColor.primaryColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Icon(Icons.Default.FileOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pilih Berkas Musik (.mp3 / .wav)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (!settings.customAudioUri.isNullOrBlank()) {
                                Button(
                                    onClick = {
                                        onRemoveCustomAudio()
                                        onPlaySfxTick()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.25f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("Reset", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        // Volume Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Volume BGM: ${(settings.bgmVolume * 100).toInt()}%", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                        Slider(
                            value = settings.bgmVolume,
                            onValueChange = { onUpdateSettings(settings.copy(bgmVolume = it)) },
                            colors = SliderDefaults.colors(
                                thumbColor = settings.themeColor.glowColor,
                                activeTrackColor = settings.themeColor.primaryColor,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // SFX Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Efek Suara Tombol (SFX Konfirmasi & Batal)", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Bunyi saat menekan tombol A, B, atau menu", color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp)
                            }
                            Switch(
                                checked = settings.sfxEnabled,
                                onCheckedChange = {
                                    onPlaySfxTick()
                                    onUpdateSettings(settings.copy(sfxEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = settings.themeColor.primaryColor
                                )
                            )
                        }

                        // Suara Geser Game (Dipisahkan agar tidak ramai saat gamepad navigasi cepat)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Suara Saat Geser Game (Nav Tick)", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Matikan agar senyap & tidak ramai saat geser game pakai Gamepad", color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp)
                            }
                            Switch(
                                checked = settings.swipeSfxEnabled,
                                onCheckedChange = {
                                    onPlaySfxTick()
                                    onUpdateSettings(settings.copy(swipeSfxEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = settings.themeColor.primaryColor
                                )
                            )
                        }
                    }
                }

                // ==========================================
                // SECTION 2: TEMA & LATAR BELAKANG
                // ==========================================
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = settings.themeColor.glowColor, modifier = Modifier.size(18.dp))
                            Text("Warna Aksen Konsol Handheld", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeColorPreset.entries.forEach { preset ->
                                val isSelected = settings.themeColor == preset
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(preset.primaryColor)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            onPlaySfxSelect()
                                            onUpdateSettings(settings.copy(themeColor = preset))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset.displayName.split(" ").first(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gaya Animasi Latar Belakang:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BackgroundWaveStyle.entries.forEach { wave ->
                                val isSelected = settings.waveStyle == wave
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) settings.themeColor.primaryColor.copy(alpha = 0.5f)
                                            else Color.White.copy(alpha = 0.06f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) settings.themeColor.glowColor else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            onPlaySfxTick()
                                            onUpdateSettings(settings.copy(waveStyle = wave))
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = wave.displayName.split(" ").first(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pilihan Preset Wallpaper Konsol:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            com.example.model.CustomWallpaperPreset.entries.forEach { wp ->
                                val isSelected = settings.customWallpaperPreset == wp.id && settings.customBackgroundUri.isNullOrBlank()
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) settings.themeColor.primaryColor.copy(alpha = 0.55f)
                                            else Color.White.copy(alpha = 0.08f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) settings.themeColor.glowColor else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            onPlaySfxTick()
                                            onUpdateSettings(
                                                settings.copy(
                                                    customWallpaperPreset = wp.id,
                                                    customBackgroundUri = null
                                                )
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = wp.displayName,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Wallpaper Latar Belakang Gambar Galeri:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    wallpaperPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (!settings.customBackgroundUri.isNullOrBlank()) "Ganti Wallpaper" else "Pilih Wallpaper Gambar",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                            if (!settings.customBackgroundUri.isNullOrBlank()) {
                                IconButton(
                                    onClick = {
                                        onRemoveCustomWallpaper?.invoke()
                                        onPlaySfxTick()
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Hapus Wallpaper", tint = Color(0xFFFF5252))
                                }
                            }
                        }

                        // Toggle dynamic backdrop from selected game
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Latar Mengikuti Game Terpilih", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Latar belakang beradaptasi halus dengan gambar game yang sedang disorot", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                            }
                            Switch(
                                checked = settings.useGameBackdrop,
                                onCheckedChange = {
                                    onPlaySfxTick()
                                    onUpdateSettings(settings.copy(useGameBackdrop = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = settings.themeColor.glowColor)
                            )
                        }
                    }
                }

                // ==========================================
                // SECTION 3: KONTROL GAMEPAD & PANDUAN TOMBOL HANDHELD
                // ==========================================
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Gamepad, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                            Text(
                                text = "Gaya Panduan Tombol Handheld",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Button guide style selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            com.example.model.ButtonGuideStyle.entries.forEach { style ->
                                val isSelected = settings.buttonGuideStyle == style
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) settings.themeColor.primaryColor.copy(alpha = 0.5f)
                                            else Color.White.copy(alpha = 0.06f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) settings.themeColor.glowColor else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            onPlaySfxTick()
                                            onUpdateSettings(settings.copy(buttonGuideStyle = style))
                                        }
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = style.displayName,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Gamepad mappings guide
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Pemetaan Kontroler / Gamepad Handheld:", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("• D-Pad / Analog Stick: Geser & pilih game", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("• Tombol A (South): Pilih / Buka game", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("• Tombol B (East): Kembali / Batal", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("• Tombol X (West): Opsi & Konfigurasi emulator", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("• Tombol Y (North): Tambah Game Baru", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("• Tombol + / Start / Menu: Buka Pengaturan Konsol", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text("• Tombol L1 / R1: Lompat 3 game sekaligus", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        }
                    }
                }

                // ==========================================
                // SECTION 4: MANAJEMEN GAME (KOSONG SEPERTI APK BARU)
                // ==========================================
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Manajemen Perpustakaan Game",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pilih untuk memulai kosong seperti aplikasi baru atau memulihkan game demo bawaan.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onClearAllGames?.invoke()
                                    onPlaySfxSelect()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828).copy(alpha = 0.85f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mulai Kosong (Aplikasi Baru)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onRestoreDefaultGames?.invoke()
                                    onPlaySfxSelect()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5).copy(alpha = 0.85f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Muat Contoh Game", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Selesai Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = settings.themeColor.primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Simpan & Tutup Pengaturan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

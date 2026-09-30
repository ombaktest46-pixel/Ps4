package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundWaveStyle
import com.example.model.BgmPreset
import com.example.model.LauncherSettings
import com.example.model.ThemeColorPreset

@Composable
fun Ps4PersonalizationMenu(
    settings: LauncherSettings,
    onUpdateSettings: (LauncherSettings) -> Unit,
    onPlaySfxTick: () -> Unit,
    onPlaySfxSelect: () -> Unit,
    onPickCustomAudioUri: ((Uri) -> Unit)? = null,
    onRemoveCustomAudio: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expandedSection by remember { mutableStateOf<Int?>(1) } // Default open BGM section so user sees audio customization right away
    var manualUriInput by remember { mutableStateOf(settings.customAudioUri ?: "") }

    // Launcher for user to pick their own audio file (.mp3, .wav, .ogg, .flac, .m4a)
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickCustomAudioUri?.invoke(uri) ?: run {
                val displayName = uri.lastPathSegment?.substringAfterLast('/') ?: "Audio Pilihan Pengguna"
                onUpdateSettings(
                    settings.copy(
                        bgmPreset = BgmPreset.CUSTOM_AUDIO,
                        customAudioUri = uri.toString(),
                        customAudioTitle = displayName,
                        isBgmPlaying = true
                    )
                )
            }
            onPlaySfxSelect()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Judul Menu: Teks besar bergaya PS4
        Column {
            Text(
                text = "Menu Personalisasi: Latar Belakang & Musik Kustom",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Isi audio musik Anda sendiri, atur animasi latar konsol, dan sesuaikan warna tema.",
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 11.sp
            )
        }

        // Pilihan Menu 1: Musik Latar (Isi Audio Sendiri)
        PersonalizationOptionCard(
            icon = Icons.Default.GraphicEq,
            secondaryIcon = Icons.Default.MusicNote,
            title = "Musik Latar (Audio Kustom)",
            valueBadge = if (settings.isBgmPlaying) {
                if (settings.bgmPreset == BgmPreset.CUSTOM_AUDIO) {
                    "[Audio Sendiri: ${settings.customAudioTitle.take(20)}]"
                } else {
                    "[Preset: ${settings.bgmPreset.displayName}]"
                }
            } else {
                "[Hening / Nonaktif]"
            },
            subtitle = "Pilih berkas audio Anda sendiri dari memori HP atau alunan preset konsol",
            isExpanded = expandedSection == 1,
            glowColor = settings.themeColor.glowColor,
            onClick = {
                onPlaySfxSelect()
                expandedSection = if (expandedSection == 1) null else 1
            },
            testTag = "menu_option_bgm"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play / Pause + Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Status Pemutaran Audio:",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (settings.bgmPreset == BgmPreset.CUSTOM_AUDIO && !settings.customAudioUri.isNullOrBlank()) {
                                "File: ${settings.customAudioTitle}"
                            } else {
                                settings.bgmPreset.displayName
                            },
                            color = settings.themeColor.glowColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            onPlaySfxSelect()
                            onUpdateSettings(settings.copy(isBgmPlaying = !settings.isBgmPlaying))
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(settings.themeColor.primaryColor)
                            .testTag("bgm_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (settings.isBgmPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play Pause BGM",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Primary Button: Pilih Berkas Audio Sendiri (.mp3 / .wav / .ogg / .m4a / .flac)
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
                            .height(42.dp)
                            .border(1.dp, settings.themeColor.glowColor, RoundedCornerShape(8.dp))
                            .testTag("pick_audio_file_button")
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!settings.customAudioUri.isNullOrBlank()) "Ganti Berkas Audio Sendiri" else "Pilih Berkas Audio Sendiri (.mp3 / .wav)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!settings.customAudioUri.isNullOrBlank()) {
                        Button(
                            onClick = {
                                onRemoveCustomAudio?.invoke()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(42.dp)
                                .border(1.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        ) {
                            Text("Reset", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }

                if (settings.customAudioUri.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "ℹ️ Audio musik belum dipilih. Anda bebas memasukkan musik favorit Anda dari memori HP kapan saja.",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Preset & Custom choices
                Text(
                    text = "Atau Pilih Preset Musik Konsol:",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BgmPreset.entries.forEach { preset ->
                        val isSelected = settings.bgmPreset == preset
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) settings.themeColor.primaryColor.copy(alpha = 0.40f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) settings.themeColor.glowColor else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onPlaySfxSelect()
                                    if (preset == BgmPreset.CUSTOM_AUDIO && settings.customAudioUri.isNullOrBlank()) {
                                        audioPickerLauncher.launch("audio/*")
                                    } else {
                                        onUpdateSettings(
                                            settings.copy(
                                                bgmPreset = preset,
                                                isBgmPlaying = preset != BgmPreset.MUTED
                                            )
                                        )
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = preset.displayName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = if (preset == BgmPreset.CUSTOM_AUDIO && !settings.customAudioUri.isNullOrBlank()) {
                                        "Memuat: ${settings.customAudioTitle}"
                                    } else {
                                        preset.subtitle
                                    },
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Terpilih",
                                    tint = settings.themeColor.glowColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Volume Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Volume Musik BGM",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${(settings.bgmVolume * 100).toInt()}%",
                            color = settings.themeColor.glowColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = settings.bgmVolume,
                        onValueChange = { newVol ->
                            onUpdateSettings(settings.copy(bgmVolume = newVol))
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = settings.themeColor.glowColor,
                            activeTrackColor = settings.themeColor.primaryColor,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // SFX Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Efek Suara Tombol PS4 (SFX)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Bunyi navigasi tick & lonceng konfirmasi",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = settings.sfxEnabled,
                        onCheckedChange = { checked ->
                            onPlaySfxTick()
                            onUpdateSettings(settings.copy(sfxEnabled = checked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = settings.themeColor.primaryColor
                        )
                    )
                }
            }
        }

        // Pilihan Menu 2: Latar Belakang Menu
        PersonalizationOptionCard(
            icon = Icons.Default.Brush,
            secondaryIcon = null,
            title = "Latar Belakang Menu",
            valueBadge = "[Dinamis: '${settings.waveStyle.displayName}']",
            subtitle = "Pilih pola animasi pita bergelombang & partikel",
            isExpanded = expandedSection == 2,
            glowColor = settings.themeColor.glowColor,
            onClick = {
                onPlaySfxSelect()
                expandedSection = if (expandedSection == 2) null else 2
            },
            testTag = "menu_option_background"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BackgroundWaveStyle.entries.forEach { style ->
                    val isSelected = settings.waveStyle == style
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) settings.themeColor.primaryColor.copy(alpha = 0.40f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) settings.themeColor.glowColor else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onPlaySfxSelect()
                                onUpdateSettings(settings.copy(waveStyle = style))
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = style.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Terpilih",
                                tint = settings.themeColor.glowColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Pilihan Menu 3: Warna Tema
        PersonalizationOptionCard(
            icon = Icons.Default.Palette,
            secondaryIcon = null,
            title = "Warna Tema",
            valueBadge = "[${settings.themeColor.displayName}]",
            subtitle = "Pilih skema warna aura neon dan latar belakang",
            isExpanded = expandedSection == 3,
            glowColor = settings.themeColor.glowColor,
            onClick = {
                onPlaySfxSelect()
                expandedSection = if (expandedSection == 3) null else 3
            },
            testTag = "menu_option_theme_color"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ThemeColorPreset.entries.forEach { themePreset ->
                    val isSelected = settings.themeColor == themePreset
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) themePreset.primaryColor.copy(alpha = 0.35f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) themePreset.glowColor else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onPlaySfxSelect()
                                onUpdateSettings(settings.copy(themeColor = themePreset))
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(themePreset.primaryColor)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                            Text(
                                text = themePreset.displayName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Terpilih",
                                tint = themePreset.glowColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalizationOptionCard(
    icon: ImageVector,
    secondaryIcon: ImageVector?,
    title: String,
    valueBadge: String,
    subtitle: String,
    isExpanded: Boolean,
    glowColor: Color,
    onClick: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isExpanded) 1.5.dp else 1.dp,
                color = if (isExpanded) glowColor else Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C1628).copy(alpha = 0.75f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = glowColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = valueBadge,
                                color = glowColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 10.sp
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Expand",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                content()
            }
        }
    }
}

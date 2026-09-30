package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.model.GameItem
import com.example.model.ThemeColorPreset
import java.util.UUID

@Composable
fun AddGameDialog(
    theme: ThemeColorPreset,
    onDismiss: () -> Unit,
    onGameAdded: (GameItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("Console") }
    var genre by remember { mutableStateOf("Action / RPG") }
    var sizeText by remember { mutableStateOf("18.5 GB") }
    var customIconUri by remember { mutableStateOf("") }
    var selectedIconPreset by remember { mutableIntStateOf(R.drawable.ic_pes2017_cover) }
    var packageName by remember { mutableStateOf("") }

    val platformOptions = listOf(
        "Console",
        "PlayStation",
        "Nintendo",
        "Retro Console",
        "Handheld"
    )

    val presetIcons = listOf(
        Pair("PES 2017", R.drawable.ic_pes2017_cover),
        Pair("The Witcher 3", R.drawable.ic_witcher_cover),
        Pair("God of War II", R.drawable.ic_godofwar_ps2),
        Pair("GameHub", R.drawable.ic_gamehub_cover),
        Pair("Zelda BOTW", R.drawable.ic_zelda_cover),
        Pair("Mobile Legends", R.drawable.ic_mobile_legends_cover),
        Pair("Assassin's Creed", R.drawable.ic_assassins_cover)
    )

    val gameShortcutPresets = listOf(
        Pair("PES 2017", R.drawable.ic_pes2017_cover),
        Pair("Grand Theft Auto V", R.drawable.ic_witcher_cover),
        Pair("Need for Speed", R.drawable.ic_assassins_cover),
        Pair("The Witcher 3", R.drawable.ic_witcher_cover),
        Pair("God of War II", R.drawable.ic_godofwar_ps2)
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            customIconUri = uri.toString()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, theme.glowColor, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0C182B)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tambah Game ke Library",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                }

                // Quick Presets
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Pilihan Cepat Game Handheld:",
                        color = Color(0xFF00E676),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gameShortcutPresets.forEach { (pTitle, pRes) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(theme.primaryColor.copy(alpha = 0.45f))
                                    .border(1.dp, theme.glowColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        title = pTitle
                                        selectedPlatform = "Console"
                                        selectedIconPreset = pRes
                                        customIconUri = ""
                                        genre = if (pTitle.contains("PES")) "Sports • Sepak Bola" else "Action / RPG"
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+ $pTitle",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Judul Game Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Game (mis. PES 2017 / GTA V)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = theme.glowColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = theme.glowColor,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_game_title")
                )

                // Platform Selector
                Text(
                    text = "Pilih Platform / Tipe Sistem:",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    platformOptions.forEach { plat ->
                        val isSelected = selectedPlatform == plat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.08f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) theme.glowColor else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedPlatform = plat
                                    packageName = when {
                                        plat.contains("Winlator") -> "com.winlator"
                                        plat.contains("GameHub") -> "com.gamehub"
                                        plat.contains("Android") -> "com.mobile.legends"
                                        plat.contains("Yuzu") -> "org.yuzu.yuzu_emu"
                                        plat.contains("Aether") || plat.contains("PS2") -> "xyz.aethersx2.android"
                                        plat.contains("PPSSPP") -> "org.ppsspp.ppsspp"
                                        plat.contains("RetroArch") -> "com.retroarch"
                                        else -> packageName
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = plat,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Custom Icon section: Presets or Custom URI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ikon / Cover Art Game:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.glowColor),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Pilih dari Galeri", fontSize = 11.sp)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    presetIcons.forEach { (label, res) ->
                        val isSelected = selectedIconPreset == res && customIconUri.isBlank()
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.4f))
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) theme.glowColor else Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedIconPreset = res
                                    customIconUri = ""
                                }
                        ) {
                            Image(
                                painter = painterResource(id = res),
                                contentDescription = label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }

                    // Preview of custom URI if entered
                    if (customIconUri.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, theme.glowColor, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = customIconUri,
                                contentDescription = "Custom Icon Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }
                }

                // Custom Icon URI Input
                OutlinedTextField(
                    value = customIconUri,
                    onValueChange = { customIconUri = it },
                    label = { Text("URI / URL Ikon Kustom (opsional)") },
                    placeholder = { Text("https://... atau content://...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = theme.glowColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = theme.glowColor,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_icon_uri")
                )

                // Genre & Size inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("Genre") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = theme.glowColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sizeText,
                        onValueChange = { sizeText = it },
                        label = { Text("Ukuran") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = theme.glowColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Package name input
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name Aplikasi / Emulator (untuk peluncuran langsung)") },
                    placeholder = { Text("mis. com.winlator / xyz.aethersx2.android") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = theme.glowColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = theme.glowColor,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newGame = GameItem(
                                id = UUID.randomUUID().toString(),
                                title = title.trim(),
                                subtitle = "$selectedPlatform • $genre",
                                platform = selectedPlatform,
                                iconRes = selectedIconPreset,
                                customIconUri = customIconUri.trim().ifBlank { null },
                                packageName = packageName.trim().ifBlank { null },
                                genre = genre,
                                size = sizeText,
                                trophiesPercent = 0,
                                playTimeHours = 0,
                                lastPlayed = "Baru ditambahkan"
                            )
                            onGameAdded(newGame)
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.primaryColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_add_game")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Simpan ke Database Room", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

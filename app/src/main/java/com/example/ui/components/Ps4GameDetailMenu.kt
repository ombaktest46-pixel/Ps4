package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameItem
import com.example.model.ThemeColorPreset

@Composable
fun Ps4GameDetailMenu(
    game: GameItem,
    theme: ThemeColorPreset,
    onLaunchGame: (GameItem) -> Unit,
    onConfigureEmulator: (GameItem) -> Unit,
    onDeleteGame: ((GameItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launchIntent = remember(game.packageName) {
        if (!game.packageName.isNullOrBlank()) {
            try {
                context.packageManager.getLaunchIntentForPackage(game.packageName)
            } catch (_: Exception) {
                null
            }
        } else null
    }
    val isAppInstalled = launchIntent != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Game Title & Platform Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${game.genre} • ${game.size}",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.primaryColor.copy(alpha = 0.35f))
                            .border(1.dp, theme.glowColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = game.platform,
                            color = theme.glowColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isAppInstalled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF00E676).copy(alpha = 0.20f))
                                .border(1.dp, Color(0xFF00E676).copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "✓ Terpasang di HP",
                                color = Color(0xFF00E676),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Fresh Modern Action Button (Clean Handheld Style, no harsh blue block)
        Button(
            onClick = {
                if (launchIntent != null) {
                    try {
                        context.startActivity(launchIntent)
                    } catch (_: Exception) {
                        onLaunchGame(game)
                    }
                } else {
                    onLaunchGame(game)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAppInstalled) Color(0xFF00C853).copy(alpha = 0.90f)
                else if (game.platform.contains("Winlator", ignoreCase = true)) Color(0xFFC62828).copy(alpha = 0.90f)
                else Color(0xFF1E293B),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    1.2.dp,
                    if (isAppInstalled) Color(0xFF69F0AE).copy(alpha = 0.8f)
                    else if (game.platform.contains("Winlator", ignoreCase = true)) Color(0xFFFF8A80).copy(alpha = 0.8f)
                    else Color.White.copy(alpha = 0.25f),
                    RoundedCornerShape(12.dp)
                )
                .testTag("start_game_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isAppInstalled) Icons.Default.Launch else Icons.Default.PlayArrow,
                    contentDescription = "Mainkan",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isAppInstalled) {
                        if (game.platform.contains("Winlator", ignoreCase = true)) "Jalankan di Winlator PC (A)"
                        else "Buka Aplikasi Game (A)"
                    } else if (game.platform.contains("Winlator", ignoreCase = true)) {
                        "Jalankan Shortcut Winlator (A)"
                    } else {
                        "Mainkan Game (A)"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            }
        }

        // Clean Fresh Handheld Status Info (No fake trophies)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0C1628).copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isAppInstalled) Color(0xFF00E676) else Color(0xFF64B5F6))
                )
                Text(
                    text = if (isAppInstalled) "Terpasang & Siap Dimainkan" else "Tersedia di Handheld Library",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "${game.genre} • ${game.size}",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 11.sp
            )
        }

        // Functional Action Rows (Emulator Settings, Controller)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Emulator config card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .clickable { onConfigureEmulator(game) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0C1628).copy(alpha = 0.75f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Emulator",
                            tint = theme.glowColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Konfigurasi",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "${game.emulatorConfig.resolution} • ${game.emulatorConfig.fpsLimit}FPS",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }

            // Controller Mapping
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .clickable { onConfigureEmulator(game) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0C1628).copy(alpha = 0.75f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gamepad,
                            contentDescription = "Gamepad",
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Kontroler",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = game.emulatorConfig.controllerPreset,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Delete from Room Database option
        if (onDeleteGame != null && !game.isPersonalization && !game.isAddButton) {
            OutlinedButton(
                onClick = { onDeleteGame(game) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("delete_game_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Game",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "Hapus dari Library Database",
                    color = Color(0xFFFF8A80),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

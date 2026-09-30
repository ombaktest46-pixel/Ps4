package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.GameItem
import com.example.model.ThemeColorPreset
import kotlinx.coroutines.delay

@Composable
fun GameLaunchSimulatorDialog(
    game: GameItem,
    theme: ThemeColorPreset,
    onExitGame: () -> Unit,
    onPlaySfxTick: () -> Unit
) {
    val context = LocalContext.current
    var isBooting by remember { mutableStateOf(true) }
    var bootProgress by remember { mutableStateOf("Memuat lingkungan runtime ${game.platform}...") }

    LaunchedEffect(Unit) {
        val plat = game.platform.lowercase()
        bootProgress = when {
            plat.contains("winlator") -> "Menginisialisasi Wine Container & Box64..."
            plat.contains("gamehub") -> "Menghubungkan Container GameHub Nexus..."
            plat.contains("yuzu") || plat.contains("switch") -> "Menginisialisasi Switch Horizon OS & NCE..."
            plat.contains("aether") || plat.contains("ps2") -> "Memuat Sony PS2 BIOS (SCPH-70004)..."
            plat.contains("android") -> "Membuka Aplikasi Game Android Native..."
            else -> "Memuat lingkungan runtime ${game.platform}..."
        }
        delay(600)
        bootProgress = when {
            plat.contains("winlator") -> "Mengompilasi DXVK 2.3 & D3D11 Vulkan Pipeline..."
            plat.contains("gamehub") -> "Sinkronisasi Profil Game PC & Emulator Container..."
            plat.contains("yuzu") || plat.contains("switch") -> "Memuat Turnip Vulkan Driver Adreno..."
            plat.contains("aether") || plat.contains("ps2") -> "Menginisialisasi Emotion Engine (EE) & GS Vulkan 2x Native..."
            plat.contains("android") -> "Mengaktifkan Vulkan Game Booster & Refresh Rate 120Hz..."
            else -> "Menginisialisasi GPU Shader Cache (Vulkan)..."
        }
        delay(600)
        bootProgress = "Memetakan Kontroler DualShock 4 Wireless..."
        delay(500)
        bootProgress = "Meluncurkan ${game.title}..."
        delay(500)
        isBooting = false
    }

    val infiniteTransition = rememberInfiniteTransition(label = "game_loop")
    val fpsVariance by infiniteTransition.animateFloat(
        initialValue = 59.8f,
        targetValue = 60.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fps_val"
    )

    Dialog(
        onDismissRequest = onExitGame,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Game Art / Visual Backdrop
            if (!game.customIconUri.isNullOrBlank()) {
                AsyncImage(
                    model = game.customIconUri,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF070E1A))
                )
            } else if (game.iconRes != 0) {
                Image(
                    painter = painterResource(id = game.iconRes),
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF070E1A))
                )
            }

            // Dark vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black.copy(alpha = 0.40f),
                                Color.Black.copy(alpha = 0.90f)
                            )
                        )
                    )
            )

            // Top HUD: Exit button, Title, Live FPS & System Telemetry
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onExitGame,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("exit_game_session")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Keluar Game",
                            tint = Color.White
                        )
                    }
                    Column {
                        Text(
                            text = game.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${game.platform} • ${game.emulatorConfig.resolution}",
                            color = theme.glowColor,
                            fontSize = 11.sp
                        )
                    }
                }

                // Telemetry pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "FPS: ${String.format("%.1f", fpsVariance)}",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RAM: 3.4 GB",
                        color = Color(0xFF64B5F6),
                        fontSize = 11.sp
                    )
                }
            }

            // Booting screen state
            if (isBooting) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        color = theme.glowColor,
                        modifier = Modifier.size(42.dp),
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = bootProgress,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // In-Game Live Interactive Controls Overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Center Live Game prompt
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SESI PERMAINAN BERJALAN",
                            color = theme.glowColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gunakan kontrol virtual DualShock atau stik Bluetooth eksternal Anda.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )

                        // Launch real native APK if requested
                        if (game.packageName != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    try {
                                        val launchIntent = context.packageManager.getLaunchIntentForPackage(game.packageName)
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    } catch (_: Exception) {}
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buka APK Native di HP")
                            }
                        }
                    }

                    // Clean Gaming Handheld Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Handheld Gamepad Connected indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .border(1.dp, Color(0xFF00E676).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Text(
                                text = "🎮 Mode Gamepad Handheld Aktif",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Exit button or press B
                        Button(
                            onClick = onExitGame,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Keluar ke Menu Handheld [B]", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

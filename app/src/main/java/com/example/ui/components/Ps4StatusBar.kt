package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ThemeColorPreset
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Ps4StatusBar(
    theme: ThemeColorPreset,
    isBgmPlaying: Boolean,
    onToggleBgm: () -> Unit,
    onAvatarClick: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("13:16") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            currentTime = sdf.format(Date())
            delay(10000)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_eq")
    val eqAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_alpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: PS4 System status indicators
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Notifications with unread badge
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color(0xFFFF3D00),
                        contentColor = Color.White,
                        modifier = Modifier.size(14.dp)
                    ) {
                        Text("1", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Party / Friend Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Headset,
                    contentDescription = "Party Online",
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "DestinL",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Current Clock
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Right side: BGM status, Connectivity, Battery, and Profile
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Background Audio quick toggle pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isBgmPlaying) theme.primaryColor.copy(alpha = 0.45f)
                        else Color.Black.copy(alpha = 0.35f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isBgmPlaying) theme.glowColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onToggleBgm() }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .testTag("bgm_toggle_button")
            ) {
                Icon(
                    imageVector = if (isBgmPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeMute,
                    contentDescription = "BGM status",
                    tint = if (isBgmPlaying) theme.glowColor.copy(alpha = eqAlpha) else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBgmPlaying) "BGM: ON" else "MUTE",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Wi-Fi Icon
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Wi-Fi Terhubung",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(17.dp)
            )

            // Battery Level (as seen in screenshot: 41%)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "41%",
                    color = Color(0xFF69F0AE),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = "Baterai 41%",
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Custom Avatar
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(theme.glowColor, theme.primaryColor)
                        )
                    )
                    .border(1.5.dp, Color.White, CircleShape)
                    .clickable { onAvatarClick() }
                    .testTag("user_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "D",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }

            // Handheld Settings Icon
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .testTag("status_bar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Pengaturan Konsol",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

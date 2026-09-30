package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import com.example.model.ButtonGuideStyle
import com.example.model.ThemeColorPreset

/**
 * Minimalist Handheld Console Controller Bottom Guide.
 * Clean Nintendo Switch handheld style without cluttered geometric symbols.
 */
@Composable
fun Ps4ControllerBottomBar(
    theme: ThemeColorPreset,
    guideStyle: ButtonGuideStyle = ButtonGuideStyle.NINTENDO_SWITCH,
    onPressCross: () -> Unit,
    onPressCircle: () -> Unit,
    onPressTriangle: () -> Unit,
    onPressSquare: () -> Unit,
    onPressSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color(0xFF060A12).copy(alpha = 0.88f))
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Clean Handheld Buttons (Nintendo A/B/X/Y or Minimalist)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (guideStyle) {
                ButtonGuideStyle.NINTENDO_SWITCH, ButtonGuideStyle.MINIMAL_PILL -> {
                    // A: Pilih / Buka Game
                    NintendoButtonCap(
                        capText = "A",
                        capColor = Color(0xFFE53935),
                        label = "Pilih",
                        onClick = onPressCross,
                        testTag = "btn_ps_cross"
                    )

                    // B: Kembali
                    NintendoButtonCap(
                        capText = "B",
                        capColor = Color(0xFFFDD835),
                        textColor = Color.Black,
                        label = "Kembali",
                        onClick = onPressCircle,
                        testTag = "btn_ps_circle"
                    )

                    // X: Opsi Emulator
                    NintendoButtonCap(
                        capText = "X",
                        capColor = Color(0xFF1E88E5),
                        label = "Opsi Emulator",
                        onClick = onPressSquare,
                        testTag = "btn_ps_square"
                    )

                    // Y: Shortcut Winlator
                    NintendoButtonCap(
                        capText = "Y",
                        capColor = Color(0xFF43A047),
                        label = "+Shortcut Winlator",
                        onClick = onPressTriangle,
                        testTag = "btn_ps_triangle"
                    )
                }

                ButtonGuideStyle.PS_MINIMAL -> {
                    NintendoButtonCap(
                        capText = "✕",
                        capColor = Color(0xFF448AFF),
                        label = "Pilih",
                        onClick = onPressCross,
                        testTag = "btn_ps_cross"
                    )
                    NintendoButtonCap(
                        capText = "◯",
                        capColor = Color(0xFFFF5252),
                        label = "Kembali",
                        onClick = onPressCircle,
                        testTag = "btn_ps_circle"
                    )
                    NintendoButtonCap(
                        capText = "□",
                        capColor = Color(0xFFFF4081),
                        label = "Opsi",
                        onClick = onPressSquare,
                        testTag = "btn_ps_square"
                    )
                    NintendoButtonCap(
                        capText = "△",
                        capColor = Color(0xFF00E676),
                        label = "+Winlator",
                        onClick = onPressTriangle,
                        testTag = "btn_ps_triangle"
                    )
                }
            }
        }

        // Right: Dedicated Handheld Settings Button (+)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(theme.primaryColor.copy(alpha = 0.28f))
                .clickable { onPressSettings() }
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .testTag("btn_bottom_settings")
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = "Pengaturan",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NintendoButtonCap(
    capText: String,
    capColor: Color,
    textColor: Color = Color.White,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(capColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = capText,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.88f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


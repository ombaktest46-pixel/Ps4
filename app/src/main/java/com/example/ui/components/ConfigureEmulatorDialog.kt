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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EmulatorConfig
import com.example.model.GameItem
import com.example.model.ThemeColorPreset

@Composable
fun ConfigureEmulatorDialog(
    game: GameItem,
    theme: ThemeColorPreset,
    onDismiss: () -> Unit,
    onSaveConfig: (GameItem, EmulatorConfig) -> Unit
) {
    var resolution by remember { mutableStateOf(game.emulatorConfig.resolution) }
    var fpsLimit by remember { mutableIntStateOf(game.emulatorConfig.fpsLimit) }
    var graphicsBackend by remember { mutableStateOf(game.emulatorConfig.graphicsBackend) }
    var controllerPreset by remember { mutableStateOf(game.emulatorConfig.controllerPreset) }

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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Konfigurasi Emulator",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = game.title,
                            color = theme.glowColor,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                }

                // Resolution options
                Text("Resolusi Render:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                val resolutions = listOf("1280x720 (720p)", "1920x1080 (1080p)", "2560x1440 (2K)")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    resolutions.forEach { res ->
                        val isSelected = resolution == res
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.08f))
                                .border(1.dp, if (isSelected) theme.glowColor else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { resolution = res }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(res.substringBefore(" "), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // FPS limit
                Text("Batas FPS:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                val fpsList = listOf(30, 60, 120)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    fpsList.forEach { fps ->
                        val isSelected = fpsLimit == fps
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.08f))
                                .border(1.dp, if (isSelected) theme.glowColor else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { fpsLimit = fps }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$fps FPS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Graphics backend
                Text("Grafis API Backend:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                val backends = listOf("Vulkan (DXVK 2.3)", "OpenGL ES 3.2", "Turnip Adreno")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    backends.forEach { b ->
                        val isSelected = graphicsBackend == b
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) theme.primaryColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f))
                                .border(1.dp, if (isSelected) theme.glowColor else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { graphicsBackend = b }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(b, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val newConfig = EmulatorConfig(
                            resolution = resolution,
                            fpsLimit = fpsLimit,
                            graphicsBackend = graphicsBackend,
                            controllerPreset = controllerPreset
                        )
                        onSaveConfig(game, newConfig)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.primaryColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("save_emulator_config")
                ) {
                    Text("Simpan Konfigurasi", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

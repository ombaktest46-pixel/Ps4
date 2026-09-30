package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.model.GameItem
import com.example.model.ThemeColorPreset

@Composable
fun Ps4GameRow(
    games: List<GameItem>,
    selectedIndex: Int,
    onSelectGame: (Int) -> Unit,
    onExecuteAction: (GameItem) -> Unit,
    theme: ThemeColorPreset,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Smoothly scroll the selected item into view
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem((selectedIndex - 1).coerceAtLeast(0))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 34.dp, vertical = 38.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(games) { index, item ->
                val isSelected = index == selectedIndex

                // Efek kotak game membesar ketika dipilih (Bouncy console expansion)
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.38f else 0.85f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "card_scale"
                )

                val elevation by animateDpAsState(
                    targetValue = if (isSelected) 32.dp else 2.dp,
                    animationSpec = tween(220),
                    label = "card_elevation"
                )

                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) theme.glowColor else Color.White.copy(alpha = 0.16f),
                    animationSpec = tween(220),
                    label = "border_color"
                )

                val borderWidth by animateDpAsState(
                    targetValue = if (isSelected) 3.5.dp else 1.dp,
                    animationSpec = tween(220),
                    label = "border_width"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .zIndex(if (isSelected) 10f else 1f)
                        .scale(scale)
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .shadow(elevation, RoundedCornerShape(14.dp), ambientColor = theme.glowColor, spotColor = theme.glowColor)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF16233B),
                                        Color(0xFF0B1220)
                                    )
                                )
                            )
                            .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
                            .clickable {
                                if (isSelected) {
                                    onExecuteAction(item)
                                } else {
                                    onSelectGame(index)
                                }
                            }
                            .testTag("game_tile_${item.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.isAddButton) {
                            // Kotak Tambah Game bersih
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                theme.primaryColor.copy(alpha = 0.40f),
                                                Color(0xFF0F172A)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Game",
                                    tint = if (isSelected) theme.glowColor else Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        } else {
                            // Game Cover Image / Art (Custom Icon URI or Drawable resource)
                            if (!item.customIconUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = item.customIconUri,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (item.iconRes != 0) {
                                Image(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Subtle glossy reflection gradient
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = if (isSelected) 0.22f else 0.10f),
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.55f)
                                            )
                                        )
                                    )
                            )
                        }

                        // Kotak game polos bersih tanpa tag winlator/app/apk
                    }

                    // Indicator dot beneath selected tile
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(theme.glowColor)
                                .shadow(4.dp, CircleShape, spotColor = theme.glowColor)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(13.dp))
                    }
                }
            }
        }
    }
}

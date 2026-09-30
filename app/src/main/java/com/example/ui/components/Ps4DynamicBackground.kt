package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.BackgroundWaveStyle
import com.example.model.ThemeColorPreset
import kotlin.math.PI
import kotlin.math.sin

private data class DynamicParticle(
    val initialX: Float, // 0..1
    val initialY: Float, // 0..1
    val speed: Float,
    val size: Float,
    val baseAlpha: Float,
    val phaseOffset: Float
)

private data class TouchRipple(
    val center: Offset,
    val creationTime: Long
)

@Composable
fun Ps4DynamicBackground(
    theme: ThemeColorPreset,
    waveStyle: BackgroundWaveStyle,
    customBackgroundUri: String? = null,
    wallpaperPreset: String = "default",
    backdropIconUri: String? = null,
    backdropIconRes: Int = 0,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ps4_wave_anim")

    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (10000 / waveStyle.speedFactor).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_cycle"
    )

    // Pre-calculate deterministic particles for high performance without reallocation
    val particles = remember {
        val list = mutableListOf<DynamicParticle>()
        for (i in 0 until 40) {
            list.add(
                DynamicParticle(
                    initialX = (i * 37 % 100) / 100f,
                    initialY = (i * 73 % 100) / 100f,
                    speed = 0.05f + ((i % 5) * 0.02f),
                    size = 2.0f + (i % 4) * 1.5f,
                    baseAlpha = 0.25f + ((i % 6) * 0.12f),
                    phaseOffset = (i * 0.45f)
                )
            )
        }
        list
    }

    val activeRipples = remember { mutableStateListOf<TouchRipple>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (activeRipples.size > 8) activeRipples.removeAt(0)
                    activeRipples.add(TouchRipple(offset, System.currentTimeMillis()))
                }
            }
    ) {
        // Custom Background / Selected Game Backdrop Wallpaper
        if (!customBackgroundUri.isNullOrBlank()) {
            AsyncImage(
                model = customBackgroundUri,
                contentDescription = "Background Custom",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            )
        } else if (!backdropIconUri.isNullOrBlank()) {
            AsyncImage(
                model = backdropIconUri,
                contentDescription = "Game Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF030712).copy(alpha = 0.65f))
            )
        } else if (backdropIconRes != 0) {
            Image(
                painter = painterResource(id = backdropIconRes),
                contentDescription = "Game Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF030712).copy(alpha = 0.65f))
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Atmospheric background gradient (blends with backdrop if present)
            val hasCustomImage = !customBackgroundUri.isNullOrBlank() || !backdropIconUri.isNullOrBlank() || backdropIconRes != 0
            val (topCol, bottomCol, auraCol) = when (wallpaperPreset) {
                "deep_blue" -> Triple(Color(0xFF0D254C), Color(0xFF020B18), Color(0xFF1976D2))
                "midnight" -> Triple(Color(0xFF0D1117), Color(0xFF020406), Color(0xFF37474F))
                "cyber_neon" -> Triple(Color(0xFF1E0E38), Color(0xFF060114), Color(0xFF9C27B0))
                "crimson_nebula" -> Triple(Color(0xFF330E18), Color(0xFF0B0205), Color(0xFFE91E63))
                "carbon" -> Triple(Color(0xFF161A22), Color(0xFF090B0F), Color(0xFF546E7A))
                else -> Triple(theme.backgroundTop, theme.backgroundBottom, theme.primaryColor)
            }

            drawRect(
                brush = Brush.verticalGradient(
                    colors = if (hasCustomImage) {
                        listOf(
                            topCol.copy(alpha = 0.60f),
                            bottomCol.copy(alpha = 0.85f)
                        )
                    } else {
                        listOf(
                            topCol,
                            topCol.copy(alpha = 0.95f),
                            bottomCol
                        )
                    }
                ),
                size = size
            )

            // 2. Central / Bottom Radial Glow Aura
            val auraCenter = Offset(width * 0.5f, height * 0.65f)
            val auraRadius = (width.coerceAtLeast(height) * 0.85f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraCol.copy(alpha = 0.38f),
                        theme.glowColor.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = auraCenter,
                    radius = auraRadius
                ),
                radius = auraRadius,
                center = auraCenter
            )

            // 3. Floating Shimmering Light Motes / Particles
            particles.forEach { p ->
                val particleYProgress = (p.initialY - (animTime / (2 * PI.toFloat())) * p.speed * 2.5f) % 1.0f
                val normalizedY = if (particleYProgress < 0f) particleYProgress + 1.0f else particleYProgress
                val currentY = normalizedY * height

                // Gentle horizontal oscillation
                val driftX = sin(animTime + p.phaseOffset) * 20f
                val currentX = (p.initialX * width + driftX).coerceIn(0f, width)

                val pulseAlpha = (p.baseAlpha * (0.6f + 0.4f * sin(animTime * 2f + p.phaseOffset))).coerceIn(0.1f, 0.95f)

                // Particle soft glow
                drawCircle(
                    color = theme.ribbonColorSecondary.copy(alpha = pulseAlpha * 0.35f),
                    radius = p.size * 2.4f,
                    center = Offset(currentX, currentY)
                )
                // Particle bright core
                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha),
                    radius = p.size,
                    center = Offset(currentX, currentY)
                )
            }

            // 4. Iconic PS4 Glowing Wave Ribbons (Multi-layered Sinusoidal Splines)
            val waveCount = waveStyle.waveCount
            val baseMidY = height * 0.45f

            for (w in 0 until waveCount) {
                val waveOffsetPhase = animTime + (w * 0.85f)
                val waveBaseY = baseMidY + ((w - waveCount / 2f) * (height * 0.08f))

                val path = Path()
                val steps = 40
                val stepX = width / steps

                for (s in 0..steps) {
                    val x = s * stepX
                    // Dual frequency harmonic sinusoidal function creates the fluid ribbon effect
                    val wave1 = sin((x / width) * 2.2 * PI + waveOffsetPhase)
                    val wave2 = sin((x / width) * 4.4 * PI - waveOffsetPhase * 0.6) * 0.4
                    val y = (waveBaseY + (wave1 + wave2) * (height * 0.09f)).toFloat()

                    if (s == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                val ribbonAlpha = (0.30f + 0.50f * sin(waveOffsetPhase * 0.8f + w)).coerceIn(0.20f, 0.85f)
                val strokeThickness = when (w % 3) {
                    0 -> 4.5f
                    1 -> 2.5f
                    else -> 1.5f
                }

                // Outer soft halo
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            theme.ribbonColorPrimary.copy(alpha = 0.05f),
                            theme.ribbonColorSecondary.copy(alpha = ribbonAlpha * 0.4f),
                            theme.glowColor.copy(alpha = ribbonAlpha * 0.5f),
                            theme.ribbonColorPrimary.copy(alpha = 0.05f)
                        )
                    ),
                    style = Stroke(width = strokeThickness * 2.8f, cap = StrokeCap.Round)
                )

                // Crisp luminous ribbon core
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            theme.ribbonColorPrimary.copy(alpha = 0.1f),
                            Color.White.copy(alpha = ribbonAlpha * 0.9f),
                            theme.ribbonColorSecondary.copy(alpha = ribbonAlpha * 0.8f),
                            theme.ribbonColorPrimary.copy(alpha = 0.1f)
                        )
                    ),
                    style = Stroke(width = strokeThickness, cap = StrokeCap.Round)
                )
            }

            // 5. Dynamic Touch Waves / Shockwaves
            val now = System.currentTimeMillis()
            activeRipples.removeAll { now - it.creationTime > 1200 }
            activeRipples.forEach { ripple ->
                val progress = (now - ripple.creationTime) / 1200f
                if (progress in 0f..1f) {
                    val rippleRadius = progress * (width * 0.6f)
                    val rippleAlpha = (1f - progress) * 0.5f
                    drawCircle(
                        color = theme.glowColor.copy(alpha = rippleAlpha),
                        radius = rippleRadius,
                        center = ripple.center,
                        style = Stroke(width = (6f * (1f - progress)).coerceAtLeast(1f))
                    )
                }
            }
        }
    }
}

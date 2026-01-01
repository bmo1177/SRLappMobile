package com.example.srlappexperiment.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.srlappexperiment.ui.theme.*

/**
 * A premium gradient progress bar with shimmer effect.
 */
@Composable
fun PremiumGradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    primaryColor: Color = PrimaryPurple,
    secondaryColor: Color = PrimaryBlue
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
        )
    }
}

/**
 * A glassmorphism modifier for premium card designs.
 */
fun Modifier.glassmorphism() = this
    .background(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    )
    .padding(1.dp) // Border effect
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.2f),
                Color.White.copy(alpha = 0.05f)
            )
        ),
        shape = RoundedCornerShape(16.dp)
    )

/**
 * A pulsing loading indicator with brand colors.
 */
@Composable
fun PremiumLoadingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(
                Brush.radialGradient(
                    colors = listOf(PrimaryPurple, Color.Transparent)
                ),
                shape = RoundedCornerShape(50)
            )
    )
}
@Composable
fun PremiumSuccessAnimation(
    modifier: Modifier = Modifier,
    onAnimationComplete: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }
    val pathPortion = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        startAnimation = true
        pathPortion.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        onAnimationComplete()
    }

    Canvas(modifier = modifier.size(100.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.5f)
            lineTo(size.width * 0.45f, size.height * 0.75f)
            lineTo(size.width * 0.8f, size.height * 0.3f)
        }

        drawPath(
            path = path,
            color = Success,
            style = Stroke(
                width = 8.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(pathPortion.value * 500f, 500f),
                    phase = (1f - pathPortion.value) * 500f
                )
            )
        )
    }
}

/**
 * A premium background modifier with brand gradients.
 */
fun Modifier.premiumBackground(
    style: GradientStyle = GradientStyle.PurpleBlue
) = this.then(
    Modifier.background(brush = style.brush)
)

enum class GradientStyle(val brush: Brush) {
    PurpleBlue(Brush.linearGradient(colors = listOf(PrimaryPurple, PrimaryBlue))),
    BlueTeal(Brush.linearGradient(colors = listOf(PrimaryBlue, PrimaryTeal))),
    Sunrise(Brush.linearGradient(colors = listOf(GoldAccent, AccentCoral))),
    Twilight(Brush.verticalGradient(colors = listOf(PrimaryDark, Color(0xFF1E272E))))
}


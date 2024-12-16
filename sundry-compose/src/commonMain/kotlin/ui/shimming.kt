package net.lsafer.sundry.compose.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

private const val ANIMATION_INITIAL_VALUE = 0f
private const val ANIMATION_TARGET_VALUE = 1300f
private const val ANIMATION_DURATION = 800

private val SHIMMER_COLORS = listOf(
    Color.LightGray.copy(alpha = 0.6f),
    Color.LightGray.copy(alpha = 0.2f),
    Color.LightGray.copy(alpha = 0.6f),
)

fun Modifier.shimming(enabled: Boolean = true, shape: Shape? = null): Modifier {
    return composed {
        if (!enabled) return@composed this

        val transition = rememberInfiniteTransition()
        val translateAnimation = transition.animateFloat(
            initialValue = ANIMATION_INITIAL_VALUE,
            targetValue = ANIMATION_TARGET_VALUE,
            animationSpec = infiniteRepeatable(
                animation = tween(ANIMATION_DURATION),
                repeatMode = RepeatMode.Reverse
            )
        )

        val brush = Brush.linearGradient(
            colors = SHIMMER_COLORS,
            start = Offset.Zero,
            end = Offset(
                x = translateAnimation.value,
                y = translateAnimation.value
            )
        )

        background(brush, shape ?: MaterialTheme.shapes.extraSmall, 1f)
    }
}

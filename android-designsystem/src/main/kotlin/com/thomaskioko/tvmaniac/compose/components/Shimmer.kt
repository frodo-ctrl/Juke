package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode

private const val SHIMMER_DURATION_MILLIS = 1200

/**
 * 在半透明基底上绘制扫过高光，表示内容正在加载。
 * 当动画减少或在检查模式下时，回退到静态基底，以便预览和截图测试保持确定性。
 * 在此之前应用 [clip] 将扫描限制在某个形状内。
 */
@Composable
public fun Modifier.shimmer(): Modifier {
    val animationsDisabled = LocalInspectionMode.current || rememberReduceMotionEnabled()
    val baseColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)

    if (animationsDisabled) {
        return this.background(baseColor)
    }

    val transition = rememberInfiniteTransition(label = "Shimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ShimmerProgress",
    )

    return this
        .background(baseColor)
        .drawBehind {
            val width = size.width
            val start = progress.value * 2f * width - width
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, highlightColor, Color.Transparent),
                    start = Offset(start, 0f),
                    end = Offset(start + width, size.height),
                ),
            )
        }
}

/** 预裁剪到 [shape] 的 Box，渲染 [shimmer]。由调用方通过 [modifier] 设置大小。 */
@Composable
public fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
) {
    Box(modifier = modifier.clip(shape).shimmer())
}

package com.thomaskioko.tvmaniac.compose.extensions

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import kotlin.math.pow

/**
 * 在前景中绘制垂直渐变遮罩。
 *
 * @param color 渐变遮罩的颜色。
 * @param startYPercentage 起始 y 值，以布局高度的百分比表示（0f 到 1f）
 * @param endYPercentage 结束 y 值，以布局高度的百分比表示（0f 到 1f）
 * @param decay 应用于渐变的指数衰减。默认为 `1.0f`，即线性渐变。
 * @param numStops 渐变中要绘制的颜色停止点数量。数字越大，视觉质量越高，但会以绘制性能为代价。默认为 `16`。
 */
@SuppressLint("ComposeModifierComposed")
public fun Modifier.verticalGradientScrim(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) startYPercentage: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) endYPercentage: Float = 1f,
    decay: Float = 1.0f,
    numStops: Int = 16,
): Modifier = composed {
    val colors = remember(color, numStops) {
        if (decay != 1f) {
            // 如果有非线性衰减，我们需要手动创建颜色渐变步骤
            val baseAlpha = color.alpha
            List(numStops) { i ->
                val x = i * 1f / (numStops - 1)
                val opacity = x.pow(decay)
                color.copy(alpha = baseAlpha * opacity)
            }
        } else {
            // 如果有线性衰减，我们只创建一个简单的起始 + 结束颜色列表
            listOf(color.copy(alpha = 0f), color)
        }
    }

    var height by remember { mutableFloatStateOf(0f) }
    val brush = remember(color, numStops, startYPercentage, endYPercentage, height) {
        Brush.verticalGradient(
            colors = colors,
            startY = height * startYPercentage,
            endY = height * endYPercentage,
        )
    }

    drawBehind {
        height = size.height
        drawRect(brush = brush)
    }
}

@SuppressLint("ComposeModifierComposed")
internal fun Modifier.iconButtonBackgroundScrim(
    enabled: Boolean,
    shape: Shape = CircleShape,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
): Modifier = composed {
    if (enabled) {
        Modifier
            .padding(horizontal = TvManiacSpacing.xSmall)
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = alpha),
                shape = shape,
            )
    } else {
        this.padding(horizontal = TvManiacSpacing.xxSmall)
    }
}

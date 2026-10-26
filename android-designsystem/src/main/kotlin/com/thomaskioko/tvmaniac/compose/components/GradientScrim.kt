package com.thomaskioko.tvmaniac.compose.components

import android.annotation.SuppressLint
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.pow

/**
 * 在前景中绘制垂直渐变遮罩。
 *
 * @param color 渐变遮罩的颜色。
 * @param decay 应用于渐变的指数衰减。默认为 `3.0f`，即三次衰减。
 * @param numStops 渐变中要绘制的颜色停止点数量。数字越大，视觉质量越高，但会以绘制性能为代价。默认为 `16`。
 */
@SuppressLint("ComposeModifierComposed")
public fun Modifier.drawForegroundGradientScrim(
    color: Color,
    decay: Float = 3.0f,
    numStops: Int = 16,
    startY: Float = 0f,
    endY: Float = 1f,
): Modifier = composed {
    val colors = remember(color, numStops) {
        val baseAlpha = color.alpha
        List(numStops) { i ->
            val x = i * 1f / (numStops - 1)
            val opacity = x.pow(decay)
            color.copy(alpha = baseAlpha * opacity)
        }
    }

    drawWithContent {
        drawContent()
        drawRect(
            topLeft = Offset(x = 0f, y = startY * size.height),
            size = size.copy(height = (endY - startY) * size.height),
            brush = Brush.verticalGradient(colors = colors),
        )
    }
}

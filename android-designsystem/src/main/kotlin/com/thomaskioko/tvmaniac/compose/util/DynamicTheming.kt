package com.thomaskioko.tvmaniac.compose.util

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val COVER_IMAGE_SIZE = 240
private const val MAC_COLOR_COUNT = 8

@Composable
public fun rememberDominantColorState(
    context: Context = LocalContext.current,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    defaultOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    cacheSize: Int = 12,
    isColorValid: (Color) -> Boolean = { true },
): DominantColorState = remember {
    DominantColorState(context, defaultColor, defaultOnColor, cacheSize, isColorValid)
}

/**
 * 允许从图像动态设置 [androidx.compose.material.Colors.primary] 颜色的可组合项。
 */
@Composable
public fun DynamicThemePrimaryColorsFromImage(
    dominantColorState: DominantColorState = rememberDominantColorState(),
    content: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme.copy(
        primary = animateColorAsState(
            dominantColorState.color,
            spring(stiffness = Spring.StiffnessLow),
            label = "primaryColorAnimation",
        )
            .value,
        onPrimary = animateColorAsState(
            dominantColorState.onColor,
            spring(stiffness = Spring.StiffnessLow),
            label = "onPrimaryColorAnimation",
        )
            .value,
    )

    MaterialTheme(colorScheme = colors, content = content)
}

/**
 * 存储和缓存从图像计算的任何主色结果的类。
 *
 * @param context Android context
 * @param defaultColor 默认颜色，当 [calculateDominantColor] 无法计算主色时使用
 * @param defaultOnColor [defaultColor] 的默认前景色 'on color'。
 * @param cacheSize 用于存储最近结果的 [LruCache] 大小。传入 `0` 禁用缓存。
 * @param isColorValid 允许过滤计算出的图像颜色的 lambda。
 */
@Stable
public class DominantColorState(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    cacheSize: Int = 12,
    private val isColorValid: (Color) -> Boolean = { true },
) {
    public var color: Color by mutableStateOf(defaultColor)
        private set

    public var onColor: Color by mutableStateOf(defaultOnColor)
        private set

    private val cache = when {
        cacheSize > 0 -> LruCache<String, DominantColors>(cacheSize)
        else -> null
    }

    public suspend fun updateColorsFromImageUrl(url: String) {
        val result = calculateDominantColor(url)
        color = result?.color ?: defaultColor
        onColor = result?.onColor ?: defaultOnColor
    }

    private suspend fun calculateDominantColor(url: String): DominantColors? {
        val cached = cache?.get(url)
        if (cached != null) {
            return cached
        }

        // 否则我们计算图像中的色板，并返回第一个有效颜色
        return calculateSwatchesInImage(context, url)
            .sortedByDescending { swatch -> swatch.population }
            .firstOrNull { swatch -> isColorValid(Color(swatch.rgb)) }
            ?.let { swatch ->
                DominantColors(
                    color = Color(swatch.rgb),
                    onColor = Color(swatch.bodyTextColor).copy(alpha = 1f),
                )
            }
            ?.also { result -> cache?.put(url, result) }
    }

    /** 将颜色值重置为 [defaultColor]。 */
    public fun reset() {
        color = defaultColor
        onColor = defaultColor
    }
}

@Immutable
private data class DominantColors(val color: Color, val onColor: Color)

/** 使用 Coil 获取给定 [imageUrl]，然后使用 [Palette] 计算主色。 */
private suspend fun calculateSwatchesInImage(
    context: Context,
    imageUrl: String,
): List<Palette.Swatch> {
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(COVER_IMAGE_SIZE)
        .scale(Scale.FILL)
        .allowHardware(false)
        .memoryCacheKey("$imageUrl.palette")
        .build()

    val bitmap = when (val result = context.imageLoader.execute(request)) {
        is SuccessResult -> result.drawable.toBitmap()
        else -> null
    }

    return bitmap?.let {
        withContext(Dispatchers.Default) {
            val palette = Palette.Builder(bitmap)
                .resizeBitmapArea(0)
                .clearFilters()
                .maximumColorCount(MAC_COLOR_COUNT)
                .generate()

            palette.swatches
        }
    }
        ?: emptyList()
}

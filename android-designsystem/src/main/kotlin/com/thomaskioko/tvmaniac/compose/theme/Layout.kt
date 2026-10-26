package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 当前窗口宽度大小类，在根节点从 `calculateWindowSizeClass` 提供一次。
 * 预览和测试默认为 [WindowWidthSizeClass.Compact]。
 */
public val LocalWindowWidthSizeClass: ProvidableCompositionLocal<WindowWidthSizeClass> =
    staticCompositionLocalOf { WindowWidthSizeClass.Compact }

/**
 * 竖版海报卡片的尺寸乘数，从用户的海报样式偏好提供。
 * 缩放 [Layout.posterWidth] 并派生 [Layout.posterColumns]。默认为 1.0（标准），
 * 精确复制响应式基础尺寸。
 */
public val LocalPosterWidthScale: ProvidableCompositionLocal<Float> =
    staticCompositionLocalOf { 1f }

/**
 * 横版（16:9 背景）卡片的尺寸乘数，从用户的海报样式偏好提供。
 * 缩放 [Layout.backdropCardWidth]。默认为 1.0（标准）。
 */
public val LocalLandscapeWidthScale: ProvidableCompositionLocal<Float> =
    staticCompositionLocalOf { 1f }

/**
 * 应用于每张海报的圆角半径，从用户的海报样式偏好提供。
 * 默认为 0.dp（Sharp），复制当前的直角海报。
 */
public val LocalPosterCornerRadius: ProvidableCompositionLocal<Dp> =
    staticCompositionLocalOf { 0.dp }

public object Layout {
    public val bodyMargin: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 24.dp
            WindowWidthSizeClass.Expanded -> 32.dp
            else -> 16.dp
        }

    public val gutter: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 12.dp
            WindowWidthSizeClass.Expanded -> 16.dp
            else -> 8.dp
        }

    public val posterColumns: Int
        @Composable @ReadOnlyComposable
        get() {
            val base = when (LocalWindowWidthSizeClass.current) {
                WindowWidthSizeClass.Medium -> 5
                WindowWidthSizeClass.Expanded -> 7
                else -> 3
            }
            return (base / LocalPosterWidthScale.current).roundToInt().coerceAtLeast(1)
        }

    /**
     * 忽略用户海报尺寸偏好的响应式海报宽度。
     * 用于列表行缩略图和其他不应随偏好调整大小的固定布局表面。
     */
    public val posterWidthFixed: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 140.dp
            WindowWidthSizeClass.Expanded -> 160.dp
            else -> 112.dp
        }

    public val posterWidth: Dp
        @Composable @ReadOnlyComposable
        get() = posterWidthFixed * LocalPosterWidthScale.current

    public val backdropCardWidth: Dp
        @Composable @ReadOnlyComposable
        get() {
            val base = when (LocalWindowWidthSizeClass.current) {
                WindowWidthSizeClass.Medium -> 300.dp
                WindowWidthSizeClass.Expanded -> 360.dp
                else -> 240.dp
            }
            return base * LocalLandscapeWidthScale.current
        }

    public val castCardWidth: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 128.dp
            WindowWidthSizeClass.Expanded -> 144.dp
            else -> 112.dp
        }

    /** 紧凑列表行的图片宽度，匹配 iOS `ImageDimens.compactThumbnailWidth`。 */
    public val compactThumbnailWidth: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 120.dp
            WindowWidthSizeClass.Expanded -> 136.dp
            else -> 104.dp
        }
}

/**
 * 不依赖窗口大小类的固定图片 token：宽高比（以 width / height 表示，
 * 匹配 `Modifier.aspectRatio` 语义）和 hero/视差高度。
 */
public object ImageDimens {
    public val PosterAspect: Float = 2f / 3f
    public val BackdropAspect: Float = 16f / 9f
    public val CastAspect: Float = 3f / 4f

    public val GridItemSpacing: Dp = 4.dp

    public val HeroDefaultHeight: Dp = 520.dp
    public val HeroShowDetailsHeight: Dp = 500.dp
    public val HeroProfileHeight: Dp = 350.dp
    public val HeroCollapsedHeight: Dp = 120.dp
}

public object TvManiacSpacing {
    public val none: Dp = 0.dp
    public val xxxSmall: Dp = 2.dp
    public val xxSmall: Dp = 4.dp
    public val xSmall: Dp = 8.dp
    public val small: Dp = 12.dp
    public val medium: Dp = 16.dp
    public val large: Dp = 24.dp
    public val xLarge: Dp = 32.dp
    public val xxLarge: Dp = 48.dp
    public val xxxLarge: Dp = 64.dp
}

public object TvManiacElevation {
    public val small: Dp = 2.dp
    public val medium: Dp = 4.dp
    public val large: Dp = 8.dp
}

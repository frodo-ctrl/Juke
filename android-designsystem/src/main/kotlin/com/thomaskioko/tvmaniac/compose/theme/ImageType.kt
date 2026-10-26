package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp

/**
 * 图片表面的种类。将以大小类为键的 [width] 与匹配的 [aspect] 捆绑在一起，
 * 以便调用站点声明种类一次，而不是手动将 [Layout] 宽度与 [ImageDimens] 宽高比配对。
 * 仅用于布局尺寸；图片获取/桶选择保留给 TMDB 拦截器。
 */
public enum class ImageType {
    Poster,
    Backdrop,
    Cast,
    ;

    /** 此种类的宽高比，以 width / height 表示（匹配 `Modifier.aspectRatio`）。 */
    public val aspect: Float
        get() = when (this) {
            Poster -> ImageDimens.PosterAspect
            Backdrop -> ImageDimens.BackdropAspect
            Cast -> ImageDimens.CastAspect
        }

    /** 此种类在当前窗口宽度大小类下的宽度。 */
    public val width: Dp
        @Composable @ReadOnlyComposable
        get() = when (this) {
            Poster -> Layout.posterWidth
            Backdrop -> Layout.backdropCardWidth
            Cast -> Layout.castCardWidth
        }
}

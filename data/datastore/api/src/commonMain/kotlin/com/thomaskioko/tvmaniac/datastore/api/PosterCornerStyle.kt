package com.thomaskioko.tvmaniac.datastore.api

/**
 * 海报角预设。[cornerRadius] 是以密度无关单位表示的圆角半径，两个平台共享；
 * [SHARP] 是中性的直角基线。
 */
public enum class PosterCornerStyle(public val cornerRadius: Float) {
    SHARP(0f),
    CLASSIC(4f),
    ROUNDED(8f),
    PILL(16f),
}

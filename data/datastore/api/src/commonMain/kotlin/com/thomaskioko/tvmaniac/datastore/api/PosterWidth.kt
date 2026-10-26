package com.thomaskioko.tvmaniac.datastore.api

/**
 * 海报宽度预设。[scale] 乘以响应式基础宽度并与窗口尺寸等级组合；
 * [STANDARD] 是中性的 1.0 基线。
 */
public enum class PosterWidth(public val scale: Float) {
    COMPACT(0.85f),
    STANDARD(1.0f),
    COMFORTABLE(1.15f),
    LARGE(1.25f),
}

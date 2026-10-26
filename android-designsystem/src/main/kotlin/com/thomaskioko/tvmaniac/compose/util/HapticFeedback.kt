package com.thomaskioko.tvmaniac.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * 是否在支持的交互上触发触觉反馈。在应用根节点从持久化偏好提供；
 * 默认为 `true`。通过 [rememberHapticFeedback] 门控每个触觉调用站点，
 * 以便将其关闭时全部静默。
 */
public val LocalHapticFeedbackEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf {
    true
}

/**
 * 返回一个回调，当 [LocalHapticFeedbackEnabled] 开启时执行触觉脉冲，否则无操作。
 * 在交互站点（如已观看切换和长按）调用。
 */
@Composable
public fun rememberHapticFeedback(): () -> Unit {
    val enabled = LocalHapticFeedbackEnabled.current
    val haptics = LocalHapticFeedback.current
    return remember(enabled, haptics) {
        { if (enabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress) }
    }
}

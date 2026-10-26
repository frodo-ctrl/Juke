package com.thomaskioko.tvmaniac.compose.components

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * 报告用户是否在系统范围内禁用了动画。读取
 * [Settings.Global.ANIMATOR_DURATION_SCALE]；比例为 `0` 表示动画已关闭。
 *
 * @return 当应抑制动效并立即显示最终状态时返回 `true`。
 */
@Composable
public fun rememberReduceMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }
}

package com.thomaskioko.tvmaniac.compose.util

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 控制 Discover 精选分页器（`PosterCardsPager`）上的自动前进 LaunchedEffect。
 *
 * 生产中默认为 `true`：每 4.5 秒分页器动画到下一页，以便精选轮播自动循环。
 * 测试可以通过 `CompositionLocalProvider(LocalAutoAdvanceEnabled provides false)` 将其覆盖为
 * `false`，以便可见页面保持在测试离开的位置。这使得分页器断言具有确定性，
 * 无论自 activity 启动以来经过了多少实际时间。
 */
public val LocalAutoAdvanceEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf {
    true
}

package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** 用于为 Now in Android 建模背景色和色调高度值的类。 */
@Immutable
internal data class BackgroundTheme(
    val color: Color = Color.Unspecified,
    val tonalElevation: Dp = Dp.Unspecified,
)

/** [BackgroundTheme] 的组合局部。 */
internal val LocalBackgroundTheme = staticCompositionLocalOf { BackgroundTheme() }

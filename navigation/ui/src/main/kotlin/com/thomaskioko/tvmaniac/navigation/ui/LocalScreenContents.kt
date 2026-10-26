package com.thomaskioko.tvmaniac.navigation.ui

import androidx.compose.runtime.compositionLocalOf

/**
 * 对 [ScreenContent] 多绑定的 Composition 作用域访问。
 *
 * 在根 composable（`RootScreen`）处提供一次，由渲染自己子栈的后代读取。
 * 当中间 UI 容器（如 home 标签页宿主）需要分发推送的屏幕时，
 * 避免将 [ScreenContent] 穿过每个嵌套的 composable 签名。
 */
public val LocalScreenContents: androidx.compose.runtime.ProvidableCompositionLocal<Set<ScreenContent>> =
    compositionLocalOf { emptySet() }

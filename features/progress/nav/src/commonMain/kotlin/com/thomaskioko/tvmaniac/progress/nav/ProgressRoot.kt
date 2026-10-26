package com.thomaskioko.tvmaniac.progress.nav

import com.thomaskioko.tvmaniac.navigation.NavRoot
import kotlinx.serialization.Serializable

/**
 * Progress 底部标签页目的地的标签根。
 *
 * 锚定 Progress 标签页的返回栈。通过 [com.thomaskioko.tvmaniac.progress.nav.di.ProgressRootBinding]
 * 贡献到 `Set<NavRoot>`。
 */
@Serializable
public data object ProgressRoot : NavRoot

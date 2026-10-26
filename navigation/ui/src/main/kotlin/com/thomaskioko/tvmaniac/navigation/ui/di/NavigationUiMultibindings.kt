package com.thomaskioko.tvmaniac.navigation.ui.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.ui.ScreenContent
import com.thomaskioko.tvmaniac.navigation.ui.SheetContent
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

/**
 * 声明功能 `ui` 模块贡献到的两个 Compose 侧多绑定 set。
 * 在 [ActivityScope] 贡献，以便每个功能模块提供自己的渲染器而无需触及集中图。
 *
 * - `Set<ScreenContent>`：匹配根导航栈中的一个条目并渲染匹配的 Compose 屏幕。
 * - `Set<SheetContent>`：匹配模态 sheet 插槽中的一个条目并渲染匹配的 Compose sheet。
 */
@ContributesTo(ActivityScope::class)
public interface NavigationUiMultibindings {
    @Multibinds
    public fun screenContents(): Set<ScreenContent>

    @Multibinds
    public fun sheetContents(): Set<SheetContent>
}

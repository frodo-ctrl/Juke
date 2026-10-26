package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.NavRootBinding
import com.thomaskioko.tvmaniac.navigation.NavRouteBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

/**
 * 声明功能模块贡献到的四个导航多绑定 set。在 [ActivityScope] 贡献，
 * 以便每个功能模块提供自己的条目而无需触及集中图。
 *
 * - `Set<NavDestination<*>>`：路由到 presenter 的解析器，每个屏幕、覆盖层或标签页根节点一个。
 * - `Set<NavRouteBinding<*>>`：每个 [NavRoute] 子类的多态序列化器条目。
 * - `Set<NavRoot>`：已注册的标签页锚点。
 * - `Set<NavRootBinding<*>>`：每个 [NavRoot] 子类的多态序列化器条目。
 *
 * 前两个 set 按契约非空；零导航目标的应用不会启动。
 * 两个根 set 允许为空，以便测试图可以省略它们不使用的根节点。
 */
@ContributesTo(ActivityScope::class)
public interface NavigationMultibindings {
    @Multibinds
    public fun navDestinations(): Set<NavDestination<*>>

    @Multibinds
    public fun navRouteBindings(): Set<NavRouteBinding<*>>

    @Multibinds(allowEmpty = true)
    public fun navRoots(): Set<NavRoot>

    @Multibinds(allowEmpty = true)
    public fun navRootBindings(): Set<NavRootBinding<*>>
}

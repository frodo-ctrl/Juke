package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * 将 [NavRoute] 子类与其生成的 [KSerializer] 配对。
 *
 * 功能通过在与其 [NavDestination] 相同的依赖注入模块中使用 `@Provides @IntoSet`
 * 为每个路由贡献一个条目，通常在 `@ContributesTo(ActivityScope::class) interface` 内。
 * 贡献被收集为 `Set<NavRouteBinding<*>>` 并折叠到多态 [NavRouteSerializer] 中，
 * 供 Decompose 的 `childStack` 使用，以便栈在进程死亡后仍然存活。
 *
 * @param T 与其序列化器配对的具体 [NavRoute] 子类。
 * @property kClass 已注册子类的具体 `KClass`。
 * @property serializer 已注册子类的生成 `KSerializer`。
 */
public data class NavRouteBinding<T : NavRoute>(
    public val kClass: KClass<T>,
    public val serializer: KSerializer<T>,
)

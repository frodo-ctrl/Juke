package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * 将 [NavRoot] 子类与其生成的 [KSerializer] 配对。
 *
 * 每个标签页的 `:nav` 模块通过在 `@ContributesTo(ActivityScope::class) interface`
 * 中使用 `@Provides @IntoSet` 为每个 [NavRoot] 贡献一个 [NavRootBinding]。
 * 贡献被收集为 `Set<NavRootBinding<*>>` 并折叠到多态 [NavRootSerializer] 中，
 * 供 Decompose 的顶层 `childStack` 使用，以便活动标签页顺序在进程死亡后仍然存活。
 *
 * @param T 与其序列化器配对的具体 [NavRoot] 子类。
 * @property kClass 已注册子类的具体 `KClass`。
 * @property serializer 已注册子类的生成 `KSerializer`。
 */
public data class NavRootBinding<T : NavRoot>(
    public val kClass: KClass<T>,
    public val serializer: KSerializer<T>,
)

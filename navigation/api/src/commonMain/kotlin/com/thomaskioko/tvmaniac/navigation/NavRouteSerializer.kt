package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * 覆盖每个通过 [NavRouteBinding] 注册的 [NavRoute] 子类的多态 [KSerializer]。
 *
 * 在 `navigation/implementation` 中从多绑定的 `Set<NavRouteBinding<*>>` 构建，
 * 并由 Decompose 的 `childStack(serializer = ...)` 消费，以便返回栈
 * 在配置更改和进程死亡后仍然存活。功能模块从不直接实现此接口；
 * 它们只贡献一个 [NavRouteBinding]。
 */
public interface NavRouteSerializer {
    /** 聚合的多态序列化器。传递给 Decompose 的 `childStack`。 */
    public val serializer: KSerializer<NavRoute>
}

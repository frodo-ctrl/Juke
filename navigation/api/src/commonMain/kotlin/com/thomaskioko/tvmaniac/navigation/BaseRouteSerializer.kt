package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * 覆盖每个通过 [NavRouteBinding] 或 [NavRootBinding] 注册的 [BaseRoute] 子类的多态 [KSerializer]。
 *
 * 在 `navigation/implementation` 中从两个多绑定 set 构建，并由 Decompose 的每标签页
 * `childStack(serializer = ...)` 消费。每个标签页的返回栈以 [NavRoot] 为底
 * 并在其上累积 [NavRoute] 条目，因此序列化器必须接受任一子类型。
 * 功能模块从不直接实现此接口；它们只向两个 set 之一贡献绑定。
 */
public interface BaseRouteSerializer {
    /** 聚合的多态序列化器。传递给 Decompose 的 `childStack`。 */
    public val serializer: KSerializer<BaseRoute>
}

package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer

/**
 * 覆盖每个通过 [NavRootBinding] 注册的 [NavRoot] 子类的多态 [KSerializer]。
 *
 * 在 `navigation/implementation` 中从多绑定的 `Set<NavRootBinding<*>>` 构建，
 * 并由 Decompose 的顶层 `childStack(serializer = ...)` 消费，以便活动标签页顺序
 * 在配置更改和进程死亡后仍然存活。功能模块从不直接实现此接口；
 * 它们只贡献一个 [NavRootBinding]。
 */
public interface NavRootSerializer {
    /** 聚合的多态序列化器。传递给 Decompose 的 `childStack`。 */
    public val serializer: KSerializer<NavRoot>
}

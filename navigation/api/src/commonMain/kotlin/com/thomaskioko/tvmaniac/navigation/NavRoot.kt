package com.thomaskioko.tvmaniac.navigation

/**
 * 标记锚定其自身返回栈的顶层目标。
 *
 * 底部标签页根节点（Discover、Library、Profile、Progress）实现 [NavRoot]。
 * 每个已注册的根节点贡献一个用于多态序列化的 [NavRootBinding]，
 * 以及它自己的实例到 `Set<NavRoot>`，以便 [Navigator] 可以在实例化时为每个根节点构建返回栈映射。
 *
 * 切换到 [NavRoot] 使用 [Navigator.switchBackStack]（保留目标的现有栈）
 * 或 [Navigator.showRoot]（清除它）。推送到活动根节点的栈上使用
 * [Navigator.navigateTo] 配合 [NavRoute]。
 */
public interface NavRoot : BaseRoute

/**
 * 返回适合将 UI 状态键控到标签页锚点的稳定标识符。
 *
 * 镜像 kotlinx.serialization 插件为 `@Serializable data object` 声明
 * 生成的 `KSerializer.descriptor.serialName`。由每个 [NavRootBinding] 组合的多态 `NavRoot`
 * 序列化器使用相同的标识来持久化和恢复活动标签页及其返回栈，
 * 跨配置更改和进程死亡，因此以 [stableKey] 为键的 UI 作用域与保存的导航状态对齐。
 */
public val NavRoot.stableKey: String
    get() = this::class.qualifiedName
        ?: error("NavRoot ${this::class.simpleName} has no qualified name; declare it as a top-level class.")

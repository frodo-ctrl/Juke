package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.ChildStack

/**
 * 从 [Navigator.buildHostNavigation] 返回的快照，包含活动标签页和
 * 每个已注册 [NavRoot] 的 [ChildStack]。
 *
 * 渲染侧 presenter 读取 [tabStacks] 获取每个标签页的返回栈，
 * 并观察 [activeRoot] 以决定哪个标签页窗格可见。
 *
 * @param T 宿主的 `childFactory` 生成的组件类型。
 * @property activeRoot 顶部条目当前可见的标签页。
 * @property tabStacks 每个已注册标签页的返回栈，以 [NavRoot] 为键。
 */
public data class MultiStackHostState<out T : Any>(
    public val activeRoot: NavRoot,
    public val tabStacks: Map<NavRoot, ChildStack<BaseRoute, T>>,
)

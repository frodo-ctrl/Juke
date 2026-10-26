package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import kotlin.reflect.KClass

/**
 * 多栈导航状态变更的单一入口点。
 *
 * 将 Decompose 的 [com.arkivanov.decompose.router.children.children] 导航 API 封装在一个
 * 公共表面之后，通过一个 [com.arkivanov.decompose.router.children.NavState] 为每个标签页
 * 协调返回栈。单独的 [com.arkivanov.decompose.router.slot.SlotNavigation] 处理覆盖层。
 * Presenter 依赖此接口而非直接依赖 Decompose，因此调用站点专注于意图（导航到某个路由、
 * 切换标签页），测试可以替换 fake 而无需引入 Decompose。
 *
 * [navigateTo] 按路由类型分发：
 * [OverlayRoute] 激活覆盖层插槽；任何其他 [NavRoute] 推送到当前活动标签页的栈上。
 * 标签页导航使用 [switchBackStack]（保留目标标签页的栈）或 [showRoot]（清除它）。
 *
 * 宿主（通常是 `HomePresenter`）使用其 `ComponentContext` 和子工厂调用一次 [buildHostNavigation]；
 * 返回的 [Value] 跟踪活动根节点和每个标签页的 [com.arkivanov.decompose.router.stack.ChildStack]。
 * 只需要活动根节点的 Presenter 可以直接观察 [activeRoot]。渲染覆盖层的宿主调用 [buildOverlaySlot]。
 */
public interface Navigator {
    /**
     * 可观察的当前活动 [NavRoot]。底层状态与 [buildHostNavigation] 相同，因此
     * 在每次标签页切换、[showRoot] 和 [replaceAllBackStacks] 时都会发出更新。
     */
    public val activeRoot: Value<NavRoot>

    /**
     * 将 [route] 推送到活动标签页返回栈的顶部。如果 [route] 实现了 [OverlayRoute]，
     * 则改为在覆盖层插槽中激活它。
     */
    public fun navigateTo(route: NavRoute)

    /**
     * 从活动标签页返回栈中弹出顶部条目。如果只剩标签页根节点则无效。
     * 如果有活动的覆盖层，先关闭覆盖层。
     */
    public fun navigateBack()

    /**
     * 从活动标签页返回栈顶部弹出条目，直到最近一个配置为 [routeClass] 实例的条目位于顶部。
     * 如果 [inclusive] 为 `true`，该条目也会被弹出。如果活动栈中不存在该类型的条目则无效。
     */
    public fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean = false)

    /**
     * 从活动标签页返回栈顶部弹出条目，直到 [toIndex] 处的条目位于顶部。
     * 供 UI 宿主（特别是 SwiftUI 的 `NavigationStack`）使用，其路径绑定以栈深度而非路由类型表达导航。
     */
    public fun popTo(toIndex: Int)

    /**
     * 如果同类型的实例已存在，将 [route] 带到活动标签页返回栈的顶部（基于类的去重），
     * 否则将其添加到顶部。
     */
    public fun bringToFront(route: NavRoute)

    /**
     * 将 [route] 推送到活动标签页返回栈顶部，仅在现有条目与 [route] 完全相等时才移除它（基于相等性的去重）。
     */
    public fun pushToFront(route: NavRoute)

    /**
     * 将活动标签页切换为 [root]，保留目标标签页的现有返回栈。
     * 如果 [root] 已处于活动状态则无效。当前标签页的返回栈不受影响。
     */
    public fun switchBackStack(root: NavRoot)

    /**
     * 将活动标签页切换为 [root] 并将其返回栈清除到根条目。
     * 用于显式重置流程：重新点击活动标签页、深度链接进入、个人资料登出。
     */
    public fun showRoot(root: NavRoot)

    /**
     * 重置整个导航状态。每个已注册标签页的返回栈清除到其根条目，[root] 变为活动状态。
     * 用于登出或硬重置流程。
     */
    public fun replaceAllBackStacks(root: NavRoot)

    /**
     * 将多栈导航宿主连接到 [componentContext]。每个宿主调用恰好一次（通常从 `HomePresenter.init`）。
     * [initialRoot] 是首次启动时的活动标签页；如果存在之前保存的状态，则以保存的活动根节点为准。
     * 返回的 [Value] 暴露当前活动根节点和每个已注册 [NavRoot] 的 [com.arkivanov.decompose.router.stack.ChildStack]；
     * 通过 `value.tabStacks[root]` 访问标签页栈。
     *
     * Decompose 的 `children(...)` API 按照
     * [com.arkivanov.decompose.router.children.ChildNavState.Status] 协调子组件生命周期：
     * 活动标签页的顶部为 `RESUMED`，其他所有条目为 `CREATED`（实例保留，返回处理程序分离，回调暂停）。
     * 此调用注册的单个 `backTransformer` 替换每个标签页中的返回按钮处理程序，因此返回按键始终弹出活动标签页。
     *
     * [childFactory] 接收 [BaseRoute]，因为每个标签页的栈以标签页的 [NavRoot] 为底层起点，
     * 并在其上累积 [NavRoute] 条目。工厂按类型分发以渲染标签页的首页内容（条目为 [NavRoot] 时）
     * 或推送的屏幕（条目为 [NavRoute] 时）。
     *
     * @param T [childFactory] 为每个条目生成的组件类型。
     * @param componentContext 宿主作用域的生命周期所有者。
     * @param initialRoot 不存在保存状态时首次启动的活动标签页。
     * @param childFactory 为每个 [BaseRoute] 条目（跨每个标签页）构建组件。
     * @return 只读 [Value]，跟踪活动根节点和每个标签页的返回栈。
     */
    public fun <T : Any> buildHostNavigation(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<MultiStackHostState<T>>

    /**
     * 为 [componentContext] 构建覆盖层子插槽，返回 UI 消费的只读 [Value]。
     * 覆盖层是实现了 [OverlayRoute] 的 [NavRoute] 子类型；激活流程通过 [navigateTo] 进行，与其他路由相同。
     *
     * @param T [childFactory] 为活动覆盖层生成的组件类型。
     * @param componentContext 插槽作用域的生命周期所有者。
     * @param childFactory 为活动覆盖层 [NavRoute] 构建组件。
     * @return 只读 [Value]，持有活动覆盖层子组件或无活动时为 `null`。
     */
    public fun <T : Any> buildOverlaySlot(
        componentContext: ComponentContext,
        childFactory: (NavRoute, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>>

    /**
     * 关闭活动覆盖层（如果有）。当前无活动覆盖层则无效。
     * 由拥有 [OverlayRoute] 的 presenter 以编程方式关闭自身（操作提交、深度链接或同级导航）。
     * 硬件返回按键已通过插槽的 `handleBackButton` 处理关闭。
     */
    public fun dismissOverlay()
}

/**
 * [Navigator.navigateBackTo] 的类型安全重载。弹出返回最近一个配置为 [T] 实例的条目。
 *
 * @param T 目标 [NavRoute] 类型，弹出返回该类型。
 * @param inclusive 当为 `true` 时，也弹出匹配的条目。
 */
public inline fun <reified T : NavRoute> Navigator.navigateBackTo(inclusive: Boolean = false) {
    navigateBackTo(T::class, inclusive)
}

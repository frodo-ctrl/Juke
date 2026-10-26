package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import kotlin.reflect.KClass

/**
 * 通过三个子类型之一将 [BaseRoute] 映射到其 [RootChild] 包装器。
 *
 * 每个功能通过 `@ContributesIntoSet(ActivityScope::class)` 贡献一个 [NavDestination] 子类型
 * （通常通过 codegen）。宿主（overlay slot 的 `DefaultRootPresenter`，多栈主体的 `HomePresenter`）
 * 收集每个贡献为 `Set<NavDestination<*>>`，按子类型过滤，按路由类匹配，并委托给 [Screen.createChild]、
 * [Overlay.createChild] 或 [TabRoot.createChild]。集中的 when 块不进入根模块。
 *
 * 三个子类型对应 [Navigator.navigateTo] 中的运行时分发：
 * - [Screen]：推送到活动标签页的返回栈。
 * - [Overlay]：在覆盖层插槽中作为模态激活。
 * - [TabRoot]：已注册 [NavRoot] 标签页之一的栈底条目。
 *
 * @param R 此目标匹配的路由类型。
 * @property routeClass 在分发时与传入的 [BaseRoute] 实例进行匹配。
 */
public sealed class NavDestination<out R : BaseRoute>(
    public val routeClass: KClass<out R>,
) {
    /** 如果此目标处理 [route]，返回 `true`。 */
    public fun matches(route: BaseRoute): Boolean = routeClass.isInstance(route)

    /**
     * 通过 [Navigator.navigateTo] 推送到活动标签页返回栈上的栈屏幕。
     *
     * @param R 此目标匹配的路由子类型。
     */
    public class Screen<R : NavRoute>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        /**
         * 使用配置的工厂为 [route] 创建 [RootChild]。调用方在调用前验证路由的类与 [routeClass] 匹配。
         *
         * @param route 与 [routeClass] 匹配的栈条目。
         * @param componentContext 新子组件的 Decompose 上下文。
         */
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }

    /**
     * 当路由也实现 [OverlayRoute] 时，通过 [Navigator.navigateTo] 激活的模态覆盖层。
     * 宿主（通常是 `DefaultRootPresenter`）在覆盖层插槽中渲染它。
     *
     * @param R 此目标匹配的路由子类型。
     */
    public class Overlay<R : NavRoute>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        /**
         * 使用配置的工厂为 [route] 创建 [RootChild]。结果由 `DefaultRootPresenter.createOverlay`
         * 重新包装用于覆盖层插槽。
         *
         * @param route 与 [routeClass] 匹配的覆盖层路由。
         * @param componentContext 新子组件的 Decompose 上下文。
         */
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }

    /**
     * 锚定其自身返回栈的标签页根节点。由 `HomePresenter` 在为每个已注册标签页投影
     * [com.arkivanov.decompose.router.stack.ChildStack] 时解析。
     *
     * @param R 此目标匹配的 [NavRoot] 子类型。
     */
    public class TabRoot<R : NavRoot>(
        routeClass: KClass<R>,
        private val factory: (R, ComponentContext) -> RootChild,
    ) : NavDestination<R>(routeClass) {
        /**
         * 使用配置的工厂为 [route] 创建 [RootChild]。在构建宿主栈时，为每个标签页调用一次。
         *
         * @param route 与 [routeClass] 匹配的标签页锚点。
         * @param componentContext 新子组件的 Decompose 上下文。
         */
        @Suppress("UNCHECKED_CAST")
        public fun createChild(route: BaseRoute, componentContext: ComponentContext): RootChild =
            factory(route as R, componentContext)
    }
}

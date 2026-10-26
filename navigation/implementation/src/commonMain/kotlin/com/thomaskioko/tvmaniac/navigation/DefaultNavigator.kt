package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.SimpleNavigation
import com.arkivanov.decompose.router.children.children
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.CrashReporter
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.reflect.KClass

/**
 * 通过 Decompose 的 `SimpleNavigation<MultiStackNavEvent>`（多栈主体）
 * 和 `SlotNavigation<NavRoute>`（覆盖层）驱动每次导航变更。
 *
 * 宿主在每个 activity 中调用一次 [buildHostNavigation] 将多栈树附加到
 * [com.arkivanov.decompose.ComponentContext]；然后 navigator 将每个公共调用路由到
 * 匹配的 Decompose 源。[navigateTo] 按类型分发：同时实现 [OverlayRoute] 的 [NavRoute]
 * 激活插槽，其他任何内容推送到活动标签页。标签页切换通过 [switchBackStack]（保留目标的栈）
 * 或 [showRoot]（清除它）进行。
 *
 * 构造函数验证至少注册了一个 [NavRoot]。第一个注册的 [NavRoot] 成为 [activeRoot] 的初始值，
 * 直到调用 [buildHostNavigation] 或恢复保存的状态。
 *
 * @param navRouteSerializer 推送到标签页上的 [NavRoute] 条目的多态序列化器。
 * @param navRootSerializer [NavRoot] 标签页锚点的多态序列化器。
 * @param baseRouteSerializer 覆盖路由和根节点的组合序列化器，用于每个标签页的返回栈。
 * @param navRoots 通过导航多绑定 set 注册的标签页锚点。
 */
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavigator(
    private val navRouteSerializer: NavRouteSerializer,
    private val navRootSerializer: NavRootSerializer,
    private val baseRouteSerializer: BaseRouteSerializer,
    private val navRoots: Set<NavRoot>,
    private val crashReporter: CrashReporter,
) : Navigator {
    init {
        require(navRoots.isNotEmpty()) {
            "No NavRoots registered. Contribute at least one via @IntoSet on the Set<NavRoot> binding."
        }
    }

    private val multiStackSource = SimpleNavigation<MultiStackNavEvent>()
    private val overlayNavigation = SlotNavigation<NavRoute>()

    private val multiStackStateSerializer by lazy {
        MultiStackNavStateSerializer(
            baseRouteSerializer = baseRouteSerializer.serializer,
            navRootSerializer = navRootSerializer.serializer,
        )
    }

    private val activeRootValue = MutableValue(navRoots.first())

    override val activeRoot: Value<NavRoot> get() = activeRootValue

    override fun navigateTo(route: NavRoute) {
        recordScreen(route)
        if (route is OverlayRoute) {
            overlayNavigation.activate(route)
            return
        }
        multiStackSource.navigate(MultiStackNavEvent.Push(route))
    }

    private fun recordScreen(route: NavRoute) {
        crashReporter.setCustomKey(CrashReportKeys.SCREEN, route::class.simpleName.orEmpty())
    }

    override fun navigateBack() {
        multiStackSource.navigate(MultiStackNavEvent.Pop())
    }

    override fun navigateBackTo(routeClass: KClass<out NavRoute>, inclusive: Boolean) {
        multiStackSource.navigate(MultiStackNavEvent.PopUntilType(routeClass, inclusive))
    }

    override fun popTo(toIndex: Int) {
        multiStackSource.navigate(MultiStackNavEvent.Pop(toIndex))
    }

    override fun bringToFront(route: NavRoute) {
        recordScreen(route)
        multiStackSource.navigate(MultiStackNavEvent.BringToFront(route))
    }

    override fun pushToFront(route: NavRoute) {
        recordScreen(route)
        multiStackSource.navigate(MultiStackNavEvent.PushToFront(route))
    }

    override fun switchBackStack(root: NavRoot) {
        requireRegistered(root)
        multiStackSource.navigate(MultiStackNavEvent.SwitchTab(root, resetStack = false))
    }

    override fun showRoot(root: NavRoot) {
        requireRegistered(root)
        multiStackSource.navigate(MultiStackNavEvent.SwitchTab(root, resetStack = true))
    }

    override fun replaceAllBackStacks(root: NavRoot) {
        requireRegistered(root)
        multiStackSource.navigate(MultiStackNavEvent.ReplaceAll(root))
    }

    override fun <T : Any> buildHostNavigation(
        componentContext: ComponentContext,
        initialRoot: NavRoot,
        childFactory: (BaseRoute, ComponentContext) -> T,
    ): Value<MultiStackHostState<T>> {
        requireRegistered(initialRoot)
        activeRootValue.value = initialRoot

        val hostStateValue: Value<MultiStackHostState<T>> = componentContext.children(
            source = multiStackSource,
            stateSerializer = multiStackStateSerializer,
            initialState = { multiStackInitialState(navRoots, initialRoot) },
            key = HOST_NAV_KEY,
            navTransformer = ::multiStackNavTransformer,
            stateMapper = { state, children -> mapToHostState(state, children) },
            backTransformer = ::multiStackBackTransformer,
            childFactory = { tabbedRoute, ctx -> childFactory(tabbedRoute.route, ctx) },
        )

        val cancellation = hostStateValue.subscribe { state ->
            activeRootValue.value = state.activeRoot
        }
        componentContext.lifecycle.doOnDestroy(cancellation::cancel)

        return hostStateValue
    }

    override fun <T : Any> buildOverlaySlot(
        componentContext: ComponentContext,
        childFactory: (NavRoute, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>> = componentContext.childSlot(
        source = overlayNavigation,
        key = OVERLAY_SLOT_KEY,
        serializer = navRouteSerializer.serializer,
        handleBackButton = true,
        childFactory = childFactory,
    )

    override fun dismissOverlay() {
        overlayNavigation.dismiss()
    }

    private fun requireRegistered(root: NavRoot) {
        require(root in navRoots) {
            "NavRoot $root is not registered. Contribute it to Set<NavRoot> via @IntoSet."
        }
    }

    private fun <T : Any> mapToHostState(
        state: MultiStackNavState,
        children: List<Child<TabbedRoute, T>>,
    ): MultiStackHostState<T> {
        val byTabbed: Map<TabbedRoute, Child.Created<TabbedRoute, T>> =
            children.filterIsInstance<Child.Created<TabbedRoute, T>>()
                .associateBy { it.configuration }
        val tabStacks = state.tabStacks.mapValues { (root, entries) ->
            val created = entries.map { entry ->
                val tabbed = TabbedRoute(tabRoot = root, route = entry)
                val child = byTabbed[tabbed]
                    ?: error("Child for $tabbed not found in children list")
                Child.Created(configuration = entry, instance = child.instance)
            }
            val active = created.lastOrNull()
                ?: error("Tab stack for $root is empty; expected at least the tab root entry")
            ChildStack(active = active, backStack = created.dropLast(1))
        }
        return MultiStackHostState(activeRoot = state.activeRoot, tabStacks = tabStacks)
    }

    private fun multiStackBackTransformer(state: MultiStackNavState): (() -> MultiStackNavState)? {
        val activeStack = state.tabStacks[state.activeRoot] ?: return null
        return if (activeStack.size > 1) {
            {
                state.copy(
                    tabStacks = state.tabStacks + (state.activeRoot to activeStack.dropLast(1)),
                )
            }
        } else {
            null
        }
    }

    private companion object {
        const val HOST_NAV_KEY = "MultiStackHostKey"
        const val OVERLAY_SLOT_KEY = "OverlaySlotKey"
    }
}

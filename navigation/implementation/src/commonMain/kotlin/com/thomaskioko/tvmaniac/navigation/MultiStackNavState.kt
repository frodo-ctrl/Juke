package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState

/**
 * 将 [NavRoot] 与该标签页返回栈中的一个条目配对，以便 Decompose 的 `children()` API 能够
 * 在多栈树中唯一地寻址每个栈条目。
 *
 * Decompose 要求同一棵树中的每个 [ChildNavState.configuration] 都是唯一的。
 * 两个标签页可能在它们的栈底共享相同的 [NavRoot]，因此将 [tabRoot] 与 [route] 配对以消除条目的歧义。
 */
internal data class TabbedRoute(
    val tabRoot: NavRoot,
    val route: BaseRoute,
)

/**
 * [DefaultNavigator] 拥有的多栈导航树的快照。
 *
 * 每个已注册的 [NavRoot] 映射到一个非空的 [BaseRoute] 条目列表：第一个元素是标签页的根条目，
 * 尾部是推送的 [NavRoute] 序列。[activeRoot] 指向当前顶部条目可见的标签页。
 * [multiStackNavTransformer] 响应 [MultiStackNavEvent] 生成此类的新实例。
 *
 * 此类是传递给 Decompose 的 `children()` 的 [NavState]。
 * [children] 将每个标签页中的每个条目投影为具有相应生命周期状态的 [ChildNavState]。
 */
internal data class MultiStackNavState(
    val activeRoot: NavRoot,
    val tabStacks: Map<NavRoot, List<BaseRoute>>,
) : NavState<TabbedRoute> {
    init {
        require(activeRoot in tabStacks) {
            "activeRoot $activeRoot must be present in tabStacks ${tabStacks.keys}"
        }
        tabStacks.forEach { (root, stack) ->
            require(stack.isNotEmpty()) { "Tab stack for $root must contain at least its root entry" }
        }
    }

    /**
     * 将每个标签页中的每个条目投影为 [ChildNavState]。只有活动标签页的顶部是
     * [ChildNavState.Status.RESUMED]；其他一切都是 [ChildNavState.Status.CREATED]。
     *
     * Decompose 的规范邻居通常是 [ChildNavState.Status.STARTED]（ChildPages `INACTIVE`）
     * 或 [ChildNavState.Status.CREATED]（ChildStack 返回栈）。此处选择 [CREATED] 是为了
     * 通过 `lifecycle.coroutineScope()` 连接的协程不会为非活动标签页运行，
     * 在底部导航重新进入时节省 CPU 和电量。
     *
     * 标签页 Presenter 必须为需要在标签页重新进入时刷新的任何 flow 使用
     * `stateIn(scope, SharingStarted.WhileSubscribed(...))`；该 flow 在 UI 订阅时重新启动，
     * 与生命周期状态无关。
     */
    override val children: List<ChildNavState<TabbedRoute>> by lazy {
        tabStacks.flatMap { (root, stack) ->
            stack.mapIndexed { index, entry ->
                SimpleChildNavState(
                    configuration = TabbedRoute(tabRoot = root, route = entry),
                    status = when {
                        root == activeRoot && index == stack.lastIndex -> ChildNavState.Status.RESUMED
                        else -> ChildNavState.Status.CREATED
                    },
                )
            }
        }
    }
}

/**
 * 构建初始 [MultiStackNavState]，每个已注册标签页一个根条目，[initialRoot] 活动。
 * 当活动没有保存状态需要恢复时使用一次。
 */
internal fun multiStackInitialState(navRoots: Set<NavRoot>, initialRoot: NavRoot): MultiStackNavState =
    MultiStackNavState(
        activeRoot = initialRoot,
        tabStacks = navRoots.associateWith { listOf(it as BaseRoute) },
    )

/**
 * 将 [event] 应用于 [state] 并返回下一个 [MultiStackNavState]。
 *
 * Push、pop 和 dedupe 事件仅针对活动标签页的栈。
 * [MultiStackNavEvent.SwitchTab] 和 [MultiStackNavEvent.ReplaceAll] 更改哪个标签页活动并可能重置栈。
 * 未注册的根节点在此层抛出，以便导航器在接近调用站点的地方暴露配置错误。
 */
internal fun multiStackNavTransformer(
    state: MultiStackNavState,
    event: MultiStackNavEvent,
): MultiStackNavState = when (event) {
    is MultiStackNavEvent.Push -> state.withActiveStack { stack ->
        if (stack.lastOrNull() == event.route) stack else stack + (event.route as BaseRoute)
    }

    is MultiStackNavEvent.BringToFront -> state.withActiveStack { stack ->
        stack.filterNot { it::class == event.route::class } + (event.route as BaseRoute)
    }

    is MultiStackNavEvent.PushToFront -> state.withActiveStack { stack ->
        stack.filterNot { it == event.route } + (event.route as BaseRoute)
    }

    is MultiStackNavEvent.Pop -> state.withActiveStack { stack ->
        when {
            event.toIndex < 0 -> stack.dropLast(1).ifEmpty { stack }
            event.toIndex < stack.size -> stack.take(event.toIndex + 1)
            else -> stack
        }
    }

    is MultiStackNavEvent.PopUntilType -> state.withActiveStack { stack ->
        val targetIndex = stack.indexOfLast { event.routeClass.isInstance(it) }
        if (targetIndex < 0) {
            stack
        } else {
            val keep = if (event.inclusive) targetIndex else targetIndex + 1
            if (keep <= 0) stack else stack.take(keep)
        }
    }

    is MultiStackNavEvent.SwitchTab -> {
        require(event.root in state.tabStacks) {
            "NavRoot ${event.root} is not registered. Add it to Set<NavRoot>."
        }
        val newStacks = if (event.resetStack) {
            state.tabStacks + (event.root to listOf(event.root as BaseRoute))
        } else {
            state.tabStacks
        }
        state.copy(activeRoot = event.root, tabStacks = newStacks)
    }

    is MultiStackNavEvent.ReplaceAll -> {
        require(event.root in state.tabStacks) {
            "NavRoot ${event.root} is not registered. Add it to Set<NavRoot>."
        }
        val resetStacks = state.tabStacks.mapValues { (root, _) -> listOf(root as BaseRoute) }
        state.copy(activeRoot = event.root, tabStacks = resetStacks)
    }
}

private inline fun MultiStackNavState.withActiveStack(
    transform: (List<BaseRoute>) -> List<BaseRoute>,
): MultiStackNavState {
    val current = tabStacks[activeRoot] ?: error("Active root $activeRoot missing from tabStacks")
    val next = transform(current)
    if (next == current) return this
    return copy(tabStacks = tabStacks + (activeRoot to next))
}

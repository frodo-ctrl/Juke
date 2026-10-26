package com.thomaskioko.tvmaniac.navigation

import kotlin.reflect.KClass

/**
 * [DefaultNavigator] 多栈导航源的事件语言。
 *
 * 公共 [Navigator] 方法将一次调用转换为一个 [MultiStackNavEvent] 并发布到
 * `SimpleNavigation<MultiStackNavEvent>`。然后 Decompose 针对当前 [MultiStackNavState]
 * 运行 [multiStackNavTransformer] 以计算下一个状态。保持事件语言为 sealed
 * 允许转换器穷举匹配每个变体，并拒绝任何没有伴随转换器分支的新事件。
 */
internal sealed interface MultiStackNavEvent {
    /**
     * 将 [route] 推送到活动标签页的栈上。当 [route] 已等于栈的顶部条目时忽略，
     * 这样快速双击不会排入重复的配置（Decompose 会拒绝）。
     */
    data class Push(val route: NavRoute) : MultiStackNavEvent

    /** 将 [route] 类的任何现有实例带到顶部，或如果不存在则推送 [route]。 */
    data class BringToFront(val route: NavRoute) : MultiStackNavEvent

    /** 将 [route] 推送到顶部，仅移除与 [route] 完全相等的条目。 */
    data class PushToFront(val route: NavRoute) : MultiStackNavEvent

    /**
     * 从活动栈顶部弹出条目。默认 [toIndex] 为 `-1` 时弹出一个条目。
     * 非负 [toIndex] 时弹出直到该索引处的条目位于顶部。
     */
    data class Pop(val toIndex: Int = -1) : MultiStackNavEvent

    /**
     * 从活动栈顶部弹出条目，直到类型为 [routeClass] 的最近条目位于顶部。
     * 如果 [inclusive] 为 `true`，该条目也会被弹出。
     */
    data class PopUntilType(
        val routeClass: KClass<out NavRoute>,
        val inclusive: Boolean,
    ) : MultiStackNavEvent

    /**
     * 将活动标签页切换为 [root]。如果 [resetStack] 为 `true`，目标标签页的栈被清除到其根条目。
     */
    data class SwitchTab(val root: NavRoot, val resetStack: Boolean) : MultiStackNavEvent

    /** 将每个标签页的栈重置为其根条目并激活 [root]。 */
    data class ReplaceAll(val root: NavRoot) : MultiStackNavEvent
}

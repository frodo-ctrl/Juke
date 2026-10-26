package com.thomaskioko.tvmaniac.navigation

/**
 * 标记作为覆盖层（模态 sheet）而非全屏栈条目激活的 [NavRoute] 子类型。
 *
 * [Navigator.navigateTo] 在运行时检查路由的类型。实现 [OverlayRoute] 的路由
 * 分发到覆盖层插槽；其他所有路由分发到活动根节点的返回栈。
 * 该标记将覆盖层路由保持在 [Navigator] 内部，而不将公共方法拆分为两个。
 */
public interface OverlayRoute : NavRoute

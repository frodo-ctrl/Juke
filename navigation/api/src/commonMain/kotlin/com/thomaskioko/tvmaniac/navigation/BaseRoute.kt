package com.thomaskioko.tvmaniac.navigation

/**
 * 多栈模型中每个导航目标的公共父类。
 *
 * 两个直接子类型按角色拆分导航表面：[NavRoute] 用于位于返回栈内的栈条目，
 * [NavRoot] 用于锚定每个返回栈的顶层目标（通常是四个底部标签页根节点）。
 * [Navigator] 使用类型区分将屏幕推送（[NavRoute]）和标签页切换（[NavRoot]）
 * 分发到不同的内部源。
 *
 * 为 sealed，因为框架拥有这两个子类型；功能 `:nav` 模块直接实现 [NavRoute]、
 * [NavRoot] 或 [OverlayRoute]，从不实现 [BaseRoute]。
 */
public sealed interface BaseRoute

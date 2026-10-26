package com.thomaskioko.tvmaniac.navigation

/**
 * 将功能 presenter 包装为模态 sheet 插槽中的 [SheetChild]。
 *
 * [ScreenDestination] 的 sheet 对应物。具体的 presenter 类型 [T]
 * 仅在创建站点（功能的 [NavDestination.Overlay]）和消费站点（平台 UI）处已知，
 * 因此 `navigation/api` 不包含 presenter 特定的类型。
 *
 * @param T 此 sheet 目标持有的 presenter 类型。
 * @property presenter 由平台 UI 渲染的 sheet presenter。
 */
public class SheetDestination<out T : Any>(public val presenter: T) : SheetChild

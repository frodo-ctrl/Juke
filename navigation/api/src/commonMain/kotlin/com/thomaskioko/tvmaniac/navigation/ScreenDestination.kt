package com.thomaskioko.tvmaniac.navigation

/**
 * 将功能 presenter 包装为活动返回栈中的 [RootChild]。
 *
 * 一个泛型包装器避免了为每个功能创建子类（例如 `SettingsDestination`、`DebugDestination`），
 * 并使 `navigation/api` 不包含 presenter 特定的类型。具体的 presenter 类型 [T]
 * 仅在创建站点（功能的 [NavDestination]）和消费站点（平台 UI）处已知，
 * 后者对 [presenter] 进行模式匹配以渲染正确的屏幕。
 *
 * @param T 此目标持有的 presenter 类型。
 * @property presenter 由平台 UI 渲染的屏幕 presenter。
 */
public class ScreenDestination<out T : Any>(public val presenter: T) : RootChild

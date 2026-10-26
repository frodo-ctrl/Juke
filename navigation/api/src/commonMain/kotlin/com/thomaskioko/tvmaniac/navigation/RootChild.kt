package com.thomaskioko.tvmaniac.navigation

/**
 * 标记存储在根 Decompose 栈的每个条目中的值。
 *
 * 具体实现（最常见的是 [ScreenDestination]）包装功能 presenter，
 * 以便根 UI 可以对子类型进行模式匹配并渲染匹配的 composable 或 SwiftUI 视图。
 * 直接实现很少见；优先使用 [ScreenDestination]。
 */
public interface RootChild

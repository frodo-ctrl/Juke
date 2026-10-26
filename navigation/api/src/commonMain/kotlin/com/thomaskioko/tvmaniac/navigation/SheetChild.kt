package com.thomaskioko.tvmaniac.navigation

/**
 * 标记存储在根模态 sheet 插槽中的值。
 *
 * 单个 sheet 覆盖层的 [RootChild] 对应物。具体实现（最常见的是 [SheetDestination]）
 * 包装 sheet 的 presenter，根 UI 对子类型进行模式匹配以渲染匹配的 sheet 内容。
 */
public interface SheetChild

package com.thomaskioko.tvmaniac.navigation.ui

import androidx.compose.runtime.Composable
import com.thomaskioko.tvmaniac.navigation.SheetChild

/**
 * 根模态 sheet 插槽中 [SheetChild] 的平台侧渲染器。
 *
 * [ScreenContent] 的 sheet 对应物。每个拥有 sheet 的功能 `ui` 模块通过
 * `@ContributesTo(ActivityScope::class)` 贡献一个 [SheetContent]。
 * 根 Compose 消费者遍历注入的 `Set<SheetContent>`，选取第一个 [matches] 返回
 * `true` 的，并调用 [content] 渲染 sheet。
 *
 * @property matches 当此内容可以渲染给定子组件时返回 `true` 的谓词。
 * @property content 使用匹配的子组件调用的可组合项。
 */
public class SheetContent(
    public val matches: (SheetChild) -> Boolean,
    public val content: @Composable (SheetChild) -> Unit,
)

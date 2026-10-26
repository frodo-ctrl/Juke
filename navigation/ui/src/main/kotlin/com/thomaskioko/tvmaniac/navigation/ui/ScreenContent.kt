package com.thomaskioko.tvmaniac.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thomaskioko.tvmaniac.navigation.RootChild

/**
 * 根导航栈中 [RootChild] 的平台侧渲染器。
 *
 * 镜像 Compose UI 层的共享代码 [com.thomaskioko.tvmaniac.navigation.NavDestination] 多绑定。
 * 每个功能 `ui` 模块通过带 `@Provides @IntoSet` 的 `@ContributesTo(ActivityScope::class)`
 * 贡献一个 [ScreenContent]。根 Compose 栈遍历注入的 `Set<ScreenContent>`，
 * 选取第一个 [matches] 返回 `true` 的，并调用 [content] 渲染屏幕。
 * 这使 `features/root/ui` 不包含功能特定的导入。
 *
 * @property matches 当此内容可以渲染给定子组件时返回 `true` 的谓词。
 * @property content 使用匹配的子组件和布局 [Modifier] 调用的可组合项。
 */
public class ScreenContent(
    public val matches: (RootChild) -> Boolean,
    public val content: @Composable (RootChild, Modifier) -> Unit,
)

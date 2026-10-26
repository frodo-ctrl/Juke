package com.thomaskioko.tvmaniac.navigation

/**
 * 将功能的 `@Serializable` 路由类标记为返回栈条目。
 *
 * 每个路由类通过 [NavRouteBinding] 注册为 Metro 多绑定，以便 Decompose 栈
 * 多态地序列化和反序列化路由，而无需集中的 sealed 层次结构。
 * 添加新路由只需修改功能自己的 `nav` 模块及其 `NavDestination` 贡献；
 * `navigation/api` 和根 presenter 保持不变。
 *
 * 与 [NavRoot] 一起扩展 [BaseRoute]。充当覆盖层的路由还实现 [OverlayRoute]，
 * 以便 [Navigator.navigateTo] 将它们分发到覆盖层插槽而不是活动返回栈。
 */
public interface NavRoute : BaseRoute

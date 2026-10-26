package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * 表示来自另一个目标的待处理结果请求。
 *
 * 通过注入 [NavigationResultRegistry] 并从源屏幕调用 [registerForNavigationResult]
 * 来创建一个。将 [key] 传入目标路由，以便目标可以将投递匹配回此请求。
 *
 * @param R 目标目标期望的结果类型。
 * @property key 嵌入在目标路由中以匹配投递的标识符。
 * @property results 发出每个投递结果的 flow。
 */
public class NavigationResultRequest<R : Any> @PublishedApi internal constructor(
    public val key: Key<R>,
    public val results: Flow<R>,
) {
    /**
     * 待处理导航结果的标识符。该 key 是 [Serializable] 的，因此可以嵌入在
     * [NavRoute] 中并通过保存/恢复往返传递。
     */
    @Serializable
    public data class Key<R : Any>(
        internal val ownerRouteQualifiedName: String,
        internal val resultQualifiedName: String,
    )
}

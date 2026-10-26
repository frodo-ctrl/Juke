@file:Suppress("NOTHING_TO_INLINE")

package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

/**
 * 创建一个连接了给定 [fetcher] 和 [sourceOfTruth] 的 [StoreBuilder]。
 *
 * @param fetcher 按 [Key] 获取 [Local] 值的网络数据源。
 * @param sourceOfTruth 按 [Key] 读取 [Output] 并写入 [Local] 的本地持久化层。
 * @return 可进一步配置（例如验证、缓存策略）的 [StoreBuilder]。
 */
public inline fun <Key : Any, Local : Any, Output : Any> storeBuilder(
    fetcher: Fetcher<Key, Local>,
    sourceOfTruth: SourceOfTruth<Key, Local, Output>,
): StoreBuilder<Key, Output> = StoreBuilder.from(fetcher, sourceOfTruth)

/**
 * 创建将 [ApiResponse] 映射到 [FetcherResult] 的 [Fetcher]。
 *
 * - [ApiResponse.Success] → [FetcherResult.Data]
 * - [ApiResponse.Unauthenticated] → 带有 [AuthenticationException] 的 [FetcherResult.Error.Exception]
 * - 其他错误变体 → 带有描述性字符串的 [FetcherResult.Error.Message]
 *
 * @param apiCall 为给定 [Key] 执行网络请求的挂起函数。
 */
public inline fun <Key : Any, reified Output : Any> apiFetcher(
    crossinline apiCall: suspend (Key) -> ApiResponse<Output>,
): Fetcher<Key, Output> = Fetcher.ofResult { key: Key ->
    when (val response = apiCall(key)) {
        is ApiResponse.Success -> FetcherResult.Data(response.body)
        is ApiResponse.Unauthenticated -> FetcherResult.Error.Exception(
            AuthenticationException("Not authenticated"),
        )
        is ApiResponse.Error -> FetcherResult.Error.Exception(SyncException(response.toSyncError()))
    }
}

/**
 * 调用 [Store.get] 同时静默忽略 [AuthenticationException]。
 *
 * 处理用户未登录且端点需要认证的情况。请求被跳过作为无操作，而不是显示错误。
 *
 * 注意：已认证用户的 token 过期由上游活动提供者的 Ktor Auth 插件处理，
 * 它刷新 token 并向表现层发出会话过期错误。
 */
public suspend fun <Key : Any, Output : Any> Store<Key, Output>.get(key: Key) {
    try {
        get(key)
    } catch (_: AuthenticationException) {
    }
}

/**
 * 调用 [Store.fresh]（绕过缓存）同时静默忽略 [AuthenticationException]。
 *
 * 处理用户未登录且端点需要认证的情况。请求被跳过作为无操作，而不是显示错误。
 *
 * 注意：已认证用户的 token 过期由上游活动提供者的 Ktor Auth 插件处理，
 * 它刷新 token 并向表现层发出会话过期错误。
 */
public suspend fun <Key : Any, Output : Any> Store<Key, Output>.fresh(key: Key) {
    try {
        fresh(key)
    } catch (_: AuthenticationException) {
    }
}

/**
 * 包装此 [SourceOfTruth]，使读取在 [readDispatcher] 上运行，
 * 写入（包括删除）在 [writeDispatcher] 上执行。
 *
 * @param readDispatcher 用于 [SourceOfTruth.reader] 发射的调度器。
 * @param writeDispatcher 用于 [SourceOfTruth.write]、[SourceOfTruth.delete]
 *   和 [SourceOfTruth.deleteAll] 的调度器。
 */
public fun <Key : Any, Local : Any, Output : Any> SourceOfTruth<Key, Local, Output>.usingDispatchers(
    readDispatcher: CoroutineDispatcher,
    writeDispatcher: CoroutineDispatcher,
): SourceOfTruth<Key, Local, Output> {
    val wrapped = this
    return object : SourceOfTruth<Key, Local, Output> {
        override fun reader(key: Key): Flow<Output?> = wrapped.reader(key).flowOn(readDispatcher)

        override suspend fun write(key: Key, value: Local) = withContext(writeDispatcher) {
            wrapped.write(key, value)
        }

        override suspend fun delete(key: Key) = withContext(writeDispatcher) {
            wrapped.delete(key)
        }

        override suspend fun deleteAll() = withContext(writeDispatcher) {
            wrapped.deleteAll()
        }
    }
}

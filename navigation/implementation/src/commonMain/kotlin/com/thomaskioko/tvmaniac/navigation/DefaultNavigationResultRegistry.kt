package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * 内存中的 [NavigationResultRegistry] 实现。
 *
 * 每个 [NavigationResultRequest.Key] 由一个容量为 1 且具有 [BufferOverflow.DROP_OLDEST]
 * 语义的 [Channel] 支持：新的投递会替换同一 key 未消费的先前值，
 * 第一个收集器恰好消费该值一次。每个 key 仅支持一个逻辑收集器，
 * 这与每个导航目标的生命周期一致。
 *
 * registry 是 activity 作用域的，预期从主线程访问，这与导航调用的来源一致。
 * key 映射未应用显式同步。
 */
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavigationResultRegistry : NavigationResultRegistry {

    private val channels = mutableMapOf<NavigationResultRequest.Key<*>, Channel<Any>>()

    override fun <R : Any> register(key: NavigationResultRequest.Key<R>): Flow<R> {
        @Suppress("UNCHECKED_CAST")
        return channelFor(key).receiveAsFlow() as Flow<R>
    }

    override fun <R : Any> deliver(key: NavigationResultRequest.Key<R>, result: R) {
        channelFor(key).trySend(result)
    }

    private fun channelFor(key: NavigationResultRequest.Key<*>): Channel<Any> =
        channels.getOrPut(key) {
            Channel(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        }
}

package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Firebase 实时监听器写入和 [com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig]
 * 读取的共享内存缓存。以 Remote Config key 为键（匹配底层 Firebase 配置）。
 * 源默认为 [FeatureFlagSource.Firebase]；`DebugRemoteConfig` 装饰器在覆盖激活时
 * 叠加 [FeatureFlagSource.Local]。
 *
 * 在应用作用域构造一次；`AndroidRemoteConfig` 和 `IosRemoteConfig` 在每个平台图中
 * 共享同一实例。
 */
@SingleIn(AppScope::class)
@Inject
public class RemoteConfigState {

    private val valuesFlow: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(emptyMap())

    public fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        valuesFlow
            .map { it[key] ?: default }
            .distinctUntilChanged()

    public fun observeSource(key: String): Flow<FeatureFlagSource> = flowOf(FeatureFlagSource.Firebase)

    public fun update(values: Map<String, Boolean>) {
        valuesFlow.update { it + values }
    }
}

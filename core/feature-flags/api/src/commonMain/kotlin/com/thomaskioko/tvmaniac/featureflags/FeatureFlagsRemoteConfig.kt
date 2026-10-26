package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow

public interface FeatureFlagsRemoteConfig {

    /**
     * 观察 [key] 的布尔值。在 Firebase 返回显式值之前发出 [default]。
     * 当实时监听器推送新值时更新 flow。
     */
    public fun observeBoolean(key: String, default: Boolean): Flow<Boolean>

    /**
     * 观察 [key] 当前值的来源。生产实现发出 [FeatureFlagSource.Firebase]；
     * 调试装饰器在覆盖激活时发出 [FeatureFlagSource.Local]。
     */
    public fun observeSource(key: String): Flow<FeatureFlagSource>

    /**
     * 获取 Firebase Remote Config 并激活结果。由调试屏幕的强制刷新操作手动触发。
     * 实时监听器会自动接收服务端更改，无需调用此方法。
     */
    public suspend fun refresh()

    /**
     * 从 [defaults] 种子 Firebase Remote Config 默认值。在应用启动时调用一次，
     * 传入每个 [RemoteFlag] 的 `defaultValue` 的并集。
     */
    public suspend fun setDefaults(defaults: Map<String, Boolean>)
}

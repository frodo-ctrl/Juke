package com.thomaskioko.tvmaniac.featureflags

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * 存储以 Firebase Remote Config key 为键的 debug 构建本地覆盖。
 * 跨进程重启持久化（DataStore 支持）。仅由 `DebugRemoteConfig` 装饰器使用；
 * 生产构建永远不会通过此接口读取或写入。
 *
 * 存储对值类型是泛型的，因此未来的非布尔 flag 层级可以使用相同的存储而无需重新连接 API。
 * 目前仅使用 `Boolean` 值。
 */
public interface FeatureFlagLocalStore {

    /**
     * 观察 [key] 的本地覆盖。在为相同 [key] 调用 [set] 并写入匹配 [type] 的值之前，
     * 发出 null。
     */
    public fun <T : Any> observe(key: String, type: KClass<T>): Flow<T?>

    /**
     * 观察存储的覆盖条目全集。由调试屏幕用于显示哪些 flag 当前带有本地覆盖。
     */
    public fun observeAll(): Flow<Map<String, Any>>

    /** 为 [key] 写入本地覆盖。跨进程重启持久化。 */
    public suspend fun <T : Any> set(key: String, value: T)

    /** 移除 [key] 的本地覆盖，将读取恢复到生产的 [FeatureFlagsRemoteConfig]。 */
    public suspend fun clear(key: String)

    /** 移除每个本地覆盖。由调试屏幕的"全部重置"操作触发。 */
    public suspend fun clearAll()
}

/** 便利重载，为读取已知值类型的调用方具体化 [T]。 */
public inline fun <reified T : Any> FeatureFlagLocalStore.observe(key: String): Flow<T?> =
    observe(key, T::class)

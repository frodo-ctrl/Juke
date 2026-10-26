package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * 功能 flag 的类型安全句柄。消费者注入 `@QualifierAnnotation FeatureFlag<T>`，
 * 其中限定注解标识哪个 flag，`T` 是值类型（目前只有 `Boolean`）。
 * 绑定位于实现模块的 `FlagBindings` 接口中；调试屏幕通过随每个限定绑定一起贡献的
 * `Set<FeatureFlag<Boolean>>` 多绑定遍历相同的 flag。
 *
 * @property key Firebase Remote Config key。驱动 Firebase 查找和调试存储覆盖。
 * @property title 人类可读名称。显示在调试屏幕行上。
 * @property description 单行摘要。显示在调试屏幕标题下方。
 * @property dateAdded flag 进入代码库的日期。驱动调试屏幕的"添加日期"排序。
 */
public interface FeatureFlag<T> {
    public val key: String
    public val title: String
    public val description: String
    public val dateAdded: LocalDate

    /**
     * 观察 flag 的当前值。发出来自 Firebase 实时监听器的更新，
     * 以及在 debug 构建中来自本地覆盖的更新。
     */
    public fun observe(): Flow<T>

    /**
     * 观察当前值的来源。当调试覆盖激活时发出 [FeatureFlagSource.Local]，
     * 否则发出 [FeatureFlagSource.Firebase]。
     */
    public fun observeSource(): Flow<FeatureFlagSource>
}

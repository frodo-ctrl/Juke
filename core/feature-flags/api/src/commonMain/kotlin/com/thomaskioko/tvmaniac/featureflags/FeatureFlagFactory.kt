package com.thomaskioko.tvmaniac.featureflags

import kotlinx.datetime.LocalDate

/**
 * 在构建时创建 [FeatureFlag] 实例。具体绑定在 `@Provides` 函数内调用工厂方法
 * 来组装每个开关的元数据，并将其连接到底层的远程配置提供者。
 *
 * 目前只有一个方法；额外的方法（`enum`、`integer`、`string`）会在
 * 第一个需要它们的开关出现时添加。
 */
public interface FeatureFlagFactory {

    /**
     * 创建一个由 [FeatureFlagsRemoteConfig] 支持的 `Boolean` 类型开关。
     *
     * @param key Firebase Remote Config 键。同时驱动 Firebase 查询和调试存储覆盖。
     * @param title 显示在调试屏幕行上的可读名称。
     * @param description 显示在调试屏幕标题下方的一行摘要。
     * @param defaultValue 在 Firebase 提供明确值之前返回的回退值。
     * @param dateAdded 开关进入代码库的日期。驱动调试屏幕的"添加日期"排序。
     */
    public fun boolean(
        key: String,
        title: String,
        description: String,
        defaultValue: Boolean,
        dateAdded: LocalDate,
    ): FeatureFlag<Boolean>
}

package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * 由 Firebase Remote Config 支持的 flag 的基类。[observe] 委托给
 * [FeatureFlagsRemoteConfig.observeBoolean]，[observeSource] 委托给
 * [FeatureFlagsRemoteConfig.observeSource]。本地 debug 构建覆盖由实现模块中的
 * `DebugRemoteConfig` 装饰器分层；此类本身不依赖 `DebugConfig` 和 `FeatureFlagLocalStore`。
 *
 * 实例由 [FeatureFlagFactory.boolean] 生成，从实现模块 `FlagBindings` 接口中的
 * `@Provides` 函数调用。作者不应在功能代码中直接子类化 [RemoteFlag]；
 * 在 `flags/` 下声明限定注解并将绑定对添加到 `FlagBindings` 即可。
 *
 * @param key Firebase Remote Config key。
 * @param title 调试屏幕行上显示的人类可读名称。
 * @param description 标题下方显示的单行摘要。
 * @param dateAdded flag 进入代码库的日期。
 * @param defaultValue 在 Firebase 返回显式值之前回退的默认值。
 * @param remote 通过 Metro 注入的共享 Firebase Remote Config 提供者。
 */
public abstract class RemoteFlag(
    override val key: String,
    override val title: String,
    override val description: String,
    override val dateAdded: LocalDate,
    public val defaultValue: Boolean,
    private val remote: FeatureFlagsRemoteConfig,
) : FeatureFlag<Boolean> {

    override fun observe(): Flow<Boolean> = remote.observeBoolean(key, defaultValue)

    override fun observeSource(): Flow<FeatureFlagSource> = remote.observeSource(key)
}

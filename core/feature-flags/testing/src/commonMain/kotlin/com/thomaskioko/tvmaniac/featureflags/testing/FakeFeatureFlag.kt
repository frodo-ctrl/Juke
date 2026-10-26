package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate

/**
 * 单元测试用的内存 [FeatureFlag]。持有单个可变 [value] 和可编辑的源。
 * 测试通过赋值 `value = true` 来翻转 flag，并调用 [setSource] 来模拟调试覆盖处于活动状态。
 * 跳过 `FeatureFlagsRemoteConfig` / `DefaultFeatureFlagFactory` 的间接层，
 * 以便 presenter 和 interactor 测试专注于消费者的分支逻辑。
 *
 * 对于端到端测试调试覆盖装饰器的测试，应改为构造 [FakeFeatureFlagsRemoteConfig]，
 * 并让生产绑定容器解析该 flag。
 *
 * @param initial 起始值。在重新赋值 [value] 之前由 [observe] 读取。
 * @param key Firebase Remote Config key。已设置默认值；当测试断言渲染行时覆盖。
 * @param title 人类可读名称。已设置默认值；当测试断言渲染行时覆盖。
 * @param description 单行摘要。已设置默认值；当测试断言渲染行时覆盖。
 * @param dateAdded flag 进入代码库的日期。已设置默认值；当测试断言排序顺序时覆盖。
 */
public class FakeFeatureFlag<T>(
    initial: T,
    override val key: String = "fake_flag",
    override val title: String = "Fake",
    override val description: String = "Fake flag for tests.",
    override val dateAdded: LocalDate = LocalDate(2026, 1, 1),
) : FeatureFlag<T> {

    private val valueFlow: MutableStateFlow<T> = MutableStateFlow(initial)
    private val sourceFlow: MutableStateFlow<FeatureFlagSource> =
        MutableStateFlow(FeatureFlagSource.Firebase)

    /**
     * 当前值。重新赋值以翻转 flag；观察者发出新值。
     * */
    public var value: T
        get() = valueFlow.value
        set(newValue) {
            valueFlow.value = newValue
        }

    /**
     * 设置 [observeSource] 发出的源。默认为 [FeatureFlagSource.Firebase]。
     * */
    public fun setSource(source: FeatureFlagSource) {
        sourceFlow.value = source
    }

    override fun observe(): Flow<T> = valueFlow

    override fun observeSource(): Flow<FeatureFlagSource> = sourceFlow
}

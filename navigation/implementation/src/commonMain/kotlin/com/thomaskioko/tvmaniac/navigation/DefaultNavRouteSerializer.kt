package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.essenty.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.essenty.statekeeper.polymorphicSerializer
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.reflect.KClass

/**
 * 将每个在 [ActivityScope] 贡献的 [NavRouteBinding] 组合成一个
 * [NavRoute] 的多态 [KSerializer]。
 *
 * Decompose 的每标签页 `childStack(serializer = ...)` 使用结果，以便返回栈
 * 在配置更改和进程死亡后仍然存活。构造函数验证至少存在一个绑定，
 * 且没有 [NavRoute] 子类贡献超过一次。验证在构造时运行，
 * 以便配置错误的图在任何导航发生之前失败。
 *
 * @param bindings 每个已注册 [NavRoute] 子类的多态序列化器条目。
 */
@OptIn(ExperimentalStateKeeperApi::class, ExperimentalSerializationApi::class)
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavRouteSerializer(
    bindings: Set<NavRouteBinding<*>>,
) : NavRouteSerializer {
    init {
        require(bindings.isNotEmpty()) {
            "DefaultNavRouteSerializer requires at least one NavRouteBinding. " +
                "Contribute via @IntoSet on Set<NavRouteBinding<*>>."
        }
        val duplicates = bindings.map { it.kClass }
            .groupingBy { it }.eachCount()
            .filterValues { it > 1 }
            .keys
        require(duplicates.isEmpty()) {
            "Duplicate NavRouteBindings for: ${duplicates.map { it.simpleName }}. " +
                "Each NavRoute class may only contribute one binding."
        }
    }

    private val module: SerializersModule = SerializersModule {
        polymorphic(NavRoute::class) {
            bindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<NavRoute>,
                    binding.serializer as KSerializer<NavRoute>,
                )
            }
        }
    }

    override val serializer: KSerializer<NavRoute> = polymorphicSerializer(
        baseClass = NavRoute::class,
        module = module,
    )
}

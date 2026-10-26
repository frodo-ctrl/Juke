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
 * 将每个在 [ActivityScope] 贡献的 [NavRouteBinding] 和 [NavRootBinding] 组合成一个
 * [BaseRoute] 的多态 [KSerializer]。
 *
 * 当每个标签页的返回栈在底部混合 [NavRoot]、在顶部混合 [NavRoute] 条目时，
 * Decompose 的 `childStack(serializer = ...)` 使用生成的序列化器。
 * 构造函数验证三个不变量：至少存在一个绑定，每个贡献的 [NavRoot] 也有匹配的
 * [NavRootBinding]，并且没有路由或根类贡献多个绑定。
 * 验证在构造时运行一次，以便配置错误的图在启动时失败，而不是在第一个导航事件时失败。
 *
 * @param routeBindings 每个 [NavRoute] 子类的多态序列化器条目。
 * @param rootBindings 每个 [NavRoot] 子类的多态序列化器条目。
 * @param navRoots 通过导航多绑定 set 注册的标签页锚点；用于验证每个都有匹配的
 *   [NavRootBinding]。
 */
@OptIn(ExperimentalStateKeeperApi::class, ExperimentalSerializationApi::class)
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultBaseRouteSerializer(
    routeBindings: Set<NavRouteBinding<*>>,
    rootBindings: Set<NavRootBinding<*>>,
    navRoots: Set<NavRoot>,
) : BaseRouteSerializer {
    init {
        require(routeBindings.isNotEmpty() || rootBindings.isNotEmpty()) {
            "BaseRoute serializer requires at least one NavRouteBinding or NavRootBinding. " +
                "Contribute via @IntoSet on Set<NavRouteBinding<*>> or Set<NavRootBinding<*>>."
        }

        val rootBindingClasses = rootBindings.mapTo(mutableSetOf()) { it.kClass }
        val missingRootBindings = navRoots.mapTo(mutableSetOf()) { it::class } - rootBindingClasses
        require(missingRootBindings.isEmpty()) {
            "Missing NavRootBinding for: ${missingRootBindings.map { it.simpleName }}. " +
                "Each NavRoot contributed to Set<NavRoot> must also contribute a NavRootBinding " +
                "via @IntoSet on Set<NavRootBinding<*>>."
        }

        val allBindingClasses = routeBindings.map { it.kClass } + rootBindings.map { it.kClass }
        val duplicates = allBindingClasses.groupingBy { it }.eachCount()
            .filterValues { it > 1 }
            .keys
        require(duplicates.isEmpty()) {
            "Duplicate BaseRoute bindings registered for: ${duplicates.map { it.simpleName }}. " +
                "Each route or root class may only contribute one binding."
        }
    }

    private val module: SerializersModule = SerializersModule {
        polymorphic(BaseRoute::class) {
            routeBindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<BaseRoute>,
                    binding.serializer as KSerializer<BaseRoute>,
                )
            }
            rootBindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<BaseRoute>,
                    binding.serializer as KSerializer<BaseRoute>,
                )
            }
        }
    }

    override val serializer: KSerializer<BaseRoute> = polymorphicSerializer(
        baseClass = BaseRoute::class,
        module = module,
    )
}

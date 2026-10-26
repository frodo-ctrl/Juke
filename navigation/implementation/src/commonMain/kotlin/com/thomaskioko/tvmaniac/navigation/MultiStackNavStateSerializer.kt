package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * 使用通过导航多绑定 set 贡献的多态 [BaseRoute] 和 [NavRoot] 序列化器
 * 序列化 [MultiStackNavState]。
 *
 * Decompose 在 activity 被销毁时调用 [serialize]，在恢复状态时调用 [deserialize]。
 * 活动根节点和每个标签页的返回栈被编码为两个字段；标签页内的条目顺序由
 * [ListSerializer] 保留，标签页顺序由 [MapSerializer] 保留。
 *
 * @param baseRouteSerializer 用于单个栈条目的聚合路由和根序列化器。
 * @param navRootSerializer 用于活动标签页锚点和标签页键的序列化器。
 */
internal class MultiStackNavStateSerializer(
    baseRouteSerializer: KSerializer<BaseRoute>,
    private val navRootSerializer: KSerializer<NavRoot>,
) : KSerializer<MultiStackNavState> {

    private val tabStacksSerializer: KSerializer<Map<NavRoot, List<BaseRoute>>> =
        MapSerializer(navRootSerializer, ListSerializer(baseRouteSerializer))

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MultiStackNavState") {
        element("activeRoot", navRootSerializer.descriptor)
        element("tabStacks", tabStacksSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MultiStackNavState) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, navRootSerializer, value.activeRoot)
            encodeSerializableElement(descriptor, 1, tabStacksSerializer, value.tabStacks)
        }
    }

    override fun deserialize(decoder: Decoder): MultiStackNavState =
        decoder.decodeStructure(descriptor) {
            var activeRoot: NavRoot? = null
            var tabStacks: Map<NavRoot, List<BaseRoute>>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> activeRoot = decodeSerializableElement(descriptor, 0, navRootSerializer)
                    1 -> tabStacks = decodeSerializableElement(descriptor, 1, tabStacksSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected serial element index: $index")
                }
            }
            MultiStackNavState(
                activeRoot = requireNotNull(activeRoot) { "activeRoot missing from saved state" },
                tabStacks = requireNotNull(tabStacks) { "tabStacks missing from saved state" },
            )
        }
}

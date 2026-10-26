package com.thomaskioko.tvmaniac.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

/**
 * 计算使 lazy 列表中的下一项部分可见所需的滚动偏移量。
 *
 * 此函数用于创建"窥视"效果，让下一项或上一项对用户略微可见，
 * 表明还有更多内容可以滚动。
 *
 * @param itemWidth lazy 列表中单个项的宽度。
 * @param itemSpacing lazy 列表中各项之间的间距。默认为 0.dp。
 * @param visibleFraction 下一项应该可见的比例。
 *   值为 0.1f 表示下一项的 10% 将可见。默认为 0.1f。
 * @return 计算出的滚动偏移量（像素）。此值可与 `LazyListState.scrollToItem()`
 *   配合使用以正确定位列表。
 */
@Composable
public fun calculateScrollOffset(
    itemWidth: Dp,
    itemSpacing: Dp = TvManiacSpacing.none,
    visibleFraction: Float = 0.1f,
): Int {
    val density = LocalDensity.current
    val totalItemWidth = with(density) { (itemWidth + itemSpacing).roundToPx() }
    return (totalItemWidth * (1f - visibleFraction)).toInt()
}

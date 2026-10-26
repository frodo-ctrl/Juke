package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.thomaskioko.tvmaniac.compose.util.LocalHapticFeedbackEnabled

/**
 * 可点击的 [Row]，拥有其长按功能的触觉脉冲，因此调用站点传递普通的操作 lambda
 * 而不是自己连接触觉。普通点击保持静音（行通常导航）；
 * [onLongClick] 在运行前触发门控脉冲。
 *
 * [hapticEnabled] 门控脉冲，默认为环境 [LocalHapticFeedbackEnabled]。
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun HapticRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    hapticEnabled: Boolean = LocalHapticFeedbackEnabled.current,
    content: @Composable RowScope.() -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick?.let { longClick ->
                {
                    if (hapticEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    longClick()
                }
            },
        ),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        content = content,
    )
}

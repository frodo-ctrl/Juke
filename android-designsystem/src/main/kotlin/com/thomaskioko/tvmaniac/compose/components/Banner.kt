package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

@Stable
public enum class BannerStyle {
    Error,
    Warning,
    Success,
    Info,
}

@Immutable
internal data class BannerColors(
    val container: Color,
    val content: Color,
)

@Composable
private fun BannerStyle.colors(): BannerColors = when (this) {
    BannerStyle.Error -> BannerColors(
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer,
    )
    BannerStyle.Warning -> BannerColors(
        container = Color(0xFFFB8C00),
        content = Color.White,
    )
    BannerStyle.Success -> BannerColors(
        container = Color(0xFF43A047),
        content = Color.White,
    )
    BannerStyle.Info -> BannerColors(
        container = Color(0xFF1E88E5),
        content = Color.White,
    )
}

/**
 * 持久可关闭的屏幕顶部横幅。边缘到边缘渲染，无圆角，
 * 因此看起来像系统 chrome 的一部分，并在视觉上与圆角内凹的
 * [TvManiacSnackBar] 保持区分。内部应用 [statusBarsPadding] 以便
 * 消费者不需要重新添加，并通过 [AnimatedVisibility] 进行进入/退出动画。
 *
 * 布局将消息行（文本、关闭按钮）堆叠在可选的右对齐 [action] 行上方。
 * `Error` 变体从 [MaterialTheme.colorScheme.errorContainer]/
 * [androidx.compose.material3.ColorScheme.onErrorContainer] 解析其调色板，
 * 因此适应每个活动主题；其他变体使用固定调色板。
 *
 * @param message 向用户显示的正文内容。
 * @param onDismiss 点击关闭图标时调用。
 * @param modifier 应用于外部动画容器的布局 modifier。
 * @param visible 驱动进入/退出动画。通过 `false` 隐藏以向上滑出横幅。
 * @param style 视觉变体。驱动背景和内容颜色。
 * @param dismissContentDescription 关闭图标的无障碍标签。
 * @param action 在消息下方渲染的可选尾部操作槽，右对齐。
 */
@Composable
public fun TvManiacBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    style: BannerStyle = BannerStyle.Info,
    dismissContentDescription: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = style.colors()
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
            slideInVertically(animationSpec = tween(durationMillis = 300), initialOffsetY = { -it }),
        exit = fadeOut(animationSpec = tween(durationMillis = 150)) +
            slideOutVertically(animationSpec = tween(durationMillis = 300), targetOffsetY = { -it }),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tvmaniac_banner"),
            shape = RectangleShape,
            color = colors.container,
            contentColor = colors.content,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(
                        start = TvManiacSpacing.medium,
                        end = TvManiacSpacing.xSmall,
                        top = TvManiacSpacing.small,
                        bottom = TvManiacSpacing.small,
                    ),
                verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = dismissContentDescription,
                        )
                    }
                }

                if (action != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        action()
                    }
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TvManiacBannerPreview(
    @PreviewParameter(BannerPreviewParameterProvider::class) param: BannerPreviewParam,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        TvManiacBanner(
            message = param.message,
            onDismiss = {},
            style = param.style,
            dismissContentDescription = "Dismiss",
            action = param.action,
        )
    }
}

internal data class BannerPreviewParam(
    val message: String,
    val style: BannerStyle,
    val action: (@Composable () -> Unit)? = null,
)

private class BannerPreviewParameterProvider : PreviewParameterProvider<BannerPreviewParam> {
    override val values: Sequence<BannerPreviewParam> = sequenceOf(
        BannerPreviewParam(
            message = "Your Trakt account is full. Upgrade to keep syncing new shows.",
            style = BannerStyle.Error,
            action = { Text("Upgrade") },
        ),
        BannerPreviewParam(
            message = "Your session is about to expire.",
            style = BannerStyle.Warning,
        ),
        BannerPreviewParam(
            message = "Library synced successfully.",
            style = BannerStyle.Success,
        ),
        BannerPreviewParam(
            message = "New episode notifications are now available.",
            style = BannerStyle.Info,
        ),
    )
}

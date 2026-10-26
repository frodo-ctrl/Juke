package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

/**
 * 一个为预览提供 [TvManiacTheme] 的 [PreviewWrapperProvider]。
 */
public class TvManiacPreviewWrapperProvider : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        TvManiacTheme {
            TvManiacBackground {
                content()
            }
        }
    }
}

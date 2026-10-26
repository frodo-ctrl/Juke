package com.thomaskioko.tvmaniac.featureflags.flags

import io.github.thomaskioko.codegen.annotations.FeatureFlag

/**
 * 继续观看 Nitro 开关。codegen 从此锚点发出 `ContinueWatchingNitroFlagQualifier` 和
 * `ContinueWatchingNitroFlagBinding`；消费者注入
 * `@ContinueWatchingNitroFlagQualifier FeatureFlag<Boolean>`。
 */
@FeatureFlag(
    key = "enable_continue_watching_nitro",
    title = "Progress Endpoint",
    description = "Use Trakt's internal /sync/progress/up_next_nitro call instead of the documented multi-step progress fetch.",
    defaultValue = false,
    dateAdded = "2026-05-20",
)
public object ContinueWatchingNitroFlag

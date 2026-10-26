package com.thomaskioko.tvmaniac.featureflags.flags

import io.github.thomaskioko.codegen.annotations.FeatureFlag

/**
 * 账户切换标志。代码生成器从此锚点发出 `AccountSwitchFlagQualifier` 和 `AccountSwitchFlagBinding`；
 * 消费者注入 `@AccountSwitchFlagQualifier FeatureFlag<Boolean>`。
 */
@FeatureFlag(
    key = "enable_account_switch",
    title = "Account Switch",
    description = "Show the button to switch the active provider on the settings account screen.",
    defaultValue = false,
    dateAdded = "2026-06-14",
)
public object AccountSwitchFlag

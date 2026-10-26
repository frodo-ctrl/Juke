package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * 操作系统触发后台任务之前必须满足的条件。
 *
 * @property requiresNetwork 当为 `true` 时，任务仅在任何网络连接
 *   （蜂窝或 Wi-Fi）可用时运行。
 * @property requiresUnmeteredNetwork 当为 `true` 时，任务仅在非计费
 *   （通常是 Wi-Fi）连接上运行。暗示 [requiresNetwork]。
 */
public data class TaskConstraints(
    val requiresNetwork: Boolean = false,
    val requiresUnmeteredNetwork: Boolean = false,
)

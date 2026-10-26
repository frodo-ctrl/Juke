package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * 描述要调度的周期性后台任务。
 *
 * Worker 通常暴露一个 companion 的 `REQUEST` 常量，以便所有调用站点
 * 共享相同的配置：
 * ```kotlin
 * companion object {
 *   const val WORKER_NAME = "com.thomaskioko.tvmaniac.sync.library"
 *   val REQUEST = PeriodicTaskRequest(
 *     id = WORKER_NAME,
 *     intervalMs = 6.hours.inWholeMilliseconds,
 *     constraints = TaskConstraints(requiresNetwork = true),
 *   )
 * }
 * ```
 *
 * @property id 唯一任务标识符。必须与应执行此请求的 worker 的
 *   [BackgroundWorker.workerName] 匹配。
 * @property intervalMs 两次执行之间的最小间隔（毫秒）。
 *   操作系统可能会根据系统条件延迟执行超过此间隔。
 *   在 Android 上，WorkManager 强制要求最少 15 分钟。
 * @property constraints 可选约束，必须在操作系统触发执行之前满足。
 * @property longRunning 当为 `true` 时，向操作系统请求延长执行时间。
 *   在 iOS 上使用 `BGProcessingTaskRequest`（分钟级运行时间，空闲时运行）
 *   代替 `BGAppRefreshTaskRequest`（约 30 秒）。在 Android 上，由于
 *   WorkManager 自动处理执行时间，因此是无操作。
 */
public data class PeriodicTaskRequest(
    val id: String,
    val intervalMs: Long,
    val constraints: TaskConstraints = TaskConstraints(),
    val longRunning: Boolean = false,
)

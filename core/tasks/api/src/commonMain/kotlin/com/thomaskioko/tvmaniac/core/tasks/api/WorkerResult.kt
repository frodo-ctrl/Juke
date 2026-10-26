package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * [BackgroundWorker.doWork] 执行的结果。
 *
 * 平台调度器将这些结果映射到其原生等价物
 * （例如 Android 上的 `ListenableWorker.Result`，iOS 上的 `BGTask.setTaskCompleted`）。
 */
public sealed class WorkerResult {

    /** 工作成功完成。 */
    public data object Success : WorkerResult()

    /** 工作暂时失败，应由操作系统重试。 */
    public data class Retry(val message: String? = null, val cause: Throwable? = null) : WorkerResult()

    /** 工作永久失败。操作系统不会自动重试。 */
    public data class Failure(val message: String? = null, val cause: Throwable? = null) : WorkerResult()
}

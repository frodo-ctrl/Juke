package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * 可由任务调度器执行的后台工作单元。
 *
 * Worker 包含在两个平台上运行的共享业务逻辑。
 * 每个 worker 通过 Metro 多绑定注册，并在运行时通过 [WorkerFactory] 按 [workerName] 查找。
 *
 * 要添加新的后台作业，请在 `commonMain` 中实现此接口并使用
 * `@ContributesIntoSet(AppScope::class)` 注解。
 */
public interface BackgroundWorker {

    /**
     * 此 worker 的唯一标识符。必须使用反向 DNS 格式
     * （例如 `com.thomaskioko.tvmaniac.sync.library`），并与相应的 iOS
     * `Info.plist` `BGTaskSchedulerPermittedIdentifiers` 条目匹配。
     */
    public val workerName: String

    /**
     * 执行后台工作并返回结果。
     *
     * @return [WorkerResult.Success] 如果工作完成，
     *   [WorkerResult.Retry] 如果工作稍后应重试，或
     *   [WorkerResult.Failure] 如果工作永久失败。
     */
    public suspend fun doWork(): WorkerResult
}

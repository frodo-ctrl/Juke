package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * 在运行时按名称解析 [BackgroundWorker] 实例。
 *
 * 平台调度器在操作系统触发后台执行时委托给此工厂。
 * 默认实现（[DefaultWorkerFactory]）使用 kotlin-inject 多绑定
 * 收集所有已注册的 worker，并通过 [BackgroundWorker.workerName] 查找它们。
 */
public interface WorkerFactory {

    public val workerNames: Set<String>

    /**
     * 返回注册在 [workerName] 下的 [BackgroundWorker]，如果没有匹配的 worker 则返回 `null`。
     */
    public fun createWorker(workerName: String): BackgroundWorker?
}

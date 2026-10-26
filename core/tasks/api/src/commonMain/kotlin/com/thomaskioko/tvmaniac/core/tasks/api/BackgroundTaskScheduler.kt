package com.thomaskioko.tvmaniac.core.tasks.api

public interface BackgroundTaskScheduler {

    /**
     * 向操作系统调度器注册周期性任务。不会立即执行任何工作；
     * 操作系统根据系统条件和请求的 [PeriodicTaskRequest.intervalMs] 决定何时触发任务。
     *
     * 在应用启动时使用它来确保后台作业已注册，而不会在启动时造成不必要的网络调用。
     *
     * 所有周期性 worker 都通过此入口点路由，并由平台特定的 funnel
     * （Android：`SchedulerDispatchWorker`；iOS：`IosTaskScheduler.executeWithinWindow`）分发。
     */
    public fun schedulePeriodic(request: PeriodicTaskRequest)

    /**
     * 注册周期性任务 **并** 立即运行它。用于用户发起的操作
     * （例如开启通知切换），用户期望立即结果以及未来的周期性执行。
     */
    public fun scheduleAndExecute(request: PeriodicTaskRequest)

    /**
     * 通过其唯一 [id] 取消之前调度的任务。
     *
     * @param id 调度时使用的 worker 名称 / 任务标识符。
     */
    public fun cancel(id: String)

    /**
     * 取消此调度器管理的所有已调度任务。
     */
    public fun cancelAll()

    /**
     * 将所有当前活动的周期性请求重新提交给操作系统调度器。
     *
     * 这仅在 iOS 上有意义，其中 `BGTaskScheduler` 要求每次应用进入后台时
     * 重新提交任务。默认实现对于不需要此行为的平台是无操作。
     */
    public fun rescheduleBackgroundTask() {}
}

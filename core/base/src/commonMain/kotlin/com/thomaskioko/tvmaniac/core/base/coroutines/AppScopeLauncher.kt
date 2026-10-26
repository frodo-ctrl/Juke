package com.thomaskioko.tvmaniac.core.base.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * 启动必须超出调用方协程作用域的工作。
 *
 * 用于由用户操作触发的后续同步工作（写入网络、刷新缓存），
 * 如果用户在启动的块完成之前导航离开，这些工作仍应完成。
 * 该块在会话作用域的 supervisor 上运行，因此取消调用方不会取消已启动的作业。
 *
 * 会话作用域，而非操作系统调度。登出和账户切换会取消任何进行中的工作
 * （参见 [SyncCoroutineScope]）；否则该作用域随进程消亡。对于必须在进程死亡后存活的工作，
 * 请改用 `core/tasks` 中的操作系统管理的调度器。
 *
 * launcher 捕获块内的任何失败，并在提供的 tag 下记录。
 * CancellationException 会被重新抛出（从不记录），因此进程关闭期间的结构化取消仍然有效。
 */
public interface AppScopeLauncher {

    public fun launch(
        tag: String,
        block: suspend CoroutineScope.() -> Unit,
    ): Job
}

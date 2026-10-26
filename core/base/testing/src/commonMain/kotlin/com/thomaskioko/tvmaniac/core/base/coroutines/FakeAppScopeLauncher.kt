package com.thomaskioko.tvmaniac.core.base.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * [AppScopeLauncher] 的测试 fake，在调用方提供的作用域上运行启动的块。
 *
 * 将测试的 `TestScope` 作为 [scope] 传入，以便启动的工作在与测试主体相同的
 * 调度器下执行，`runTest` 可以将其推进到完成。
 */
public class FakeAppScopeLauncher(
    private val scope: CoroutineScope,
) : AppScopeLauncher {

    private val recordedTags = mutableListOf<String>()
    private val recordedThrowables = mutableListOf<Throwable>()

    public val launches: List<String> get() = recordedTags.toList()

    public val thrown: List<Throwable> get() = recordedThrowables.toList()

    override fun launch(
        tag: String,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        recordedTags += tag
        return scope.launch {
            try {
                block()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                recordedThrowables += throwable
            }
        }
    }
}

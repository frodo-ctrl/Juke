package com.thomaskioko.tvmaniac.core.base.extensions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.thomaskioko.tvmaniac.core.base.coroutines.CoroutineCrashUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * 此辅助实现来自 Cofetti Kmp App，参见
 * https://github.com/joreilly/Confetti/blob/fb832c2131b2f3e5276a1a3a30666aa571e1e17e/shared/src/commonMain/kotlin/dev/johnoreilly/confetti/decompose/DecomposeUtils.kt#L27
 */
public fun LifecycleOwner.coroutineScope(
    context: CoroutineContext = Dispatchers.Main.immediate,
): CoroutineScope {
    val scope = LifecycleAwareCoroutineScope(
        CoroutineScope(CoroutineCrashUtil.handler + context + SupervisorJob()),
        lifecycle,
    )
    lifecycle.doOnDestroy(scope::cancel)

    return scope
}

/**
 * [CoroutineScope]，它记住其所属组件的 [Lifecycle]，让 [asValue] 在组件停止时
 * 暂停收集（非活动底部导航标签页、返回栈条目、后台应用）。
 */
internal class LifecycleAwareCoroutineScope(
    delegate: CoroutineScope,
    internal val lifecycle: Lifecycle,
) : CoroutineScope by delegate

/**
 * 创建绑定到此 [ComponentContext] 生命周期的 Main [CoroutineScope] 实例。
 */
public fun LifecycleOwner.componentCoroutineScope(): CoroutineScope =
    coroutineScope(Dispatchers.Main)

/**
 * 将此 Kotlin [StateFlow] 转换为 Decompose [Value]。
 * 在 [Dispatchers.Main.immediate] 上收集以确保线程安全的 [MutableValue] 更新。
 *
 * 当 [scope] 来自 [LifecycleOwner.coroutineScope] 时，收集仅在所属组件启动时运行。
 * 这保持了 `SharingStarted.WhileSubscribed` 状态 flow 在停止的组件上未订阅
 * （非活动标签页被导航层挂起在 CREATED），因此它们的上游数据库和网络 flow 在组件再次显示前保持空闲。
 * 普通 scope 则在整个生命周期内收集。
 */
public fun <T : Any> StateFlow<T>.asValue(scope: CoroutineScope): Value<T> {
    val mutableValue = MutableValue(value)
    val lifecycle = (scope as? LifecycleAwareCoroutineScope)?.lifecycle
    if (lifecycle == null) {
        scope.launch(Dispatchers.Main.immediate) {
            collect { mutableValue.value = it }
        }
    } else {
        var job: Job? = null
        lifecycle.doOnStart(isOneTime = false) {
            job = scope.launch(Dispatchers.Main.immediate) {
                collect { mutableValue.value = it }
            }
        }
        lifecycle.doOnStop(isOneTime = false) {
            job?.cancel()
            job = null
        }
    }
    return mutableValue
}

/**
 * 将此 Decompose [Value] 转换为 Kotlin [StateFlow]。
 */
public fun <T : Any> Value<T>.asStateFlow(coroutineScope: CoroutineScope): StateFlow<T> = asFlow()
    .stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = value,
    )

/**
 * 将此 Decompose [Value] 转换为 Kotlin [Flow]。
 */
public fun <T : Any> Value<T>.asFlow(): Flow<T> = callbackFlow {
    val cancellation = subscribe { value ->
        trySendBlocking(value)
    }

    awaitClose {
        cancellation.cancel()
    }
}

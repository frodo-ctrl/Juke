package com.thomaskioko.tvmaniac.core.base.interactor

import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeStatus
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

// https://github.com/chrisbanes/tivi/blob/main/domain/src/commonMain/kotlin/app/tivi/domain/Interactor.kt
public abstract class Interactor<in P> {
    public operator fun invoke(
        params: P,
        timeoutMs: Duration = defaultTimeoutMs,
    ): Flow<InvokeStatus> {
        val source = this::class.simpleName
        return flow {
            try {
                withTimeout(timeoutMs) {
                    emit(InvokeStarted)
                    doWork(params)
                    emit(InvokeSuccess)
                }
            } catch (t: TimeoutCancellationException) {
                emit(InvokeError(t, source))
            }
        }.catch { t -> emit(InvokeError(t, source)) }
    }

    public suspend fun executeSync(params: P): Unit = doWork(params)

    protected abstract suspend fun doWork(params: P)

    public companion object {
        private val defaultTimeoutMs = 5.minutes
    }
}

public suspend inline fun Interactor<Unit>.executeSync(): Unit = executeSync(Unit)

public abstract class ResultInteractor<in P, R> {
    public operator fun invoke(params: P): Flow<R> = flow { emit(doWork(params)) }

    public suspend fun executeSync(params: P): R = doWork(params)

    protected abstract suspend fun doWork(params: P): R
}

public abstract class SubjectInteractor<P : Any, T> {
    // 理想情况下这应该是 buffer = 0，因为我们在下面使用 flatMapLatest，但 invoke 不是
    // 挂起的。这意味着我们不能在 flatMapLatest 取消任何现有 flow 时挂起。
    // 缓冲区为 1 意味着我们可以使用 tryEmit() 并缓冲该值，
    // 结果几乎相同。
    private val paramState = MutableSharedFlow<P>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    public val flow: Flow<T> = paramState
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it) }
        .distinctUntilChanged()

    public operator fun invoke(params: P) {
        paramState.tryEmit(params)
    }

    protected abstract fun createObservable(params: P): Flow<T>
}

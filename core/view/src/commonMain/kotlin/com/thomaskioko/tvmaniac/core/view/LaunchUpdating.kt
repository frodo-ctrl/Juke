package com.thomaskioko.tvmaniac.core.view

import com.thomaskioko.tvmaniac.core.logger.Logger
import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 在 [statusFlow] 运行期间在 [updatingIds] 中标记 [id]，驱动逐项进度指示器。
 * 收集完成后（无论成功或失败）移除 [id]，并通过 [collectStatus] 路由错误。
 * [counter] 额外将该操作作为全局加载信号进行跟踪。
 * 当 [id] 已经在更新中时忽略重新分发。
 */
public fun CoroutineScope.launchUpdating(
    id: Long,
    updatingIds: MutableStateFlow<PersistentSet<Long>>,
    counter: ObservableLoadingCounter,
    logger: Logger? = null,
    uiMessageManager: UiMessageManager? = null,
    sourceId: String? = null,
    errorToStringMapper: ErrorToStringMapper? = null,
    statusFlow: () -> Flow<InvokeStatus>,
) {
    if (id in updatingIds.value) return
    updatingIds.update { it.adding(id) }
    launch {
        try {
            statusFlow().collectStatus(
                counter = counter,
                logger = logger,
                uiMessageManager = uiMessageManager,
                sourceId = sourceId,
                errorToStringMapper = errorToStringMapper,
            )
        } finally {
            updatingIds.update { it.removing(id) }
        }
    }
}

package com.thomaskioko.tvmaniac.core.view

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

public enum class UiMessageType {
    Error,
    Success,
}

public data class UiMessage(
    val message: String,
    val id: Long = nextUiMessageId(),
    val sourceId: String? = null,
    val type: UiMessageType = UiMessageType.Error,
)

public fun UiMessage(
    t: Throwable,
    id: Long = nextUiMessageId(),
    sourceId: String? = null,
    fallbackMessage: String = DEFAULT_ERROR_MESSAGE,
): UiMessage = UiMessage(
    message = t.message ?: fallbackMessage,
    id = id,
    sourceId = sourceId,
)

internal const val DEFAULT_ERROR_MESSAGE = "Something went wrong. Please try again."

private val uiMessageIds = atomic(0L)

internal fun nextUiMessageId(): Long = uiMessageIds.incrementAndGet()

public class UiMessageManager {
    private val mutex = Mutex()

    private val _messages = MutableStateFlow(emptyList<UiMessage>())

    public val message: Flow<UiMessage?> = _messages.map { it.firstOrNull() }.distinctUntilChanged()

    public fun emitMessage(message: UiMessage) {
        _messages.update { it + message }
    }

    public suspend fun emitMessageCombined(throwable: Throwable, sourceId: String? = null) {
        mutex.withLock {
            val errorMessage = throwable.message ?: DEFAULT_ERROR_MESSAGE

            // 检查是否已有相同错误类型的消息
            val existingMessages = _messages.value.filter {
                it.message.contains(errorMessage) || errorMessage.contains(it.message)
            }

            if (existingMessages.isEmpty()) {
                _messages.value = _messages.value + UiMessage(throwable, sourceId = sourceId)
            } else {
                // 有相似的消息，合并它们
                val firstMessage = existingMessages.first()
                val sources = mutableSetOf<String>()

                // 从现有消息中收集所有源 ID
                existingMessages.forEach { message ->
                    if (message.sourceId != null) {
                        sources.add(message.sourceId)
                    }
                }

                if (sourceId != null) {
                    sources.add(sourceId)
                }

                val combinedMessage = if (sources.isEmpty()) {
                    "Multiple errors of type: $errorMessage"
                } else {
                    "Errors in ${sources.joinToString(", ")}: $errorMessage"
                }

                // 我们需要以稍后可以检索的方式存储合并的源
                // 目前，我们将使用第一个源作为代表
                val representativeSource = sources.firstOrNull()

                // 用合并的消息替换现有消息
                _messages.value = _messages.value.filterNot {
                    existingMessages.contains(it)
                } + UiMessage(combinedMessage, firstMessage.id, sourceId = representativeSource)
            }
        }
    }

    public suspend fun clearMessage(id: Long) {
        mutex.withLock {
            _messages.value = _messages.value.filterNot { it.id == id }
        }
    }
}

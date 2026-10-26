package com.thomaskioko.tvmaniac.core.logger

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * 将每个调用委托给所有已注册 [Logger] 实例的 [Logger] 实现。
 *
 * 这允许多个日志目的地（例如用于本地控制台输出的 [KermitLogger]，
 * 用于远程崩溃报告的 [FirebaseCrashLogger]）在单个 [Logger] 接口后组合。
 * 任何注入 [Logger] 的类都会透明地接收所有目的地。
 *
 * @param loggers 要委托到的 [Logger] 实现的 set。
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class CompositeLogger(
    private val loggers: Set<Logger>,
) : Logger {

    override fun setup(debugMode: Boolean) {
        loggers.forEach { it.setup(debugMode) }
    }

    override fun debug(message: String) {
        loggers.forEach { it.debug(message) }
    }

    override fun debug(tag: String, message: String) {
        loggers.forEach { it.debug(tag, message) }
    }

    override fun error(message: String, throwable: Throwable) {
        loggers.forEach { it.error(message, throwable) }
    }

    override fun error(tag: String, message: String) {
        loggers.forEach { it.error(tag, message) }
    }

    override fun error(tag: String, message: String, throwable: Throwable, keys: Map<String, String>) {
        loggers.forEach { it.error(tag, message, throwable, keys) }
    }

    override fun info(message: String, throwable: Throwable) {
        loggers.forEach { it.info(message, throwable) }
    }

    override fun info(tag: String, message: String) {
        loggers.forEach { it.info(tag, message) }
    }

    override fun warning(message: String) {
        loggers.forEach { it.warning(message) }
    }

    override fun warning(tag: String, message: String) {
        loggers.forEach { it.warning(tag, message) }
    }

    override fun warning(tag: String, message: String, throwable: Throwable, keys: Map<String, String>) {
        loggers.forEach { it.warning(tag, message, throwable, keys) }
    }

    override fun verbose(message: String) {
        loggers.forEach { it.verbose(message) }
    }

    override fun verbose(tag: String, message: String) {
        loggers.forEach { it.verbose(tag, message) }
    }

    override fun recordException(throwable: Throwable, keys: Map<String, String>) {
        loggers.forEach { it.recordException(throwable, keys) }
    }

    override fun setUserId(userId: String) {
        loggers.forEach { it.setUserId(userId) }
    }

    override fun setCustomKey(key: String, value: String) {
        loggers.forEach { it.setCustomKey(key, value) }
    }
}

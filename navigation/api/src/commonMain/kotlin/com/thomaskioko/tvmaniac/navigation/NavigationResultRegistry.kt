package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.Flow

/**
 * 在 activity 作用域内在目标之间代理 [NavigationResultRequest] 投递。
 *
 * 需要接收结果的 Presenter 注入 [NavigationResultRegistry] 并调用 [registerForNavigationResult]。
 * 需要投递结果的 Presenter 注入同一个 registry 并调用 [deliverNavigationResult]。
 * registry 为每个 key 保留一个待处理结果，在下次收集时投递它。
 *
 * 结果仅存在于内存中。它们在 Decompose 重组后仍然存在，但在当前实现中不会在进程死亡后保留。
 * 将投递的结果视为至多一次的提示，当该保证重要时通过 repository 协调权威状态。
 */
public interface NavigationResultRegistry {

    /**
     * 返回一个 [Flow]，它发出为 [key] 投递的下一个结果。每个投递的结果
     * 恰好对第一个观察它的收集器发出一次；后续收集器只接收新的投递。
     *
     * @param R 与 [key] 关联的结果类型。
     * @param key 与 [deliver] 调用匹配的标识符。
     * @return 冷 flow，对每个投递的结果恰好发出一次。
     */
    public fun <R : Any> register(key: NavigationResultRequest.Key<R>): Flow<R>

    /**
     * 将 [result] 投递到任何当前或未来为 [key] 注册的收集器。如果收集器
     * 已经在观察 flow，则立即接收值；否则该值被缓冲直到有人开始收集。
     *
     * @param R 与 [key] 关联的结果类型。
     * @param key 由源目标注册的标识符。
     * @param result 要投递到源的值。
     */
    public fun <R : Any> deliver(key: NavigationResultRequest.Key<R>, result: R)
}

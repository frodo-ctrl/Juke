package com.thomaskioko.tvmaniac.navigation

/**
 * 创建一个作用域为源路由 [Source] 和结果类型 [R] 的 [NavigationResultRequest]。
 *
 * 示例：
 * ```kotlin
 * // In a presenter hosted by SignInCallerRoute
 * private val signInRequest = resultRegistry.registerForNavigationResult<SignInCallerRoute, SignInResult>()
 *
 * init {
 *   coroutineScope.launch {
 *     signInRequest.results.collect { result -> handleSignIn(result) }
 *   }
 * }
 *
 * fun onSignInClicked() {
 *   navigator.navigateTo(SignInRoute(resultKey = signInRequest.key))
 * }
 * ```
 *
 * 生成的 key 对于给定的 [Source] 和 [R] 对是稳定的，因此可以安全地嵌入在
 * 目标路由中并通过保存和恢复往返传递。
 *
 * @param Source 托管注册结果的 Presenter 的源路由。
 * @param R 源期望接收的结果类型。
 * @return 其 [NavigationResultRequest.results] flow 发出每个投递结果的请求。
 */
public inline fun <reified Source : NavRoute, reified R : Any> NavigationResultRegistry.registerForNavigationResult(): NavigationResultRequest<R> {
    val key = NavigationResultRequest.Key<R>(
        ownerRouteQualifiedName = requireNotNull(Source::class.qualifiedName) {
            "Source route class must have a qualified name to register for navigation results."
        },
        resultQualifiedName = requireNotNull(R::class.qualifiedName) {
            "Result class must have a qualified name to register for navigation results."
        },
    )
    return NavigationResultRequest(key = key, results = register(key))
}

/**
 * 为 [key] 标识的请求投递 [result]。目标目标负责在投递后从栈中弹出自身（如果这是期望的流程）。
 *
 * @param R 源目标期望的结果类型。
 * @param key 由源嵌入在目标路由中的标识符。
 * @param result 要投递到源的值。
 */
public fun <R : Any> NavigationResultRegistry.deliverNavigationResult(
    key: NavigationResultRequest.Key<R>,
    result: R,
): Unit = deliver(key, result)

package com.thomaskioko.tvmaniac.accountmanager.api

/**
 * 为单个 [SyncProviderSource] 驱动登录 web 流程：启动它、注册平台结果处理程序、
 * 连接平台完成回调。每个后端贡献一个实现到多绑定的 set；组合根遍历该 set 以连接每个提供者。
 */
public interface AuthManager : SyncProvider {
    public fun launchWebView()

    public fun registerResult()

    public fun setAuthCallback(callback: () -> Unit)
}

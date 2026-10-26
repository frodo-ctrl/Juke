package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource

/**
 * 共享的、提供者无关的 OAuth 启动器。[launch] 为给定提供者的 [AuthClientConfig]
 * 运行授权码流程，并通过 [AuthStateHolder] 持久化结果；提供者仅在配置上不同，行为相同。
 * 在 Android 上驱动 AppAuth；在 iOS 上桥接到通过 [setCallback] 注册的逐提供者原生协调器。
 * 单个实例服务于所有提供者——OAuth 流程一次运行一个。
 */
public interface OAuthLauncher {
    public fun register()
    public fun launch(config: AuthClientConfig)
    public fun setCallback(provider: SyncProviderSource, callback: () -> Unit)
}

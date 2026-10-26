package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState

/**
 * 提供者无关的认证 repository：[AccountAuthRepository] 加上 HTTP 客户端附加 bearer token
 * 所需的挂起 [getAuthState] 访问器。通用的 OAuth 持有者实现此接口；提供者 facade
 * （例如 Trakt）扩展它。
 */
public interface OAuthRepository : AccountAuthRepository {
    public suspend fun getAuthState(): AuthState?
}

package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * 共享的、以提供者为键的认证状态引擎：通过 [AuthStore] 为每个 [SyncProviderSource]
 * 缓存和持久化 token，并编排刷新。提供者 repository 转发给它，因此 token 状态逻辑
 * 集中在一处而非每个提供者重复。[refreshTokens] 接受提供者的 [TokenRefreshAction]
 * （对于 token 永不过期的提供者为 null）。
 */
public interface AuthStateHolder {
    public fun state(provider: SyncProviderSource): Flow<AccountAuthState>
    public fun authState(provider: SyncProviderSource): Flow<AuthState?>
    public fun authError(provider: SyncProviderSource): Flow<AuthError?>
    public fun loginEvents(provider: SyncProviderSource): SharedFlow<Unit>
    public fun isLoggedIn(provider: SyncProviderSource): Boolean
    public suspend fun getAuthState(provider: SyncProviderSource): AuthState?
    public suspend fun saveTokens(
        provider: SyncProviderSource,
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    )
    public suspend fun refreshTokens(provider: SyncProviderSource, action: TokenRefreshAction?): TokenRefreshResult
    public suspend fun logout(provider: SyncProviderSource)
    public suspend fun setAuthError(provider: SyncProviderSource, error: AuthError?)
}

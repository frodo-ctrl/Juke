package com.thomaskioko.tvmaniac.accountmanager.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * 单个 [SyncProviderSource] 的认证状态和账户级操作。每个后端贡献一个实现到多绑定的 set；
 * [AccountManager] 解析活动的那个。
 */
public interface AccountAuthRepository : SyncProvider {
    public val state: Flow<AccountAuthState>
    public val authState: Flow<AuthState?>
    public val authError: Flow<AuthError?>

    /** 每次显式登录完成时发出一次。缓存恢复和 token 刷新不发出。 */
    public val loginEvents: SharedFlow<Unit>

    public fun isLoggedIn(): Boolean

    public suspend fun logout()

    public suspend fun refreshTokens(): TokenRefreshResult

    public suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    )

    public suspend fun setAuthError(error: AuthError?)
}

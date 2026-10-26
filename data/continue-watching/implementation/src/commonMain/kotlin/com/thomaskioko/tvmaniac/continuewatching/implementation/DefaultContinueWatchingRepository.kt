package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultContinueWatchingRepository(
    private val nitroStore: NitroContinueWatchingStore,
    private val progressStore: ProgressContinueWatchingStore,
    private val continueWatchingDao: ContinueWatchingDao,
    private val dispatchers: AppCoroutineDispatchers,
) : ContinueWatchingRepository {

    override suspend fun sync(forceRefresh: Boolean, useNitro: Boolean) {
        try {
            if (useNitro) {
                nitroStore.fetchWith(forceRefresh)
            } else {
                progressStore.fetchWith(forceRefresh)
            }
        } catch (_: FetcherSkipSignal) {
            // 有意为之：抓取器发出"不要改动本地表"的信号。
            // 上游 HTTP 失败或 Nitro 的空响应保护。
        }
    }

    override suspend fun getEntries(): List<ContinueWatchingEntry> =
        withContext(dispatchers.databaseRead) {
            continueWatchingDao.entries()
        }

    override suspend fun deriveMembershipFromWatchedEpisodes() {
        withContext(dispatchers.databaseWrite) {
            continueWatchingDao.insertMembershipFromWatchedEpisodes()
        }
    }
}

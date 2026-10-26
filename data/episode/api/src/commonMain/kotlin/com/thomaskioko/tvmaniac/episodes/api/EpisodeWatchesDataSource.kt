package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider

public interface EpisodeWatchesDataSource : SyncProvider {

    public suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry>

    public suspend fun getAllWatchedShows(page: Int = 1, limit: Int = 100): List<WatchedShowBatch>

    /**
     * 用户已观看节目的时长、发行年份和类型。已从 [getAllWatchedShows] 返回这些内容的
     * 提供者保持默认实现，不产生额外请求。
     */
    public suspend fun getWatchedShowMetadata(page: Int = 1, limit: Int = 100): List<WatchedShowMetadata> = emptyList()

    public suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>)

    public suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>)
}

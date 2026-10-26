package com.thomaskioko.tvmaniac.startwatching.api

import kotlinx.coroutines.flow.Flow

public interface StartWatchingRepository {

    /**
     * 观察已发布但尚未开始观看的已关注节目，排除任何已存在于 continue-watching 中的节目。
     */
    public fun observeStartWatching(): Flow<List<StartWatchingShow>>

    /**
     * 重新同步支撑 Start Watching 车道的 Trakt watchlist，将节目元数据和海报
     * 写入共享的已关注节目表。仅拉取：保留待处理的关注/取消关注操作，永远不上传本地更改。
     *
     * @param forceRefresh 当为 true 时绕过缓存新鲜度窗口。
     */
    public suspend fun syncWatchlist(forceRefresh: Boolean = false)
}

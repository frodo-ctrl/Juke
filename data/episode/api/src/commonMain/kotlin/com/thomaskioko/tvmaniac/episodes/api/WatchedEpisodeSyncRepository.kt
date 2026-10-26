package com.thomaskioko.tvmaniac.episodes.api

public interface WatchedEpisodeSyncRepository {

    /**
     * 将本地标记的 `pending_action='UPLOAD'` 和 `pending_action='DELETE'` 行推送到 Trakt。
     * 不进行远程读取。当没有待处理行时提前返回，以便即时 UI 触发的推送和周期性 worker
     * 在空闲路径上保持低成本。
     */
    public suspend fun syncPendingEpisodes()

    /**
     * 从 Trakt 的批量 `/sync/watched/shows` 端点（分页，以 100 个节目一页排空）
     * 拉取完整的已观看节目状态，并 upsert 到本地 `watched_episodes` 表中。
     * 通过两步跳过进行门控：当批量消费者的活动检查点是最新的且每功能 TTL 未过期
     * 且 [forceRefresh] 为 false 时，不进行任何远程读取直接返回。
     */
    public suspend fun syncAllWatchedEpisodes(forceRefresh: Boolean = false)

    /**
     * 逐节目显式刷新。由节目详情屏幕的下拉刷新流程使用。
     * 当 [forceRefresh] 为 false 时，如果批量消费者已赶上或每节目 TTL 未过期，
     * 调用会短路。
     */
    public suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean = false)

    public suspend fun countPendingEpisodes(): Long

    public suspend fun getWatchedShowsMissingGenres(): List<Long>
}

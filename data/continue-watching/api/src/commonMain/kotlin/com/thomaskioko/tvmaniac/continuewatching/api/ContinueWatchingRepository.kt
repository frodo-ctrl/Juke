package com.thomaskioko.tvmaniac.continuewatching.api

public interface ContinueWatchingRepository {

    /**
     * 从 Trakt 刷新本地 continue-watching 表。
     *
     * @param forceRefresh 忽略 Store5 TTL 验证器并访问网络。
     * @param useNitro 当为 true 时，通过 `/sync/progress/up_next_nitro` 获取。
     *   当为 false 时，使用文档化的路径：分页 `/sync/watched/shows`（规范已观看节目 feed）
     *   与 `/sync/playback/episodes`（剧集中途的节目）连接，然后逐节目
     *   `/shows/{id}/progress/watched` 以填充下一集状态。调用方解析
     *   运行时功能 flag 值（`CONTINUE_WATCHING_NITRO_ENABLED`）并传入；
     *   此 repository 自身不观察该 flag。
     */
    public suspend fun sync(forceRefresh: Boolean = false, useNitro: Boolean = false)

    /**
     * 本地缓存的 continue-watching 条目的快照。
     */
    public suspend fun getEntries(): List<ContinueWatchingEntry>

    /**
     * 为没有原生 continue-watching feed 的提供者（Simkl）从本地已观看剧集
     * 播种 continue-watching 成员资格。现有行永远不会回退。
     */
    public suspend fun deriveMembershipFromWatchedEpisodes()
}

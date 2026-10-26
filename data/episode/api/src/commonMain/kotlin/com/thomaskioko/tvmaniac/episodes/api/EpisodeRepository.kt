package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.episodes.api.model.MostWatchedShow
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedEpisodeRuntime
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedShowComposition
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

public interface EpisodeRepository {

    public fun observeEpisodeById(episodeId: Long): Flow<EpisodeById?>

    /** 观察所有节目中最近观看的剧集，最新在前。 */
    public fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>>

    public fun observeWatchedEpisodeRuntimes(): Flow<List<WatchedEpisodeRuntime>>

    public fun observeWatchedShowComposition(): Flow<List<WatchedShowComposition>>

    public fun observeMostWatchedShows(limit: Long): Flow<List<MostWatchedShow>>

    public suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    public suspend fun updateWatchedDate(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    /**
     * 将剧集连同所有之前未观看的剧集一起标记为已观看。
     * 如果节目尚未在库中，自动将其添加到库。
     */
    public suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    /**
     * 将剧集标记为未观看。SQL 视图会自动更新下一集计算。
     */
    public suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long)

    /**
     * 观察特定季的观看进度。
     */
    public fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    /**
     * 观察整个节目的观看进度（跨所有季）。
     */
    public fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress>

    /**
     * 观察节目所有季的观看进度。
     * 返回 SeasonWatchProgress 列表，每季一个。
     */
    public fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>>

    /**
     * 将一季中的所有剧集标记为已观看。
     * 如果节目尚未在库中，自动将其添加到库。
     */
    public suspend fun markSeasonWatched(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    /**
     * 将一季中的所有剧集连同所有之前的季一起标记为已观看。
     * 如果节目尚未在库中，自动将其添加到库。
     */
    public suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    public suspend fun markShowWatched(
        showId: Long,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    /**
     * 将一季中的所有剧集标记为未观看。
     */
    public suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long)

    /**
     * 观察指定季编号之前各季中未观看剧集的数量。
     * 供响应式 UI 用于确定是否应显示"之前各季"对话框。
     */
    public fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long>

    /**
     * 在指定时间窗口内从已关注的节目中获取即将到来的剧集。
     * 仅返回设置了 first_aired 且尚未观看的剧集。
     * 时间过滤（从当前时间到现在 + limit）在内部处理。
     * @param limit 从现在开始搜索即将到来剧集的时长
     * @return 按播出时间排序的即将到来剧集列表
     */
    public suspend fun getUpcomingEpisodesFromFollowedShows(limit: Duration): List<UpcomingEpisode>

    public suspend fun syncUpcomingEpisodes(
        startDate: String,
        days: Int,
        forceRefresh: Boolean = false,
    )

    public suspend fun getShowMetadataSyncInfo(showId: Long): ShowMetadataSyncInfo?

    public suspend fun getEpisodeId(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Long?

    public companion object {
        public const val ALL_SEASONS: Long = Long.MAX_VALUE
    }
}

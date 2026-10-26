package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class ProgressContinueWatchingFetcher(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val syncRepository: ActivitySyncRepository,
    private val datastoreRepository: DatastoreRepository,
    private val logger: Logger,
) {

    internal operator fun invoke(): Flow<ProgressBatch?> = channelFlow {
        val cursor = syncRepository.getSyncTimestamp(
            consumerId = ActivitySyncTypes.PROGRESS_CONTINUE_WATCHING,
            activityType = ActivityType.EPISODES_WATCHED,
        )
        val playbackDeferred = async { traktSyncDataSource.getPlaybackEpisodes() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }
        val watchedShowsDeferred = async {
            traktSyncDataSource.getWatchedShows(page = 1, limit = WATCHED_SHOWS_LIMIT)
        }

        val playbackResponse = playbackDeferred.await()
        if (playbackResponse !is ApiResponse.Success) {
            send(null)
            return@channelFlow
        }
        val watchedShowsResponse = watchedShowsDeferred.await()
        if (watchedShowsResponse !is ApiResponse.Success) {
            send(null)
            return@channelFlow
        }
        val hiddenResponse = hiddenDeferred.await()
        if (hiddenResponse !is ApiResponse.Success) {
            send(null)
            return@channelFlow
        }

        val hiddenIds = hiddenResponse.body
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()
        val descriptors = buildDescriptors(watchedShowsResponse.body, playbackResponse.body)
        val candidates = descriptors.keys.filter { it !in hiddenIds }

        val lastActivity = cursor?.toString()
        val includeSpecials = datastoreRepository.getIncludeSpecials()

        val responses = mutableListOf<Pair<Long, ApiResponse<TraktWatchedProgressResponse>>>()
        var rateLimitedAt: Long? = null
        for (showId in candidates) {
            val response = traktSyncDataSource.getShowWatchedProgress(
                showId = showId,
                lastActivity = lastActivity,
                specials = includeSpecials,
            )
            responses += showId to response
            when (response) {
                is ApiResponse.Success -> {
                    val progress = response.body
                    // 负载过滤器。捕获重置的节目（`reset_at` 已填充）以及任何
                    // Trakt 逐节目逻辑返回 null next_episode 的情况。
                    // 移除它会持久化永远不会在 watchlist 中渲染的行。
                    if (progress.nextEpisode != null) {
                        send(ProgressBatch.Entry(toEntry(showId, descriptors[showId], progress), traktId = showId))
                    }
                }
                is ApiResponse.Error -> {
                    if (response.toSyncError() is SyncError.Retryable) {
                        rateLimitedAt = showId
                        break
                    }
                }
                is ApiResponse.Unauthenticated -> {
                    rateLimitedAt = showId
                    break
                }
            }
        }

        if (rateLimitedAt != null) {
            logger.warning(LOG_TAG, "Backing off progress fan-out after retryable failure on $rateLimitedAt")
            return@channelFlow
        }

        val withNextEpisode = responses.count { (_, response) ->
            (response as? ApiResponse.Success)?.body?.nextEpisode != null
        }
        logger.debug(
            LOG_TAG,
            "Progress per-show fan-out: candidates=${candidates.size} withNextEpisode=$withNextEpisode",
        )

        if (responses.all { (_, response) -> response is ApiResponse.Success }) {
            val completedShowIds = responses
                .mapNotNull { (traktId, response) ->
                    val progress = (response as ApiResponse.Success).body
                    if (progress.nextEpisode != null) {
                        descriptors[traktId]?.tmdbId ?: traktId
                    } else {
                        null
                    }
                }
                .toSet()
            send(ProgressBatch.Complete(completedShowIds))
        }
    }

    private fun buildDescriptors(
        watchedShows: List<TraktWatchedShowResponse>,
        playback: List<TraktPlaybackEpisodeResponse>,
    ): Map<Long, ProgressDescriptor> = buildMap {
        watchedShows.forEach { item ->
            val show = item.show
            put(show.ids.trakt, ProgressDescriptor(traktId = show.ids.trakt, tmdbId = show.ids.tmdb, title = show.title, year = show.year))
        }
        playback.forEach { item ->
            put(item.show.ids.trakt, ProgressDescriptor(traktId = item.show.ids.trakt, tmdbId = item.show.ids.tmdb, title = item.show.title, year = item.show.year))
        }
    }

    private companion object {
        const val WATCHED_SHOWS_LIMIT = 100
        const val LOG_TAG = "ProgressContinueWatchingFetcher"
    }
}

internal data class ProgressDescriptor(
    val traktId: Long,
    val tmdbId: Long?,
    val title: String?,
    val year: Long?,
)

/**
 * Continue Watching 获取器发出的增量同步事件。
 *
 * 订阅者将每个变体独立应用到本地表，因此 UI 会随着条目到达而渐进更新，
 * 而不是等待单次原子翻转。
 */
internal sealed interface ProgressBatch {
    /** 单个已解析的节目条目，准备就绪后立即发送。[traktId] 携带用于 stub tvshow 插入的 Trakt id。 */
    data class Entry(val entry: ContinueWatchingEntry, val traktId: Long? = null) : ProgressBatch

    /**
     * 终止事件。[finalShowIds] 是同步的 watchlist 的权威 tmdb 边界 id 集；
     * 订阅者删除不在此集中的行，然后标记新鲜度标记。
     */
    data class Complete(val finalShowIds: Set<Long>) : ProgressBatch
}

private fun toEntry(
    showId: Long,
    descriptor: ProgressDescriptor?,
    progress: TraktWatchedProgressResponse,
): ContinueWatchingEntry {
    val lastWatchedAtMs = progress.lastWatchedAt
        ?.let { Instant.parse(it).toEpochMilliseconds() }
        ?: 0L
    return ContinueWatchingEntry(
        showId = descriptor?.tmdbId ?: showId,
        tmdbId = descriptor?.tmdbId,
        airedEpisodes = progress.aired.toLong(),
        completedCount = progress.completed.toLong(),
        lastWatchedAt = lastWatchedAtMs,
        lastUpdatedAt = lastWatchedAtMs,
        title = descriptor?.title,
        year = descriptor?.year,
    )
}

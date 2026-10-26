package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface NextEpisodeDao {

    /**
     * 观察 watchlist 中所有节目的下一集。
     * @param includeSpecials 是否在计算中包含特辑（第 0 季）
     */
    public fun observeNextEpisodesForWatchlist(includeSpecials: Boolean): Flow<List<NextEpisodeWithShow>>

    /** 观察用户已看完的 watchlist 节目，即所有已播出剧集都已观看。 */
    public fun observeCompletedShows(): Flow<List<CompletedShow>>
}

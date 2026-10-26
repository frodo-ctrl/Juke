package com.thomaskioko.tvmaniac.episodes.api.model

import com.thomaskioko.tvmaniac.db.Watched_episodes

/**
 * 使用公式计算绝对剧集编号：season * 1000 + episode
 * 这确保剧集跨季正确排序（例如 S2E1 在 S1E10 之后）
 */
public fun Long.toAbsoluteEpisodeNumber(episodeNumber: Long): Long =
    this * 1000 + episodeNumber

/**
 * 获取已观看剧集的绝对剧集编号的扩展函数
 */
public fun Watched_episodes.absoluteEpisodeNumber(): Long =
    season_number.toAbsoluteEpisodeNumber(episode_number)

/**
 * 为任何有季/剧集编号的剧集获取绝对剧集编号的扩展函数
 */
public fun calculateAbsoluteEpisodeNumber(seasonNumber: Long, episodeNumber: Long): Long =
    seasonNumber.toAbsoluteEpisodeNumber(episodeNumber)

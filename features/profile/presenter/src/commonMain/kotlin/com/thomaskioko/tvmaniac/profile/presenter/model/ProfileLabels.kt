package com.thomaskioko.tvmaniac.profile.presenter.model

/**
 * 个人资料屏幕的本地化显示字符串，在 presenter 中解析一次，这样 Android 和 iOS
 * 都从同一共享状态渲染，而不是按平台解析字符串。
 */
public data class ProfileLabels(
    val title: String = "",
    val settingsContentDescription: String = "",
    val profilePictureContentDescription: String = "",
    val editButton: String = "",
    val statsTitle: String = "",
    val episodesWatched: String = "",
    val showsWatched: String = "",
    val watchTime: String = "",
    val monthsShort: String = "",
    val daysShort: String = "",
    val hoursShort: String = "",
    val lists: String = "",
    val viewButton: String = "",
    val userListsTitle: String = "",
    val progressTitle: String = "",
    val completedFilter: String = "",
    val inProgressFilter: String = "",
    val progressEmpty: String = "",
    val recentlyWatchedTitle: String = "",
    val favoritesTitle: String = "",
    val viewAllButton: String = "",
    val retry: String = "",
    val unauthenticatedTitle: String = "",
    val authTitle: String = "",
    val authDescription: String = "",
    val featureDiscoverTitle: String = "",
    val featureDiscoverDescription: String = "",
    val featureTrackTitle: String = "",
    val featureTrackDescription: String = "",
    val featureManageTitle: String = "",
    val featureManageDescription: String = "",
    val featureMoreTitle: String = "",
    val featureMoreDescription: String = "",
)

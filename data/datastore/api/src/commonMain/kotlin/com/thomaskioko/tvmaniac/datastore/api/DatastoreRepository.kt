package com.thomaskioko.tvmaniac.datastore.api

import kotlinx.coroutines.flow.Flow

public interface DatastoreRepository {
    /**
     * 保存应用的主题偏好。
     *
     * @param appTheme 要保存的 [AppTheme]，可以是 LIGHT_THEME、DARK_THEME 或 SYSTEM_THEME 之一。
     */
    public fun saveTheme(appTheme: AppTheme)

    /**
     * 观察存储在 datastore 中的应用主题偏好变化。
     *
     * @return 一个发出当前 [AppTheme] 的 Flow，可以是 LIGHT_THEME、DARK_THEME 或 SYSTEM_THEME。
     */
    public fun observeTheme(): Flow<AppTheme>

    /**
     * 保存用户首选语言。
     *
     * @param languageCode ISO 639-1 语言代码（例如 "en"、"fr"、"de"）。
     */
    public suspend fun saveLanguage(languageCode: String)

    /**
     * 观察用户首选语言。
     *
     * @return 用户首选语言代码的 Flow。
     */
    public fun observeLanguage(): Flow<String>

    /**
     * 保存用户为 watchlist 首选的列表样式。
     *
     * @param listStyle 要保存的 [ListStyle]（GRID 或 LIST）。
     */
    public suspend fun saveListStyle(listStyle: ListStyle)

    /**
     * 观察用户为 watchlist 首选的列表样式。
     *
     * @return 用户首选 [ListStyle] 的 Flow，默认为 GRID。
     */
    public fun observeListStyle(): Flow<ListStyle>

    /**
     * 保存用户首选的图片质量设置。
     *
     * @param quality 要保存的 [ImageQuality]（HIGH、MEDIUM 或 LOW）。
     */
    public suspend fun saveImageQuality(quality: ImageQuality)

    /**
     * 观察用户首选的图片质量设置。
     *
     * @return 当前 [ImageQuality] 的 Flow，默认为 MEDIUM。
     */
    public fun observeImageQuality(): Flow<ImageQuality>

    /**
     * 保存用户关于在 YouTube 应用中打开预告片的偏好。
     *
     * @param enabled 是否在 YouTube 应用中打开预告片。
     */
    public suspend fun saveOpenTrailersInYoutube(enabled: Boolean)

    /**
     * 观察用户关于在 YouTube 应用中打开预告片的偏好。
     *
     * @return Boolean 的 Flow，如果预告片应在 YouTube 应用中打开则为 true，否则为 false。
     */
    public fun observeOpenTrailersInYoutube(): Flow<Boolean>

    /**
     * 保存用户关于在将季标记为已观看时是否包含第 0 季（特辑）的偏好。
     *
     * @param includeSpecials 标记季时是否包含特辑剧集。
     */
    public suspend fun saveIncludeSpecials(includeSpecials: Boolean)

    /**
     * 观察用户关于在将季标记为已观看时是否包含特辑的偏好。
     *
     * @return Boolean 的 Flow，如果应包含特辑则为 true，否则为 false。默认为 false。
     */
    public fun observeIncludeSpecials(): Flow<Boolean>

    /**
     * 获取用户关于在将季标记为已观看时是否包含特辑的偏好。
     *
     * @return Boolean，如果应包含特辑则为 true，否则为 false。默认为 false。
     */
    public suspend fun getIncludeSpecials(): Boolean

    /**
     * 保存最后登录的 Trakt 用户 ID（slug）。
     * 用于在重新登录时检测用户切换，防止账户间数据泄露。
     *
     * @param userId Trakt 用户 slug，或 null 表示清除。
     */
    public suspend fun saveLastTraktUserId(userId: String?)

    /**
     * 获取最后登录的 Trakt 用户 ID（slug）。
     *
     * @return 存储的用户 slug，或 null（如果没有）。
     */
    public suspend fun getLastTraktUserId(): String?

    /**
     * 保存用户关于后台同步的偏好。
     *
     * @param enabled 是否启用后台同步。
     */
    public suspend fun setBackgroundSyncEnabled(enabled: Boolean)

    /**
     * 观察用户关于后台同步的偏好。
     *
     * @return Boolean 的 Flow，如果后台同步已启用则为 true，否则为 false。默认为 true。
     */
    public fun observeBackgroundSyncEnabled(): Flow<Boolean>

    /**
     * 保存最后一次成功后台同步的时间戳。
     *
     * @param timestamp 上次同步的纪元毫秒数。
     */
    /**
     * 保存是否应按计划执行备份。
     *
     * @param enabled true 表示运行自动备份，false 表示停止。
     */
    public suspend fun setAutoBackupEnabled(enabled: Boolean)

    /**
     * 观察是否按计划执行备份。默认为 false。
     */
    public fun observeAutoBackupEnabled(): Flow<Boolean>

    /**
     * 保存自动备份的执行频率。
     */
    public suspend fun saveAutoBackupInterval(interval: AutoBackupInterval)

    /**
     * 观察自动备份的执行频率。默认为 [AutoBackupInterval.Default]。
     */
    public fun observeAutoBackupInterval(): Flow<AutoBackupInterval>

    /**
     * 保存备份写入的文件夹，或当其停止工作且用户必须重新选择新文件夹时为 null。
     */
    public suspend fun saveBackupFolder(folder: String?)

    /**
     * 观察备份写入的文件夹，或未选择时为 null。
     */
    public fun observeBackupFolder(): Flow<String?>

    /**
     * 读取备份写入的文件夹，供无法观察的调用方使用。
     */
    public suspend fun getBackupFolder(): String?

    /**
     * 保存备份写入的文件名。
     */
    public suspend fun saveBackupFileName(fileName: String)

    /**
     * 观察备份写入的文件名。默认为 [BackupFileName.Default]。
     */
    public fun observeBackupFileName(): Flow<String>

    /**
     * 读取备份写入的文件名，供无法观察的调用方使用。
     */
    public suspend fun getBackupFileName(): String

    public suspend fun setLastSyncTimestamp(timestamp: Long)

    /**
     * 观察最后一次成功后台同步的时间戳。
     *
     * @return 纪元毫秒数时间戳的 Flow，或 null（如果从未同步过）。
     */
    public fun observeLastSyncTimestamp(): Flow<Long?>

    /**
     * 保存用户关于剧集通知的偏好。
     *
     * @param enabled 是否启用剧集通知。
     */
    public suspend fun setEpisodeNotificationsEnabled(enabled: Boolean)

    /**
     * 观察用户关于剧集通知的偏好。
     *
     * @return Boolean 的 Flow，如果剧集通知已启用则为 true，否则为 false。默认为 false。
     */
    public fun observeEpisodeNotificationsEnabled(): Flow<Boolean>

    /**
     * 获取用户关于剧集通知的偏好。
     *
     * @return Boolean，如果剧集通知已启用则为 true，否则为 false。默认为 false。
     */
    public suspend fun getEpisodeNotificationsEnabled(): Boolean

    /**
     * 保存是否已请求通知权限。
     * 用于确保我们只在首次启动时提示一次。
     *
     * @param asked 是否已请求权限。
     */
    public suspend fun setNotificationPermissionAsked(asked: Boolean)

    /**
     * 观察是否已请求通知权限。
     *
     * @return Boolean 的 Flow，如果已请求权限则为 true，否则为 false。默认为 false。
     */
    public fun observeNotificationPermissionAsked(): Flow<Boolean>

    /**
     * 获取是否已请求通知权限。
     *
     * @return Boolean，如果已请求权限则为 true，否则为 false。默认为 false。
     */
    public suspend fun getNotificationPermissionAsked(): Boolean

    /**
     * 设置是否显示通知原理对话框。
     *
     * @param show 是否显示原理对话框。
     */
    public suspend fun setShowNotificationRationale(show: Boolean)

    /**
     * 观察是否显示通知原理对话框。
     *
     * @return Boolean 的 Flow，如果应显示原理则为 true。
     */
    public fun observeShowNotificationRationale(): Flow<Boolean>

    /**
     * 设置是否请求系统通知权限。
     *
     * @param request 是否请求权限。
     */
    public suspend fun setRequestNotificationPermission(request: Boolean)

    /**
     * 观察是否请求系统通知权限。
     *
     * @return Boolean 的 Flow，如果应请求权限则为 true。
     */
    public fun observeRequestNotificationPermission(): Flow<Boolean>

    /**
     * 保存用户首选的库排序选项。
     *
     * @param sortOption 排序选项名称（例如 "LAST_WATCHED"、"ALPHABETICAL"）。
     */
    public suspend fun saveLibrarySortOption(sortOption: String)

    /**
     * 观察用户首选的库排序选项。
     *
     * @return 排序选项名称的 Flow，默认为 "ADDED_DESC"。
     */
    public fun observeLibrarySortOption(): Flow<String>

    /**
     * 保存用户首选的 Up Next 排序选项。
     *
     * @param sortOption 排序选项名称（例如 "LAST_WATCHED"、"AIR_DATE"）。
     */
    public suspend fun saveUpNextSortOption(sortOption: String)

    /**
     * 观察用户首选的 Up Next 排序选项。
     *
     * @return 排序选项名称的 Flow，默认为 "LAST_WATCHED"。
     */
    public fun observeUpNextSortOption(): Flow<String>

    /**
     * 保存用户首选的 watchlist 排序选项。
     *
     * @param sortOption 排序选项名称（例如 "ADDED_DESC"、"TITLE_ASC"）。
     */
    public suspend fun saveWatchlistSortOption(sortOption: String)

    /**
     * 观察用户首选的 watchlist 排序选项。
     *
     * @return 排序选项名称的 Flow，默认为 "ADDED_DESC"。
     */
    public fun observeWatchlistSortOption(): Flow<String>

    public suspend fun setLastUpNextSyncTimestamp(timestamp: Long)

    public fun observeLastUpNextSyncTimestamp(): Flow<Long?>

    public suspend fun setLastTokenRefreshTimestamp(timestamp: Long)

    public fun observeLastTokenRefreshTimestamp(): Flow<Long?>

    /**
     * 保存用户首选的类型节目分类。
     *
     * @param category 分类名称（例如 "POPULAR"、"TRENDING"）。
     */
    public suspend fun saveGenreShowCategory(category: String)

    /**
     * 获取用户首选的类型节目分类。
     *
     * @return 分类名称，默认为 "POPULAR"。
     */
    public suspend fun getGenreShowCategory(): String

    /**
     * 观察用户首选的类型节目分类。
     *
     * @return 分类名称的 Flow，默认为 "POPULAR"。
     */
    public fun observeGenreShowCategory(): Flow<String>

    /**
     * 保存用户关于崩溃报告的偏好。
     *
     * @param enabled 是否启用崩溃报告。
     */
    public suspend fun setCrashReportingEnabled(enabled: Boolean)

    /**
     * 观察用户关于崩溃报告的偏好。
     *
     * @return Boolean 的 Flow，如果崩溃报告已启用则为 true，否则为 false。默认为 true。
     */
    public fun observeCrashReportingEnabled(): Flow<Boolean>

    /**
     * 保存隐藏的调试菜单是否已解锁。
     *
     * @param enabled 调试菜单是否已解锁。
     */
    public suspend fun setDebugMenuEnabled(enabled: Boolean)

    /**
     * 观察隐藏的调试菜单是否已解锁。
     *
     * @return Boolean 的 Flow，一旦调试菜单已解锁则为 true。默认为 false。
     */
    public fun observeDebugMenuEnabled(): Flow<Boolean>

    public suspend fun saveAccountType(override: String?)

    public fun observeAccountType(): Flow<String?>

    /**
     * 保存是否在支持的交互上触发触觉反馈。
     *
     * @param enabled 是否启用触觉反馈。
     */
    public suspend fun saveHapticFeedbackEnabled(enabled: Boolean)

    /**
     * 观察是否在支持的交互上触发触觉反馈。
     *
     * @return Boolean 的 Flow，如果触觉反馈已启用则为 true。默认为 true。
     */
    public fun observeHapticFeedbackEnabled(): Flow<Boolean>

    /**
     * 保存季在节目详情页上列出的顺序。
     *
     * @param sortOrder 最老或最新的季是否排在首位。
     */
    public suspend fun saveSeasonSortOrder(sortOrder: SeasonSortOrder)

    /**
     * 观察季在节目详情页上列出的顺序。
     *
     * @return SeasonSortOrder 的 Flow。默认为 [SeasonSortOrder.OLDEST_FIRST]。
     */
    public fun observeSeasonSortOrder(): Flow<SeasonSortOrder>

    /**
     * 保存是否对未观看剧集的剧照进行模糊处理以避免剧透。
     *
     * @param enabled 是否对未观看剧集的图片进行模糊处理。
     */
    public suspend fun saveBlurUnwatchedEpisodeImages(enabled: Boolean)

    /**
     * 观察是否对未观看剧集的剧照进行模糊处理以避免剧透。
     *
     * @return Boolean 的 Flow，如果未观看剧集的图片已模糊则为 true。默认为 false。
     */
    public fun observeBlurUnwatchedEpisodeImages(): Flow<Boolean>

    /**
     * 保存从发现屏幕中隐藏的发现分区。
     *
     * @param sections 用户选择隐藏的分区。
     */
    public suspend fun saveHiddenDiscoverSections(sections: Set<DiscoverSection>)

    /**
     * 观察从发现屏幕中隐藏的发现分区。
     *
     * @return 隐藏分区的 Flow。默认为空集。未知的存储名称会被忽略。
     */
    public fun observeHiddenDiscoverSections(): Flow<Set<DiscoverSection>>

    /**
     * 在一次写入事务内原子性地显示或隐藏单个发现分区。
     *
     * @param section 可见性变更的分区。
     * @param visible 是否显示该分区；false 表示隐藏。
     */
    public suspend fun updateDiscoverSectionVisibility(section: DiscoverSection, visible: Boolean)

    /**
     * 保存应用字体大小（默认值的百分比）。
     *
     * @param percent 缩放百分比，读取时会被限制在 85 到 130 之间。
     */
    public suspend fun saveFontSizePercent(percent: Int)

    /**
     * 观察应用字体大小百分比。
     *
     * @return 缩放百分比的 Flow，默认为 100 且限制在 85 到 130 之间。
     */
    public fun observeFontSizePercent(): Flow<Int>

    /**
     * 保存竖版海报卡片的尺寸预设，应用于应用内的每张海报。
     *
     * @param width 海报尺寸预设。
     */
    public suspend fun savePosterWidth(width: PosterWidth)

    /**
     * 观察竖版海报卡片的尺寸预设。
     *
     * @return [PosterWidth] 的 Flow。默认为 [PosterWidth.STANDARD]；未知的存储名称会回退到默认值。
     */
    public fun observePosterWidth(): Flow<PosterWidth>

    /**
     * 保存横版（16:9 背景）卡片的尺寸预设。
     *
     * @param width 横版卡片尺寸预设。
     */
    public suspend fun saveLandscapeWidth(width: PosterWidth)

    /**
     * 观察横版（16:9 背景）卡片的尺寸预设。
     *
     * @return [PosterWidth] 的 Flow。默认为 [PosterWidth.STANDARD]；未知的存储名称会回退到默认值。
     */
    public fun observeLandscapeWidth(): Flow<PosterWidth>

    /**
     * 保存应用于每张海报的圆角样式。
     *
     * @param style 圆角样式预设。
     */
    public suspend fun savePosterCornerStyle(style: PosterCornerStyle)

    /**
     * 观察应用于每张海报的圆角样式。
     *
     * @return [PosterCornerStyle] 的 Flow。默认为 [PosterCornerStyle.SHARP]；未知的存储名称会回退到默认值。
     */
    public fun observePosterCornerStyle(): Flow<PosterCornerStyle>

    public suspend fun saveWidgetTheme(appTheme: AppTheme?)

    public fun observeWidgetTheme(): Flow<AppTheme?>

    /**
     * 保存剧集标记为已观看后是否弹出评分表。
     *
     * @param enabled 是否启用评分提示。
     */
    public suspend fun saveQuickRateEnabled(enabled: Boolean)

    /**
     * 观察剧集标记为已观看后是否弹出评分表。
     *
     * @return Boolean 的 Flow，如果评分提示已启用则为 true。默认为 false。
     */
    public fun observeQuickRateEnabled(): Flow<Boolean>

    /**
     * 保存观看记录之前是否弹出观看日期表。
     *
     * @param enabled 是否启用自定义观看日期输入。
     */
    public suspend fun saveCustomWatchDateEnabled(enabled: Boolean)

    /**
     * 观察观看记录之前是否弹出观看日期表。
     *
     * @return Boolean 的 Flow，如果自定义观看日期输入已启用则为 true。默认为 false。
     */
    public fun observeCustomWatchDateEnabled(): Flow<Boolean>

    /**
     * 保存节目是否可以多次标记为已观看。
     *
     * @param enabled 是否允许多次播放。
     */
    public suspend fun saveMultiplePlaysEnabled(enabled: Boolean)

    /**
     * 观察节目是否可以多次标记为已观看。
     *
     * @return Boolean 的 Flow，当允许多次播放时为 true。默认为 true。
     */
    public fun observeMultiplePlaysEnabled(): Flow<Boolean>
}

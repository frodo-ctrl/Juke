package com.thomaskioko.tvmaniac.syncactivity.api

import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

/**
 * 跟踪每个消费者的活动时间戳同步位置，作用域为活动提供者。
 *
 * 替换了共享的 `trakt_last_activity.synced_remote_timestamp` 列，该列有多个写入器
 * 并导致一个消费者的标记抑制了所有其他消费者的读取。每个条目跟踪其针对给定 [ActivityType]
 * 远程 `remote_timestamp` 的自己的"已同步至"位置。实现将检查点键控在活动连接的提供者上，
 * 因此 Trakt 和未来的 Simkl 账户不会冲突；调用方永远不需要传递提供者，添加提供者也不需要调用站点变更。
 */
public interface ActivitySyncRepository {

    /**
     * [activityType] 的远程时间戳是否领先于 [consumerId] 在活动提供者上的检查点。
     *
     * 当尚不存在检查点（首次同步）时返回 true。当未连接提供者时返回 false，
     * 或此活动未记录远程时间戳（从未获取）；调用方必须先运行活动获取再检查此门控。
     */
    public suspend fun isAheadOf(consumerId: String, activityType: ActivityType): Boolean

    /**
     * 在活动提供者下，将 [consumerId] 记录为已同步至 [activityType] 的当前远程时间戳。
     * 未连接提供者或未记录远程时间戳时无操作。
     */
    public suspend fun markSyncedTo(consumerId: String, activityType: ActivityType)

    /**
     * 活动提供者的 [consumerId] 检查点针对 [activityType] 的上次同步时间戳，
     * 或未连接提供者或不存在检查点时为 null。供需要增量"since"参数的消费者用作查询游标。
     */
    public suspend fun getSyncTimestamp(consumerId: String, activityType: ActivityType): Instant?

    /**
     * 跨所有提供者移除每个检查点。从 `LogoutInteractor` 调用，
     * 以便账户切换不会将之前账户的位置带入新账户的同步。
     */
    public suspend fun clearAll()
}

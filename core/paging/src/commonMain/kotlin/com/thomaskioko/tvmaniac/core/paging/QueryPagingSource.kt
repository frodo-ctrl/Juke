package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterBase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.properties.Delegates

internal abstract class QueryPagingSource<Key : Any, RowType : Any> :
    PagingSource<Key, RowType>(), Query.Listener {

    protected var currentQuery: Query<RowType>? by
        Delegates.observable(null) { _, old, new ->
            old?.removeListener(this)
            new?.addListener(this)
        }

    init {
        registerInvalidatedCallback {
            currentQuery?.removeListener(this)
            currentQuery = null
        }
    }

    final override fun queryResultsChanged() = invalidate()
}

/**
 * 创建一个 [PagingSource]，根据 [queryProvider] 生成的查询对结果进行分页。
 * [queryProvider] 返回的查询应期望执行基于 SQL offset/limit 的分页。
 * 因此，需要 [countQuery] 来计算页码和页面偏移量。
 * [initialOffset] 是开始分页的初始偏移量。
 *
 * [queryProvider] 返回的示例查询可能如下所示：
 * ```sql
 * SELECT value FROM numbers
 * LIMIT 10
 * OFFSET 100;
 * ```
 *
 * 查询将在 [context] 上执行。
 */
@Suppress("FunctionName")
@JvmName("QueryPagingSourceInt")
@JvmOverloads
public fun <RowType : Any> QueryPagingSource(
    countQuery: Query<Int>,
    transacter: TransacterBase,
    context: CoroutineContext,
    queryProvider: (limit: Int, offset: Int) -> Query<RowType>,
    initialOffset: Int = 0,
): PagingSource<Int, RowType> = OffsetQueryPagingSource(
    queryProvider,
    countQuery,
    transacter,
    context,
    initialOffset,
)

/**
 * [QueryPagingSource] 的变体，接受 [Long] 而非 [Int] 作为 [countQuery]、
 * [queryProvider] 和 [initialOffset]。
 *
 * 如果 [countQuery] 的结果超过 [Int.MAX_VALUE]，则计数会被截断为此 [Long] 值的低 32 位。
 *
 * @see toInt
 */
@Suppress("FunctionName")
@JvmName("QueryPagingSourceLong")
@JvmOverloads
public fun <RowType : Any> QueryPagingSource(
    countQuery: Query<Long>,
    transacter: TransacterBase,
    context: CoroutineContext,
    queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    initialOffset: Long = 0,
): PagingSource<Int, RowType> = OffsetQueryPagingSource(
    { limit, offset -> queryProvider(limit.toLong(), offset.toLong()) },
    countQuery.toInt(),
    transacter,
    context,
    initialOffset.toInt(),
)

private fun Query<Long>.toInt(): Query<Int> = object : Query<Int>({ cursor -> mapper(cursor).toInt() }) {
    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>) = this@toInt.execute(mapper)

    override fun addListener(listener: Listener) = this@toInt.addListener(listener)

    override fun removeListener(listener: Listener) = this@toInt.removeListener(listener)
}

/**
 * 创建一个 [PagingSource]，根据 [queryProvider] 生成的查询对结果进行分页。
 * [queryProvider] 返回的查询应期望执行键集分页。因此，查询应排列为无歧义的 `ORDER BY` 子句。
 * [Key] 必须是行排序所依据的唯一子句。出于性能原因，[Key] 上应存在索引。
 *
 * [pageBoundariesProvider] 是一个回调，它生成包含 [Key] 项的查询，指定每个页面边界在完整数据集中的位置。例如：
 *
 * 数据集 `[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]` 升序排列、页面大小为 2，将产生页面边界 `[0, 2, 4, 6, 8]`。
 *
 * 数据集 `[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]` 升序排列、页面大小为 3，将产生页面边界 `[0, 3, 6, 9]`。
 *
 * [pageBoundariesProvider] 的回调可能包含一个 `anchor` 键，如果存在，应出现在结果查询中。
 *
 * 因为页面边界是预先计算的，[PagingConfig.initialLoadSize] 应匹配 [PagingConfig.pageSize]。
 * 否则会导致意外的页面大小，因为 [pageBoundariesProvider] 在对此源的 [PagingSource.load] 首次调用时仅调用一次。
 *
 * 通常，只能使用 SQLite 窗口函数生成页面边界。
 * 生成如上所示页面边界的示例查询如下所示。
 *
 * ```sql
 * SELECT value
 * FROM (
 *   SELECT
 *     value,
 *     CASE
 *       WHEN ((row_number() OVER(ORDER BY value ASC) - 1) % :limit) = 0 THEN 1
 *       WHEN value = :anchor THEN 1
 *       ELSE 0
 *     END page_boundary
 *   FROM numbers
 *   ORDER BY value ASC
 * )
 * WHERE page_boundary = 1;
 * ```
 *
 * SQLite 窗口查询从版本 3.25.0 开始可用。因此，消费应用可能需要设置 minSdk 为 30，
 * 或捆绑一个独立于操作系统提供模块的 SQLite 模块。
 *
 * [queryProvider] 返回的示例查询可能如下所示：
 * ```sql
 * SELECT value FROM numbers
 * WHERE value >= :beginInclusive AND (value < :endExclusive OR :endExclusive IS NULL)
 * ORDER BY value ASC;
 * ```
 *
 * 查询将在 [context] 上执行。
 *
 * 此 [PagingSource] _不_ 支持跳转。如果你的用例需要跳转，请使用基于偏移量的 [QueryPagingSource] 函数。
 */
@Suppress("FunctionName")
public fun <Key : Any, RowType : Any> QueryPagingSource(
    transacter: TransacterBase,
    context: CoroutineContext,
    pageBoundariesProvider: (anchor: Key?, limit: Long) -> Query<Key>,
    queryProvider: (beginInclusive: Key, endExclusive: Key?) -> Query<RowType>,
): PagingSource<Key, RowType> = KeyedQueryPagingSource(
    queryProvider,
    pageBoundariesProvider,
    transacter,
    context,
)

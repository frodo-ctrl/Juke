package com.thomaskioko.tvmaniac.data.watchproviders.implementation

/**
 * 将 TMDB 观看提供者变体合并为单一规范品牌条目。
 *
 * TMDB 以多种方式列出同一品牌：规范服务（"MGM Plus"、"Amazon Prime Video"）、
 * 频道变体（"MGM+ Amazon Channel"、"MGM Plus Roku Premium Channel"）和层级变体
 * （"Amazon Prime Video with Ads"）。在所有这些中，规范品牌是最短的词序列前缀——
 * 每个变体都用一个或多个额外的词扩展该前缀。
 *
 * 对于每个条目，如果任何其他条目的规范化词形成此条目的严格前缀，
 * 则此条目是该较短品牌的变体并被丢弃。保留较短的条目。当两个条目规范化为相同的词
 * （例如 "MGM+" 和 "MGM Plus"，因为 `+` 规范化为 ` Plus`）时，第一个出现的保留
 * 以保持输入顺序。
 *
 * 词前缀匹配是动态的：没有硬编码的后缀列表，因此新的 TMDB 变体
 * （例如 "Hulu with Live TV"、"Paramount+ with Showtime"）只要同一 payload 中存在规范品牌，
 * 就会自动合并。
 */
internal inline fun <T> List<T>.dedupedByBrand(name: (T) -> String?): List<T> {
    if (isEmpty()) return this
    val words: List<List<String>> = map { name(it).orEmpty().toBrandWords() }
    val keep = BooleanArray(size) { true }

    for (i in indices) {
        val iWords = words[i]
        if (iWords.isEmpty()) {
            keep[i] = false
            continue
        }
        for (j in indices) {
            if (i == j) continue
            val jWords = words[j]
            if (jWords.size < iWords.size && jWords.isWordPrefixOf(iWords)) {
                keep[i] = false
                break
            }
        }
    }

    val seen = mutableSetOf<List<String>>()
    return filterIndexed { i, _ -> keep[i] && seen.add(words[i]) }
}

private fun String.toBrandWords(): List<String> =
    replace("+", " Plus")
        .lowercase()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

private fun List<String>.isWordPrefixOf(other: List<String>): Boolean =
    other.take(size) == this

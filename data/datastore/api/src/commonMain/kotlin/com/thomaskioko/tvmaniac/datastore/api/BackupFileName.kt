package com.thomaskioko.tvmaniac.datastore.api

public object BackupFileName {
    public val Default: String = "tvmaniac-backup.json"
    private const val EXTENSION = ".json"
    private val FORBIDDEN = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

    /**
     * @return 可用于写入备份的名称，当 [name] 无法用作备份名时返回 null。
     */
    public fun sanitize(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null
        if (trimmed.any { it in FORBIDDEN }) return null
        if (trimmed == "." || trimmed == "..") return null
        return if (trimmed.endsWith(EXTENSION)) trimmed else "$trimmed$EXTENSION"
    }
}

package com.thomaskioko.root.nav

public interface NotificationRationale {
    /**
     * 如果尚未询问用户且当前没有显示任何理由，则在下一个空闲刻度上显示通知权限理由。
     * 可以安全地重复调用；当不满足前置条件时为无操作。
     */
    public suspend fun showIfNeeded()
}

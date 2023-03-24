package com.lib.notification_manager.data

import android.graphics.drawable.Drawable

data class NotificationModel(
    val id: Int,
    val key: String,
    val icon: Drawable?,
    val title: String?,
    val text: String?,
    val applicationName: String?
)
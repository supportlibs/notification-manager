package com.lib.notification_manager.data

data class NotificationModelWrapper(
    val notificationModel: NotificationModel,
    val count: Int
){
    override fun toString(): String = "App name: ${notificationModel.applicationName}, Count: $count"
}
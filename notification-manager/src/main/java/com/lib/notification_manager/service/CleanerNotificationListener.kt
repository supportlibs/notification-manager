package com.lib.notification_manager.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.lib.notification_manager.manager.NotificationCleanerManager

class CleanerNotificationListener : NotificationListenerService() {

    lateinit var notificationCleanerManager: NotificationCleanerManager

    override fun onCreate() {
        super.onCreate()
        notificationCleanerManager = NotificationCleanerManager(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        addNotification(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        notificationCleanerManager.onNotificationRemoved(sbn)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        setupRemoveAction()
        activeNotifications.forEachIndexed { i, notification ->
            Log.d("TAG_DEV", "=== NOTIFICATION NUM ${i + 1} ===")
            for (key in notification.notification.extras.keySet()){
                Log.d("TAG_DEV", "Key: $key, value: ${notification.notification.extras.get(key)}")
            }
            addNotification(notification)
            addNotificationToDelete(notification)
        }
    }

    private fun setupRemoveAction() {
        notificationCleanerManager.removeAction = { notificationKeys ->
            cancelNotifications(notificationKeys)
        }
    }

    private fun addNotification(sbn: StatusBarNotification) {
        if (sbn.isClearable) {
            notificationCleanerManager.addNotification(sbn, notificationCleanerManager.notificationsList)
        }
    }

    private fun addNotificationToDelete(sbn: StatusBarNotification) {
            notificationCleanerManager.addNotification(sbn, notificationCleanerManager.notificationsListToDelete)
    }
}
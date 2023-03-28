package com.lib.notification_manager.manager

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.service.notification.StatusBarNotification
import androidx.core.content.res.ResourcesCompat
import com.lib.notification_manager.data.NotificationModel
import com.lib.notification_manager.data.NotificationModelWrapper

class NotificationCleanerManager(
    private val context: Context
) {

    companion object {
        const val ACTION_TOO_MANY_NOTIFICATIONS = "ACTION_TOO_MANY_NOTIFICATIONS"
        const val NOTIFICATION_SIMPLE_NOTICLEANER_CODE = 1337


        // TODO Change that singleton logic because of memory leak
        // private volatile instance variable to hold the singleton instance
        @Volatile
        lateinit var INSTANCE: NotificationCleanerManager

        // public function to retrieve the singleton instance
        fun getInstance(context: Context): NotificationCleanerManager {
            // Check if the instance is already created
            if (!this::INSTANCE.isInitialized) {
                // synchronize the block to ensure only one thread can execute at a time
                synchronized(this) {
                    // check again if the instance is already created
                    if (!this::INSTANCE.isInitialized) {
                        // create the singleton instance
                        INSTANCE = NotificationCleanerManager(context)
                    }
                }
            }
            // return the singleton instance
            return INSTANCE
        }
    }

    var showNotificationAction: () -> Unit = {}

    private val sharePreferencesManager = SharePreferencesManager(context)
    val notificationsList = mutableListOf<NotificationModel>()
    val notificationsListToDelete = mutableListOf<NotificationModel>()
    var removeAction: ((NotificationKeys: Array<String>) -> Unit) = {}
    var onRemovedAction: (() -> Unit) = {}
    val notificationCount: Int
        get() = notificationsList.size

    fun addNotification(sbn: StatusBarNotification, list: MutableList<NotificationModel>) {
        val packageManager = context.packageManager
        val title = sbn.notification.extras.getCharSequence("android.title")
        val titleBig = sbn.notification.extras.getCharSequence("android.title.big")
        val text = sbn.notification.extras.getCharSequence("android.text")
        val bigText = sbn.notification.extras.getCharSequence("android.bigText")
        val summaryText = sbn.notification.extras.getCharSequence("android.summaryText")
        val iconId = sbn.notification.extras.getInt("android.icon")

        val finalText = text ?: bigText ?: summaryText ?: title ?: titleBig ?: '-'
        val icon = try {
            val resources = packageManager.getResourcesForApplication(sbn.packageName)
            ResourcesCompat.getDrawable(resources, iconId, null)
        } catch (e: NotFoundException) {
            val appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            appList.find { it.packageName == sbn.packageName }?.loadIcon(packageManager)
        }

        val applicationInfo = packageManager.getApplicationInfo(sbn.packageName, 0)
        val applicationName = packageManager.getApplicationLabel(applicationInfo).toString()

        val notification = NotificationModel(
            id = sbn.id,
            key = sbn.key,
            icon = icon,
            title = title.toString(),
            text = finalText.toString(),
            applicationName = applicationName
        )
        if (!list.any { it.key == notification.key }
            && !notification.key.contains(context.packageName)) {
            list.add(notification)
        }

        if (notificationCount > 7
            && System.currentTimeMillis() > sharePreferencesManager.notificationCleanerTimestamp + 20 * 60 * 1000
            && !isRunning(context)
        ) {
            showNotification()
        }
    }

    private fun isRunning(context: Context): Boolean {
        val activityManager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val tasks: List<ActivityManager.RunningTaskInfo> = activityManager.getRunningTasks(Int.MAX_VALUE)

        for (task in tasks) {
            if (context.packageName.equals(task.baseActivity?.packageName, true)) return true
        }

        return false
    }

    fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            notificationsList.removeAll { it.id == sbn.id }
            onRemovedAction()
            if (notificationCount <= 4) hideNotification()
        }
    }

    fun cancelNotifications(checkedNotificationsKeys: Array<String>) {
        removeAction(checkedNotificationsKeys)
    }

    fun cancelAllAvailableNotifications() {
        removeAction(notificationsListToDelete.map { it.key }.toTypedArray())
    }

    private fun showNotification() {
        showNotificationAction.invoke()
        sharePreferencesManager.notificationCleanerTimestamp = System.currentTimeMillis()
    }

    private fun hideNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_SIMPLE_NOTICLEANER_CODE)
    }

    fun getUniqueNotificationsWithCount(): List<NotificationModelWrapper> =
        notificationsList
            .groupBy { it.applicationName }
            .map {
                NotificationModelWrapper(
                    it.value.first(),
                    it.value.size
                )
            }
}
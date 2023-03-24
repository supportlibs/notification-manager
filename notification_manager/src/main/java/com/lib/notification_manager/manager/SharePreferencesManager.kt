package com.lib.notification_manager.manager

import android.content.Context
import android.content.SharedPreferences

class SharePreferencesManager (private val appContext: Context) {

    companion object {
        private const val SHARED_PREFERENCES_NAME = "notificationManagerSharPref"
        private const val NOTIFICATION_CLEANER_TIMESTAMP = "NOTIFICATION_CLEANER_TIMESTAMP"
    }

    private val preferences = appContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()

    var notificationCleanerTimestamp: Long
        get() = preferences.getLong(NOTIFICATION_CLEANER_TIMESTAMP, 0)
        set(value) {
            editor.putLong(NOTIFICATION_CLEANER_TIMESTAMP, value)
            editor.apply()
        }
}
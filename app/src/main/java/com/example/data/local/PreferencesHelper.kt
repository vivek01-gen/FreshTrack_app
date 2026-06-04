package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("FreshTrackPrefs", Context.MODE_PRIVATE)

    var language: String
        get() = prefs.getString("language", "en") ?: "en"
        set(value) = prefs.edit().putString("language", value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var isNotificationsOn: Boolean
        get() = prefs.getBoolean("notifications_on", true)
        set(value) = prefs.edit().putBoolean("notifications_on", value).apply()

    var isFirstTimeUser: Boolean
        get() = prefs.getBoolean("is_first_time_user", true)
        set(value) = prefs.edit().putBoolean("is_first_time_user", value).apply()

    var isUserLoggedIn: Boolean
        get() = prefs.getBoolean("is_user_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_user_logged_in", value).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}

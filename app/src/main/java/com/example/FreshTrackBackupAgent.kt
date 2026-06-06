package com.example

import android.app.backup.BackupAgentHelper
import android.app.backup.FileBackupHelper
import android.app.backup.SharedPreferencesBackupHelper

class FreshTrackBackupAgent : BackupAgentHelper() {
    override fun onCreate() {
        // Register helper for SharedPreferences
        val prefsHelper = SharedPreferencesBackupHelper(this, "FreshTrackPrefs")
        addHelper("prefs", prefsHelper)

        // Register helper for Room Database
        // Databases are stored relative to the app's databases folder.
        // Direct backup agent uses files list inside /data/data/<package>/databases/
        val dbHelper = FileBackupHelper(
            this,
            "../../databases/freshtrack_database",
            "../../databases/freshtrack_database-wal",
            "../../databases/freshtrack_database-shm"
        )
        addHelper("database", dbHelper)
    }
}

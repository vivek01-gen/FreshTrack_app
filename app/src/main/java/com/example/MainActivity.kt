package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.receiver.ExpiryAlarmReceiver
import com.example.ui.FreshTrackApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled gracefully
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Perform immediate silent app-launch security integrity audit
        com.example.security.SecurityAuditor.executeSilentAudit(this)

        // Schedule daily Alarm at 10 AM for local expiry notifications
        try {
            ExpiryAlarmReceiver.scheduleDailyAlarm(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Request modern Android 13 post notifications permission
        checkAndRequestNotificationPermission()

        // Check for post-restore scenario (restored data but uninstalled alarms)
        val devicePrefs = getSharedPreferences("device", Context.MODE_PRIVATE)
        val alarmsRegistered = devicePrefs.getBoolean("alarms_registered_for_install", false)
        if (!alarmsRegistered) {
            val db = com.example.data.local.AppDatabase.getDatabase(this)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val items = db.groceryDao().getAllItems()
                    if (items.isNotEmpty()) {
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                        val today = java.util.Calendar.getInstance().apply {
                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                            set(java.util.Calendar.MINUTE, 0)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }

                        var rescheduledCount = 0
                        for (item in items) {
                            try {
                                val expDate = sdf.parse(item.expiryDate) ?: continue
                                val expCal = java.util.Calendar.getInstance().apply {
                                    time = expDate
                                    set(java.util.Calendar.HOUR_OF_DAY, 9)
                                    set(java.util.Calendar.MINUTE, 0)
                                    set(java.util.Calendar.SECOND, 0)
                                    set(java.util.Calendar.MILLISECOND, 0)
                                }

                                if (expCal.timeInMillis >= today.timeInMillis) {
                                    com.example.receiver.AlarmUtils.scheduleAlarmsForItem(this@MainActivity, item)
                                    rescheduledCount++
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "Failed to reschedule item: ${item.name}", e)
                            }
                        }
                        android.util.Log.d("MainActivity", "Post-Restore/Reinstall: Rescheduled alarms for $rescheduledCount items.")
                    }
                    devicePrefs.edit().putBoolean("alarms_registered_for_install", true).apply()
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Failed to run post-restore alarm check", e)
                }
            }
        }

        setContent {
            FreshTrackApp()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

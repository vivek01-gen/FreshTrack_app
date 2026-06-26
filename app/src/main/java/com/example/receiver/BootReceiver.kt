package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "System finished booting. Automatically re-registering all non-expired grocery item alarms...")

            val pendingResult = goAsync()
            val db = AppDatabase.getDatabase(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Perform silent security integrity audit on device boot complete
                    com.example.security.SecurityAuditor.executeSilentAudit(context)

                    val items = db.groceryDao().getAllItems()
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    var rescheduledCount = 0
                    for (item in items) {
                        try {
                            val expDate = sdf.parse(item.expiryDate) ?: continue
                            val expCal = Calendar.getInstance().apply {
                                time = expDate
                                set(Calendar.HOUR_OF_DAY, 9)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            // If expiry is today or in the future, reschedule alarms
                            if (expCal.timeInMillis >= today.timeInMillis) {
                                AlarmUtils.scheduleAlarmsForItem(context, item)
                                rescheduledCount++
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse or reschedule item: ${item.name}", e)
                        }
                    }
                    Log.d(TAG, "Re-scheduling process complete. Rescheduled alarms for $rescheduledCount items.")
                } catch (e: Exception) {
                    Log.e(TAG, "Critical failure during BootReceiver re-scheduling", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

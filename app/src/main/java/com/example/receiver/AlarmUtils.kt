package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.model.GroceryItem
import java.text.SimpleDateFormat
import java.util.*

object AlarmUtils {
    private const val TAG = "AlarmUtils"

    fun scheduleAlarmsForItem(context: Context, item: GroceryItem) {
        if (item.id == 0L) {
            Log.e(TAG, "Cannot schedule alarm for item with ID 0. Make sure item is saved to database first.")
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val expiryDate = try {
            sdf.parse(item.expiryDate)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing expiry date: ${item.expiryDate}", e)
            null
        } ?: return

        // Set the reference expiry time to 9:00 AM of the expiry day
        val expiryCalendar = Calendar.getInstance().apply {
            time = expiryDate
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val expiryTimeMs = expiryCalendar.timeInMillis
        val alarmTimes = mapOf(
            "24h" to expiryTimeMs - 24 * 60 * 60 * 1000L,
            "12h" to expiryTimeMs - 12 * 60 * 60 * 1000L,
            "exact" to expiryTimeMs
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        alarmTimes.forEach { (type, triggerTimeMs) ->
            // Only schedule if the alarm is in the future
            if (triggerTimeMs > System.currentTimeMillis()) {
                val intent = Intent(context, ExpiryReceiver::class.java).apply {
                    putExtra("itemId", item.id)
                    putExtra("itemName", item.name)
                    putExtra("alarmType", type)
                    putExtra("expiryDate", item.expiryDate)
                }

                // Generate a unique request code for each of the three alarms
                val requestCode = getRequestCode(item.id, type)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerTimeMs,
                                pendingIntent
                            )
                        } else {
                            alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerTimeMs,
                                pendingIntent
                            )
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTimeMs,
                            pendingIntent
                        )
                    } else {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerTimeMs,
                            pendingIntent
                        )
                    }
                    Log.d(TAG, "Scheduled $type alarm for '${item.name}' (ID: ${item.id}) at ${Date(triggerTimeMs)}")
                } catch (e: SecurityException) {
                    Log.e(TAG, "SecurityException scheduling exact alarm, falling back to non-exact", e)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTimeMs,
                            pendingIntent
                        )
                    } else {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerTimeMs,
                            pendingIntent
                        )
                    }
                }
            } else {
                Log.d(TAG, "Skipping $type alarm for '${item.name}' (ID: ${item.id}) because trigger time is in the past.")
            }
        }
    }

    fun cancelAlarmsForItem(context: Context, item: GroceryItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val types = listOf("24h", "12h", "exact")

        types.forEach { type ->
            val intent = Intent(context, ExpiryReceiver::class.java)
            val requestCode = getRequestCode(item.id, type)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d(TAG, "Cancelled $type alarm for item ID: ${item.id}")
            }
        }
    }

    private fun getRequestCode(itemId: Long, type: String): Int {
        val offset = when (type) {
            "24h" -> 0
            "12h" -> 1
            else -> 2 // "exact"
        }
        return (itemId * 3 + offset).toInt()
    }
}

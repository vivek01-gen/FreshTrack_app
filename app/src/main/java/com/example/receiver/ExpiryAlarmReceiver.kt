package com.example.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpiryAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleDailyAlarm(context)
            return
        }

        val prefs = context.getSharedPreferences("FreshTrackPrefs", Context.MODE_PRIVATE)
        val notifEnabled = prefs.getBoolean("notifications_on", true)
        if (!notifEnabled) return

        // Process expiry checks in a background coroutine
        val db = AppDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            val items = db.groceryDao().getAllItems()
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var expiringIn2DaysCount = 0
            var expiringTodayCount = 0
            var expiredCount = 0
            val itemNames = mutableListOf<String>()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            for (item in items) {
                try {
                    val expDate = sdf.parse(item.expiryDate) ?: continue
                    val expCal = Calendar.getInstance().apply {
                        time = expDate
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val diffTime = expCal.timeInMillis - today.timeInMillis
                    val daysLeft = (diffTime / (1000 * 60 * 60 * 24)).toInt()

                    if (daysLeft == 2) {
                        expiringIn2DaysCount++
                        itemNames.add("${item.name} (in 2d)")
                    } else if (daysLeft == 0) {
                        expiringTodayCount++
                        itemNames.add("${item.name} (today)")
                    } else if (daysLeft < 0) {
                        expiredCount++
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (expiringIn2DaysCount > 0 || expiringTodayCount > 0 || expiredCount > 0) {
                // Determine language to present notifications
                val lang = prefs.getString("language", "en") ?: "en"
                val title = if (lang == "hi") "🌿 FreshTrak खाद्य अलर्ट!" else "🌿 FreshTrak Expiry Alert!"
                
                val body = StringBuilder()
                if (lang == "hi") {
                    if (expiringTodayCount > 0) body.append("$expiringTodayCount आज खराब होने वाले हैं! ")
                    if (expiringIn2DaysCount > 0) body.append("$expiringIn2DaysCount 2 दिन में खराब हो रहे हैं (${itemNames.joinToString(", ")}). ")
                    if (expiredCount > 0) body.append("$expiredCount खराब हो चुके हैं!")
                } else {
                    if (expiringTodayCount > 0) body.append("$expiringTodayCount item(s) expiring TODAY! ")
                    if (expiringIn2DaysCount > 0) body.append("$expiringIn2DaysCount expiring in 2 days (${itemNames.joinToString(", ")}). ")
                    if (expiredCount > 0) body.append("$expiredCount expired item(s) need attention!")
                }

                sendNotification(context, title, body.toString())
            }
        }

        // Reschedule to ensure it always fires next day at 10 AM even if system missed repetition
        scheduleDailyAlarm(context)
    }

    private fun sendNotification(context: Context, title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "freshtrack_expiry_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FreshTrak Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies about grocery items expiring soon"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    companion object {
        fun scheduleDailyAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ExpiryAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1101,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // If it's already past 10 AM today, schedule for tomorrow
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }
}

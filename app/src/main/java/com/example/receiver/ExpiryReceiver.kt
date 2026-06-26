package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.os.VibrationEffect
import androidx.core.app.NotificationCompat
import android.util.Log
import com.example.MainActivity

class ExpiryReceiver : BroadcastReceiver() {
    private val TAG = "ExpiryReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getLongExtra("itemId", -1L)
        val itemName = intent.getStringExtra("itemName") ?: "Grocery Item"
        val alarmType = intent.getStringExtra("alarmType") ?: "exact"
        val expiryDate = intent.getStringExtra("expiryDate") ?: ""

        Log.d(TAG, "Received expiry broadcast for item ID: $itemId ($itemName), type: $alarmType")

        if (itemId == -1L) {
            Log.e(TAG, "Invalid item ID received in broadcast.")
            return
        }

        // Determine language preference
        val prefs = context.getSharedPreferences("FreshTrackPrefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "en") ?: "en"

        val locale = java.util.Locale(lang)
        val config = android.content.res.Configuration(context.resources.configuration)
        config.setLocale(locale)
        val localizedContext = context.createConfigurationContext(config)

        // Localized title & description text using Android strings resources
        val (title, content) = when (alarmType) {
            "24h" -> {
                Pair(
                    localizedContext.getString(com.example.R.string.alarm_24h_title),
                    localizedContext.getString(com.example.R.string.alarm_24h_content, itemName, expiryDate)
                )
            }
            "12h" -> {
                Pair(
                    localizedContext.getString(com.example.R.string.alarm_12h_title),
                    localizedContext.getString(com.example.R.string.alarm_12h_content, itemName, expiryDate)
                )
            }
            else -> { // "exact"
                Pair(
                    localizedContext.getString(com.example.R.string.alarm_exact_title),
                    localizedContext.getString(com.example.R.string.alarm_exact_content, itemName, expiryDate)
                )
            }
        }

        sendHeadsUpNotification(context, itemId, alarmType, title, content)
    }

    private fun sendHeadsUpNotification(
        context: Context,
        itemId: Long,
        alarmType: String,
        title: String,
        content: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val channelId = "freshtrack_item_expiry_channel"

        // Sound URI
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        // Vibration pattern
        val vibePattern = longArrayOf(0, 500, 250, 500)

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Item Expiration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Heads-up alerts for items expiring within 24 hours, 12 hours, or today."
                enableLights(true)
                enableVibration(true)
                vibrationPattern = vibePattern
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("itemId", itemId)
        }

        val notificationId = (itemId * 10 + when (alarmType) {
            "24h" -> 0
            "12h" -> 1
            else -> 2
        }).toInt()

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // fallback default icon
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_MAX) // High priority for heads-up
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(vibePattern)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Try playing sound and vibrating explicitly to guarantee delivery
        try {
            val r = RingtoneManager.getRingtone(context, soundUri)
            r?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing ringtone custom sound directly", e)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(vibePattern, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(vibePattern, -1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error vibrating device", e)
        }

        notificationManager.notify(notificationId, builder.build())
        Log.d(TAG, "Dispatched notification ID: $notificationId for item ID: $itemId")
    }
}

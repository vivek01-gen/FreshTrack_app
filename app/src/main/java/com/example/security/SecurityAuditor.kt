package com.example.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Debug
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import java.io.File
import java.security.MessageDigest
import java.util.Locale

data class SecurityThreat(
    val id: String,
    val titleResId: Int,
    val descriptionResId: Int
)

object SecurityAuditor {

    private const val SEC_PREFS = "security_integrity_audit_prefs"
    private const val KEY_ACTIVE_THREAT = "active_threat_id"
    private const val CHANNEL_ID = "freshtrack_security_channel"

    @Volatile
    private var simulatedThreat: SecurityThreat? = null

    // Known SHA-256 fingerprint of Developer Vivek Jha's production signature and the AI Studio debug key
    private val VERIFIED_SIGNATURES = setOf(
        "B8:FA:7E:6D:77:24:D1:D8:AF:71:A6:49:EE:41:88:AC:DC:7A:B3:63:F5:EF:5E:28:C0:B0:1B:32:05:78:E2:20", // Dev Key
        "F8:AA:7A:89:12:1A:11:AC:56:0F:7B:69:BD:A1:FE:24:28:BB:BB:3C:93:CF:13:D7:88:FF:C2:AB:3E:C2:5F:AA"  // Standard Debug fallback
    )

    /**
     * Set a simulated threat for verification and demonstration purposes.
     */
    fun setSimulatedThreat(threat: SecurityThreat?) {
        simulatedThreat = threat
    }

    /**
     * Checks if the device appears to be rooted (using lightweight, safe heuristics)
     */
    fun isDeviceRooted(): Boolean {
        // Method 1: Check build tags
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // Method 2: Check standard paths for SU binary
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }

        // Method 3: Try to execute su command in terminal
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = process.inputStream.bufferedReader()
            val text = reader.readLine()
            reader.close()
            text != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    /**
     * Checks if a debugger is active or if the app is flagged as debuggable in release configuration
     */
    fun isDebuggerActive(context: Context): Boolean {
        if (Debug.isDebuggerConnected()) return true
        
        // Also check if debuggable flag is enabled
        return try {
            val flags = context.applicationInfo.flags
            (flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtains the SHA-256 signature fingerprint of this installed package
     */
    fun getSignatureSHA256(context: Context): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signers = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signers != null && signers.isNotEmpty()) {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signers[0].toByteArray())
                digest.joinToString(":") { String.format(Locale.US, "%02X", it) }
            } else {
                "NO_SIGNATURE_FOUND"
            }
        } catch (e: Exception) {
            "ERROR: ${e.message}"
        }
    }

    /**
     * Validates if the signature of the app matches the official developer credentials.
     * Returns true if it matches our verified signature set or standard debug.
     */
    fun isSignatureValid(context: Context): Boolean {
        val currentSig = getSignatureSHA256(context)
        if (VERIFIED_SIGNATURES.contains(currentSig)) return true
        
        val isDebugBuild = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebugBuild) return true

        return false
    }

    /**
     * Performs a comprehensive, silent background security audit.
     * Checks Device Security/Root, Sandbox/Debug, App Signature, and App Integrity.
     * Returns a SecurityThreat if any check fails, or null if secure.
     */
    fun performSecurityAudit(context: Context): SecurityThreat? {
        // Return simulated threat if active
        val simulated = simulatedThreat
        if (simulated != null) return simulated

        val isDebugBuild = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        // 1. Official Signature Verification
        if (!isSignatureValid(context)) {
            return SecurityThreat(
                id = "invalid_signature",
                titleResId = com.example.R.string.security_threat_signature_title,
                descriptionResId = com.example.R.string.security_threat_signature_desc
            )
        }

        // 2. Sandbox & Debugger Validation
        // Only trigger warning on release configurations to prevent blocking emulator preview builds
        if (!isDebugBuild && isDebuggerActive(context)) {
            return SecurityThreat(
                id = "debugger_detected",
                titleResId = com.example.R.string.security_threat_debug_title,
                descriptionResId = com.example.R.string.security_threat_debug_desc
            )
        }

        // 3. Device Security / Root detection
        // Only trigger warning on release configurations to prevent blocking developers using emulators
        if (!isDebugBuild && isDeviceRooted()) {
            return SecurityThreat(
                id = "root_detected",
                titleResId = com.example.R.string.security_threat_root_title,
                descriptionResId = com.example.R.string.security_threat_root_desc
            )
        }

        return null
    }

    /**
     * Executes the silent security audit, persists any threat state, and triggers a high-priority system notification.
     */
    fun executeSilentAudit(context: Context) {
        val threat = performSecurityAudit(context)
        val prefs = context.getSharedPreferences(SEC_PREFS, Context.MODE_PRIVATE)
        
        if (threat != null) {
            prefs.edit().putString(KEY_ACTIVE_THREAT, threat.id).apply()
            triggerSecurityNotification(context, threat)
        } else {
            // Keep 100% silent on pass
            prefs.edit().remove(KEY_ACTIVE_THREAT).apply()
        }
    }

    /**
     * Retrieves the persisted active threat if any exists.
     */
    fun getActiveThreat(context: Context): SecurityThreat? {
        val prefs = context.getSharedPreferences(SEC_PREFS, Context.MODE_PRIVATE)
        val threatId = prefs.getString(KEY_ACTIVE_THREAT, null) ?: return null
        
        return when (threatId) {
            "invalid_signature" -> SecurityThreat(
                "invalid_signature",
                com.example.R.string.security_threat_signature_title,
                com.example.R.string.security_threat_signature_desc
            )
            "debugger_detected" -> SecurityThreat(
                "debugger_detected",
                com.example.R.string.security_threat_debug_title,
                com.example.R.string.security_threat_debug_desc
            )
            "root_detected" -> SecurityThreat(
                "root_detected",
                com.example.R.string.security_threat_root_title,
                com.example.R.string.security_threat_root_desc
            )
            else -> null
        }
    }

    /**
     * Triggers a high-priority, system-level Android Notification
     */
    private fun triggerSecurityNotification(context: Context, threat: SecurityThreat) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val vibePattern = longArrayOf(0, 800, 400, 800, 400, 800)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channelName = context.getString(com.example.R.string.security_alert_notification_channel_name)
            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts regarding system security and app integrity."
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
                vibrationPattern = vibePattern
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(threat.titleResId)
        val description = context.getString(threat.descriptionResId)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(vibePattern)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(999, builder.build())
    }
}

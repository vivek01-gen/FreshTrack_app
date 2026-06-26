package com.example.utils

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object FeedbackSender {

    /**
     * Silently sends a feedback/bug report email via Gmail SMTP in a background IO coroutine thread.
     */
    suspend fun sendFeedbackEmail(
        context: Context,
        name: String,
        userEmail: String,
        category: String,
        rating: Int?,
        message: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val host = "smtp.gmail.com"
            val port = "465"
            
            // Securely configured background transit account credentials for email relay
            val senderEmail = "freshtrack.app.feedback@gmail.com"
            val senderPassword = "wdqj dmsm eicp xqku" // Secure App Password for automatic background relay

            val properties = Properties().apply {
                put("mail.smtp.host", host)
                put("mail.smtp.port", port)
                put("mail.smtp.ssl.enable", "true")
                put("mail.smtp.auth", "true")
                put("mail.smtp.socketFactory.port", port)
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.socketFactory.fallback", "false")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, senderPassword)
                }
            })

            // Fetch package diagnostic information safely
            val appVersion = try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName
            } catch (e: Exception) {
                "v1.0.0"
            }
            val deviceModel = Build.MODEL
            val androidVersion = Build.VERSION.RELEASE
            val apiLevel = Build.VERSION.SDK_INT

            val emailSubject = "[FreshTrack Feedback] $category from $name"
            val emailBody = buildString {
                appendLine("=== FRESH TRACK FEEDBACK & BUG REPORT ===")
                appendLine("Name: $name")
                appendLine("User Email: $userEmail")
                appendLine("Category: $category")
                if (rating != null && rating > 0) {
                    appendLine("Star Rating: $rating / 5 ⭐")
                }
                appendLine()
                appendLine("--- User Message ---")
                appendLine(message)
                appendLine("--------------------")
                appendLine()
                appendLine("=== Technical Metadata (App & Device Diagnostic) ===")
                appendLine("App Version: $appVersion")
                appendLine("Device Model: $deviceModel")
                appendLine("Android OS Version: $androidVersion (API Level $apiLevel)")
                appendLine("Timestamp: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())}")
            }

            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail, "FreshTrack In-App Feedback"))
                setRecipient(Message.RecipientType.TO, InternetAddress("iam.vkjha.official@gmail.com"))
                subject = emailSubject
                setText(emailBody)
            }

            Transport.send(mimeMessage)
            true
        } catch (e: Exception) {
            Log.e("FeedbackSender", "Failed to send email via SMTP", e)
            false
        }
    }
}

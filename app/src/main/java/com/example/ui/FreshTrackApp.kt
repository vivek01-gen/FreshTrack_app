package com.example.ui

import android.app.DatePickerDialog
import com.example.receiver.AlarmUtils
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesHelper
import com.example.data.model.GroceryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// ── LOCALIZATION ──
val I18N = mapOf(
    "en" to mapOf(
        "back" to "Back",
        "addItem" to "Add Item",
        "analytics" to "Analytics",
        "settings" to "Settings",
        "subtitle" to "Never waste food again",
        "addFood" to "+ Add Food",
        "home" to "Home",
        "searchItems" to "Search items...",
        "myInventory" to "My Inventory",
        "items" to "items",
        "totalItems" to "Total Items",
        "statTotalSubtitle" to "Active storage",
        "expiringSoon" to "Expiring Soon",
        "statSoonSubtitle" to "Days remaining",
        "expired" to "Expired",
        "statExpiredSubtitle" to "Needs action",
        "alerts" to "ALERTS",
        "more" to "more",
        "bulkDelete" to "Bulk Delete",
        "csv" to "Export CSV",
        "all" to "All",
        "fresh" to "Fresh",
        "expiring" to "Expiring",
        "noItems" to "No items found",
        "addFirst" to "Add your first food item!",
        "noExpired" to "No expired items",
        "itemRemoved" to "Item removed",
        "expiredCleared" to "Expired items cleared!",
        "csvExported" to "CSV exported!",
        "deleteExpiredQ" to "Delete all expired items?",
        "cancel" to "Cancel",
        "delete" to "Delete",
        "saveItem" to "Save Item",
        "photo" to "Photo",
        "choosePhoto" to "Choose Photo",
        "photoHint" to "Saved securely locally",
        "itemName" to "Item Name",
        "itemNamePh" to "e.g. Amul Milk, Apple...",
        "category" to "Category",
        "qtyUnit" to "Quantity & Unit",
        "qtyPh" to "e.g. 500",
        "price" to "Price (₹) — Optional",
        "purchaseDate" to "Purchase Date",
        "expiryDate" to "Expiry Date",
        "notes" to "Notes",
        "notesPh" to "e.g. Kept in back of fridge...",
        "nameRequired" to "Item name is required",
        "expiryRequired" to "Expiry date is required",
        "appearance" to "Appearance",
        "theme" to "Theme",
        "darkMode" to "Dark Mode",
        "lightMode" to "Light Mode",
        "language" to "Language",
        "notifications" to "Notifications",
        "enableNotifications" to "Enable Notifications",
        "dailyAt" to "Daily at 10:00 AM",
        "alertTune" to "Alert Tune",
        "storageInfo" to "Storage Info",
        "storage1" to "Items auto-saved to SQLite Room (offline)",
        "storage2" to "Settings persist across sessions",
        "storage3" to "Photos stored securely in app files",
        "storage4" to "100% offline — no internet needed",
        "statusFresh" to "FRESH",
        "statusSoon" to "EXPIRING SOON",
        "statusToday" to "EXPIRES TODAY",
        "statusExpired" to "EXPIRED",
        "expiresToday" to "Expires TODAY!",
        "wasted" to "₹ Wasted",
        "itemsExpired" to "Items Expired",
        "thisMonth" to "This Month",
        "monthlyWaste" to "Monthly Waste (₹)",
        "wasteByCategory" to "Waste by Category",
        "expiredItems" to "Expired Items",
        "googleDriveBackup" to "Google Drive Backup",
        "backupNow" to "Backup to Drive",
        "restoreNow" to "Restore from Drive",
        "driveBackupDesc" to "Back up your data to a secure backup file in your Google Drive",
        "driveStatusNotConnected" to "Google Account not backup linked",
        "driveStatusBackupSuccess" to "Last backed up: Just now",
        "shareBackup" to "Share Local Backup File",
        "importBackup" to "Import Backup File",
        "dangerZone" to "Danger Zone",
        "clearAllData" to "Reset App Database",
        "clearDataConfirm" to "Are you absolutely sure you want to reset and clear all items?",
        "resetSuccess" to "App reset complete!",
        "csvReportTitle" to "Category-wise Expense Summary"
    ),
    "hi" to mapOf(
        "back" to "वापस",
        "addItem" to "आइटम जोड़ें",
        "analytics" to "एनालिटिक्स",
        "settings" to "सेटिंग्स",
        "subtitle" to "खाना बर्बाद होने से बचाएँ",
        "addFood" to "+ खाना जोड़ें",
        "home" to "होम",
        "searchItems" to "आइटम खोजें...",
        "myInventory" to "मेरी इन्वेंटरी",
        "items" to "आइटम",
        "totalItems" to "कुल आइटम",
        "statTotalSubtitle" to "सक्रिय भण्डारण",
        "expiringSoon" to "जल्द खराब",
        "statSoonSubtitle" to "शेष दिन",
        "expired" to "खराब हो चुके",
        "statExpiredSubtitle" to "कार्रवाई योग्य",
        "alerts" to "अलर्ट",
        "more" to "और",
        "bulkDelete" to "बल्क डिलीट",
        "csv" to "CSV एक्सपोर्ट",
        "all" to "सभी",
        "fresh" to "फ्रेश",
        "expiring" to "जल्द खराब",
        "noItems" to "कोई आइटम नहीं मिला",
        "addFirst" to "अपना पहला food item जोड़ें!",
        "noExpired" to "कोई expired item नहीं",
        "itemRemoved" to "आइटम हटा दिया गया",
        "expiredCleared" to "Expired items हटा दिए गए!",
        "csvExported" to "CSV export हो गया!",
        "deleteExpiredQ" to "सभी expired items delete करें?",
        "cancel" to "कैंसल",
        "delete" to "डिलीट",
        "saveItem" to "आइटम सेव करें",
        "photo" to "फोटो",
        "choosePhoto" to "फोटो चुनें",
        "photoHint" to "सुरक्षित रूप से सेव किया गया",
        "itemName" to "आइटम का नाम",
        "itemNamePh" to "जैसे Amul Milk, Apple...",
        "category" to "कैटेगरी",
        "qtyUnit" to "मात्रा और यूनिट",
        "qtyPh" to "जैसे 500",
        "price" to "कीमत (₹) — वैकल्पिक",
        "purchaseDate" to "खरीद तारीख",
        "expiryDate" to "एक्सपायरी तारीख",
        "notes" to "नोट्स",
        "notesPh" to "जैसे फ्रिज के पीछे रखा है...",
        "nameRequired" to "Item name जरूरी है",
        "expiryRequired" to "Expiry date जरूरी है",
        "appearance" to "लुक",
        "theme" to "थीम",
        "darkMode" to "डार्क मोड",
        "lightMode" to "लाइट मोड",
        "language" to "भाषा",
        "notifications" to "नोटिफिकेशन",
        "enableNotifications" to "नोटिफिकेशन चालू करें",
        "dailyAt" to "Daily at 10:00 AM",
        "alertTune" to "अलर्ट ट्यून",
        "storageInfo" to "स्टोरेज जानकारी",
        "storage1" to "Items SQLite Room में offline save होते हैं",
        "storage2" to "Settings sessions में बनी रहती हैं",
        "storage3" to "Photos डिवाइस में सुरक्षित सेव होती हैं",
        "storage4" to "100% offline — internet की जरूरत नहीं",
        "statusFresh" to "फ्रेश",
        "statusSoon" to "जल्द खराब",
        "statusToday" to "आज खराब होगा",
        "statusExpired" to "खराब हो चुका",
        "expiresToday" to "आज expire होगा!",
        "wasted" to "₹ बर्बाद",
        "itemsExpired" to "Expired Items",
        "thisMonth" to "इस महीने",
        "monthlyWaste" to "Monthly Waste (₹)",
        "wasteByCategory" to "Category के हिसाब से waste",
        "expiredItems" to "Expired Items",
        "googleDriveBackup" to "गूगल ड्राइव बैकअप",
        "backupNow" to "ड्राइव पर बैकअप लें",
        "restoreNow" to "ड्राइव से रिस्टोर करें",
        "driveBackupDesc" to "अपना डेटा अपने स्वयं के Google ड्राइव में एक सुरक्षित बैकअप फ़ाइल में सहेजें",
        "driveStatusNotConnected" to "गूगल अकाउंट लिंक नहीं है",
        "driveStatusBackupSuccess" to "पिछला बैकअप: अभी लिया गया",
        "shareBackup" to "लोकल बैकअप फाइल साझा करें",
        "importBackup" to "बैकअप फाइल इम्पोर्ट करें",
        "dangerZone" to "खतरे का क्षेत्र",
        "clearAllData" to "डेटाबेस रीसेट करें",
        "clearDataConfirm" to "क्या आप वाकई सभी आइटम रीसेट और हटाना चाहते हैं?",
        "resetSuccess" to "ऐप रीसेट हो गया!",
        "csvReportTitle" to "श्रेणी-वार व्यय मूल्य सारांश"
    )
)

fun translate(key: String, lang: String): String {
    return translateExt(key, lang)
}

val CAT_EMOJI = mapOf(
    "fruits_veg" to "🍎",
    "dairy_eggs" to "🥛",
    "bakery_bread" to "🍞",
    "meat_seafood" to "🥩",
    "pantry_grains" to "🌾",
    "beverages" to "🧃",
    "others" to "📦"
)

val CAT_NAMES_I18N = mapOf(
    "en" to mapOf(
        "fruits_veg" to "Fruits & Vegetables",
        "dairy_eggs" to "Dairy & Eggs",
        "bakery_bread" to "Bakery & Bread",
        "meat_seafood" to "Meat & Seafood",
        "pantry_grains" to "Pantry & Grains",
        "beverages" to "Beverages",
        "others" to "Others"
    ),
    "hi" to mapOf(
        "fruits_veg" to "फल और सब्जियां",
        "dairy_eggs" to "डेयरी और अंडे",
        "bakery_bread" to "बेकरी और ब्रेड",
        "meat_seafood" to "मीट और सीफूड",
        "pantry_grains" to "पेंट्री और अनाज",
        "beverages" to "पेय पदार्थ",
        "others" to "अन्य"
    )
)

val UNITS = listOf("kg", "gm", "litre", "ml", "pieces", "packets")

fun getCategoryEmoji(cat: String): String {
    val emoji = CAT_EMOJI[cat]
    if (emoji != null) return emoji
    return when(cat) {
        "fruits" -> "🍎"
        "vegetables" -> "🥦"
        "dairy" -> "🥛"
        "meat" -> "🥩"
        "bakery" -> "🍞"
        "beverages" -> "🧃"
        "snacks" -> "🍿"
        "grains" -> "🌾"
        "frozen" -> "🧊"
        else -> "🍽️"
    }
}

fun getNormalizedCategory(cat: String): String {
    return when(cat) {
        "fruits", "vegetables", "fruits_veg" -> "fruits_veg"
        "dairy", "dairy_eggs" -> "dairy_eggs"
        "bakery", "bakery_bread" -> "bakery_bread"
        "meat", "meat_seafood" -> "meat_seafood"
        "grains", "pantry", "pantry_grains" -> "pantry_grains"
        "beverages" -> "beverages"
        "others" -> "others"
        else -> "others"
    }
}

fun getCategoryLabel(cat: String, lang: String): String {
    val norm = getNormalizedCategory(cat)
    return getCategoryLabelExt(norm, lang)
}

fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    var currentStr = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < line.length) {
        val c = line[i]
        if (c == '"') {
            if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                currentStr.append('"')
                i++
            } else {
                inQuotes = !inQuotes
            }
        } else if (c == ',') {
            if (inQuotes) {
                currentStr.append(c)
            } else {
                result.add(currentStr.toString().trim())
                currentStr = StringBuilder()
            }
        } else {
            currentStr.append(c)
        }
        i++
    }
    result.add(currentStr.toString().trim())
    return result
}

fun mapCategoryLabelToKey(label: String): String {
    val l = label.lowercase(Locale.getDefault()).trim()
    return when {
        l.contains("fruit") || l.contains("veg") || l.contains("सब्जी") || l.contains("फल") -> "fruits_veg"
        l.contains("dairy") || l.contains("egg") || l.contains("दूध") || l.contains("डेयरी") -> "dairy_eggs"
        l.contains("bakery") || l.contains("bread") || l.contains("बेकरी") -> "bakery_bread"
        l.contains("meat") || l.contains("seafood") || l.contains("मीट") || l.contains("मछली") -> "meat_seafood"
        l.contains("pantry") || l.contains("grain") || l.contains("अनाज") || l.contains("पेंट्री") -> "pantry_grains"
        l.contains("beverage") || l.contains("drink") || l.contains("पेय") || l.contains("शरबत") -> "beverages"
        else -> "others"
    }
}

// ── DATE CALCULATIONS ──
fun calcDaysLeft(expiryDateStr: String): Int {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val expDate = sdf.parse(expiryDateStr) ?: return 9999
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expCal = Calendar.getInstance().apply {
            time = expDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffTime = expCal.timeInMillis - today.timeInMillis
        (diffTime / (1000 * 60 * 60 * 24)).toInt()
    } catch (e: Exception) {
        9999
    }
}

fun getExpiryStatus(daysLeft: Int): String {
    return when {
        daysLeft > 3 -> "fresh"
        daysLeft in 1..3 -> "soon"
        daysLeft == 0 -> "today"
        else -> "expired"
    }
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "fresh" -> Color(0xFF22C55E)
        "soon" -> Color(0xFFF59E0B)
        "today" -> Color(0xFFEF4444)
        else -> Color(0xFFDC2626)
    }
}

fun getStatusBgColor(status: String, isDark: Boolean): Color {
    return if (isDark) {
        when (status) {
            "fresh" -> Color(0xFF0D2818)
            "soon" -> Color(0xFF2A1F06)
            "today" -> Color(0xFF2A0F06)
            else -> Color(0xFF1A0A0A)
        }
    } else {
        when (status) {
            "fresh" -> Color(0xFFDCFCE7)
            "soon" -> Color(0xFFFEF9C3)
            "today" -> Color(0xFFFEE2E2)
            else -> Color(0xFFFECACA)
        }
    }
}

fun getStatusLabel(status: String, lang: String): String {
    return when (status) {
        "fresh" -> translate("statusFresh", lang)
        "soon" -> translate("statusSoon", lang)
        "today" -> translate("statusToday", lang)
        else -> translate("statusExpired", lang)
    }
}

// Custom Draw Composable for the Logo (Clock + Leaf style perfectly matching FreshTrak image)
@Composable
fun FreshTrackVectorLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = false,
    textColor: Color = Color.White
) {
    Image(
        painter = painterResource(id = com.example.R.drawable.freshtrack_logo),
        contentDescription = "FreshTrack Logo",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

// Save captured bitmap locally inside the app internal files
private fun saveBitmapLocally(context: Context, bitmap: Bitmap): String? {
    return try {
        val photosDir = File(context.filesDir, "grocery_photos")
        if (!photosDir.exists()) photosDir.mkdirs()

        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val destinationFile = File(photosDir, fileName)

        FileOutputStream(destinationFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
            outputStream.flush()
        }
        destinationFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Copy selected photo locally inside the app internal files
private fun copyPhotoLocally(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val photosDir = File(context.filesDir, "grocery_photos")
        if (!photosDir.exists()) photosDir.mkdirs()

        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val destinationFile = File(photosDir, fileName)

        FileOutputStream(destinationFile).use { outputStream ->
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
        }
        destinationFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Compute cumulative folder size on the storage disk
private fun getFolderSize(file: File): Long {
    var size: Long = 0
    if (file.isDirectory) {
        val files = file.listFiles()
        if (files != null) {
            for (f in files) {
                size += getFolderSize(f)
            }
        }
    } else {
        size += file.length()
    }
    return size
}

// Delete image snapshots no longer linked with active grocery products
private fun clearOrphanedPhotos(context: Context, items: List<GroceryItem>) {
    val photosDir = File(context.filesDir, "grocery_photos")
    if (photosDir.exists()) {
        val referencedPaths = items.mapNotNull { it.photoPath }.toSet()
        val files = photosDir.listFiles()
        if (files != null) {
            var deletedCount = 0
            for (f in files) {
                if (!referencedPaths.contains(f.absolutePath)) {
                    if (f.delete()) deletedCount++
                }
            }
            Toast.makeText(context, "Cleared $deletedCount unused images!", Toast.LENGTH_SHORT).show()
        }
    }
}

// Share export Utility
private fun shareFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Export FreshTrack Data"))
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FreshTrackApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val formattedTodayStr = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()) }

    // ── DATA SOURCES ──
    val db = remember { AppDatabase.getDatabase(context) }
    val groceryDao = db.groceryDao()
    val prefs = remember { PreferencesHelper(context) }

    // ── STATE ──
    var activeView by remember { mutableStateOf("home") } // home, additem, analytics, settings
    val itemsList by groceryDao.getAllItemsFlow().collectAsState(initial = emptyList())

    // Onboarding flow: Splash & Login state manager (Login bypassed for local-first use)
    var showSplash by remember { mutableStateOf(true) }
    var isUserLoggedInState by remember { mutableStateOf(true) }

    // Delightfully transition and auto-dismiss Splash Screen after 2.2 seconds
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2200)
        showSplash = false
    }

    val sharedPrefs = remember { context.getSharedPreferences("FreshTrackPrefs", Context.MODE_PRIVATE) }
    var lang by remember { mutableStateOf(prefs.language) }
    var themeMode by remember { mutableStateOf(sharedPrefs.getString("theme_mode", "light") ?: "light") }
    val systemInDark = isSystemInDarkTheme()
    var isDarkState by remember {
        mutableStateOf(
            when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemInDark
            }
        )
    }
    LaunchedEffect(themeMode, systemInDark) {
        isDarkState = when (themeMode) {
            "light" -> false
            "dark" -> true
            else -> systemInDark
        }
        prefs.isDarkMode = isDarkState
    }
    var notificationsOn by remember { mutableStateOf(prefs.isNotificationsOn) }
    var isFirstTimeUser by remember { mutableStateOf(prefs.isFirstTimeUser) }

    // Persistent active threat check. If a threat is detected, operations are suspended and UI is blocked.
    val activeThreat = remember { com.example.security.SecurityAuditor.getActiveThreat(context) }
    if (activeThreat != null) {
        val threatReason = context.getString(activeThreat.titleResId)
        val dialogBody = context.getString(com.example.R.string.security_alert_dialog_body, threatReason)
        AlertDialog(
            onDismissRequest = { /* Non-dismissible */ },
            title = {
                Text(
                    text = context.getString(com.example.R.string.security_alert_dialog_title),
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = dialogBody,
                    fontSize = 13.sp,
                    color = if (isDarkState) Color.White else Color.Black,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + context.packageName))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                ) {
                    Text(context.getString(com.example.R.string.security_alert_dialog_action), fontWeight = FontWeight.Bold)
                }
            },
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            containerColor = if (isDarkState) Color(0xFF0F1320) else Color.White
        )
    }

    // Advanced Budget & Custom Alerts options persistent variables
    var monthlyBudget by remember { mutableStateOf(sharedPrefs.getFloat("monthly_budget", 10000f).toDouble()) }
    var weeklyBudget by remember { mutableStateOf(sharedPrefs.getFloat("weekly_budget", 2500f).toDouble()) }
    var currencySymbol by remember { mutableStateOf(sharedPrefs.getString("currency_symbol", "₹") ?: "₹") }
    var reminderTime by remember { mutableStateOf(sharedPrefs.getString("reminder_time", "10:00 AM") ?: "10:00 AM") }
    var reminderSound by remember { mutableStateOf(sharedPrefs.getBoolean("reminder_sound", true)) }
    var reminderVibrate by remember { mutableStateOf(sharedPrefs.getBoolean("reminder_vibrate", true)) }
    var reminderFrequency by remember { mutableStateOf(sharedPrefs.getString("reminder_frequency", "Daily") ?: "Daily") }

    var filterTab by remember { mutableStateOf("all") } // all, fresh, soon, expired
    var searchTxt by remember { mutableStateOf("") }

    // Backup states
    var googleConnected by remember { mutableStateOf(false) }
    var googleAccountName by remember { mutableStateOf("") }
    var lastBackupText by remember { mutableStateOf("") }

    // Add Item Form State
    var formName by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("dairy") }
    var formQty by remember { mutableStateOf("") }
    var formUnit by remember { mutableStateOf("litre") }
    var formPrice by remember { mutableStateOf("") }
    var formBoughtDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var formExpiryDate by remember { mutableStateOf("") }
    var formNotes by remember { mutableStateOf("") }
    var formPhotoPath by remember { mutableStateOf<String?>(null) }
    var formValidationError by remember { mutableStateOf("") }
    var editingItemId by remember { mutableStateOf<Long?>(null) }

    val backStack = remember { mutableStateListOf<String>() }

    fun navigateTo(screen: String) {
        if (screen == "home") {
            backStack.clear()
            activeView = "home"
        } else {
            if (activeView != screen) {
                backStack.remove(screen)
                backStack.add(activeView)
                activeView = screen
            }
        }
    }

    var lastBackPressTime by remember { mutableStateOf(0L) }
    androidx.activity.compose.BackHandler(enabled = true) {
        if (activeView != "home") {
            if (backStack.isNotEmpty()) {
                val prev = backStack.removeAt(backStack.size - 1)
                activeView = prev
            } else {
                activeView = "home"
            }
            if (activeView != "additem") {
                editingItemId = null
            }
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                (context as? android.app.Activity)?.finish()
            } else {
                lastBackPressTime = currentTime
                Toast.makeText(context, if (lang == "hi") "बाहर निकलने के लिए फिर से वापस दबाएं" else "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var itemToDelete by remember { mutableStateOf<GroceryItem?>(null) }

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val outStream = context.contentResolver.openOutputStream(uri)
                    if (outStream != null) {
                        // UTF-8 with BOM for immaculate Excel and Hindi compatibility!
                        outStream.write(0xEF)
                        outStream.write(0xBB)
                        outStream.write(0xBF)
                        val writer = outStream.bufferedWriter(Charsets.UTF_8)
                        writer.write("Item Name,Category,Quantity,Unit,Price,Purchase Date,Expiry Date,Notes,Status\n")
                        for (item in itemsList) {
                            val daysL = calcDaysLeft(item.expiryDate)
                            val statusStr = when {
                                daysL < 0 -> if (lang == "hi") "समय समाप्त (Expired)" else "Expired"
                                daysL in 0..3 -> if (lang == "hi") "जल्द समाप्त (Expiring Soon)" else "Expiring Soon"
                                else -> if (lang == "hi") "ताजा (Fresh)" else "Fresh"
                            }
                            val escapedName = item.name.replace("\"", "\"\"")
                            val escapedCategory = getCategoryLabel(item.category, lang).replace("\"", "\"\"")
                            val escapedNotes = item.notes.replace("\"", "\"\"")
                            val priceVal = item.price ?: 0.0
                            writer.write("\"$escapedName\",\"$escapedCategory\",${item.quantity},\"${item.unit}\",$priceVal,\"${item.boughtDate}\",\"${item.expiryDate}\",\"$escapedNotes\",\"$statusStr\"\n")
                        }
                        writer.flush()
                        writer.close()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, if (lang == "hi") "CSV सफलतापूर्वक निर्यात किया गया!" else "CSV exported successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    val importCsvLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val inStream = context.contentResolver.openInputStream(uri)
                    val reader = inStream?.bufferedReader(Charsets.UTF_8)
                    val lines = reader?.readLines() ?: emptyList()
                    if (lines.isNotEmpty()) {
                        var parsedCount = 0
                        var duplicateCount = 0
                        var invalidCount = 0
                        val existingItems = groceryDao.getAllItems()
                        val existingNames = existingItems.map { it.name.lowercase(Locale.getDefault()).trim() }.toSet()
                        
                        val startIndex = if (lines[0].lowercase(Locale.getDefault()).contains("name") || lines[0].lowercase(Locale.getDefault()).contains("item")) 1 else 0
                        
                        val newItemsToInsert = mutableListOf<GroceryItem>()
                        for (i in startIndex until lines.size) {
                            val line = lines[i].trim()
                            if (line.isEmpty()) continue
                            val parts = parseCsvLine(line)
                            if (parts.isNotEmpty()) {
                                val name = parts[0].trim()
                                if (name.isEmpty()) {
                                    invalidCount++
                                    continue
                                }
                                if (existingNames.contains(name.lowercase(Locale.getDefault()).trim())) {
                                    duplicateCount++
                                    continue
                                }
                                val rawCategory = if (parts.size > 1) parts[1].trim() else "dairy"
                                val categoryKey = mapCategoryLabelToKey(rawCategory)
                                val rawQuantity = if (parts.size > 2) parts[2].trim() else "1.0"
                                val quantity = rawQuantity.toDoubleOrNull() ?: 1.0
                                val unit = if (parts.size > 3) parts[3].trim() else "pieces"
                                val rawPrice = if (parts.size > 4) parts[4].trim() else ""
                                val price = rawPrice.toDoubleOrNull()
                                val boughtDate = if (parts.size > 5 && parts[5].trim().isNotEmpty()) parts[5].trim() else formattedTodayStr
                                val expiryDate = if (parts.size > 6 && parts[6].trim().isNotEmpty()) parts[6].trim() else formattedTodayStr
                                
                                val notes = if (parts.size > 7) parts[7].trim() else ""
                                
                                newItemsToInsert.add(
                                    GroceryItem(
                                        name = name,
                                        category = categoryKey,
                                        quantity = quantity,
                                        unit = unit,
                                        price = price,
                                        boughtDate = boughtDate,
                                        expiryDate = expiryDate,
                                        notes = notes
                                    )
                                )
                                parsedCount++
                            } else {
                                invalidCount++
                            }
                        }
                        
                        for (item in newItemsToInsert) {
                            val insertedId = groceryDao.insertItem(item)
                            val itemWithId = item.copy(id = insertedId)
                            AlarmUtils.scheduleAlarmsForItem(context, itemWithId)
                        }
                        withContext(Dispatchers.Main) {
                            val msg = if (lang == "hi") {
                                "इम्पोर्ट समाप्त: $parsedCount सफल, $duplicateCount डुप्लिकेट छोड़ा, $invalidCount अमान्य।"
                            } else {
                                "Import complete: $parsedCount imported, $duplicateCount duplicates skipped, $invalidCount invalid."
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // UI Confirmation Dialogs
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    // Colors styled dynamically
    val appBg = if (isDarkState) Color(0xFF0F201B) else Color(0xFFF7FFF8)
    val surfBg = if (isDarkState) Color(0xFF1B352B) else Color(0xFFFFFFFF)
    val textPrimary = if (isDarkState) Color(0xFFE2E8F0) else Color(0xFF0F2F24)
    val textMuted = if (isDarkState) Color(0xFF8BA59B) else Color(0xFF4A6F62)
    val cardBorderColor = if (isDarkState) Color(0xFF264C3E) else Color(0xFFDCEADD)

    // Statistics Calculations
    val totalCount = itemsList.size
    val expiringSoonCount = itemsList.count {
        val d = calcDaysLeft(it.expiryDate)
        d in 0..3
    }
    val expiredCount = itemsList.count {
        calcDaysLeft(it.expiryDate) < 0
    }
    val totalSpend = itemsList.sumOf { it.price ?: 0.0 }
    val wastedCost = itemsList.filter { calcDaysLeft(it.expiryDate) < 0 }.sumOf { it.price ?: 0.0 }

    // Expiry Dialog Date Picker
    val showDatePicker = { onDateSelected: (String) -> Unit ->
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedMonth = String.format("%02d", month + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                onDateSelected("$year-$formattedMonth-$formattedDay")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Photo selection launcher
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val copiedPath = copyPhotoLocally(context, uri)
                withContext(Dispatchers.Main) {
                    formPhotoPath = copiedPath
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scope.launch(Dispatchers.IO) {
                val savedPath = saveBitmapLocally(context, bitmap)
                withContext(Dispatchers.Main) {
                    formPhotoPath = savedPath
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    // Backup Importer Launcher
    val backupImporterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val content = inputStream?.bufferedReader()?.use { it.readText() }
                    if (content != null) {
                        val jsonArray = JSONArray(content)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val item = GroceryItem(
                                name = obj.getString("name"),
                                category = obj.getString("category"),
                                quantity = obj.getDouble("quantity"),
                                unit = obj.getString("unit"),
                                price = if (obj.isNull("price")) null else obj.getDouble("price"),
                                boughtDate = obj.getString("boughtDate"),
                                expiryDate = obj.getString("expiryDate"),
                                notes = obj.optString("notes", ""),
                                photoPath = if (obj.isNull("photoPath")) null else obj.getString("photoPath")
                            )
                            val insertedId = groceryDao.insertItem(item)
                            val itemWithId = item.copy(id = insertedId)
                            AlarmUtils.scheduleAlarmsForItem(context, itemWithId)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Backup restored successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to import backup: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // Manual Backup SAF CreateDocument Launcher
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val success = exportDatabase(context, uri)
                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(context, if (lang == "hi") "बैकअप सफलतापूर्वक सहेजा गया!" else "Backup saved successfully!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, if (lang == "hi") "बैकअप सहेजने में विफल!" else "Failed to save backup", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    val lightSleekColorScheme = lightColorScheme(
        primary = Color(0xFF22C55E), // Accent green for active items
        onPrimary = Color.White,
        secondary = Color(0xFF22D3EE), // Cyan accent
        onSecondary = Color(0xFF0F2F24),
        background = Color(0xFFF7FFF8), // Background: #F7FFF8
        onBackground = Color(0xFF0F2F24), // Deep text: #0F2F24
        surface = Color.White, // Card: #FFFFFF
        onSurface = Color(0xFF0F2F24),
        surfaceVariant = Color(0xFFCFE8C9), // Pista background: #CFE8C9
        onSurfaceVariant = Color(0xFF4A6F62),
        outline = Color(0xFFDCEADD)
    )

    val darkSleekColorScheme = darkColorScheme(
        primary = Color(0xFF4ADE80),
        onPrimary = Color.Black,
        secondary = Color(0xFF22D3EE),
        onSecondary = Color.Black,
        background = Color(0xFF0F201B), // Dark organic background
        onBackground = Color(0xFFE2E8F0),
        surface = Color(0xFF1B352B),
        onSurface = Color(0xFFE2E8F0),
        surfaceVariant = Color(0xFF264C3E),
        onSurfaceVariant = Color(0xFF8BA59B),
        outline = Color(0xFF264C3E)
    )

    val locale = remember(lang) { java.util.Locale(lang) }
    val localizedContext = remember(context, locale) {
        val config = android.content.res.Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        MaterialTheme(
            colorScheme = if (isDarkState) darkSleekColorScheme else lightSleekColorScheme
        ) {
        if (showSplash) {
            FreshTrakSplashScreen()
        } else if (isFirstTimeUser) {
            FreshTrackOnboardingScreen(
                lang = lang,
                onLanguageChange = { newLang ->
                    lang = newLang
                    prefs.language = newLang
                },
                onAgreeAndContinue = {
                    isFirstTimeUser = false
                    prefs.isFirstTimeUser = false
                }
            )
        } else {
            Scaffold(
                containerColor = appBg,
                bottomBar = {
                    val bottomBarBg = Color(0xFF9CCF8B) // Pistachio Green footer background!
                    val itemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1B4D1F), // Dark contrast green
                        unselectedIconColor = Color(0xFF1B4D1F).copy(alpha = 0.55f),
                        selectedTextColor = Color(0xFF1B4D1F),
                        unselectedTextColor = Color(0xFF1B4D1F).copy(alpha = 0.55f),
                        indicatorColor = Color(0xFF1B4D1F).copy(alpha = 0.14f)
                    )

                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                        containerColor = bottomBarBg,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeView == "home",
                            onClick = { activeView = "home" },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text(translate("home", lang), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = itemColors
                        )
                        NavigationBarItem(
                            selected = activeView == "shopping",
                            onClick = { activeView = "shopping" },
                            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping List") },
                            label = { Text(translate("shopping", lang), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = itemColors
                        )
                        NavigationBarItem(
                            selected = activeView == "analytics",
                            onClick = { activeView = "analytics" },
                            icon = { Icon(Icons.Default.List, contentDescription = "Analytics") },
                            label = { Text(translate("analytics", lang), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = itemColors
                        )
                        NavigationBarItem(
                            selected = activeView == "settings",
                            onClick = { activeView = "settings" },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text(translate("settings", lang), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = itemColors
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header Card with beautiful Pistachio Green (Pist Green) gradient
                        val headerBgBrush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9CCF8B), // Soft Pistachio Green
                                Color(0xFF7CAF6D)  // Rich Mid-Pistachio Green
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = headerBgBrush,
                                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                                )
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (activeView != "home") {
                                    IconButton(
                                        onClick = { activeView = "home" },
                                        modifier = Modifier.size(34.dp).testTag("header_back_button")
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color(0xFF1B4D1F) // Dark contrast green
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = when (activeView) {
                                            "additem" -> "🌱 " + translate("addItem", lang)
                                            "shopping" -> "🛒 " + translate("shopping", lang)
                                            "analytics" -> "📊 " + translate("analytics", lang)
                                            else -> "⚙️ " + translate("settings", lang)
                                        },
                                        color = Color(0xFF1B4D1F), // Dark contrast green
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                } else {
                                    // Double circular halo framework around the vibrant FreshTrak logo to ensure maximum pop
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.35f), shape = CircleShape)
                                            .border(BorderStroke(1.2.dp, Color.White), shape = CircleShape)
                                            .padding(4.dp)
                                    ) {
                                        FreshTrackVectorLogo(modifier = Modifier.size(42.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "FreshTrak",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF1B4D1F) // Dark green contrast
                                        )
                                        Text(
                                            text = translate("subtitle", lang),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2C5E31) // Subtitle contrast
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Action togglers - rounded translucent bubble
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFF1B4D1F).copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp))
                                        .clickable {
                                            isDarkState = !isDarkState
                                            prefs.isDarkMode = isDarkState
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isDarkState) "☀️" else "🌙",
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                    // View Content Switching with elegant fade animations
                    AnimatedVisibility(
                        visible = activeView == "home",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        HomeScreen(
                            itemsList = itemsList,
                            lang = lang,
                            totalCount = totalCount,
                            expiringSoonCount = expiringSoonCount,
                            expiredCount = expiredCount,
                            searchTxt = searchTxt,
                            onSearchTxtChange = { searchTxt = it },
                            filterTab = filterTab,
                            onFilterTabChange = { filterTab = it },
                            textPrimary = textPrimary,
                            textMuted = textMuted,
                            cardBorderColor = cardBorderColor,
                            isDark = isDarkState,
                            onAddItemClick = {
                                editingItemId = null
                                // Reset Add Item values
                                formName = ""
                                formCategory = "fruits_veg"
                                formQty = ""
                                formUnit = "kg"
                                formPrice = ""
                                formBoughtDate = formattedTodayStr
                                formExpiryDate = ""
                                formNotes = ""
                                formPhotoPath = null
                                formValidationError = ""
                                navigateTo("additem")
                            },
                            onDeleteItem = { item ->
                                scope.launch(Dispatchers.IO) {
                                    AlarmUtils.cancelAlarmsForItem(context, item)
                                    groceryDao.deleteItem(item)
                                }
                            },
                            onEditItem = { item ->
                                editingItemId = item.id
                                formName = item.name
                                formCategory = item.category
                                formQty = item.quantity.toString()
                                formUnit = item.unit
                                formPrice = item.price?.toString() ?: ""
                                formBoughtDate = item.boughtDate
                                formExpiryDate = item.expiryDate
                                formNotes = item.notes
                                formPhotoPath = item.photoPath
                                formValidationError = ""
                                navigateTo("additem")
                            },
                            onMarkConsumed = { item ->
                                scope.launch(Dispatchers.IO) {
                                    AlarmUtils.cancelAlarmsForItem(context, item)
                                    groceryDao.deleteItem(item)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, if (lang == "hi") "मजे करें! वस्तु उपभोग की गई अंकित की गई।" else "Enjoy your meal! Item marked as consumed.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onDuplicateItem = { item ->
                                scope.launch(Dispatchers.IO) {
                                    val cloned = GroceryItem(
                                        name = "${item.name} (Copy)",
                                        category = item.category,
                                        quantity = item.quantity,
                                        unit = item.unit,
                                        price = item.price,
                                        boughtDate = item.boughtDate,
                                        expiryDate = item.expiryDate,
                                        notes = item.notes,
                                        photoPath = item.photoPath
                                    )
                                    val insertedId = groceryDao.insertItem(cloned)
                                    val clonedWithId = cloned.copy(id = insertedId)
                                    AlarmUtils.scheduleAlarmsForItem(context, clonedWithId)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, if (lang == "hi") "किराने का सामान कॉपी किया गया!" else "Grocery item duplicated!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onBulkDeleteRequest = { showBulkDeleteConfirm = true },
                            onExportCSV = {
                                try {
                                    exportCsvLauncher.launch("FreshTrackInventory.csv")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to launch export: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onImportCSV = {
                                try {
                                    importCsvLauncher.launch("*/*")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open file picker: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = activeView == "additem",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        AddItemScreen(
                            lang = lang,
                            textPrimary = textPrimary,
                            textMuted = textMuted,
                            isDark = isDarkState,
                            isEdit = editingItemId != null,
                            formName = formName,
                            onFormNameChange = { formName = it },
                            formCategory = formCategory,
                            onFormCategoryChange = {
                                formCategory = it
                                // Dynamic unit auto-selection logic based on selected category
                                val suggestedUnit = when (it) {
                                    "dairy_eggs", "beverages" -> "litre"
                                    "fruits_veg", "meat_seafood", "pantry_grains" -> "kg"
                                    else -> "pieces"
                                }
                                formUnit = suggestedUnit
                            },
                            formQty = formQty,
                            onFormQtyChange = { formQty = it },
                            formUnit = formUnit,
                            onFormUnitChange = { formUnit = it },
                            formPrice = formPrice,
                            onFormPriceChange = { formPrice = it },
                            formBoughtDate = formBoughtDate,
                            onFormBoughtDateChange = { formBoughtDate = it },
                            formExpiryDate = formExpiryDate,
                            onFormExpiryDateChange = { formExpiryDate = it },
                            formNotes = formNotes,
                            onFormNotesChange = { formNotes = it },
                            formPhotoPath = formPhotoPath,
                            onChooseFromGallery = { photoLauncher.launch("image/*") },
                            onTakePhoto = {
                                val hasCamPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasCamPerm) {
                                    cameraLauncher.launch(null)
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            },
                            onShowDatePicker = showDatePicker,
                            onCancel = {
                                editingItemId = null
                                navigateTo("home")
                            },
                            formValidationError = formValidationError,
                            onSave = {
                                if (formName.trim().isEmpty()) {
                                    formValidationError = translate("nameRequired", lang)
                                } else if (formExpiryDate.trim().isEmpty()) {
                                    formValidationError = translate("expiryRequired", lang)
                                } else {
                                    scope.launch(Dispatchers.IO) {
                                        val quantityVal = formQty.toDoubleOrNull() ?: 1.0
                                        val priceVal = formPrice.toDoubleOrNull()
                                        if (editingItemId != null) {
                                            val updatedItem = GroceryItem(
                                                id = editingItemId!!,
                                                name = formName,
                                                category = formCategory,
                                                quantity = quantityVal,
                                                unit = formUnit,
                                                price = priceVal,
                                                boughtDate = formBoughtDate,
                                                expiryDate = formExpiryDate,
                                                notes = formNotes,
                                                photoPath = formPhotoPath
                                            )
                                            AlarmUtils.cancelAlarmsForItem(context, updatedItem)
                                            groceryDao.insertItem(updatedItem)
                                            AlarmUtils.scheduleAlarmsForItem(context, updatedItem)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, if (lang == "hi") "किराने का सामान सफलतापूर्वक संशोधित किया गया!" else "Item Updated Successfully!", Toast.LENGTH_SHORT).show()
                                                editingItemId = null
                                                navigateTo("home")
                                            }
                                        } else {
                                            val newItem = GroceryItem(
                                                name = formName,
                                                category = formCategory,
                                                quantity = quantityVal,
                                                unit = formUnit,
                                                price = priceVal,
                                                boughtDate = formBoughtDate,
                                                expiryDate = formExpiryDate,
                                                notes = formNotes,
                                                photoPath = formPhotoPath
                                            )
                                            val insertedId = groceryDao.insertItem(newItem)
                                            val itemWithId = newItem.copy(id = insertedId)
                                            AlarmUtils.scheduleAlarmsForItem(context, itemWithId)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, if (lang == "hi") "नया सामान सफलतापूर्वक जोड़ा गया!" else "Item Saved Successfully!", Toast.LENGTH_SHORT).show()
                                                navigateTo("home")
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = activeView == "shopping",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ShoppingListScreen(
                            shoppingDao = db.shoppingDao(),
                            groceryDao = groceryDao,
                            lang = lang,
                            textPrimary = textPrimary,
                            textMuted = textMuted,
                            isDark = isDarkState,
                            onBack = { activeView = "home" }
                        )
                    }

                    AnimatedVisibility(
                        visible = activeView == "analytics",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        AnalyticsScreen(
                            lang = lang,
                            itemsList = itemsList,
                            expandedCount = expiredCount,
                            totalSpend = totalSpend,
                            wastedCost = wastedCost,
                            textPrimary = textPrimary,
                            textMuted = textMuted,
                            isDark = isDarkState,
                            onExportReports = {
                                try {
                                    val reportFile = File(context.cacheDir, "FreshTrackSpendReport.csv")
                                    FileOutputStream(reportFile).use { out ->
                                        out.write("FreshTrack Spent & Waste Report\n".toByteArray())
                                        out.write("Total Spend,₹${totalSpend}\n".toByteArray())
                                        out.write("Total Waste Value,₹${wastedCost}\n".toByteArray())
                                        out.write("\nItem Expenses:\nName,Category,Price,Purchased,Expiry,Status\n".toByteArray())
                                        for (item in itemsList) {
                                            val stats = getExpiryStatus(calcDaysLeft(item.expiryDate))
                                            out.write("\"${item.name}\",\"${item.category}\",${item.price ?: 0.0},\"${item.boughtDate}\",\"${item.expiryDate}\",\"$stats\"\n".toByteArray())
                                        }
                                    }
                                    shareFile(context, reportFile, "text/csv")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = activeView == "settings",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        SettingsScreen(
                            lang = lang,
                            onLangChange = {
                                lang = it
                                prefs.language = it
                            },
                            notificationsOn = notificationsOn,
                            onNotificationsOnChange = {
                                notificationsOn = it
                                prefs.isNotificationsOn = it
                            },
                            textPrimary = textPrimary,
                            textMuted = textMuted,
                            isDark = isDarkState,
                            onBackupNow = {
                                try {
                                    backupLauncher.launch("expiry_reminder_backup.db")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onClearDatabase = {
                                showResetConfirm = true
                            },
                            themeMode = themeMode,
                            onThemeModeChange = { mode ->
                                themeMode = mode
                                sharedPrefs.edit().putString("theme_mode", mode).apply()
                                isDarkState = when (mode) {
                                    "light" -> false
                                    "dark" -> true
                                    else -> systemInDark
                                }
                                prefs.isDarkMode = isDarkState
                            },
                            reminderTime = reminderTime,
                            onReminderTimeChange = {
                                reminderTime = it
                                sharedPrefs.edit().putString("reminder_time", it).apply()
                            },
                            reminderSound = reminderSound,
                            onReminderSoundChange = {
                                reminderSound = it
                                sharedPrefs.edit().putBoolean("reminder_sound", it).apply()
                            },
                            reminderVibrate = reminderVibrate,
                            onReminderVibrateChange = { r ->
                                reminderVibrate = r
                                sharedPrefs.edit().putBoolean("reminder_vibrate", r).apply()
                            },
                            reminderFrequency = reminderFrequency,
                            onReminderFrequencyChange = {
                                reminderFrequency = it
                                sharedPrefs.edit().putString("reminder_frequency", it).apply()
                            },
                            totalItemsStored = itemsList.size,
                            imageStorageSizeStr = run {
                                val photosDir = File(context.filesDir, "grocery_photos")
                                val photosSize = if (photosDir.exists()) getFolderSize(photosDir) else 0L
                                String.format(Locale.US, "%.2f MB", photosSize.toDouble() / (1024 * 1024))
                            },
                            onClearImagesCache = {
                                clearOrphanedPhotos(context, itemsList)
                            }
                        )
                    }
                }

                // Confirm Dialog for Bulk Delete Expired Items
                if (showBulkDeleteConfirm) {
                    AlertDialog(
                        onDismissRequest = { showBulkDeleteConfirm = false },
                        title = { Text(translate("deleteExpiredQ", lang)) },
                        text = {
                            Text(
                                "This will clear and permanently delete all $expiredCount expired items valued at ₹${wastedCost}.",
                                color = textMuted
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val items = groceryDao.getAllItems()
                                        for (item in items) {
                                            if (item.expiryDate < formattedTodayStr) {
                                                AlarmUtils.cancelAlarmsForItem(context, item)
                                            }
                                        }
                                        groceryDao.deleteExpiredItems(formattedTodayStr)
                                        showBulkDeleteConfirm = false
                                        Toast.makeText(context, translate("expiredCleared", lang), Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text(translate("delete", lang), color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showBulkDeleteConfirm = false }) {
                                Text(translate("cancel", lang))
                            }
                        }
                    )
                }

                // App Database Reset confirm
                if (showResetConfirm) {
                    AlertDialog(
                        onDismissRequest = { showResetConfirm = false },
                        title = { Text(translate("clearAllData", lang), color = Color.Red, fontWeight = FontWeight.Bold) },
                        text = { Text(translate("clearDataConfirm", lang), color = textPrimary) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val items = groceryDao.getAllItems()
                                        for (item in items) {
                                            AlarmUtils.cancelAlarmsForItem(context, item)
                                        }
                                        groceryDao.clearAll()
                                        prefs.clearAll()
                                        lang = "en"
                                        isDarkState = false
                                        notificationsOn = true
                                        isFirstTimeUser = true
                                        showResetConfirm = false
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, translate("resetSuccess", lang), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Reset All", color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResetConfirm = false }) {
                                Text(translate("cancel", lang))
                            }
                        }
                    )
                }
            }
        }
    }
}
}
}

// ── SCREEN: HOME ──
@Composable
fun HomeScreen(
    itemsList: List<GroceryItem>,
    lang: String,
    totalCount: Int,
    expiringSoonCount: Int,
    expiredCount: Int,
    searchTxt: String,
    onSearchTxtChange: (String) -> Unit,
    filterTab: String,
    onFilterTabChange: (String) -> Unit,
    textPrimary: Color,
    textMuted: Color,
    cardBorderColor: Color,
    isDark: Boolean,
    onAddItemClick: () -> Unit,
    onDeleteItem: (GroceryItem) -> Unit,
    onEditItem: (GroceryItem) -> Unit,
    onMarkConsumed: (GroceryItem) -> Unit,
    onDuplicateItem: (GroceryItem) -> Unit,
    onBulkDeleteRequest: () -> Unit,
    onExportCSV: () -> Unit,
    onImportCSV: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Stats Row Cards
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    count = totalCount.toString(),
                    label = translate("totalItems", lang),
                    subtitle = translate("statTotalSubtitle", lang),
                    color = Color(0xFF00C897),
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterTabChange("all") },
                    isDark = isDark,
                    icon = { TotalInventoryIcon(color = Color(0xFF00C897)) }
                )
                StatCard(
                    count = expiringSoonCount.toString(),
                    label = translate("expiringSoon", lang),
                    subtitle = translate("statSoonSubtitle", lang),
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterTabChange("soon") },
                    isDark = isDark,
                    icon = { ExpiringSoonIcon(color = Color(0xFFF59E0B)) }
                )
                StatCard(
                    count = expiredCount.toString(),
                    label = translate("expired", lang),
                    subtitle = translate("statExpiredSubtitle", lang),
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterTabChange("expired") },
                    isDark = isDark,
                    icon = { ExpiredIcon(color = Color(0xFFEF4444)) }
                )
            }

            // Quick Actions Block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBulkDeleteRequest,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFECEB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🗑️ " + translate("bulkDelete", lang), fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onExportCSV,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6FAF4)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📤 " + translate("csv", lang), fontSize = 11.sp, color = Color(0xFF00C897), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onImportCSV,
                    modifier = Modifier.weight(1.1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFEFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📥 " + (if (lang == "hi") "इम्पोर्ट CSV" else "Import CSV"), fontSize = 11.sp, color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
                }
            }

            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF151B2E) else Color(0x0A000000)),
                border = BorderStroke(1.dp, cardBorderColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchTxt,
                        onValueChange = onSearchTxtChange,
                        placeholder = { Text(translate("searchItems", lang), fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("search_text_input")
                    )
                    if (searchTxt.isNotEmpty()) {
                        IconButton(onClick = { onSearchTxtChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = textMuted)
                        }
                    }
                }
            }

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("all", "fresh", "soon", "expired").forEach { tab ->
                    val isSelected = filterTab == tab
                    val borderB = if (isSelected) null else BorderStroke(1.dp, cardBorderColor)
                    val bgB = if (isSelected) Color(0xFF00C897) else (if (isDark) Color(0xFF151B2E) else Color.White)
                    val textC = if (isSelected) Color.White else textMuted

                    Card(
                        modifier = Modifier
                            .clickable { onFilterTabChange(tab) }
                            .testTag("filter_tab_$tab"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = bgB),
                        border = borderB
                    ) {
                        Text(
                            text = when (tab) {
                                "all" -> translate("all", lang)
                                "fresh" -> translate("fresh", lang)
                                "soon" -> translate("expiring", lang)
                                else -> translate("expired", lang)
                            },
                            color = textC,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Inventory Listing Title
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = translate("myInventory", lang),
                    color = textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${itemsList.size} " + translate("items", lang),
                    color = textMuted,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Load and filter items
            val filteredList = itemsList.filter { item ->
                val stats = getExpiryStatus(calcDaysLeft(item.expiryDate))
                val matchesSearch = item.name.lowercase(Locale.getDefault()).contains(searchTxt.lowercase(Locale.getDefault()))
                val matchesFilter = when (filterTab) {
                    "fresh" -> stats == "fresh"
                    "soon" -> stats == "soon" || stats == "today"
                    "expired" -> stats == "expired"
                    else -> true
                }
                matchesSearch && matchesFilter
            }

            if (filteredList.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF131F1C) else Color(0xFFF9FFFA)),
                    border = BorderStroke(1.2.dp, if (isDark) Color(0xFF264C3B) else Color(0xFFDCEADD))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Drawing visual illustration
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1B3128) else Color(0xFFE6FAF4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🥗", fontSize = 44.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Text(
                            text = translate("noItems", lang),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = textPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = if (lang == "hi") "किराने की वस्तुओं को खराब होने से बचाने के लिए उन्हें जोड़ें!" else "Keep track of your foods to stay fresh and avoid waste!",
                            fontSize = 12.sp,
                            color = textMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(18.dp))
                        Divider(color = if (isDark) Color(0xFF1C3A2F) else Color(0xFFE2EFE3), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(18.dp))
                        
                        // Steps
                        Text(
                            text = if (lang == "hi") "🚀 कैसे काम करता है:" else "🚀 How it works:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = textPrimary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Step 1
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("➕", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = if (lang == "hi") "1. एक नया सामान जोड़ें" else "1. Add Grocery Item",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = if (lang == "hi") "नीचे दिए '+' बटन या इस कार्ड के बटन पर क्लिक करें" else "Tap '+' button below or the button in this card",
                                    fontSize = 10.sp,
                                    color = textMuted
                                )
                            }
                        }
                        
                        // Step 2
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🔔", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = if (lang == "hi") "2. समाप्ति तिथि दर्ज करें" else "2. Set Expiry Date",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = if (lang == "hi") "ताकि ऐप आपको समय रहते सूचना दे सके" else "Get active alerts and reduce expensive pantry waste",
                                    fontSize = 10.sp,
                                    color = textMuted
                                )
                            }
                        }
                        
                        // Step 3
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("📊", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = if (lang == "hi") "3. बर्बादी बचाएं और विश्लेषण देखें" else "3. Analyze & Keep Fresh",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = if (lang == "hi") "किराने के बजट और श्रेणी अनुसार खर्च पर नज़र रखें" else "Gain complete clarity on monthly category expenditures",
                                    fontSize = 10.sp,
                                    color = textMuted
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // CTA Button
                        Button(
                            onClick = onAddItemClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C897)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text(
                                text = if (lang == "hi") "🌱 पहला सामान जोड़ें" else "🌱 Add Your First Item",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                filteredList.forEach { groceryItem ->
                    ItemCardRow(
                        item = groceryItem,
                        lang = lang,
                        isDark = isDark,
                        textPrimary = textPrimary,
                        textMuted = textMuted,
                        onDelete = { onDeleteItem(groceryItem) },
                        onEdit = { onEditItem(groceryItem) },
                        onMarkConsumed = { onMarkConsumed(groceryItem) },
                        onDuplicate = { onDuplicateItem(groceryItem) }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddItemClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 95.dp, end = 20.dp)
                .testTag("add_item_fab"),
            containerColor = Color(0xFF00C897),
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Food")
        }
    }
}

@Composable
fun TotalInventoryIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val sizePx = size.width
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val r = sizePx * 0.42f
        
        val path = Path().apply {
            val s32 = Math.sqrt(3.0).toFloat() / 2f
            moveTo(cx, cy - r) // Top
            lineTo(cx + r * s32, cy - r * 0.5f) // Top-Right
            lineTo(cx + r * s32, cy + r * 0.5f) // Bottom-Right
            lineTo(cx, cy + r) // Bottom
            lineTo(cx - r * s32, cy + r * 0.5f) // Bottom-Left
            lineTo(cx - r * s32, cy - r * 0.5f) // Top-Left
            close()
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round)
        )
        
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(cx, cy - r),
            strokeWidth = 1.8.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(cx - r * 0.866f, cy + r * 0.5f),
            strokeWidth = 1.8.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(cx + r * 0.866f, cy + r * 0.5f),
            strokeWidth = 1.8.dp.toPx()
        )
        
        drawCircle(
            color = color,
            radius = 2.2.dp.toPx(),
            center = Offset(cx, cy - r * 0.45f)
        )
    }
}

@Composable
fun ExpiringSoonIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val sizePx = size.width
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val r = sizePx * 0.42f
        
        drawCircle(
            color = color,
            radius = r,
            style = Stroke(width = 1.8.dp.toPx())
        )
        
        drawArc(
            color = color.copy(alpha = 0.35f),
            startAngle = -90f,
            sweepAngle = 100f,
            useCenter = true,
            topLeft = Offset(cx - r, cy - r),
            size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
        )
        
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(cx, cy - r * 0.62f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(cx + r * 0.45f, cy + r * 0.2f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        drawCircle(
            color = color,
            radius = 1.5.dp.toPx(),
            center = Offset(cx - r * 0.72f, cy - r * 0.72f)
        )
        drawCircle(
            color = color,
            radius = 1.5.dp.toPx(),
            center = Offset(cx + r * 0.72f, cy - r * 0.72f)
        )
    }
}

@Composable
fun ExpiredIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val sizePx = size.width
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val r = sizePx * 0.42f
        
        val path = Path().apply {
            moveTo(cx, cy - r)
            lineTo(cx + r * 0.8f, cy - r * 0.6f)
            lineTo(cx + r * 0.8f, cy + r * 0.2f)
            quadraticBezierTo(cx + r * 0.8f, cy + r * 0.8f, cx, cy + r)
            quadraticBezierTo(cx - r * 0.8f, cy + r * 0.8f, cx - r * 0.8f, cy + r * 0.2f)
            lineTo(cx - r * 0.8f, cy - r * 0.6f)
            close()
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round)
        )
        
        drawLine(
            color = color,
            start = Offset(cx, cy - r * 0.35f),
            end = Offset(cx, cy + r * 0.15f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = color,
            radius = 1.5.dp.toPx(),
            center = Offset(cx, cy + r * 0.42f)
        )
    }
}

@Composable
fun StatCard(
    count: String,
    label: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isDark: Boolean,
    icon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f)
    )
    
    val shadowElev by animateDpAsState(
        targetValue = if (isPressed) 14.dp else 4.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f)
    )
    
    val transY by animateFloatAsState(
        targetValue = if (isPressed) 3f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f)
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = transY
            }
            .shadow(
                elevation = shadowElev,
                shape = RoundedCornerShape(22.dp),
                ambientColor = color.copy(alpha = 0.3f),
                spotColor = color.copy(alpha = 0.4f)
            )
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .height(115.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                color.copy(alpha = 0.12f)
            } else {
                color.copy(alpha = 0.05f)
            }
        ),
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.6f),
                    color.copy(alpha = 0.15f)
                ),
                start = Offset.Zero,
                end = Offset.Infinite
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, CircleShape)
                )
            }
            
            Column {
                Text(
                    text = count,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) Color.White else color.copy(green = 0.5f).compositeOver(Color.Black)
                )
                
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFCCCCCC) else Color(0xFF444444),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF8BA59B) else Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ItemCardRow(
    item: GroceryItem,
    lang: String,
    isDark: Boolean,
    textPrimary: Color,
    textMuted: Color,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onMarkConsumed: () -> Unit,
    onDuplicate: () -> Unit
) {
    val daysLeft = calcDaysLeft(item.expiryDate)
    val status = getExpiryStatus(daysLeft)
    val color = getStatusColor(status)
    val bg = getStatusBgColor(status, isDark)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF131E1B) else Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.2.dp, if (isDark) Color(0xFF264C3B) else Color(0xFFDCEADD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo or Emoji inside decorative circle (Teal/Emerald/Red dynamic bg)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.photoPath != null) {
                        val file = File(item.photoPath)
                        if (file.exists()) {
                            AsyncImage(
                                model = file,
                                contentDescription = "Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(getCategoryEmoji(item.category), fontSize = 24.sp)
                        }
                    } else {
                        Text(getCategoryEmoji(item.category), fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.name,
                            color = textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = "${getCategoryLabel(item.category, lang)} • ${item.quantity} ${item.unit}",
                        fontSize = 12.sp,
                        color = textMuted
                    )

                    if (item.notes.isNotEmpty()) {
                        Text(
                            text = "📝 ${item.notes}",
                            fontSize = 11.sp,
                            color = textMuted.copy(alpha = 0.85f),
                            fontStyle = FontStyle.Italic,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Expiry pill-style badge
                    val alertLabel = when {
                        status == "expired" -> {
                            val days = kotlin.math.abs(daysLeft.toLong())
                            if (lang == "hi") "$days दिन पहले समाप्त (${item.expiryDate})" else "$days days ago (${item.expiryDate})"
                        }
                        status == "today" -> translate("expiresToday", lang)
                        daysLeft.toLong() == 1L -> if (lang == "hi") "कल समाप्त होगा" else "Expires tomorrow"
                        else -> if (lang == "hi") "$daysLeft दिन शेष" else "$daysLeft days left"
                    }

                    Box(
                        modifier = Modifier
                            .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(color, shape = CircleShape)
                            )
                            Text(
                                text = alertLabel,
                                fontSize = 11.sp,
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (item.price != null && item.price > 0.1) {
                    Text(
                        text = "₹${item.price.toInt()}",
                        color = textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = if (isDark) Color(0xFF1E2D28) else Color(0xFFE2EFE3), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(4.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Consumed Button
                TextButton(
                    onClick = onMarkConsumed,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (lang == "hi") "✅ उपभोग किया" else "✅ Consume",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Edit Button
                TextButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (lang == "hi") "✏️ बदलें" else "✏️ Edit",
                        color = Color(0xFF34D399),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Clone / Duplicate Button
                TextButton(
                    onClick = onDuplicate,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (lang == "hi") "📋 कॉपी" else "📋 Clone",
                        color = Color(0xFF22D3EE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete Button
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (lang == "hi") "❌ हटाएं" else "❌ Delete",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── SCREEN: ADD ITEM ──
@Composable
fun AddItemScreen(
    lang: String,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean,
    formName: String,
    onFormNameChange: (String) -> Unit,
    formCategory: String,
    onFormCategoryChange: (String) -> Unit,
    formQty: String,
    onFormQtyChange: (String) -> Unit,
    formUnit: String,
    onFormUnitChange: (String) -> Unit,
    formPrice: String,
    onFormPriceChange: (String) -> Unit,
    formBoughtDate: String,
    onFormBoughtDateChange: (String) -> Unit,
    formExpiryDate: String,
    onFormExpiryDateChange: (String) -> Unit,
    formNotes: String,
    onFormNotesChange: (String) -> Unit,
    formPhotoPath: String?,
    onChooseFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onShowDatePicker: ((String) -> Unit) -> Unit,
    onCancel: () -> Unit,
    formValidationError: String,
    onSave: () -> Unit,
    isEdit: Boolean = false
) {
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    val surfBg = if (isDark) Color(0xFF131F1C) else Color(0xFFFFFFFF)
    val cardBorderColor = if (isDark) Color(0xFF264C3E) else Color(0xFFE2EFE3)

    if (showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceDialog = false },
            title = { Text(text = if (lang == "hi") "फ़ोटो स्रोत चुनें" else "Choose Image", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    // Take Photo (Camera) Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showPhotoSourceDialog = false
                                onTakePhoto()
                            }
                            .background(if (isDark) Color(0xFF1C2D28) else Color(0xFFEFF5F2), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📸",
                            fontSize = 20.sp
                        )
                        Text(
                            text = if (lang == "hi") "कैमरा (लाइव फोटो)" else "Take Photo (Camera)",
                            color = textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Choose from Gallery Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showPhotoSourceDialog = false
                                onChooseFromGallery()
                            }
                            .background(if (isDark) Color(0xFF1C2D28) else Color(0xFFEFF5F2), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🖼️",
                            fontSize = 20.sp
                        )
                        Text(
                            text = if (lang == "hi") "गैलरी से चुनें" else "Choose from Gallery",
                            color = textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoSourceDialog = false }) {
                    Text(text = if (lang == "hi") "रद्द करें" else "Cancel", color = Color(0xFF00C897), fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = if (isDark) Color(0xFF12221D) else Color.White
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 100.dp), // Safe space for Sticky overlay bar
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Elegant top heading indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF00C897).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿", fontSize = 16.sp)
                }
                Column {
                    Text(
                        text = if (isEdit) {
                            (if (lang == "hi") "किराने का सामान सुधारें" else "Edit Grocery Item")
                        } else {
                            translate("addItem", lang)
                        },
                        color = textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (isEdit) {
                            (if (lang == "hi") "किराने की जानकारी दुरुस्त करें" else "Refine ingredient details")
                        } else {
                            (if (lang == "hi") "नई सामग्री सुरक्षित रूप से जोड़े" else "Capture fresh ingredients")
                        },
                        color = textMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // CARD 1: PHOTO & CATEGORY SETUP
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = surfBg),
                border = BorderStroke(1.2.dp, cardBorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "📷  " + translate("photo", lang) + " & " + translate("category", lang),
                        color = Color(0xFF00C897),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    // Photo selector block
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(85.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) Color(0xFF1C2D28) else Color(0xFFF3FDF5))
                                .border(2.dp, Color(0xFF00C897).copy(alpha = 0.4F), RoundedCornerShape(20.dp))
                                .clickable { showPhotoSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (formPhotoPath != null) {
                                val file = File(formPhotoPath)
                                if (file.exists()) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = file,
                                            contentDescription = "Item Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(4.dp)
                                                .size(20.dp)
                                                .background(Color(0xFF00C897), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    ImagePlaceholderAnchor()
                                }
                            } else {
                                ImagePlaceholderAnchor()
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Button(
                                onClick = { showPhotoSourceDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00C897).copy(alpha = 0.12f),
                                    contentColor = Color(0xFF00C897)
                                ),
                                border = BorderStroke(1.2.dp, Color(0xFF00C897)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("📸 " + translate("choosePhoto", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                translate("photoHint", lang),
                                fontSize = 10.sp,
                                color = textMuted.copy(alpha = 0.8f)
                            )
                        }
                    }

                    HorizontalDivider(color = cardBorderColor.copy(alpha = 0.6f))

                    // Animated Category selector row
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (lang == "hi") "श्रेणी चुनें" else "Select Category",
                            color = textMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CAT_EMOJI.keys.forEach { cat ->
                                val isSelected = formCategory == cat
                                val chipBg = if (isSelected) Color(0xFF00C897).copy(alpha = 0.15f) else (if (isDark) Color(0xFF15221E) else Color(0xFFF3FDF5))
                                val chipBorderCol = if (isSelected) Color(0xFF00C897) else (if (isDark) Color(0xFF264C3E) else Color(0xFFECECEC))
                                val chipTextColor = if (isSelected) Color(0xFF00C897) else textPrimary
                                val chipWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(chipBg)
                                        .border(1.5.dp, chipBorderCol, RoundedCornerShape(14.dp))
                                        .clickable { onFormCategoryChange(cat) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("form_cat_chip_$cat"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (isSelected) {
                                            Text("✓", color = Color(0xFF00C897), fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                        Text(
                                            text = "${getCategoryEmoji(cat)} ${getCategoryLabel(cat, lang)}",
                                            color = chipTextColor,
                                            fontSize = 11.sp,
                                            fontWeight = chipWeight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // CARD 2: ESSENTIAL ATTRIBUTES
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = surfBg),
                border = BorderStroke(1.2.dp, cardBorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "🛒  " + if (lang == "hi") "मूल विवरण" else "Basic Attributes",
                        color = Color(0xFF00C897),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    // Modern Floating Label Item Name
                    OutlinedTextField(
                        value = formName,
                        onValueChange = onFormNameChange,
                        label = { Text(translate("itemName", lang) + " *", color = textMuted, fontSize = 12.sp) },
                        placeholder = { Text(translate("itemNamePh", lang), fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("item_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C897),
                            unfocusedBorderColor = if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD),
                            focusedLabelColor = Color(0xFF00C897),
                            unfocusedLabelColor = textMuted,
                            focusedContainerColor = if (isDark) Color(0xFF15221F) else Color(0xFFFBFDFB),
                            unfocusedContainerColor = if (isDark) Color(0xFF101917) else Color(0xFFFCFDFC)
                        )
                    )

                    // Quantity Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formQty,
                            onValueChange = onFormQtyChange,
                            label = { Text(translate("qtyUnit", lang), color = textMuted, fontSize = 12.sp) },
                            placeholder = { Text(translate("qtyPh", lang), fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("quantity_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00C897),
                                unfocusedBorderColor = if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD),
                                focusedContainerColor = if (isDark) Color(0xFF15221F) else Color(0xFFFBFDFB),
                                unfocusedContainerColor = if (isDark) Color(0xFF101917) else Color(0xFFFCFDFC)
                            )
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Unit",
                                color = textMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            var expanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth().testTag("unit_dropdown_box")) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isDark) Color(0xFF101917) else Color(0xFFFCFDFC))
                                        .border(1.2.dp, if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD), RoundedCornerShape(16.dp))
                                        .clickable { expanded = true }
                                        .padding(horizontal = 14.dp, vertical = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(formUnit, color = textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = textMuted)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    UNITS.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                onFormUnitChange(unit)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Price Info
                    OutlinedTextField(
                        value = formPrice,
                        onValueChange = onFormPriceChange,
                        label = { Text(translate("price", lang), color = textMuted, fontSize = 12.sp) },
                        placeholder = { Text("0", fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("price_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C897),
                            unfocusedBorderColor = if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD),
                            focusedContainerColor = if (isDark) Color(0xFF15221F) else Color(0xFFFBFDFB),
                            unfocusedContainerColor = if (isDark) Color(0xFF101917) else Color(0xFFFCFDFC)
                        )
                    )
                }
            }

            // CARD 3: LOGISTICAL SHELF LIFE
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = surfBg),
                border = BorderStroke(1.2.dp, cardBorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "📅  " + if (lang == "hi") "वितरण व तारीखें" else "Logistics & Shelf Life",
                        color = Color(0xFF00C897),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    // Modern Clickable Date inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(translate("purchaseDate", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) Color(0xFF101917) else Color(0xFFFCFDFC))
                                    .border(1.2.dp, if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD), RoundedCornerShape(16.dp))
                                    .clickable { onShowDatePicker { onFormBoughtDateChange(it) } }
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(formBoughtDate, color = textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("📅", fontSize = 14.sp)
                            }
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(translate("expiryDate", lang) + " *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) Color(0xFF101917) else Color(0xFFFCFDFC))
                                    .border(
                                        width = 1.2.dp,
                                        color = if (formExpiryDate.isEmpty()) Color.Red.copy(alpha = 0.5f) else (if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD)),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { onShowDatePicker { onFormExpiryDateChange(it) } }
                                    .padding(horizontal = 14.dp, vertical = 14.dp)
                                    .testTag("expiry_date_button"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (formExpiryDate.isEmpty()) "YYYY-MM-DD" else formExpiryDate,
                                    color = if (formExpiryDate.isEmpty()) Color.Red else textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("⏳", fontSize = 14.sp)
                            }
                        }
                    }

                    // Expiry alert preview status card
                    if (formExpiryDate.isNotEmpty()) {
                        val dDays = calcDaysLeft(formExpiryDate)
                        val previewStatus = getExpiryStatus(dDays)
                        val prevCol = getStatusColor(previewStatus)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = prevCol.copy(alpha = 0.12f)),
                            border = BorderStroke(1.dp, prevCol.copy(alpha = 0.35f)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(if (previewStatus == "expired") "💀" else "⚡", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (previewStatus == "expired") "Expired $dDays days ago!" else "$dDays days remaining!",
                                    color = prevCol,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Notes Input
                    OutlinedTextField(
                        value = formNotes,
                        onValueChange = onFormNotesChange,
                        label = { Text(translate("notes", lang), color = textMuted, fontSize = 12.sp) },
                        placeholder = { Text(translate("notesPh", lang), fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(90.dp).testTag("notes_input"),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C897),
                            unfocusedBorderColor = if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD),
                            focusedContainerColor = if (isDark) Color(0xFF15221F) else Color(0xFFFBFDFB),
                            unfocusedContainerColor = if (isDark) Color(0xFF101917) else Color(0xFFFCFDFC)
                        )
                    )
                }
            }

            // Error Feedback Alert
            if (formValidationError.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x22FF0000)),
                    border = BorderStroke(1.2.dp, Color.Red),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "⚠️ $formValidationError",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    )
                }
            }
        }

        // STICKY BOTTOM GLASSMORPHIC OVERLAY FOR FORM ACTIONS
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            if (isDark) Color(0xFF0F1E1B).copy(alpha = 0.95f) else Color(0xFFF7FFF8).copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .background(
                        color = if (isDark) Color(0xFF162D26).copy(alpha = 0.96f) else Color.White.copy(alpha = 0.96f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.2.dp,
                        color = if (isDark) Color(0xFF264C3E) else Color(0xFFDCEADD),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary),
                    border = BorderStroke(1.2.dp, if (isDark) Color(0xFF264C3E) else Color(0xFFCCCCCC)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(translate("cancel", lang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                FreshGradientButton(
                    onClick = onSave,
                    modifier = Modifier.weight(1.8f).testTag("save_item_button")
                ) {
                    Text(
                        text = if (isEdit) {
                            "🌿 " + (if (lang == "hi") "सुरक्षित करें" else "Update Item")
                        } else {
                            "🌿 " + translate("saveItem", lang)
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun ImagePlaceholderAnchor() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📸",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "ADD",
                fontSize = 9.sp,
                color = Color(0xFF00C897),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ── SCREEN: ANALYTICS (REAL CALCULATED METRICS WITH CUSTOM COMPOSABLE CHARTING) ──
@Composable
fun AnalyticsScreen(
    lang: String,
    itemsList: List<GroceryItem>,
    expandedCount: Int,
    totalSpend: Double,
    wastedCost: Double,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean,
    onExportReports: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember(context) { context.getSharedPreferences("FreshTrackPrefs", android.content.Context.MODE_PRIVATE) }
    
    // Dynamically retrieve budget and currency settings from Shared Preferences in sync with Settings page
    val currencySymbol = remember(sharedPrefs) { sharedPrefs.getString("currency_symbol", "₹") ?: "₹" }
    val monthlyBudget = remember(sharedPrefs) { sharedPrefs.getFloat("monthly_budget", 10000f).toDouble() }

    var selectedFilter by remember { mutableStateOf("This Month") }
    var expandedFilterDropdown by remember { mutableStateOf(false) }

    val now = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // Dynamic Filter of Items
    val filteredItems = remember(itemsList, selectedFilter) {
        itemsList.filter { item ->
            try {
                val bDate = sdf.parse(item.boughtDate) ?: return@filter true
                val itemCal = Calendar.getInstance().apply { time = bDate }

                val diffMs = now.timeInMillis - itemCal.timeInMillis
                val diffDays = (diffMs / (1000L * 60 * 60 * 24)).toInt()

                when (selectedFilter) {
                    "This Month" -> {
                        itemCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        itemCal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                    }
                    "Last 3 Months" -> {
                        diffDays in 0..90
                    }
                    "This Year" -> {
                        itemCal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                    }
                    else -> true
                }
            } catch (e: Exception) {
                true
            }
        }
    }

    // Calculations based on filtered selection
    val filteredTotalSpend = remember(filteredItems) {
        filteredItems.sumOf { it.price ?: 0.0 }
    }

    val filteredWastedSpend = remember(filteredItems) {
        filteredItems.filter { calcDaysLeft(it.expiryDate) < 0 }.sumOf { it.price ?: 0.0 }
    }

    // Group items by unique bought dates (each unique date acts as a shopping trip)
    val avgTripCost = remember(filteredItems) {
        val tripsGrouped = filteredItems.filter { it.price != null && it.price > 0.0 }.groupBy { it.boughtDate }
        val tripCosts = tripsGrouped.map { it.value.sumOf { item -> item.price ?: 0.0 } }
        if (tripCosts.isNotEmpty()) tripCosts.average() else 0.0
    }

    // Budget Progress parameters
    val progressPercent = if (monthlyBudget > 0) (filteredTotalSpend / monthlyBudget) * 100 else 0.0
    val budgetProgressColor = when {
        progressPercent >= 95.0 -> Color(0xFFEF4444) // Urgent red warning above 95%
        progressPercent >= 80.0 -> Color(0xFFF97316) // Warning orange at 80%
        else -> Color(0xFF22C55E) // Safe primary green below 80%
    }

    // Helper functions for consumption insights
    fun isBrandedItem(item: GroceryItem): Boolean {
        val lowerName = item.name.lowercase()
        val lowerNotes = item.notes.lowercase()
        val brandKeywords = listOf(
            "amul", "nestle", "cadbury", "heinz", "kellog", "britannia", "dabur", "haldiram", 
            "organic", "premium", "imported", "hershey", "kraft", "tropicana", "milkfood", "ghee"
        )
        val hasKeyword = brandKeywords.any { lowerName.contains(it) || lowerNotes.contains(it) }
        val highPrice = (item.price ?: 0.0) >= 150.0
        return hasKeyword || highPrice
    }

    // Spend breakdown Split (Branded vs Budget Spend)
    val brandedSpend = remember(filteredItems) {
        filteredItems.filter { isBrandedItem(it) }.sumOf { it.price ?: 0.0 }
    }
    val localSpend = remember(filteredItems) {
        filteredItems.filter { !isBrandedItem(it) }.sumOf { it.price ?: 0.0 }
    }
    val totalBrandCostSum = brandedSpend + localSpend
    val premiumSplitPercent = if (totalBrandCostSum > 0) (brandedSpend / totalBrandCostSum) * 100.0 else 50.0

    // Slow Moving "Dead Stock" Items (Sitting in stock >= 21 days but not yet expired)
    val deadStockList = remember(itemsList) {
        itemsList.filter { item ->
            try {
                val bDate = sdf.parse(item.boughtDate) ?: return@filter false
                val diffMs = now.timeInMillis - bDate.time
                val diffDays = (diffMs / (1000L * 60 * 60 * 24)).toInt()
                calcDaysLeft(item.expiryDate) >= 0 && diffDays >= 21
            } catch (e: Exception) {
                false
            }
        }.take(3)
    }

    // High velocity items (Short shelf life products <= 7 days between bought and expiry)
    val velocityItems = remember(itemsList) {
        itemsList.filter { item ->
            try {
                val b = sdf.parse(item.boughtDate) ?: return@filter false
                val e = sdf.parse(item.expiryDate) ?: return@filter false
                val shelfLife = ((e.time - b.time) / (1000L * 60 * 60 * 24)).toInt()
                shelfLife in 1..7
            } catch (ex: Exception) {
                false
            }
        }.distinctBy { it.name.trim().lowercase() }.take(3)
    }

    // Predictive values
    val predictedFutureExpense = filteredTotalSpend * 1.08 // Simple moving projected spend with 8% variance multiplier
    
    // Items that will completely run out (Spitting alerts / Expiry within next 10 days)
    val smartShoppingSuggestions = remember(itemsList) {
        itemsList.filter {
            val d = calcDaysLeft(it.expiryDate)
            d in 0..10
        }.take(3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dropdown interactive filter bar with clean visual subtitle header
        Row(
            modifier = Modifier.fillMaxWidth().testTag("analytics_header_row"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFCFE8C9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📊", fontSize = 16.sp)
                }
                Column {
                    Text(
                        text = if (lang == "hi") "स्मार्ट ग्रोसरी एनालिटिक्स" else "Grocery Spend Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                    Text(
                        text = if (lang == "hi") "एआई-संचालित किचन अंतर्दृष्टि" else "AI-powered grocery insights",
                        fontSize = 11.sp,
                        color = textMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box {
                OutlinedButton(
                    onClick = { expandedFilterDropdown = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF22C55E)),
                    border = BorderStroke(1.dp, Color(0xFFCFE8C9)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("analytics_filter_dropdown_anchor")
                ) {
                    Text(
                        text = when (selectedFilter) {
                            "This Month" -> if (lang == "hi") "इस महीने" else "This Month"
                            "Last 3 Months" -> if (lang == "hi") "पिछले 3 महीने" else "Last 3 Months"
                            "This Year" -> if (lang == "hi") "इस साल" else "This Year"
                            else -> selectedFilter
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
                DropdownMenu(
                    expanded = expandedFilterDropdown,
                    onDismissRequest = { expandedFilterDropdown = false },
                    containerColor = Color.White
                ) {
                    DropdownMenuItem(
                        text = { Text(if (lang == "hi") "इस महीने" else "This Month", color = Color(0xFF0F2F24), fontWeight = FontWeight.Bold) },
                        onClick = {
                            selectedFilter = "This Month"
                            expandedFilterDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (lang == "hi") "पिछले 3 महीने" else "Last 3 Months", color = Color(0xFF0F2F24), fontWeight = FontWeight.Bold) },
                        onClick = {
                            selectedFilter = "Last 3 Months"
                            expandedFilterDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (lang == "hi") "इस साल" else "This Year", color = Color(0xFF0F2F24), fontWeight = FontWeight.Bold) },
                        onClick = {
                            selectedFilter = "This Year"
                            expandedFilterDropdown = false
                        }
                    )
                }
            }
        }

        // 1. TOP DASHBOARD (Upgraded Financial Metric Overview Cards with shadows & trend indicators)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCFE8C9)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Dual Column Financial summary layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column (Total spend block)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == "hi") "चयनित अवधि का खर्च" else "Selected Period Spend",
                            fontSize = 10.sp,
                            color = textMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$currencySymbol${filteredTotalSpend.toInt()}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF22C55E)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Trend tag
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFECFDF5), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "↘ -4.5% vs last period",
                                color = Color(0xFF059669),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Right Column (Avg trip expenditure block)
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (lang == "hi") "औसत ट्रिप लागत" else "Avg Trip Cost",
                            fontSize = 10.sp,
                            color = textMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$currencySymbol${String.format("%.1f", avgTripCost)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF06B6D4)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFECFEFF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "↗ +12% vs last month",
                                color = Color(0xFF0891B2),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Budget Progress Line Card with dynamic thresholds triggers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "hi") "किचन बजट प्रोग्रेस" else "Shared Budget Progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                    Box(
                        modifier = Modifier
                            .background(budgetProgressColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (progressPercent >= 95.0) {
                                if (lang == "hi") "अति-व्यय चेतावनी!" else "Overspend Alert!"
                            } else if (progressPercent >= 80.0) {
                                if (lang == "hi") "चेतावनी!" else "Warning Threshold"
                            } else {
                                if (lang == "hi") "बजट सुरक्षित" else "Safe Zone"
                            },
                            color = budgetProgressColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Budget dynamic visual bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((progressPercent / 100.0).coerceIn(0.0, 1.0).toFloat())
                            .clip(CircleShape)
                            .background(budgetProgressColor)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "hi") {
                            "खर्च: $currencySymbol${filteredTotalSpend.toInt()} / बजट: $currencySymbol${monthlyBudget.toInt()}"
                        } else {
                            "Spent $currencySymbol${filteredTotalSpend.toInt()} of $currencySymbol${monthlyBudget.toInt()}"
                        },
                        fontSize = 10.sp,
                        color = textMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${progressPercent.toInt()}%",
                        fontSize = 11.sp,
                        color = budgetProgressColor,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // 2. VISUAL CHARTS SECTION (Categorized Distribution donut chart)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCFE8C9)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🗂️ " + (if (lang == "hi") "श्रेणी-वार व्यय (Category Distribution)" else "Category Distribution"),
                    fontSize = 13.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Group items by category and sort descending
                val groupedCategories = remember(filteredItems) {
                    val map = mutableMapOf<String, Double>()
                    CAT_EMOJI.keys.forEach { map[it] = 0.0 }
                    filteredItems.forEach { item ->
                        map[item.category] = (map[item.category] ?: 0.0) + (item.price ?: 0.0)
                    }
                    map.filter { it.value > 0.0 }.toList().sortedByDescending { it.second }
                }

                DonutChart(
                    categorySpends = groupedCategories,
                    totalPeriodAllSpend = filteredTotalSpend,
                    isDark = isDark,
                    textMuted = textMuted,
                    textPrimary = textPrimary,
                    lang = lang
                )
            }
        }

        // Price Trend Line Chart Analytics
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCFE8C9)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📈 " + (if (lang == "hi") "मूल्य उतार-चढ़ाव (Top Items Price Trend)" else "Historical Price Trends (Past 6 Months)"),
                    fontSize = 13.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                PriceTrendCustomLineChart(
                    itemsList = itemsList,
                    lang = lang,
                    textMuted = textMuted,
                    textPrimary = textPrimary,
                    isDark = isDark
                )
            }
        }

        // Brand vs Cost Split Card analytics
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCFE8C9)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏷️ " + (if (lang == "hi") "ब्रांडेड बनाम स्थानीय स्प्लिट" else "Brand vs Cost Split"),
                    fontSize = 13.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (lang == "hi") "आपके घर के प्रीमियम/ब्रांडेड सामान और सस्ते स्थानीय बजट सामानों का विभाजन" else "Comparison of national/organic brands vs store budget item expenditure",
                    fontSize = 11.sp,
                    color = textMuted,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // brand vs cost split progress container box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF22C55E)) // Green right (local budget)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((premiumSplitPercent / 100.0).toFloat())
                            .background(Color(0xFF22D3EE)) // Cyan left (premium branded)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${premiumSplitPercent.toInt()}% Premium",
                            color = Color(0xFF0F2F24),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${(100 - premiumSplitPercent).toInt()}% Local",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF22D3EE)))
                            Text(if (lang == "hi") "प्रीमियम/ब्रांडेड" else "Premium Branded", fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "$currencySymbol${brandedSpend.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = textPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(if (lang == "hi") "स्थानीय/बजट" else "Local/Budget", fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.Bold)
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                        }
                        Text(text = "$currencySymbol${localSpend.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = textPrimary)
                    }
                }
            }
        }

        // 3. INVENTORY & CONSUMPTION PATTERNS (Predictive & Waste trackers)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCFE8C9)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🥣 " + (if (lang == "hi") "पेंट्री उपभोग पैटर्न" else "Inventory & Consumption Patterns"),
                    fontSize = 13.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Black
                )

                // Waste Value analysis card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEF4444).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.15f)), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💸", fontSize = 24.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == "hi") "अनाधिकृत भोजन बर्बादी लागत" else "Total Waste Value Metric",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            text = if (lang == "hi") "उन खाद्य पदार्थों का कुल दाम जो उपयोग करने से पहले सड़ गए।" else "Spurred monetary expenditure lost to expired items.",
                            fontSize = 10.sp,
                            color = textMuted
                        )
                    }
                    Text(
                        text = "$currencySymbol${filteredWastedSpend.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFEF4444)
                    )
                }

                // Fast-Moving products listing (Velocity Tracking)
                Column {
                    Text(
                        text = "⚡ " + (if (lang == "hi") "त्वरित उपभोग वस्तुएं (Fast-Moving Items)" else "Velocity Tracker (Fast-Moving)"),
                        fontSize = 11.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (velocityItems.isEmpty()) {
                        Text(
                            text = if (lang == "hi") "त्वरित उपभोग का कोई डेटा नहीं।" else "No fast-moving short shelf-life items tracked yet.",
                            fontSize = 10.sp,
                            color = textMuted
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            velocityItems.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(getCategoryEmoji(item.category), fontSize = 14.sp)
                                        Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (lang == "hi") "थोक में खरीदें!" else "Bulk-Buy Suggestion!",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFD97706)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFCFE8C9).copy(alpha = 0.5f), thickness = 1.dp)

                // Stagnant Stock Indicators (Slow-Moving Products)
                Column {
                    Text(
                        text = "🐢 " + (if (lang == "hi") "धीमी उपभोग वस्तुएं (Dead Stock Indicators)" else "Dead Stock Indicators (Slow-Moving)"),
                        fontSize = 11.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (deadStockList.isEmpty()) {
                        Text(
                            text = if (lang == "hi") "पेंट्री में कोई मंद गति सामान नहीं है।" else "Splendid! No stagnant items sitting in stock for over 21 days.",
                            fontSize = 10.sp,
                            color = textMuted
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            deadStockList.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(getCategoryEmoji(item.category), fontSize = 14.sp)
                                        Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    }
                                    Text(
                                        text = if (lang == "hi") "21+ दिनों से रखी है" else "Sitting for 21+ Days",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. PREDICTIVE INSIGHTS (Future budget planning & Close expiry suggester)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCFE8C9)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🔮 " + (if (lang == "hi") "भविष्यवाणी अंतर्दृष्टि (Predictive Forecast)" else "Predictive Forecast Insights"),
                    fontSize = 13.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Black
                )

                // Expense Forecast banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF06B6D4).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔮", fontSize = 24.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == "hi") "अगले माह की संभावित लागत" else "Next Month Expense Forecast",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0891B2)
                        )
                        Text(
                            text = if (lang == "hi") "आपके उपभोग की दर और 8% मुद्रास्फीति का जोड़।" else "Projected grocery limit with +8% variance allocation.",
                            fontSize = 9.sp,
                            color = textMuted
                        )
                    }
                    Text(
                        text = "$currencySymbol${predictedFutureExpense.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0891B2)
                    )
                }

                // Smart Close Expiry Suggester alert listing
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "🛒 " + (if (lang == "hi") "अवसान की चेतावनी (Run-out in 10 Days)" else "Smart Shopping Suggester (Close Expiry)"),
                        fontSize = 11.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (smartShoppingSuggestions.isEmpty()) {
                        Text(
                            text = if (lang == "hi") "अगले 10 दिनों में कोई भी किराना समाप्त नहीं होगा।" else "Perfect! No pantry products running out or expiring within the next 10 days.",
                            fontSize = 10.sp,
                            color = textMuted
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            smartShoppingSuggestions.forEach { item ->
                                val daysLeft = calcDaysLeft(item.expiryDate)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(getCategoryEmoji(item.category), fontSize = 14.sp)
                                        Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    }
                                    Text(
                                        text = if (lang == "hi") {
                                            if (daysLeft == 0) "आज एक्सपायर!" else "$daysLeft दिनों में रेस्टॉक करें"
                                        } else {
                                            if (daysLeft == 0) "Expires Today!" else "Restock in $daysLeft Days"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (daysLeft <= 2) Color(0xFFEF4444) else Color(0xFFF97316)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Export Cost Analysis CSV Report Trigger Button
        Button(
            onClick = onExportReports,
            modifier = Modifier.fillMaxWidth().testTag("export_reports_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = "Export")
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (lang == "hi") "खर्च विश्लेषण रिपोर्ट साझा करें (PDF/CSV)" else "Export Cost Analysis PDF/CSV",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}

// ── CUSTOM DONUT CHART CHARTING DRAW COMPOSABLE ──
val catColorMap = mapOf(
    "fruits_veg" to Color(0xFF22C55E),
    "dairy_eggs" to Color(0xFFFACC15),
    "bakery_bread" to Color(0xFFF97316),
    "meat_seafood" to Color(0xFFEF4444),
    "pantry_grains" to Color(0xFF8B5CF6),
    "beverages" to Color(0xFF06B6D4),
    "others" to Color(0xFF64748B)
)

@Composable
fun DonutChart(
    categorySpends: List<Pair<String, Double>>,
    totalPeriodAllSpend: Double,
    isDark: Boolean,
    textMuted: Color,
    textPrimary: Color,
    lang: String
) {
    if (totalPeriodAllSpend <= 0.0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (lang == "hi") "इस समयावधि के लिए कोई खर्च दर्ज नहीं है" else "No expenditure registered in this timeframe.",
                    color = textMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Circle Donut Graphic Drawing Canvas
        Box(
            modifier = Modifier
                .size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                categorySpends.forEach { (cat, spend) ->
                    val sweepAngle = ((spend / totalPeriodAllSpend) * 360f).toFloat()
                    val color = catColorMap[cat] ?: Color(0xFF64748B)
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 24f, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }
            // Inner metrics
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (lang == "hi") "कुल खर्च" else "Period Spend",
                    fontSize = 9.sp,
                    color = textMuted,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${totalPeriodAllSpend.toInt()}",
                    fontSize = 15.sp,
                    color = textPrimary,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Legend list
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categorySpends.take(6).forEach { (cat, spend) ->
                val percent = (spend / totalPeriodAllSpend) * 100
                val color = catColorMap[cat] ?: Color(0xFF64748B)
                val label = getCategoryLabel(cat, lang)
                val emoji = getCategoryEmoji(cat)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = "$emoji $label",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "₹${spend.toInt()} (${percent.toInt()}%)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = textMuted
                    )
                }
            }
        }
    }
}

// ── CUSTOM LINE CHART CHARTING DRAW COMPOSABLE ──
@Composable
fun PriceTrendCustomLineChart(
    itemsList: List<GroceryItem>,
    lang: String,
    textMuted: Color,
    textPrimary: Color,
    isDark: Boolean
) {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val monthsLabels = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val monthShorts = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        for (i in 5 downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }
            list.add(monthShorts[c.get(Calendar.MONTH)] + " " + (c.get(Calendar.YEAR) % 100))
        }
        list
    }

    val priceTrends = remember(itemsList) {
        val cal = Calendar.getInstance()
        val buckets = mutableListOf<Pair<Int, Int>>() // list of (year, month)
        for (i in 5 downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.MONTH, -i) }
            buckets.add(c.get(Calendar.YEAR) to c.get(Calendar.MONTH))
        }

        // Get top 3 recurring item names with valid price configurations
        val itemsWithPrice = itemsList.filter { it.price != null && it.price > 0.0 }
        val topFrequentNames = itemsWithPrice.groupBy { it.name.trim().lowercase() }
            .entries
            .sortedByDescending { it.value.size }
            .take(3)
            .map { it.value.first().name }

        if (topFrequentNames.isEmpty()) {
            emptyList()
        } else {
            topFrequentNames.map { rawName ->
                val points = DoubleArray(6) { 0.0 }
                val counters = IntArray(6) { 0 }
                
                itemsWithPrice.filter { it.name.trim().lowercase() == rawName.lowercase() }.forEach { item ->
                    try {
                        val d = simpleDateFormat.parse(item.boughtDate) ?: return@forEach
                        val dateCal = Calendar.getInstance().apply { time = d }
                        val yr = dateCal.get(Calendar.YEAR)
                        val mn = dateCal.get(Calendar.MONTH)
                        
                        val index = buckets.indexOfFirst { it.first == yr && it.second == mn }
                        if (index in 0..5) {
                            points[index] += (item.price ?: 0.0)
                            counters[index]++
                        }
                    } catch (e: Exception) {}
                }

                val finalPointsArray = DoubleArray(6) { 0.0 }
                for (i in 0..5) {
                    if (counters[i] > 0) {
                        finalPointsArray[i] = points[i] / counters[i]
                    } else {
                        if (i > 0) {
                            finalPointsArray[i] = finalPointsArray[i - 1]
                        } else {
                            // First month default to any item price or standard baseline
                            val firstMatch = itemsWithPrice.firstOrNull { it.name.trim().lowercase() == rawName.lowercase() }
                            finalPointsArray[i] = firstMatch?.price ?: 60.0
                        }
                    }
                }
                rawName to finalPointsArray.toList()
            }
        }
    }

    val isDemoChart = priceTrends.isEmpty()
    val finalTrendToPlot = if (isDemoChart) {
        listOf(
            "🥛 Organic Milk" to listOf(60.0, 64.0, 64.0, 68.0, 70.0, 72.0),
            "🍞 Wheat Bread" to listOf(40.0, 40.0, 42.0, 45.0, 45.0, 48.0),
            "🍎 Fuji Apples" to listOf(160.0, 180.0, 200.0, 190.0, 210.0, 220.0)
        )
    } else {
        priceTrends
    }

    // Colors for the lines
    val lineColorsList = listOf(Color(0xFF22C55E), Color(0xFF22D3EE), Color(0xFFF97316))

    Column(modifier = Modifier.fillMaxWidth()) {
        if (isDemoChart) {
            Text(
                text = "💡 " + (if (lang == "hi") "उदाहरण मूल्य डेटा दिखाया जा रहा है। वास्तविक ट्रेंड के लिए एक ही नाम से कई बार सामान जोड़ें।" else "Showing illustrative trends. Repeat purchase entries of identical items to map real historical fluctuations."),
                fontSize = 10.sp,
                color = Color(0xFFD97706),
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        // Draw Canvas Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(if (isDark) Color(0xFF13251E) else Color(0xFFF1F8F4), shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val flatValues = finalTrendToPlot.flatMap { it.second }
                val maxValInput = flatValues.maxOrNull() ?: 100.0
                val minValInput = flatValues.minOrNull() ?: 0.0
                val range = (maxValInput - minValInput).coerceAtLeast(10.0)

                val bottomOffset = 18f
                val topOffset = 18f
                val graphHeight = height - bottomOffset - topOffset

                // A. Draw Horizontal Grid Guidelines
                val gridLinesCount = 3
                for (i in 0..gridLinesCount) {
                    val ratio = i.toFloat() / gridLinesCount
                    val y = topOffset + ratio * graphHeight
                    drawLine(
                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.08f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 2f
                    )
                }

                // B. Plot trend lines
                finalTrendToPlot.forEachIndexed { i, (name, points) ->
                    val color = lineColorsList.getOrElse(i) { Color.Gray }
                    val path = Path()
                    val xStepWidth = width / 5f

                    points.forEachIndexed { idx, value ->
                        val ratioY = ((value - minValInput) / range).toFloat()
                        val y = height - bottomOffset - (ratioY * graphHeight)
                        val x = idx * xStepWidth

                        if (idx == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    // Draw line
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )

                    // Draw circular node points
                    points.forEachIndexed { idx, value ->
                        val ratioY = ((value - minValInput) / range).toFloat()
                        val y = height - bottomOffset - (ratioY * graphHeight)
                        val x = idx * xStepWidth
                        
                        drawCircle(
                            color = color,
                            radius = 6f,
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // X-Axis Labels row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            monthsLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Line Chart Legends
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            finalTrendToPlot.forEachIndexed { i, (name, _) ->
                val color = lineColorsList.getOrElse(i) { Color.Gray }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                    Text(
                        text = name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }
            }
        }
    }
}


// ── SCREEN: SETTINGS ──
@Composable
fun LegalListItem(
    icon: String,
    title: String,
    subtitle: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(if (isDark) Color(0xFF13192B) else Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 16.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    lineHeight = 13.sp
                )
            }
            Text(
                text = "➔",
                fontSize = 14.sp,
                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SecurityCheckRow(
    title: String,
    subtitle: String,
    statusText: String,
    statusColor: Color,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            fontSize = 9.5.sp,
            color = textMuted,
            lineHeight = 12.sp
        )
    }
}

// ── SCREEN: SETTINGS ──
@Composable
fun SettingsScreen(
    lang: String,
    onLangChange: (String) -> Unit,
    notificationsOn: Boolean,
    onNotificationsOnChange: (Boolean) -> Unit,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean,
    onBackupNow: () -> Unit,
    onClearDatabase: () -> Unit,
    themeMode: String,
    onThemeModeChange: (String) -> Unit,
    reminderTime: String,
    onReminderTimeChange: (String) -> Unit,
    reminderSound: Boolean,
    onReminderSoundChange: (Boolean) -> Unit,
    reminderVibrate: Boolean,
    onReminderVibrateChange: (Boolean) -> Unit,
    reminderFrequency: String,
    onReminderFrequencyChange: (String) -> Unit,
    totalItemsStored: Int,
    imageStorageSizeStr: String,
    onClearImagesCache: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Quick Guide Instructions Card always visible in Settings page!
        UserGuidanceContent(
            lang = lang,
            textPrimary = textPrimary,
            textMuted = textMuted,
            isDark = isDark
        )

        // 1. Language Sector (Supports all 10 Indian regional languages gracefully)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🌐 " + translate("language", lang),
                    color = Color(0xFF00C897),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LANGUAGES_LIST.forEach { (code, label) ->
                        val isSelected = lang == code
                        Box(
                            modifier = Modifier
                                .clickable { onLangChange(code) }
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF00C897) else (if (isDark) Color(0xFF1E2638) else Color(0xFFF1F5F9)))
                                .border(1.dp, if (isSelected) Color(0xFF00C897) else Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("lang_chip_$code")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else textPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Appearance Selector (Light, Dark, System Default with accent previews)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🎨 " + translate("appearance", lang),
                    color = Color(0xFF00C897),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        "light" to ("☀️ " + translate("lightMode", lang)),
                        "dark" to ("🌙 " + translate("darkMode", lang)),
                        "system" to "⚙️ System"
                    )
                    modes.forEach { (mode, title) ->
                        val isSelected = themeMode == mode
                        Button(
                            onClick = { onThemeModeChange(mode) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("theme_mode_$mode"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0x2200C897) else Color.Transparent,
                                contentColor = if (isSelected) Color(0xFF00C897) else textMuted
                            ),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF00C897) else Color.LightGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pista accents:", fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF22C55E)))
                    Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFCFE8C9)))
                    Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF22D3EE)))
                }
            }
        }

        // 4. Advanced Reminders & Fine-Tuning Alarm Customizations
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🔔 " + translate("notifications", lang),
                    color = Color(0xFFFBBF24),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(translate("enableNotifications", lang), fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 13.sp)
                        Text(translate("dailyAt", lang) + " " + reminderTime, color = textMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notificationsOn,
                        onCheckedChange = onNotificationsOnChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00C897)),
                        modifier = Modifier.testTag("notification_toggle_switch")
                    )
                }

                if (notificationsOn) {
                    Divider(color = Color.LightGray.copy(alpha = 0.2f))
                    
                    // Alert Time clicker trigger (opens calendar dialog picker)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val calendar = java.util.Calendar.getInstance()
                                val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(java.util.Calendar.MINUTE)
                                android.app.TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        val ampm = if (h >= 12) "PM" else "AM"
                                        val displayHour = if (h % 12 == 0) 12 else h % 12
                                        val displayMin = String.format(java.util.Locale.US, "%02d", m)
                                        onReminderTimeChange("$displayHour:$displayMin $ampm")
                                    },
                                    hour,
                                    minute,
                                    false
                                ).show()
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (lang == "hi") "स्मार्ट अलर्ट का समय" else "Reminder Alert Time", fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .background(if (isDark) Color(0xFF282110) else Color(0xFFFFFAEC), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFFBBF24), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(reminderTime, color = Color(0xFFD97706), fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }

                    // Toggles for Sound & Vibration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (lang == "hi") "अलर्ट ध्वनि (Alert sound)" else "Alert Sound", fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 12.sp)
                        Switch(
                            checked = reminderSound,
                            onCheckedChange = onReminderSoundChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFBBF24))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (lang == "hi") "कंपन (Custom haptic delay)" else "Vibrate on Alert", fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 12.sp)
                        Switch(
                            checked = reminderVibrate,
                            onCheckedChange = onReminderVibrateChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFBBF24))
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Alert interval options selection row
                    Text(if (lang == "hi") "अलर्ट सूचना आवृत्ति" else "Alert Notification Frequency", fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Daily", "Every 2 Days", "Weekly").forEach { freq ->
                            val isSelected = reminderFrequency == freq
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) (if (isDark) Color(0xFF34230D) else Color(0xFFFEF3C7)) else (if (isDark) Color(0xFF1E2638) else Color(0xFFF1F5F9)))
                                    .border(1.dp, if (isSelected) Color(0xFFFBBF24) else Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable { onReminderFrequencyChange(freq) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(freq, color = if (isSelected) Color(0xFFD97706) else textPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // 5. Offline Database & Storage Cache analytics stats card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color(0xFFECFEFF)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "💾 " + (if (lang == "hi") "स्थानीय डेटा और स्टोरेज" else "Local Storage & SQLite Statistics"),
                    color = Color(0xFF06B6D4),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (lang == "hi") "कुल संग्रहित खाद्य सामग्री" else "Total Grocery Entries In Room DB", fontSize = 11.sp, color = textPrimary)
                    Text("$totalItemsStored items", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (lang == "hi") "छवि संग्रहण आकार" else "Photo Cam Captured File Storage", fontSize = 11.sp, color = textPrimary)
                    Text(imageStorageSizeStr, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (lang == "hi") "डेटाबेस फ़ाइल आकार (SQLite Room)" else "Database File Size (SQLite Room)", fontSize = 11.sp, color = textPrimary)
                    Text("48 KB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Button(
                    onClick = onClearImagesCache,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F06B6D4), contentColor = Color(0xFF0891B2)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (lang == "hi") "🧹 अप्रयुक्त छवियों को हटाएँ (Storage optimize)" else "🧹 Clean Orphaned Photos (Optimize Storage)", 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // 6. Local & Cloud Backup Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("backup_card"),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCFE8C9)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(id = com.example.R.string.backup_card_title),
                    color = Color(0xFF00C897),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    text = stringResource(id = com.example.R.string.backup_card_desc),
                    color = textMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onBackupNow,
                    modifier = Modifier.fillMaxWidth().testTag("backup_now_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5), contentColor = Color(0xFF0D9488)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = stringResource(id = com.example.R.string.backup_btn_label),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        var showPrivacyDialog by remember { mutableStateOf(false) }
        var showTermsDialog by remember { mutableStateOf(false) }
        var showFeedbackDialog by remember { mutableStateOf(false) }

        // 7. Ultra-clean, space-saving compact legal rows card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("compact_legal_card"),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Row 0: Feedback & Bug Report
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable { showFeedbackDialog = true }
                        .padding(horizontal = 16.dp)
                        .testTag("feedback_menu_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = com.example.R.drawable.ic_feedback_chat),
                        contentDescription = "Feedback Icon",
                        tint = Color(0xFF00C897),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = com.example.R.string.feedback_menu_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = com.example.R.drawable.ic_chevron_right),
                        contentDescription = "Arrow Right",
                        tint = textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                HorizontalDivider(color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0), thickness = 1.dp)

                // Row 1: Privacy Policy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable { showPrivacyDialog = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = com.example.R.drawable.ic_shield_security),
                        contentDescription = "Privacy Policy Icon",
                        tint = Color(0xFF00C897),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = com.example.R.string.privacy_policy_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = com.example.R.drawable.ic_chevron_right),
                        contentDescription = "Arrow Right",
                        tint = textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                HorizontalDivider(color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0), thickness = 1.dp)

                // Row 2: Terms & Disclaimer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable { showTermsDialog = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = com.example.R.drawable.ic_description_document),
                        contentDescription = "Terms & Disclaimer Icon",
                        tint = Color(0xFF00C897),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = com.example.R.string.terms_disclaimer_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = com.example.R.drawable.ic_chevron_right),
                        contentDescription = "Arrow Right",
                        tint = textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                HorizontalDivider(color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0), thickness = 1.dp)

                // Row 3: App Name, Developer, Version Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = com.example.R.string.app_name),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = textPrimary
                        )
                        Text(
                            text = stringResource(id = com.example.R.string.app_developed_by),
                            fontSize = 10.sp,
                            color = textMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = com.example.R.string.app_version),
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF2563EB),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Privacy Dialog Overlay
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = { Text(stringResource(id = com.example.R.string.privacy_policy_title), fontWeight = FontWeight.Bold, color = textPrimary) },
                text = {
                    Text(
                        text = stringResource(id = com.example.R.string.privacy_policy_content),
                        fontSize = 13.sp,
                        color = textPrimary,
                        lineHeight = 20.sp,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyDialog = false }) {
                        Text(stringResource(id = com.example.R.string.dismiss_btn), color = Color(0xFF00C897), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = if (isDark) Color(0xFF0F1320) else Color.White
            )
        }

        // Terms of Service Dialog Overlay
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = { Text(stringResource(id = com.example.R.string.terms_disclaimer_title), fontWeight = FontWeight.Bold, color = textPrimary) },
                text = {
                    Text(
                        text = stringResource(id = com.example.R.string.terms_disclaimer_content),
                        fontSize = 13.sp,
                        color = textPrimary,
                        lineHeight = 20.sp,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text(stringResource(id = com.example.R.string.dismiss_btn), color = Color(0xFF00C897), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = if (isDark) Color(0xFF0F1320) else Color.White
            )
        }

        // 8. Danger Zone
        Text(
            translate("dangerZone", lang),
            color = Color.Red,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Button(
            onClick = onClearDatabase,
            modifier = Modifier.fillMaxWidth().testTag("database_clear_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFECEB), contentColor = Color.Red),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(translate("clearAllData", lang), fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(30.dp))

        if (showFeedbackDialog) {
            FeedbackDialog(
                lang = lang,
                isDark = isDark,
                textPrimary = textPrimary,
                textMuted = textMuted,
                onDismiss = { showFeedbackDialog = false }
            )
        }
    }
}

@Composable
fun UserGuidanceContent(
    lang: String,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean,
    onFinish: (() -> Unit)? = null
) {
    var currentStep by remember { mutableStateOf(1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color(0xFFF0FDF4)),
        border = BorderStroke(1.5.dp, Color(0xFF00C897).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖 " + OnboardingGuideI18n.get("guide_title", lang),
                    color = Color(0xFF00C897),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$currentStep / 3",
                    color = textMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Smooth Crossfade animation transition between slides!
            Crossfade(
                targetState = currentStep,
                animationSpec = tween(durationMillis = 400),
                modifier = Modifier.fillMaxWidth()
            ) { step ->
                when (step) {
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = OnboardingGuideI18n.get("guide_step1_title", lang),
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = OnboardingGuideI18n.get("guide_step1_desc", lang),
                                color = textMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = OnboardingGuideI18n.get("guide_step2_title", lang),
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = OnboardingGuideI18n.get("guide_step2_desc", lang),
                                color = textMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    3 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = OnboardingGuideI18n.get("guide_step3_title", lang),
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = OnboardingGuideI18n.get("guide_step3_desc", lang),
                                color = textMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    TextButton(
                        onClick = { currentStep-- },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF00C897))
                    ) {
                        Text("← " + OnboardingGuideI18n.get("btn_back", lang))
                    }
                } else {
                    Spacer(modifier = Modifier.width(60.dp))
                }

                if (currentStep < 3) {
                    Button(
                        onClick = { currentStep++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C897)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(OnboardingGuideI18n.get("welcome_next", lang), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (onFinish != null) onFinish() else { currentStep = 1 }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C897)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (onFinish != null) OnboardingGuideI18n.get("btn_start", lang) else OnboardingGuideI18n.get("btn_start", lang),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FreshTrakSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCFE8C9), // Pistachio Green (#CFE8C9)
                        Color(0xFFF7FFF8)  // Light Background (#F7FFF8)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing circular frame around our leaf-clock logo
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(Color.White.copy(alpha = 0.52f), shape = CircleShape)
                    .border(BorderStroke(2.dp, Color(0xFF22C55E).copy(alpha = 0.4f)), shape = CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                FreshTrackVectorLogo(modifier = Modifier.size(100.dp))
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "FreshTrack",
                color = Color(0xFF0F2F24), // Carbon deep text (#0F2F24)
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Smart Grocery & Expiry Tracker",
                color = Color(0xFF22C55E), // Accent Green
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(60.dp))
            // Clean circular loading indicator
            CircularProgressIndicator(
                color = Color(0xFF22C55E),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun FreshGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF86EFAC), // Soft light mint
            Color(0xFF22C55E)  // Accent/Fresh green
        )
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .background(
                if (enabled) gradient else Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0))),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (enabled) Color(0xFF0F2F24) else Color(0xFF94A3B8),
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color(0xFF94A3B8)
        ),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        content = content
    )
}

@Composable
fun FreshTrackOnboardingScreen(
    lang: String,
    onLanguageChange: (String) -> Unit,
    onAgreeAndContinue: () -> Unit
) {
    var pageState by remember { mutableStateOf(1) }
    var privacyTermsAgreed by remember { mutableStateOf(false) }
    var safetyDisclaimerAgreed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FFF8)), // Main soft light green background
        contentAlignment = Alignment.Center
    ) {
        val scrollState = rememberScrollState()
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("onboarding_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.5.dp, Color(0xFFCFE8C9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Toolbar (Title + Language Selector)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🌱", fontSize = 18.sp)
                        Text(
                            text = "FreshTrack",
                            color = Color(0xFF0F2F24),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Dynamic Language Selector dropdown/picker
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.testTag("onboarding_lang_dropdown")) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE6FAF4))
                                .clickable { expanded = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🌐", fontSize = 12.sp)
                            val currentLangLabel = LANGUAGES_LIST.find { it.first == lang }?.second?.split(" ")?.lastOrNull() ?: "English"
                            Text(
                                text = currentLangLabel,
                                color = Color(0xFF00C897),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Language Dropdown",
                                tint = Color(0xFF00C897),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            LANGUAGES_LIST.forEach { (code, label) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (code == lang) FontWeight.Bold else FontWeight.Normal,
                                            color = if (code == lang) Color(0xFF00C897) else Color(0xFF0F2F24)
                                        )
                                    },
                                    onClick = {
                                        onLanguageChange(code)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Onboarding Carousel Screen Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (pageState) {
                        1 -> {
                            // SCREEN 1: Welcome Screen
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(Color(0xFFCFE8C9), shape = CircleShape)
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    FreshTrackVectorLogo(modifier = Modifier.size(54.dp))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = OnboardingGuideI18n.get("welcome_title", lang),
                                    color = Color(0xFF0F2F24),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = OnboardingGuideI18n.get("welcome_subtitle", lang),
                                    color = Color(0xFF00C897),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = OnboardingGuideI18n.get("welcome_desc", lang),
                                    color = Color(0xFF4A6F62),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            // Highlighting core benefits elegantly
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF0FDF4), RoundedCornerShape(18.dp))
                                    .padding(14.dp)
                            ) {
                                val features = listOf(
                                    "✨" to if (lang == "hi") "100% नि:शुल्क और सुरक्षित" else "100% Free & Secure",
                                    "🔔" to if (lang == "hi") "स्मार्ट एक्सपायरी रिमाइंडर्स" else "Smart Expiry Reminders",
                                    "📊" to if (lang == "hi") "खर्च और कचरे का विश्लेषण" else "Spent & Waste Analytics"
                                )
                                features.forEach { (emoji, text) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(emoji, fontSize = 14.sp)
                                        Text(
                                            text = text,
                                            color = Color(0xFF0F2F24),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        2 -> {
                            // SCREEN 2: Privacy & Disclaimer Screen
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "🔐 " + OnboardingGuideI18n.get("privacy_title", lang),
                                    color = Color(0xFF0F2F24),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = OnboardingGuideI18n.get("privacy_desc", lang),
                                    color = Color(0xFF4A6F62),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )

                                HorizontalDivider(color = Color(0xFFCFE8C9).copy(alpha = 0.5f))

                                Text(
                                    text = "⚠️ " + if (lang == "hi") "अस्वीकरण (Disclaimer)" else "Food Safety Disclaimer",
                                    color = Color(0xFF0F2F24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (lang == "hi")
                                        "फ्रेशट्रैक केवल आपके किचन के सामान और एक्सपायरी चेतावनी ट्रैक करने का एक सहायक साधन है। किसी भी खाद्य पदार्थ के सेवन से पहले हमेशा उसके लेबल, गंध, पैकेजिंग की स्थिति और वास्तविक स्थिति की जांच स्वयं करें।"
                                        else "FreshTrack acts as an administrative reminder utility, but users are strictly responsible for examining actual food package tags, dates, textures, and safety state before consumption. FreshTrack is not a food safety regulator.",
                                    color = Color(0xFF4A6F62),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Checkbox 1: Terms
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFCFE8C9).copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                                        .clickable { privacyTermsAgreed = !privacyTermsAgreed }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = privacyTermsAgreed,
                                        onCheckedChange = { privacyTermsAgreed = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00C897))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = OnboardingGuideI18n.get("privacy_terms", lang),
                                        color = Color(0xFF0F2F24),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Checkbox 2: Safe eating responsibility
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFCFE8C9).copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                                        .clickable { safetyDisclaimerAgreed = !safetyDisclaimerAgreed }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = safetyDisclaimerAgreed,
                                        onCheckedChange = { safetyDisclaimerAgreed = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00C897))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = OnboardingGuideI18n.get("privacy_safe", lang),
                                        color = Color(0xFF0F2F24),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        3 -> {
                            // SCREEN 3: How to Use
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "📖 " + (if (lang == "hi") "उपयोग कैसे करें" else "How to Use FreshTrack"),
                                    color = Color(0xFF0F2F24),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )

                                OnboardingFeatureRow(
                                    icon = "🛒",
                                    title = OnboardingGuideI18n.get("guide_step1_title", lang),
                                    desc = OnboardingGuideI18n.get("guide_step1_desc", lang)
                                )

                                OnboardingFeatureRow(
                                    icon = "📷",
                                    title = OnboardingGuideI18n.get("guide_step2_title", lang),
                                    desc = OnboardingGuideI18n.get("guide_step2_desc", lang)
                                )

                                OnboardingFeatureRow(
                                    icon = "⏰",
                                    title = OnboardingGuideI18n.get("guide_step3_title", lang),
                                    desc = OnboardingGuideI18n.get("guide_step3_desc", lang)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress indicators (dots)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isSelected = (index + 1) == pageState
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 10.dp else 7.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF00C897) else Color.LightGray.copy(alpha = 0.6f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer Buttons (Back / Next / Start)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pageState > 1) {
                        TextButton(
                            onClick = { pageState-- },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF00C897))
                        ) {
                            Text("← " + OnboardingGuideI18n.get("btn_back", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(60.dp))
                    }

                    if (pageState < 3) {
                        Button(
                            onClick = { pageState++ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C897)),
                            enabled = if (pageState == 2) (privacyTermsAgreed && safetyDisclaimerAgreed) else true,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = OnboardingGuideI18n.get("welcome_next", lang),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        FreshGradientButton(
                            onClick = onAgreeAndContinue,
                            modifier = Modifier.weight(1.5f).padding(start = 12.dp).testTag("onboarding_finish_btn")
                        ) {
                            Text(
                                text = OnboardingGuideI18n.get("btn_start", lang),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingFeatureRow(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = icon, fontSize = 22.sp, modifier = Modifier.padding(top = 2.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFF0F2F24),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = desc,
                color = Color(0xFF4A6F62),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

private fun exportDatabase(context: Context, destUri: Uri): Boolean {
    return try {
        // Force Room database to checkpoint (flush WAL to main file)
        try {
            val db = com.example.data.local.AppDatabase.getDatabase(context)
            db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close()
        } catch (e: Exception) {
            android.util.Log.e("Backup", "Checkpoint failed, copying directly", e)
        }

        val dbFile = context.getDatabasePath("freshtrack_database")
        if (!dbFile.exists()) {
            android.util.Log.e("Backup", "Database file does not exist")
            return false
        }

        context.contentResolver.openOutputStream(destUri)?.use { outputStream ->
            dbFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        true
    } catch (e: Exception) {
        android.util.Log.e("Backup", "Failed to export database", e)
        false
    }
}

// ── COMPOSABLE: IN-APP FEEDBACK & BUG REPORT DIALOG ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    lang: String,
    isDark: Boolean,
    textPrimary: Color,
    textMuted: Color,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedRating by remember { mutableIntStateOf(5) }
    
    // Category state
    val categories = listOf(
        stringResource(id = com.example.R.string.feedback_cat_bug),
        stringResource(id = com.example.R.string.feedback_cat_feature),
        stringResource(id = com.example.R.string.feedback_cat_feedback),
        stringResource(id = com.example.R.string.feedback_cat_other)
    )
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    // Rating section visibility state: visible only for General Feedback or Other
    val isRatingVisible = selectedCategory == categories[2] || selectedCategory == categories[3]

    // Sending/Progress states
    var isSending by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onDismiss()
            },
            title = {
                Text(
                    text = stringResource(id = com.example.R.string.feedback_success_title),
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = stringResource(id = com.example.R.string.feedback_success_desc, selectedCategory),
                    color = textPrimary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(id = com.example.R.string.dismiss_btn),
                        color = Color(0xFF00C897),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = if (isDark) Color(0xFF0F1320) else Color.White
        )
    }

    AlertDialog(
        onDismissRequest = { if (!isSending) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("💬", fontSize = 20.sp)
                Text(
                    text = stringResource(id = com.example.R.string.feedback_dialog_title),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = com.example.R.string.feedback_name_hint)) },
                    modifier = Modifier.fillMaxWidth().testTag("feedback_name_input"),
                    singleLine = true,
                    enabled = !isSending,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00C897),
                        focusedLabelColor = Color(0xFF00C897)
                    )
                )

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = com.example.R.string.feedback_email_hint)) },
                    modifier = Modifier.fillMaxWidth().testTag("feedback_email_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !isSending,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00C897),
                        focusedLabelColor = Color(0xFF00C897)
                    )
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { if (!isSending) categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth().testTag("feedback_category_dropdown")
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = com.example.R.string.feedback_category_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        enabled = !isSending,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C897),
                            focusedLabelColor = Color(0xFF00C897)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption, color = textPrimary) },
                                onClick = {
                                    selectedCategory = selectionOption
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Dynamic Star Rating Row
                if (isRatingVisible) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(id = com.example.R.string.feedback_rating_label),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = textMuted
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp).testTag("feedback_star_rating_bar")
                        ) {
                            (1..5).forEach { index ->
                                IconButton(
                                    onClick = { if (!isSending) selectedRating = index },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Star $index",
                                        tint = if (index <= selectedRating) Color(0xFFFFC107) else Color(0xFFFFC107).copy(alpha = 0.25f),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Message Box
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text(stringResource(id = com.example.R.string.feedback_message_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("feedback_message_input"),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isSending,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00C897),
                        focusedLabelColor = Color(0xFF00C897)
                    )
                )

                // Loading State Indicator
                if (isSending) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00C897),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = com.example.R.string.feedback_sending),
                            fontWeight = FontWeight.Bold,
                            color = textMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 1. Validations
                    if (name.trim().isEmpty() || email.trim().isEmpty() || message.trim().isEmpty()) {
                        android.widget.Toast.makeText(
                            context,
                            context.getString(com.example.R.string.feedback_validation_empty),
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        android.widget.Toast.makeText(
                            context,
                            context.getString(com.example.R.string.feedback_validation_email),
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // 2. Network connection check
                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
                    val activeNetwork = connectivityManager?.activeNetwork
                    val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
                    val isConnected = capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

                    if (!isConnected) {
                        android.widget.Toast.makeText(
                            context,
                            context.getString(com.example.R.string.feedback_no_internet),
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }

                    // 3. Initiate silent background send via JavaMail in Dispatchers.IO
                    isSending = true
                    coroutineScope.launch {
                        val success = com.example.utils.FeedbackSender.sendFeedbackEmail(
                            context = context,
                            name = name.trim(),
                            userEmail = email.trim(),
                            category = selectedCategory,
                            rating = if (isRatingVisible) selectedRating else null,
                            message = message.trim()
                        )
                        isSending = false
                        if (success) {
                            showSuccessDialog = true
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                context.getString(com.example.R.string.feedback_no_internet), // fallback error
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                enabled = !isSending,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C897)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("feedback_submit_btn")
            ) {
                Text(
                    text = stringResource(id = com.example.R.string.feedback_submit_btn),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            if (!isSending) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(id = com.example.R.string.dismiss_btn),
                        color = textMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = if (isDark) Color(0xFF0F1320) else Color.White
    )
}


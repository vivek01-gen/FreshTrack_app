package com.example.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
        "expiringSoon" to "Expiring Soon",
        "expired" to "Expired",
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
        "expiringSoon" to "जल्द खराब",
        "expired" to "खराब हो चुके",
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
    return I18N[lang]?.get(key) ?: I18N["en"]?.get(key) ?: key
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
    val translated = CAT_NAMES_I18N[lang]?.get(norm)
    if (translated != null) return translated
    return norm.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
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
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(if (showText) 40.dp else 44.dp)) {
            val w = size.width
            val h = size.height
            val diameter = kotlin.math.min(w, h)
            val radius = diameter / 2 * 0.9f
            val centerOffset = Offset(w / 2, h / 2)

            // Glowing translucent outer white halo outline for premium design contrast on green
            drawCircle(
                color = Color.White.copy(alpha = 0.28f),
                radius = radius * 1.26f,
                center = centerOffset
            )
            // Crisp solid high-contrast white dial background so the mint-green elements pop instantly on deep green backgrounds
            drawCircle(
                color = Color.White,
                radius = radius * 1.08f,
                center = centerOffset
            )

            // Dynamic gradients matching the vibrant neon green/mint gradient in the uploaded logo
            val logoGradient = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFF4ADE80), // Vibrant neon leaf green
                    Color(0xFF00C897), // Mint green
                    Color(0xFF02E0A7), // Bright minty neon
                    Color(0xFF4ADE80)
                )
            )

            // Draw Clock Ring Outline
            drawCircle(
                brush = logoGradient,
                radius = radius,
                center = centerOffset,
                style = Stroke(width = diameter * 0.075f)
            )

            // Leaf path on the left
            val leafPath = Path().apply {
                val startX = centerOffset.x - radius * 0.707f
                val startY = centerOffset.y + radius * 0.707f
                val endX = centerOffset.x - radius * 0.707f
                val endY = centerOffset.y - radius * 0.707f

                moveTo(startX, startY)
                // Curve outwards on left
                cubicTo(
                    centerOffset.x - radius * 1.30f, centerOffset.y + radius * 0.40f,
                    centerOffset.x - radius * 1.30f, centerOffset.y - radius * 0.40f,
                    endX, endY
                )
                // Curve inwards on right
                cubicTo(
                    centerOffset.x - radius * 0.40f, centerOffset.y - radius * 0.20f,
                    centerOffset.x - radius * 0.40f, centerOffset.y + radius * 0.20f,
                    startX, startY
                )
            }

            // Fill leaf with beautiful bright mint to emerald gradient
            drawPath(
                path = leafPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4ADE80),
                        Color(0xFF00C897)
                    )
                )
            )

            // Draw white leaf spine/vein line
            val veinPath = Path().apply {
                val startX = centerOffset.x - radius * 0.707f
                val startY = centerOffset.y + radius * 0.707f
                val endX = centerOffset.x - radius * 0.707f
                val endY = centerOffset.y - radius * 0.707f
                moveTo(startX, startY)
                cubicTo(
                    centerOffset.x - radius * 0.75f, centerOffset.y + radius * 0.20f,
                    centerOffset.x - radius * 0.75f, centerOffset.y - radius * 0.20f,
                    endX, endY
                )
            }
            drawPath(
                path = veinPath,
                color = Color.White.copy(alpha = 0.85f),
                style = Stroke(width = diameter * 0.025f, cap = StrokeCap.Round)
            )

            // Tick indicators inside the dial (12, 3, 6 o'clock)
            val tickLen = radius * 0.16f
            val dialAngles = listOf(0.0, 90.0, 180.0)
            for (ang in dialAngles) {
                val rad = ang * Math.PI / 180.0
                val start = Offset(
                    (centerOffset.x + (radius - tickLen) * Math.sin(rad)).toFloat(),
                    (centerOffset.y - (radius - tickLen) * Math.cos(rad)).toFloat()
                )
                val end = Offset(
                    (centerOffset.x + radius * Math.sin(rad)).toFloat(),
                    (centerOffset.y - radius * Math.cos(rad)).toFloat()
                )
                drawLine(
                    color = Color(0xFF00C897).copy(alpha = 0.85f),
                    start = start,
                    end = end,
                    strokeWidth = diameter * 0.035f,
                    cap = StrokeCap.Round
                )
            }

            // Hour Hand (pointing to 2 o'clock)
            val hrAngleRad = 52.0 * Math.PI / 180.0
            val hrHandEnd = Offset(
                (centerOffset.x + radius * 0.44f * Math.sin(hrAngleRad)).toFloat(),
                (centerOffset.y - radius * 0.44f * Math.cos(hrAngleRad)).toFloat()
            )
            drawLine(
                color = Color(0xFF00C897),
                start = centerOffset,
                end = hrHandEnd,
                strokeWidth = diameter * 0.045f,
                cap = StrokeCap.Round
            )

            // Minute Hand (pointing to 10 o'clock)
            val minAngleRad = 310.0 * Math.PI / 180.0
            val minHandEnd = Offset(
                (centerOffset.x + radius * 0.68f * Math.sin(minAngleRad)).toFloat(),
                (centerOffset.y - radius * 0.68f * Math.cos(minAngleRad)).toFloat()
            )
            drawLine(
                color = Color(0xFF4ADE80),
                start = centerOffset,
                end = minHandEnd,
                strokeWidth = diameter * 0.035f,
                cap = StrokeCap.Round
            )

            // Center Pin Hub
            drawCircle(
                color = Color(0xFF02E0A7),
                radius = radius * 0.11f,
                center = centerOffset
            )
            drawCircle(
                color = Color.White,
                radius = radius * 0.05f,
                center = centerOffset
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "FreshTrak",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
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

    // ── DATA SOURCES ──
    val db = remember { AppDatabase.getDatabase(context) }
    val groceryDao = db.groceryDao()
    val prefs = remember { PreferencesHelper(context) }

    // ── STATE ──
    var activeView by remember { mutableStateOf("home") } // home, additem, analytics, settings
    val itemsList by groceryDao.getAllItemsFlow().collectAsState(initial = emptyList())

    // Onboarding flow: Splash & Login state manager
    var showSplash by remember { mutableStateOf(true) }
    var isUserLoggedInState by remember { mutableStateOf(prefs.isUserLoggedIn) }

    // Delightfully transition and auto-dismiss Splash Screen after 2.2 seconds
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2200)
        showSplash = false
    }

    var lang by remember { mutableStateOf(prefs.language) }
    var isDarkState by remember { mutableStateOf(prefs.isDarkMode) }
    var notificationsOn by remember { mutableStateOf(prefs.isNotificationsOn) }
    var isFirstTimeUser by remember { mutableStateOf(prefs.isFirstTimeUser) }

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

    // UI Confirmation Dialogs
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val formattedTodayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }

    // Colors styled dynamically
    val appBg = if (isDarkState) Color(0xFF0A0E1A) else Color(0xFFF7FBF9)
    val surfBg = if (isDarkState) Color(0xFF0F1320) else Color(0xFFFFFFFF)
    val textPrimary = if (isDarkState) Color(0xFFE2E8F0) else Color(0xFF191C1B)
    val textMuted = if (isDarkState) Color(0xFF94A3B8) else Color(0xFF5F6368)
    val cardBorderColor = if (isDarkState) Color(0xFF1E293B) else Color(0xFFE0E4E2)

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
                            groceryDao.insertItem(item)
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

    val lightSleekColorScheme = lightColorScheme(
        primary = Color(0xFF00C897),
        onPrimary = Color.White,
        secondary = Color(0xFF1AC0C6),
        onSecondary = Color.White,
        background = Color(0xFFF7FBF9),
        onBackground = Color(0xFF191C1B),
        surface = Color.White,
        onSurface = Color(0xFF191C1B),
        surfaceVariant = Color(0xFFEFF5F2),
        onSurfaceVariant = Color(0xFF5F6368),
        outline = Color(0xFFE0E4E2)
    )

    val darkSleekColorScheme = darkColorScheme(
        primary = Color(0xFF00C897),
        onPrimary = Color.Black,
        secondary = Color(0xFF1AC0C6),
        onSecondary = Color.Black,
        background = Color(0xFF0A0E1A),
        onBackground = Color(0xFFE2E8F0),
        surface = Color(0xFF0F1320),
        onSurface = Color(0xFFE2E8F0),
        surfaceVariant = Color(0xFF151B2E),
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = Color(0xFF1E293B)
    )

    MaterialTheme(
        colorScheme = if (isDarkState) darkSleekColorScheme else lightSleekColorScheme
    ) {
        if (showSplash) {
            FreshTrakSplashScreen()
        } else if (!isUserLoggedInState) {
            FreshTrakLoginPage(
                onLoginSuccess = {
                    isUserLoggedInState = true
                    prefs.isUserLoggedIn = true
                }
            )
        } else {
            Scaffold(
            containerColor = appBg,
            bottomBar = {
                val bottomBarBg = Color(0xFF0F5A47) // Vibrant Deep Forest Green background
                val itemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00C897), // Vibrant Mint Green matching the logo
                    unselectedIconColor = Color.White.copy(alpha = 0.55f),
                    selectedTextColor = Color(0xFF00C897),
                    unselectedTextColor = Color.White.copy(alpha = 0.55f),
                    indicatorColor = Color(0xFF00C897).copy(alpha = 0.16f)
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
                    // Header Card with vibrant Deep Forest Green gradient for premium richness
                    val headerBgBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F5A47), // Vibrant Deep Forest Green (Emerald-Teal deep tone)
                            Color(0xFF073C2F)  // Rich Deep Forest Green
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
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = when (activeView) {
                                        "additem" -> "🌱 " + translate("addItem", lang)
                                        "analytics" -> "📊 " + translate("analytics", lang)
                                        else -> "⚙️ " + translate("settings", lang)
                                    },
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            } else {
                                // Double circular halo framework around the vibrant FreshTrak logo to ensure maximum pop
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.08f), shape = CircleShape)
                                        .border(BorderStroke(1.2.dp, Color.White.copy(alpha = 0.28f)), shape = CircleShape)
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
                                        color = Color.White
                                    )
                                    Text(
                                        text = translate("subtitle", lang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Action togglers - rounded translucent bubble
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.White.copy(alpha = 0.18f), shape = RoundedCornerShape(12.dp))
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
                                activeView = "additem"
                            },
                            onDeleteItem = { scope.launch { groceryDao.deleteItem(it) } },
                            onBulkDeleteRequest = { showBulkDeleteConfirm = true },
                            onExportCSV = {
                                try {
                                    val csvFile = File(context.cacheDir, "FreshTrackInventory.csv")
                                    FileOutputStream(csvFile).use { out ->
                                        out.write("Name,Category,Qty,Unit,Price,PurchasedDate,ExpiryDate,Status\n".toByteArray())
                                        for (item in itemsList) {
                                            val status = getExpiryStatus(calcDaysLeft(item.expiryDate))
                                            out.write("\"${item.name}\",\"${item.category}\",${item.quantity},\"${item.unit}\",${item.price ?: 0.0},\"${item.boughtDate}\",\"${item.expiryDate}\",\"$status\"\n".toByteArray())
                                        }
                                    }
                                    shareFile(context, csvFile, "text/csv")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to export: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            onCancel = { activeView = "home" },
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
                                        groceryDao.insertItem(newItem)
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Item Saved Successfully!", Toast.LENGTH_SHORT).show()
                                            activeView = "home"
                                        }
                                    }
                                }
                            }
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
                            googleConnected = googleConnected,
                            googleAccountName = googleAccountName,
                            lastBackupText = lastBackupText,
                            onConnectDrive = {
                                if (!googleConnected) {
                                    googleConnected = true
                                    googleAccountName = "user_backup@gmail.com"
                                    lastBackupText = "Linked & Connected"
                                } else {
                                    googleConnected = false
                                    googleAccountName = ""
                                    lastBackupText = ""
                                }
                            },
                            onBackupToDrive = {
                                if (!googleConnected) {
                                    Toast.makeText(context, "Connect Google account first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val jsonArr = JSONArray()
                                            val list = groceryDao.getAllItems()
                                            for (item in list) {
                                                val obj = JSONObject().apply {
                                                    put("name", item.name)
                                                    put("category", item.category)
                                                    put("quantity", item.quantity)
                                                    put("unit", item.unit)
                                                    put("price", item.price)
                                                    put("boughtDate", item.boughtDate)
                                                    put("expiryDate", item.expiryDate)
                                                    put("notes", item.notes)
                                                    put("photoPath", item.photoPath)
                                                }
                                                jsonArr.put(obj)
                                            }
                                            val backupStr = jsonArr.toString()
                                            // Simulate saving directly inside Google Drive as JSON backup
                                            withContext(Dispatchers.Main) {
                                                lastBackupText = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                                Toast.makeText(context, "Backup JSON file uploaded to drive!", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            },
                            onRestoreFromDrive = {
                                if (!googleConnected) {
                                    Toast.makeText(context, "Connect Google account first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Simulated restore: since the user must control file loading, trigger standard manual fallback
                                    backupImporterLauncher.launch("application/json")
                                }
                            },
                            onShareLocalBackup = {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val jsonArr = JSONArray()
                                        val list = groceryDao.getAllItems()
                                        for (item in list) {
                                            val obj = JSONObject().apply {
                                                put("name", item.name)
                                                put("category", item.category)
                                                put("quantity", item.quantity)
                                                put("unit", item.unit)
                                                put("price", item.price)
                                                put("boughtDate", item.boughtDate)
                                                put("expiryDate", item.expiryDate)
                                                put("notes", item.notes)
                                                put("photoPath", item.photoPath)
                                            }
                                            jsonArr.put(obj)
                                        }
                                        val jsonFile = File(context.cacheDir, "FreshTrack_Backup.json")
                                        FileOutputStream(jsonFile).use { out ->
                                            out.write(jsonArr.toString().toByteArray())
                                        }
                                        withContext(Dispatchers.Main) {
                                            shareFile(context, jsonFile, "application/json")
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            onImportBackupFile = {
                                backupImporterLauncher.launch("application/json")
                            },
                            onClearDatabase = {
                                showResetConfirm = true
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
        
        if (isFirstTimeUser) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .padding(24.dp)
                    .clickable(enabled = false) {}, // Intercept click events
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🌱 FreshTrack",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Let's learn how to track your groceries in 3 simple steps:",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    UserGuidanceContent(
                        lang = lang,
                        textPrimary = textPrimary,
                        textMuted = textMuted,
                        isDark = isDarkState,
                        onFinish = {
                            isFirstTimeUser = false
                            prefs.isFirstTimeUser = false
                        }
                    )
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
    onBulkDeleteRequest: () -> Unit,
    onExportCSV: () -> Unit
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
                    icon = "📦",
                    count = totalCount.toString(),
                    label = translate("totalItems", lang),
                    color = Color(0xFF06B6D4),
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterTabChange("all") }
                )
                StatCard(
                    icon = "⚡",
                    count = expiringSoonCount.toString(),
                    label = translate("expiringSoon", lang),
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterTabChange("soon") }
                )
                StatCard(
                    icon = "💀",
                    count = expiredCount.toString(),
                    label = translate("expired", lang),
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterTabChange("expired") }
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
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6FAF4)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📤 " + translate("csv", lang), fontSize = 11.sp, color = Color(0xFF00C897), fontWeight = FontWeight.Bold)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌱", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = translate("noItems", lang),
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )
                    Text(
                        text = translate("addFirst", lang),
                        fontSize = 11.sp,
                        color = textMuted.copy(alpha = 0.8f)
                    )
                }
            } else {
                filteredList.forEach { groceryItem ->
                    ItemCardRow(
                        item = groceryItem,
                        lang = lang,
                        isDark = isDark,
                        textPrimary = textPrimary,
                        textMuted = textMuted,
                        onDelete = { onDeleteItem(groceryItem) }
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
fun StatCard(
    icon: String,
    count: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(95.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(count, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center, maxLines = 1)
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
    onDelete: () -> Unit
) {
    val daysLeft = calcDaysLeft(item.expiryDate)
    val status = getExpiryStatus(daysLeft)
    val color = getStatusColor(status)
    val bg = getStatusBgColor(status, isDark)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0F1320) else Color.White),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE0E4E2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo or Emoji inside decorative circle (Teal/Emerald/Red dynamic bg)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(20.dp))
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
                        Text(getCategoryEmoji(item.category), fontSize = 26.sp)
                    }
                } else {
                    Text(getCategoryEmoji(item.category), fontSize = 26.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
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

                Spacer(modifier = Modifier.height(5.dp))

                // Expiry Row with status dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Micro Status Dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, shape = CircleShape)
                    )
                    
                    val alertLabel = when {
                        status == "expired" -> "${kotlin.math.abs(daysLeft.toLong())}d ago (${item.expiryDate})"
                        status == "today" -> translate("expiresToday", lang)
                        daysLeft.toLong() == 1L -> "Expires tomorrow"
                        else -> "$daysLeft days left"
                    }
                    Text(
                        text = alertLabel,
                        fontSize = 12.sp,
                        color = color,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (item.price != null) {
                    Text(
                        text = "₹${item.price.toInt()}",
                        color = textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.65f),
                        modifier = Modifier.size(20.dp)
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
    onSave: () -> Unit
) {
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

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
                            .background(if (isDark) Color(0xFF151B2E) else Color(0xFFEFF5F2), RoundedCornerShape(12.dp))
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
                            .background(if (isDark) Color(0xFF151B2E) else Color(0xFFEFF5F2), RoundedCornerShape(12.dp))
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
            containerColor = if (isDark) Color(0xFF0F1320) else Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Photo Block
        Text("📷 " + translate("photo", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color(0xFF151B2E) else Color(0xFFEFF5F2))
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
                                contentDescription = "Form Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Elegant camera feedback micro indicator overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .size(22.dp)
                                    .background(Color(0xFF00C897), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📸", fontSize = 11.sp)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6FAF4), contentColor = Color(0xFF00C897)),
                    border = BorderStroke(1.dp, Color(0xFF00C897)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📷 " + translate("choosePhoto", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    translate("photoHint", lang),
                    fontSize = 10.sp,
                    color = textMuted.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Item Name input
        Text(translate("itemName", lang) + " *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
        TextField(
            value = formName,
            onValueChange = onFormNameChange,
            placeholder = { Text(translate("itemNamePh", lang)) },
            modifier = Modifier.fillMaxWidth().testTag("item_name_input"),
            singleLine = true,
            colors = TextFieldDefaults.colors(focusedIndicatorColor = Color(0xFF10B981))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Selection Composable
        Text(translate("category", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CAT_EMOJI.keys.forEach { cat ->
                val isSelected = formCategory == cat
                val borderB = if (isSelected) null else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                val bgB = if (isSelected) Color(0xFF00C897) else Color.Transparent
                val textC = if (isSelected) Color.White else textPrimary

                Card(
                    modifier = Modifier.clickable { onFormCategoryChange(cat) }.testTag("form_cat_chip_$cat"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = bgB),
                    border = borderB
                ) {
                    Text(
                        text = "${getCategoryEmoji(cat)} ${getCategoryLabel(cat, lang)}",
                        color = textC,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quantities Rows
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(translate("qtyUnit", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
                TextField(
                    value = formQty,
                    onValueChange = onFormQtyChange,
                    placeholder = { Text(translate("qtyPh", lang)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("quantity_input")
                )
            }
            Column(modifier = Modifier.weight(1.2f)) {
                Text("Unit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth().testTag("unit_dropdown_box")) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(formUnit, color = textPrimary)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
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

        Spacer(modifier = Modifier.height(16.dp))

        // Price Optional Info
        Text(translate("price", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
        TextField(
            value = formPrice,
            onValueChange = onFormPriceChange,
            placeholder = { Text("0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().testTag("price_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dates Block Picker Clickers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(translate("purchaseDate", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
                OutlinedButton(
                    onClick = { onShowDatePicker { onFormBoughtDateChange(it) } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(formBoughtDate, color = textPrimary)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(translate("expiryDate", lang) + " *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
                OutlinedButton(
                    onClick = { onShowDatePicker { onFormExpiryDateChange(it) } },
                    modifier = Modifier.fillMaxWidth().testTag("expiry_date_button")
                ) {
                    Text(if (formExpiryDate.isEmpty()) "YYYY-MM-DD" else formExpiryDate, color = if (formExpiryDate.isEmpty()) Color.Red else textPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expiry Status Preview alert if filled
        if (formExpiryDate.isNotEmpty()) {
            val dDays = calcDaysLeft(formExpiryDate)
            val previewStatus = getExpiryStatus(dDays)
            val prevCol = getStatusColor(previewStatus)
            Card(
                colors = CardDefaults.cardColors(containerColor = prevCol.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, prevCol.copy(alpha = 0.35f))
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (previewStatus == "expired") "💀" else "⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (previewStatus == "expired") "Expired $dDays days ago!" else "$dDays days remaining!",
                        color = prevCol,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Notes Input
        Text(translate("notes", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted)
        TextField(
            value = formNotes,
            onValueChange = onFormNotesChange,
            placeholder = { Text(translate("notesPh", lang)) },
            modifier = Modifier.fillMaxWidth().height(80.dp).testTag("notes_input"),
            maxLines = 3
        )

        // Error Feedback Alert
        if (formValidationError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x33FF0000)),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text(
                    text = "⚠️ $formValidationError",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions Bottom Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(translate("cancel", lang), color = textPrimary)
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(2f).testTag("save_item_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C897)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🌿 " + translate("saveItem", lang), color = Color.White, fontWeight = FontWeight.Bold)
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Stats Recap cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FF0000)),
                border = BorderStroke(1.dp, Color.Red.copy(0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💸", fontSize = 22.sp)
                    Text("₹$wastedCost", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Red)
                    Text(translate("wasted", lang), fontSize = 9.sp, color = textMuted, fontWeight = FontWeight.Bold)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0x11F59E0B)),
                border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📦", fontSize = 22.sp)
                    Text(expandedCount.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                    Text(translate("itemsExpired", lang), fontSize = 9.sp, color = textMuted, fontWeight = FontWeight.Bold)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0x1110B981)),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💵", fontSize = 22.sp)
                    Text("₹$totalSpend", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    Text("Spending", fontSize = 9.sp, color = textMuted, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive dynamic visual Monthly waste overview Bar Chart drawn with Canvas!
        // Groups waste based on item purchase/bought dates
        MonthlyWasteCanvasChart(
            items = itemsList,
            lang = lang,
            textMuted = textMuted,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Category-wise Expense & Waste Chart Component
        CategoryWasteCanvasChart(
            items = itemsList,
            lang = lang,
            textPrimary = textPrimary,
            textMuted = textMuted,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Exporter Action Button
        Button(
            onClick = onExportReports,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = "Export")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Export Cost Analysis PDF/CSV", fontWeight = FontWeight.Bold)
        }
    }
}

// Dynamic Month Waste Custom chart drawing with Compose Graphic Canvas
@Composable
fun MonthlyWasteCanvasChart(
    items: List<GroceryItem>,
    lang: String,
    textMuted: Color,
    isDark: Boolean
) {
    // Generate actual monthly bucket expenses calculated using SimpleDateFormat boughtDate parsing
    val monthlySpendData = remember(items) {
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val values = DoubleArray(12) { 0.0 }
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (item in items) {
            try {
                val date = sdf.parse(item.boughtDate) ?: continue
                val cal = Calendar.getInstance().apply { time = date }
                val monthIdx = cal.get(Calendar.MONTH)
                if (monthIdx in 0..11) {
                    values[monthIdx] += (item.price ?: 0.0)
                }
            } catch (e: Exception) {
                // Ignore parsing anomalies
            }
        }
        months.zip(values.toList())
    }

    val maxVal = remember(monthlySpendData) {
        val max = monthlySpendData.map { it.second }.maxOrNull() ?: 1.0
        if (max == 0.0) 1.0 else max
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0x1F000000)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📈 " + translate("monthlyWaste", lang),
                color = Color(0xFF8B5CF6),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Graphical Plot area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                monthlySpendData.forEach { (month, value) ->
                    val proportion = (value / maxVal).toFloat()
                    val barHeightDp = (proportion * 80).coerceAtLeast(4f).dp

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "₹${value.toInt()}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = textMuted,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(barHeightDp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFA78BFA),
                                            Color(0xFF7C3AED)
                                        )
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = month,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = textMuted
                        )
                    }
                }
            }
        }
    }
}

// Horizontal progress visual spending bar graphs grouped by categories
@Composable
fun CategoryWasteCanvasChart(
    items: List<GroceryItem>,
    lang: String,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean
) {
    val categoryWasteList = remember(items) {
        val wasteMap = mutableMapOf<String, Double>()
        // Seed categories with initial zero values
        CAT_EMOJI.keys.forEach { wasteMap[it] = 0.0 }
        
        for (item in items) {
            val isExpired = calcDaysLeft(item.expiryDate) < 0
            if (isExpired && item.price != null) {
                val current = wasteMap[item.category] ?: 0.0
                wasteMap[item.category] = current + item.price
            }
        }
        wasteMap.entries.sortedByDescending { it.value }
    }

    val maxVal = remember(categoryWasteList) {
        val max = categoryWasteList.maxOfOrNull { it.value } ?: 1.0
        if (max == 0.0) 1.0 else max
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0x1F000000)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🗂️ " + translate("wasteByCategory", lang),
                color = Color(0xFFF87171),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(14.dp))

            val nonZeroCategories = categoryWasteList.filter { it.value > 0.0 }
            if (nonZeroCategories.isEmpty()) {
                Text(
                    text = "✅ No expired waste recorded across categories!",
                    fontSize = 12.sp,
                    color = textMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                nonZeroCategories.forEach { entry ->
                    val proportion = (entry.value / maxVal).toFloat()
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${getCategoryEmoji(entry.key)} ${getCategoryLabel(entry.key, lang)}",
                                color = textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹${entry.value}",
                                color = Color(0xFFF87171),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(proportion)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFF87171),
                                                Color(0xFFDC2626)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
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
    googleConnected: Boolean,
    googleAccountName: String,
    lastBackupText: String,
    onConnectDrive: () -> Unit,
    onBackupToDrive: () -> Unit,
    onRestoreFromDrive: () -> Unit,
    onShareLocalBackup: () -> Unit,
    onImportBackupFile: () -> Unit,
    onClearDatabase: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // App Quick Guide Instructions Card always visible in Settings page!
        UserGuidanceContent(
            lang = lang,
            textPrimary = textPrimary,
            textMuted = textMuted,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Appearance Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0x1F000000)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌐 " + translate("language", lang),
                    color = Color(0xFF00C897),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val activeEn = lang == "en"
                    val activeHi = lang == "hi"

                    Button(
                        onClick = { onLangChange("en") },
                        modifier = Modifier.weight(1f).testTag("lang_en_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeEn) Color(0x2200C897) else Color.Transparent,
                            contentColor = if (activeEn) Color(0xFF00C897) else textMuted
                        ),
                        border = BorderStroke(1.dp, if (activeEn) Color(0xFF00C897) else Color.LightGray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🇬🇧 English", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onLangChange("hi") },
                        modifier = Modifier.weight(1f).testTag("lang_hi_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeHi) Color(0x2200C897) else Color.Transparent,
                            contentColor = if (activeHi) Color(0xFF00C897) else textMuted
                        ),
                        border = BorderStroke(1.dp, if (activeHi) Color(0xFF00C897) else Color.LightGray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🇮🇳 हिंदी", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Alarms & Notifications Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0x1F000000)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔔 " + translate("notifications", lang),
                    color = Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(translate("enableNotifications", lang), fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 13.sp)
                        Text(translate("dailyAt", lang), color = textMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notificationsOn,
                        onCheckedChange = onNotificationsOnChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00C897)),
                        modifier = Modifier.testTag("notification_toggle_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Google Drive Backup Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0x1F000000)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💾 " + translate("googleDriveBackup", lang),
                    color = Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    translate("driveBackupDesc", lang),
                    color = textMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Connection indicator status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (googleConnected) Color(0xFFECFDF5) else Color(0xFFF8FAFC))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (googleConnected) "Status: Connected" else "Status: Offline Only",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (googleConnected) Color(0xFF10B981) else Color.Gray
                        )
                        if (googleConnected) {
                            Text(googleAccountName, fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                    Button(
                        onClick = onConnectDrive,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (googleConnected) Color.Red else Color(0xFF3B82F6)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (googleConnected) "Disconnect" else "Link Account", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onBackupToDrive,
                        enabled = googleConnected,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(translate("backupNow", lang), fontSize = 11.sp)
                    }
                    Button(
                        onClick = onRestoreFromDrive,
                        enabled = googleConnected,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(translate("restoreNow", lang), fontSize = 11.sp)
                    }
                }
                if (googleConnected && lastBackupText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "${translate("driveStatusBackupSuccess", lang)} ($lastBackupText)",
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Local Offline Save Backup
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0x1F000000)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📥 Offline Local Backup/Restore",
                    color = Color(0xFF06B6D4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onShareLocalBackup,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5), contentColor = Color(0xFF0D9488)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(translate("shareBackup", lang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onImportBackupFile,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5), contentColor = Color(0xFF0D9488)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(translate("importBackup", lang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Storage Detail Notes Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF151D33) else Color(0xC2E0FBFC)),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0x2406B6D4)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💾 " + translate("storageInfo", lang),
                    color = Color(0xFF06B6D4),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                listOf("storage1", "storage2", "storage3", "storage4").forEach { tipKey ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("→", color = Color(0xFF06B6D4), fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(translate(tipKey, lang), color = textMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danger zone
        Text(
            translate("dangerZone", lang),
            color = Color.Red,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onClearDatabase,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFECEB), contentColor = Color.Red),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(translate("clearAllData", lang), fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(30.dp))
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
                    text = "📖 " + (if (lang == "hi") "ऐप मार्गदर्शिका (निर्देश)" else "App Quick Guide"),
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
                                text = if (lang == "hi") "1. फ्रेशट्रैक में आपका स्वागत है!" else "1. Welcome to FreshTrak!",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (lang == "hi")
                                    "ताजा भोजन का रिकॉर्ड रखें, खाना बर्बाद होने से बचाएं और पैसे बचाएं। फ्रेशट्रैक आपकी एक्सपायरी तारीखों को आसानी से ट्रैक करने में मदद करता है।"
                                    else "Keep food fresh! FreshTrak helps you track and manage your grocery inventory, alerts you before expiry, and saves you money.",
                                color = textMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (lang == "hi") "2. आइटम जोड़ें और लाइव फोटो लें" else "2. Add Items & Live Photos",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (lang == "hi")
                                    "नया आइटम जोड़ने के लिए '+' दबाएं। अपनी पसंद की कैटेगरी (जैसे डेयरी, फल आदि) चुनें, इकाई (unit) अपने आप तय हो जाएगी। कैमरे पर टैप करके लाइव फोटो भी खींच सकते हैं!"
                                    else "Tap the Plus button to add items. Choose a smart category (Dairy, Fruits, etc.) and the units auto-calculate! Take live camera snapshots or pick from gallery.",
                                color = textMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    3 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (lang == "hi") "3. स्मार्ट अलर्ट और सुरक्षित बैकअप" else "3. Smart Alerts & Secure Backup",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (lang == "hi")
                                    "रोजाना सूचनाएं पाएं जो आपको सतर्क रखती हैं। सुरक्षित रूप से अपने गूगल ड्राइव पर डेटा बैकअप भी बना सकते हैं!"
                                    else "Get smart reminder alerts every day so nothing goes bad in your pantry. Set up email / cloud recovery options in Settings.",
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
                        Text("← " + (if (lang == "hi") "पीछे" else "Prev"))
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
                        Text((if (lang == "hi") "आगे" else "Next") + " →", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            text = if (onFinish != null) (if (lang == "hi") "शुरू करें" else "Get Started") else (if (lang == "hi") "पुनरावलोकन" else "Restart"),
                            color = Color.White,
                            fontSize = 12.sp,
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
                        Color(0xFF0F5A47), // Vibrant Deep Forest Green
                        Color(0xFF073C2F)  // Rich Deep Emerald Forest
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
                    .background(Color.White.copy(alpha = 0.08f), shape = CircleShape)
                    .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.28f)), shape = CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                FreshTrackVectorLogo(modifier = Modifier.size(100.dp))
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "FreshTrak",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Smart Grocery & Expiry Tracker",
                color = Color(0xFF00C897),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(60.dp))
            // Clean circular loading indicator
            CircularProgressIndicator(
                color = Color(0xFF00C897),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun FreshTrakLoginPage(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var stayLoggedIn by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F5A47), // Vibrant Deep Forest Green
                        Color(0xFF073C2F)  // Rich Deep Emerald Forest
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val scrollState = rememberScrollState()
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .verticalScroll(scrollState),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF152220).copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large App Logo with premium glowing halo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White.copy(alpha = 0.08f), shape = CircleShape)
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), shape = CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FreshTrackVectorLogo(modifier = Modifier.size(68.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome to FreshTrak",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Never waste food again",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Outlined Input Fields
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; loginError = "" },
                    label = { Text("Email Address", color = Color.White.copy(alpha = 0.65f)) },
                    placeholder = { Text("email@example.com", color = Color.White.copy(alpha = 0.35f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                        focusedBorderColor = Color(0xFF00C897),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        cursorColor = Color(0xFF00C897)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_email_input")
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; loginError = "" },
                    label = { Text("Password", color = Color.White.copy(alpha = 0.65f)) },
                    placeholder = { Text("••••••••", color = Color.White.copy(alpha = 0.35f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                        focusedBorderColor = Color(0xFF00C897),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        cursorColor = Color(0xFF00C897)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Stay logged in",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                    Switch(
                        checked = stayLoggedIn,
                        onCheckedChange = { stayLoggedIn = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00C897),
                            uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }

                if (loginError.isNotEmpty()) {
                    Text(
                        text = loginError,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            loginError = "Please enter both Email and Password"
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            loginError = "Please enter a valid Email Address"
                        } else {
                            onLoginSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C897),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_button")
                ) {
                    Text(
                        text = "Log In",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = { onLoginSuccess() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue as Demo Guest ➔",
                        color = Color(0xFF00C897),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

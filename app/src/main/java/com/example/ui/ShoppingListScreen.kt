package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.data.local.ShoppingDao
import com.example.data.local.GroceryDao
import com.example.data.model.ShoppingList
import com.example.data.model.ShoppingItem
import com.example.data.model.GroceryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ShoppingListScreen(
    shoppingDao: ShoppingDao,
    groceryDao: GroceryDao,
    lang: String,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Observe all shopping lists reactive flow
    val allLists by shoppingDao.getAllShoppingListsFlow().collectAsState(initial = emptyList())

    // Active active list focus
    var activeListId by remember { mutableStateOf<Long?>(null) }

    // List Creation Dialog States
    var showCreateDialog by remember { mutableStateOf(false) }

    // Interactive Long Press overlay states
    var activeListActionsMenu by remember { mutableStateOf<ShoppingList?>(null) }
    var showRenameDialog by remember { mutableStateOf<ShoppingList?>(null) }
    var listNameToRename by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf<ShoppingList?>(null) }

    // Expiry Tracker Migration Dialog
    var itemToMigrate by remember { mutableStateOf<ShoppingItem?>(null) }

    val pistaGreenMain = Color(0xFF7CAF6D)
    val mintSoftColor = Color(0xFFECFDF5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (activeListId == null) {
                // ── 1. SHOPPING LIST DASHBOARD SCREEN ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(id = com.example.R.string.shopping_list_title),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary
                        )
                        Text(
                            text = "Premium Grocery Notepad",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = pistaGreenMain
                        )
                    }

                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier
                            .background(pistaGreenMain.copy(alpha = 0.15f), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = pistaGreenMain
                        )
                    }
                }

                if (allLists.isEmpty()) {
                    // Empty state visual dashboard
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(130.dp)
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFECFDF5), CircleShape)
                                .border(2.dp, pistaGreenMain.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Box(modifier = Modifier.offset(x = (-20).dp, y = (-15).dp)) {
                                Text("🥑", fontSize = 42.sp)
                            }
                            Box(modifier = Modifier.offset(x = 20.dp, y = (-10).dp)) {
                                Text("🍒", fontSize = 36.sp)
                            }
                            Box(modifier = Modifier.offset(x = 0.dp, y = 18.dp)) {
                                Text("🥦", fontSize = 46.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(id = com.example.R.string.shopping_list_empty_dashboard_title),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = com.example.R.string.shopping_list_empty_hint),
                            fontSize = 13.sp,
                            color = textMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = pistaGreenMain),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = com.example.R.string.shopping_list_empty_dashboard_btn),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Shopping list dashboard items feed
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 96.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allLists, key = { it.id }) { list ->
                            ShoppingListCard(
                                list = list,
                                shoppingDao = shoppingDao,
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textMuted = textMuted,
                                onClick = { activeListId = list.id },
                                onLongClick = { activeListActionsMenu = list }
                            )
                        }
                    }
                }

                // Create List Floating Action Button on Dashboard Screen
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = stringResource(id = com.example.R.string.shopping_list_create_btn),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.White) },
                    onClick = { showCreateDialog = true },
                    containerColor = pistaGreenMain,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(24.dp)
                        .testTag("create_shopping_list_fab"),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                )

            } else {
                // ── 2. PREMIUM GROCERY NOTEPAD DETAIL SCREEN ──
                val currentListId = activeListId!!
                val activeList = allLists.find { it.id == currentListId }

                if (activeList != null) {
                    val listItems by shoppingDao.getShoppingItemsForListFlow(currentListId).collectAsState(initial = emptyList())

                    var newItemName by remember { mutableStateOf("") }
                    var newItemQty by remember { mutableStateOf("1") }
                    var selectedUnit by remember { mutableStateOf("Pcs") }
                    var showShareSheet by remember { mutableStateOf(false) }

                    // Unit selector options
                    val units = listOf("Kg", "Gram", "Litre", "ML", "Pack", "Pcs", "Bottle", "Box", "Dozen")
                    var isUnitExpanded by remember { mutableStateOf(false) }

                    // Header Notepad Block
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { activeListId = null },
                            modifier = Modifier
                                .background(pistaGreenMain.copy(alpha = 0.12f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = pistaGreenMain
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Interactive Editable Title: clicking allows direct rename
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showRenameDialog = activeList
                                    listNameToRename = activeList.title
                                }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = getCoverEmoji(activeList.coverImagePath) + "  " + activeList.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Edit, contentDescription = "Edit Title", modifier = Modifier.size(13.dp), tint = pistaGreenMain)
                            }
                            Text(
                                text = if (activeList.isCompleted) "Completed" else "Active Notepad List",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeList.isCompleted) pistaGreenMain else textMuted
                            )
                        }

                        // Header Actions
                        IconButton(
                            onClick = { showShareSheet = true },
                            modifier = Modifier
                                .background(pistaGreenMain.copy(alpha = 0.12f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = pistaGreenMain)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { activeListActionsMenu = activeList },
                            modifier = Modifier
                                .background(pistaGreenMain.copy(alpha = 0.12f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = pistaGreenMain)
                        }
                    }

                    // Compact Add-Item Card Panel at the Top
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFECFDF5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Item name field
                            OutlinedTextField(
                                value = newItemName,
                                onValueChange = { newItemName = it },
                                modifier = Modifier.weight(1.8f),
                                placeholder = { Text(stringResource(id = com.example.R.string.shopping_list_item_name_placeholder), fontSize = 12.sp) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = pistaGreenMain,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC),
                                    unfocusedContainerColor = if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // Quantity field
                            OutlinedTextField(
                                value = newItemQty,
                                onValueChange = { newItemQty = it },
                                modifier = Modifier.width(54.dp),
                                placeholder = { Text("1", fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, textAlign = TextAlign.Center),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = pistaGreenMain,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC),
                                    unfocusedContainerColor = if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // Unit Selector dropdown
                            Box(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC))
                                    .clickable { isUnitExpanded = true }
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = selectedUnit,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        maxLines = 1
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Units", modifier = Modifier.size(12.dp), tint = textMuted)
                                }

                                DropdownMenu(
                                    expanded = isUnitExpanded,
                                    onDismissRequest = { isUnitExpanded = false }
                                ) {
                                    units.forEach { u ->
                                        DropdownMenuItem(
                                            text = { Text(u, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                            onClick = {
                                                selectedUnit = u
                                                isUnitExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Circular organic green add button
                            IconButton(
                                onClick = {
                                    val nameTrimmed = newItemName.trim()
                                    if (nameTrimmed.isNotEmpty()) {
                                        val qtyInt = newItemQty.toIntOrNull() ?: 1
                                        scope.launch(Dispatchers.IO) {
                                            val item = ShoppingItem(
                                                listId = currentListId,
                                                itemName = nameTrimmed,
                                                quantity = qtyInt,
                                                unit = selectedUnit,
                                                price = 0.0
                                            )
                                            shoppingDao.insertShoppingItem(item)
                                            updateListTotalAndModified(currentListId, shoppingDao)
                                            withContext(Dispatchers.Main) {
                                                newItemName = ""
                                                newItemQty = "1"
                                                focusManager.clearFocus()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(pistaGreenMain, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color.White)
                            }
                        }
                    }

                    // Checklist area
                    Box(modifier = Modifier.weight(1f)) {
                        if (listItems.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = com.example.R.string.shopping_list_empty_hint),
                                    fontSize = 13.sp,
                                    color = textMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 140.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(listItems, key = { it.id }) { item ->
                                    ShoppingItemRow(
                                        item = item,
                                        shoppingDao = shoppingDao,
                                        textPrimary = textPrimary,
                                        textMuted = textMuted,
                                        isDark = isDark,
                                        scope = scope,
                                        listId = currentListId,
                                        onSwipeRight = { itemToMigrate = item },
                                        onSwipeLeft = {
                                            scope.launch(Dispatchers.IO) {
                                                shoppingDao.deleteShoppingItem(item)
                                                updateListTotalAndModified(currentListId, shoppingDao)
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "Item deleted!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        // ── 3. STICKY TOTAL EXPENSE CALCULATOR & ACTION SUMMARY ──
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC)
                                        ),
                                        startY = 0f,
                                        endY = 40f
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            val totalItemsCount = listItems.size
                            val purchasedItemsCount = listItems.count { it.isChecked }
                            val pendingItemsCount = totalItemsCount - purchasedItemsCount
                            val totalSumPrice = listItems.sumOf { it.price * it.quantity }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = pistaGreenMain),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = stringResource(id = com.example.R.string.shopping_list_summary_total_items, totalItemsCount),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.95f)
                                            )
                                            Text(
                                                text = stringResource(id = com.example.R.string.shopping_list_summary_purchased, purchasedItemsCount),
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.95f)
                                            )
                                            Text(
                                                text = stringResource(id = com.example.R.string.shopping_list_summary_pending, pendingItemsCount),
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.95f)
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = stringResource(id = com.example.R.string.shopping_list_summary_total_expense),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                            Text(
                                                text = "₹${String.format("%.2f", totalSumPrice)}",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Complete List Checkout: copies checked items to inventory and archives list
                                    Button(
                                        onClick = {
                                            if (listItems.isEmpty()) return@Button
                                            scope.launch(Dispatchers.IO) {
                                                // Mark parent list as completed
                                                val completedList = activeList.copy(
                                                    isCompleted = true,
                                                    totalExpense = totalSumPrice,
                                                    completedAt = System.currentTimeMillis(),
                                                    modifiedAt = System.currentTimeMillis()
                                                )
                                                shoppingDao.updateShoppingList(completedList)

                                                // Feed Expense Tracker / Analytics by adding items in Inventory table
                                                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                                                val cal = Calendar.getInstance()
                                                cal.add(Calendar.DAY_OF_YEAR, 7) // default 7 day expiry
                                                val expStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

                                                listItems.filter { it.isChecked }.forEach { item ->
                                                    val groceryItem = GroceryItem(
                                                        name = item.itemName,
                                                        category = "others",
                                                        quantity = item.quantity.toDouble(),
                                                        unit = item.unit.lowercase(),
                                                        price = item.price,
                                                        boughtDate = todayStr,
                                                        expiryDate = expStr,
                                                        notes = "Purchased from list: ${activeList.title}"
                                                    )
                                                    groceryDao.insertItem(groceryItem)
                                                }

                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(com.example.R.string.shopping_list_complete_success),
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    activeListId = null
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(10.dp)
                                    ) {
                                        Text(
                                            text = stringResource(id = com.example.R.string.shopping_list_complete_btn),
                                            color = pistaGreenMain,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Multimodal Share Dialog Selector
                    if (showShareSheet) {
                        Dialog(onDismissRequest = { showShareSheet = false }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(id = com.example.R.string.shopping_list_share_title),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = textPrimary
                                    )

                                    Text(
                                        text = stringResource(id = com.example.R.string.shopping_list_share_desc),
                                        fontSize = 13.sp,
                                        color = textMuted,
                                        textAlign = TextAlign.Center
                                    )

                                    // Share as Text button
                                    Button(
                                        onClick = {
                                            shareListAsText(context, activeList.title, listItems)
                                            showShareSheet = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = pistaGreenMain),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.List, contentDescription = "Text", tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(id = com.example.R.string.shopping_list_share_text_btn), fontWeight = FontWeight.Bold)
                                    }

                                    // Share as Image button
                                    Button(
                                        onClick = {
                                            shareListAsImage(context, activeList.title, activeList.coverImagePath, listItems)
                                            showShareSheet = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399)), // mint green accent
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Image", tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(id = com.example.R.string.shopping_list_share_image_btn), fontWeight = FontWeight.Bold)
                                    }

                                    TextButton(onClick = { showShareSheet = false }) {
                                        Text(stringResource(id = com.example.R.string.shopping_list_cancel), color = textMuted, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── 4. DIALOGS & ACTION MODALS OVERLAYS ──

        // Master List Creation Dialog
        if (showCreateDialog) {
            var selectedCoverToCreate by remember { mutableStateOf("grocery") }
            var enteredTitle by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = {
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_dialog_create_title),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = enteredTitle,
                            onValueChange = { enteredTitle = it },
                            label = { Text(stringResource(id = com.example.R.string.shopping_list_dialog_enter_name)) },
                            placeholder = { Text("e.g. Monthly Grocery") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = pistaGreenMain),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column {
                            Text(
                                text = stringResource(id = com.example.R.string.shopping_list_select_cover),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Cover selector options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val covers = listOf(
                                    Pair("grocery", "🥑"),
                                    Pair("party", "🎉"),
                                    Pair("monthly", "📅"),
                                    Pair("fruits", "🍎"),
                                    Pair("dairy", "🥛")
                                )
                                covers.forEach { (type, emoji) ->
                                    val isSel = selectedCoverToCreate == type
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSel) pistaGreenMain.copy(alpha = 0.15f) else (if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSel) pistaGreenMain else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedCoverToCreate = type }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(emoji, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            val coverLabel = when (type) {
                                                "grocery" -> "Groc"
                                                "party" -> "Party"
                                                "monthly" -> "Month"
                                                "fruits" -> "Fruit"
                                                "dairy" -> "Dairy"
                                                else -> ""
                                            }
                                            Text(coverLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val finalTitle = enteredTitle.trim().ifEmpty {
                                "Shopping List ${System.currentTimeMillis() % 1000}"
                            }
                            scope.launch(Dispatchers.IO) {
                                val newList = ShoppingList(
                                    title = finalTitle,
                                    coverImagePath = selectedCoverToCreate,
                                    createdAt = System.currentTimeMillis(),
                                    modifiedAt = System.currentTimeMillis()
                                )
                                val insertedId = shoppingDao.insertShoppingList(newList)
                                withContext(Dispatchers.Main) {
                                    activeListId = insertedId
                                    showCreateDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = pistaGreenMain)
                    ) {
                        Text(stringResource(id = com.example.R.string.shopping_list_dialog_ok), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text(stringResource(id = com.example.R.string.shopping_list_dialog_cancel), color = textMuted)
                    }
                }
            )
        }

        // Long Press Bottom Sheet overlay dialog
        if (activeListActionsMenu != null) {
            val targetedList = activeListActionsMenu!!
            Dialog(onDismissRequest = { activeListActionsMenu = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = targetedList.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Inline Cover Quick Picker
                        Column {
                            Text(
                                text = "Cover Category",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val covers = listOf("grocery", "party", "monthly", "fruits", "dairy")
                                val emojis = listOf("🥑", "🎉", "📅", "🍎", "🥛")
                                covers.forEachIndexed { i, type ->
                                    val isSelected = targetedList.coverImagePath == type
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSelected) pistaGreenMain.copy(alpha = 0.2f) else (if (isDark) Color(0xFF0F1320) else Color(0xFFF1F5F9)),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSelected) pistaGreenMain else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                scope.launch(Dispatchers.IO) {
                                                    shoppingDao.updateShoppingList(targetedList.copy(coverImagePath = type, modifiedAt = System.currentTimeMillis()))
                                                    withContext(Dispatchers.Main) {
                                                        activeListActionsMenu = shoppingDao.getShoppingListById(targetedList.id)
                                                    }
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emojis[i], fontSize = 18.sp)
                                    }
                                }
                            }
                        }

                        Divider(color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))

                        // Actions Checklist
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    listNameToRename = targetedList.title
                                    showRenameDialog = targetedList
                                    activeListActionsMenu = null
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename", tint = pistaGreenMain, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(id = com.example.R.string.shopping_list_rename_btn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val listToDuplicate = targetedList
                                    scope.launch(Dispatchers.IO) {
                                        val uniqueTitle = "Copy of ${listToDuplicate.title}"
                                        val duplicatedParent = ShoppingList(
                                            title = uniqueTitle,
                                            coverImagePath = listToDuplicate.coverImagePath,
                                            totalExpense = listToDuplicate.totalExpense,
                                            createdAt = System.currentTimeMillis(),
                                            modifiedAt = System.currentTimeMillis()
                                        )
                                        val parentId = shoppingDao.insertShoppingList(duplicatedParent)
                                        val itemsToDuplicate = shoppingDao.getShoppingItemsForList(listToDuplicate.id)
                                        itemsToDuplicate.forEach { item ->
                                            shoppingDao.insertShoppingItem(item.copy(id = 0, listId = parentId, createdAt = System.currentTimeMillis()))
                                        }
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "List Duplicated!", Toast.LENGTH_SHORT).show()
                                            activeListActionsMenu = null
                                        }
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Duplicate", tint = pistaGreenMain, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(id = com.example.R.string.shopping_list_duplicate_btn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeleteConfirmDialog = targetedList
                                    activeListActionsMenu = null
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(id = com.example.R.string.shopping_list_delete_btn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }

                        Button(
                            onClick = { activeListActionsMenu = null },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(id = com.example.R.string.shopping_list_cancel), color = textPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Rename dialog popup
        if (showRenameDialog != null) {
            val listToRename = showRenameDialog!!
            AlertDialog(
                onDismissRequest = { showRenameDialog = null },
                title = {
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_dialog_rename_title),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                },
                text = {
                    OutlinedTextField(
                        value = listNameToRename,
                        onValueChange = { listNameToRename = it },
                        label = { Text(stringResource(id = com.example.R.string.shopping_list_dialog_enter_name)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = pistaGreenMain),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newTitle = listNameToRename.trim()
                            if (newTitle.isNotEmpty()) {
                                scope.launch(Dispatchers.IO) {
                                    shoppingDao.updateShoppingList(listToRename.copy(title = newTitle, modifiedAt = System.currentTimeMillis()))
                                    withContext(Dispatchers.Main) {
                                        showRenameDialog = null
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = pistaGreenMain)
                    ) {
                        Text(stringResource(id = com.example.R.string.shopping_list_dialog_ok), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = null }) {
                        Text(stringResource(id = com.example.R.string.shopping_list_dialog_cancel), color = textMuted)
                    }
                }
            )
        }

        // Delete Confirm Dialog popup
        if (showDeleteConfirmDialog != null) {
            val listToDelete = showDeleteConfirmDialog!!
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = null },
                title = {
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_delete_btn),
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_confirm_delete),
                        color = textPrimary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                shoppingDao.deleteShoppingList(listToDelete)
                                withContext(Dispatchers.Main) {
                                    if (activeListId == listToDelete.id) {
                                        activeListId = null
                                    }
                                    showDeleteConfirmDialog = null
                                    Toast.makeText(context, "List Deleted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(stringResource(id = com.example.R.string.shopping_list_dialog_ok), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = null }) {
                        Text(stringResource(id = com.example.R.string.shopping_list_dialog_cancel), color = textMuted)
                    }
                }
            )
        }

        // Swipe Right Inventory Migration dialog overlay
        if (itemToMigrate != null) {
            val currentItem = itemToMigrate!!
            MigrateDialog(
                itemName = currentItem.itemName,
                lang = lang,
                isDark = isDark,
                textPrimary = textPrimary,
                textMuted = textMuted,
                onDismiss = { itemToMigrate = null },
                onMigrate = { selectedCat, expDate ->
                    scope.launch(Dispatchers.IO) {
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        val groceryItem = GroceryItem(
                            name = currentItem.itemName,
                            category = selectedCat,
                            quantity = currentItem.quantity.toDouble(),
                            unit = currentItem.unit.lowercase(),
                            price = currentItem.price,
                            boughtDate = todayStr,
                            expiryDate = expDate,
                            notes = "Migrated from Shopping List"
                        )
                        groceryDao.insertItem(groceryItem)

                        // Remove from original Shopping List parent child database
                        shoppingDao.deleteShoppingItem(currentItem)
                        updateListTotalAndModified(currentItem.listId, shoppingDao)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, context.getString(com.example.R.string.shopping_list_migrate_success), Toast.LENGTH_SHORT).show()
                            itemToMigrate = null
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingListCard(
    list: ShoppingList,
    shoppingDao: ShoppingDao,
    isDark: Boolean,
    textPrimary: Color,
    textMuted: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val itemsFlow = remember(list.id) { shoppingDao.getShoppingItemsForListFlow(list.id) }
    val items by itemsFlow.collectAsState(initial = emptyList())

    val totalCount = items.size
    val purchasedCount = items.count { it.isChecked }
    val formattedPrice = String.format("%.2f", list.totalExpense)

    // Render 3 preview items: e.g. Milk • Rice • Oil
    val itemsPreviewText = if (items.isNotEmpty()) {
        items.take(3).joinToString(" • ") { it.itemName }
    } else {
        "No items"
    }

    val coverEmoji = getCoverEmoji(list.coverImagePath)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("shopping_list_card_${list.id}"),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13192B) else Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFECFDF5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Cover Illustration
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        if (isDark) Color(0xFF1E293B) else Color(0xFFECFDF5),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(coverEmoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = list.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "₹$formattedPrice",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color(0xFF7CAF6D)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_progress_label, purchasedCount, totalCount),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )

                    // Updated Date timestamp label
                    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val updatedTodayStr = stringResource(id = com.example.R.string.shopping_list_updated_today, formatter.format(Date(list.modifiedAt)))
                    Text(
                        text = updatedTodayStr,
                        fontSize = 10.sp,
                        color = textMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal item preview line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDark) Color(0xFF0F1320) else Color(0xFFF0FDF4),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Preview",
                            tint = Color(0xFF7CAF6D).copy(alpha = 0.8f),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = itemsPreviewText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF047857),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    shoppingDao: ShoppingDao,
    textPrimary: Color,
    textMuted: Color,
    isDark: Boolean,
    scope: CoroutineScope,
    listId: Long,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = 180f

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(),
        label = "Swipe Offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(item.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            onSwipeRight()
                        } else if (offsetX < -swipeThreshold) {
                            onSwipeLeft()
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-240f, 240f)
                    }
                )
            }
            .testTag("shopping_item_row_${item.id}")
    ) {
        // Drag swipe backgrounds
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when {
                        offsetX > 15f -> Color(0xFF7CAF6D) // Green for inventory migrates
                        offsetX < -15f -> Color(0xFFEF4444) // Red for deletes
                        else -> Color.Transparent
                    }
                )
                .padding(horizontal = 16.dp),
            contentAlignment = if (offsetX > 0f) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            if (offsetX > 15f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Migrate", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(id = com.example.R.string.shopping_list_migrate_swipe), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else if (offsetX < -15f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(id = com.example.R.string.shopping_list_delete_swipe), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                }
            }
        }

        // Active Forefront Row Card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) },
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF13192B) else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { checked ->
                        scope.launch(Dispatchers.IO) {
                            shoppingDao.updateShoppingItem(item.copy(isChecked = checked))
                            updateListTotalAndModified(listId, shoppingDao)
                        }
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF7CAF6D))
                )

                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = item.itemName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isChecked) textPrimary.copy(alpha = 0.45f) else textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = if (item.isChecked) LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough) else LocalTextStyle.current
                    )
                    Text(
                        text = "${item.quantity} ${item.unit}",
                        fontSize = 11.sp,
                        color = textMuted
                    )
                }

                // Price Input box (editable price, instantly updates Room database)
                var priceStr by remember(item.id) { mutableStateOf(if (item.price == 0.0) "" else item.price.toString()) }

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                            priceStr = newValue
                            val parsedPrice = newValue.toDoubleOrNull() ?: 0.0
                            scope.launch(Dispatchers.IO) {
                                shoppingDao.updateShoppingItem(item.copy(price = parsedPrice, updatedAt = System.currentTimeMillis()))
                                updateListTotalAndModified(listId, shoppingDao)
                            }
                        }
                    },
                    modifier = Modifier.width(82.dp),
                    placeholder = { Text("₹0", fontSize = 11.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End),
                    prefix = { Text("₹", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF7CAF6D)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7CAF6D),
                        unfocusedBorderColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                        focusedContainerColor = if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC),
                        unfocusedContainerColor = if (isDark) Color(0xFF0F1320) else Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            shoppingDao.deleteShoppingItem(item)
                            updateListTotalAndModified(listId, shoppingDao)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun MigrateDialog(
    itemName: String,
    lang: String,
    isDark: Boolean,
    textPrimary: Color,
    textMuted: Color,
    onDismiss: () -> Unit,
    onMigrate: (category: String, expiryDate: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("others") }
    var expiryDate by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf("fruits_veg", "dairy_eggs", "bakery_bread", "meat_seafood", "pantry_grains", "beverages", "others")
    val pistaGreenMain = Color(0xFF7CAF6D)

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 7) // 7 days in future default
        expiryDate = sdf.format(cal.time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = com.example.R.string.shopping_list_migrate_title),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = com.example.R.string.shopping_list_item_label, itemName),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )

                // Category selector dropdown card
                Column {
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_migrate_category),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { isDropdownExpanded = true }
                            .padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val emoji = com.example.ui.CAT_EMOJI[selectedCategory] ?: "📦"
                            val localizedName = com.example.ui.CAT_NAMES_I18N[lang]?.get(selectedCategory) ?: selectedCategory
                            Text(
                                text = "$emoji $localizedName",
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = textPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        val emoji = com.example.ui.CAT_EMOJI[cat] ?: "📦"
                                        val localizedName = com.example.ui.CAT_NAMES_I18N[lang]?.get(cat) ?: cat
                                        Text("$emoji $localizedName")
                                    },
                                    onClick = {
                                        selectedCategory = cat
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Expiry Picker input field
                Column {
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_migrate_expiry),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = pistaGreenMain),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = stringResource(id = com.example.R.string.shopping_list_format_help),
                        fontSize = 10.sp,
                        color = textMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onMigrate(selectedCategory, expiryDate)
                },
                colors = ButtonDefaults.buttonColors(containerColor = pistaGreenMain)
            ) {
                Text(
                    text = stringResource(id = com.example.R.string.shopping_list_migrate_btn),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = com.example.R.string.shopping_list_cancel),
                    color = textMuted
                )
            }
        }
    )
}

// Helper: updates parent list total sum and modified timestamp
suspend fun updateListTotalAndModified(listId: Long, dao: ShoppingDao) {
    val items = dao.getShoppingItemsForList(listId)
    val totalExpense = items.sumOf { it.price * it.quantity }
    val list = dao.getShoppingListById(listId)
    if (list != null) {
        dao.updateShoppingList(
            list.copy(
                totalExpense = totalExpense,
                modifiedAt = System.currentTimeMillis()
            )
        )
    }
}

fun getCoverEmoji(path: String?): String {
    return when (path) {
        "grocery" -> "🥑"
        "party" -> "🎉"
        "monthly" -> "📅"
        "fruits" -> "🍎"
        "dairy" -> "🥛"
        else -> "🥑"
    }
}

// ── UTILS: MULTIMODAL SHARING SYSTEM ──

fun shareListAsText(context: Context, title: String, items: List<ShoppingItem>) {
    val unCheckedItems = items.filter { !it.isChecked }
    if (unCheckedItems.isEmpty()) {
        Toast.makeText(context, context.getString(com.example.R.string.shopping_list_no_items_share), Toast.LENGTH_SHORT).show()
        return
    }

    val stringBuilder = StringBuilder()
    stringBuilder.append("📋 $title\n\n")
    unCheckedItems.forEachIndexed { i, item ->
        stringBuilder.append("${i + 1}. [ ] ${item.itemName} - ${item.quantity} ${item.unit} - ₹${item.price}\n")
    }

    val total = unCheckedItems.sumOf { it.price * it.quantity }
    stringBuilder.append("\nTotal Expense: ₹$total\n")
    stringBuilder.append(context.getString(com.example.R.string.shopping_list_share_footer))

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
    }
    context.startActivity(Intent.createChooser(intent, context.getString(com.example.R.string.shopping_list_share_title)))
}

fun shareListAsImage(context: Context, title: String, coverType: String?, items: List<ShoppingItem>) {
    val unCheckedItems = items.filter { !it.isChecked }
    if (unCheckedItems.isEmpty()) {
        Toast.makeText(context, context.getString(com.example.R.string.shopping_list_no_items_share), Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val bitmap = generateShoppingListBitmap(context, title, coverType, unCheckedItems)
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/shopping_list.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val imagePath = File(context.cacheDir, "images/shopping_list.png")
        val imageUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imagePath)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(com.example.R.string.shopping_list_share_image_btn)))
    } catch (e: Exception) {
        Toast.makeText(context, "Sharing failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}

fun generateShoppingListBitmap(context: Context, title: String, coverType: String?, items: List<ShoppingItem>): Bitmap {
    val width = 600
    val rowHeight = 45
    val headerHeight = 220
    val footerHeight = 120
    val itemsHeight = items.size * rowHeight
    val height = headerHeight + itemsHeight + footerHeight

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background: elegant pista green tint style
    canvas.drawColor(AndroidColor.parseColor("#ECFDF5"))

    // Border line decoration
    val borderPaint = Paint().apply {
        color = AndroidColor.parseColor("#7CAF6D")
        style = Paint.Style.STROKE
        strokeWidth = 14f
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)

    // Header Paints
    val titlePaint = Paint().apply {
        color = AndroidColor.parseColor("#111827")
        textSize = 34f
        isFakeBoldText = true
        isAntiAlias = true
    }
    val subtitlePaint = Paint().apply {
        color = AndroidColor.parseColor("#7CAF6D")
        textSize = 15f
        isFakeBoldText = true
        isAntiAlias = true
    }

    val emoji = getCoverEmoji(coverType)

    canvas.drawText("FreshTrack Grocery Notepad", 70f, 80f, subtitlePaint)
    canvas.drawText("$emoji  $title", 70f, 135f, titlePaint)

    // Today Date
    val datePaint = Paint().apply {
        color = AndroidColor.GRAY
        textSize = 12f
        isAntiAlias = true
    }
    val today = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(Date())
    canvas.drawText(context.getString(com.example.R.string.shopping_list_image_generated, today), 70f, 175f, datePaint)

    // Accent line divider
    val divPaint = Paint().apply {
        color = AndroidColor.parseColor("#7CAF6D")
        strokeWidth = 3f
    }
    canvas.drawLine(70f, 195f, width - 70f, 195f, divPaint)

    // Body lists loop rendering
    var currentY = 240f

    val itemPaint = Paint().apply {
        color = AndroidColor.parseColor("#1F2937")
        textSize = 15f
        isAntiAlias = true
    }
    val qtyPaint = Paint().apply {
        color = AndroidColor.parseColor("#4B5563")
        textSize = 13f
        isAntiAlias = true
    }
    val pricePaint = Paint().apply {
        color = AndroidColor.parseColor("#059669")
        textSize = 15f
        isFakeBoldText = true
        isAntiAlias = true
    }
    val checkboxPaint = Paint().apply {
        color = AndroidColor.parseColor("#9CA3AF")
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    items.forEach { item ->
        // Draw Checkbox box
        canvas.drawRoundRect(RectF(70f, currentY - 14f, 85f, currentY + 1f), 3f, 3f, checkboxPaint)

        // Draw Name text
        val nameText = item.itemName
        val displayUnitText = "(${item.quantity} ${item.unit})"
        canvas.drawText(nameText, 105f, currentY, itemPaint)
        canvas.drawText(displayUnitText, 110f + itemPaint.measureText(nameText) + 5f, currentY, qtyPaint)

        // Draw Price text
        val priceText = "₹${item.price * item.quantity}"
        canvas.drawText(priceText, width - 70f - pricePaint.measureText(priceText), currentY, pricePaint)

        currentY += rowHeight
    }

    // Divider line before sum total
    canvas.drawLine(70f, currentY, width - 70f, currentY, divPaint)
    currentY += 40f

    // Total expense block sum drawing
    val totalExpenseSum = items.sumOf { it.price * it.quantity }
    val totalLabelPaint = Paint().apply {
        color = AndroidColor.parseColor("#1F2937")
        textSize = 15f
        isFakeBoldText = true
        isAntiAlias = true
    }
    val totalSumPaint = Paint().apply {
        color = AndroidColor.parseColor("#059669")
        textSize = 22f
        isFakeBoldText = true
        isAntiAlias = true
    }

    canvas.drawText("Total Estimated Cost:", 70f, currentY, totalLabelPaint)
    val totalValueText = "₹${String.format("%.2f", totalExpenseSum)}"
    canvas.drawText(totalValueText, width - 70f - totalSumPaint.measureText(totalValueText), currentY + 4f, totalSumPaint)

    // Watermark
    val watermarkPaint = Paint().apply {
        color = AndroidColor.parseColor("#7CAF6D")
        textSize = 13f
        isFakeBoldText = true
        isAntiAlias = true
        alpha = 150
    }
    canvas.drawText(context.getString(com.example.R.string.shopping_list_image_watermark), 70f, height.toFloat() - 45f, watermarkPaint)

    return bitmap
}

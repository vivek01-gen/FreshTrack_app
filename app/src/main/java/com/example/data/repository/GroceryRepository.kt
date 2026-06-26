package com.example.data.repository

import com.example.data.local.GroceryDao
import com.example.data.model.GroceryItem
import kotlinx.coroutines.flow.Flow

class GroceryRepository(private val groceryDao: GroceryDao) {
    val allItemsFlow: Flow<List<GroceryItem>> = groceryDao.getAllItemsFlow()

    suspend fun getAllItems(): List<GroceryItem> = groceryDao.getAllItems()

    suspend fun getItemById(id: Long): GroceryItem? = groceryDao.getItemById(id)

    suspend fun insertItem(item: GroceryItem): Long = groceryDao.insertItem(item)

    suspend fun deleteItem(item: GroceryItem) = groceryDao.deleteItem(item)

    suspend fun deleteItemById(id: Long) = groceryDao.deleteItemById(id)

    suspend fun deleteExpiredItems(todayStr: String): Int = groceryDao.deleteExpiredItems(todayStr)

    suspend fun clearAll() = groceryDao.clearAll()
}

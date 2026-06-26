package com.example.data.local

import androidx.room.*
import com.example.data.model.GroceryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {
    @Query("SELECT * FROM grocery_items ORDER BY expiryDate ASC")
    fun getAllItemsFlow(): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items ORDER BY expiryDate ASC")
    suspend fun getAllItems(): List<GroceryItem>

    @Query("SELECT * FROM grocery_items WHERE id = :id")
    suspend fun getItemById(id: Long): GroceryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GroceryItem): Long

    @Delete
    suspend fun deleteItem(item: GroceryItem)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM grocery_items WHERE expiryDate < :todayStr")
    suspend fun deleteExpiredItems(todayStr: String): Int

    @Query("DELETE FROM grocery_items")
    suspend fun clearAll()
}

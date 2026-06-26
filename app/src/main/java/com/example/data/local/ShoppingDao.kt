package com.example.data.local

import androidx.room.*
import com.example.data.model.ShoppingList
import com.example.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    // ── SHOPPING LIST OPERATIONS ──
    @Query("SELECT * FROM shopping_lists ORDER BY modifiedAt DESC")
    fun getAllShoppingListsFlow(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    suspend fun getShoppingListById(id: Long): ShoppingList?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(list: ShoppingList): Long

    @Update
    suspend fun updateShoppingList(list: ShoppingList)

    @Delete
    suspend fun deleteShoppingList(list: ShoppingList)

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteShoppingListById(id: Long)

    @Query("DELETE FROM shopping_lists")
    suspend fun clearAllShoppingLists()

    // ── SHOPPING ITEM OPERATIONS ──
    @Query("SELECT * FROM shopping_items WHERE listId = :listId ORDER BY createdAt ASC")
    fun getShoppingItemsForListFlow(listId: Long): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE listId = :listId ORDER BY createdAt ASC")
    suspend fun getShoppingItemsForList(listId: Long): List<ShoppingItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItem): Long

    @Update
    suspend fun updateShoppingItem(item: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteShoppingItemById(id: Long)

    @Query("DELETE FROM shopping_items WHERE listId = :listId")
    suspend fun deleteShoppingItemsForList(listId: Long)

    @Query("DELETE FROM shopping_items")
    suspend fun clearAllShoppingItems()
}

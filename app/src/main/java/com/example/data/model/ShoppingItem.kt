package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "shopping_lists")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val totalExpense: Double = 0.0,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L,
    val coverImagePath: String? = null
)

@Entity(
    tableName = "shopping_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["listId"])]
)
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val itemName: String,
    val quantity: Int = 1,
    val unit: String = "Pcs",
    val price: Double = 0.0,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

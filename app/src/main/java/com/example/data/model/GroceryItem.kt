package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // fruits, vegetables, dairy, etc.
    val quantity: Double,
    val unit: String, // kg, gm, litre, ml, pieces, packets
    val price: Double?, // optional price
    val boughtDate: String, // YYYY-MM-DD
    val expiryDate: String, // YYYY-MM-DD
    val notes: String = "",
    val photoPath: String? = null // local file path to the image
)

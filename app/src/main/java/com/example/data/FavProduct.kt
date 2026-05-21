package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavProduct(
    @PrimaryKey val id: String,
    val title: String,
    val url: String,
    val imageUrl: String,
    val price: String,
    val isSale: Boolean,
    val category: String = "Geral",
    val description: String = ""
)

package com.example.tvmaze.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val showId: Int,
    val name: String,
    val imageUrl: String?
)

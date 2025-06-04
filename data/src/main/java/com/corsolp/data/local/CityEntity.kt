package com.corsolp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val name: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false
)

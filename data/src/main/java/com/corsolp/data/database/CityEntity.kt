package com.corsolp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val name: String,
    val latitude: Double,
    val longitude: Double
)

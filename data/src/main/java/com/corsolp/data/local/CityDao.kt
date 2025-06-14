package com.corsolp.data.local

import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query

@Dao
interface CityDao {
    @Query("SELECT * FROM cities")
    suspend fun getAllCities(): List<CityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)

    @Delete
    suspend fun deleteCity(city: CityEntity)

    @Update
    suspend fun updateCity(city: CityEntity)
}

package com.corsolp.domain.repository

import com.corsolp.domain.model.City


interface CityRepository {
    suspend fun insertCity(city: City)
    suspend fun getAllCities(): List<City>
    suspend fun deleteCity(city: City)
    suspend fun updateCity(city: City)
}

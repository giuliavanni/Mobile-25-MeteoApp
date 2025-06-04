package com.corsolp.data.repository

import com.corsolp.data.local.CityDao
import com.corsolp.data.mapper.toEntity
import com.corsolp.domain.model.City
import com.corsolp.domain.repository.CityRepository
import com.corsolp.data.mapper.toDomain

class CityRepositoryImpl(private val dao: CityDao) : CityRepository {

    override suspend fun insertCity(city: City) {
        dao.insertCity(city.toEntity())
    }

    override suspend fun getAllCities(): List<City> {
        return dao.getAllCities().map { it.toDomain() }
    }

    override suspend fun deleteCity(city: City) {
        dao.deleteCity(city.toEntity())
    }

    override suspend fun updateCity(city: City) {
        dao.updateCity(city.toEntity())
    }
}

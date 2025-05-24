package com.corsolp.domain.usecase

import com.corsolp.domain.repository.CityRepository
import com.corsolp.domain.model.City

class GetSavedCitiesUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(): List<City> {
        return repository.getAllCities()
    }
}


package com.corsolp.domain.usecase

import com.corsolp.domain.repository.CityRepository
import com.corsolp.domain.model.City


class SaveCityUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(city: City) {
        repository.insertCity(city)
    }
}

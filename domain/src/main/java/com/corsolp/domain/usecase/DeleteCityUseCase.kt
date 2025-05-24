package com.corsolp.domain.usecase

import com.corsolp.domain.model.City
import com.corsolp.domain.repository.CityRepository

class DeleteCityUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(city: City) {
        repository.deleteCity(city)
    }
}

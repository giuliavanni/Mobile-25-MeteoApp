package com.corsolp.domain.usecase

import com.corsolp.domain.model.City
import com.corsolp.domain.repository.CityRepository

class ToggleFavoriteCityUseCase(private val cityRepository: CityRepository) {
    suspend operator fun invoke(city: City) {
        val updatedCity = city.copy(isFavorite = !city.isFavorite)
        cityRepository.updateCity(updatedCity)
    }
}
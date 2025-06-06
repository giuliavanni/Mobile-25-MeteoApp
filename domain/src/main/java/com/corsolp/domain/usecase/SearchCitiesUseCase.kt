package com.corsolp.domain.usecase

import com.corsolp.domain.model.NominatimResult
import com.corsolp.domain.repository.CityRepository

class SearchCitiesUseCase(private val cityRepository: CityRepository) {
    suspend fun execute(query: String): List<NominatimResult> {
        return cityRepository.searchCities(query)
    }
}

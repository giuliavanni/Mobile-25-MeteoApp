package com.corsolp.domain.usecase

import com.corsolp.domain.model.ForecastItem
import com.corsolp.domain.repository.ForecastRepository

class FetchForecastUseCase(private val repository: ForecastRepository) {
    suspend operator fun invoke(city: String, lang: String, apiKey: String): Result<List<ForecastItem>> {
        return repository.getForecast(city, lang, apiKey)
    }
}

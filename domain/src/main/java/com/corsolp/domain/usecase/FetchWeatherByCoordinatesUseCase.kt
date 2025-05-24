package com.corsolp.domain.usecase

import com.corsolp.domain.model.WeatherInfo
import com.corsolp.domain.repository.WeatherRepository

class FetchWeatherByCoordinatesUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double, lang: String, apiKey: String): Result<WeatherInfo> {
        return repository.fetchWeatherByCoordinates(lat, lon, lang, apiKey)
    }
}
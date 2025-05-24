package com.corsolp.domain.usecase

import com.corsolp.domain.model.WeatherInfo
import com.corsolp.domain.repository.WeatherRepository

class FetchWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String, lang: String, apiKey: String): Result<WeatherInfo> {
        return repository.fetchWeatherByCity(city, lang, apiKey)
    }
}

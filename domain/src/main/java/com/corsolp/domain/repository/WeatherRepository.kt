package com.corsolp.domain.repository

import com.corsolp.domain.model.WeatherInfo

interface WeatherRepository {
    suspend fun fetchWeatherByCity(city: String, lang: String, apiKey: String): Result<WeatherInfo>
    suspend fun fetchWeatherByCoordinates(lat: Double, lon: Double, lang: String, apiKey: String): Result<WeatherInfo>
}

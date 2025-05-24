package com.corsolp.domain.repository


import com.corsolp.domain.model.ForecastItem

interface ForecastRepository {
    suspend fun getForecast(city: String, lang: String, apiKey: String): Result<List<ForecastItem>>
}

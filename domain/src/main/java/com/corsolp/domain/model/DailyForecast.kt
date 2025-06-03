package com.corsolp.domain.model

data class DailyForecast(
    val date: String,
    val avgTemp: Double,
    val weatherDescription: String,
    val iconUrl: String
)
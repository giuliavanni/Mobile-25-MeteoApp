package com.corsolp.data

data class ForecastItem(
    val date: String,
    val temp: Double,
    val description: String,
    val iconUrl: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double
)
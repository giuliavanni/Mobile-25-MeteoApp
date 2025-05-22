package com.corsolp.domain.model

data class WeatherInfo(
    val cityName: String,
    val temperature: Double,
    val description: String,
    val iconUrl: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
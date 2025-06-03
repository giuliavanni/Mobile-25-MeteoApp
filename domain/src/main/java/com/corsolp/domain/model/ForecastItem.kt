package com.corsolp.domain.model


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ForecastItem(
    val date: String,
    val temp: Double,
    val description: String,
    val iconUrl: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double
) : Parcelable

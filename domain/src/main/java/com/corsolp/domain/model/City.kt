package com.corsolp.domain.model

data class City(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false
)
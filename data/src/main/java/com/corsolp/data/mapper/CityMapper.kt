package com.corsolp.data.mapper

import com.corsolp.domain.model.City
import com.corsolp.data.local.CityEntity

fun CityEntity.toDomain(): City {
    return City(
        name = this.name,
        latitude = this.latitude,
        longitude = this.longitude,
        isFavorite = this.isFavorite
    )
}

fun City.toEntity(): CityEntity {
    return CityEntity(
        name = this.name,
        latitude = this.latitude,
        longitude = this.longitude,
        isFavorite = this.isFavorite
    )
}

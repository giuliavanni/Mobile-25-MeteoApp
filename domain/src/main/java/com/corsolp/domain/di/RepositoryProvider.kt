package com.corsolp.domain.di

import com.corsolp.domain.repository.CityRepository
import com.corsolp.domain.repository.ForecastRepository
import com.corsolp.domain.repository.WeatherRepository


interface RepositoryProvider {
    val cityRepository: CityRepository
    val weatherRepository: WeatherRepository
    val forecastRepository: ForecastRepository
}
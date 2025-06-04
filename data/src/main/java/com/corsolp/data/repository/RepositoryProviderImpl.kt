package com.corsolp.data.repository


import com.corsolp.data.local.CityDao
import com.corsolp.domain.di.RepositoryProvider
import com.corsolp.domain.repository.CityRepository
import com.corsolp.domain.repository.ForecastRepository
import com.corsolp.domain.repository.WeatherRepository

class RepositoryProviderImpl(private val cityDao: CityDao,): RepositoryProvider {
    override val cityRepository: CityRepository = CityRepositoryImpl(cityDao)
    override val weatherRepository: WeatherRepository = WeatherRepositoryImpl()
    override val forecastRepository: ForecastRepository = ForecastRepositoryImpl()
}
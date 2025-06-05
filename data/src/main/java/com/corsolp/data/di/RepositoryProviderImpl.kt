package com.corsolp.data.di

import android.content.Context
import com.corsolp.data.local.AppDatabase
import com.corsolp.data.repository.CityRepositoryImpl
import com.corsolp.data.repository.ForecastRepositoryImpl
import com.corsolp.data.repository.SettingsRepositoryImpl
import com.corsolp.data.repository.WeatherRepositoryImpl
import com.corsolp.domain.di.RepositoryProvider
import com.corsolp.domain.repository.CityRepository
import com.corsolp.domain.repository.ForecastRepository
import com.corsolp.domain.repository.SettingsRepository
import com.corsolp.domain.repository.WeatherRepository

class RepositoryProviderImpl(context: Context) : RepositoryProvider {

    private val appContext = context.applicationContext

    override val cityRepository: CityRepository by lazy {
        val dao = AppDatabase.getDatabase(appContext).cityDao()
        CityRepositoryImpl(dao)
    }

    override val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl()
    }

    override val forecastRepository: ForecastRepository by lazy {
        ForecastRepositoryImpl()
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(appContext)
    }
}


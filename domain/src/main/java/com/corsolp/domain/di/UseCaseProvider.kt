package com.corsolp.domain.di

import com.corsolp.domain.usecase.DeleteCityUseCase
import com.corsolp.domain.usecase.FetchForecastUseCase
import com.corsolp.domain.usecase.FetchWeatherByCoordinatesUseCase
import com.corsolp.domain.usecase.FetchWeatherUseCase
import com.corsolp.domain.usecase.GetSavedCitiesUseCase
import com.corsolp.domain.usecase.SaveCityUseCase

object UseCaseProvider {

    lateinit var fetchWeatherUseCase: FetchWeatherUseCase
    lateinit var fetchForecastUseCase: FetchForecastUseCase
    lateinit var fetchWeatherByCoordinatesUseCase: FetchWeatherByCoordinatesUseCase
    lateinit var saveCityUseCase: SaveCityUseCase
    lateinit var getSavedCitiesUseCase: GetSavedCitiesUseCase
    lateinit var deleteCityUseCase: DeleteCityUseCase

    fun setup(
        repositoryProvider: RepositoryProvider
    ) {
        fetchWeatherUseCase = FetchWeatherUseCase(repositoryProvider.weatherRepository)
        fetchForecastUseCase = FetchForecastUseCase(repositoryProvider.forecastRepository)
        fetchWeatherByCoordinatesUseCase = FetchWeatherByCoordinatesUseCase(repositoryProvider.weatherRepository)

        saveCityUseCase = SaveCityUseCase(repositoryProvider.cityRepository)
        getSavedCitiesUseCase = GetSavedCitiesUseCase(repositoryProvider.cityRepository)
        deleteCityUseCase = DeleteCityUseCase(repositoryProvider.cityRepository)
    }
}

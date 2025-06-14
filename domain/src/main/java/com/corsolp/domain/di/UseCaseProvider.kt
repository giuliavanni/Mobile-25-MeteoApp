package com.corsolp.domain.di

import com.corsolp.domain.usecase.DeleteCityUseCase
import com.corsolp.domain.usecase.FetchForecastUseCase
import com.corsolp.domain.usecase.FetchWeatherByCoordinatesUseCase
import com.corsolp.domain.usecase.FetchWeatherUseCase
import com.corsolp.domain.usecase.GetAppLanguageUseCase
import com.corsolp.domain.usecase.GetSavedCitiesUseCase
import com.corsolp.domain.usecase.SaveCityUseCase
import com.corsolp.domain.usecase.SearchCitiesUseCase
import com.corsolp.domain.usecase.ToggleFavoriteCityUseCase

object UseCaseProvider {

    lateinit var fetchWeatherUseCase: FetchWeatherUseCase
    lateinit var fetchForecastUseCase: FetchForecastUseCase
    lateinit var fetchWeatherByCoordinatesUseCase: FetchWeatherByCoordinatesUseCase
    lateinit var saveCityUseCase: SaveCityUseCase
    lateinit var getSavedCitiesUseCase: GetSavedCitiesUseCase
    lateinit var deleteCityUseCase: DeleteCityUseCase
    lateinit var toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase
    lateinit var getAppLanguageUseCase: GetAppLanguageUseCase
    lateinit var searchCitiesUseCase: SearchCitiesUseCase


    fun setup(
        repositoryProvider: RepositoryProvider
    ) {
        fetchWeatherUseCase = FetchWeatherUseCase(repositoryProvider.weatherRepository)
        fetchForecastUseCase = FetchForecastUseCase(repositoryProvider.forecastRepository)
        fetchWeatherByCoordinatesUseCase = FetchWeatherByCoordinatesUseCase(repositoryProvider.weatherRepository)

        saveCityUseCase = SaveCityUseCase(repositoryProvider.cityRepository)
        getSavedCitiesUseCase = GetSavedCitiesUseCase(repositoryProvider.cityRepository)
        deleteCityUseCase = DeleteCityUseCase(repositoryProvider.cityRepository)
        toggleFavoriteCityUseCase = ToggleFavoriteCityUseCase(repositoryProvider.cityRepository)
        getAppLanguageUseCase = GetAppLanguageUseCase(repositoryProvider.settingsRepository)
        searchCitiesUseCase = SearchCitiesUseCase(repositoryProvider.cityRepository)
    }
}


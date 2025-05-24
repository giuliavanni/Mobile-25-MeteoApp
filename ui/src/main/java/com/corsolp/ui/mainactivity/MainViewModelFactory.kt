package com.corsolp.ui.mainactivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.usecase.DeleteCityUseCase
import com.corsolp.domain.usecase.FetchWeatherByCoordinatesUseCase
import com.corsolp.domain.usecase.FetchWeatherUseCase
import com.corsolp.domain.usecase.GetSavedCitiesUseCase
import com.corsolp.domain.usecase.SaveCityUseCase

class MainViewModelFactory(
    private val application: Application,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val saveCityUseCase: SaveCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val fetchWeatherUseCase: FetchWeatherUseCase,
    private val fetchWeatherByCoordinatesUseCase: FetchWeatherByCoordinatesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                application,
                getSavedCitiesUseCase,
                saveCityUseCase,
                deleteCityUseCase,
                fetchWeatherUseCase,
                fetchWeatherByCoordinatesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

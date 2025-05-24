package com.corsolp.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.corsolp.domain.usecase.FetchForecastUseCase

class ForecastViewModelFactory(
    private val fetchForecastUseCase: FetchForecastUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForecastViewModel(fetchForecastUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

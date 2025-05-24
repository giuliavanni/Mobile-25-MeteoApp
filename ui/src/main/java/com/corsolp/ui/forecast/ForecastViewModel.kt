package com.corsolp.ui.forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.model.ForecastItem
import com.corsolp.domain.usecase.FetchForecastUseCase
import kotlinx.coroutines.launch



class ForecastViewModel(private val fetchForecastUseCase: FetchForecastUseCase) : ViewModel() {

    private val _forecastList = MutableLiveData<List<ForecastItem>>()
    val forecastList: LiveData<List<ForecastItem>> = _forecastList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadForecast(city: String, lang: String, apiKey: String) {
        viewModelScope.launch {
            val result = fetchForecastUseCase(city, lang, apiKey)
            result
                .onSuccess { _forecastList.value = it }
                .onFailure { _error.value = "Errore: ${it.message}" }
        }
    }
}

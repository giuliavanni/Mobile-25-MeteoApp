package com.corsolp.ui.forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.domain.model.DailyForecast
import com.corsolp.domain.model.ForecastItem
import com.corsolp.domain.usecase.FetchForecastUseCase
import com.corsolp.domain.usecase.GetAppLanguageUseCase
import com.corsolp.ui.BuildConfig
import kotlinx.coroutines.launch



class ForecastViewModel(
    private val fetchForecastUseCase: FetchForecastUseCase,
    private val getAppLanguageUseCase: GetAppLanguageUseCase
) : ViewModel() {

    private val _forecastList = MutableLiveData<List<ForecastItem>>()
    val forecastList: LiveData<List<ForecastItem>> = _forecastList

    private val _dailyForecastList = MutableLiveData<List<DailyForecast>>()
    val dailyForecastList: LiveData<List<DailyForecast>> = _dailyForecastList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadForecast(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val lang = getAppLanguageUseCase.execute()
                val result = fetchForecastUseCase(city, lang, apiKey)

                if (result.isSuccess) {
                    val forecastList = result.getOrThrow()
                    _forecastList.value = forecastList
                    _dailyForecastList.value = summarizeForecast(forecastList)
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Errore sconosciuto"
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Errore sconosciuto"
            }
        }
    }

    private fun summarizeForecast(forecastList: List<ForecastItem>): List<DailyForecast> {
        return forecastList
            .groupBy { it.date.substringBefore(" ") }
            .map { (date, items) ->
                val avgTemp = items.map { it.temp }.average()
                val filteredItems = items.filterNot { it.iconUrl.contains("01n") }
                val relevantItems = if (filteredItems.isNotEmpty()) filteredItems else items
                val mainCondition = relevantItems.groupBy { it.description }
                    .maxByOrNull { it.value.size }?.key ?: "N/A"
                val iconUrl = relevantItems.firstOrNull()?.iconUrl ?: ""
                DailyForecast(date, avgTemp, mainCondition, iconUrl)
            }
    }
}


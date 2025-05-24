package com.corsolp.ui.mainactivity

import android.app.Application
import androidx.lifecycle.*
import com.corsolp.data.database.CityEntity
import com.corsolp.data.mapper.toDomain
import com.corsolp.data.mapper.toEntity
import com.corsolp.domain.model.City
import com.corsolp.domain.model.WeatherInfo
import com.corsolp.domain.usecase.DeleteCityUseCase
import com.corsolp.domain.usecase.FetchWeatherUseCase
import com.corsolp.domain.usecase.GetSavedCitiesUseCase
import com.corsolp.domain.usecase.SaveCityUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val saveCityUseCase: SaveCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val fetchWeatherUseCase: FetchWeatherUseCase
) : AndroidViewModel(application) {

    private val _cities = MutableLiveData<List<CityEntity>?>()
    val cities: LiveData<List<CityEntity>?> = _cities

    private val _weather = MutableLiveData<WeatherInfo?>()
    val weather: LiveData<WeatherInfo?> = _weather

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadSavedCities() {
        viewModelScope.launch {
            val domainCities = getSavedCitiesUseCase()
            _cities.value = domainCities.map { it.toEntity() }
        }
    }

    fun saveCity(city: CityEntity) {
        viewModelScope.launch {
            saveCityUseCase(city.toDomain())
            loadSavedCities()
        }
    }

    fun deleteCity(city: CityEntity) {
        viewModelScope.launch {
            deleteCityUseCase(city.toDomain())
            loadSavedCities()
        }
    }

    fun fetchWeather(city: String, lang: String, apiKey: String) {
        viewModelScope.launch {
            val result = fetchWeatherUseCase(city, lang, apiKey)
            result.onSuccess { info ->
                _weather.value = info
                saveCityUseCase(City(info.cityName, info.latitude, info.longitude))
                loadSavedCities()
            }
            result.onFailure {
                _error.value = it.message ?: "Errore sconosciuto"
            }
        }
    }

    fun fetchWeatherByCoordinates(lat: Double, lon: Double, lang: String, apiKey: String) {
        viewModelScope.launch {
            val result = fetchWeatherUseCase.byCoordinates(lat, lon, lang, apiKey)
            result.onSuccess { _weather.value = it }
            result.onFailure { _error.value = it.message ?: "Errore sconosciuto" }
        }
    }
}
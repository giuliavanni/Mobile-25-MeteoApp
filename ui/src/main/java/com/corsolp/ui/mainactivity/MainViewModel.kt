package com.corsolp.ui.mainactivity

import android.app.Application
import androidx.lifecycle.*
import com.corsolp.domain.model.City
import com.corsolp.domain.model.WeatherInfo
import com.corsolp.domain.repository.CityRepository
import com.corsolp.domain.usecase.DeleteCityUseCase
import com.corsolp.domain.usecase.FetchWeatherByCoordinatesUseCase
import com.corsolp.domain.usecase.FetchWeatherUseCase
import com.corsolp.domain.usecase.GetAppLanguageUseCase
import com.corsolp.domain.usecase.GetSavedCitiesUseCase
import com.corsolp.domain.usecase.SaveCityUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val saveCityUseCase: SaveCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val fetchWeatherUseCase: FetchWeatherUseCase,
    private val fetchWeatherByCoordinatesUseCase: FetchWeatherByCoordinatesUseCase,
    private val cityRepository: CityRepository,
    private val getAppLanguageUseCase: GetAppLanguageUseCase


) : AndroidViewModel(application) {

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> get() = _cities

    private val _weather = MutableLiveData<WeatherInfo?>()
    val weather: LiveData<WeatherInfo?> = _weather

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadSavedCities() {
        viewModelScope.launch {
            val domainCities = getSavedCitiesUseCase()
            _cities.postValue(domainCities.sortedByDescending { it.isFavorite })
        }
    }

    fun saveCity(city: City) {
        viewModelScope.launch {
            saveCityUseCase(city)
            loadSavedCities()
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch {
            deleteCityUseCase(city)
            loadSavedCities()
        }
    }

    fun getLanguage(): String {
        return getAppLanguageUseCase.execute()
    }

    fun fetchWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            val lang = getAppLanguageUseCase.execute()
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

    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            val lang = getAppLanguageUseCase.execute()
            val result = fetchWeatherByCoordinatesUseCase(lat, lon, lang, apiKey)
            result.onSuccess { _weather.value = it }
            result.onFailure { _error.value = it.message ?: "Errore sconosciuto" }
        }
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            val updatedCity = city.copy(isFavorite = !city.isFavorite)
            cityRepository.updateCity(updatedCity)
            loadSavedCities()
        }
    }

}
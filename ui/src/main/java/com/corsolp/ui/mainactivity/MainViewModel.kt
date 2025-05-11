package com.corsolp.ui.mainactivity

import android.app.Application
import androidx.lifecycle.*
import com.corsolp.data.database.AppDatabase
import com.corsolp.data.database.CityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val _cities = MutableLiveData<List<CityEntity>>()
    val cities: LiveData<List<CityEntity>> = _cities

    fun loadSavedCities() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = database.cityDao().getAllCities()
            _cities.postValue(result)
        }
    }

    fun saveCity(city: CityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            database.cityDao().insertCity(city)
            loadSavedCities()
        }
    }

    fun deleteCity(city: CityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            database.cityDao().deleteCity(city)
            loadSavedCities()
        }
    }
}

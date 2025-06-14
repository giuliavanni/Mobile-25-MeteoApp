package com.corsolp.ui.mainactivity

import androidx.activity.viewModels
import com.corsolp.domain.model.WeatherInfo
import com.corsolp.data.local.CityEntity
import com.corsolp.ui.BuildConfig
import com.corsolp.ui.forecast.ForecastActivity
import com.corsolp.ui.databinding.ActivityMainBinding
import com.corsolp.ui.map.MapActivity
import com.corsolp.data.repository.SettingsRepositoryImpl
import com.corsolp.data.mapper.toEntity
import com.corsolp.domain.usecase.GetAppLanguageUseCase
import com.corsolp.ui.BaseActivity
import com.corsolp.ui.utils.TemperatureUtils
import java.util.Locale

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.corsolp.domain.di.UseCaseProvider
import com.corsolp.domain.model.City
import com.corsolp.domain.model.NominatimResult
import com.corsolp.ui.R
import com.corsolp.ui.settings.SettingsActivity
import okhttp3.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.io.IOException
import java.util.*
import kotlin.collections.mutableListOf

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cityAdapter: CityAdapter
    private val savedCities = mutableListOf<CityEntity>()

        private val viewModel : MainViewModel  by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    application = application,
                    getSavedCitiesUseCase = UseCaseProvider.getSavedCitiesUseCase,
                    saveCityUseCase = UseCaseProvider.saveCityUseCase,
                    deleteCityUseCase = UseCaseProvider.deleteCityUseCase,
                    fetchWeatherUseCase = UseCaseProvider.fetchWeatherUseCase,
                    fetchWeatherByCoordinatesUseCase = UseCaseProvider.fetchWeatherByCoordinatesUseCase,
                    getAppLanguageUseCase = GetAppLanguageUseCase(SettingsRepositoryImpl(applicationContext)),
                    toggleFavoriteCityUseCase = UseCaseProvider.toggleFavoriteCityUseCase,
                    searchCitiesUseCase = UseCaseProvider.searchCitiesUseCase
                ) as T
            }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Autocomplete city search
    private lateinit var cityAutoCompleteAdapter: ArrayAdapter<String>
    private val cityResults = mutableListOf<NominatimResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.weatherIconImageView.visibility = View.GONE

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val settingsButton = findViewById<ImageButton>(R.id.iconSettings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // RecyclerView e CityAdapter
        cityAdapter = CityAdapter(savedCities,
            onItemClick = { city -> getWeather(city.latitude, city.longitude) },
            onItemLongClick = { city ->
                viewModel.deleteCity(city)
                Toast.makeText(this, "Città eliminata", Toast.LENGTH_SHORT).show()
            },
            onMapClick = { city -> openMapForCity(city.toEntity()) },
            onForecastClick = { city ->
                val intent = Intent(this, ForecastActivity::class.java)
                intent.putExtra("city_name", city.name)
                startActivity(intent)
            },
            onFavoriteToggle = { city -> viewModel.toggleFavorite(city) }
        )
        binding.savedCitiesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.savedCitiesRecyclerView.adapter = cityAdapter

        viewModel.cities.observe(this) { updatedCities ->
            updatedCities?.let {
                savedCities.clear()
                savedCities.addAll(it.map { city -> city.toEntity() })
                cityAdapter.notifyDataSetChanged()
            }
        }
        viewModel.loadSavedCities()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissionAndFetchWeather()

        // Setup autocomplete
        cityAutoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line)
        binding.cityAutoCompleteTextView?.setAdapter(cityAutoCompleteAdapter)

        binding.cityAutoCompleteTextView?.threshold = 1

        binding.cityAutoCompleteTextView?.addTextChangedListener(object : TextWatcher {
            private var timer = Timer()
            private val DELAY = 400L  // debounce 400ms

            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val query = s?.toString()?.trim()
                        if (!query.isNullOrEmpty() && query.length >= 2) {
                            viewModel.searchCities(query)
                        }
                    }
                }, DELAY)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.searchResults.observe(this) { results ->
            cityResults.clear()
            cityResults.addAll(results)
            val names = results.map { it.display_name }
            cityAutoCompleteAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                names
            )
            binding.cityAutoCompleteTextView!!.setAdapter(cityAutoCompleteAdapter)
            cityAutoCompleteAdapter.notifyDataSetChanged()
            binding.cityAutoCompleteTextView!!.showDropDown()
        }


        binding.cityAutoCompleteTextView?.setOnItemClickListener { parent, view, position, id ->
            val selectedCity = cityResults[position]
            val lat = selectedCity.lat.toDoubleOrNull()
            val lon = selectedCity.lon.toDoubleOrNull()
            if (lat != null && lon != null) {
                getWeather(lat, lon)
                val city = selectedCity.toCity()
                viewModel.saveCity(city)
            }
            binding.cityAutoCompleteTextView!!.setText("")
        }


        viewModel.weather.observe(this) { info ->
            info?.let { updateUI(it) }
        }

        viewModel.error.observe(this) { message ->
            message?.let { showError(it) }
        }
    }

    fun NominatimResult.toCity(): City {
        return City(
            name = this.display_name,
            latitude = this.lat.toDoubleOrNull() ?: 0.0,
            longitude = this.lon.toDoubleOrNull() ?: 0.0,
            isFavorite = false
        )
    }

    private fun checkLocationPermissionAndFetchWeather() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                AlertDialog.Builder(this)
                    .setTitle("Permesso necessario")
                    .setMessage("Per mostrare il meteo nella tua posizione, l'app ha bisogno del permesso di localizzazione.")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
                    }
                    .setNegativeButton("Annulla", null)
                    .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                Toast.makeText(this, "Permesso posizione negato", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    getWeather(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Posizione non disponibile", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun getWeather(lat: Double, lon: Double) {
        val apiKey = BuildConfig.OPENWEATHER_API_KEY
        viewModel.fetchWeatherByCoordinates(lat, lon, apiKey)
    }

    private fun updateUI(info: WeatherInfo) {
        binding.textCityName.text = info.cityName

        val (tempToShow, unitSymbol) = TemperatureUtils.convertTemperature(this, info.temperature)
        val formattedTemp = String.format(Locale.getDefault(), "%.1f %s", tempToShow, unitSymbol)
        binding.textTemperature.text = formattedTemp

        binding.textDescription.text = info.description

        binding.textCityName.visibility = View.VISIBLE
        binding.textTemperature.visibility = View.VISIBLE
        binding.textDescription.visibility = View.VISIBLE
        binding.weatherIconImageView.visibility = View.VISIBLE

        Glide.with(this)
            .load(info.iconUrl)
            .into(binding.weatherIconImageView)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun openMapForCity(city: CityEntity) {
        val intent = Intent(this, MapActivity::class.java).apply {
            putExtra("city_name", city.name)
            putExtra("latitude", city.latitude)
            putExtra("longitude", city.longitude)
        }
        startActivity(intent)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

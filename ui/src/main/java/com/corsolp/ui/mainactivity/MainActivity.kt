package com.corsolp.ui.mainactivity

import android.content.Context
import android.content.res.Configuration
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.corsolp.domain.model.WeatherInfo
import com.corsolp.data.database.CityEntity
import com.corsolp.ui.BuildConfig
import com.corsolp.ui.forecast.ForecastActivity
import com.corsolp.ui.databinding.ActivityMainBinding
import com.corsolp.ui.map.MapActivity
import com.corsolp.data.database.AppDatabase
import com.corsolp.data.di.CityRepositoryImpl
import com.corsolp.data.di.SettingsRepositoryImpl
import com.corsolp.data.di.WeatherRepositoryImpl
import com.corsolp.data.mapper.toEntity
import com.corsolp.domain.usecase.DeleteCityUseCase
import com.corsolp.domain.usecase.FetchWeatherByCoordinatesUseCase
import com.corsolp.domain.usecase.FetchWeatherUseCase
import com.corsolp.domain.usecase.GetAppLanguageUseCase
import com.corsolp.domain.usecase.GetSavedCitiesUseCase
import com.corsolp.domain.usecase.SaveCityUseCase
import com.corsolp.ui.BaseActivity
import com.corsolp.ui.utils.TemperatureUtils
import java.util.Locale

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.corsolp.domain.model.City
import com.corsolp.ui.NominatimResult
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

    private val viewModel: MainViewModel by viewModels {
        provideViewModelFactory()
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
                            searchCities(query) { results ->
                                runOnUiThread {
                                    cityResults.clear()
                                    cityResults.addAll(results)
                                    val names = results.map { it.display_name }

                                    cityAutoCompleteAdapter = ArrayAdapter(
                                        this@MainActivity,
                                        android.R.layout.simple_dropdown_item_1line,
                                        names
                                    )
                                    binding.cityAutoCompleteTextView!!.setAdapter(cityAutoCompleteAdapter)
                                    cityAutoCompleteAdapter.notifyDataSetChanged()
                                    binding.cityAutoCompleteTextView!!.showDropDown()
                                }
                            }
                        }
                    }
                }, DELAY)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


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

    // -- Inserisci qui la tua funzione searchCities (con OkHttp + Gson)

    fun searchCities(query: String, onResult: (List<NominatimResult>) -> Unit) {
        val client = OkHttpClient()
        val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=json&addressdetails=1&limit=5"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "YourAppName/1.0")  // importante
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (body != null) {
                        val gson = Gson()
                        val results = gson.fromJson(body, Array<NominatimResult>::class.java).toList()
                        onResult(results)
                    } else {
                        onResult(emptyList())
                    }
                } else {
                    onResult(emptyList())
                }
            }
        })
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

    private fun provideViewModelFactory(): MainViewModelFactory {
        val dao = AppDatabase.getDatabase(applicationContext).cityDao()
        val cityRepository = CityRepositoryImpl(dao)
        val weatherRepository = WeatherRepositoryImpl()
        val settingsRepository = SettingsRepositoryImpl(applicationContext)
        val getAppLanguageUseCase = GetAppLanguageUseCase(settingsRepository)

        return MainViewModelFactory(
            application,
            getSavedCitiesUseCase = GetSavedCitiesUseCase(cityRepository),
            saveCityUseCase = SaveCityUseCase(cityRepository),
            deleteCityUseCase = DeleteCityUseCase(cityRepository),
            fetchWeatherUseCase = FetchWeatherUseCase(weatherRepository),
            fetchWeatherByCoordinatesUseCase = FetchWeatherByCoordinatesUseCase(weatherRepository),
            getAppLanguageUseCase = getAppLanguageUseCase,
            cityRepository = cityRepository
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

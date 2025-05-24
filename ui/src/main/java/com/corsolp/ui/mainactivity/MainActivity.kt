package com.corsolp.ui.mainactivity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.corsolp.domain.model.WeatherInfo
import com.corsolp.data.database.CityEntity
import com.corsolp.data.settings.SettingsManager
import com.corsolp.ui.BuildConfig
import com.corsolp.ui.R
import com.corsolp.ui.forecast.ForecastActivity
import com.corsolp.ui.databinding.ActivityMainBinding
import com.corsolp.ui.map.MapActivity
import com.corsolp.ui.settings.SettingsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.corsolp.data.database.AppDatabase
import com.corsolp.data.repository.CityRepositoryImpl
import com.corsolp.data.repository.WeatherRepositoryImpl
import com.corsolp.domain.usecase.DeleteCityUseCase
import com.corsolp.domain.usecase.FetchWeatherUseCase
import com.corsolp.domain.usecase.GetSavedCitiesUseCase
import com.corsolp.domain.usecase.SaveCityUseCase
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cityAdapter: CityAdapter
    private val savedCities = mutableListOf<CityEntity>()

    private val viewModel: MainViewModel by viewModels {
        provideViewModelFactory()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun attachBaseContext(newBase: Context) {
        val lang = SettingsManager.getLanguage(newBase)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(newContext)

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.weatherIconImageView.visibility = View.GONE

        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // RecyclerView
        cityAdapter = CityAdapter(savedCities,
            onItemClick = { city ->
                getWeather(city.latitude, city.longitude)
            },
            onItemLongClick = { city ->
                viewModel.deleteCity(city)
                Toast.makeText(this, "Città eliminata", Toast.LENGTH_SHORT).show()
            },
            onMapClick = { city -> openMapForCity(city)},
            onForecastClick = { city ->
                val intent = Intent(this, ForecastActivity::class.java)
                intent.putExtra("city_name", city.name)
                startActivity(intent)
            }
        )

        binding.savedCitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.savedCitiesRecyclerView.adapter = cityAdapter

        // ViewModel: osserva le città salvate
        viewModel.cities.observe(this) { updatedCities ->
            updatedCities?.let {
                savedCities.clear()
                savedCities.addAll(it)
                cityAdapter.notifyDataSetChanged()
            }
        }

        // Carica città iniziali
        viewModel.loadSavedCities()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissionAndFetchWeather()

        // Pulsante ricerca
        binding.searchButton.setOnClickListener {
            val cityName = binding.cityEditText.text.toString().trim()
            if (cityName.isNotEmpty()) {
                getWeatherAndSave(cityName)
            } else {
                Toast.makeText(this, "Inserisci una città", Toast.LENGTH_SHORT).show()
            }
        binding.cityEditText.text.clear()
        }

        // osserva meteo e errori
        viewModel.weather.observe(this) { info ->
            info?.let { updateUI(it) }
        }

        viewModel.error.observe(this) { message ->
            message?.let { showError(it) }
        }

    }

    private fun checkLocationPermissionAndFetchWeather() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        // Verifica se il permesso è già stato concesso
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            // Se il permesso non è stato concesso, chiedilo
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Mostra una spiegazione all'utente prima di richiedere il permesso
                AlertDialog.Builder(this)
                    .setTitle("Permesso necessario")
                    .setMessage("Per mostrare il meteo nella tua posizione, l'app ha bisogno del permesso di localizzazione.")
                    .setPositiveButton("OK") { _, _ ->
                        // Richiedi il permesso
                        ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
                    }
                    .setNegativeButton("Annulla", null)
                    .show()
            } else {
                // Se non è necessario mostrare una spiegazione, chiedi direttamente il permesso
                ActivityCompat.requestPermissions(this, arrayOf(permission), LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Verifica se il permesso è stato concesso
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concesso, ottieni la posizione
                getLastKnownLocation()
            } else {
                // Permesso negato, mostra un messaggio all'utente
                Toast.makeText(this, "Permesso posizione negato", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLastKnownLocation() {
        // Verifica se i permessi sono concessi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // Ottieni la latitudine e longitudine e chiama la funzione per ottenere il meteo
                    getWeather(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Posizione non disponibile", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Se il permesso non è stato concesso, chiedilo
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getWeather(lat: Double, lon: Double) {
        val lang = SettingsManager.getLanguage(this)
        val apiKey = BuildConfig.OPENWEATHER_API_KEY
        viewModel.fetchWeatherByCoordinates(lat, lon, lang, apiKey)

    }


    private fun getWeatherAndSave(city: String) {
        val lang = SettingsManager.getLanguage(this)
        val apiKey = BuildConfig.OPENWEATHER_API_KEY
        viewModel.fetchWeather(city, lang, apiKey)
    }


    private fun updateUI(info: WeatherInfo) {
        binding.textCityName.text = info.cityName
        binding.textTemperature.text = getString(R.string.temperature_format, info.temperature)
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

        return MainViewModelFactory(
            application,
            getSavedCitiesUseCase = GetSavedCitiesUseCase(cityRepository),
            saveCityUseCase = SaveCityUseCase(cityRepository),
            deleteCityUseCase = DeleteCityUseCase(cityRepository),
            fetchWeatherUseCase = FetchWeatherUseCase(weatherRepository)         )
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

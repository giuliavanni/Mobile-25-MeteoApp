package com.corsolp.ui.mainactivity

import android.Manifest
import android.app.AlertDialog
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
import com.corsolp.data.WeatherInfo
import com.corsolp.data.database.CityEntity
import com.corsolp.domain.settings.SettingsManager
import com.corsolp.ui.BuildConfig
import com.corsolp.ui.R
import com.corsolp.ui.forecast.ForecastActivity
import com.corsolp.ui.databinding.ActivityMainBinding
import com.corsolp.ui.map.MapActivity
import com.corsolp.ui.settings.SettingsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cityAdapter: CityAdapter
    private val savedCities = mutableListOf<CityEntity>()

    private val viewModel: MainViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedLanguage()
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
                getWeather(city.name, city.latitude, city.longitude)
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
            savedCities.clear()
            savedCities.addAll(updatedCities)
            cityAdapter.notifyDataSetChanged()
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
                    getWeather("La tua posizione", location.latitude, location.longitude)
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

    private fun getWeather(cityName: String, lat: Double, lon: Double) {
        val lang = SettingsManager.getLanguage(this)
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=${BuildConfig.OPENWEATHER_API_KEY}&units=metric&lang=$lang"

        lifecycleScope.launch {
            fetchWeatherData(
                url,
                onSuccess = { info -> updateUI(info) },
                onError = { error -> showError(error) }
            )
        }
    }

    private fun getWeatherAndSave(city: String) {
        val lang = SettingsManager.getLanguage(this)
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=${BuildConfig.OPENWEATHER_API_KEY}&units=metric&lang=$lang"

        lifecycleScope.launch {
            fetchWeatherData(
                url,
                onSuccess = { info ->
                    viewModel.saveCity(CityEntity(info.cityName, info.latitude, info.longitude))
                    updateUI(info)
                },
                onError = { error -> showError(error) }
            )
        }
    }

    private suspend fun fetchWeatherData(
        url: String,
        onSuccess: suspend (WeatherInfo) -> Unit,
        onError: suspend (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode

                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val weatherInfo = parseWeatherJson(json)
                    withContext(Dispatchers.Main) { onSuccess(weatherInfo) }
                } else {
                    withContext(Dispatchers.Main) {
                        onError(getString(R.string.error_http, responseCode))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(getString(R.string.error_generic, e.message ?: "Errore sconosciuto"))
                }
            }
        }
    }


    private fun parseWeatherJson(json: JSONObject): WeatherInfo {
        val cityName = json.getString("name")
        val temp = json.getJSONObject("main").getDouble("temp")
        val condition = json.getJSONArray("weather").getJSONObject(0).getString("description")
        val iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon")
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        val coord = json.getJSONObject("coord")
        val lat = coord.getDouble("lat")
        val lon = coord.getDouble("lon")

        return WeatherInfo(cityName, temp, condition, iconUrl, lat, lon)
    }

    private fun updateUI(info: WeatherInfo) {
        binding.textCityName.text = info.cityName
        binding.textTemperature.text = getString(R.string.temperature, info.temperature)
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

    private fun applySavedLanguage() {
        val lang = SettingsManager.getLanguage(this)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        // Applica la nuova configurazione
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

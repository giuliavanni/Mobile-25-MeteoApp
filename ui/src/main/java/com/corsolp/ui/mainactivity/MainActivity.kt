package com.corsolp.ui.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.corsolp.data.database.CityEntity
import com.corsolp.ui.BuildConfig
import com.corsolp.ui.databinding.ActivityMainBinding
import com.corsolp.ui.map.MapActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cityAdapter: CityAdapter
    private val savedCities = mutableListOf<CityEntity>()

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.weatherIconImageView.visibility = View.GONE


        // RecyclerView
        cityAdapter = CityAdapter(savedCities,
            onItemClick = { city ->
                getWeather(city.name, city.latitude, city.longitude)
            },
            onItemLongClick = { city ->
                viewModel.deleteCity(city)
                Toast.makeText(this, "Città eliminata", Toast.LENGTH_SHORT).show()
            },
            onMapClick = { city -> openMapForCity(city)}
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

    private fun getWeather(cityName: String, latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiKey = BuildConfig.OPENWEATHER_API_KEY
                val url = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=metric")

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)

                    // Recupera le informazioni meteo
                    val temp = json.getJSONObject("main").getDouble("temp")
                    val condition = json.getJSONArray("weather").getJSONObject(0).getString("description")
                    val cityName = json.getString("name")

                    // Recupera l'icona meteo (es. "01d", "02n")
                    val iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon")
                    val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png" // URL dell'icona

                    // Imposta il risultato meteo nella TextView
                    val result = "$cityName: %.1f°C, %s".format(temp, condition)

                    // Aggiorna l'interfaccia utente
                    withContext(Dispatchers.Main) {
                        binding.weatherResultText.text = result

                        binding.weatherIconImageView.visibility = View.VISIBLE

                        // Carica l'icona meteo
                        Glide.with(this@MainActivity)
                            .load(iconUrl)
                            .into(binding.weatherIconImageView)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.weatherResultText.text = "Errore HTTP: $responseCode"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.weatherResultText.text = "Errore: ${e.message}"
                }
            }
        }
    }


    private fun getWeatherAndSave(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiKey = BuildConfig.OPENWEATHER_API_KEY
                val url = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=it")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val temp = json.getJSONObject("main").getDouble("temp")
                    val condition = json.getJSONArray("weather").getJSONObject(0).getString("description")
                    val cityName = json.getString("name")
                    val coord = json.getJSONObject("coord")
                    val lat = coord.getDouble("lat")
                    val lon = coord.getDouble("lon")

                    val cityEntity = CityEntity(name = cityName, latitude = lat, longitude = lon)
                    viewModel.saveCity(cityEntity)

                    val result = "$cityName: %.1f°C, %s".format(temp, condition)

                    withContext(Dispatchers.Main) {
                        binding.weatherResultText.text = result
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.weatherResultText.text = "Errore HTTP: $responseCode"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.weatherResultText.text = "Errore: ${e.message}"
                }
            }
        }
    }

    private fun openMapForCity(city: CityEntity) {
        val intent = Intent(this, MapActivity::class.java).apply {
            putExtra("city_name", city.name)
            putExtra("latitude", city.latitude)
            putExtra("longitude", city.longitude)
        }
        startActivity(intent)
    }
}

package com.corsolp.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

import android.widget.ArrayAdapter
import android.widget.Toast

import com.corsolp.data.database.AppDatabase
import com.corsolp.data.database.CityEntity

import com.corsolp.ui.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var cityEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var resultTextView: TextView

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: ArrayAdapter<CityEntity>
    private val savedCities = mutableListOf<CityEntity>()

    //private val apiKey = "OPENWEATHER_API_KEY"
    private val apiHost = "open-weather13.p.rapidapi.com"

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityEditText = findViewById(R.id.cityEditText)
        searchButton = findViewById(R.id.searchButton)
        resultTextView = findViewById(R.id.weatherResultText)

        //searchButton.setOnClickListener {
        //    val city = cityEditText.text.toString().trim()
        //    if (city.isNotEmpty()) {
        //        getWeather(city)
        //    } else {
        //        Toast.makeText(this, "Inserisci una città", Toast.LENGTH_SHORT).show()
        //    }
        //}

        // Inizializza ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inizializza Room
        database = AppDatabase.getDatabase(this)

        // Imposta adapter per la lista
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, savedCities)
        binding.savedCitiesListView.adapter = adapter

        loadSavedCities()

        binding.searchButton.setOnClickListener {
            val city = binding.cityEditText.text.toString().trim()
            if (city.isNotEmpty()) {
                getWeatherAndSave(city)
            } else {
                Toast.makeText(this, "Inserisci una città", Toast.LENGTH_SHORT).show()
            }
        }

        binding.savedCitiesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = savedCities[position]
            //binding.cityEditText.setText(selectedCity)
            getWeather(selectedCity.name, selectedCity.latitude, selectedCity.longitude)
        }

        binding.savedCitiesListView.setOnItemLongClickListener { _, _, position, _ ->
            // Ottieni la città selezionata
            val cityToDelete = savedCities[position]

            // Esegui la cancellazione in un Coroutine
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Cancella la città dal database
                    database.cityDao().deleteCity(cityToDelete)

                    // Rimuovi la città dalla lista 'savedCities'
                    val updatedCities = database.cityDao().getAllCities()

                    // Aggiorna l'interfaccia utente sul thread principale
                    withContext(Dispatchers.Main) {
                        savedCities.clear()
                        savedCities.addAll(updatedCities)
                        // Notifica l'adapter di aggiornare la lista
                        //(binding.savedCitiesListView.adapter as ArrayAdapter<CityEntity>).notifyDataSetChanged()
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_list_item_1,
                            savedCities.map { it.name }
                        )
                        binding.savedCitiesListView.adapter = adapter
                        Toast.makeText(applicationContext, "Città eliminata", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Errore nella cancellazione: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            true
        }

        (binding.savedCitiesListView.adapter as? ArrayAdapter<CityEntity>)?.let {
            it.notifyDataSetChanged()
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

                    val temp = json.getJSONObject("main").getDouble("temp")
                    val condition = json.getJSONArray("weather").getJSONObject(0).getString("description")
                    val cityName = json.getString("name")

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

    private fun getWeatherAndSave(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiKey =  BuildConfig.OPENWEATHER_API_KEY
                val url = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric")
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
                    database.cityDao().insertCity(cityEntity)

                    val result = "$cityName: %.1f°C, %s".format(temp, condition)

                    withContext(Dispatchers.Main) {
                        binding.weatherResultText.text = result
                        loadSavedCities()
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

    private fun loadSavedCities() {
        CoroutineScope(Dispatchers.IO).launch {
            val cities = database.cityDao().getAllCities()
            //val cityNames = cities.map { "${it.name} (${it.latitude}, ${it.longitude})" }
            savedCities.clear()
            savedCities.addAll(cities)

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    savedCities.map { it.name }
                )
                binding.savedCitiesListView.adapter = adapter
            }
        }
    }
}





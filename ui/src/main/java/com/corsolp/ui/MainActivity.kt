package com.corsolp.ui


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var cityEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var resultTextView: TextView

    private val apiKey = "OPENWEATHER_API_KEY"
    private val apiHost = "open-weather13.p.rapidapi.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityEditText = findViewById(R.id.cityEditText)
        searchButton = findViewById(R.id.searchButton)
        resultTextView = findViewById(R.id.weatherResultText)

        searchButton.setOnClickListener {
            val city = cityEditText.text.toString().trim()
            if (city.isNotEmpty()) {
                getWeather(city)
            } else {
                Toast.makeText(this, "Inserisci una città", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeather(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiKey = "7d99ee59c5d8aacd2292032f07f69e3e"
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

                    val result = "$cityName: %.1f°C, %s".format(temp, condition)

                    withContext(Dispatchers.Main) {
                        resultTextView.text = result
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        resultTextView.text = "Errore HTTP: $responseCode"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultTextView.text = "Errore: ${e.message}"
                }
            }
        }
    }


}


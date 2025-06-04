package com.corsolp.data.repository

import com.corsolp.domain.model.WeatherInfo
import com.corsolp.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherRepositoryImpl : WeatherRepository {

    override suspend fun fetchWeatherByCity(city: String, lang: String, apiKey: String): Result<WeatherInfo> {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=$lang"
        return fetch(url)
    }

    override suspend fun fetchWeatherByCoordinates(lat: Double, lon: Double, lang: String, apiKey: String): Result<WeatherInfo> {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric&lang=$lang"
        return fetch(url)
    }

    private suspend fun fetch(url: String): Result<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)

                    val weatherInfo = WeatherInfo(
                        cityName = json.getString("name"),
                        temperature = json.getJSONObject("main").getDouble("temp"),
                        description = json.getJSONArray("weather").getJSONObject(0).getString("description"),
                        iconUrl = "https://openweathermap.org/img/wn/${
                            json.getJSONArray("weather").getJSONObject(0).getString("icon")
                        }@2x.png",
                        latitude = json.getJSONObject("coord").getDouble("lat"),
                        longitude = json.getJSONObject("coord").getDouble("lon")
                    )

                    Result.success(weatherInfo)
                } else {
                    Result.failure(Exception("Errore HTTP: ${connection.responseCode}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

package com.corsolp.data.repository

import com.corsolp.domain.model.ForecastItem
import com.corsolp.domain.repository.ForecastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ForecastRepositoryImpl : ForecastRepository {
    override suspend fun getForecast(city: String, lang: String, apiKey: String): Result<List<ForecastItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey&units=metric&lang=$lang"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val list = json.getJSONArray("list")
                    val forecastItems = mutableListOf<ForecastItem>()

                    for (i in 0 until list.length()) {
                        val item = list.getJSONObject(i)
                        val main = item.getJSONObject("main")
                        val weather = item.getJSONArray("weather").getJSONObject(0)
                        val wind = item.getJSONObject("wind")

                        forecastItems.add(
                            ForecastItem(
                                date = item.getString("dt_txt"),
                                temp = main.getDouble("temp"),
                                description = weather.getString("description"),
                                iconUrl = "https://openweathermap.org/img/wn/${weather.getString("icon")}@2x.png",
                                humidity = main.getInt("humidity"),
                                pressure = main.getInt("pressure"),
                                windSpeed = wind.getDouble("speed")
                            )
                        )
                    }
                    Result.success(forecastItems)
                } else {
                    Result.failure(IOException("HTTP error code: ${connection.responseCode}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
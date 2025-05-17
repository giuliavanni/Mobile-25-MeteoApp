package com.corsolp.ui.forecast

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corsolp.data.ForecastItem
import com.corsolp.domain.settings.SettingsManager
import com.corsolp.ui.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale


class ForecastViewModel : ViewModel() {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    private val _forecastList = MutableLiveData<List<ForecastItem>>()
    val forecastList: LiveData<List<ForecastItem>> get() = _forecastList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadForecast(city: String, context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lang = SettingsManager.getLanguage(context)
                val encodedCity = URLEncoder.encode(city, "UTF-8")
                val urlString =
                    "https://api.openweathermap.org/data/2.5/forecast?q=$encodedCity&appid=$apiKey&units=metric&lang=$lang"

                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()
                connection.disconnect()

                val forecastItems = parseForecast(response.toString())

                _forecastList.postValue(forecastItems)

            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue("Errore: ${e.message}")
            }
        }
    }

    private fun parseForecast(json: String): List<ForecastItem> {
        val list = mutableListOf<ForecastItem>()
        val jsonObject = JSONObject(json)
        val forecastArray = jsonObject.getJSONArray("list")

        for (i in 0 until forecastArray.length() step 8) {
            val item = forecastArray.getJSONObject(i)
            //val date = item.getString("dt_txt")
            val inputDate = item.getString("dt_txt")
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE dd MMMM", Locale("it"))
            val date = outputFormat.format(inputFormat.parse(inputDate)!!)
            val temp = item.getJSONObject("main").getDouble("temp")
            val desc = item.getJSONArray("weather")
                .getJSONObject(0)
                .getString("description")
            val iconCode = item.getJSONArray("weather").getJSONObject(0).getString("icon")
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            list.add(ForecastItem(date, temp, desc, iconUrl))
        }

        return list
    }
}

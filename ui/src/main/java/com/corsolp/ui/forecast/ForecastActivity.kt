package com.corsolp.ui.forecast

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.corsolp.domain.di.UseCaseProvider
import com.corsolp.ui.BaseActivity
import com.corsolp.ui.BuildConfig
import com.corsolp.ui.R
import com.corsolp.ui.detailedforecast.DetailedForecastActivity


class ForecastActivity : BaseActivity() {

    private lateinit var forecastRecyclerView: RecyclerView
    private lateinit var dailyForecastAdapter: DailyForecastAdapter
    private lateinit var viewModel: ForecastViewModel
    private lateinit var cityNameTextView: TextView

    private lateinit var city: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        // Inizializza UI
        forecastRecyclerView = findViewById(R.id.forecastRecyclerView)
        cityNameTextView = findViewById(R.id.cityNameTextView)

        // Recupera la città
        city = intent.getStringExtra("city_name") ?: ""
        cityNameTextView.text = getString(R.string.city_name_format, city)

        if (city.isEmpty()) {
            Toast.makeText(this, "Nessuna città ricevuta.", Toast.LENGTH_SHORT).show()
            return
        }

        // Imposta il RecyclerView con il listener click
        dailyForecastAdapter = DailyForecastAdapter(emptyList()) { dailyForecast ->
            // Intent per aprire l'activity dettagliata
            val filteredList = viewModel.forecastList.value?.filter {
                it.date.startsWith(dailyForecast.date)
            } ?: emptyList()

            if (filteredList.isNotEmpty()) {
                val intent = Intent(this, DetailedForecastActivity::class.java)
                intent.putParcelableArrayListExtra("detailed_forecast_list", ArrayList(filteredList))
                intent.putExtra("date_full", dailyForecast.date)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Nessun dettaglio disponibile", Toast.LENGTH_SHORT).show()
            }
        }
        forecastRecyclerView.layoutManager = LinearLayoutManager(this)
        forecastRecyclerView.adapter = dailyForecastAdapter

        // Inizializza ViewModel
        viewModel = ForecastViewModel(
            fetchForecastUseCase = UseCaseProvider.fetchForecastUseCase,
            getAppLanguageUseCase = UseCaseProvider.getAppLanguageUseCase
        )

        // Osserva i dati meteo
        viewModel.dailyForecastList.observe(this) { summarized ->
            dailyForecastAdapter.updateData(summarized)
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Chiamata API
        val apiKey = BuildConfig.OPENWEATHER_API_KEY
        viewModel.loadForecast(city, apiKey)
    }

}


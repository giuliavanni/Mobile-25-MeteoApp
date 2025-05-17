package com.corsolp.ui.forecast

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.corsolp.ui.R


class ForecastActivity : AppCompatActivity() {

    private lateinit var forecastRecyclerView: RecyclerView
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var viewModel: ForecastViewModel
    private lateinit var cityNameTextView: TextView

    private lateinit var city: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        forecastRecyclerView = findViewById(R.id.forecastRecyclerView)
        forecastAdapter = ForecastAdapter(emptyList())
        forecastRecyclerView.layoutManager = LinearLayoutManager(this)
        forecastRecyclerView.adapter = forecastAdapter

        city = intent.getStringExtra("city_name") ?: ""

        cityNameTextView = findViewById(R.id.cityNameTextView)
        cityNameTextView.text = getString(R.string.city_name_format, city)

        if (city.isEmpty()) {
            Toast.makeText(this, "Nessuna città ricevuta.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel = ViewModelProvider(this)[ForecastViewModel::class.java]

        viewModel.forecastList.observe(this) { forecastItems ->
            forecastAdapter.updateData(forecastItems)
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.loadForecast(city, this)
    }
}




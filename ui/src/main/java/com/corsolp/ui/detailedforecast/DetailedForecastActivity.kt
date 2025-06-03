package com.corsolp.ui.detailedforecast

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.corsolp.domain.model.ForecastItem
import com.corsolp.ui.R
import com.corsolp.ui.forecast.ForecastViewModel
import com.corsolp.ui.forecast.ForecastViewModelFactory
import java.util.Locale

class DetailedForecastActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ThreeHourForecastAdapter
    private lateinit var textDateFull: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_forecast)

        recyclerView = findViewById(R.id.detailedForecastRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        textDateFull = findViewById(R.id.textDateFull)

        val date = intent.getStringExtra("date_full") ?: ""

        // Formatta la data (es. Martedì 4 Giugno)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())

        val parsedDate = inputFormat.parse(date)
        val formattedDate = if (parsedDate != null) {
            outputFormat.format(parsedDate).replaceFirstChar { it.uppercaseChar() }
        } else date

        textDateFull.text = formattedDate

        val forecastList = intent.getParcelableArrayListExtra<ForecastItem>("detailed_forecast_list")

        if (forecastList != null) {
            adapter = ThreeHourForecastAdapter(forecastList)
            recyclerView.adapter = adapter
        } else {
            Toast.makeText(this, "Nessun dettaglio disponibile", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
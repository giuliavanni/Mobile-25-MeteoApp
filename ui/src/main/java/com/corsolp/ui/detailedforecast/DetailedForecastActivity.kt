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
import com.corsolp.ui.BaseActivity
import com.corsolp.ui.R
import com.corsolp.ui.forecast.ForecastViewModel
import com.corsolp.ui.forecast.ForecastViewModelFactory
import java.util.Locale

class DetailedForecastActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ThreeHourForecastAdapter
    private lateinit var textDateFull: TextView
    private lateinit var viewModel: DetailedForecastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_forecast)

        recyclerView = findViewById(R.id.detailedForecastRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        textDateFull = findViewById(R.id.textDateFull)

        viewModel = ViewModelProvider(this)[DetailedForecastViewModel::class.java]

        // Prende i dati dall'Intent
        val date = intent.getStringExtra("date_full") ?: ""
        val forecastList = intent.getParcelableArrayListExtra<ForecastItem>("detailed_forecast_list") ?: emptyList()

        if (forecastList.isEmpty()) {
            Toast.makeText(this, "Nessun dettaglio disponibile", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Carica i dati nel ViewModel
        viewModel.loadData(date, forecastList)

        // Osserva i dati formattati e la lista
        viewModel.formattedDate.observe(this) { formattedDate ->
            textDateFull.text = formattedDate
        }

        viewModel.forecastList.observe(this) { list ->
            adapter = ThreeHourForecastAdapter(list)
            recyclerView.adapter = adapter
        }
    }
}

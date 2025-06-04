package com.corsolp.ui.detailedforecast

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.corsolp.domain.model.ForecastItem
import java.util.Locale

class DetailedForecastViewModel : ViewModel() {

    private val _formattedDate = MutableLiveData<String>()
    val formattedDate: LiveData<String> = _formattedDate

    private val _forecastList = MutableLiveData<List<ForecastItem>>()
    val forecastList: LiveData<List<ForecastItem>> = _forecastList

    fun loadData(dateString: String, rawForecastList: List<ForecastItem>) {
        // Formatta la data
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())

        val parsedDate = inputFormat.parse(dateString)
        val formatted = if (parsedDate != null) {
            outputFormat.format(parsedDate).replaceFirstChar { it.uppercaseChar() }
        } else dateString

        _formattedDate.value = formatted
        _forecastList.value = rawForecastList
    }
}

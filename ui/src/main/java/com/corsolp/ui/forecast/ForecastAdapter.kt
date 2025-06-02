package com.corsolp.ui.forecast

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.corsolp.domain.model.ForecastItem
import com.corsolp.ui.R
import com.corsolp.ui.utils.TemperatureUtils
import java.util.Locale

class ForecastAdapter(
    private var forecastList: List<ForecastItem>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val descTextView: TextView = itemView.findViewById(R.id.descTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val humidityTextView: TextView = itemView.findViewById(R.id.textHumidity)
        private val pressureTextView: TextView = itemView.findViewById(R.id.textPressure)
        private val windTextView: TextView = itemView.findViewById(R.id.textWind)

        fun bind(forecast: ForecastItem) {
            //dateTextView.text = forecast.date
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentLocale = itemView.context.resources.configuration.locales[0]
            val outputDateFormat = SimpleDateFormat("EEEE dd MMMM", currentLocale)
            val outputTimeFormat = SimpleDateFormat("HH:mm", currentLocale)


            val parsedDate = inputFormat.parse(forecast.date)
            val formattedDate = outputDateFormat.format(parsedDate!!)
            val formattedTime = outputTimeFormat.format(parsedDate)

            dateTextView.text = itemView.context.getString(R.string.forecast_date_format, formattedDate, formattedTime)

            val (convertedTemp, unitSymbol) = TemperatureUtils.convertTemperature(itemView.context, forecast.temp)
            tempTextView.text = String.format(currentLocale, "%.1f %s", convertedTemp, unitSymbol)

            descTextView.text = forecast.description
            humidityTextView.text = itemView.context.getString(R.string.humidity_format, forecast.humidity)
            pressureTextView.text = itemView.context.getString(R.string.pressure_format, forecast.pressure)
            windTextView.text = itemView.context.getString(R.string.wind_format, forecast.windSpeed)

            Glide.with(itemView)
                .load(forecast.iconUrl)
                .into(iconImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(forecastList[position])
    }

    override fun getItemCount(): Int = forecastList.size

    // Metodo per aggiornare i dati
    fun updateData(newList: List<ForecastItem>) {
        forecastList = newList
        notifyDataSetChanged()
    }
}
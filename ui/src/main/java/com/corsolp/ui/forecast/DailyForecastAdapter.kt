package com.corsolp.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.corsolp.domain.model.DailyForecast
import com.bumptech.glide.Glide
import com.corsolp.ui.R
import com.corsolp.ui.databinding.ItemDailyForecastBinding
import com.corsolp.ui.utils.TemperatureUtils
import java.text.SimpleDateFormat
import java.util.*

class DailyForecastAdapter(
    private var dailyForecasts: List<DailyForecast>,
    private val onItemClick: (DailyForecast) -> Unit
) : RecyclerView.Adapter<DailyForecastAdapter.DailyForecastViewHolder>() {

    inner class DailyForecastViewHolder(val binding: ItemDailyForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(dailyForecasts[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = ItemDailyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DailyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val forecast = dailyForecasts[position]
        val binding = holder.binding

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
        val date = inputFormat.parse(forecast.date)
        val formattedDate = if (date != null) outputFormat.format(date) else forecast.date

        binding.textDate.text = formattedDate

        val (convertedTemp, unit) = TemperatureUtils.convertTemperature(binding.root.context, forecast.avgTemp)
        binding.textAvgTemp.text = binding.root.context.getString(R.string.temperature_display, convertedTemp, unit)

        Glide.with(binding.root.context)
            .load(forecast.iconUrl)
            .into(binding.imageWeatherIcon)
    }

    override fun getItemCount(): Int = dailyForecasts.size

    fun updateData(newData: List<DailyForecast>) {
        dailyForecasts = newData
        notifyDataSetChanged()
    }
}



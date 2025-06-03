package com.corsolp.ui.detailedforecast

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
import com.corsolp.ui.databinding.ItemThreeHourForecastBinding
import com.corsolp.ui.utils.TemperatureUtils
import java.util.Locale

class ThreeHourForecastAdapter(
    private val items: List<ForecastItem>
) : RecyclerView.Adapter<ThreeHourForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeText: TextView = itemView.findViewById(R.id.textTime)
        val iconImage: ImageView = itemView.findViewById(R.id.imageWeatherIcon)
        val tempText: TextView = itemView.findViewById(R.id.textTemp)
        val descriptionText: TextView = itemView.findViewById(R.id.textDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_three_hour_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = items[position]

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val parsedDate = inputFormat.parse(item.date)
        holder.timeText.text = if (parsedDate != null) outputFormat.format(parsedDate) else item.date

        val (tempConverted, unit) = TemperatureUtils.convertTemperature(holder.itemView.context, item.temp)
        holder.tempText.text = String.format(Locale.getDefault(), "%.1f %s", tempConverted, unit)

        holder.descriptionText.text = item.description

        Glide.with(holder.itemView.context)
            .load(item.iconUrl)
            .into(holder.iconImage)
    }

    override fun getItemCount(): Int = items.size
}



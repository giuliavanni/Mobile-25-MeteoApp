package com.corsolp.ui.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.corsolp.data.ForecastItem
import com.corsolp.ui.R

class ForecastAdapter(
    private var forecastList: List<ForecastItem>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val descTextView: TextView = itemView.findViewById(R.id.descTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)

        fun bind(forecast: ForecastItem) {
            dateTextView.text = forecast.date
            tempTextView.text = "${forecast.temp}°C"
            descTextView.text = forecast.description

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
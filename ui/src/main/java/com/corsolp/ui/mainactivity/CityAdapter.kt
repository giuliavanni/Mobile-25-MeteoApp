package com.corsolp.ui.mainactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.corsolp.data.database.CityEntity
import com.corsolp.data.mapper.toDomain
import com.corsolp.domain.model.City
import com.corsolp.ui.R

class CityAdapter(
    private val cities: List<CityEntity>,
    private val onItemClick: (City) -> Unit,
    private val onItemLongClick: (City) -> Unit,
    private val onMapClick: (City) -> Unit,
    private val onForecastClick: (City) -> Unit,
    private val onFavoriteToggle: (City) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
        private val mapButton: View = itemView.findViewById(R.id.openMapButton)
        private val forecastButton: View = itemView.findViewById(R.id.buttonForecast)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)

        fun bind(city: City) {
            cityNameTextView.text = city.name

            val iconRes = if (city.isFavorite) R.drawable.ic_heart_fill else R.drawable.ic_heart_outline
            favoriteIcon.setImageResource(iconRes)

            favoriteIcon.setOnClickListener {
                onFavoriteToggle(city)
            }

            itemView.setOnClickListener {
                onItemClick(city)
            }

            itemView.setOnLongClickListener {
                onItemLongClick(city)
                true
            }

            mapButton.setOnClickListener {
                onMapClick(city)
            }

            forecastButton.setOnClickListener {
                onForecastClick(city)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position].toDomain()
        holder.bind(city)
    }

    override fun getItemCount(): Int = cities.size

}
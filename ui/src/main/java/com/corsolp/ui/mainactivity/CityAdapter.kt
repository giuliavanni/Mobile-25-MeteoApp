package com.corsolp.ui.mainactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.corsolp.data.database.CityEntity
import com.corsolp.ui.R

class CityAdapter(
    private val cities: List<CityEntity>,
    private val onItemClick: (CityEntity) -> Unit,
    private val onItemLongClick: (CityEntity) -> Unit,
    private val onMapClick: (CityEntity) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
        private val mapButton: View = itemView.findViewById(R.id.openMapButton)

        fun bind(city: CityEntity) {
            cityNameTextView.text = city.name

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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size
}
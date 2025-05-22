package com.corsolp.ui.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.corsolp.ui.R
import com.corsolp.ui.databinding.ItemListApartmentBinding
import com.corsolp.ui.databinding.ItemListHotelBinding
import com.corsolp.domain.model.AccomodationType

//Versione 3a ora
class AccomodationAdapter(
    private val dataSet: MutableList<AccomodationType>
) : RecyclerView.Adapter<AccomodationAdapter.ViewHolder>() {

    fun updateList(newList: List<AccomodationType>) {
        dataSet.clear()
        dataSet.addAll(newList)
        notifyDataSetChanged()
    }

    //VERSIONE con ViewBinding
    //ViewBinding
    abstract class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        abstract fun bind(accomodationType: AccomodationType)
    }

    class ViewHolderHotel(private val binding: ItemListHotelBinding) :
        ViewHolder(binding.root) {

        override fun bind(accomodationType: AccomodationType) {
            binding.txtAccomodationType.text = when (accomodationType) {
                is AccomodationType.Hotel -> itemView.context.getString(R.string.hotel)
                is AccomodationType.Apartment -> itemView.context.getString(R.string.apartment)
            }
            binding.txtAccomodationName.text = accomodationType.name
            binding.txtAccomodationDescription.text = accomodationType.description
            binding.txtScore.text = accomodationType.score.toString()
//            binding.txtScore.setButtonText(accomodationType.score.toString())
//            binding.txtScore.setButtonIcon(R.drawable.ic_international)

            Glide.with(itemView.context)
                .load(accomodationType.pictureUrl)
                .into(binding.accomodationImage)
        }
    }

    class ViewHolderAp(private val binding: ItemListApartmentBinding) :
        ViewHolder(binding.root) {

        override fun bind(accomodationType: AccomodationType) {
            binding.txtAccomodationType.text = when (accomodationType) {
                is AccomodationType.Hotel -> itemView.context.getString(R.string.hotel)
                is AccomodationType.Apartment -> itemView.context.getString(R.string.apartment)
            }
            binding.txtAccomodationName.text = accomodationType.name
            binding.txtAccomodationDescription.text = accomodationType.description
            binding.txtCustomButton.setButtonText(accomodationType.score.toString())
            binding.txtCustomButton.setButtonIcon(R.drawable.ic_international)

            Glide.with(itemView.context)
                .load(accomodationType.pictureUrl)
                .into(binding.accomodationImage)
        }
    }


    override fun getItemViewType(position: Int): Int {
        val accomodationType = dataSet[position]
        return when (accomodationType) {
            is AccomodationType.Hotel -> 1
            is AccomodationType.Apartment -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            1 -> {
                val binding = ItemListHotelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolderHotel(binding)
            }

            else -> {
                val binding = ItemListApartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolderAp(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val accomodation = dataSet[position]
        holder.bind(accomodationType = accomodation)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}


/*

Versione 1 e 2 ora

class AccomodationAdapter(
    private val dataSet: List<AccomodationType>
): RecyclerView.Adapter<AccomodationAdapter.ViewHolder>() {

    //VERSIONE con findViewById
//    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
//        val txtAccomodationType = view.findViewById<TextView>(R.id.txtAccomodationType)
//        val txtAccomodationName = view.findViewById<TextView>(R.id.txtAccomodationName)
//        val txtAccomodationDescription = view.findViewById<TextView>(R.id.txtAccomodationDescription)
//        val accomodationImage = view.findViewById<ImageView>(R.id.accomodationImage)
//        val txtScore = view.findViewById<TextView>(R.id.txtScore)
//    }

    //VERSIONE con ViewBinding
    class ViewHolder(private val itemListHotelBinding: ItemListHotelBinding): RecyclerView.ViewHolder(itemListHotelBinding.root) {

        fun bind(accomodationType: AccomodationType) {
            itemListHotelBinding.txtAccomodationType.text = when (accomodationType) {
                is AccomodationType.Hotel -> itemListHotelBinding.root.context.getString(R.string.hotel)
                is AccomodationType.Apartment -> itemListHotelBinding.root.context.getString(R.string.apartment)
            }
            itemListHotelBinding.txtAccomodationName.text = accomodationType.name
            itemListHotelBinding.txtAccomodationDescription.text = accomodationType.description
            itemListHotelBinding.txtScore.text = accomodationType.score.toString()

            Glide.with(itemListHotelBinding.root.context).
                load(accomodationType.pictureUrl).
                into(itemListHotelBinding.accomodationImage)

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccomodationAdapter.ViewHolder {
        //VERSIONE con ViewBinding
        val view = ItemListHotelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)

        //VERSIONE con findViewById
//        val view = LayoutInflater.from(
//            parent.context
//        ).inflate(
//            R.layout.item_list_hotel,
//            parent,
//            false
//        )
//        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccomodationAdapter.ViewHolder, position: Int) {
        val accomodation = dataSet[position]

        //VERSIONE con ViewBinding
        holder.bind(accomodation)

        //VERSIONE con findViewById
//        holder.txtAccomodationType.text = when(accomodation) {
//            is AccomodationType.Hotel -> holder.itemView.context.getString(R.string.hotel)
//            is AccomodationType.Apartment -> holder.itemView.context.getString(R.string.apartment)
//        }
//        holder.txtAccomodationName.text = accomodation.name
//        holder.txtAccomodationDescription.text = accomodation.description
//        holder.txtScore.text = accomodation.score.toString()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}




 */

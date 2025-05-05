package com.corsolp.data.repository

import com.corsolp.domain.models.AccomodationType
import com.corsolp.domain.repository.AccomodationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccomodationRepositoryImpl: AccomodationRepository {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _accomodationTypeList = MutableStateFlow<List<AccomodationType>>(listOf())
    override val accomodationTypeList: StateFlow<List<AccomodationType>> = _accomodationTypeList

    private val imgParisBase64 =
        "data:image/jpeg;base64,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"

    private val imgUrl = "https://picsum.photos/200/300"

    private val imgUrlAlce = "https://fastly.picsum.photos/id/577/200/200.jpg?hmac=o426LHIb2MgD4TJnSfBnhpGdOTOdofsp1A7yraIpo-Y"

    private val accomodationTypes = listOf(
        AccomodationType.Hotel(
            hotelName = "Hotel Rosalba",
            hotelDescription = "Questa è la descrizione dell'hotel Rosalba che si trova a Cesena, in via Marina 4",
            hotelPictureUrl = imgUrl,
            hotelScore = 8.4
        ),
        AccomodationType.Hotel(
            hotelName = "Hotel Internazionale",
            hotelDescription = "Questa è la descrizione dell'hotel Internazionale che si trova a Cesena, in via Marina 4",
            hotelPictureUrl = imgUrl,
            hotelScore = 7.5
        ),

        AccomodationType.Apartment(
            apartmentName = "Appartamenti Sul Mare",
            apartmentDescription = "Questa è la descrizione degli appartamenti sul Mare che si trova a Cesena, in via Marina 4",
            apartmentPictureUrl = imgUrlAlce,
            apartmentScore = 9.8
        ),

        AccomodationType.Hotel(
            hotelName = "Hotel Cesare",
            hotelDescription = "Questa è la descrizione dell'hotel Cesare che si trova a Cesena, in via Marina 4",
            hotelPictureUrl = imgUrl,
            hotelScore = 3.8
        ),

        AccomodationType.Apartment(
            apartmentName = "Appartamenti Nuovi",
            apartmentDescription = "Questa è la descrizione degli appartamenti Nuovi che si trova a Cesena, in via Marina 4",
            apartmentPictureUrl = imgParisBase64,
            apartmentScore = 7.2
        ),

        AccomodationType.Hotel(
            hotelName = "Hotel Majestic",
            hotelDescription = "Questa è la descrizione dell'hotel Majestic che si trova a Cesena, in via Marina 4",
            hotelPictureUrl = imgUrl,
            hotelScore = 8.0
        ),

        AccomodationType.Apartment(
            apartmentName = "Appartamenti Cesira",
            apartmentDescription = "Questa è la descrizione degli appartamenti Cesira che si trova a Cesena, in via Marina 4",
            apartmentPictureUrl = imgParisBase64,
            apartmentScore = 6.7
        ),
    )

    override fun start() {
        fetchAccomodationTypeList()
    }

    override fun fetchAccomodationTypeList() {
        scope.launch {
            _accomodationTypeList.emit(accomodationTypes)
        }
    }
}
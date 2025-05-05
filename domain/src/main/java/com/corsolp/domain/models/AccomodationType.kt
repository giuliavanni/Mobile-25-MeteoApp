package com.corsolp.domain.models

sealed class AccomodationType(
    val name: String,
    val description: String,
    val pictureUrl: String,
    val score: Double
) {
    data class Hotel(
        private val hotelName: String,
        private val hotelDescription: String,
        private val hotelPictureUrl: String,
        private val hotelScore: Double
    ): AccomodationType(
        name = hotelName,
        description = hotelDescription,
        pictureUrl = hotelPictureUrl,
        score = hotelScore
    )

    data class Apartment(
        private val apartmentName: String,
        private val apartmentDescription: String,
        private val apartmentPictureUrl: String,
        private val apartmentScore: Double
    ): AccomodationType(
        name = apartmentName,
        description = apartmentDescription,
        pictureUrl = apartmentPictureUrl,
        score = apartmentScore
    )
}
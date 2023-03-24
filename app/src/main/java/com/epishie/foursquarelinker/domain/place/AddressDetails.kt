package com.epishie.foursquarelinker.domain.place

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressDetails(
    @SerialName("geocodes")
    val geocodes: Geocodes
) {
    @Serializable
    data class Geocodes(
        @SerialName("main")
        val main: Geocode
    )

    @Serializable
    data class Geocode(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double
    )
}

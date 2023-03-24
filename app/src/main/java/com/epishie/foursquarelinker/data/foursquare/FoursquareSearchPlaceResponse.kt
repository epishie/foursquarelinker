package com.epishie.foursquarelinker.data.foursquare

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoursquareSearchPlaceResponse(
    @SerialName("results")
    val results: List<Result>
) {
    @Serializable
    data class Result(
        @SerialName("fsq_id")
        val fsqId: String,
        @SerialName("name")
        val name: String
    )
}
package com.epishie.foursquarelinker.data.foursquare

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoursquareAutoCompleteResponse(
    @SerialName("results")
    val results: List<Result>
) {
    @Serializable
    data class Result(
        @SerialName("text")
        val text: Text,
        @SerialName("address")
        val address: Address
    )

    @Serializable
    data class Text(
        @SerialName("primary")
        val primary: String
    )

    @Serializable
    data class Address(
        @SerialName("address_id")
        val id: String
    )
}
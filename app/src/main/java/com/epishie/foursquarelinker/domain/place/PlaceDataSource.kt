package com.epishie.foursquarelinker.domain.place

import android.location.Location

interface PlaceDataSource {
    suspend fun search(keyword: String, location: Location, count: Int): SearchResponse

    suspend fun search(keyword: String, addressId: String, count: Int): SearchResponse

    suspend fun searchNext(next: String): SearchResponse

    suspend fun autoCompleteAddress(query: String): List<AutoCompleteAddress>

    data class SearchResponse(
        val places: List<Place>,
        val next: String?
    )
}
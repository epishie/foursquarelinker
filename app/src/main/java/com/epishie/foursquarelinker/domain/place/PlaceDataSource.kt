package com.epishie.foursquarelinker.domain.place

import android.location.Location

interface PlaceDataSource {
    suspend fun search(keyword: String, location: Location, count: Int): SearchResponse
    suspend fun searchNext(next: String): SearchResponse

    data class SearchResponse(
        val places: List<Place>,
        val next: String?
    )
}
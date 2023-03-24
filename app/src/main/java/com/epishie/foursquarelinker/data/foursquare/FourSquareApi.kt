package com.epishie.foursquarelinker.data.foursquare

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FourSquareApi {
    @GET("places/search")
    suspend fun searchPlace(
        @Query("query") query: String,
        @Query("ll") latLng: String,
        @Query("limit") limit: Int
    ): Response<FoursquareSearchPlaceResponse>

    @GET
    suspend fun searchPlaceNext(url: String): Response<FoursquareSearchPlaceResponse>

    companion object {
        const val LINK_REGEX = "<(.*)>; *rel=\"(.*)\""
    }
}
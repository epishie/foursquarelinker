package com.epishie.foursquarelinker.data.foursquare

import android.location.Location
import com.epishie.foursquarelinker.domain.place.AutoCompleteAddress
import com.epishie.foursquarelinker.domain.place.Place
import com.epishie.foursquarelinker.domain.place.PlaceDataSource
import com.epishie.foursquarelinker.domain.place.PlaceDataSource.SearchResponse
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FourSquarePlaceDataSource @Inject constructor(
    private val foursquareApi: FourSquareApi,
) : PlaceDataSource {
    private val linkRegex = FourSquareApi.LINK_REGEX.toRegex()

    override suspend fun search(keyword: String, location: Location, count: Int): SearchResponse {
        val response = foursquareApi.searchPlace(
            query = keyword,
            latLng = "${location.latitude.toFloat()},${location.longitude.toFloat()}",
            limit = count,
            sort = "distance"
        )
        return response.toSearchResponse() ?: throw IOException("Error: ${response.errorMessage()}")
    }

    override suspend fun search(keyword: String, addressId: String, count: Int): SearchResponse {
        val geocode = foursquareApi.getAddressDetails(addressId).geocodes.main
        val response = foursquareApi.searchPlace(
            query = keyword,
            latLng = "${geocode.latitude.toFloat()},${geocode.longitude.toFloat()}",
            limit = count,
            sort = "distance"
        )
        return response.toSearchResponse() ?: throw IOException("Error: ${response.errorMessage()}")
    }

    override suspend fun searchNext(next: String): SearchResponse {
        val response = foursquareApi.searchPlaceNext(next)
        return response.toSearchResponse() ?: throw IOException("Error: ${response.errorMessage()}")
    }

    override suspend fun autoCompleteAddress(query: String): List<AutoCompleteAddress> {
        val response = foursquareApi.autoComplete(query, "address")
        return response.results.map { result -> result.toAutoCompleteAddress() }
    }

    private fun Response<FoursquareSearchPlaceResponse>.toSearchResponse() =
        body()?.let { searchPlaceResponse ->
            SearchResponse(
                places = searchPlaceResponse.results.map { it.toPlace() },
                next = headers()["link"]?.let { link ->
                    linkRegex.findAll(link).firstOrNull {
                        it.groupValues.getOrNull(2) == "next"
                    }?.groupValues?.getOrNull(1)
                }
            )
        }

    private fun FoursquareSearchPlaceResponse.Result.toPlace() = Place(
        id = fsqId,
        name = name,
        address = location.address
    )

    private fun FoursquareAutoCompleteResponse.Result.toAutoCompleteAddress() = AutoCompleteAddress(
        id = address.id,
        text1 = text.primary,
        text2 = text.secondary
    )

    private fun <T> Response<T>.errorMessage(): String =
        "Code: ${code()} Message: ${this.message()}"
}
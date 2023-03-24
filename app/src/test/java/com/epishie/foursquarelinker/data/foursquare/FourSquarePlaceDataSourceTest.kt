package com.epishie.foursquarelinker.data.foursquare

import com.epishie.foursquarelinker.domain.place.Place
import com.epishie.foursquarelinker.domain.place.PlaceDataSource.SearchResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FourSquarePlaceDataSourceTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var fourSquareApi: FourSquareApi

    private lateinit var dataSource: FourSquarePlaceDataSource

    @Before
    fun setUp() {
        dataSource = FourSquarePlaceDataSource(fourSquareApi)
    }

    @Test
    fun searchReturnsResponseOnSuccess() = runTest {
        // Given
        coEvery {
            fourSquareApi.searchPlace(
                query = "test_query",
                latLng = "1.0,1.0",
                limit = 10
            )
        } returns Response.success(
            FoursquareSearchPlaceResponse(
                results = listOf(
                    FoursquareSearchPlaceResponse.Result(
                        fsqId = "fsq_id_1",
                        name = "Place 1"
                    )
                )
            ),
            Headers.headersOf(
                "link",
                "<https://api.foursquare.com/v3/places/search/next/2>; rel=\"next\""
            )
        )

        // When
        val searchResponse = dataSource.search(
            keyword = "test_query",
            location = mockk {
                every { latitude } returns 1.0
                every { longitude } returns 1.0
            },
            count = 10
        )

        // Then
        assertThat(searchResponse).isEqualTo(
            SearchResponse(
                places = listOf(Place(id = "fsq_id_1", "Place 1")),
                next = "https://api.foursquare.com/v3/places/search/next/2"
            )
        )
    }

    @Test
    fun searchThrowsErrorOnHttpError() = runTest {
        // Given
        coEvery {
            fourSquareApi.searchPlace(
                query = "test_query",
                latLng = "1.0,1.0",
                limit = 10
            )
        } returns Response.error(401, "".toResponseBody())

        // When
        val exception = kotlin.runCatching {
            dataSource.search(
                keyword = "test_query",
                location = mockk {
                    every { latitude } returns 1.0
                    every { longitude } returns 1.0
                },
                count = 10
            )
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(IOException::class.java)
    }

    @Test
    fun searchNextReturnsResponseOnSuccess() = runTest {
        // Given
        coEvery {
            fourSquareApi.searchPlaceNext(url = "https://api.foursquare.com/v3/places/search/next/1")
        } returns Response.success(
            FoursquareSearchPlaceResponse(
                results = listOf(
                    FoursquareSearchPlaceResponse.Result(
                        fsqId = "fsq_id_1",
                        name = "Place 1"
                    )
                )
            )
        )

        // When
        val searchResponse = dataSource.searchNext(
            next = "https://api.foursquare.com/v3/places/search/next/1"
        )

        // Then
        assertThat(searchResponse).isEqualTo(
            SearchResponse(
                places = listOf(Place(id = "fsq_id_1", "Place 1")),
                next = null
            )
        )
    }

    @Test
    fun searchNextThrowsErrorOnHttpError() = runTest {
        // Given
        coEvery {
            fourSquareApi.searchPlaceNext(url = "https://api.foursquare.com/v3/places/search/next/1")
        } returns Response.error(401, "".toResponseBody())

        // When
        val exception = kotlin.runCatching {
            dataSource.searchNext(next = "https://api.foursquare.com/v3/places/search/next/1")
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(IOException::class.java)
    }
}
package com.epishie.foursquarelinker.data.foursquare

import com.epishie.foursquarelinker.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import javax.inject.Inject

interface FourSquareApi {
    @GET("places/search")
    suspend fun searchPlace(
        @Query("query") query: String,
        @Query("ll") latLng: String,
        @Query("limit") limit: Int,
    ): Response<FoursquareSearchPlaceResponse>

    @GET
    suspend fun searchPlaceNext(@Url url: String): Response<FoursquareSearchPlaceResponse>

    @GET("autocomplete")
    suspend fun autoComplete(
        @Query("query") query: String,
        @Query("types") types: String,
    ): FoursquareAutoCompleteResponse

    companion object {
        const val LINK_REGEX = "<(.*)>; *rel=\"(.*)\""
    }

    class Impl private constructor(api: FourSquareApi) : FourSquareApi by api {
        @OptIn(ExperimentalSerializationApi::class)
        @Inject
        constructor() : this(
            Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            val request = chain.request()
                            chain.proceed(
                                request.newBuilder()
                                    .header(
                                        "Authorization",
                                        BuildConfig.FSQ_API_KEY
                                    )
                                    .build()
                            )
                        }
                        .build()
                )
                .baseUrl("https://api.foursquare.com/v3/")
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(FourSquareApi::class.java)
        )

        companion object {
            private val json by lazy {
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }
            }
        }
    }
}
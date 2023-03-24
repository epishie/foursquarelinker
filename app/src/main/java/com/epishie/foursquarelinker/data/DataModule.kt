package com.epishie.foursquarelinker.data

import android.content.Context
import com.epishie.foursquarelinker.data.foursquare.FourSquareApi
import com.epishie.foursquarelinker.data.foursquare.FourSquarePlaceDataSource
import com.epishie.foursquarelinker.domain.place.PlaceDataSource
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindFourSquareApi(impl: FourSquareApi.Impl): FourSquareApi

    @Binds
    fun bindPlaceDataSource(dataSource: FourSquarePlaceDataSource): PlaceDataSource

    companion object {

        @Provides
        fun provideLocationClient(@ApplicationContext context: Context) =
            LocationServices.getFusedLocationProviderClient(context)
    }
}
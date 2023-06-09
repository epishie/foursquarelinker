package com.epishie.foursquarelinker.domain.place

import android.location.Location
import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepository @Inject constructor(
    private val placeDataSource: PlaceDataSource,
) {
    fun searchPlace(keyword: String, location: Location): PagingSource<String, Place> {
        return PlacePagingSource { loadPageType ->
            when (loadPageType) {
                is LoadPageType.Initial -> placeDataSource.search(
                    keyword,
                    location,
                    loadPageType.size
                )
                is LoadPageType.Next -> placeDataSource.searchNext(loadPageType.key)
            }
        }
    }

    fun searchPlace(keyword: String, addressId: String): PagingSource<String, Place> {
        return PlacePagingSource { loadPageType ->
            when (loadPageType) {
                is LoadPageType.Initial -> placeDataSource.search(
                    keyword,
                    addressId,
                    loadPageType.size
                )
                is LoadPageType.Next -> placeDataSource.searchNext(loadPageType.key)
            }
        }
    }

    suspend fun autoCompleteAddress(query: String) = placeDataSource.autoCompleteAddress(query)

    private class PlacePagingSource(
        private val loadPage: suspend (LoadPageType) -> PlaceDataSource.SearchResponse,
    ) : PagingSource<String, Place>() {
        override fun getRefreshKey(state: PagingState<String, Place>): String? = null

        override suspend fun load(params: LoadParams<String>): LoadResult<String, Place> = try {
            val loadPageType = params.key?.let { LoadPageType.Next(it) }
                ?: LoadPageType.Initial(params.loadSize)
            val response = loadPage(loadPageType)
            LoadResult.Page(
                data = response.places,
                prevKey = null,
                nextKey = response.next
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private sealed interface LoadPageType {
        data class Initial(val size: Int) : LoadPageType
        data class Next(val key: String) : LoadPageType
    }
}
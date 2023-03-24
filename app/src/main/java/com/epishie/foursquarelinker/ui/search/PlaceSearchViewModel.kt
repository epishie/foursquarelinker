package com.epishie.foursquarelinker.ui.search

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.epishie.foursquarelinker.domain.location.LocationRepository
import com.epishie.foursquarelinker.domain.place.Place
import com.epishie.foursquarelinker.domain.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    val keyword = MutableStateFlow("")
    val address = MutableStateFlow<Address>(Address.Editing())
    val searchResults = MutableStateFlow<Flow<PagingData<Place>>?>(null)

    fun updateKeyword(keyword: String) {
        this.keyword.value = keyword
    }

    fun updateAddress(address: String) {
        this.address.value = Address.Editing(value = address)
    }

    fun requestCurrentLocation() {
        viewModelScope.launch {
            address.value = Address.CurrentLocation(locationRepository.getCurrentLocation())
        }
    }

    fun search() {
        searchResults.value = Pager(config = PagingConfig(initialLoadSize = 10, pageSize = 10)) {
            placeRepository.searchPlace(
                keyword.value,
                (address.value as Address.CurrentLocation).value
            )
        }
            .flow
            .cachedIn(viewModelScope)
    }
}
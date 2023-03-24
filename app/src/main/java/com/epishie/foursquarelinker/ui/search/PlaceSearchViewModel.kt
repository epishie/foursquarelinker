package com.epishie.foursquarelinker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.epishie.foursquarelinker.domain.location.LocationRepository
import com.epishie.foursquarelinker.domain.place.AutoCompleteAddress
import com.epishie.foursquarelinker.domain.place.Place
import com.epishie.foursquarelinker.domain.place.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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
    @OptIn(FlowPreview::class)
    val autoCompleteAddresses = address
        .debounce(1000L)
        .map { address ->
            val query = (address as? Address.Editing)?.value
            query.takeUnless { it.isNullOrEmpty() }?.let {
                placeRepository.autoCompleteAddress(it)
            } ?: emptyList()
        }
        .onEach {
            android.util.Log.d("PlaceSearchViewModel", "Result: $it")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun updateKeyword(keyword: String) {
        this.keyword.value = keyword
    }

    fun updateAddress(address: String) {
        this.address.value = Address.Editing(value = address)
    }

    fun selectAddress(autoCompleteAddress: AutoCompleteAddress) {
        this.address.value = Address.AutoCompleted(value = autoCompleteAddress)
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
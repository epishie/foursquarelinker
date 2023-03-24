package com.epishie.foursquarelinker.ui.search

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epishie.foursquarelinker.domain.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {
    val keyword = MutableStateFlow("")
    val address = MutableStateFlow<Address>(Address.Editing())

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
}
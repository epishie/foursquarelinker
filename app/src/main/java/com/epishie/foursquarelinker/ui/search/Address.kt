package com.epishie.foursquarelinker.ui.search

import android.location.Location
import com.epishie.foursquarelinker.domain.place.AutoCompleteAddress

sealed interface Address {
    data class CurrentLocation(val value: Location) : Address
    data class AutoCompleted(val value: AutoCompleteAddress): Address
    data class Editing(val value: String = "") : Address
}
package com.epishie.foursquarelinker.ui.search

import android.location.Location

sealed interface Address {
    data class CurrentLocation(val value: Location) : Address
    data class Editing(val value: String = "") : Address
}
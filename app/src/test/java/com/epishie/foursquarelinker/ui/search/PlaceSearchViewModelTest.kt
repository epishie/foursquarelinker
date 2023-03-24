package com.epishie.foursquarelinker.ui.search

import android.location.Location
import com.epishie.foursquarelinker.domain.location.LocationRepository
import com.epishie.foursquarelinker.domain.place.PlaceRepository
import com.epishie.foursquarelinker.ui.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceSearchViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var locationRepository: LocationRepository

    @MockK
    private lateinit var placeRepository: PlaceRepository

    private lateinit var viewModel: PlaceSearchViewModel

    @Before
    fun setUp() {
        viewModel = PlaceSearchViewModel(locationRepository, placeRepository)
    }

    @Test
    fun updateKeywordUpdatesStates() = runTest {
        // When
        viewModel.updateKeyword("test_keyword")

        // Then
        assertThat(viewModel.keyword.value).isEqualTo("test_keyword")
    }

    @Test
    fun updateAddressUpdatesStates() = runTest {
        // When
        viewModel.updateAddress("test address")

        // Then
        assertThat(viewModel.address.value).isEqualTo(Address.Editing("test address"))
    }

    @Test
    fun requestCurrentLocationUpdatesAddress() = runTest {
        // Given
        val location = mockk<Location>()
        coEvery { locationRepository.getCurrentLocation() } returns location

        // When
        viewModel.requestCurrentLocation()

        // Then
        assertThat(viewModel.address.value).isEqualTo(Address.CurrentLocation(value = location))
    }
}
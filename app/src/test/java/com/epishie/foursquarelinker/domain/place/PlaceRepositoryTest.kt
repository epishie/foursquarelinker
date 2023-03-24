package com.epishie.foursquarelinker.domain.place

import android.location.Location
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var dataSource: PlaceDataSource

    private lateinit var repository: PlaceRepository

    @Before
    fun setUp() {
        repository = PlaceRepository(dataSource)
    }

    @Test
    fun initialSearchPlaceReturnsPageOnSuccess() = runTest {
        // Given
        val location = mockk<Location> {
            every { latitude } returns 1.0
            every { longitude } returns 1.0
        }
        coEvery {
            dataSource.search(keyword = "test_keyword", location = location, count = 10)
        } returns PlaceDataSource.SearchResponse(
            places = listOf(Place("id1", "Venue 1", "Address 1")),
            next = "https://example.com/search/next/2"
        )

        // When
        val pagingSource = repository.searchPlace(keyword = "test_keyword", location = location)
        val page = pagingSource.load(Refresh(key = null, loadSize = 10, placeholdersEnabled = false))

        // Then
        assertThat(page)
            .isEqualTo(
                PagingSource.LoadResult.Page(
                    data = listOf(Place("id1", "Venue 1", "Address 1")),
                    prevKey = null,
                    nextKey = "https://example.com/search/next/2"
                )
            )
    }

    @Test
    fun initialSearchPlaceReturnsErrorPageOnError() = runTest {
        // Given
        val location = mockk<Location> {
            every { latitude } returns 1.0
            every { longitude } returns 1.0
        }
        coEvery {
            dataSource.search(keyword = "test_keyword", location = location, count = 10)
        } throws IOException()

        // When
        val pagingSource = repository.searchPlace(keyword = "test_keyword", location = location)
        val page = pagingSource.load(Refresh(key = null, loadSize = 10, placeholdersEnabled = false))

        // Then
        assertThat(page).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }

    @Test
    fun nextSearchPlaceReturnsPageOnSuccess() = runTest {
        // Given
        coEvery {
            dataSource.searchNext(next = "https://example.com/search/next/2")
        } returns PlaceDataSource.SearchResponse(
            places = listOf(Place("id1", "Venue 1", "Address 1")),
            next = "https://example.com/search/next/3"
        )

        // When
        val pagingSource = repository.searchPlace(keyword = "test_keyword", location = mockk())
        val page = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = "https://example.com/search/next/2",
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(page)
            .isEqualTo(
                PagingSource.LoadResult.Page(
                    data = listOf(Place("id1", "Venue 1", "Address 1")),
                    prevKey = null,
                    nextKey = "https://example.com/search/next/3"
                )
            )
    }

    @Test
    fun nextSearchPlaceReturnsErrorPageOnError() = runTest {
        // Given
        coEvery {
            dataSource.searchNext(next = "https://example.com/search/next/2")
        } throws IOException()

        // When
        val pagingSource = repository.searchPlace(keyword = "test_keyword", location = mockk())
        val page = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = "https://example.com/search/next/2",
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(page).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }
}
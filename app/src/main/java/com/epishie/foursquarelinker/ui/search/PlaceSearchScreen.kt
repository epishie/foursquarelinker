package com.epishie.foursquarelinker.ui.search

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PlaceSearchScreen() {
    val viewModel = viewModel<PlaceSearchViewModel>()
    val keyword by viewModel.keyword.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()

    PlaceSearchScreen(
        keyword = keyword,
        onKeywordChange = viewModel::updateKeyword,
        address = address,
        onAddressChange = viewModel::updateAddress,
        onLocationRequest = viewModel::requestCurrentLocation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceSearchScreen(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    address: Address,
    onAddressChange: (String) -> Unit,
    onLocationRequest: () -> Unit,
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    var showLocationSettingsDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as Activity
    var isLocationPermissionGranted by remember {
        mutableStateOf(activity.isLocationPermissionGranted())
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (activity.isLocationPermissionGranted()) {
            isLocationPermissionGranted = true
            onLocationRequest()
        } else {
            if (activity.shouldShowLocationPermissionRationale()) {
                showLocationDialog = true
            } else {
                showLocationSettingsDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            SearchBar(
                keyword = keyword,
                onKeywordChange = onKeywordChange,
                address = address,
                onAddressChange = onAddressChange,
                onCurrentLocationRequest = {
                    if (isLocationPermissionGranted) {
                        onLocationRequest()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    address: Address,
    onAddressChange: (String) -> Unit,
    onCurrentLocationRequest: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = keyword,
            onValueChange = onKeywordChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Business Type")
            }
        )
        AddressTextField(
            address = address,
            onAddressChange = onAddressChange,
            onCurrentLocationRequest = onCurrentLocationRequest,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun Activity.isLocationPermissionGranted() =
    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

private fun Activity.shouldShowLocationPermissionRationale() =
    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

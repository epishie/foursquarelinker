package com.epishie.foursquarelinker.ui.search

import android.graphics.Rect
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epishie.foursquarelinker.R
import com.epishie.foursquarelinker.domain.place.AutoCompleteAddress
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressTextField(
    address: Address,
    onAddressChange: (String) -> Unit,
    autoCompleteAddresses: List<AutoCompleteAddress>,
    onAddressSelect: (AutoCompleteAddress) -> Unit,
    onCurrentLocationRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isAddressFocused by remember { mutableStateOf(false) }
    val addressValue by remember(address, isAddressFocused) {
        derivedStateOf {
            when (address) {
                is Address.CurrentLocation -> if (isAddressFocused) {
                    ""
                } else {
                    "Current Location"
                }
                is Address.AutoCompleted -> address.value.text1
                is Address.Editing -> address.value
            }
        }
    }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(address) {
        snapshotFlow { address }
            .collect {
                if (it is Address.CurrentLocation) {
                    focusManager.clearFocus(true)
                }
            }
    }

    val view = LocalView.current
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current
    var textFieldHeight by remember { mutableStateOf(0.dp) }
    var menuHeight by remember { mutableStateOf(0) }
    TextField(
        value = addressValue,
        onValueChange = onAddressChange,
        modifier = modifier
            .onFocusChanged { focusState ->
                isAddressFocused = focusState.isFocused
            }
            .onGloballyPositioned {
                textFieldHeight = with(density) { it.size.height.toDp() }
                coordinates = it
                updateMenuHeight(view, coordinates) { height ->
                    menuHeight = height
                }
            },
        label = { Text(text = "Address") },
        readOnly = address is Address.CurrentLocation && !isAddressFocused,
        trailingIcon = {
            IconButton(
                onClick = { onCurrentLocationRequest() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_my_location_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
    var expanded by remember(autoCompleteAddresses) { mutableStateOf(autoCompleteAddresses.isNotEmpty()) }
    DropdownMenu(
        expanded = expanded && isAddressFocused,
        onDismissRequest = { expanded = false },
        modifier = modifier
            .heightIn(max = with(density) { menuHeight.toDp() }),
    ) {
        autoCompleteAddresses.forEach { autoCompleteAddress ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(text = autoCompleteAddress.text1, style = MaterialTheme.typography.bodyMedium)
                        Text(text = autoCompleteAddress.text2, style = MaterialTheme.typography.bodySmall)
                    }
                },
                onClick = {
                    expanded = false
                    onAddressSelect(autoCompleteAddress)
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
        }
    }
}

private fun updateMenuHeight(
    view: View,
    coordinates: LayoutCoordinates?,
    onUpdateMenuHeight: (Int) -> Unit,
) {
    coordinates ?: return
    val visibleWindowBounds = Rect().let {
        view.getWindowVisibleDisplayFrame(it)
        it
    }
    val heightAbove = coordinates.boundsInWindow().top - visibleWindowBounds.top
    val heightBelow =
        visibleWindowBounds.bottom - visibleWindowBounds.top - coordinates.boundsInWindow().bottom
    onUpdateMenuHeight(max(heightAbove, heightBelow).toInt())
}
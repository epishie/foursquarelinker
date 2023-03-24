package com.epishie.foursquarelinker.ui.search

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import com.epishie.foursquarelinker.R

@Composable
fun AddressTextField(
    address: Address,
    onAddressChange: (String) -> Unit,
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

    TextField(
        value = addressValue,
        onValueChange = onAddressChange,
        modifier = modifier.onFocusChanged { focusState ->
            isAddressFocused = focusState.isFocused
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
}
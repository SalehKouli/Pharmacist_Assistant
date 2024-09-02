// TopBarWithSearch.kt
package com.example.pharmacistassistant

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    isDropdownVisible: Boolean,
    searchResults: List<ProductData>,
    onDropdownItemSelected: (ProductData) -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name)) },
            navigationIcon = {
                IconButton(onClick = { /* Implement as needed */ }) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = stringResource(id = R.string.app_name))
                }
            }
        )
        Box {
            OutlinedTextField(
                value = query,
                onValueChange = { newQuery ->
                    Log.d("TopBarWithSearch", "Query changed to: $newQuery")
                    onQueryChange(newQuery)
                },
                label = { Text(stringResource(id = R.string.search)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            if (isDropdownVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQueryChange("") }
                        .padding(top = 60.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        items(searchResults) { result ->
                            ListItem(
                                headlineContent = { Text(result.tradeName) },
                                supportingContent = { Text(result.barcode) },
                                modifier = Modifier.clickable {
                                    Log.d("TopBarWithSearch", "Dropdown item clicked: ${result.tradeName}")
                                    onDropdownItemSelected(result)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
